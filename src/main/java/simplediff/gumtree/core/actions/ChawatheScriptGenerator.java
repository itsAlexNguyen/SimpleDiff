/*
 * This file is part of GumTree.
 *
 * GumTree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GumTree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GumTree.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2019 Jean-Rémy Falleri <jr.falleri@gmail.com>
 */

package simplediff.gumtree.core.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import simplediff.gumtree.core.actions.model.Action;
import simplediff.gumtree.core.actions.model.Change;
import simplediff.gumtree.core.actions.model.Delete;
import simplediff.gumtree.core.actions.model.ImportChange;
import simplediff.gumtree.core.actions.model.Insert;
import simplediff.gumtree.core.actions.model.MethodChange;
import simplediff.gumtree.core.actions.model.ModifierChange;
import simplediff.gumtree.core.actions.model.Move;
import simplediff.gumtree.core.actions.model.PackageChange;
import simplediff.gumtree.core.actions.model.Update;
import simplediff.gumtree.core.matchers.Mapping;
import simplediff.gumtree.core.matchers.MappingStore;
import simplediff.gumtree.core.tree.FakeTree;
import simplediff.gumtree.core.tree.ITree;
import simplediff.gumtree.core.tree.TreeUtils;

public class ChawatheScriptGenerator implements EditScriptGenerator {
  protected ITree origSrc;

  protected ITree cpySrc;

  protected ITree origDst;

  protected MappingStore origMappings;

  protected MappingStore cpyMappings;

  protected Set<ITree> dstInOrder;

  protected Set<ITree> srcInOrder;

  protected EditScript actions;

  protected Map<ITree, ITree> origToCopy;

  protected Map<ITree, ITree> copyToOrig;

  public EditScript computeActions(MappingStore ms) {
    initWith(ms);
    generate();
    return actions;
  }

  public void initWith(MappingStore ms) {
    this.origSrc = ms.src;
    this.cpySrc = this.origSrc.deepCopy();
    this.origDst = ms.dst;
    this.origMappings = ms;

    origToCopy = new HashMap<>();
    copyToOrig = new HashMap<>();
    Iterator<ITree> cpyTreeIterator = TreeUtils.preOrderIterator(cpySrc);
    for (ITree origTree : TreeUtils.preOrder(origSrc)) {
      ITree cpyTree = cpyTreeIterator.next();
      origToCopy.put(origTree, cpyTree);
      copyToOrig.put(cpyTree, origTree);
    }

    cpyMappings = new MappingStore(ms.src, ms.dst);
    for (Mapping m : origMappings) {
      cpyMappings.addMapping(origToCopy.get(m.first), m.second);
    }
  }

