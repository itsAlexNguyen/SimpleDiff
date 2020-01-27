package simplediff.gumtree.core.actions.model;

public class ImportChange extends Change {

  private ImportChange(String changeText, int priority) {
    super(changeText, ChangeType.IMPORT);
    this.changePriority = priority;
  }

  public static ImportChange createInsertImportChange(String importName) {
    final String insertPlaceholder = "Import %s added to file";
    return new ImportChange(String.format(insertPlaceholder, importName), 1);
  }

  public static ImportChange createDeleteImportChange(String importName) {
    final String deletePlaceholder = "Import %s removed from file";
    return new ImportChange(String.format(deletePlaceholder, importName), 2);
  }
}
