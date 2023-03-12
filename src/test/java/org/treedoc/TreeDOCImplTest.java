package org.treedoc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.treedoc.buffer.AtomBuffer;
import org.treedoc.path.TreeDocPath;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreeDOCImplTest {
	private static final int DISAMBIGUATOR = 1;

	@Mock
	private AtomBuffer<Character, Integer> atomBuffer;

	@InjectMocks
	private TreeDOCImpl<Character, Integer> treeDOC;

	@Test
	void getAtoms() {
		var atoms = List.of('A', 'B', 'C');
		when(atomBuffer.getAtoms()).thenReturn(atoms);
		assertThat(treeDOC.getAtoms()).isEqualTo(atoms);
	}

	@Test
	void insertBetweenGivenLeftIdIsHigherRightId() {
		assertThrows(IllegalArgumentException.class, () ->
						treeDOC.insertBetween(
										TreeDocPath.create(1).append(true, 2),
										TreeDocPath.create(1).append(false, 3),
										'A',
										1
						));
	}

	@Test
	void insertBetweenGivenLeftIdIsEqualRightId() {
		assertThrows(IllegalArgumentException.class, () ->
						treeDOC.insertBetween(
										TreeDocPath.create(1).append(true, 2),
										TreeDocPath.create(1).append(true, 2),
										'A',
										1
						));
	}

	@Test
	void insertBetweenGivenBothIdsNullAndBufferIsEmpty() {
		when(atomBuffer.isEmpty()).thenReturn(true);
		var path = treeDOC.insertBetween(null, null, 'A', DISAMBIGUATOR);
		assertThat(path).isEqualTo(TreeDocPath.create(DISAMBIGUATOR));
		verify(atomBuffer).insert(path, 'A');
	}

	@Test
	void insertBetweenGivenBothIdsNullAndBufferIsNotEmpty() {
		when(atomBuffer.isEmpty()).thenReturn(false);
		assertThrows(IllegalArgumentException.class,
						() -> treeDOC.insertBetween(null, null, 'A', DISAMBIGUATOR));
	}

	@Test
	void insertBetweenGivenLeftIdentifierIsNotSpecified() {
		var path = treeDOC.insertBetween(
						null,
						TreeDocPath.create(1).append(true, 2),
						'G',
						DISAMBIGUATOR
		);
		assertThat(path).isEqualTo(TreeDocPath.create(1).append(true, 2).append(false, DISAMBIGUATOR));
		verify(atomBuffer).insert(path, 'G');
	}

	@Test
	void insertBetweenGivenRightIdentifierIsNotSpecified() {
		var path = treeDOC.insertBetween(
						TreeDocPath.create(1).append(true, 2),
						null,
						'G',
						DISAMBIGUATOR
		);
		assertThat(path).isEqualTo(TreeDocPath.create(1).append(true, 2).append(true, DISAMBIGUATOR));
		verify(atomBuffer).insert(path, 'G');
	}

	@Test
	void insertBetweenGivenLeftIdentifierIsAncestorOfRightIdentifier() {
		var path = treeDOC.insertBetween(
						TreeDocPath.create(1).append(true, 2),
						TreeDocPath.create(1).append(true, 2).append(true, 3),
						'G',
						DISAMBIGUATOR
		);
		assertThat(path).isEqualTo(
					TreeDocPath.create(1)
						.append(true, 2)
						.append(true, 3)
						.append(false, DISAMBIGUATOR));
		verify(atomBuffer).insert(path, 'G');
	}

	@Test
	void insertBetweenGivenLeftIdentifierIsNotAncestorOfRightIdentifier() {
		var path = treeDOC.insertBetween(
						TreeDocPath.create(1).append(true, 2),
						TreeDocPath.create(1).append(true, 3).append(true, 3),
						'G',
						DISAMBIGUATOR
		);
		assertThat(path).isEqualTo(
						TreeDocPath.create(1)
										.append(true, 2)
										.append(true, DISAMBIGUATOR));
		verify(atomBuffer).insert(path, 'G');
	}

}