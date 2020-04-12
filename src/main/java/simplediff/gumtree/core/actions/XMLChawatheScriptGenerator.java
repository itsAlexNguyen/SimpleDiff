package simplediff.gumtree.core.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import simplediff.gumtree.core.actions.model.Action;
import simplediff.gumtree.core.actions.model.Change;
import simplediff.gumtree.core.actions.model.Delete;
import simplediff.gumtree.core.actions.model.FieldDeclarationChange;
import simplediff.gumtree.core.actions.model.ImportChange;
import simplediff.gumtree.core.actions.model.Insert;
import simplediff.gumtree.core.actions.model.JavadocChange;
import simplediff.gumtree.core.actions.model.MethodChange;
import simplediff.gumtree.core.actions.model.MethodReorderChange;
import simplediff.gumtree.core.actions.model.ModifierChange;
import simplediff.gumtree.core.actions.model.Move;
import simplediff.gumtree.core.actions.model.PackageChange;
import simplediff.gumtree.core.actions.model.TypeDeclarationChange;
import simplediff.gumtree.core.actions.model.Update;
import simplediff.gumtree.core.matchers.MappingStore;
import simplediff.gumtree.core.tree.FakeTree;
import simplediff.gumtree.core.tree.ITree;
import simplediff.gumtree.core.tree.TreeUtils;

public class XMLChawatheScriptGenerator extends ChawatheScriptGenerator {

