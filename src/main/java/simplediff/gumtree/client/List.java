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

package simplediff.gumtree.client;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import simplediff.gumtree.core.gen.Generators;
import simplediff.gumtree.core.matchers.Matchers;

@Register(description = "List things (matchers, generators, properties, ...)")
public class List extends Client {

  // This list is generated using (manually) list_properties (it is only an heuristic), some
  // properties may be missing
  public static final String[] properties =
      new String[] {
        "gumtree.client.experimental (simplediff.gumtree.core.client.Run)",
        "gumtree.client.web.port (simplediff.gumtree.core.client.diff.WebDiff)",
        "gumtree.generator.experimental (simplediff.gumtree.core.gen.TreeGeneratorRegistry)",
        "line.separator (simplediff.gumtree.core.io.IndentingXMLStreamWriter)",
        "user.dir (simplediff.gumtree.core.client.diff.AbstractDiffClient)"
      };
  private static final String SYNTAX = "Syntax: list " + Arrays.toString(Listable.values());
  private final Listable item;

  /**
   * Constructor.
   *
   * @param args - string elements
   */
  public List(String[] args) {
    super(args);

    if (args.length == 0) {
      throw new Option.OptionException(SYNTAX);
    }

    try {
      Listable listable = Listable.valueOf(args[0].toUpperCase());
      item = listable;
    } catch (Exception e) {
      throw new Option.OptionException(SYNTAX);
    }
  }

  @Override
  public void run() throws IOException {
    item.print(System.out);
  }

  enum Listable {
    MATCHERS {
      @Override
      Collection<?> list() {
        return Matchers.getInstance().getEntries();
      }
    },
    GENERATORS {
      @Override
      Collection<?> list() {
        return Generators.getInstance().getEntries();
      }
    },
    PROPERTIES {
      @Override
      Collection<?> list() {
        return Arrays.asList(properties);
      }
    },
    CLIENTS {
      @Override
      Collection<?> list() {
        return Clients.getInstance().getEntries();
      }
    };

    void print(PrintStream out) {
      this.list().forEach(item -> out.println(item));
    }

    abstract Collection<?> list();
  }
}
