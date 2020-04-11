package simplediff.gumtree.core.actions.model;

import java.util.regex.Pattern;

public class FieldDeclarationChange extends SourceChange {

  /**
   * Constructor for Change objects.
   *
   * @param changeText summary of change
   * @param srcStart start position in source file
   * @param srcLength end position in source file
   * @param dstStart start position in destination file
   * @param dstLength end position in destination file
   */
  private FieldDeclarationChange(
      final String changeText,
      final int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength, final int priority) {
    super(changeText, ChangeType.FIELD_DECLARATION, srcStart, srcLength, dstStart, dstLength);
    this.changePriority = priority;
  }

  public static FieldDeclarationChange createInsertFieldChange(
      final String methodName,
      final String enclosingType,
      final String enclosingTypeName,
      int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength) {
    final String placeHolder = "Field %s added to %s %s";
    return new FieldDeclarationChange(
        String.format(placeHolder, methodName, enclosingType.substring(0, 1).toUpperCase()
            + enclosingType.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), ""), enclosingTypeName),
        srcStart,
        srcLength,
        dstStart,
        dstLength, 9);
  }

  public static FieldDeclarationChange createUpdateFieldChange(
      final String srcMethodName,
      final String dstMethodName,
      final String enclosingType,
      final String enclosingTypeName,
      final int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength) {
    final String placeHolder = "Field %s was updated to %s in %s %s";
    return new FieldDeclarationChange(
        String.format(placeHolder, srcMethodName, dstMethodName, enclosingType.substring(0, 1).toUpperCase()
            + enclosingType.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), ""), enclosingTypeName),
        srcStart,
        srcLength,
        dstStart,
        dstLength, 10);
  }

  public static FieldDeclarationChange createDeleteFieldChange(
      final String methodName,
      final String enclosingType,
      final String enclosingTypeName,
      int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength) {
    final String placeHolder = "Field %s removed from %s %s";
    return new FieldDeclarationChange(
        String.format(placeHolder, methodName, enclosingType.substring(0, 1).toUpperCase()
            + enclosingType.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), ""), enclosingTypeName),
        srcStart,
        srcLength,
        dstStart,
        dstLength, 11);
  }

}
