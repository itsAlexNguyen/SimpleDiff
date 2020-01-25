package simplediff.gumtree.core.actions.model;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SourceChange extends Change {
  private final int srcStart;
  private final int srcEnd;
  private final int dstStart;
  private final int dstEnd;

  /**
   * Constructor for Change objects.
   *
   * @param changeText summary of change
   * @param srcStart start position in source file
   * @param srcLength end position in source file
   * @param dstStart start position in destination file
   * @param dstLength end position in destination file
   */
  protected SourceChange(final String changeText, final ChangeType changeType, final int srcStart, final int srcLength, final int dstStart, final int dstLength) {
    super(changeText, changeType);
    this.srcStart = srcStart;
    this.srcEnd = srcLength;
    this.dstStart = dstStart;
    this.dstEnd = dstLength;
  }

  public int getSrcStart() {
    return srcStart;
  }

  public int getSrcLength() {
    return srcEnd;
  }

  public int getDstStart() {
    return dstStart;
  }

  public int getDstLength() {
    return dstEnd;
  }

  public String getXMLString(final RandomAccessFile srcFile, final RandomAccessFile dstFile) throws  IOException {
    final String srcOpeningSourceTag = "<change-src>\n";
    final String srcClosingSourceTag = "\n</change-src>\n";
    final String dstOpeningSourceTag = "<change-dst>\n";
    final String dstClosingSourceTag = "\n</change-dst>\n";
    
    final StringBuilder xmlString = new StringBuilder();
    xmlString.append(this.changeType.getOpeningTag());
    xmlString.append(this.openingChangeTag);
    xmlString.append(this.openingTextTag);
    xmlString.append(this.changeText);
    xmlString.append(this.closingTextTag);

    final int srcPos = getSrcStart();
    final int srcLength = getSrcLength();
    if (srcPos != -1) {
      String sourceCode = read(srcFile, srcPos, srcLength);
      xmlString.append(srcOpeningSourceTag);
      xmlString.append(sourceCode);
      xmlString.append(srcClosingSourceTag);
    }

    final int dstPos = getDstStart();
    final int dstLength = getDstLength();
    if (dstPos != -1) {
      String sourceCode = read(dstFile, dstPos, dstLength);
      xmlString.append(dstOpeningSourceTag);
      xmlString.append(sourceCode);
      xmlString.append(dstClosingSourceTag);
    }
    xmlString.append(closingChangeTag);
    xmlString.append(this.changeType.getClosingTag());

    return xmlString.toString();
  }

  private static String read(final RandomAccessFile file, final int pos, final int length) throws IOException {
    file.seek(pos);
    final StringBuilder input = new StringBuilder();
    try {
      int count = 0;
      while (count < length && pos + count < file.length()) {
        input.append((char) file.read());
        count++;
      }
    } catch (EOFException e) {
      System.out.println(e.getMessage());
    }
    return input.toString();
  }
}
