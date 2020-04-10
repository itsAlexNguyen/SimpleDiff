package simplediff.gumtree.core.actions.model;

import java.util.regex.Pattern;

public class JavadocChange extends SourceChange {

  private JavadocChange(
      String changeText,
      final int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength,
      int priority) {
    super(changeText, ChangeType.JAVADOC, srcStart, srcLength, dstStart, dstLength);
    this.changePriority = priority;
  }

  public static JavadocChange createInsertJavadocChange(
      String enclosingTypeName,
      final int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength) {
    final String insertPlaceholder = "Javadoc added to %s";
    return new JavadocChange(String.format(insertPlaceholder, enclosingTypeName.substring(0, 1).toUpperCase()
        + enclosingTypeName.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), "")), srcStart, srcLength, dstStart, dstLength, 12);
  }

  public static JavadocChange createUpdateJavadocChange(
      String enclosingTypeName,
      final int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength) {
    final String deletePlaceholder = "Javadoc updated for %s";
    return new JavadocChange(String.format(deletePlaceholder, enclosingTypeName.substring(0, 1).toUpperCase()
        + enclosingTypeName.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), "")), srcStart, srcLength, dstStart, dstLength, 13);
  }

  public static JavadocChange createDeleteJavadocChange(
      String enclosingTypeName,
      final int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength) {
    final String deletePlaceholder = "Javadoc removed from %s";
    return new JavadocChange(String.format(deletePlaceholder, enclosingTypeName.substring(0, 1).toUpperCase()
        + enclosingTypeName.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), "")), srcStart, srcLength, dstStart, dstLength, 14);
  }
}
