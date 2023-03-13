package org.treedoc.buffer.binary_tree;

import org.treedoc.buffer.AtomBuffer;
import org.treedoc.path.TreeDocPath;

import java.util.*;
import java.util.function.Consumer;

public class BinaryTreeAtomBuffer<A, D extends Comparable<D>> implements AtomBuffer<A, D> {
	private final TreeNode root = new TreeNode();

	@Override
	public List<A> getAtoms() {
		List<A> atoms = new LinkedList<>();
		inOrder(root, node -> {
			if (node.value != null) {
				atoms.add(node.value);
			}
		});
		return atoms;
	}

	@Override
	public void insert(TreeDocPath<D> treeDocPath, A atom) {
		if (atom == null) {
			throw new IllegalArgumentException("Atom must not be null!");
		}
		var parent = root;
		for (var i = 0; i < treeDocPath.length(); i++) {
			parent.createIfNeeded(treeDocPath.isSet(i), treeDocPath.disambiguatorAt(i));
			parent = parent.extractNode(treeDocPath.isSet(i), treeDocPath.disambiguatorAt(i));
		}
		parent.value = atom;
	}

	@Override
	public void delete(TreeDocPath<D> treeDocPath) {
		var n = treeDocPath.length();
		var nodesInPath = new ArrayList<TreeNode>(n);
		var currentParent = root;
		for (var i = 0; i < n; i++) {
			var node = currentParent.extractNode(treeDocPath.isSet(i), treeDocPath.disambiguatorAt(i));
			if (node == null) {
				break;
			}
			if (i == n - 1) {
				node.value = null;
			}
			nodesInPath.add(currentParent);
			currentParent = node;
		}
		// Garbage collection
		for (var i = nodesInPath.size() - 1; i >= 0; i--) {
			var p = nodesInPath.get(i);
			var node = p.extractNode(treeDocPath.isSet(i), treeDocPath.disambiguatorAt(i));
			if (!node.isEligibleForGC()) {
				break;
			}
			p.deleteNode(treeDocPath.isSet(i), treeDocPath.disambiguatorAt(i));
		}
	}

	@Override
	public boolean isEmpty() {
		return root.isEligibleForGC();
	}

	private class TreeNode {
		private A value;
		private TreeNode left;
		private TreeNode right;
		private final NavigableMap<D, TreeNode> leftMap = new TreeMap<>();
		private final NavigableMap<D, TreeNode> rightMap = new TreeMap<>();

		TreeNode extractNode(boolean isSet, D disambiguator) {
			if (disambiguator == null) {
				return isSet ? right : left;
			}
			if (isSet) {
				return rightMap.get(disambiguator);
			}
			return leftMap.get(disambiguator);
		}

		void deleteNode(boolean isSet, D disambiguator) {
			if (disambiguator == null) {
				if (isSet) {
					right = null;
				} else {
					left = null;
				}
			} else {
				(isSet ? rightMap : leftMap).remove(disambiguator);
			}
		}

		void createIfNeeded(boolean isSet, D disambiguator) {
			if (disambiguator == null) {
				if (isSet) {
					right = right == null ? new TreeNode() : right;
				} else {
					left = left == null ? new TreeNode() : left;
				}
			} else {
				if (isSet) {
					rightMap.computeIfAbsent(disambiguator, k -> new TreeNode());
				} else {
					leftMap.computeIfAbsent(disambiguator, k -> new TreeNode());
				}
			}
		}

		boolean isEligibleForGC() {
			return left == null
							&& right == null
							&& value == null
							&& allValuesNull(leftMap)
							&& allValuesNull(rightMap);
		}

		private boolean allValuesNull(Map<?, ?> map) {
			for (var o : map.values()) {
				if (o != null) {
					return false;
				}
			}
			return true;
		}
	}

	private void inOrder(TreeNode root, Consumer<TreeNode> consumer) {
		var stack = new LinkedList<TreeNode>();
		var tempStack = new LinkedList<TreeNode>();
		appendLeftSubTree(stack, root, tempStack);
		while (!stack.isEmpty()) {
			var node = stack.pollFirst();
			consumer.accept(node);
			appendLeftSubTree(stack, node.right, tempStack);
			for (var entry : node.rightMap.descendingMap().entrySet()) {
				appendLeftSubTree(stack, entry.getValue(), tempStack);
			}
		}
	}

	private void appendLeftSubTree(LinkedList<TreeNode> stack, TreeNode node, LinkedList<TreeNode> tempStack) {
		tempStack.addFirst(node);
		while (!tempStack.isEmpty()) {
			var current = tempStack.pollFirst();
			if (current == null) {
				continue;
			}
			stack.addFirst(current);
			tempStack.addFirst(current.left);
			for (var entry : current.leftMap.entrySet()) {
				tempStack.addFirst(entry.getValue());
			}
		}
	}

}
