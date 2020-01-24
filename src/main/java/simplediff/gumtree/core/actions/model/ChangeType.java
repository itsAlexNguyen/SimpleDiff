package simplediff.gumtree.core.actions.model;

public enum ChangeType {
  PACKAGE("\t\t<change-pkg>\n", "\t\t</change-pkg>\n"),
  IMPORT("\t\t<change-import\n>", "\t\t</change-import>\n"),
  MODIFIER("\t\t<change-modifier>", "\t\t</change-modifier>\n"),
  METHOD("\t\t<change-method>\n", "\t\t</change-method>\n");

  private final String openingTag;
  private final String closingTag;

  ChangeType(String openingTag, String closingTag) {
    this.openingTag = openingTag;
    this.closingTag = closingTag;
  }

  public String getOpeningTag() {
    return this.openingTag;
  }

  public String getClosingTag() {
    return this.closingTag;
  }

}
