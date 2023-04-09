package org.treedoc.path;

/**
 * TreeDOC path data type
 * @param <D> type of disambiguator, Disambiguators must be unique and ordered (3.1)
 */
public interface TreeDocPath<D extends Comparable<D>> {
	static <D extends Comparable<D>> TreeDocPath<D> create(D disambiguator) {
		MutableTreeDocPathImpl<D> treeDocPath = new MutableTreeDocPathImpl<>(1);
		treeDocPath.disambiguatorAt(0, disambiguator);
		return treeDocPath;
	}

	int length();
	boolean isSet(int position);
	D disambiguatorAt(int position);
	TreeDocPath<D> append(boolean bit, D disambiguator);
}
