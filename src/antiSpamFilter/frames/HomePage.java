package antiSpamFilter.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
	private Map<String, ImageIcon> images;
	private JButton select, cancel;
	private String rules_path = "?", spam_path = "?", ham_path = "?";
	private String[] options = { "Selecionar ficheiro rules.cf", "Selecionar ficheiro spam.log",
			"Selecionar ficheiro ham.log", "Gera��o autom�tica de uma configura��o",
			"Afina��o manual do filtro anti-spam", "Otimiza��o do filtro anti-spam" };

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
			if (!new File("./src/antiSpamFilter/frames/config_files_path.txt").createNewFile()) {
				Scanner scn = new Scanner(new File("./src/antiSpamFilter/frames/config_files_path.txt"));
				String[] line = scn.nextLine().split(":");
				if (line[0] != "?" && new File(line[0]).exists() && new File(line[0]).isFile())
					rules_path = line[0];
				if (line[1] != "?" && new File(line[1]).exists() && new File(line[1]).isFile())
					spam_path = line[1];
				if (line[2] != "?" && new File(line[2]).exists() && new File(line[2]).isFile())
					ham_path = line[2];
				scn.close();
			} else {
				PrintWriter scn = new PrintWriter(new File("./src/antiSpamFilter/frames/config_files_path.txt"));
				scn.write("?:?:?");
				scn.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		buttons_panel.add(select);
		buttons_panel.add(cancel);
		panel.add(buttons_panel, BorderLayout.SOUTH);

		frame.add(panel);
	}

	private void selectOptions() {
		switch (list.getSelectedIndex()) {
		case 0:
			rules_path = choose("rules.cf");
			break;
		case 1:
			spam_path = choose("spam.log");
			break;
		case 2:
			ham_path = choose("ham.log");
			break;
		case 3:
			checkConfigFiles();
			break;
		case 4:
			checkConfigFiles();
			break;
		case 5:
			checkConfigFiles();
			break;
		}
	}

	private void checkConfigFiles() {
		String newLine = System.getProperty("line.separator");
		if (rules_path == "?" || spam_path == "?" || ham_path == "?") {
			String message = new String(
					"Antes de poder realizar esta opera��o, � necess�rio selecionar todos os ficheiros de configura��o."
							+ newLine + newLine + "O ficheiro rules.cf est� configurado?             "
							+ String.valueOf(rules_path != "?") + newLine
							+ "O ficheiro spam.log est� configurado?          "
							+ String.valueOf(spam_path != "?") + newLine
							+ "O ficheiro ham.log est� configurado?            "
							+ String.valueOf(ham_path != "?"));
			JOptionPane.showMessageDialog(frame, message);
		}
	}

	private String choose(String string) {
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
