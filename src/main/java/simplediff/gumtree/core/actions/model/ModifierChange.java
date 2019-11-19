package simplediff.gumtree.core.actions.model;

import java.util.List;

public class ModifierChange extends SourceChange {

  /**
   * Constructor for Change objects.
   *
   * @param changeText summary of change
   * @param srcStart start position in source file
   * @param srcLength length in source file
   * @param dstStart start position in destination file
   * @param dstLength end position in destination file
   */
  private ModifierChange(
      final String changeText,
      final int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength, final int priority) {
    super(changeText, srcStart, srcLength, dstStart, dstLength);
    this.changePriority = priority;
  }

  public static ModifierChange createModifierChange(
      final List<String> modifierList,
      final String enclosingDeclaration,
      final String enclosingDeclarationName,
      int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength) {
    final String placeHolder = "Modifiers of %s %s changed to %s";
    return new ModifierChange(
        String.format(
            placeHolder,
            enclosingDeclaration,
            enclosingDeclarationName,
            String.join(",", modifierList)),
        srcStart,
        srcLength,
        dstStart,
        dstLength, 3);
  }
}
