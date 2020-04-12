package simplediff.gumtree.core.actions.model;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ModifierChange extends SourceChange {

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
    super(changeText, ChangeType.MODIFIER, srcStart, srcLength, dstStart, dstLength);
    this.changePriority = priority;
  }

  public static ModifierChange createModifierChange(
      final List<String> modifierList,
      final String enclosingDeclaration,
      final String enclosingDeclarationName,
      int srcStart,
      final int srcLength,
      final int dstStart,
      final int dstLength,
      boolean update) {
    if(update) {
      changeCounter.put("Updates", changeCounter.get("Updates") + 1);
    }
    final String placeHolder = "Modifiers of %s %s changed to %s";
    String dec = "";
    if(enclosingDeclaration.length() > 1){
        dec = enclosingDeclaration.substring(0, 1).toUpperCase()
                + enclosingDeclaration.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), "");
    } else{
      dec = enclosingDeclaration;
    }
    return new ModifierChange(
        String.format(
            placeHolder,
            dec,
            enclosingDeclarationName,
            String.join(",", modifierList)),
        srcStart,
        srcLength,
        dstStart,
        dstLength, 3);
  }

  public static void reset(){
    changeCounter.put("Additions", 0);
    changeCounter.put("Updates", 0);
    changeCounter.put("Removals", 0);
  }
}