  public List<Change> generateChanges(MappingStore ms) {
    initWith(ms);
    ITree srcFakeRoot = new FakeTree(cpySrc);
    ITree dstFakeRoot = new FakeTree(origDst);
    cpySrc.setParent(srcFakeRoot);
    origDst.setParent(dstFakeRoot);

    actions = new EditScript();
    dstInOrder = new HashSet<>();
    srcInOrder = new HashSet<>();
    final List<Change> changeList = new LinkedList<Change>();

    cpyMappings.addMapping(srcFakeRoot, dstFakeRoot);

    HashMap<ITree, ITree> modifierList = new HashMap<ITree, ITree>();

    List<ITree> bfsSrc = TreeUtils.preOrder(origSrc);
    List<ITree> bfsDst = TreeUtils.preOrder(origDst);

    List<String> srcMethods = new LinkedList<String>();
    List<String> dstMethods = new LinkedList<String>();

    for (ITree x : bfsSrc){
      if (isMethodDeclaration(x))
      srcMethods.add(x.getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel());
    }

    for (ITree x : bfsDst){
      if (isMethodDeclaration(x)){
        String label = x.getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel();
        dstMethods.add(label);
      }
    }
    //what is this filtering
    //srcMethods = srcMethods.stream().filter(dstMethods::contains).collect(Collectors.toList());
    if (!srcMethods.equals(dstMethods)){
      changeList.add(MethodReorderChange.createMethodReorderChange(srcMethods, dstMethods, true));
    }

    for (ITree x : bfsDst) {
      ITree w = null;
      ITree y = x.getParent();
      ITree z = cpyMappings.getSrcForDst(y);

      /* Insertion of elements */
      if (!cpyMappings.isDstMapped(x)) {

        if (isPackageDeclaration(x)) {
          changeList.add(PackageChange.createInsertPackageChange(x.getChild(0).getLabel(), true));
        } else if (isSimpleName(x) && (isTypeDeclaration(y) || isEnumDeclaration(y))) {
          if (isTypeDeclaration(y)){
            changeList.add(TypeDeclarationChange.createInsertTypeDeclarationChange(
                y.getChildren().stream().filter(p -> p.getType().name.equals("TYPE_DECLARATION_KIND")).collect(Collectors.toList()).get(0).getLabel() + " " +
                    x.getLabel(),
                y.getParent().getLabel().equals("") ? y.getParent().getType().name : y.getParent().getLabel() , true));
          } else {
            changeList.add(TypeDeclarationChange.createInsertTypeDeclarationChange(
                y.getType().name + " " + x.getLabel(),
                y.getParent().getLabel().equals("") ? y.getParent().getType().name : y.getParent().getLabel(), true ));
          }
        } else if (isImportDeclaration(x)) {
          changeList.add(ImportChange.createInsertImportChange(x.getChild(0).getLabel(), true));
        } else if (isSimpleName(x) && isMethodDeclaration(y)) {
          List<ITree> parent = getMethodParentBlock(y);
          changeList.add(
              MethodChange.createInsertMethodChange(
                  x.getLabel(),
                  parent.get(0).getLabel(),
                  parent.get(1).getLabel(),
                  -1,
                  -1,
                  y.getPos(),
                  y.getLength(), true));
        } else if (isJavaDoc(x)) {
          changeList.add(JavadocChange.createInsertJavadocChange(y.getType().name + " " +
              y.getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel(),
              -1, -1, x.getPos(), x.getLength() + y.getLength(), true));
        } else if (isFieldDeclaration(x)) {
          changeList.add(
              FieldDeclarationChange.createInsertFieldChange(
                  x.getChildren().stream().filter(p -> p.getType().name.equals("VariableDeclarationFragment"))
                  .collect(Collectors.toList()).get(0).getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel(),
                  y.getType().name,
                  y.getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel(),
                  -1,
                  -1,
                  y.getPos(),
                  y.getLength(), true));
        } else if (isModifier(x) && !cpyMappings.getSrcForDst(y).getType().name.equals("")) {
          if (!modifierList.containsKey(y)) {
            if (isFieldDeclaration(y)) {
              List<String> modifierNames = getModifierNames(getModifiers(y));
              changeList.add(
                  ModifierChange.createModifierChange(
                      modifierNames, y.getLabel(), "", -1, -1, y.getPos(), y.getLength(), true));
            } else {
              List<ITree> parent =
                  y.getChildren().stream()
                      .filter(this::isSimpleName)
                      .collect(Collectors.toList());

              List<String> modifierNames = getModifierNames(getModifiers(y));
              String name = getEnclosingType(y);

              changeList.add(
                  ModifierChange.createModifierChange(
                      modifierNames,
                      name,
                      parent.get(0).getLabel(),
                      -1,
                      -1,
                      y.getPos(),
                      y.getLength(), true));
            }
            modifierList.put(y, cpyMappings.getSrcForDst(y));
          }
        }

        int k = findPos(x);
        // Insertion case : insert new node.
        w = new FakeTree();
        copyToOrig.put(w, x);
        cpyMappings.addMapping(w, x);
        z.insertChild(w, k);
      } else {
        /* Updating or moving of elements */
        w = cpyMappings.getSrcForDst(x);

        if (isPackageDeclaration(x) && !x.getLabel().equals(w.getLabel())) {
          changeList.add(
              PackageChange.createUpdatePackageChange(
                  x.getChild(0).getLabel(), copyToOrig.get(w).getChild(0).getLabel(), true));
        }

        if (isSimpleName(x) && ((isTypeDeclaration(y) || isEnumDeclaration(y)))) {
          String srcType = "";
          if (copyToOrig.get(w).getParent().getType().name.equals("TypeDeclaration")){
            srcType = copyToOrig.get(w).getParent().getChildren().stream().filter(p -> p.getType().name.equals("TYPE_DECLARATION_KIND")).collect(Collectors.toList()).get(0).getLabel();
          } else {
            srcType = copyToOrig.get(w).getType().name;
          }


          if (isTypeDeclaration(y)){
            if (srcType.equals(y.getChildren().stream().filter(p -> p.getType().name.equals("TYPE_DECLARATION_KIND")).collect(Collectors.toList()).get(0).getLabel()) && copyToOrig.get(w).getLabel().equals(x.getLabel())){
              continue;
            }
            changeList.add(TypeDeclarationChange.createUpdateTypeDeclarationChange(srcType + " " + copyToOrig.get(w).getLabel(),
                y.getChildren().stream().filter(p -> p.getType().name.equals("TYPE_DECLARATION_KIND")).collect(Collectors.toList()).get(0).getLabel() + " " +
                    x.getLabel(),
                y.getParent().getLabel().equals("") ? y.getParent().getType().name : y.getParent().getLabel(), true ));
          } else {
            if (srcType.equals(y.getType().name) && copyToOrig.get(w).getLabel().equals(x.getLabel())){
              continue;
            }
            changeList.add(TypeDeclarationChange.createUpdateTypeDeclarationChange(srcType + " " + copyToOrig.get(w).getLabel(),
                y.getType().name + " " + x.getLabel(),
                y.getParent().getLabel().equals("") ? y.getParent().getType().name : y.getParent().getLabel(), true ));
          }
        }

        if (isJavaDoc(x) && !w.getChildren().equals(x.getChildren())) { changeList.add(JavadocChange.createUpdateJavadocChange(y.getType().name + " " +
              y.getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel(),
              y.getParent().getPos(), y.getParent().getLength(), copyToOrig.get(w.getParent().getParent()).getPos(), copyToOrig.get(w.getParent().getParent()).getLength(), true));
          }

        if (isFieldDeclaration(x)) {
          if (copyToOrig.get(w).getChildren().stream().filter(p -> p.getType().name.equals("VariableDeclarationFragment"))
              .collect(Collectors.toList()).get(0).getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel().equals(
                  x.getChildren().stream().filter(p -> p.getType().name.equals("VariableDeclarationFragment"))
                      .collect(Collectors.toList()).get(0).getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel()
              )){
            continue;
          }
          changeList.add(
              FieldDeclarationChange.createUpdateFieldChange(
                  copyToOrig.get(w).getChildren().stream().filter(p -> p.getType().name.equals("VariableDeclarationFragment"))
                  .collect(Collectors.toList()).get(0).getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel(),
                  x.getChildren().stream().filter(p -> p.getType().name.equals("VariableDeclarationFragment"))
                      .collect(Collectors.toList()).get(0).getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel(),
                  y.getType().name,
                  y.getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel(),
                  copyToOrig.get(w).getPos(),
                  copyToOrig.get(w).getLength(),
                  x.getPos(),
                  x.getLength(), true));
        }

        if (x.getType().name.equals("QualifiedName")
            && isImportDeclaration(y)
            && !cpyMappings.getSrcForDst(x).getLabel().equals(x.getLabel())) {
          changeList.add(ImportChange.createDeleteImportChange(w.getLabel(), true));
          changeList.add(ImportChange.createInsertImportChange(x.getLabel(), true));
        }

        if (isSimpleName(x) && isMethodDeclaration(y) && !w.getLabel().equals(x.getLabel())) {
          List<ITree> parent = getMethodParentBlock(y);
          changeList.add(
              MethodChange.createUpdateMethodChange(
                  copyToOrig.get(w).getLabel(),
                  x.getLabel(),
                  parent.get(0).getLabel(),
                  parent.get(1).getLabel(),
                  copyToOrig.get(w).getParent().getPos(),
                  copyToOrig.get(w).getParent().getLength(),
                  y.getPos(),
                  y.getLength(), true));
        }

        if (isModifier(x)) {
          final ITree v = w.getParent();
          if (!w.getLabel().equals(x.getLabel())) {

            if (!modifierList.containsKey(y)) {
              if (isFieldDeclaration(y)) {
                List<String> modifierNames = getModifierNames(getModifiers(v));
                changeList.add(
                    ModifierChange.createModifierChange(
                        modifierNames,
                        y.getLabel(),
                        "",
                        x.getPos(),
                        x.getLength(),
                        v.getParent().getPos(),
                        v.getParent().getLength(), true));
              } else {
                List<ITree> parent =
                    v.getChildren().stream()
                        .filter(this::isSimpleName)
                        .collect(Collectors.toList());
                List<String> modifierNames = getModifierNames(getModifiers(y));
                String name = getEnclosingType(v);
                changeList.add(
                    ModifierChange.createModifierChange(
                        modifierNames,
                        name,
                        parent.get(0).getLabel(),
                        v.getPos(),
                        v.getLength(),
                        x.getParent().getPos(),
                        x.getParent().getLength(), true));
              }
            }
          }
        }

        if (!x.equals(origDst)) { // TODO => x != origDst // Case of the root
          final ITree v = w.getParent();
          if (!w.getLabel().equals(x.getLabel())) {
            w.setLabel(x.getLabel());
          }
          if (!z.equals(v)) {
            int k = findPos(x);
            int oldk = w.positionInParent();
            w.getParent().getChildren().remove(oldk);
            z.insertChild(w, k);
          }
        }
      }

      srcInOrder.add(w);
      dstInOrder.add(x);
      alignChildren(w, x);
    }

    /* Deletion actions */
    for (ITree w : cpySrc.postOrder()) {
      if (!cpyMappings.isSrcMapped(w)) {
        final ITree v = w.getParent();

        if (isPackageDeclaration(w)) {
          changeList.add(PackageChange.createDeletePackageChange(w.getChild(0).getLabel(), true));
        } else if (isSimpleName(w) && (isTypeDeclaration(v) || isEnumDeclaration(v))) {
          if (isTypeDeclaration(v)){
            changeList.add(TypeDeclarationChange.createDeleteTypeDeclarationChange(
                v.getChildren().stream().filter(p -> p.getType().name.equals("TYPE_DECLARATION_KIND")).collect(Collectors.toList()).get(0).getLabel() + " " +
                    w.getLabel(),
                v.getParent().getLabel().equals("") ? v.getParent().getType().name : v.getParent().getLabel(), true ));
          } else {
            changeList.add(TypeDeclarationChange.createDeleteTypeDeclarationChange(
                v.getType().name + " " + w.getLabel(),
                v.getParent().getLabel().equals("") ? v.getParent().getType().name : v.getParent().getLabel(), true ));
          }
        } else if (isImportDeclaration(w)) {
          changeList.add(ImportChange.createDeleteImportChange(w.getChild(0).getLabel(), true));
        } else if (isSimpleName(w) && isMethodDeclaration(v)) {
          List<ITree> parent = getMethodParentBlock(v);
          changeList.add(
              MethodChange.createDeleteMethodChange(
                  w.getLabel(),
                  parent.get(0).getLabel(),
                  parent.get(1).getLabel(),
                  v.getPos(),
                  v.getLength(),
                  -1,
                  -1, true));
        } else if (isJavaDoc(w)) {
          changeList.add(JavadocChange.createDeleteJavadocChange(v.getType().name + " " +
              w.getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel(),
              w.getPos(), w.getPos() + w.getLength(), -1, -1, true));
        } else if (isFieldDeclaration(w)) {
          //TODO change this to global field declaration
          changeList.add(
              FieldDeclarationChange.createDeleteFieldChange(
                  w.getLabel(),
                  v.getType().name,
                  v.getChildren().stream().filter(p -> p.getType().name.equals("SimpleName")).collect(Collectors.toList()).get(0).getLabel(),
                  v.getPos(),
                  v.getLength(),
                  -1,
                  -1, true));
        } else if (isModifier(w)) {
          if (!modifierList.containsKey(v) && cpyMappings.isSrcMapped(v)) {
            if (isFieldDeclaration(v)) {
              List<String> modifierNames = getModifierNames(getModifiers(v));
              changeList.add(
                  ModifierChange.createModifierChange(
                      modifierNames, v.getType().name, "", v.getPos(), v.getLength(), -1, -1, true));
            } else {
              List<String> modifierNames = getModifierNames(getModifiers(v));
              String name = getEnclosingType(v);
              changeList.add(
                  ModifierChange.createModifierChange(
                      modifierNames, name, getEnclosingName(v), v.getPos(), v.getLength(), -1, -1, true));
            }
            modifierList.put(w.getParent(), cpyMappings.getSrcForDst(w.getParent()));
          }
        }
      }
    }
    return changeList;
  }
}
