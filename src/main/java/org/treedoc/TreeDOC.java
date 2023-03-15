package org.treedoc;

import org.treedoc.buffer.binary_tree.BinaryTreeAtomBuffer;
import org.treedoc.path.TreeDocPath;
import org.treedoc.utils.Pair;

import java.util.List;

public interface TreeDOC<A, D extends Comparable<D>> {
	static <A, D extends Comparable<D>> TreeDOC<A, D> create() {
		return new TreeDOCImpl<>(new BinaryTreeAtomBuffer<A, D>());
	}

	List<Pair<TreeDocPath<D>, A>> getEntries();
	TreeDocPath<D> insertBetween(TreeDocPath<D> leftTreePath, TreeDocPath<D> rightTreePath, A atom, D disambiguator);
	void delete(TreeDocPath<D> path);
}
