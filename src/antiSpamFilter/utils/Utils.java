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
	public static ArrayList<String[]> ham = new ArrayList<String[]>(), spam = new ArrayList<String[]>();
	public static HashMap<String, Double> rules = new HashMap<String, Double>();

	public static String newLine = System.getProperty("line.separator");

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
		} catch (FileNotFoundException e) { // if is not a file
			JOptionPane.showMessageDialog(new JFrame(),
					"O ficheiro " + file_path + " já não se encontra na diretoria indicada",
					"Configuração dos ficheiros", JOptionPane.WARNING_MESSAGE);
			return null;
		}
		return lines;
	}

	public static boolean rules() {
		String file_path = config_files_path[0];
		if (!file_path.equals("?")) {
			rules.clear();
			ArrayList<String> list = lines(file_path);
			if (list == null)
				return false;
			for (String s : list) {
				String[] ss = s.split(" ");
				if (ss.length < 2)
					rules.put(s, ((Math.random() * 10) - 5));
				else
					try {
						rules.put(ss[0], Double.valueOf(ss[1]));
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(new JFrame(), "Ficheiro rules.cf tem um formato inválido",
								"Conteúdo dos ficheiros", JOptionPane.WARNING_MESSAGE);
						config_files_path[0] = "?";
						return false;
					}
			}
			if (rules.isEmpty()) {
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

	public static boolean log(boolean b) {
		String file_path = config_files_path[b ? 1 : 2];
		ArrayList<String[]> var = new ArrayList<String[]>(b ? spam : ham);
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
				spam = new ArrayList<String[]>(var);
			else
				ham = new ArrayList<String[]>(var);
			if (var.isEmpty()) {
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

	public static boolean readConfigFiles() {
		return rules() && log(true) && log(false);
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
	 * 
	 * @param b
	 *            true se for falsos Positivos
	 * @return
	 */
	public static int falses(boolean b) {
		if (ham.isEmpty() || spam.isEmpty()) {
			String var = ham.isEmpty() ? "ham" : "spam";
			JOptionPane.showMessageDialog(new JFrame(),
					"O ficheiro " + var + ".log não está configurado, não posso continuar...");
			config_files_path[2] = "?";
		}
		int total = 0;
		ArrayList<String[]> v = new ArrayList<String[]>(b ? spam : ham);
		for (String[] var : v) {
			double sum = 0;
			for (String key : var)
				try {
					sum += rules.get(key);
				} catch (NullPointerException e) {
				}
			if ((sum > 5.0 && b) || (sum < 5.0 && !b))
				total++;
		}
		return total;
	}

}