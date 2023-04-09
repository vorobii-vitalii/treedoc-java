package org.treedoc.path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class MutableTreeDocPathImplTest {
	private static final int N = 100;
	private static final int DISAMBIGUATOR = 12345;

	private final MutableTreeDocPathImpl<Integer> mutableTreeDocPath = new MutableTreeDocPathImpl<>(N);

	@Test
	void length() {
		assertThat(mutableTreeDocPath.length()).isEqualTo(N);
	}

	@ValueSource(ints = {0, 1, 2, 3, 4, 5, 89})
	@ParameterizedTest
	void isSet(int index) {
		mutableTreeDocPath.set(index);
		for (var i = 0; i < N; i++) {
			assertThat(mutableTreeDocPath.isSet(i)).isEqualTo(i == index);
		}
	}

	@ValueSource(ints = {0, 1, 2, 3, 4, 5, 89})
	@ParameterizedTest
	void disambiguatorAt(int index) {
		mutableTreeDocPath.disambiguatorAt(index, DISAMBIGUATOR);
		for (var i = 0; i < N; i++) {
			assertThat(mutableTreeDocPath.disambiguatorAt(i)).isEqualTo(i == index ? DISAMBIGUATOR : null);
		}
	}

	@Test
	void append() {
		mutableTreeDocPath.set(1);
		mutableTreeDocPath.set(3);
		mutableTreeDocPath.set(5);
		mutableTreeDocPath.disambiguatorAt(2, DISAMBIGUATOR);

		var newTreeDocPath = mutableTreeDocPath.append(true, 99);
		assertThat(newTreeDocPath.length()).isEqualTo(N + 1);
		for (int i = 0; i < N; i++) {
			assertThat(mutableTreeDocPath.isSet(i)).isEqualTo(newTreeDocPath.isSet(i));
			assertThat(mutableTreeDocPath.disambiguatorAt(i)).isEqualTo(newTreeDocPath.disambiguatorAt(i));
		}
		assertThat(newTreeDocPath.disambiguatorAt(N)).isEqualTo(99);
		assertThat(newTreeDocPath.isSet(N)).isEqualTo(true);
	}
}