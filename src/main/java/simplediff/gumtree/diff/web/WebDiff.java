/*
 * This file is part of GumTree.
 *
 * GumTree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GumTree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GumTree.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2011-2015 Jean-Rémy Falleri <jr.falleri@gmail.com>
 * Copyright 2011-2015 Floréal Morandat <florealm@gmail.com>
 */

package simplediff.gumtree.diff.web;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import org.atteo.classindex.ClassIndex;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;
import simplediff.gumtree.client.Option;
import simplediff.gumtree.client.Register;
import simplediff.gumtree.core.actions.ChawatheScriptGenerator;
import simplediff.gumtree.core.actions.XMLChawatheScriptGenerator;
import simplediff.gumtree.core.gen.Generators;
import simplediff.gumtree.core.gen.Registry;
import simplediff.gumtree.core.gen.TreeGenerator;
import simplediff.gumtree.core.io.DirectoryComparator;
import simplediff.gumtree.core.tree.TreeContext;
import simplediff.gumtree.core.utils.Pair;
import simplediff.gumtree.diff.AbstractDiffClient;

@Register(
    description = "a web diff client",
    options = WebDiff.Options.class,
    priority = Registry.Priority.HIGH)
public class WebDiff extends AbstractDiffClient<WebDiff.Options> {

  public WebDiff(String[] args) {
    super(args);
  }

  public static void initGenerators() {
    ClassIndex.getSubclasses(TreeGenerator.class)
        .forEach(
            gen -> {
              simplediff.gumtree.core.gen.Register a =
                  gen.getAnnotation(simplediff.gumtree.core.gen.Register.class);
              if (a != null) Generators.getInstance().install(gen, a);
            });
  }

  private static String render(Renderable r) {
    HtmlCanvas c = new HtmlCanvas();
    try {
      r.renderOn(c);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return c.toHtml();
  }

  private static String readFile(String path, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

  @Override
  protected Options newOptions() {
    return new Options();
  }

  @Override
  public void run() {
    DirectoryComparator comparator = new DirectoryComparator(opts.src, opts.dst);
    comparator.compare();
    Pair<File, File> pair = comparator.getModifiedFiles().get(0);
    try {
      Renderable view =
          new DiffView(
              pair.first,
              pair.second,
              this.getTreeContext(pair.first.getAbsolutePath()),
              this.getTreeContext(pair.second.getAbsolutePath()),
              getMatcher(),
              new ChawatheScriptGenerator());
      System.out.println(render(view));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String generate(String title, String targetBranch) {
    final DirectoryComparator comparator = new DirectoryComparator(opts.src, opts.dst);
    comparator.compare();
    final List<Pair<TreeContext, TreeContext>> contexts = new LinkedList<>();

    for (Pair<File,File> pair: comparator.getModifiedFiles()) {
      contexts.add(new Pair<>(this.getTreeContext(pair.first.getAbsolutePath()), this.getTreeContext(pair.second.getAbsolutePath())));
    }

    try {
      return XMLDiff.publish(
              comparator.getModifiedFiles(),
              contexts,
              comparator.getModifiedFiles().size(),
              title,
              targetBranch,
              getMatcher(),
              new ChawatheScriptGenerator(),
              new XMLChawatheScriptGenerator());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "File diff could not be performed";
  }

  static class Options extends AbstractDiffClient.Options {
    protected int defaultPort = Integer.parseInt(System.getProperty("gt.webdiff.port", "4567"));
    boolean stdin = true;

    @Override
    public Option[] values() {
      return Option.Context.addValue(
          super.values(),
          new Option("--port", String.format("set server port (default to %d)", defaultPort), 1) {
            @Override
            protected void process(String name, String[] args) {
              int p = Integer.parseInt(args[0]);
              if (p > 0) defaultPort = p;
              else System.err.printf("Invalid port number (%s), using %d\n", args[0], defaultPort);
            }
          },
          new Option("--no-stdin", String.format("Do not listen to stdin"), 0) {
            @Override
            protected void process(String name, String[] args) {
              stdin = false;
            }
          });
    }
  }
}
