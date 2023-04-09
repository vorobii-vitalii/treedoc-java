package org.treedoc;

import org.treedoc.buffer.AtomBuffer;
import org.treedoc.path.TreeDocPath;
import org.treedoc.path.TreeDocPathComparator;
import org.treedoc.utils.Pair;

import java.util.List;
import java.util.Objects;

public class TreeDOCImpl<A, D extends Comparable<D>> implements TreeDOC<A, D> {
	private final AtomBuffer<A, D> atomBuffer;
	private final TreeDocPathComparator<D> treeDocPathComparator = new TreeDocPathComparator<>();

	public TreeDOCImpl(AtomBuffer<A, D> atomBuffer) {
		this.atomBuffer = atomBuffer;
	}

	@Override
	public List<Pair<TreeDocPath<D>, A>> getEntries() {
		return atomBuffer.getEntries();
	}

	@Override
	public TreeDocPath<D> insertBetween(
			TreeDocPath<D> leftTreePath,
			TreeDocPath<D> rightTreePath,
			A atom,
			D disambiguator
	) {
		var newPosition = createInsertionIdentifier(leftTreePath, rightTreePath, disambiguator);
		atomBuffer.insert(newPosition, atom);
		return newPosition;
	}

	@Override
	public void delete(TreeDocPath<D> path) {
		atomBuffer.delete(path);
	}

	/**
	 * <img src="./doc_files/new_id_algorithm.png" />
	 */
	private TreeDocPath<D> createInsertionIdentifier(
			TreeDocPath<D> leftTreePath,
			TreeDocPath<D> rightTreePath,
			D disambiguator
	) {
		// Should only happen if and only if buffer is empty
		if (leftTreePath == null && rightTreePath == null) {
			return TreeDocPath.create(disambiguator);
		}
		if (leftTreePath == null || rightTreePath == null) {
			return leftTreePath == null
							? rightTreePath.append(false, disambiguator)
							: leftTreePath.append(true, disambiguator);
		}
		assertLeftPositionIsLower(leftTreePath, rightTreePath);
		if (isAncestor(leftTreePath, rightTreePath)) {
			return rightTreePath.append(false, disambiguator);
		}
		return leftTreePath.append(true, disambiguator);
	}

	private void assertLeftPositionIsLower(TreeDocPath<D> leftTreePath, TreeDocPath<D> rightTreePath) {
		if (treeDocPathComparator.compare(leftTreePath, rightTreePath) >= 0) {
			throw new IllegalArgumentException(
							"Atom can be inserted between two positions if and only if left position is strictly lower"
			);
		}
	}

	private boolean isAncestor(TreeDocPath<D> potentialAncestor, TreeDocPath<D> path) {
		if (path.length() < potentialAncestor.length()) {
			return false;
		}
		for (var i = 0; i < potentialAncestor.length(); i++) {
			if (potentialAncestor.isSet(i) != path.isSet(i)) {
				return false;
			}
			if (!Objects.equals(potentialAncestor.disambiguatorAt(i), path.disambiguatorAt(i))) {
				return false;
			}
		}
		return true;
	}

}
