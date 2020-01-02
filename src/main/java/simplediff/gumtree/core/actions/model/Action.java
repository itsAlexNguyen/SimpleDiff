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

package simplediff.gumtree.core.actions.model;

import simplediff.gumtree.core.tree.ITree;
import simplediff.gumtree.core.tree.Type;

public abstract class Action {
  protected ITree node;
  protected Type type;

  public Action(ITree node) {
    this.node = node;
  }

  public Action(ITree node, Type type) {
    this.node = node;
    this.type = type;
  }

  public ITree getNode() {
    return node;
  }

  public void setNode(ITree node) {
    this.node = node;
  }

  public Type getType() {
    return type;
  }

  public abstract String getName();

  /** Equals method for comparing Actions. */
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o == null) {
      return false;
    } else if (o.getClass() != this.getClass()) {
      return false;
    }
    return node == ((Action) o).node;
  }
}
