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

public class Utils {

	public static String[] config_files_path = { "?", "?", "?" };
	public static File fileConfigs = new File("./src/antiSpamFilter/frames/config_files_path.txt");
	public static ArrayList<String[]> hamLogRules = new ArrayList<String[]>(), spamLogRules = new ArrayList<String[]>();
	public static HashMap<String, Double> rules_weights = new HashMap<String, Double>();

	// Garante a utiliza��o do caractere de mudan�a de linha, independentemente
	// do Sistema Operativo em que a aplica��o corre
	public static String newLine = System.getProperty("line.separator");

	/**
	 * Retorna o conte�do, divido por linhas, do ficheiro cujo path � passado
	 * como argumento .
	 * 
	 * @param file_path
	 *            Path do ficheiro cujo conte�do queremos obter
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
			// N�o encontrou o ficheiro na diretoria indicada
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(new JFrame(),
					"O ficheiro " + file_path + " j� n�o se encontra na diretoria indicada",
					"Configura��o dos ficheiros", JOptionPane.WARNING_MESSAGE);
			return null;
		}
		return lines;
	}

	/**
	 * Mapeia as regras e pesos (presentes no ficheiro rules.cf configurado) num
	 * HashMap<String, Double>. Retorna 'true' caso o ficheiro rules.cf esteja
	 * configurado e n�o esteja vazio e 'false' caso contr�rio.
	 * 
	 * @return correuTudoBem
	 */
	public static boolean rules() {
		String file_path = config_files_path[0];
		if (!file_path.equals("?")) {
			rules_weights.clear();
			ArrayList<String> list = lines(file_path);
			if (list == null)
				return false;
			for (String s : list) {
				String[] ss = s.split(" ");
				// O ficheiro rules.cf n�o tem pesos guardados
				if (ss.length < 2)
					rules_weights.put(s, ((Math.random() * 10) - 5));
				// O ficheiro rules.cf tem pesos atribu�dos �s regras
				else
					try {
						rules_weights.put(ss[0], Double.valueOf(ss[1]));
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(new JFrame(), "Ficheiro rules.cf tem um formato inv�lido",
								"Conte�do dos ficheiros", JOptionPane.WARNING_MESSAGE);
						config_files_path[0] = "?";
						return false;
					}
			}
			if (rules_weights.isEmpty()) {
				JOptionPane.showMessageDialog(new JFrame(),
						"O ficheiro rules.cf selecionados est� vazio. Por favor, reconfigure-o",
						"Conte�do dos ficheiros", JOptionPane.WARNING_MESSAGE);
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
	 * configurado e n�o esteja vazio e 'false' caso contr�rio.
	 * 
	 * @return correuTudoBem
	 */
	public static boolean log(boolean b) {
		String file_path = config_files_path[b ? 2 : 1];
		ArrayList<String[]> var = new ArrayList<String[]>(b ? hamLogRules : spamLogRules);
		if (!file_path.equals("?")) {
			var.clear();
			ArrayList<String> list = lines(file_path);
			if (list == null)
				return false;
			for (String s : list) {
				String[] ss = s.split("\t");
				var.add(Arrays.copyOfRange(ss, 1, ss.length));
			}
			if (b)
				hamLogRules = new ArrayList<String[]>(var);
			else
				spamLogRules = new ArrayList<String[]>(var);
			if (var.isEmpty()) {
				JOptionPane.showMessageDialog(new JFrame(),
						"O ficheiro " + (b ? "ham" : "spam") + ".log selecionado est� vazio. Por favor, reconfigure-o",
						"Conte�do dos ficheiros", JOptionPane.WARNING_MESSAGE);
				config_files_path[b ? 2 : 1] = "?";
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Executa, por invoca��o, o mapeamento das regras, mensagens ham e
	 * mensagens spam nas respetivas estruturas de dados. Caso todos tenham
	 * decorrido sem problemas, retorna 'true'. Caso contr�rio, retorna 'false'.
	 * 
	 * @return correuTudoBem
	 */
	public static boolean readConfigFiles() {
		return rules() && log(true) && log(false);
	}

	/**
	 * Guarda o path dos ficheiros de configura��o escolhidos num ficheiro.
	 * Permite que sejam mantidas as configura��es para sess�es seguintes.
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
			JOptionPane.showMessageDialog(new JFrame(), "N�o foi poss�vel prosseguir! O ficheiro "
					+ fileConfigs.getAbsolutePath() + " n�o pode ser aberto.", "Erro Fatal",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * Calcula o n�mero de Falsos Positivos no dom�nio. O n�mero de FP �
	 * calculado percorrendo o ficheiro ham.log e incrementando um contador de
	 * cada vez que o somat�rio os pesos das regras presentes numa mensagem
	 * totalize um valor superior ao threshold estabelecido (=5).
	 * 
	 * @return N�mero de falsos positivos
	 */
	public static int falses(boolean b) {
		if (hamLogRules.isEmpty() || spamLogRules.isEmpty()) {
			JOptionPane.showMessageDialog(new JFrame(), "O ficheiro " + (hamLogRules.isEmpty() ? "ham" : "spam")
					+ ".log n�o est� configurado, n�o posso continuar...");
			config_files_path[b ? 2 : 1] = "?";
			return 0;
		}
		int total = 0;
		ArrayList<String[]> v = new ArrayList<String[]>(b ? hamLogRules : spamLogRules);
		for (String[] msg : v) {
			double sum = 0;
			for (String rule : msg)
				try {
					sum += rules_weights.get(rule);
				} catch (NullPointerException e) {
				}
			if ((sum > 5.0 && b) || (sum < 5.0 && !b))
				total++;
		}
		return total;
	}

}