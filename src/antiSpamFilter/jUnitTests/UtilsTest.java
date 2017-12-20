package antiSpamFilter.jUnitTests;

import java.io.File;
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

    private String testFilesPath = "./src/antiSpamFilter/jUnitTests/test_files/";

    /**
     * Test method for
     * {@link antiSpamFilter.utils.Utils#lines(java.lang.String)}.
     */
    @Test
    public void testLines() {
	// Cen�rio 1 - O ficheiro existe
	Utils.lines(testFilesPath + "testLines.txt");

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
	Utils.configFilesPaths[0] = testFilesPath + "testRules.txt";
	Utils.rules();

	// Cen�rio 2 - O ficheiro rules.cf encontra-se vazio
	Utils.configFilesPaths[0] = testFilesPath + "testRules2.txt";
	Utils.rules();

	// Cen�rio 3 - O ficheiro rules.cf est� bem formatado mas ainda n�o
	// cont�m pesos
	Utils.configFilesPaths[0] = testFilesPath + "testRules3.txt";
	Utils.rules();

	// Cen�rio 4 - O ficheiro rules.cf n�o est� bem formatado
	Utils.configFilesPaths[0] = testFilesPath + "testRules4.txt";
	Utils.rules();

	// Cen�rio 5 - O path para o ficheiro rules.cf n�o est� configurado
	Utils.configFilesPaths[0] = "?";
	Utils.rules();

	// Cen�rio 6 - O path para o ficheiro rules.cf n�o existe
	Utils.configFilesPaths[0] = "./don't_exist";
	Utils.rules();
    }

    /**
     * Test method for {@link antiSpamFilter.utils.Utils#log()}.
     */
    @Test
    public void testLog() {
	// Cen�rio 1 - O ficheiro spam.log est� bem configurado
	Utils.configFilesPaths[1] = testFilesPath + "testLog.txt";
	Utils.log(false);

	// Cen�rio 2 - O ficheiro ham.log est� bem configurado
	Utils.configFilesPaths[2] = testFilesPath + "testLog.txt";
	Utils.log(true);

	// Cen�rio 3 - O caminho para o ficheiro ham.log ou spam.log n�o est�
	// configurado
	Utils.configFilesPaths[1] = "?";
	Utils.log(false);
	Utils.configFilesPaths[2] = "?";
	Utils.log(true);

	// Cen�rio 4 - O ficheiro ham.log ou spam.log est� vazio
	Utils.configFilesPaths[1] = testFilesPath + "testLog2.txt";
	Utils.log(false);
	Utils.configFilesPaths[2] = testFilesPath + "testLog2.txt";
	Utils.log(true);

	// Cen�rio 5 - O ficheiro ham.log ou spam.log n�o existe
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
	// Cen�rio 1 - Os ficheiros est�o configurado
	Utils.configFilesPaths[0] = testFilesPath + "testRules.txt";
	Utils.configFilesPaths[1] = testFilesPath + "testLog.txt";
	Utils.configFilesPaths[2] = testFilesPath + "testLog.txt";
	Utils.readConfigFiles();

	// Cen�rio 2 - Um dos ficheiros n�o est� configurado
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
	// Cen�rio 1 - Os ficheiros existem
	Utils.saveConfigFilesPaths();

	// Cen�rio 2 - Entra no Catch
	Utils.configFilesPathsFile = new File("./src");
	Utils.saveConfigFilesPaths();
    }

    /**
     * Test method for {@link antiSpamFilter.utils.Utils#falses()}.
     */
    @Test
    public void testFalses() {
	// Cen�rio - Ambos os ficheiros spam.log e ham.log est�o bem
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