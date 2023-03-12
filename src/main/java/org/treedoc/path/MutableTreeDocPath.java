package org.treedoc.path;

public interface MutableTreeDocPath<D extends Comparable<D>> extends TreeDocPath<D> {
	void set(int pos);
	void disambiguatorAt(int pos, D disambiguator);
}
