package antiSpamFilter.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class HomePage {

	private JFrame frame;
	private JList<String> list;
	private JButton select, cancel;
	private Map<String, ImageIcon> images;
	private File configs = new File("./src/antiSpamFilter/frames/config_files_path.txt");
	private String newLine = System.getProperty("line.separator");
	private String[] options = { "Selecionar ficheiro rules.cf", "Selecionar ficheiro spam.log",
			"Selecionar ficheiro ham.log", "Gera��o autom�tica de uma configura��o",
			"Afina��o manual do filtro anti-spam", "Otimiza��o do filtro anti-spam" },
			config_files_path = { "?", "?", "?" }, config_files_names = { "rules.cf", "spam.log", "ham.log" };

	public HomePage() {
		frame = new JFrame();
		frame.setTitle("Home page");
		frame.setResizable(false);

		configFiles();

		addContents();

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(450, 450);
	}

	private void configFiles() {
		try {
			if (!configs.createNewFile()) {
				Scanner scn = new Scanner(configs);
				String[] line = scn.nextLine().split("<");
				String message = "";
				boolean config = false;
				for (int i = 0; i < line.length; i++) {
					if (line[i] != "?" && new File(line[i]).exists() && new File(line[i]).isFile()) {
						config_files_path[i] = line[i];
						message += config_files_names[i] + " - " + line[i] + newLine;
						config = true;
					}
				}
				if (config && (JOptionPane.showConfirmDialog(frame, "Os seguintes ficheiros encontram-se configurados:"
						+ newLine + message + newLine + "Quer manter estes ficheiros de configura��o?") == 1))
					for (int i = 0; i < config_files_path.length; i++)
						config_files_path[i] = "?";
				scn.close();
			} else {
				saveConfigFilesPath();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addContents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JLabel label = new JLabel("Menu");
		label.setFont(new Font("Arial", Font.BOLD, 28));
		panel.add(label, BorderLayout.NORTH);

		images = createImages();
		list = new JList<>(options);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new ListRenderer());
		JScrollPane s = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(s, BorderLayout.CENTER);

		JPanel buttons_panel = new JPanel();
		buttons_panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		select = new JButton("Selecionar");
		select.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectOptions();
			}
		});
		cancel = new JButton("Cancelar");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		});
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				saveConfigFilesPath();
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		buttons_panel.add(select);
		buttons_panel.add(cancel);
		panel.add(buttons_panel, BorderLayout.SOUTH);

		frame.add(panel);
	}

	private void saveConfigFilesPath() {
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

	private void selectOptions() {
		int index = list.getSelectedIndex();
		if (index >= 0 && index <= 2) {
			String file_path = getFileChosen(config_files_names[index]);
			if (file_path == null)
				return;
			for (int i = 0; i < config_files_names.length; i++)
				if (i != index && config_files_path[i].equals(file_path)) {
					JOptionPane.showMessageDialog(frame,
							"O ficheiro selecionado j� foi configurado para " + config_files_names[i]);
					return;
				}
			config_files_path[index] = file_path;
		} else if (index == 3) {
			checkConfigFiles();
		} else if (index == 4) {
			checkConfigFiles();
		} else if (index == 5) {
			checkConfigFiles();
		}
	}

	private void checkConfigFiles() {
		boolean noFile = false;
		for (int i = 0; i < config_files_path.length; i++)
			noFile = noFile || config_files_path[i] == "?";

		if (noFile) {
			String message = new String(
					"Antes de poder realizar esta opera��o, � necess�rio selecionar todos os ficheiros de configura��o."
							+ newLine + newLine + "O ficheiro rules.cf est� configurado?             "
							+ String.valueOf(config_files_path[0] != "?") + newLine
							+ "O ficheiro spam.log est� configurado?          "
							+ String.valueOf(config_files_path[1] != "?") + newLine
							+ "O ficheiro ham.log est� configurado?            "
							+ String.valueOf(config_files_path[2] != "?"));
			JOptionPane.showMessageDialog(frame, message);
		}
	}

	/**
	 * Baseado FileChooser do jSwing
	 * 
	 * @param string
	 *            Nome do ficheiro cujo path queremos configurar
	 * @return Path do ficheiro selecionado
	 */
	private String getFileChosen(String string) {
		JFileChooser fc = new JFileChooser();
		// Inicia a GUI na diretoria do projeto
		fc.setCurrentDirectory(new java.io.File("."));
		fc.setDialogTitle("Selecionar ficheiro " + string);
		fc.setMultiSelectionEnabled(false);
		String extension = string.substring(string.lastIndexOf('.') + 1);
		fc.setFileFilter(new FileNameExtensionFilter(extension.toUpperCase() + " File", extension));
		fc.setAcceptAllFileFilterUsed(false);
		if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile().getAbsolutePath();
		return null;
	}

	private HashMap<String, ImageIcon> createImages() {
		HashMap<String, ImageIcon> m = new HashMap<>();
		m.put(options[0], new ImageIcon("src/antiSpamFilter/frames/icons/file.PNG"));
		m.put(options[1], new ImageIcon("src/antiSpamFilter/frames/icons/file.PNG"));
		m.put(options[2], new ImageIcon("src/antiSpamFilter/frames/icons/file.PNG"));
		m.put(options[3], new ImageIcon("src/antiSpamFilter/frames/icons/circle.PNG"));
		m.put(options[4], new ImageIcon("src/antiSpamFilter/frames/icons/pencil.PNG"));
		m.put(options[5], new ImageIcon("src/antiSpamFilter/frames/icons/magic_wand.PNG"));
		return m;
	}

	private class ListRenderer extends DefaultListCellRenderer {

		/**
		 * Default
		 */
		private static final long serialVersionUID = 1L;
		private Font font = new Font("Consolas", Font.BOLD, 14);

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

	public void open() {
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		HomePage h = new HomePage();
		h.open();
	}

}
