package simplediff.gumtree.core.actions.model;

import java.util.Hashtable;
import java.util.Map;

public class ImportChange extends Change {

  public static Map<String, Integer> changeCounter = new Hashtable<String, Integer>();

  static {
    changeCounter.put("Additions", 0);
    changeCounter.put("Updates", 0);
    changeCounter.put("Removals", 0);
  }


  private ImportChange(String changeText, int priority) {
    super(changeText, ChangeType.IMPORT);
    this.changePriority = priority;
  }

  public static ImportChange createInsertImportChange(String importName, boolean update) {
    if(update) {
      changeCounter.put("Additions", changeCounter.get("Additions") + 1);
    }
    final String insertPlaceholder = "Import %s added to file";
    return new ImportChange(String.format(insertPlaceholder, importName), 1);
  }

  public static ImportChange createDeleteImportChange(String importName, boolean update) {
    if(update) {
      changeCounter.put("Removals", changeCounter.get("Removals") + 1);
    }
    final String deletePlaceholder = "Import %s removed from file";
    return new ImportChange(String.format(deletePlaceholder, importName), 2);
  }

  public static void reset(){
    changeCounter.put("Additions", 0);
    changeCounter.put("Updates", 0);
    changeCounter.put("Removals", 0);
  }
}
