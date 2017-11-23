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
	public static File configs = new File("./src/antiSpamFilter/frames/config_files_path.txt");
	public static ArrayList<List<String>> ham = new ArrayList<List<String>>(), spam = new ArrayList<List<String>>();
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
					"O ficheiro " + file_path + " já não está na diretoria indicada");
			System.exit(1);
		}
		return lines;
	}

	public static void rules() {
		String file_path = config_files_path[0];
		if (rules.isEmpty() && !file_path.equals("?")) {
			ArrayList<String> list = lines(file_path);
			for (String s : list) {
				String[] ss = s.split(" ");
				if (ss.length < 2)
					rules.put(s, ((Math.random() * 10) - 5));
				else
					try {
						rules.put(ss[0], Double.valueOf(ss[1]));
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(new JFrame(), "Ficheiro rules.cf tem um formato inválido");
						System.exit(1);
					}
			}
		}
	}

	public static void spamLog() {
		String file_path = config_files_path[1];
		if (spam.isEmpty() && !file_path.equals("?")) {
			ArrayList<String> list = lines(file_path);
			for (String s : list) {
				String[] ss = s.split("\t");
				spam.add(Arrays.asList(Arrays.copyOfRange(ss, 1, ss.length)));
			}
		}
	}

	public static void hamLog() {
		String file_path = config_files_path[2];
		if (ham.isEmpty() && !file_path.equals("?")) {
			ArrayList<String> list = lines(file_path);
			for (String s : list) {
				String[] ss = s.split("\t");
				ham.add(Arrays.asList(Arrays.copyOfRange(ss, 1, ss.length)));
			}
		}
	}

	public static void readConfigFiles() {
		rules();
		spamLog();
		hamLog();
	}

	/**
	 * Guarda o path dos ficheiros de configuração escolhidos num ficheiro.
	 * Permite que sejam mantidas as configurações para sessões seguintes.
	 */
	public static void saveConfigFilesPath() {
		try {
			FileWriter w = new FileWriter(configs, false);
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

	public static int falsePositives() {
		if (ham.isEmpty()) {
			JOptionPane.showMessageDialog(new JFrame(),
					"O ficheiro ham.log não está configurado, não posso continuar...");
			System.exit(1);
		}
		int total = 0;
		for (List<String> var : ham) {
			double sum = 0;
			for (String key : var)
				try {
					sum += rules.get(key);
				} catch (NullPointerException e) {
				}
			if (sum < 5.0)
				total++;
		}
		return total;
	}

	public static int falseNegatives() {
		if (spam.isEmpty()) {
			JOptionPane.showMessageDialog(new JFrame(),
					"O ficheiro spam.log não está configurado, não posso continuar...");
			System.exit(1);
		}
		int total = 0;
		for (List<String> var : spam) {
			double sum = 0;
			for (String key : var) {
				try {
					sum += rules.get(key);
				} catch (NullPointerException e) {
				}
			}
			if (sum > 5.0)
				total++;
		}
		return total;
	}

}
