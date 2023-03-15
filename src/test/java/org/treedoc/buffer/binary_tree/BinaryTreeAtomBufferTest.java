package org.treedoc.buffer.binary_tree;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.treedoc.path.TreeDocPath;
import org.treedoc.utils.Pair;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class BinaryTreeAtomBufferTest {
	private final BinaryTreeAtomBuffer<Character, Integer> binaryTreeAtomBuffer = new BinaryTreeAtomBuffer<>();

	private static Object[][] testParameters() {
		return new Object[][]{
						{
										List.of(),
										List.of()
						},
						{
										List.of(
														new Pair<>(TreeDocPath.create(1).append(false, null), 'A')
										),
										List.of('A')
						},
						{
										List.of(
														new Pair<>(TreeDocPath.create(1).append(false, null), 'A'),
														new Pair<>(TreeDocPath.create(1).append(false, null), null)
										),
										List.of()
						},
						{
										List.of(
														new Pair<>(TreeDocPath.create(1).append(false, null), 'A'),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, null)
																		.append(false, null)
																		.append(false, null), null),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, null)
																		.append(false, null)
																		.append(true, null), null),
														new Pair<>(TreeDocPath.create(1).append(false, null), null)
										),
										List.of()
						},
						{
										List.of(
														new Pair<>(TreeDocPath.create(1)
																		.append(false, null)
																		.append(true, 1), 'A')
										),
										List.of('A')
						},
						{
										//
										List.of(
														new Pair<>(TreeDocPath.create(1).append(false, 1), 'B'),
														new Pair<>(TreeDocPath.create(1).append(false, null), 'A'),
														new Pair<>(TreeDocPath.create(1).append(false, 2), 'C')
										),
										List.of('A', 'B', 'C')
						},
						{
										List.of(
														new Pair<>(TreeDocPath.create(1).append(false, 1), 'A'),
														new Pair<>(TreeDocPath.create(1).append(true, null), 'C'),
														new Pair<>(TreeDocPath.create(1).append(false, 2), 'B')
										),
										List.of('A', 'B', 'C')
						},
						{
										List.of(
														new Pair<>(TreeDocPath.create(1).append(false, 1), 'A'),
														new Pair<>(TreeDocPath.create(1).append(false, 1), 'A')
										),
										List.of('A')
						},
						{
										List.of(
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1), 'B'),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1)
																		.append(false, 2), 'A'),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1)
																		.append(true, 3), 'A'),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1)
																		.append(true, 2), 'C')
										),
										List.of('A', 'B', 'C', 'A')
						},
						{
										List.of(
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1), 'B'),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1)
																		.append(true, 3), 'A'),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1)
																		.append(true, 2), 'C'),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1)
																		.append(false, 2), 'A')
										),
										List.of('A', 'B', 'C', 'A')
						},
						{
										List.of(
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1), 'B'),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1)
																		.append(true, 3), 'A'),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1), null),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1)
																		.append(true, 3), null)
										),
										List.of()
						},
						{
										List.of(
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1), 'B'),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1)
																		.append(true, 3), 'A'),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1), null),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1)
																		.append(true, 3), null),
														new Pair<>(TreeDocPath.create(1)
																		.append(false, 1), 'B')
										),
										List.of('B')
						}
		};
	}

	@ParameterizedTest
	@MethodSource("testParameters")
	void expectAtomListAfterSetOfModifications(
					List<Pair<TreeDocPath<Integer>, Character>> modifications,
					List<Character> expectedAtomList
	) {
		for (var modification : modifications) {
			if (modification.second() == null) {
				binaryTreeAtomBuffer.delete(modification.first());
			} else {
				binaryTreeAtomBuffer.insert(modification.first(), modification.second());
			}
		}
		assertThat(binaryTreeAtomBuffer.getEntries().stream().map(Pair::first).anyMatch(Objects::isNull)).isFalse();
		assertThat(binaryTreeAtomBuffer.getEntries().stream().map(Pair::second).collect(Collectors.toList()))
						.isEqualTo(expectedAtomList);
		assertThat(binaryTreeAtomBuffer.isEmpty()).isEqualTo(expectedAtomList.isEmpty());
	}

}
