package simplediff.gumtree.core.actions.model;

import java.util.List;

public class MethodReorderChange extends Change {

  private MethodReorderChange(final String changeText, final int priority) {
    super(changeText, ChangeType.METHOD_REORDER);
    this.changePriority = priority;
  }

  public static MethodReorderChange createMethodReorderChange(final List<String> initialOrder, final List<String> finalOrder){
    final String insertPlaceholder = "Initial order &lt;ul&gt;&lt;li&gt;%s&lt;/li&gt;&lt;ul&gt;";
    return new MethodReorderChange(String.format(insertPlaceholder, String.join("&lt;/li&gt;&lt;li&gt;", initialOrder)), 0);
  }
}
