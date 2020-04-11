package simplediff.gumtree.core.actions.model;

import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

public class JavadocChange extends SourceChange {

  public static Map<String, Integer> changeCounter = new Hashtable<String, Integer>();

  static {
    changeCounter.put("Additions", 0);
    changeCounter.put("Updates", 0);
    changeCounter.put("Removals", 0);
  }


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
      final int dstLength,
      boolean update) {
    if(update) {
      changeCounter.put("Additions", changeCounter.get("Additions") + 1);
    }
    final String insertPlaceholder = "Javadoc added to %s";
    return new JavadocChange(String.format(insertPlaceholder, enclosingTypeName.substring(0, 1).toUpperCase()
        + enclosingTypeName.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), "")), srcStart, srcLength, dstStart, dstLength, 12);
  }

  public static JavadocChange createUpdateJavadocChange(
      String enclosingTypeName,
      final int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength,
      boolean update) {
    if(update) {
      changeCounter.put("Updates", changeCounter.get("Updates") + 1);
    }
    final String deletePlaceholder = "Javadoc updated for %s";
    return new JavadocChange(String.format(deletePlaceholder, enclosingTypeName.substring(0, 1).toUpperCase()
        + enclosingTypeName.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), "")), srcStart, srcLength, dstStart, dstLength, 13);
  }

  public static JavadocChange createDeleteJavadocChange(
      String enclosingTypeName,
      final int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength,
      boolean update) {
    if(update) {
      changeCounter.put("Removals", changeCounter.get("Removals") + 1);
    }
    final String deletePlaceholder = "Javadoc removed from %s";
    return new JavadocChange(String.format(deletePlaceholder, enclosingTypeName.substring(0, 1).toUpperCase()
        + enclosingTypeName.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), "")), srcStart, srcLength, dstStart, dstLength, 14);
  }

  public static void reset(){
    changeCounter.put("Additions", 0);
    changeCounter.put("Updates", 0);
    changeCounter.put("Removals", 0);
  }
}
