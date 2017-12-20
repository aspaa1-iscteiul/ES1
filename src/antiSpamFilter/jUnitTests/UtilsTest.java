package antiSpamFilter.jUnitTests;

import java.io.File;
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

    private String testFilesPath = "./src/antiSpamFilter/jUnitTests/test_files/";

    /**
     * Test method for
     * {@link antiSpamFilter.utils.Utils#lines(java.lang.String)}.
     */
    @Test
    public void testLines() {
	// Cenário 1 - O ficheiro existe
	Utils.lines(testFilesPath + "testLines.txt");

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
	Utils.configFilesPaths[0] = testFilesPath + "testRules.txt";
	Utils.rules();

	// Cenário 2 - O ficheiro rules.cf encontra-se vazio
	Utils.configFilesPaths[0] = testFilesPath + "testRules2.txt";
	Utils.rules();

	// Cenário 3 - O ficheiro rules.cf está bem formatado mas ainda não
	// contém pesos
	Utils.configFilesPaths[0] = testFilesPath + "testRules3.txt";
	Utils.rules();

	// Cenário 4 - O ficheiro rules.cf não está bem formatado
	Utils.configFilesPaths[0] = testFilesPath + "testRules4.txt";
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
	Utils.configFilesPaths[1] = testFilesPath + "testLog.txt";
	Utils.log(false);

	// Cenário 2 - O ficheiro ham.log está bem configurado
	Utils.configFilesPaths[2] = testFilesPath + "testLog.txt";
	Utils.log(true);

	// Cenário 3 - O caminho para o ficheiro ham.log ou spam.log não está
	// configurado
	Utils.configFilesPaths[1] = "?";
	Utils.log(false);
	Utils.configFilesPaths[2] = "?";
	Utils.log(true);

	// Cenário 4 - O ficheiro ham.log ou spam.log está vazio
	Utils.configFilesPaths[1] = testFilesPath + "testLog2.txt";
	Utils.log(false);
	Utils.configFilesPaths[2] = testFilesPath + "testLog2.txt";
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
	Utils.configFilesPaths[0] = testFilesPath + "testRules.txt";
	Utils.configFilesPaths[1] = testFilesPath + "testLog.txt";
	Utils.configFilesPaths[2] = testFilesPath + "testLog.txt";
	Utils.readConfigFiles();

	// Cenário 2 - Um dos ficheiros não está configurado
	Utils.configFilesPaths[0] = "?";
	Utils.readConfigFiles();
	Utils.configFilesPaths[0] = testFilesPath + "testRules.txt";
	Utils.configFilesPaths[1] = "?";
	Utils.readConfigFiles();
	Utils.configFilesPaths[1] = testFilesPath + "testLog.txt";
	Utils.configFilesPaths[2] = "?";
	Utils.readConfigFiles();
    }

    /**
     * Test method for {@link antiSpamFilter.utils.Utils#saveConfigFilesPaths()}.
     */
    @Test
    public void testSaveConfigFilesPath() {
	// Cenário 1 - Os ficheiros existem
	Utils.saveConfigFilesPaths();

	// Cenário 2 - Entra no Catch
	Utils.configFilesPathsFile = new File("./src");
	Utils.saveConfigFilesPaths();
    }

    /**
     * Test method for {@link antiSpamFilter.utils.Utils#falses()}.
     */
    @Test
    public void testFalses() {
	// Cenário - Ambos os ficheiros spam.log e ham.log estão bem
	// configurados
	Utils.configFilesPaths[0] = testFilesPath + "testFalses_rules.txt";
	Utils.configFilesPaths[1] = testFilesPath + "testFalses_spam.txt";
	Utils.configFilesPaths[2] = testFilesPath + "testFalses_ham.txt";
	Utils.readConfigFiles();
	Utils.falses(false);
	Utils.falses(true);

	Utils.configFilesPaths[0] = testFilesPath + "testFalses_rules.txt";
	Utils.configFilesPaths[1] = testFilesPath + "testFalses_spam.txt";
	Utils.configFilesPaths[2] = "?";
	Utils.readConfigFiles();
	Utils.falses(true);
	Utils.configFilesPaths[0] = testFilesPath + "testFalses_rules.txt";
	Utils.configFilesPaths[1] = "?";
	Utils.configFilesPaths[2] = testFilesPath + "testFalses_ham.txt";
	Utils.readConfigFiles();
	Utils.falses(false);
    }

}