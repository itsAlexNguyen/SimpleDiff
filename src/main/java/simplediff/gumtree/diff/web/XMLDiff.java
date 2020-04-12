package simplediff.gumtree.diff.web;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.List;

import simplediff.gumtree.core.actions.Diff;
import simplediff.gumtree.core.actions.EditScript;
import simplediff.gumtree.core.actions.EditScriptGenerator;
import simplediff.gumtree.core.actions.XMLChawatheScriptGenerator;
import simplediff.gumtree.core.actions.model.Change;
import simplediff.gumtree.core.actions.model.FieldDeclarationChange;
import simplediff.gumtree.core.actions.model.ImportChange;
import simplediff.gumtree.core.actions.model.JavadocChange;
import simplediff.gumtree.core.actions.model.MethodChange;
import simplediff.gumtree.core.actions.model.MethodReorderChange;
import simplediff.gumtree.core.actions.model.ModifierChange;
import simplediff.gumtree.core.actions.model.PackageChange;
import simplediff.gumtree.core.actions.model.SourceChange;
import simplediff.gumtree.core.actions.model.TypeDeclarationChange;
import simplediff.gumtree.core.matchers.MappingStore;
import simplediff.gumtree.core.matchers.Matcher;
import simplediff.gumtree.core.tree.TreeContext;
import simplediff.gumtree.core.utils.Pair;

public class XMLDiff {

  public static String publish(
      final List<Pair<File,File>> modifiedFiles,
      final List<Pair<TreeContext, TreeContext>> contexts,
      final int numFiles,
      final String title,
      final String targetBranch,
      final Matcher matcher,
      final EditScriptGenerator editScriptGenerator,
      final XMLChawatheScriptGenerator xmlScriptGenerator)
      throws IOException {

    StringBuilder output = new StringBuilder();
    output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n");
    output.append("<?xml-stylesheet type=\"text/xsl\" href=\"dist\\diff.xsl\" ?>\n");
    output.append("<data>\n");

    output.append("\t<title>\n");
    output.append("\t\t<name>").append(title).append("</name>\n");
    output.append("\t</title>\n");

    output.append("\t<targetBranch>\n");
    output.append("\t\t<name>").append(targetBranch).append("</name>\n");
    output.append("\t</targetBranch>\n");

    for (int i = 0; i < numFiles; i++) {

      FieldDeclarationChange.reset();
      ImportChange.reset();
      JavadocChange.reset();
      MethodChange.reset();
      MethodReorderChange.reset();
      ModifierChange.reset();
      PackageChange.reset();
      TypeDeclarationChange.reset();

      final Pair<File, File> pair = modifiedFiles.get(i);
      final TreeContext src = contexts.get(i).first;
      final TreeContext dst = contexts.get(i).second;

      File srcFile = pair.first;
      File dstFile = pair.second;

      final MappingStore mappings = matcher.match(src.getRoot(), dst.getRoot());
      final EditScript editScript = editScriptGenerator.computeActions(mappings);
      final Diff diff = new Diff(src, dst, mappings, editScript);
      final HtmlDiffs htmlDiff = new HtmlDiffs(srcFile, dstFile, diff);
      htmlDiff.produce();

      final String rawHtmlDiff = getRawHTMLDiff(htmlDiff);
      final List<Change> changeList = xmlScriptGenerator.generateChanges(mappings);
      Collections.sort(changeList);

      output.append("\t<file>\n");
      output.append("\t\t<name>").append(srcFile).append("</name>\n");
      
      
      output.append("\t\t<change-pkg-additions>").append(PackageChange.changeCounter.get("Additions")).append("</change-pkg-additions>\n");
      output.append("\t\t<change-pkg-updates>").append(PackageChange.changeCounter.get("Updates")).append("</change-pkg-updates>\n");
      output.append("\t\t<change-pkg-removals>").append(PackageChange.changeCounter.get("Removals")).append("</change-pkg-removals>\n");

      output.append("\t\t<change-import-additions>").append(ImportChange.changeCounter.get("Additions")).append("</change-import-additions>\n");
      output.append("\t\t<change-import-updates>").append(ImportChange.changeCounter.get("Updates")).append("</change-import-updates>\n");
      output.append("\t\t<change-import-removals>").append(ImportChange.changeCounter.get("Removals")).append("</change-import-removals>\n");

      output.append("\t\t<change-method-additions>").append(MethodChange.changeCounter.get("Additions")).append("</change-method-additions>\n");
      output.append("\t\t<change-method-updates>").append(MethodChange.changeCounter.get("Updates")).append("</change-method-updates>\n");
      output.append("\t\t<change-method-removals>").append(MethodChange.changeCounter.get("Removals")).append("</change-method-removals>\n");

      output.append("\t\t<change-field-declaration-additions>").append(FieldDeclarationChange.changeCounter.get("Additions")).append("</change-field-declaration-additions>\n");
      output.append("\t\t<change-field-declaration-updates>").append(FieldDeclarationChange.changeCounter.get("Updates")).append("</change-field-declaration-updates>\n");
      output.append("\t\t<change-field-declaration-removals>").append(FieldDeclarationChange.changeCounter.get("Removals")).append("</change-field-declaration-removals>\n");

      output.append("\t\t<change-method-reorder-additions>").append(MethodReorderChange.changeCounter.get("Additions")).append("</change-method-reorder-additions>\n");
      output.append("\t\t<change-method-reorder-updates>").append(MethodReorderChange.changeCounter.get("Updates")).append("</change-method-reorder-updates>\n");
      output.append("\t\t<change-method-reorder-removals>").append(MethodReorderChange.changeCounter.get("Removals")).append("</change-method-reorder-removals>\n");

      output.append("\t\t<change-type-declaration-additions>").append(TypeDeclarationChange.changeCounter.get("Additions")).append("</change-type-declaration-additions>\n");
      output.append("\t\t<change-type-declaration-updates>").append(TypeDeclarationChange.changeCounter.get("Updates")).append("</change-type-declaration-updates>\n");
      output.append("\t\t<change-type-declaration-removals>").append(TypeDeclarationChange.changeCounter.get("Removals")).append("</change-type-declaration-removals>\n");

      output.append("\t\t<change-modifier-additions>").append(ModifierChange.changeCounter.get("Additions")).append("</change-modifier-additions>\n");
      output.append("\t\t<change-modifier-updates>").append(ModifierChange.changeCounter.get("Updates")).append("</change-modifier-updates>\n");
      output.append("\t\t<change-modifier-removals>").append(ModifierChange.changeCounter.get("Removals")).append("</change-modifier-removals>\n");

      output.append("\t\t<change-javadoc-additions>").append(JavadocChange.changeCounter.get("Additions")).append("</change-javadoc-additions>\n");
      output.append("\t\t<change-javadoc-updates>").append(JavadocChange.changeCounter.get("Updates")).append("</change-javadoc-updates>\n");
      output.append("\t\t<change-javadoc-removals>").append(JavadocChange.changeCounter.get("Removals")).append("</change-javadoc-removals>\n");
      
      
      output.append(publishFile(srcFile, dstFile, changeList, rawHtmlDiff));
      output.append("\t</file>\n");
    }
    output.append("</data>\n");

    return output.toString();
    }

