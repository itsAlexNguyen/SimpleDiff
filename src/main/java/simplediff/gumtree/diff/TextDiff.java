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

package simplediff.gumtree.diff;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import simplediff.gumtree.client.Option;
import simplediff.gumtree.client.Register;
import simplediff.gumtree.core.actions.ChawatheScriptGenerator;
import simplediff.gumtree.core.actions.EditScript;
import simplediff.gumtree.core.io.ActionsIoUtils;
import simplediff.gumtree.core.matchers.MappingStore;
import simplediff.gumtree.core.tree.TreeContext;

@Register(
    name = "diff",
    description = "Dump actions in our textual format",
    options = AbstractDiffClient.Options.class)
public class TextDiff extends AbstractDiffClient<TextDiff.Options> {

  public TextDiff(String[] args) {
    super(args);

    if (opts.format == null) {
      opts.format = OutputFormat.TEXT;
      if (opts.output != null) {
        if (opts.output.endsWith(".json")) opts.format = OutputFormat.JSON;
        else if (opts.output.endsWith(".xml")) opts.format = OutputFormat.XML;
      }
    }
  }

  @Override
  protected Options newOptions() {
    return new Options();
  }

  @Override
  public void run() {
    MappingStore ms = matchTrees();
    EditScript actions = new ChawatheScriptGenerator().computeActions(ms);
    try {
      ActionsIoUtils.ActionSerializer serializer =
          opts.format.getSerializer(getSrcTreeContext(), actions, ms);
      if (opts.output == null) serializer.writeTo(System.out);
      else serializer.writeTo(opts.output);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  enum OutputFormat { // TODO make a registry for that also ?
    TEXT {
      @Override
      ActionsIoUtils.ActionSerializer getSerializer(
          TreeContext sctx, EditScript actions, MappingStore mappings) throws IOException {
        return ActionsIoUtils.toText(sctx, actions, mappings);
      }
    },
    XML {
      @Override
      ActionsIoUtils.ActionSerializer getSerializer(
          TreeContext sctx, EditScript actions, MappingStore mappings) throws IOException {
        return ActionsIoUtils.toXml(sctx, actions, mappings);
      }
    },
    JSON {
      @Override
      ActionsIoUtils.ActionSerializer getSerializer(
          TreeContext sctx, EditScript actions, MappingStore mappings) throws IOException {
        return ActionsIoUtils.toJson(sctx, actions, mappings);
      }
    };

    abstract ActionsIoUtils.ActionSerializer getSerializer(
        TreeContext sctx, EditScript actions, MappingStore mappings) throws IOException;
  }

  public static class Options extends AbstractDiffClient.Options {
    protected OutputFormat format;
    protected String output;

    @Override
    public Option[] values() {
      return Option.Context.addValue(
          super.values(),
          new Option("-f", String.format("format: %s", Arrays.toString(OutputFormat.values())), 1) {
            @Override
            protected void process(String name, String[] args) {
              try {
                format = OutputFormat.valueOf(args[0].toUpperCase());
              } catch (IllegalArgumentException e) {
                System.err.printf(
                    "No such format '%s', available formats are: %s\n",
                    args[0].toUpperCase(), Arrays.toString(OutputFormat.values()));
                System.exit(-1);
              }
            }
          },
          new Option("-o", "output file", 1) {
            @Override
            protected void process(String name, String[] args) {
              output = args[0];
            }
          });
    }

    @Override
    void dump(PrintStream out) {
      super.dump(out);
      out.printf("format: %s\n", format);
      out.printf("output file: %s\n", output == null ? "<Stdout>" : output);
    }
  }
}