  public EditScript generate() {
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

    List<ITree> bfsDst = TreeUtils.preOrder(origDst);
    for (ITree x : bfsDst) {
      ITree w = null;
      ITree y = x.getParent();
      ITree z = cpyMappings.getSrcForDst(y);

      //      if (x.getType().name.equals("TypeDeclaration")
      //          && !x.getParent().getType().name.equals("CompilationUnit")) {
      //        System.out.println(x.getChildren());
      //      }

      /* Insertion of elements */
      if (!cpyMappings.isDstMapped(x)) {

        if (isPackageDeclaration(x)) {
          changeList.add(PackageChange.createInsertPackageChange(x.getChild(0).getLabel(), false));
        } else if (isImportDeclaration(x)) {
          changeList.add(ImportChange.createInsertImportChange(x.getChild(0).getLabel(), false));
        } else if (isSimpleName(x) && isMethodDeclaration(y)) {
          List<ITree> parent = getMethodParentBlock(y);
          changeList.add(
              MethodChange.createInsertMethodChange(
                  x.getLabel(),
                  parent.get(0).getLabel(),
                  parent.get(1).getLabel(),
                  -1,
                  -1,
                  x.getPos(),
                  x.getLength(), false));
        } else if (isModifier(x) && !cpyMappings.getSrcForDst(y).getType().name.equals("")) {
          if (!modifierList.containsKey(y)) {
            if (isFieldDeclaration(y)) {
              List<String> modifierNames = getModifierNames(getModifiers(y));
              changeList.add(
                  ModifierChange.createModifierChange(
                      modifierNames, y.getLabel(), "", -1, -1, y.getPos(), y.getLength(), false));
            } else {
              List<ITree> parent =
                  y.getChildren().stream()
                      .filter(p -> isSimpleName(p))
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
                      y.getLength(), false));
            }
            modifierList.put(y, cpyMappings.getSrcForDst(y));
          }
        }

        int k = findPos(x);
        // Insertion case : insert new node.
        w = new FakeTree();
        // In order to use the real nodes from the second tree, we
        // furnish x instead of w
        Action ins = new Insert(x, copyToOrig.get(z), k, x.getType());
        actions.add(ins);
        copyToOrig.put(w, x);
        cpyMappings.addMapping(w, x);
        z.insertChild(w, k);
      } else {
        /* Updating or moving of elements */
        w = cpyMappings.getSrcForDst(x);

        if (isPackageDeclaration(x)) {
          changeList.add(
              PackageChange.createUpdatePackageChange(
                  x.getChild(0).getLabel(), copyToOrig.get(w).getChild(0).getLabel(), false));
        }

        if (x.getType().name.equals("QualifiedName")
            && isImportDeclaration(y)
            && !cpyMappings.getSrcForDst(x).getLabel().equals(x.getLabel())) {
          changeList.add(ImportChange.createDeleteImportChange(w.getLabel(), false));
          changeList.add(ImportChange.createInsertImportChange(x.getLabel(), false));
        }

        if (isSimpleName(x) && isMethodDeclaration(y) && !w.getLabel().equals(x.getLabel())) {
          List<ITree> parent = getMethodParentBlock(y);
          changeList.add(
              MethodChange.createUpdateMethodChange(
                  copyToOrig.get(w).getLabel(),
                  x.getLabel(),
                  parent.get(0).getLabel(),
                  parent.get(1).getLabel(),
                  copyToOrig.get(w).getPos(),
                  copyToOrig.get(w).getLength(),
                  x.getPos(),
                  x.getLength(), false));
        }

        if (isModifier(x)) {
          final ITree v = w.getParent();
          if (!w.getLabel().equals(x.getLabel())) {

            if (!modifierList.containsKey(y)) {
              if (isFieldDeclaration(y)) {
                List<String> modifierNames = getModifierNames(getModifiers(y));
                changeList.add(
                    ModifierChange.createModifierChange(
                        modifierNames,
                        y.getType().name,
                        "",
                        y.getPos(),
                        y.getLength(),
                        w.getParent().getPos(),
                        w.getParent().getLength(), false));
              } else {
                List<ITree> parent =
                    v.getChildren().stream()
                        .filter(p -> isSimpleName(p))
                        .collect(Collectors.toList());
                List<String> modifierNames = getModifierNames(getModifiers(v));
                String name = getEnclosingType(v);
                changeList.add(
                    ModifierChange.createModifierChange(
                        modifierNames,
                        name,
                        parent.get(0).getType().name,
                        y.getPos(),
                        y.getLength(),
                        w.getParent().getPos(),
                        w.getParent().getLength(),false));
              }
            }
          }
        }

        if (!x.equals(origDst)) { // TODO => x != origDst // Case of the root
          final ITree v = w.getParent();
          if (!w.getLabel().equals(x.getLabel())) {
            actions.add(new Update(copyToOrig.get(w), x.getLabel(), x.getType()));
            w.setLabel(x.getLabel());
          }
          if (!z.equals(v)) {
            int k = findPos(x);
            Action mv = new Move(copyToOrig.get(w), copyToOrig.get(z), k, x.getType());
            actions.add(mv);
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
          changeList.add(PackageChange.createDeletePackageChange(w.getChild(0).getLabel(), false));
        } else if (isImportDeclaration(w)) {
          changeList.add(ImportChange.createDeleteImportChange(w.getChild(0).getLabel(), false));
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
                  -1, false));
        } else if (isModifier(w)) {
          if (!modifierList.containsKey(v) && cpyMappings.isSrcMapped(v)) {
            if (isFieldDeclaration(v)) {
              List<String> modifierNames = getModifierNames(getModifiers(v));
              changeList.add(
                  ModifierChange.createModifierChange(
                      modifierNames, v.getType().name, "", v.getPos(), v.getLength(), -1, -1, false));
            } else {
              List<String> modifierNames = getModifierNames(getModifiers(v));
              String name = getEnclosingType(v);
              changeList.add(
                  ModifierChange.createModifierChange(
                      modifierNames, name, getEnclosingName(v), v.getPos(), v.getLength(), -1, -1, false));
            }
            modifierList.put(w.getParent(), cpyMappings.getSrcForDst(w.getParent()));
          }
        }

