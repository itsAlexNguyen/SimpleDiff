package simplediff.gumtree.core.actions.model;

public class PackageChange extends Change {

  private PackageChange(final String changeText, final int priority) {
    super(changeText, ChangeType.PACKAGE);
    this.changePriority = priority;
  }

  public static PackageChange createInsertPackageChange(final String packageName) {
    final String insertPlaceholder = "Source added to package %s";
    return new PackageChange(String.format(insertPlaceholder, packageName), 0);
  }

  public static PackageChange createUpdatePackageChange(final String srcPackage, final String dstPackage) {
    final String updatePlaceholder = "Package %s changed to %s";
    return new PackageChange(String.format(updatePlaceholder, srcPackage, dstPackage), 0);
  }

  public static PackageChange createDeletePackageChange(final String packageName) {
    final String deletePlaceholder = "Source removed from package %s";
    return new PackageChange(String.format(deletePlaceholder, packageName), 0);
  }
}
