package org.treedoc.buffer;

import org.treedoc.path.TreeDocPath;
import org.treedoc.utils.Pair;

import java.util.List;

/**
 * Atom buffer
 * @param <A> - type of atom
 * @param <D> - type of disambiguator, Disambiguators must be unique and ordered (3.1)
 */
public interface AtomBuffer<A, D extends Comparable<D>> {
	List<Pair<TreeDocPath<D>, A>> getEntries();
	void insert(TreeDocPath<D> treeDocPath, A atom);
	void delete(TreeDocPath<D> treeDocPath);
}
