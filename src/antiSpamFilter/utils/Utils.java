package antiSpamFilter.utils;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import antiSpamFilter.frames.AfinacaoAutomatica;

public class Utils {

	public static String[] config_files_path = { "?", "?", "?" };
	public static File configs = new File("./src/antiSpamFilter/frames/config_files_path.txt");
	public static ArrayList<List<String>> ham = new ArrayList<List<String>>(), spam = new ArrayList<List<String>>();
	public static HashMap<String, Double> rules = new HashMap<String, Double>();

	public static String newLine = System.getProperty("line.separator");

	public static String[] lines(String file_path) {
		List<String> lines = new ArrayList<>();
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
		return lines.toArray(new String[0]);
	}

	public static void rules() {
		String file_path = config_files_path[0];
		if (!file_path.equals("?")) {
			String[] list = lines(file_path);
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
		} else {
			JOptionPane.showMessageDialog(new JFrame(), "Não devias estar aqui - Utils.java line 71");
		}
	}

	public static void spamLog() {
		String file_path = config_files_path[1];
		if (!file_path.equals("?")) {
			String[] list = lines(file_path);
			for (String s : list) {
				String[] ss = s.split("\t");
				spam.add(Arrays.asList(Arrays.copyOfRange(ss, 1, ss.length)));
			}
		} else {
			JOptionPane.showMessageDialog(new JFrame(), "Não devias estar aqui - Utils.java line 84");
		}
	}

	public static void hamLog() {
		String file_path = config_files_path[2];
		if (!file_path.equals("?")) {
			String[] list = lines(file_path);
			for (String s : list) {
				String[] ss = s.split("\t");
				ham.add(Arrays.asList(Arrays.copyOfRange(ss, 1, ss.length)));
			}
		} else {
			JOptionPane.showMessageDialog(new JFrame(), "Não devias estar aqui - Utils.java line 97");
		}
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
		int total = 0;
		for (List<String> var : ham) {
			double sum = 0;
			for (String key : var)
				if (rules.get(key) != null)
					sum += rules.get(key);
				else
					JOptionPane.showMessageDialog(new JFrame(), "FP - KEY ERROR");
			if (sum < 5.0)
				total++;
		}
		return total;
	}

	public static int falseNegatives() {
		int total = 0;
		for (List<String> var : spam) {
			double sum = 0;
			for (String key : var)
				if (rules.get(key) != null)
					sum += rules.get(key);
				else
					JOptionPane.showMessageDialog(new JFrame(), "FN - KEY ERROR -> " + key);
			if (sum > 5.0)
				total++;
		}
		return total;
	}

	/*
	 * 
	 * Classes
	 * 
	 */
	public static class ListRenderer extends DefaultListCellRenderer {

		/**
		 * Default
		 */
		private static final long serialVersionUID = 1L;
		private Font font = new Font("Consolas", Font.BOLD, 14);
		private Map<String, ImageIcon> images;

		public ListRenderer(Map<String, ImageIcon> images) {
			this.images = images;
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			label.setIcon(images.get((String) value));
			label.setHorizontalTextPosition(JLabel.RIGHT);
			label.setFont(font);
			return label;
		}

	}

	public static class WindowClose implements WindowListener {

		private boolean home_page;

		public WindowClose(boolean home_page) {
			this.home_page = home_page;
		}

		@Override
		public void windowOpened(WindowEvent e) {
		}

		@Override
		public void windowClosing(WindowEvent e) {
			if (home_page)
				saveConfigFilesPath();
			else
				AfinacaoAutomatica.backHome();
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowActivated(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}

	}

}
