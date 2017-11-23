package antiSpamFilter.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import antiSpamFilter.utils.Utils;

public class HomePage {

	private static JFrame frame;
	private JList<String> list;
	private JButton select, cancel;
	private Map<String, ImageIcon> images;
	private String[] options = { "Selecionar ficheiro rules.cf", "Selecionar ficheiro spam.log",
			"Selecionar ficheiro ham.log", "Gera��o autom�tica de uma configura��o",
			"Afina��o manual do filtro anti-spam", "Otimiza��o do filtro anti-spam" },
			config_files_names = { "rules.cf", "spam.log", "ham.log" };

	/**
	 * Construtor da Home Page
	 */
	public HomePage() {
		frame = new JFrame();
		frame.setTitle("Home Page");

		configFiles();

		addContents();

		/*
		 * Define o tamanho base da janela e impede que esta seja redimensionada
		 */
		frame.setSize(450, 450);
		frame.setResizable(false);
	}

	/**
	 * Recupera, caso existam, os ficheiros de configura��o selecionados durante
	 * a �ltima sess�o e oferece ao utilizador a possibilidade de os manter ou
	 * descartar.
	 */
	private void configFiles() {
		try {
			if (!Utils.configs.createNewFile()) {
				Scanner scn = new Scanner(Utils.configs);
				String[] line = scn.nextLine().split("<");
				String message = "";
				boolean config = false;
				/*
				 * Recupera os paths dos ficheiros configurados durante a �ltima
				 * sess�o.
				 */
				for (int i = 0; i < line.length; i++) {
					if (line[i] != "?" && new File(line[i]).exists() && new File(line[i]).isFile()) {
						Utils.config_files_path[i] = line[i];
						message += config_files_names[i] + " - " + line[i] + Utils.newLine;
						config = true;
					}
				}

				Utils.readConfigFiles();

				/*
				 * Abre uma janela JOptionPane.showConfirmDialog que permite ao
				 * utilizador escolher manter a configura��o dos ficheiros
				 * selecionados durante a �ltima sess�o ou descart�-los.
				 */

				if (config) {
					int n = (JOptionPane.showConfirmDialog(frame,
							"Os seguintes ficheiros encontram-se configurados:" + Utils.newLine + Utils.newLine
									+ message + Utils.newLine + Utils.newLine
									+ "Quer manter estes ficheiros de configura��o?" + Utils.newLine + Utils.newLine));
					// Escolher a op��o 'No' resulta na perda das configura��es
					if (n == JOptionPane.NO_OPTION) {
						for (int i = 0; i < Utils.config_files_path.length; i++)
							Utils.config_files_path[i] = "?";
						// Escolher a op��o 'Cancel' resulta no t�rmino do
						// processo
					} else if (n == JOptionPane.CANCEL_OPTION) {
						System.exit(0);
					}
				}
				scn.close();
			} else {
				/*
				 * Caso o ficheiro onde s�o guardados os paths n�o exista,
				 * cria-o com os valores por defeito para a falta de
				 * configura��es (?)
				 */
				Utils.saveConfigFilesPath();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adiciona os conte�dos � janela da Home Page
	 */
	private void addContents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JLabel label = new JLabel("Menu");
		label.setFont(new Font("Arial", Font.BOLD, 28));
		panel.add(label, BorderLayout.NORTH);

		// Adiciona as op��es do menu
		images = createImages();
		list = new JList<>(options);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new Utils.ListRenderer(images));

		// Reagir a eventos double-click na lista do menu
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 2) {
					selectOptions();
				}
			}
		});

		// Reagir a eventos key-enter na lista do menu
		list.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					selectOptions();
				}
			}
		});

		JScrollPane s = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(s, BorderLayout.CENTER);

		// Adiciona o bot�o de "Selecionar" uma op��o do menu
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
		 * Adiciona o bot�o de "Cancelar" e, caso o mesmo seja pressionado,
		 * define a sentinela para o fecho da janela
		 */
		cancel = new JButton("Cancelar");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		frame.addWindowListener(new Utils.HomePageClose());
		buttons_panel.add(select);
		buttons_panel.add(cancel);
		panel.add(buttons_panel, BorderLayout.SOUTH);

		frame.add(panel);
	}

	/**
	 * Determina que a��es executar consoante a op��o escolhida no menu da Home
	 * Page.
	 */
	private void selectOptions() {
		int index = list.getSelectedIndex();
		if (index == -1) // n�o foi selecionada nenhuma op��o
			return;
		if (index >= 0 && index <= 2) {
			String file_path = getFileChosen(config_files_names[index]);
			if (file_path == null)
				return;
			/*
			 * Verifica se o ficheiro escolhido tinha sido previamente
			 * selecionado como outro ficheiro de configura��o. Evita que o
			 * mesmo ficheiro seja escolhido como ham.log e spam.log
			 * simultaneamente.
			 */
			for (int i = 0; i < config_files_names.length; i++)
				if (i != index && Utils.config_files_path[i].equals(file_path)) {
					JOptionPane.showMessageDialog(frame,
							"O ficheiro selecionado j� foi configurado para " + config_files_names[i],
							"Selecionar ficheiro " + config_files_names[index], JOptionPane.WARNING_MESSAGE);
					return;
				}
			Utils.config_files_path[index] = file_path;
			if (index == 0)
				Utils.rules();
			else if (index == 1)
				Utils.spamLog();
			else
				Utils.hamLog();

		} else if (checkConfigFiles()) {
			Utils.saveConfigFilesPath();
			if (index == 3) {
				AfinacaoAutomatica.launch();
				visible(false);
			} else if (index == 4) {
			} else if (index == 5) {
			}
		}
	}

	/**
	 * Verifica se j� est�o atribu�dos paths aos ficheiros de configura��o e
	 * expressa os resultados numa janela JOptionPane.showMessageDialog para o
	 * utilizador consultar.
	 */
	private boolean checkConfigFiles() {
		boolean noFile = false;
		String message = "Antes de poder realizar esta opera��o, � necess�rio selecionar todos os ficheiros de configura��o."
				+ Utils.newLine + Utils.newLine;
		for (int i = 0; i < Utils.config_files_path.length; i++) {
			message += "O ficheiro " + config_files_names[i];
			if (Utils.config_files_path[i] == "?") {
				message += " n�o est� configurado";
				noFile = true;
			} else
				message += " est� configurado em " + Utils.config_files_path[i];
			message += Utils.newLine;
		}

		if (noFile)
			JOptionPane.showMessageDialog(frame, message, "Configura��o dos ficheiros",
					JOptionPane.INFORMATION_MESSAGE);
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
		// Inclui no t�tulo da janela o nome do ficheiro a selecionar
		fc.setDialogTitle("Selecionar ficheiro " + string);
		// Impede a sele��o de m�ltiplas op��es
		fc.setMultiSelectionEnabled(false);
		// Apenas os ficheiros com extens�o correta s�o vis�veis
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
	 * @return HashMap resultante da associa��o
	 */
	private HashMap<String, ImageIcon> createImages() {
		// Associa uma op��o do menu ao respetivo icon, de acordo com os mockups
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

	public static void visible(boolean open) {
		frame.setVisible(open);
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		HomePage h = new HomePage();
		h.visible(true);
	}

}
