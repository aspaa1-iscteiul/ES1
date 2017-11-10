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
			"Selecionar ficheiro ham.log", "Geração automática de uma configuração",
			"Afinação manual do filtro anti-spam", "Otimização do filtro anti-spam" },
			config_files_path = { "?", "?", "?" }, config_files_names = { "rules.cf", "spam.log", "ham.log" };

	/**
	 * Construtor da Home Page
	 */
	public HomePage() {
		frame = new JFrame();
		frame.setTitle("Home page");

		configFiles();

		addContents();

		/*
		 * Define o fecho da janela ao clicar no "Fechar" do canto superior
		 * direito
		 */
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		/*
		 * Define o tamanho base da janela e impede que esta seja redimensionada
		 */
		frame.setSize(450, 450);
		frame.setResizable(false);
	}

	/**
	 * Recupera, caso existam, os ficheiros de configuração selecionados durante
	 * a última sessão e oferece ao utilizador a possibilidade de os manter ou
	 * descartar.
	 */
	private void configFiles() {
		try {
			if (!configs.createNewFile()) {
				Scanner scn = new Scanner(configs);
				String[] line = scn.nextLine().split("<");
				String message = "";
				boolean config = false;
				/*
				 * Recupera os paths dos ficheiros configurados durante a última
				 * sessão.
				 */
				for (int i = 0; i < line.length; i++) {
					if (line[i] != "?" && new File(line[i]).exists() && new File(line[i]).isFile()) {
						config_files_path[i] = line[i];
						message += config_files_names[i] + " - " + line[i] + newLine;
						config = true;
					}
				}
				/*
				 * Abre uma janela JOptionPane.showConfirmDialog que permite ao
				 * utilizador escolher manter a configuração dos ficheiros
				 * selecionados durante a última sessão ou descartá-los.
				 */
				if (config && (JOptionPane.showConfirmDialog(frame, "Os seguintes ficheiros encontram-se configurados:"
						+ newLine + message + newLine + "Quer manter estes ficheiros de configuração?") == 1))
					for (int i = 0; i < config_files_path.length; i++)
						config_files_path[i] = "?";
				scn.close();
			} else {
				/*
				 * Caso o ficheiro onde são guardados os paths não exista,
				 * cria-o com os valores por defeito para a falta de
				 * configurações (?)
				 */
				saveConfigFilesPath();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adiciona os conteúdos à janela da Home Page
	 */
	private void addContents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JLabel label = new JLabel("Menu");
		label.setFont(new Font("Arial", Font.BOLD, 28));
		panel.add(label, BorderLayout.NORTH);

		// Adiciona as opções do menu
		images = createImages();
		list = new JList<>(options);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new ListRenderer());
		JScrollPane s = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(s, BorderLayout.CENTER);

		// Adiciona o botão de "Selecionar" uma opção do menu
		JPanel buttons_panel = new JPanel();
		buttons_panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		select = new JButton("Selecionar");
		select.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectOptions();
			}
		});
		/*
		 * Adiciona o botão de "Cancelar" e, caso o mesmo seja pressionado,
		 * define a sentinela para o fecho da janela
		 */
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

	/**
	 * Guarda o path dos ficheiros de configuração escolhidos num ficheiro.
	 * Permite que sejam mantidas as configurações para sessões seguintes.
	 */
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

	/**
	 * Determina que ações executar consoante a opção escolhida no menu da Home
	 * Page.
	 */
	private void selectOptions() {
		int index = list.getSelectedIndex();
		if (index == -1) // não foi selecionada nenhuma opção
			return;
		if (index >= 0 && index <= 2) {
			String file_path = getFileChosen(config_files_names[index]);
			if (file_path == null)
				return;
			/*
			 * Verifica se o ficheiro escolhido tinha sido previamente
			 * selecionado como outro ficheiro de configuração. Evita que o
			 * mesmo ficheiro seja escolhido como ham.log e spam.log
			 * simultaneamente.
			 */
			for (int i = 0; i < config_files_names.length; i++)
				if (i != index && config_files_path[i].equals(file_path)) {
					JOptionPane.showMessageDialog(frame,
							"O ficheiro selecionado já foi configurado para " + config_files_names[i]);
					return;
				}
			config_files_path[index] = file_path;

		} else if (checkConfigFiles()) {
			if (index == 3) {
			} else if (index == 4) {
			} else if (index == 5) {
			}
		}
	}

	/**
	 * Verifica se já estão atribuídos paths aos ficheiros de configuração e
	 * expressa os resultados numa janela JOptionPane.showMessageDialog para o
	 * utilizador consultar.
	 */
	private boolean checkConfigFiles() {
		boolean noFile = false;
		String message = "Antes de poder realizar esta operação, é necessário selecionar todos os ficheiros de configuração."
				+ newLine + newLine;
		for (int i = 0; i < config_files_path.length; i++) {
			message += "O ficheiro " + config_files_names[i];
			if (config_files_path[i] == "?") {
				message += " não está configurado";
				noFile = true;
			} else
				message += " está configurado em " + config_files_path[i];
			message += newLine;
		}

		if (noFile)
			JOptionPane.showMessageDialog(frame, message);
		return !noFile;
	}

	/**
	 * Baseado JFileChooser do java Swing providencia um mecanismo simples para
	 * que o utilizador escolha um ficheiro.
	 * 
	 * @param string
	 *            Nome do ficheiro cujo path queremos configurar
	 * @return Path do ficheiro selecionado
	 */
	private String getFileChosen(String string) {
		JFileChooser fc = new JFileChooser();
		// Inicia a GUI na diretoria do projeto
		fc.setCurrentDirectory(new java.io.File("."));
		// Inclui no título da janela o nome do ficheiro a selecionar
		fc.setDialogTitle("Selecionar ficheiro " + string);
		// Impede a seleção de múltiplas opções
		fc.setMultiSelectionEnabled(false);
		// Apenas os ficheiros com extensão correta são visíveis
		String extension = string.substring(string.lastIndexOf('.') + 1);
		fc.setFileFilter(new FileNameExtensionFilter(extension.toUpperCase() + " File", extension));
		fc.setAcceptAllFileFilterUsed(false);
		// Retorna o path do ficheiro selecionado
		if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile().getAbsolutePath();
		return null;
	}

	/**
	 * Associa uma String a uma imagem.
	 * 
	 * @return HashMap resultante da associação
	 */
	private HashMap<String, ImageIcon> createImages() {
		// Associa uma opção do menu ao respetivo icon, de acordo com os mockups
		// desenhados
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
