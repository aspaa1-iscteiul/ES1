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
import antiSpamFilter.frames.HomePage;

public class Utils {

	public static String newLine = System.getProperty("line.separator");

	public static String[] rules(String rules_path) {
		List<String> rules = new ArrayList<>();
		try {
			Scanner scn = new Scanner(new File(rules_path));
			while (scn.hasNextLine()) {
				String line = scn.nextLine();
				if (!line.equals(""))
					rules.add(line);
			}
			scn.close();
		} catch (FileNotFoundException e) { // if is not a file
			JOptionPane.showMessageDialog(new JFrame(), "O ficheiro rules.cf já não está na diretoria indicada");
			System.exit(1);
		}
		return rules.toArray(new String[0]);
	}

	/**
	 * Guarda o path dos ficheiros de configuração escolhidos num ficheiro.
	 * Permite que sejam mantidas as configurações para sessões seguintes.
	 */
	public static void saveConfigFilesPath() {
		try {
			FileWriter w = new FileWriter(HomePage.configs, false);
			for (int i = 0; i < HomePage.config_files_path.length; i++) {
				w.write(HomePage.config_files_path[i]);
				if (i < HomePage.config_files_path.length - 1)
					w.write("<");
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
