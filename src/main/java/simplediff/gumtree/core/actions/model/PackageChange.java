package simplediff.gumtree.core.actions.model;

import java.util.Hashtable;
import java.util.Map;

public class PackageChange extends Change {

  public static Map<String, Integer> changeCounter = new Hashtable<String, Integer>();

  static {
    changeCounter.put("Additions", 0);
    changeCounter.put("Updates", 0);
    changeCounter.put("Removals", 0);
  }


  private PackageChange(final String changeText, final int priority) {
    super(changeText, ChangeType.PACKAGE);
    this.changePriority = priority;
  }

  public static PackageChange createInsertPackageChange(final String packageName) {
    changeCounter.put("Additions", changeCounter.get("Additions") + 1);
    final String insertPlaceholder = "Source added to package %s";
    return new PackageChange(String.format(insertPlaceholder, packageName), 0);
  }

  public static PackageChange createUpdatePackageChange(final String srcPackage, final String dstPackage) {
    changeCounter.put("Updates", changeCounter.get("Updates") + 1);
    final String updatePlaceholder = "Package %s changed to %s";
    return new PackageChange(String.format(updatePlaceholder, srcPackage, dstPackage), 0);
  }

  public static PackageChange createDeletePackageChange(final String packageName) {
    changeCounter.put("Removals", changeCounter.get("Removals") + 1);
    final String deletePlaceholder = "Source removed from package %s";
    return new PackageChange(String.format(deletePlaceholder, packageName), 0);
  }

  public static void reset(){
    changeCounter.put("Additions", 0);
    changeCounter.put("Updates", 0);
    changeCounter.put("Removals", 0);
  }
}
