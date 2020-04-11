package simplediff.gumtree.core.actions.model;

public enum ChangeType {
  PACKAGE("\t\t<change-pkg>\n", "\t\t</change-pkg>\n"),
  IMPORT("\t\t<change-import\n>", "\t\t</change-import>\n"),
  TYPE_DECLARATION("\t\t<change-type-declaration>\n", "\t\t</change-type-declaration>\n"),
  METHOD("\t\t<change-method>\n", "\t\t</change-method>\n"),
  MODIFIER("\t\t<change-modifier>", "\t\t</change-modifier>\n"),
  FIELD_DECLARATION("\t\t<change-field-declaration>\n", "\t\t</change-field-declaration>\n"),
  JAVADOC("\t\t<change-javadoc\n>", "\t\t</change-javadoc>\n"),
  METHOD_REORDER("\t\t<change-method-reorder>\n", "\t\t</change-method-reorder>\n");

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
