package simplediff.gumtree.core.actions.model;

/** Represents a single diff. */
public class Change implements Comparable<Change> {

  private final String changeText;
  protected int changePriority = Integer.MAX_VALUE;

  public Change(final String changeText) {
    this.changeText = changeText;
  }

  public String getChangeText() {
    return changeText;
  }

  @Override
  public String toString() {
    return changeText;
  }

  @Override
  public int compareTo(Change o) {
    return Integer.compare(this.changePriority, o.changePriority);
  }
}
