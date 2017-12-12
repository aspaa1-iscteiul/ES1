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
import antiSpamFilter.utils.GuiUtils;
import antiSpamFilter.utils.Utils;

/**
 * 
 * Classe responsável por determinar o comportamento da primeira interface de
 * interação com o utilizador - a Home Page -, incluindo suporte à seleção de
 * uma opção do menu e à seleção dos ficheiros de configuração rules.cf,
 * spam.log e ham.log.
 * 
 * @author Ana Pestana, Diogo Reis, Guilherme Azevedo, Rafael Costa
 *
 */
public class HomePage {

	private static JFrame homePage;
	private JList<String> menuList;
	private Map<String, ImageIcon> images;
	private String[] menuOptions = { "Selecionar ficheiro rules.cf", "Selecionar ficheiro spam.log",
			"Selecionar ficheiro ham.log", "Geração aleatória de uma configuração",
			"Afinação manual do filtro anti-spam", "Otimização do filtro anti-spam" },
			config_files_names = { "rules.cf", "spam.log", "ham.log" };

	/**
	 * Construtor da Home Page
	 */
	public HomePage() {
		homePage = new JFrame();
		homePage.setTitle("Home Page");

		configFiles();

		addContents();

		/*
		 * Define o tamanho base da janela e impede que esta seja redimensionada
		 */
		homePage.setSize(450, 450);
		homePage.setResizable(false);
		GuiUtils.frameAtCenter(homePage);
	}

