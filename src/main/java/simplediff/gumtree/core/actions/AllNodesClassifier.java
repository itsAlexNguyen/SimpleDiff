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

import simplediff.gumtree.core.actions.model.Action;
import simplediff.gumtree.core.actions.model.Delete;
import simplediff.gumtree.core.actions.model.Insert;
import simplediff.gumtree.core.actions.model.Move;
import simplediff.gumtree.core.actions.model.TreeDelete;
import simplediff.gumtree.core.actions.model.TreeInsert;
import simplediff.gumtree.core.actions.model.Update;

public class AllNodesClassifier extends AbstractITreeClassifier {
  public AllNodesClassifier(Diff diff) {
    super(diff);
  }

  @Override
  public void classify() {
    for (Action a : diff.editScript) {
      if (a instanceof Delete) srcDelTrees.add(a.getNode());
      else if (a instanceof TreeDelete) {
        srcDelTrees.add(a.getNode());
        srcDelTrees.addAll(a.getNode().getDescendants());
      } else if (a instanceof Insert) dstAddTrees.add(a.getNode());
      else if (a instanceof TreeInsert) {
        dstAddTrees.add(a.getNode());
        dstAddTrees.addAll(a.getNode().getDescendants());
      } else if (a instanceof Update) {
        srcUpdTrees.add(a.getNode());
        dstUpdTrees.add(diff.mappings.getDstForSrc(a.getNode()));
      } else if (a instanceof Move) {
        srcMvTrees.add(a.getNode());
        srcMvTrees.addAll(a.getNode().getDescendants());
        dstMvTrees.add(diff.mappings.getDstForSrc(a.getNode()));
        dstMvTrees.addAll(diff.mappings.getDstForSrc(a.getNode()).getDescendants());
      }
    }
  }
}
