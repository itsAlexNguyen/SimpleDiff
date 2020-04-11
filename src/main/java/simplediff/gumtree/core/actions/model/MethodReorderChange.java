package simplediff.gumtree.core.actions.model;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MethodReorderChange extends Change {

  public static Map<String, Integer> changeCounter = new Hashtable<String, Integer>();

  static {
    changeCounter.put("Additions", 0);
    changeCounter.put("Updates", 0);
    changeCounter.put("Removals", 0);
  }


  private MethodReorderChange(final String changeText, final int priority) {
    super(changeText, ChangeType.METHOD_REORDER);
    this.changePriority = priority;
  }

  public static MethodReorderChange createMethodReorderChange(final List<String> initialOrder, final List<String> finalOrder){
    changeCounter.put("Additions", changeCounter.get("Updates") + 1);
    final String insertPlaceholder =
        "&lt;div class=\"row\"&gt;&lt;div class=\"col-6\"&gt; Initial order &lt;ul&gt;&lt;li&gt;%s&lt;/li&gt;&lt;/ul&gt;&lt;/div&gt;&lt;div class=\"col-6\"&gt;" +
            "New order &lt;ul&gt;&lt;li&gt;%s&lt;/li&gt;&lt;/ul&gt;&lt;/div&gt;&lt;/div&gt;";
    return new MethodReorderChange(String.format(insertPlaceholder, String.join("&lt;/li&gt;&lt;li&gt;", initialOrder), String.join("&lt;/li&gt;&lt;li&gt;", finalOrder)), 0);
  }

  public static void reset(){
    changeCounter.put("Additions", 0);
    changeCounter.put("Updates", 0);
    changeCounter.put("Removals", 0);
  }
}
