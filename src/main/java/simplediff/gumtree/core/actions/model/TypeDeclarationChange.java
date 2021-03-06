package simplediff.gumtree.core.actions.model;

import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

public class TypeDeclarationChange extends Change {

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
   */
  private TypeDeclarationChange(final String changeText, final int priority) {
    super(changeText, ChangeType.TYPE_DECLARATION);
    this.changePriority = priority;
  }

  public static TypeDeclarationChange createInsertTypeDeclarationChange(
      final String typeName, final String enclosingScope,
      boolean update) {
    if(update) {
      changeCounter.put("Additions", changeCounter.get("Additions") + 1);
    }
    final String insertPlaceholder = "%s added in scope %s";
    return new TypeDeclarationChange(
        String.format(
            insertPlaceholder,
            typeName.substring(0, 1).toUpperCase()
                + typeName.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), ""),
            (enclosingScope.equals("CompilationUnit") ? "Source file" : enclosingScope)),
        12);
  }

  public static TypeDeclarationChange createDeleteTypeDeclarationChange(
      final String typeName, final String enclosingScope,
      boolean update) {
    if(update) {
      changeCounter.put("Removals", changeCounter.get("Removals") + 1);
    }
    final String deletePlaceholder = "%s removed from scope %s";
    return new TypeDeclarationChange(
        String.format(
            deletePlaceholder,
            typeName.substring(0, 1).toUpperCase()
                + typeName.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), ""),
            (enclosingScope.equals("CompilationUnit") ? "Source file" : enclosingScope)),
        14);
  }

  public static TypeDeclarationChange createUpdateTypeDeclarationChange(
      final String typeName, final String newTypeName, final String enclosingScope,
      boolean update) {
    if(update) {
      changeCounter.put("Updates", changeCounter.get("Updates") + 1);
    }
    final String updatePlaceholder = "%s changed to %s in scope %s";
    return new TypeDeclarationChange(
        String.format(
            updatePlaceholder,
            typeName.substring(0, 1).toUpperCase()
                + typeName.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), ""),
            newTypeName.substring(0, 1).toUpperCase()
                + newTypeName.substring(1).replaceAll("(?i)" + Pattern.quote("declaration"), ""),
            (enclosingScope.equals("CompilationUnit") ? "Source file" : enclosingScope)),
        13);
  }

  public static void reset(){
    changeCounter.put("Additions", 0);
    changeCounter.put("Updates", 0);
    changeCounter.put("Removals", 0);
  }
}
