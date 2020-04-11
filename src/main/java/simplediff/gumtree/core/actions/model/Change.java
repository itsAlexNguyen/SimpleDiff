package simplediff.gumtree.core.actions.model;

import java.util.Hashtable;
import java.util.Map;

/** Represents a single diff. */
public class Change implements Comparable<Change> {

  protected final String changeText;

  protected final String openingChangeTag = "\t\t\t<change>\n";
  protected final String closingChangeTag = "\t\t\t</change>\n";

  protected final String openingTextTag = "\t\t\t\t<change-text>";
  protected final String closingTextTag = "\n\t\t\t\t</change-text>\n";

  protected final ChangeType changeType;
  protected int changePriority = Integer.MAX_VALUE;

  public Change(final String changeText, final ChangeType changeType) {
    this.changeText = changeText;
    this.changeType = changeType;
  }

  public String getChangeText() {
    return changeText;
  }

  @Override
  public String toString() {
    return changeText;
  }

  public String getXMLString() {
    return changeType.getOpeningTag() +
        openingChangeTag +
        openingTextTag +
        changeText +
        closingTextTag +
        closingChangeTag +
        changeType.getClosingTag();
  }

  @Override
  public int compareTo(Change o) {
    return Integer.compare(this.changePriority, o.changePriority);
  }
}
