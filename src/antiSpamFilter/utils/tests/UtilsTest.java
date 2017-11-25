package antiSpamFilter.utils.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import antiSpamFilter.utils.Utils;

public class UtilsTest {

	/**
	 * Test method for
	 * {@link antiSpamFilter.utils.Utils#lines(java.lang.String)}.
	 */
	@Test
	public void testLines() {
		Utils.lines("./src/antiSpamFilter/utils/tests/testLines.txt");
		Utils.lines("./nada");
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#rules()}.
	 */
	@Test
	public void testRules() {
		Utils.config_files_path[0] = "?";
		Utils.rules();

		Utils.config_files_path[0] = "./nada";
		Utils.rules();
		
		Utils.config_files_path[0] = "./src/antiSpamFilter/utils/tests/testRules.txt";
		Utils.rules();

		Utils.config_files_path[0] = "./src/antiSpamFilter/utils/tests/testRules2.txt";
		Utils.rules();

		Utils.config_files_path[0] = "./src/antiSpamFilter/utils/tests/testRules3.txt";
		Utils.rules();
		
		Utils.config_files_path[0] = "./src/antiSpamFilter/utils/tests/testRules4.txt";
		Utils.rules();
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#spamLog()}.
	 */
	@Test
	public void testSpamLog() {
		ArrayList<String[]> all = new ArrayList<String[]>();
		String[] var = new String[2];
		var[0] = "ola";
		var[1] = "adeus";
		all.add(var);

		var = new String[3];
		var[0] = "então";
		var[1] = "tudo";
		var[2] = "fixe";
		all.add(var);

		var = new String[4];
		var[0] = "como";
		var[1] = "vai";
		var[2] = "o";
		var[3] = "trabalho";
		all.add(var);

		var = new String[1];
		var[0] = "acabou";
		all.add(var);

		var = new String[6];
		var[0] = "não";
		var[1] = "há";
		var[2] = "mais";
		var[3] = "nada";
		var[4] = "a";
		var[5] = "fazer";
		all.add(var);

		var = new String[1];
		var[0] = "ADEUS";
		all.add(var);

		Utils.config_files_path[1] = "./src/antiSpamFilter/utils/tests/testSpamLog.txt";
		Utils.log(true);

		for (int i = 0; i < all.size(); i++) {
			for (int j = 0; j < all.get(i).length; j++) {
				assertEquals(Utils.spam.get(i)[j], all.get(i)[j], "ok");
			}
		}

		// var = new String[1];
		// var[0] = ("nothing");
		// all.add(var);
		// assert (!Utils.spam.equals(all));
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#hamLog()}.
	 */
	@Test
	public void testHamLog() {
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#readConfigFiles()}.
	 */
	@Test
	public void testReadConfigFiles() {
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#saveConfigFilesPath()}.
	 */
	@Test
	public void testSaveConfigFilesPath() {
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#falsePositives()}.
	 */
	@Test
	public void testFalsePositives() {
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#falseNegatives()}.
	 */
	@Test
	public void testFalseNegatives() {
	}

}
