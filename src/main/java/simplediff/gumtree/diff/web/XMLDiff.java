package simplediff.gumtree.diff.web;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import simplediff.gumtree.core.actions.XMLChawatheScriptGenerator;
import simplediff.gumtree.core.actions.model.Change;
import simplediff.gumtree.core.actions.model.ImportChange;
import simplediff.gumtree.core.actions.model.MethodChange;
import simplediff.gumtree.core.actions.model.PackageChange;
import simplediff.gumtree.core.actions.model.SourceChange;
import simplediff.gumtree.core.matchers.MappingStore;
import simplediff.gumtree.core.matchers.Matcher;
import simplediff.gumtree.core.tree.TreeContext;

public class XMLDiff {
  private final List<Change> changeList;
  private final File srcFile;
  private final File dstFile;

  public XMLDiff(
      File srcFile,
      File dstFile,
      TreeContext src,
      TreeContext dst,
      Matcher matcher,
      XMLChawatheScriptGenerator scriptGenerator)
      throws IOException {

    this.srcFile = srcFile;
    this.dstFile = dstFile;

    MappingStore mappings = matcher.match(src.getRoot(), dst.getRoot());

    List<Change> changeList = scriptGenerator.generateChanges(mappings);
    Collections.sort(changeList);
    this.changeList = changeList;
  }

  public String publish() throws IOException {
    RandomAccessFile srcFile = new RandomAccessFile(this.srcFile, "r");
    RandomAccessFile dstFile = new RandomAccessFile(this.dstFile, "r");

    StringBuilder packages = new StringBuilder();
    List<String> importChanges = new LinkedList<String>();
    List<String> methodChanges = new LinkedList<String>();
    List<String> modifierChanges = new LinkedList<String>();

    final String openingChangeTag = "\t\t\t<change>\n";
    final String closingChangeTag = "\t\t\t</change>\n";
    final String openingTextTag = "\t\t\t\t<change-text>";
    final String closingTextTag = "\t\t\t\t</change-text>\n";
    final String srcOpeningSourceTag = "<change-src>\n";
    final String srcClosingSourceTag = "\n</change-src>\n";
    final String dstOpeningSourceTag = "<change-dst>\n";
    final String dstClosingSourceTag = "\n</change-dst>\n";

    for (int i = 0; i < changeList.size(); i++) {
      Change current = changeList.get(i);
      if (current instanceof PackageChange) {
        packages.append(openingChangeTag).append(openingTextTag);
        packages.append(current.getChangeText());
        packages.append(closingTextTag).append(closingChangeTag);
      } else if (current instanceof ImportChange) {
        String imports =
            openingChangeTag
                + openingTextTag
                + current.getChangeText()
                + closingTextTag
                + closingChangeTag;
        importChanges.add(imports);
      } else if (current instanceof SourceChange) {
        SourceChange sourceChange = ((SourceChange) current);

        StringBuilder text = new StringBuilder();
        text.append(openingChangeTag).append(openingTextTag).append(sourceChange.getChangeText());
        text.append(closingTextTag);

        int srcPos = sourceChange.getSrcStart();
        int srcLength = sourceChange.getSrcLength();
        if (srcPos != -1) {
          String sourceCode = read(srcFile, srcPos, srcLength);
          text.append(srcOpeningSourceTag);
          text.append(sourceCode);
          text.append(srcClosingSourceTag);
        }

        int dstPos = sourceChange.getDstStart();
        int dstLength = sourceChange.getDstLength();
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

    StringBuilder output = new StringBuilder();
    output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n");
    output.append("<?xml-stylesheet type=\"text/xsl\" href=\"\\resources\\diff.xsl\" ?>\n");
    output.append("<document>\n");
    output.append("\t<file>\n");
    output.append("\t\t<name>").append(this.srcFile).append("</name>\n");

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

    output.append("\t</file>\n");
    output.append("</document>\n");

    srcFile.close();
    dstFile.close();
    return output.toString();
  }

  private String read(RandomAccessFile file, int pos, int length) throws IOException {
    int count = 0;
    file.seek(pos);
    StringBuilder input = new StringBuilder();
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

  @Override
  public String toString() {
    return changeList.toString();
  }
}
