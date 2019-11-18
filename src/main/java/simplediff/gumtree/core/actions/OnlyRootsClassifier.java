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

package simplediff.gumtree.core.actions;

import java.util.HashSet;
import java.util.Set;
import simplediff.gumtree.core.actions.model.Action;
import simplediff.gumtree.core.actions.model.Delete;
import simplediff.gumtree.core.actions.model.Insert;
import simplediff.gumtree.core.actions.model.Move;
import simplediff.gumtree.core.actions.model.TreeDelete;
import simplediff.gumtree.core.actions.model.TreeInsert;
import simplediff.gumtree.core.actions.model.Update;
import simplediff.gumtree.core.tree.ITree;

public class OnlyRootsClassifier extends AbstractITreeClassifier {
  public OnlyRootsClassifier(Diff diff) {
    super(diff);
  }

  @Override
  public void classify() {
    for (Action a : diff.editScript)
      if (a.getType() != null && a.getType().name.equals("PackageDeclaration"))
        System.out.println(a);
    //            final String name = a.getType().name;
    //            switch (name) {
    //                case "PackageDeclaration":
    //                    System.out.print("Package " + a);
    //                    break;
    //                case "ImportDeclaration":
    //                    System.out.print("Import " + a);
    //                    break;
    //                default:
    //                    System.out.print("Other " + a);
    //            }
    //        }

    Set<ITree> insertedDsts = new HashSet<>();
    for (Action a : diff.editScript) if (a instanceof Insert) insertedDsts.add(a.getNode());

    Set<ITree> deletedSrcs = new HashSet<>();
    for (Action a : diff.editScript) if (a instanceof Delete) deletedSrcs.add(a.getNode());

    for (Action a : diff.editScript) {
      if (a instanceof TreeDelete) srcDelTrees.add(a.getNode());
      else if (a instanceof Delete) {
        if (!(deletedSrcs.containsAll(a.getNode().getDescendants())
            && deletedSrcs.contains(a.getNode().getParent()))) srcDelTrees.add(a.getNode());
      } else if (a instanceof Insert) {
        if (!(insertedDsts.containsAll(a.getNode().getDescendants())
            && insertedDsts.contains(a.getNode().getParent()))) dstAddTrees.add(a.getNode());
      } else if (a instanceof TreeInsert) dstAddTrees.add(a.getNode());
      else if (a instanceof Update) {
        srcUpdTrees.add(a.getNode());
        dstUpdTrees.add(diff.mappings.getDstForSrc(a.getNode()));
      } else if (a instanceof Move) {
        srcMvTrees.add(a.getNode());
        dstMvTrees.add(diff.mappings.getDstForSrc(a.getNode()));
      }
    }
  }
}
