package org.treedoc.path;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MutableTreeDocPathImpl<D extends Comparable<D>> implements MutableTreeDocPath<D> {
	private final BitSet bitSet;
	private final Map<Integer, D> disambiguatorByIndex = new HashMap<>();
	private final int n;

	public MutableTreeDocPathImpl(int n) {
		this.n = n;
		this.bitSet = new BitSet(n);
	}

	@Override
	public int length() {
		return n;
	}

	@Override
	public boolean isSet(int position) {
		return bitSet.get(position);
	}

	@Override
	public D disambiguatorAt(int position) {
		return disambiguatorByIndex.get(position);
	}

	@Override
	public TreeDocPath<D> append(boolean bit, D disambiguator) {
		MutableTreeDocPathImpl<D> treeDocPath = new MutableTreeDocPathImpl<>(n + 1);
		for (var i = 0; i < n; i++) {
			if (bitSet.get(i)) {
				treeDocPath.set(i);
			}
			treeDocPath.disambiguatorAt(i, disambiguatorByIndex.get(i));
		}
		if (bit) {
			treeDocPath.set(n);
		}
		treeDocPath.disambiguatorAt(n, disambiguator);
		return treeDocPath;
	}

	@Override
	public void set(int pos) {
		bitSet.set(pos);
	}

	@Override
	public void disambiguatorAt(int pos, D disambiguator) {
		disambiguatorByIndex.put(pos, disambiguator);
	}

	@Override
	public String toString() {
		return IntStream.range(0, n)
						.mapToObj(i -> "(" + bitSet.get(i) + ", " + disambiguatorAt(i) + ")")
						.collect(Collectors.joining(" -> "));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TreeDocPath<?> otherPath)) return false;
		if (otherPath.length() != length()) {
			return false;
		}
		for (var i = 0; i < n; i++) {
			if (otherPath.isSet(i) != isSet(i) || !Objects.equals(otherPath.disambiguatorAt(i), disambiguatorAt(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(bitSet, disambiguatorByIndex, n);
	}
}
