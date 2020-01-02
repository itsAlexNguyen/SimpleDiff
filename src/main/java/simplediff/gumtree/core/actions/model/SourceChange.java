package simplediff.gumtree.core.actions.model;

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
  protected SourceChange(final String changeText, final int srcStart, final int srcLength, final int dstStart, final int dstLength) {
    super(changeText);
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
}