        actions.add(new Delete(copyToOrig.get(w), w.getType()));
      }
    }
    return actions;
  }

  protected String getEnclosingName(ITree parent) {
    return parent.getChildren().stream()
        .filter(p -> isSimpleName(p))
        .collect(Collectors.toList())
        .get(0)
        .getLabel();
  }

  protected String getEnclosingType(ITree parent) {
    String name = parent.getType().name;
    for (ITree tree : parent.getChildren()) {
      if (tree.getType().name.equals("TYPE_DECLARATION_KIND")) {
        name = tree.getLabel();
        return name;
      } else if (isMethodDeclaration(tree)) {
        name = "method";
        return name;
      }
    }
    return name;
  }

  protected List<ITree> getMethodParentBlock(ITree parent) {
    return parent.getParent().getChildren().stream()
        .filter(p -> (p.getType().name.contains("DECLARATION_KIND") || isSimpleName(p)))
        .collect(Collectors.toList());
  }

  protected List<ITree> getModifiers(ITree parent) {
    return parent.getChildren().stream().filter(this::isModifier).collect(Collectors.toList());
  }

  protected List<String> getModifierNames(List<ITree> modifiers) {
    List<String> modifierNames = new LinkedList<>();
    for (ITree modifier : modifiers) {
      modifierNames.add(modifier.getLabel());
    }
    return modifierNames;
  }

  protected boolean isImportDeclaration(ITree tree) {
    return tree.getType().name.equals("ImportDeclaration");
  }

  protected boolean isFieldDeclaration(ITree tree) {
    return tree.getType().name.equals("FieldDeclaration");
  }

  protected boolean isMethodDeclaration(ITree tree) {
    return tree.getType().name.equals("MethodDeclaration");
  }

  protected boolean isModifier(ITree tree) {
    return tree.getType().name.equals("Modifier");
  }

  protected boolean isPackageDeclaration(ITree tree) {
    return tree.getType().name.equals("PackageDeclaration");
  }

  protected boolean isTypeDeclaration(ITree tree) {
    return tree.getType().name.equals("TypeDeclaration");
  }

  protected boolean isEnumDeclaration(ITree tree) {
    return tree.getType().name.equals("EnumDeclaration");
  }

  protected boolean isJavaDoc(ITree tree) {
    return tree.getType().name.equals("Javadoc");
  }

  protected boolean isSimpleType(ITree tree) {
    return tree.getType().name.equals("SimpleType");
  }

  protected boolean isSimpleName(ITree tree) {
    return tree.getType().name.equals("SimpleName");
  }

  protected void alignChildren(ITree w, ITree x) {
    srcInOrder.removeAll(w.getChildren());
    dstInOrder.removeAll(x.getChildren());

    List<ITree> s1 = new ArrayList<>();
    for (ITree c : w.getChildren()) {
      if (cpyMappings.isSrcMapped(c)) {
        if (x.getChildren().contains(cpyMappings.getDstForSrc(c))) {
          s1.add(c);
        }
      }
    }

    List<ITree> s2 = new ArrayList<>();
    for (ITree c : x.getChildren()) {
      if (cpyMappings.isDstMapped(c)) {
        if (w.getChildren().contains(cpyMappings.getSrcForDst(c))) {
          s2.add(c);
        }
      }
    }

    List<Mapping> lcs = lcs(s1, s2);

    for (Mapping m : lcs) {
      srcInOrder.add(m.first);
      dstInOrder.add(m.second);
    }

    for (ITree a : s1) {
      for (ITree b : s2) {
        if (origMappings.has(a, b)) {
          if (!lcs.contains(new Mapping(a, b))) {
            int k = findPos(b);
            Action mv =
                new Move(copyToOrig.get(a), copyToOrig.get(w), k, copyToOrig.get(a).getType());
            actions.add(mv);
            int oldk = a.positionInParent();
            w.getChildren().add(k, a);
            if (k < oldk) { // FIXME this is an ugly way to patch the index
              oldk++;
            }
            a.getParent().getChildren().remove(oldk);
            a.setParent(w);
            srcInOrder.add(a);
            dstInOrder.add(b);
          }
        }
      }
    }
  }

  protected int findPos(ITree x) {
    ITree y = x.getParent();
    List<ITree> siblings = y.getChildren();

    for (ITree c : siblings) {
      if (dstInOrder.contains(c)) {
        if (c.equals(x)) {
          return 0;
        } else {
          break;
        }
      }
    }

    int xpos = x.positionInParent();
    ITree v = null;
    for (int i = 0; i < xpos; i++) {
      ITree c = siblings.get(i);
      if (dstInOrder.contains(c)) {
        v = c;
      }
    }

    // if (v == null) throw new RuntimeException("No rightmost sibling in order");
    if (v == null) {
      return 0;
    }

    ITree u = cpyMappings.getSrcForDst(v);
    // siblings = u.getParent().getChildren();
    // int upos = siblings.indexOf(u);
    int upos = u.positionInParent();
    // int r = 0;
    // for (int i = 0; i <= upos; i++)
    // if (srcInOrder.contains(siblings.get(i))) r++;
    return upos + 1;
  }

  protected List<Mapping> lcs(List<ITree> x, List<ITree> y) {
    int m = x.size();
    int n = y.size();
    List<Mapping> lcs = new ArrayList<>();

    int[][] opt = new int[m + 1][n + 1];
    for (int i = m - 1; i >= 0; i--) {
      for (int j = n - 1; j >= 0; j--) {
        if (cpyMappings.getSrcForDst(y.get(j)).equals(x.get(i))) {
          opt[i][j] = opt[i + 1][j + 1] + 1;
        } else {
          opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
        }
      }
    }

    int i = 0;
    int j = 0;
    while (i < m && j < n) {
      if (cpyMappings.getSrcForDst(y.get(j)).equals(x.get(i))) {
        lcs.add(new Mapping(x.get(i), y.get(j)));
        i++;
        j++;
      } else if (opt[i + 1][j] >= opt[i][j + 1]) {
        i++;
      } else {
        j++;
      }
    }

    return lcs;
  }
}
