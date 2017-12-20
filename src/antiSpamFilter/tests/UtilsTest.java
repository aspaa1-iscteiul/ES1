package antiSpamFilter.tests;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;
import org.junit.Test;

import antiSpamFilter.frames.Afinacao;
import antiSpamFilter.frames.HomePage;
import antiSpamFilter.utils.GuiUtils;
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
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules.txt";
		Utils.rules();

		// Cenário 2 - O ficheiro rules.cf encontra-se vazio
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules2.txt";
		Utils.rules();

		// Cenário 3 - O ficheiro rules.cf está bem formatado mas ainda não
		// contém pesos
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules3.txt";
		Utils.rules();

		// Cenário 4 - O ficheiro rules.cf não está bem formatado
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules4.txt";
		Utils.rules();

		// Cenário 5 - O path para o ficheiro rules.cf não está configurado
		Utils.config_files_path[0] = "?";
		Utils.rules();

		// Cenário 6 - O path para o ficheiro rules.cf não existe
		Utils.config_files_path[0] = "./don't_exist";
		Utils.rules();
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#log()}.
	 */
	@Test
	public void testLog() {
		// Cenário 1 - O ficheiro spam.log está bem configurado
		Utils.config_files_path[1] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.log(false);

		// Cenário 2 - O ficheiro ham.log está bem configurado
		Utils.config_files_path[2] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.log(true);

		// Cenário 3 - O caminho para o ficheiro ham.log ou spam.log não está
		// configurado
		Utils.config_files_path[1] = "?";
		Utils.log(false);
		Utils.config_files_path[2] = "?";
		Utils.log(true);

		// Cenário 4 - O ficheiro ham.log ou spam.log está vazio
		Utils.config_files_path[1] = "./src/antiSpamFilter/tests/file_tests/testLog2.txt";
		Utils.log(false);
		Utils.config_files_path[2] = "./src/antiSpamFilter/tests/file_tests/testLog2.txt";
		Utils.log(true);

		// Cenário 5 - O ficheiro ham.log ou spam.log não existe
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
		// Cenário 1 - Os ficheiros estão configurado
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules.txt";
		Utils.config_files_path[1] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.config_files_path[2] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.readConfigFiles();

		// Cenário 2 - Um dos ficheiros não está configurado
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
		// Cenário 1 - Os ficheiros existem
		Utils.saveConfigFilesPath();

		// Cenário 2 - Entra no Catch
		Utils.fileConfigs = new File("./src");
		Utils.saveConfigFilesPath();
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#falses()}.
	 */
	@Test
	public void testFalses() {
		// Cenário - Ambos os ficheiros spam.log e ham.log estão bem
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
	 * Test method for {@link antiSpamFilter.utils.Utils#frameAtCenter()}.
	 */
	@Test
	public void testFrameAtCenter() {
		GuiUtils.frameAtCenter(new JFrame());
	}
}
