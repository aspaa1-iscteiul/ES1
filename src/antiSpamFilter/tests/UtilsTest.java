package antiSpamFilter.tests;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.junit.Test;
import antiSpamFilter.utils.Utils;

/**
 * Classe de testes unit�rios da classe Utils.java com cobertura acima de 75%
 * para todas as m�tricas.
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
		// Cen�rio 1 - O ficheiro existe
		Utils.lines("./src/antiSpamFilter/tests/testLines.txt");

		// Cen�rio 2 - O ficheiro indicado n�o existe
		Utils.lines("./don't_exist");
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#rules()}.
	 */
	@Test
	public void testRules() {
		// Cen�rio 1 - O ficheiro rules.cf est� bem formatado e j� cont�m os
		// pesos
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules.txt";
		Utils.rules();

		// Cen�rio 2 - O ficheiro rules.cf encontra-se vazio
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules2.txt";
		Utils.rules();

		// Cen�rio 3 - O ficheiro rules.cf est� bem formatado mas ainda n�o
		// cont�m pesos
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules3.txt";
		Utils.rules();

		// Cen�rio 4 - O ficheiro rules.cf n�o est� bem formatado
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules4.txt";
		Utils.rules();

		// Cen�rio 5 - O path para o ficheiro rules.cf n�o est� configurado
		Utils.config_files_path[0] = "?";
		Utils.rules();

		// Cen�rio 6 - O path para o ficheiro rules.cf n�o existe
		Utils.config_files_path[0] = "./don't_exist";
		Utils.rules();
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#log()}.
	 */
	@Test
	public void testLog() {
		// Cen�rio 1 - O ficheiro spam.log est� bem configurado
		Utils.config_files_path[1] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.log(false);

		// Cen�rio 2 - O ficheiro ham.log est� bem configurado
		Utils.config_files_path[2] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.log(true);

		// Cen�rio 3 - O caminho para o ficheiro ham.log ou spam.log n�o est�
		// configurado
		Utils.config_files_path[1] = "?";
		Utils.log(false);
		Utils.config_files_path[2] = "?";
		Utils.log(true);

		// Cen�rio 4 - O ficheiro ham.log ou spam.log est� vazio
		Utils.config_files_path[1] = "./src/antiSpamFilter/tests/file_tests/testLog2.txt";
		Utils.log(false);
		Utils.config_files_path[2] = "./src/antiSpamFilter/tests/file_tests/testLog2.txt";
		Utils.log(true);

		// Cen�rio 5 - O ficheiro ham.log ou spam.log n�o existe
		Utils.config_files_path[1] = "./don't_exist";
		Utils.log(false);
		Utils.config_files_path[2] = "./don't_exist";
		Utils.log(true);
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#readConfigFiles()}.
	 */
	@Test
	public void testReadConfigFiles() {
		// Cen�rio 1 - Os ficheiros est�o configurado
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules.txt";
		Utils.config_files_path[1] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.config_files_path[2] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.readConfigFiles();

		// Cen�rio 2 - Um dos ficheiros n�o est� configurado
		Utils.config_files_path[0] = "?";
		Utils.readConfigFiles();
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules.txt";
		Utils.config_files_path[1] = "?";
		Utils.readConfigFiles();
		Utils.config_files_path[1] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.config_files_path[2] = "?";
		Utils.readConfigFiles();
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#saveConfigFilesPath()}.
	 */
	@Test
	public void testSaveConfigFilesPath() {
		// Cen�rio 1 - Os ficheiros existem
		Utils.saveConfigFilesPath();

		// Cen�rio 2 - Entra no Catch
		Utils.fileConfigs = new File("./src");
		Utils.saveConfigFilesPath();
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#falses()}.
	 */
	@Test
	public void testFalses() {
		// Cen�rio - Ambos os ficheiros spam.log e ham.log est�o bem
		// configurados
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testFalses_rules.txt";
		Utils.config_files_path[1] = "./src/antiSpamFilter/tests/file_tests/testFalses_spam.txt";
		Utils.config_files_path[2] = "./src/antiSpamFilter/tests/file_tests/testFalses_ham.txt";
		Utils.readConfigFiles();
		Utils.falses(false);
		Utils.falses(true);

		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testFalses_rules.txt";
		Utils.config_files_path[1] = "./src/antiSpamFilter/tests/file_tests/testFalses_spam.txt";
		Utils.config_files_path[2] = "?";
		Utils.readConfigFiles();
		Utils.falses(true);
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testFalses_rules.txt";
		Utils.config_files_path[1] = "?";
		Utils.config_files_path[2] = "./src/antiSpamFilter/tests/file_tests/testFalses_ham.txt";
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

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#frameAtCenter()}.
	 */
	@Test
	public void testFrameAtCenter() {
		Utils.frameAtCenter(new JFrame());
	}

}
