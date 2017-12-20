package antiSpamFilter.jUnitTests;

import java.io.File;
import java.util.ArrayList;
import org.junit.Test;

import antiSpamFilter.utils.Utils;

/**
 * Classe de testes unitários da classe Utils.java com cobertura acima de 75%
 * para todas as métricas.
 * 
 * @author Ana Pestana, Diogo Reis, Guilherme Azevedo, Rafael Costa
 *
 */
public class UtilsTest {

	/**
	 * Test method for
	 * {@link antiSpamFilter.utils.Utils#lines(java.lang.String)}.
	 */
	@Test
	public void testLines() {
		// Cenário 1 - O ficheiro existe
		Utils.lines("./src/antiSpamFilter/tests/testLines.txt");

		// Cenário 2 - O ficheiro indicado não existe
		Utils.lines("./don't_exist");
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#rules()}.
	 */
	@Test
	public void testRules() {
		// Cenário 1 - O ficheiro rules.cf está bem formatado e já contém os
		// pesos
		Utils.configFilesPaths[0] = "./src/antiSpamFilter/tests/file_tests/testRules.txt";
		Utils.rules();

		// Cenário 2 - O ficheiro rules.cf encontra-se vazio
		Utils.configFilesPaths[0] = "./src/antiSpamFilter/tests/file_tests/testRules2.txt";
		Utils.rules();

		// Cenário 3 - O ficheiro rules.cf está bem formatado mas ainda não
		// contém pesos
		Utils.configFilesPaths[0] = "./src/antiSpamFilter/tests/file_tests/testRules3.txt";
		Utils.rules();

		// Cenário 4 - O ficheiro rules.cf não está bem formatado
		Utils.configFilesPaths[0] = "./src/antiSpamFilter/tests/file_tests/testRules4.txt";
		Utils.rules();

		// Cenário 5 - O path para o ficheiro rules.cf não está configurado
		Utils.configFilesPaths[0] = "?";
		Utils.rules();

		// Cenário 6 - O path para o ficheiro rules.cf não existe
		Utils.configFilesPaths[0] = "./don't_exist";
		Utils.rules();
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#log()}.
	 */
	@Test
	public void testLog() {
		// Cenário 1 - O ficheiro spam.log está bem configurado
		Utils.configFilesPaths[1] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.log(false);

		// Cenário 2 - O ficheiro ham.log está bem configurado
		Utils.configFilesPaths[2] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.log(true);

		// Cenário 3 - O caminho para o ficheiro ham.log ou spam.log não está
		// configurado
		Utils.configFilesPaths[1] = "?";
		Utils.log(false);
		Utils.configFilesPaths[2] = "?";
		Utils.log(true);

		// Cenário 4 - O ficheiro ham.log ou spam.log está vazio
		Utils.configFilesPaths[1] = "./src/antiSpamFilter/tests/file_tests/testLog2.txt";
		Utils.log(false);
		Utils.configFilesPaths[2] = "./src/antiSpamFilter/tests/file_tests/testLog2.txt";
		Utils.log(true);

		// Cenário 5 - O ficheiro ham.log ou spam.log não existe
		Utils.configFilesPaths[1] = "./don't_exist";
		Utils.log(false);
		Utils.configFilesPaths[2] = "./don't_exist";
		Utils.log(true);
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#readConfigFiles()}.
	 */
	@Test
	public void testReadConfigFiles() {
		// Cenário 1 - Os ficheiros estão configurado
		Utils.configFilesPaths[0] = "./src/antiSpamFilter/tests/file_tests/testRules.txt";
		Utils.configFilesPaths[1] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.configFilesPaths[2] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.readConfigFiles();

		// Cenário 2 - Um dos ficheiros não está configurado
		Utils.configFilesPaths[0] = "?";
		Utils.readConfigFiles();
		Utils.configFilesPaths[0] = "./src/antiSpamFilter/tests/file_tests/testRules.txt";
		Utils.configFilesPaths[1] = "?";
		Utils.readConfigFiles();
		Utils.configFilesPaths[1] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.configFilesPaths[2] = "?";
		Utils.readConfigFiles();
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#saveConfigFilesPath()}.
	 */
	@Test
	public void testSaveConfigFilesPath() {
		// Cenário 1 - Os ficheiros existem
		Utils.saveConfigFilesPath();

		// Cenário 2 - Entra no Catch
		Utils.configFilesPathsFile = new File("./src");
		Utils.saveConfigFilesPath();
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#falses()}.
	 */
	@Test
	public void testFalses() {
		// Cenário - Ambos os ficheiros spam.log e ham.log estão bem
		// configurados
		Utils.configFilesPaths[0] = "./src/antiSpamFilter/tests/file_tests/testFalses_rules.txt";
		Utils.configFilesPaths[1] = "./src/antiSpamFilter/tests/file_tests/testFalses_spam.txt";
		Utils.configFilesPaths[2] = "./src/antiSpamFilter/tests/file_tests/testFalses_ham.txt";
		Utils.readConfigFiles();
		Utils.falses(false);
		Utils.falses(true);

		Utils.configFilesPaths[0] = "./src/antiSpamFilter/tests/file_tests/testFalses_rules.txt";
		Utils.configFilesPaths[1] = "./src/antiSpamFilter/tests/file_tests/testFalses_spam.txt";
		Utils.configFilesPaths[2] = "?";
		Utils.readConfigFiles();
		Utils.falses(true);
		Utils.configFilesPaths[0] = "./src/antiSpamFilter/tests/file_tests/testFalses_rules.txt";
		Utils.configFilesPaths[1] = "?";
		Utils.configFilesPaths[2] = "./src/antiSpamFilter/tests/file_tests/testFalses_ham.txt";
		Utils.readConfigFiles();
		Utils.falses(false);
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#listsToMap()}.
	 */
	@Test
	public void testListsToMap() {
		ArrayList<String> var1 = new ArrayList<>();
		ArrayList<Integer> var2 = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			var1.add("" + i);
			var2.add(i);
		}
		Utils.listsToMap(var1, var2);
	}

}