package org.treedoc.path;

import java.util.Comparator;

public class TreeDocPathComparator<D extends Comparable<D>> implements Comparator<TreeDocPath<D>> {

	/**
	 * <img src="../doc_files/path_comparison_1.png" />
	 * <img src="../doc_files/path_comparison_2.png" />
	 */
	@Override
	public int compare(TreeDocPath<D> leftPath, TreeDocPath<D> rightPath) {
		var commonLength = Math.min(leftPath.length(), rightPath.length());
		for (var i = 0; i < commonLength; i++) {
			var c = compareAt(i, leftPath, rightPath);
			if (c != 0) {
				return c;
			}
		}
		if (leftPath.length() == rightPath.length()) {
			return 0;
		}
		if (leftPath.length() == commonLength) {
			return rightPath.isSet(commonLength) ? -1 : 1;
		}
		return leftPath.isSet(commonLength) ? 1 : -1;
	}

	private int compareAt(int pos, TreeDocPath<D> leftPath, TreeDocPath<D> rightPath) {
		var lPos = leftPath.isSet(pos);
		var rPos = rightPath.isSet(pos);
		var lDisambiguator = leftPath.disambiguatorAt(pos);
		var rDisambiguator = rightPath.disambiguatorAt(pos);
		if (lDisambiguator == null && rDisambiguator == null) {
			if (lPos != rPos) {
				return lPos ? 1 : -1;
			}
		}
		else if (lDisambiguator != null && rDisambiguator != null) {
			if (lPos != rPos) {
				return lPos ? 1 : -1;
			}
			return lDisambiguator.compareTo(rDisambiguator);
		}
		else if (lDisambiguator != null) {
			return rPos ? -1 : 1;
		}
		else {
			return lPos ? 1 : -1;
		}
		return 0;
	}

}
