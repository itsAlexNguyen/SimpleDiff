package simplediff.gumtree.core.actions.model;

import java.util.Hashtable;
import java.util.Map;

public class MethodChange extends SourceChange {

  public static Map<String, Integer> changeCounter = new Hashtable<String, Integer>();

  static {
    changeCounter.put("Additions", 0);
    changeCounter.put("Updates", 0);
    changeCounter.put("Removals", 0);
  }


  /**
   * Constructor for Change objects.
   *
   * @param changeText summary of change
   * @param srcStart start position in source file
   * @param srcLength end position in source file
   * @param dstStart start position in destination file
   * @param dstLength end position in destination file
   */
  private MethodChange(
      final String changeText,
      final int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength, final int priority) {
    super(changeText, ChangeType.METHOD, srcStart, srcLength, dstStart, dstLength);
    this.changePriority = priority;
  }

  public static MethodChange createInsertMethodChange(
      final String methodName,
      final String enclosingType,
      final String enclosingTypeName,
      int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength) {
    changeCounter.put("Additions", changeCounter.get("Additions") + 1);
    final String placeHolder = "Method %s added to %s %s";
    return new MethodChange(
        String.format(placeHolder, methodName, enclosingType, enclosingTypeName),
        srcStart,
        srcLength,
        dstStart,
        dstLength, 4);
  }

  public static MethodChange createUpdateMethodChange(
      final String srcMethodName,
      final String dstMethodName,
      final String enclosingType,
      final String enclosingTypeName,
      final int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength) {
    changeCounter.put("Updates", changeCounter.get("Updates") + 1);
    final String placeHolder = "Method %s was updated to %s in %s %s";
    return new MethodChange(
        String.format(placeHolder, srcMethodName, dstMethodName, enclosingType, enclosingTypeName),
        srcStart,
        srcLength,
        dstStart,
        dstLength, 6);
  }

  public static MethodChange createDeleteMethodChange(
      final String methodName,
      final String enclosingType,
      final String enclosingTypeName,
      int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength) {
    changeCounter.put("Removals", changeCounter.get("Removals") + 1);
    final String placeHolder = "Method %s removed from %s %s";
    return new MethodChange(
        String.format(placeHolder, methodName, enclosingType, enclosingTypeName),
        srcStart,
        srcLength,
        dstStart,
        dstLength, 5);
  }

  public static void reset(){
    changeCounter.put("Additions", 0);
    changeCounter.put("Updates", 0);
    changeCounter.put("Removals", 0);
  }
}
