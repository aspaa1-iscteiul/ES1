package antiSpamFilter.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Utils {

	public static String[] config_files_path = { "?", "?", "?" };
	public static File fileConfigs = new File("./src/antiSpamFilter/frames/config_files_path.txt");
	public static ArrayList<List<String>> hamLogRules = new ArrayList<List<String>>(),
			spamLogRules = new ArrayList<List<String>>();
	public static HashMap<String, Double> rules_weights = new HashMap<String, Double>();

	// Garante a utilização do caractere de mudança de linha, independentemente
	// do Sistema Operativo em que a aplicação corre
	public static String newLine = System.getProperty("line.separator");

	/**
	 * Retorna o conteúdo, divido por linhas, do ficheiro cujo path é passado
	 * como argumento .
	 * 
	 * @param file_path
	 *            Path do ficheiro cujo conteúdo queremos obter
	 * @return ArrayList com as linhas do ficheiro
	 */
	public static ArrayList<String> lines(String file_path) {
		ArrayList<String> lines = new ArrayList<>();
		try {
			Scanner scn = new Scanner(new File(file_path));
			while (scn.hasNextLine()) {
				String line = scn.nextLine();
				if (!line.equals(""))
					lines.add(line);
			}
			scn.close();
			// Não encontrou o ficheiro na diretoria indicada
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(new JFrame(),
					"O ficheiro " + file_path + " já não se encontra na diretoria indicada",
					"Configuração dos ficheiros", JOptionPane.WARNING_MESSAGE);
			System.exit(1);
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
		String file_path = config_files_path[0];
		if (!file_path.equals("?")) {
			rules_weights.clear();
			ArrayList<String> list = lines(file_path);
			for (String s : list) {
				String[] ss = s.split(" ");
				// O ficheiro rules.cf não tem pesos guardados
				if (ss.length < 2)
					rules_weights.put(s, ((Math.random() * 10) - 5));
				// O ficheiro rules.cf tem pesos atribuídos às regras
				else
					try {
						rules_weights.put(ss[0], Double.valueOf(ss[1]));
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(new JFrame(), "Ficheiro rules.cf tem um formato inválido",
								"Conteúdo dos ficheiros", JOptionPane.WARNING_MESSAGE);
						System.exit(1);
					}
			}
			if (rules_weights.isEmpty()) {
				JOptionPane.showMessageDialog(new JFrame(),
						"O ficheiro rules.cf selecionados está vazio. Por favor, reconfigure-o",
						"Conteúdo dos ficheiros", JOptionPane.WARNING_MESSAGE);
				config_files_path[0] = "?";
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Mapeia as regras que se verificam em cada mensagem spam numa ArrayList de
	 * Listas de Strings. Retorna 'true' caso o ficheiro spam.log esteja
	 * configurado e não esteja vazio e 'false' caso contrário.
	 * 
	 * @return correuTudoBem
	 */
	public static boolean spamLog() {
		String file_path = config_files_path[1];
		if (!file_path.equals("?")) {
			spamLogRules.clear();
			ArrayList<String> list = lines(file_path);
			for (String s : list) {
				String[] ss = s.split("\t");
				spamLogRules.add(Arrays.asList(Arrays.copyOfRange(ss, 1, ss.length)));
			}
			if (spamLogRules.isEmpty()) {
				JOptionPane.showMessageDialog(new JFrame(),
						"O ficheiro spam.log selecionado está vazio. Por favor, reconfigure-o",
						"Conteúdo dos ficheiros", JOptionPane.WARNING_MESSAGE);
				config_files_path[1] = "?";
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Mapeia as regras que se verificam em cada mensagem ham numa ArrayList de
	 * Listas de Strings. Retorna 'true' caso o ficheiro ham.log esteja
	 * configurado e não esteja vazio e 'false' caso contrário.
	 * 
	 * @return correuTudoBem
	 */
	// TODO O código é praticamente igual à de cima
	public static boolean hamLog() {
		String file_path = config_files_path[2];
		if (!file_path.equals("?")) {
			hamLogRules.clear();
			ArrayList<String> list = lines(file_path);
			for (String s : list) {
				String[] ss = s.split("\t");
				hamLogRules.add(Arrays.asList(Arrays.copyOfRange(ss, 1, ss.length)));
			}
			if (hamLogRules.isEmpty()) {
				JOptionPane.showMessageDialog(new JFrame(),
						"O ficheiro ham.log selecionado está vazio. Por favor, reconfigure-o", "Conteúdo dos ficheiros",
						JOptionPane.WARNING_MESSAGE);
				config_files_path[2] = "?";
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
		return rules() && spamLog() && hamLog();
	}

	/**
	 * Guarda o path dos ficheiros de configuração escolhidos num ficheiro.
	 * Permite que sejam mantidas as configurações para sessões seguintes.
	 */
	public static void saveConfigFilesPath() {
		try {
			FileWriter w = new FileWriter(fileConfigs, false);
			for (int i = 0; i < config_files_path.length; i++) {
				w.write(config_files_path[i]);
				if (i < config_files_path.length - 1)
					w.write("<");
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calcula o número de Falsos Positivos no domínio. O número de FP é
	 * calculado percorrendo o ficheiro ham.log e incrementando um contador de
	 * cada vez que o somatório os pesos das regras presentes numa mensagem
	 * totalize um valor superior ao threshold estabelecido (=5).
	 * 
	 * @return Número de falsos positivos
	 */
	public static int falsePositives() {
		if (hamLogRules.isEmpty()) {
			JOptionPane.showMessageDialog(new JFrame(),
					"O ficheiro ham.log selecionado está vazio. Por favor, reconfigure-o", "Conteúdo dos ficheiros",
					JOptionPane.WARNING_MESSAGE);
			config_files_path[2] = "?";
		}
		int numFP = 0;
		for (List<String> msg : hamLogRules) {
			double sum = 0;
			for (String rule : msg)
				try {
					sum += rules_weights.get(rule);
				} catch (NullPointerException e) {
				}
			if (sum > 5.0)
				numFP++;
		}
		return numFP;
	}

	/**
	 * Calcula o número de Falsos Negativos no domínio. O número de FN é
	 * calculado percorrendo o ficheiro spam.log e incrementando um contador de
	 * cada vez que o somatório dos pesos das regras presentes numa mensagem
	 * totalize um valor inferior ao threshold estabelecido (=5).
	 * 
	 * @return Número de falsos negativos
	 */
	// TODO Praticamente igual à anterior
	public static int falseNegatives() {
		if (spamLogRules.isEmpty()) {
			JOptionPane.showMessageDialog(new JFrame(),
					"O ficheiro spam.log não está configurado, não posso continuar...");
			config_files_path[1] = "?";
		}
		int numFN = 0;
		for (List<String> msg : spamLogRules) {
			double sum = 0;
			for (String rule : msg)
				try {
					sum += rules_weights.get(rule);
				} catch (NullPointerException e) {
				}
			if (sum < 5.0)
				numFN++;
		}
		return numFN;
	}

}