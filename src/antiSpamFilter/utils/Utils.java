package antiSpamFilter.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Classe contendo funções de âmbito geral e atributos invocados por diferentes
 * funções em várias classes
 * 
 * @author Ana Pestana, Diogo Reis, Guilherme Azevedo, Rafael Costa
 *
 */

public class Utils {

	public static String[] configFilesPaths = { "?", "?", "?" };
	public static File configFilesPathsFile = new File("./src/antiSpamFilter/frames/config_files_path.txt");
	public static ArrayList<String[]> hamLogRules = new ArrayList<String[]>(), spamLogRules = new ArrayList<String[]>();
	public static HashMap<String, Double> rulesWeights = new HashMap<String, Double>();

	/**
	 * Retorna o conteúdo, divido por linhas, do ficheiro cujo path é passado
	 * como argumento .
	 * 
	 * @param filePath
	 *            Path do ficheiro cujo conteúdo queremos obter
	 * @return ArrayList com as linhas do ficheiro
	 */
	public static ArrayList<String> lines(String filePath) {
		ArrayList<String> lines = new ArrayList<>();
		try {
			Scanner scn = new Scanner(new File(filePath));
			while (scn.hasNextLine()) {
				String line = scn.nextLine();
				if (!line.equals(""))
					lines.add(line);
			}
			scn.close();
		} catch (FileNotFoundException e) {
			// Não encontrou o ficheiro na diretoria indicada
			JOptionPane.showMessageDialog(new JFrame(),
					"O ficheiro " + filePath + " já não se encontra na diretoria indicada",
					"Configuração dos ficheiros", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return lines;
	}

	/**
	 * Mapeia as regras e pesos (presentes no ficheiro rules.cf configurado) num
	 * HashMap<String, Double>. Retorna 'true' caso o ficheiro rules.cf esteja
	 * configurado e não esteja vazio e 'false' caso contrário.
	 * 
	 * @return correuTudoBem
	 */
	public static boolean rules() {
		if (!configFilesPaths[0].equals("?")) {
			rulesWeights.clear();
			ArrayList<String> lines = lines(configFilesPaths[0]);
			if (lines == null)
				return false;
			for (String line : lines) {
				String[] values = line.split(" ");
				// O ficheiro rules.cf não tem pesos guardados
				if (values.length < 2)
					rulesWeights.put(line, ((Math.random() * 10) - 5));
				// O ficheiro rules.cf tem pesos atribuídos às regras
				else
					try {
						rulesWeights.put(values[0], Double.valueOf(values[1]));
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(new JFrame(), "Ficheiro rules.cf tem um formato inválido",
								"Conteúdo dos ficheiros", JOptionPane.ERROR_MESSAGE);
						configFilesPaths[0] = "?";
						return false;
					}
			}
			if (rulesWeights.isEmpty()) {
				JOptionPane.showMessageDialog(new JFrame(),
						"O ficheiro rules.cf selecionado está vazio. Por favor, reconfigure-o",
						"Conteúdo dos ficheiros", JOptionPane.ERROR_MESSAGE);
				configFilesPaths[0] = "?";
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Mapeia as regras que se verificam em cada mensagem ham/spam numa
	 * ArrayList de Listas de Strings. Retorna 'true' caso o ficheiro
	 * ham/spam.log esteja configurado e não esteja vazio e 'false' caso
	 * contrário.
	 * 
	 * @param hamLog
	 *            Assume o valor 'true' caso se pretenda correr a função para o
	 *            cenário ham e 'false' caso se pretenda correr a função para o
	 *            cenário spam
	 * @return correuTudoBem
	 */
	public static boolean log(boolean hamLog) {
		ArrayList<String[]> tmpList = new ArrayList<String[]>(hamLog ? hamLogRules : spamLogRules);
		if (!configFilesPaths[hamLog ? 2 : 1].equals("?")) {
			tmpList.clear();
			ArrayList<String> lines = lines(configFilesPaths[hamLog ? 2 : 1]);
			if (lines == null)
				return false;
			for (String line : lines) {
				String[] values = line.split("\t");
				tmpList.add(Arrays.copyOfRange(values, 1, values.length));
			}
			if (hamLog)
				hamLogRules = new ArrayList<String[]>(tmpList);
			else
				spamLogRules = new ArrayList<String[]>(tmpList);
			if (tmpList.isEmpty()) {
				JOptionPane.showMessageDialog(new JFrame(),
						"O ficheiro " + (hamLog ? "ham" : "spam")
								+ ".log selecionado está vazio. Por favor, reconfigure-o",
						"Conteúdo dos ficheiros", JOptionPane.ERROR_MESSAGE);
				configFilesPaths[hamLog ? 2 : 1] = "?";
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Executa, por invocação, o mapeamento das regras, mensagens ham e
	 * mensagens spam nas respetivas estruturas de dados. Caso todos tenham
	 * decorrido sem problemas, retorna 'true'. Caso contrário, retorna 'false'.
	 * 
	 * @return correuTudoBem
	 */
	public static boolean readConfigFiles() {
		return rules() && log(true) && log(false);
	}

	/**
	 * Guarda o path dos ficheiros de configuração escolhidos num ficheiro.
	 * Permite que sejam mantidas as configurações para sessões seguintes.
	 */
	public static void saveConfigFilesPath() {
		try {
			FileWriter writer = new FileWriter(configFilesPathsFile, false);
			for (int i = 0; i < configFilesPaths.length; i++) {
				writer.write(configFilesPaths[i]);
				if (i < configFilesPaths.length - 1)
					writer.write("<");
			}
			writer.close();
		} catch (IOException e) {
			JOptionPane
					.showMessageDialog(
							new JFrame(), "Não foi possível prosseguir! O ficheiro "
									+ configFilesPathsFile.getAbsolutePath() + " não pode ser aberto.",
							"Erro Fatal", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Calcula o número de Falsos Positivos/Falsos Negativos no domínio. O
	 * número de FP/FN é calculado percorrendo o ficheiro ham.log/spam.log e
	 * incrementando um contador de cada vez que o somatório os pesos das regras
	 * presentes numa mensagem totalize um valor superior/inferior ao threshold
	 * estabelecido (=5).
	 * 
	 * @param fp
	 *            Assume o valor 'true' caso se pretenda correr a função para o
	 *            cenário Falsos Positivos e 'false' caso se pretenda correr a
	 *            função para o cenário Falsos Negativos
	 * 
	 * @return correuTudoBem
	 */
	public static int falses(boolean fp) {
		if (hamLogRules.isEmpty() || spamLogRules.isEmpty()) {
			JOptionPane.showMessageDialog(new JFrame(),
					"O ficheiro " + (hamLogRules.isEmpty() ? "ham" : "spam")
							+ ".log selecionado está vazio. Por favor, reconfigure-o.",
					"Conteúdo dos ficheiros", JOptionPane.ERROR_MESSAGE);
			configFilesPaths[fp ? 2 : 1] = "?";
			return 0;
		}
		int total = 0;
		ArrayList<String[]> logs = new ArrayList<String[]>(fp ? hamLogRules : spamLogRules);
		for (String[] log : logs) {
			double sum = 0;
			for (String rule : log)
				try {
					sum += rulesWeights.get(rule);
				} catch (NullPointerException e) {
				}
			if ((sum > 5.0 && fp) || (sum < 5.0 && !fp))
				total++;
		}
		return total;
	}

}