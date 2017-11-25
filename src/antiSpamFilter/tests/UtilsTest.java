package antiSpamFilter.tests;

import static org.junit.Assert.*;

import java.io.File;
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
		/*
		 * quando o ficheiro existe
		 */
		Utils.lines("./src/antiSpamFilter/tests/testLines.txt");
		
		/*
		 * quando o ficheiro indicado não existe
		 */
		Utils.lines("./don't_exist");
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#rules()}.
	 */
	@Test
	public void testRules() {
		/*
		 * quando o ficheiro de rules está bem formatado e já contem os pesos
		 */
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules.txt";
		Utils.rules();

		/*
		 * quando o ficheiro de rules está vazio
		 */
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules2.txt";
		Utils.rules();

		/*
		 * quando o ficheiro de rules está bem formatado mas não contem os pesos
		 */
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules3.txt";
		Utils.rules();

		/*
		 * quando o ficheiro de rules não está bem formatado
		 */
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules4.txt";
		Utils.rules();

		/*
		 * quando o caminho para o ficheiro rules não está configurado
		 */
		Utils.config_files_path[0] = "?";
		Utils.rules();

		/*
		 * teste para quando o caminho para o ficheiro de rules não existe
		 */
		Utils.config_files_path[0] = "./don't_exist";
		Utils.rules();
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#log()}.
	 */
	@Test
	public void testLog() {
		/*
		 * quando o ficheiro spam.log está bem configurado
		 */
		Utils.config_files_path[1] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.log(false);

		/*
		 * quando o ficheiro ham.log está bem configurado
		 */
		Utils.config_files_path[2] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.log(true);

		/*
		 * quando quando o caminho para o ficheiro ham.log ou spam.log não está
		 * configurado
		 */
		Utils.config_files_path[1] = "?";
		Utils.log(false);
		Utils.config_files_path[2] = "?";
		Utils.log(true);

		/*
		 * quando o ficheiro de ham.log ou spam.log está vazio
		 */
		Utils.config_files_path[1] = "./src/antiSpamFilter/tests/file_tests/testLog2.txt";
		Utils.log(false);
		Utils.config_files_path[2] = "./src/antiSpamFilter/tests/file_tests/testLog2.txt";
		Utils.log(true);

		/*
		 * quando o ficheiro de ham.log ou spam.log não existe
		 */
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
		/*
		 * quando os ficheiros estão configurados
		 */
		Utils.config_files_path[0] = "./src/antiSpamFilter/tests/file_tests/testRules.txt";
		Utils.config_files_path[1] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.config_files_path[2] = "./src/antiSpamFilter/tests/file_tests/testLog.txt";
		Utils.readConfigFiles();
		
		/*
		 * quando um dos ficheiros não está configurados
		 */
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
		/*
		 * quando os ficheiros existem
		 */
		Utils.saveConfigFilesPath();
		
		/*
		 * quando vai para o catch
		 */
		Utils.fileConfigs = new File("./src");
		Utils.saveConfigFilesPath();
	}

	/**
	 * Test method for {@link antiSpamFilter.utils.Utils#falses()}.
	 */
	@Test
	public void testFalses() {
		/*
		 * quando o ficheiro spam.log e ham.log está bem configurado
		 */
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

}
