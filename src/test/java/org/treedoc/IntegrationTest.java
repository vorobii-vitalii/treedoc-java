package org.treedoc;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {
	private static final String USER_1 = "user1";
	private static final String USER_2 = "user2";
	private static final String USER_3 = "user3";

	private final TreeDOC<Character, String> treeDOC = TreeDOC.create();

	@Test
	void test() {

		// Document is initially empty
		expectContent(List.of());

		// User 1 inserted first character
		var aPath = treeDOC.insertBetween(null, null, 'A', USER_1);
		expectContent(List.of('A'));

		// User 2 inserted second character before A
		var bPath = treeDOC.insertBetween(null, aPath, 'B', USER_2);
		expectContent(List.of('B', 'A'));

		// Users 1 and 3 concurrently inserted characters between B and A
		var cPath = treeDOC.insertBetween(bPath, aPath, 'C', USER_3);
		var dPath = treeDOC.insertBetween(bPath, aPath, 'D', USER_1);
		expectContent(List.of('B', 'D', 'C', 'A'));

		// Users 1 and 3 concurrently inserted character between D and C, operations was executed in different order
		var ePath = treeDOC.insertBetween(dPath, cPath, 'E', USER_1);
		var fPath = treeDOC.insertBetween(dPath, cPath, 'F', USER_3);

		// Since disambiguator of User 1 is lower than disambiguator of User 2 character E is inserted before F
		expectContent(List.of('B', 'D', 'E', 'F', 'C', 'A'));

		// User 2 deletes character F while User 3 inserts new character between F and C
		treeDOC.delete(fPath);
		var gPath = treeDOC.insertBetween(fPath, cPath, 'G', USER_2);

		expectContent(List.of('B', 'D', 'E', 'G', 'C', 'A'));
	}

	private void expectContent(List<Character> expectedContent) {
		assertThat(treeDOC.getAtoms()).isEqualTo(expectedContent);
	}

}