	/**
	 * Recupera, caso existam, os ficheiros de configuração selecionados durante
	 * a última sessão e oferece ao utilizador a possibilidade de os manter ou
	 * descartar.
	 */
	private void configFiles() {
		try {
			/*
			 * O ficheiro onde são guardadas as configurações dos ficheiros já
			 * existe
			 */
			if (!Utils.fileConfigs.createNewFile()) {
				Scanner scn = new Scanner(Utils.fileConfigs);
				String[] line = scn.nextLine().split("<");
				String message = "";
				boolean existsFileConfig = false;
				/*
				 * Recupera os paths dos ficheiros configurados durante a última
				 * sessão.
				 */
				for (int i = 0; i < line.length; i++) {
					if (line[i] != "?" && new File(line[i]).exists() && new File(line[i]).isFile()) {
						Utils.config_files_path[i] = line[i];
						message += config_files_names[i] + " - " + line[i] + GuiUtils.newLine;
						existsFileConfig = true;
					}
				}

				/*
				 * Abre uma janela JOptionPane.showConfirmDialog que permite ao
				 * utilizador escolher manter a configuração dos ficheiros
				 * selecionados durante a última sessão ou descartá-los.
				 */
				if (existsFileConfig) {
					int n = (JOptionPane.showConfirmDialog(homePage, "Os seguintes ficheiros encontram-se configurados:"
							+ GuiUtils.newLine + GuiUtils.newLine + message + GuiUtils.newLine + GuiUtils.newLine
							+ "Quer manter estes ficheiros de configuração?" + GuiUtils.newLine + GuiUtils.newLine));
					// Escolher a opção 'No' resulta na perda das configurações
					if (n == JOptionPane.NO_OPTION) {
						for (int i = 0; i < Utils.config_files_path.length; i++)
							Utils.config_files_path[i] = "?";
						// Escolher a opção 'Cancel' resulta no término do
						// processo
					} else if (n == JOptionPane.CANCEL_OPTION) {
						System.exit(0);
					}
				}
				scn.close();
			} else {
				/*
				 * Caso o ficheiro onde são guardados os paths ainda não exista,
				 * criamo-lo com os valores por defeito para a falta de
				 * configurações (i.e., "?")
				 */
				Utils.saveConfigFilesPath();
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

		JLabel menuLabel = new JLabel("Menu");
		menuLabel.setFont(new Font("Arial", Font.BOLD, 28));
		panel.add(menuLabel, BorderLayout.NORTH);

		// Adiciona as opções do menu
		images = createImages();
		menuList = new JList<>(menuOptions);
		// Impede seleção múltipla
		menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		menuList.setCellRenderer(new GuiUtils.ListRenderer(images));

		// Reagir a eventos double-click na lista do menu
		menuList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					selectOptions();
				}
			}
		});

		// Reagir a eventos key-enter na lista do menu
		menuList.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
					selectOptions();
				}
			}
		});

		JScrollPane scrollPanel = new JScrollPane(menuList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPanel, BorderLayout.CENTER);

		// Adiciona o botão de "Selecionar" uma opção do menu
		JPanel buttons_panel = new JPanel();
		buttons_panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton select = new JButton("Selecionar");
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
		JButton cancel = new JButton("Cancelar");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				homePage.dispose();
			}
		});
		homePage.addWindowListener(new GuiUtils.HomePageClose());
		buttons_panel.add(select);
		buttons_panel.add(cancel);
		panel.add(buttons_panel, BorderLayout.SOUTH);

		homePage.add(panel);
	}

	/**
	 * Determina que ações executar consoante a opção escolhida no menu da Home
	 * Page.
	 */
	private void selectOptions() {
		int index = menuList.getSelectedIndex();
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
				if (i != index && Utils.config_files_path[i].equals(file_path)) {
					JOptionPane.showMessageDialog(homePage,
							"O ficheiro selecionado já foi configurado para " + config_files_names[i],
							"Selecionar ficheiro " + config_files_names[index], JOptionPane.WARNING_MESSAGE);
					return;
				}
			// Guarda o ficheiro escolhido
			Utils.config_files_path[index] = file_path;
			Utils.readConfigFiles();

		} else if (checkConfigFiles()) {
			visible(false);
			Utils.saveConfigFilesPath();
			if (!Utils.readConfigFiles())
				return;
			if (index == 3 || index == 4) {
				AfinacaoAutomatica.launch();
			} else if (index == 5) {
				Otimizacao.launch();
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
				+ GuiUtils.newLine + GuiUtils.newLine;
		for (int i = 0; i < Utils.config_files_path.length; i++) {
			message += "O ficheiro " + config_files_names[i];
			if (Utils.config_files_path[i] == "?") {
				message += " não está configurado";
				noFile = true;
			} else
				message += " está configurado em " + Utils.config_files_path[i];
			message += GuiUtils.newLine;
		}

		if (noFile)
			JOptionPane.showMessageDialog(homePage, message, "Configuração dos ficheiros",
					JOptionPane.INFORMATION_MESSAGE);
		return !noFile;
	}

	/**
	 * Baseado JFileChooser do java Swing providencia um mecanismo simples para
	 * que o utilizador escolha um ficheiro.
	 * 
	 * @param fileName
	 *            Nome do ficheiro cujo path queremos configurar
	 * @return Path do ficheiro selecionado
	 */
	private String getFileChosen(String fileName) {
		JFileChooser fc = new JFileChooser();
		// Inicia a GUI na diretoria do projeto
		fc.setCurrentDirectory(new java.io.File("."));
		// Inclui no título da janela o nome do ficheiro a selecionar
		fc.setDialogTitle("Selecionar ficheiro " + fileName);
		// Impede a seleção de múltiplas opções
		fc.setMultiSelectionEnabled(false);
		// Apenas os ficheiros com extensão correta são visíveis
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		fc.setFileFilter(new FileNameExtensionFilter(extension.toUpperCase() + " File", extension));
		fc.setAcceptAllFileFilterUsed(false);
		// Retorna o path do ficheiro selecionado
		if (fc.showOpenDialog(homePage) == JFileChooser.APPROVE_OPTION)
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
		m.put(menuOptions[0], new ImageIcon("src/antiSpamFilter/frames/icons/file.PNG"));
		m.put(menuOptions[1], new ImageIcon("src/antiSpamFilter/frames/icons/file.PNG"));
		m.put(menuOptions[2], new ImageIcon("src/antiSpamFilter/frames/icons/file.PNG"));
		m.put(menuOptions[3], new ImageIcon("src/antiSpamFilter/frames/icons/circle.PNG"));
		m.put(menuOptions[4], new ImageIcon("src/antiSpamFilter/frames/icons/pencil.PNG"));
		m.put(menuOptions[5], new ImageIcon("src/antiSpamFilter/frames/icons/magic_wand.PNG"));
		return m;
	}

	/**
	 * Define a visibilidade da frame da HomePage
	 * 
	 * @param setVisible
	 */
	public static void visible(boolean setVisible) {
		homePage.setVisible(setVisible);
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		HomePage h = new HomePage();
		h.visible(true);
	}

}