  private static String publishFile(final File source, final File modified, final List<Change> changeList, final String rawHtmlDiff) throws IOException {
    final RandomAccessFile srcFile = new RandomAccessFile(source, "r");
    final RandomAccessFile dstFile = new RandomAccessFile(modified, "r");

    StringBuilder output = new StringBuilder();
    Collections.sort(changeList);
    for (final Change current: changeList) {
      if (current instanceof SourceChange) {
        output.append(((SourceChange) current).getXMLString(srcFile, dstFile));
      } else {
        output.append(current.getXMLString());
      }
    }

    /* Add raw changes */
    output.append("\t\t<change-raw>\n");
    output.append("\t\t\t<change>\n").append("\t\t\t\t<change-text>");
    output.append(rawHtmlDiff);
    output.append("\n\t\t\t\t</change-text>\n").append("\t\t\t</change>\n");
    output.append("\t\t</change-raw>\n");

    srcFile.close();
    dstFile.close();
    return output.toString();
  }

  private static String getRawHTMLDiff(final HtmlDiffs htmlDiff) {
    final String srcDiff = htmlDiff.getSrcDiff();
    final String dstDiff = htmlDiff.getDstDiff();

    return "<row-node>"
        + "<half-col>"
        + srcDiff.replace("span", "span-node")
        + "</half-col>"
        + "<half-col>"
        + dstDiff.replace("span", "span-node")
        + "</half-col>"
        + "</row-node>";
  }

}
