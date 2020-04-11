package simplediff.gumtree.core.actions.model;

import java.util.List;

public class MethodReorderChange extends Change {

  private MethodReorderChange(final String changeText, final int priority) {
    super(changeText, ChangeType.METHOD_REORDER);
    this.changePriority = priority;
  }

  public static MethodReorderChange createMethodReorderChange(final List<String> initialOrder, final List<String> finalOrder){
    final String insertPlaceholder =
        "&lt;div class=\"row\"&gt;&lt;div class=\"col-6\"&gt; Initial order &lt;ul&gt;&lt;li&gt;%s&lt;/li&gt;&lt;/ul&gt;&lt;/div&gt;&lt;div class=\"col-6\"&gt;" +
            "New order &lt;ul&gt;&lt;li&gt;%s&lt;/li&gt;&lt;/ul&gt;&lt;/div&gt;&lt;/div&gt;";
    return new MethodReorderChange(String.format(insertPlaceholder, String.join("&lt;/li&gt;&lt;li&gt;", initialOrder), String.join("&lt;/li&gt;&lt;li&gt;", finalOrder)), 0);
  }
}
