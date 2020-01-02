package simplediff.gumtree.diff.web;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import simplediff.gumtree.core.actions.Diff;
import simplediff.gumtree.core.actions.EditScript;
import simplediff.gumtree.core.actions.EditScriptGenerator;
import simplediff.gumtree.core.actions.XMLChawatheScriptGenerator;
import simplediff.gumtree.core.actions.model.Change;
import simplediff.gumtree.core.actions.model.ImportChange;
import simplediff.gumtree.core.actions.model.MethodChange;
import simplediff.gumtree.core.actions.model.PackageChange;
import simplediff.gumtree.core.actions.model.SourceChange;
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
      final Matcher matcher,
      final EditScriptGenerator editScriptGenerator,
      final XMLChawatheScriptGenerator xmlScriptGenerator)
      throws IOException {

    StringBuilder output = new StringBuilder();
    output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n");
    output.append("<?xml-stylesheet type=\"text/xsl\" href=\"dist\\diff.xsl\" ?>\n");
    output.append("<document>\n");

    output.append("\t<title>\n");
    output.append("\t\t<name>").append(title).append("</name>\n");
    output.append("\t</title>\n");

    for (int i = 0; i < numFiles; i++) {
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
      output.append(publishFile(srcFile, dstFile, changeList, rawHtmlDiff));
      output.append("\t</file>\n");
    }
    output.append("</document>\n");

    return output.toString();
    }

  private static String publishFile(final File source, final File modified, final List<Change> changeList, final String rawHtmlDiff) throws IOException {
    final RandomAccessFile srcFile = new RandomAccessFile(source, "r");
    final RandomAccessFile dstFile = new RandomAccessFile(modified, "r");

    final StringBuilder packages = new StringBuilder();
    final List<String> importChanges = new LinkedList<String>();
    final List<String> methodChanges = new LinkedList<String>();
    final List<String> modifierChanges = new LinkedList<String>();

    final String openingChangeTag = "\t\t\t<change>\n";
    final String closingChangeTag = "\t\t\t</change>\n";
    final String openingTextTag = "\t\t\t\t<change-text>";
    final String closingTextTag = "\n\t\t\t\t</change-text>\n";
    final String srcOpeningSourceTag = "<change-src>\n";
    final String srcClosingSourceTag = "\n</change-src>\n";
    final String dstOpeningSourceTag = "<change-dst>\n";
    final String dstClosingSourceTag = "\n</change-dst>\n";

    for (final Change current : changeList) {
      if (current instanceof PackageChange) {
        packages.append(openingChangeTag).append(openingTextTag);
        packages.append(current.getChangeText());
        packages.append(closingTextTag).append(closingChangeTag);
      } else if (current instanceof ImportChange) {
        final String imports =
            openingChangeTag
                + openingTextTag
                + current.getChangeText()
                + closingTextTag
                + closingChangeTag;
        importChanges.add(imports);
      } else if (current instanceof SourceChange) {
        final SourceChange sourceChange = ((SourceChange) current);

        final StringBuilder text = new StringBuilder();
        text.append(openingChangeTag).append(openingTextTag).append(sourceChange.getChangeText());
        text.append(closingTextTag);

        final int srcPos = sourceChange.getSrcStart();
        final int srcLength = sourceChange.getSrcLength();
        if (srcPos != -1) {
          String sourceCode = read(srcFile, srcPos, srcLength);
          text.append(srcOpeningSourceTag);
          text.append(sourceCode);
          text.append(srcClosingSourceTag);
        }

        final int dstPos = sourceChange.getDstStart();
        final int dstLength = sourceChange.getDstLength();
        if (dstPos != -1) {
          String sourceCode = read(dstFile, dstPos, dstLength);
          text.append(dstOpeningSourceTag);
          text.append(sourceCode);
          text.append(dstClosingSourceTag);
        }
        text.append(closingChangeTag);

        if (current instanceof MethodChange) {
          methodChanges.add(text.toString());
        } else {
          modifierChanges.add(text.toString());
        }
      }
    }

    final StringBuilder output = new StringBuilder();

    /* Add package changes */
    output.append("\t\t<change-pkg>\n");
    output.append(packages.toString());
    output.append("\t\t</change-pkg>\n");

    /* Add import changes */
    output.append("\t\t<change-import\n>");
    for (String importChange : importChanges) {
      output.append(importChange);
    }
    output.append("\t\t</change-import>\n");

    /* Add modifier changes */
    output.append("\t\t<change-modifier>");
    for (String modifierChange : modifierChanges) {
      output.append(modifierChange);
    }
    output.append("\t\t</change-modifier>\n");

    /* Add method changes */
    output.append("\t\t<change-method>\n");
    for (String methodChange : methodChanges) {
      output.append(methodChange);
    }
    output.append("\t\t</change-method>\n");

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

  private static String read(final RandomAccessFile file, final int pos, final int length) throws IOException {
    int count = 0;
    file.seek(pos);
    final StringBuilder input = new StringBuilder();
    try {
      while (count < length && pos + count < file.length()) {
        input.append((char) file.read());
        count++;
      }
    } catch (EOFException e) {
      System.out.println(e.getMessage());
    }
    return input.toString();
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
