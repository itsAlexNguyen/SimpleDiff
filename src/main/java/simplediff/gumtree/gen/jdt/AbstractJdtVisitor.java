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

package simplediff.gumtree.gen.jdt;

import static simplediff.gumtree.core.tree.TypeSet.type;


import java.util.ArrayDeque;
import java.util.Deque;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import simplediff.gumtree.core.tree.ITree;
import simplediff.gumtree.core.tree.TreeContext;
import simplediff.gumtree.core.tree.Type;
import simplediff.gumtree.gen.jdt.cd.EntityType;

public abstract class AbstractJdtVisitor extends ASTVisitor {

  protected TreeContext context = new TreeContext();

  protected Deque<ITree> trees = new ArrayDeque<>();

  public AbstractJdtVisitor() {
    super(true);
  }

  protected static Type nodeAsSymbol(ASTNode node) {
    return nodeAsSymbol(node.getNodeType());
  }

  protected static Type nodeAsSymbol(int id) {
    return type(ASTNode.nodeClassForType(id).getSimpleName());
  }

  TreeContext getTreeContext() {
    return context;
  }

  protected void pushNode(ASTNode n, String label) {
    push(nodeAsSymbol(n), label, n.getStartPosition(), n.getLength());
  }

  protected void pushFakeNode(EntityType n, int startPosition, int length) {
    Type type = type(n.name()); // FIXME is that consistent with AbstractJDTVisitor.type
    push(type, "", startPosition, length);
  }

  protected void push(Type type, String label, int startPosition, int length) {
    ITree t = context.createTree(type, label);
    t.setPos(startPosition);
    t.setLength(length);

    if (trees.isEmpty()) {
      context.setRoot(t);
    } else {
      ITree parent = trees.peek();
      t.setParentAndUpdateChildren(parent);
    }

    trees.push(t);
  }

  protected ITree getCurrentParent() {
    return trees.peek();
  }

  protected void popNode() {
    trees.pop();
  }
}
