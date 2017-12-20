package antiSpamFilter.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import antiSpamFilter.frames.Afinacao;
import antiSpamFilter.frames.HomePage;
import antiSpamFilter.frames.Otimizacao;

public class GuiUtils {

	public static JScrollPane scrollRulesPanel;
	public static HashMap<String, JTextField> rulesValues;
	public static JLabel helpLabelFp, helpLabelFn;
	private static Font fontTitles = new Font("Helvetica", Font.PLAIN, 18),
			fontLabels = new Font("Helvetica", Font.PLAIN, 14), fontText = new Font("Helvetica", Font.PLAIN, 12);
	private static JTextArea helpAreaFp, helpAreaFn;

	/*
	 * Garante a utilização do caractere de mudança de linha, independentemente
	 * do Sistema Operativo em que a aplicação corre
	 */
	public static String newLine = System.getProperty("line.separator");

	/**
	 * Centra a frame passada como argumento no ecrã
	 * 
	 * @param frame
	 *            JFrame que queremos centrar
	 */
	public static void frameAtCenter(JFrame frame) {
		frame.setLocation(new Point((Toolkit.getDefaultToolkit().getScreenSize().width - frame.getWidth()) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height - frame.getHeight()) / 2));
	}

	/**
	 * Construtor do esqueleto da GUI utilizada pelas classes Otimizacao e
	 * Afinacao
	 * 
	 * @param panel
	 *            JPanel principal
	 * @param optimize
	 *            booleano que indica se estamos num cenário de Otimizacao
	 * @return center_panel necessários para fazer a atualização dos pesos
	 */
	public static JPanel constructGUI(JPanel panel, boolean optimize) {
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());

		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel titleLabel = new JLabel("Configuração do vetor de pesos       ");
		titleLabel.setFont(fontTitles);
		titlePanel.add(titleLabel);

		if (!optimize) {
			JButton button = new JButton(new ImageIcon(HomePage.class.getResource("icons/circle2.PNG")));
			button.setMargin(new Insets(0, 0, 0, 0));
			button.setFocusPainted(false);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Afinacao.changeWeights();
					centerPanel.remove(GuiUtils.scrollRulesPanel);
					GuiUtils.createRulesPanel(false);
					centerPanel.add(GuiUtils.scrollRulesPanel, BorderLayout.CENTER);
					Afinacao.update();
				}
			});
			titlePanel.add(button);
		}
		centerPanel.add(titlePanel, BorderLayout.NORTH);

		createRulesPanel(optimize);
		centerPanel.add(scrollRulesPanel, BorderLayout.CENTER);
		panel.add(centerPanel, BorderLayout.CENTER);

		JPanel rightPanel = new JPanel();
		rightPanel.setBorder(new EmptyBorder(20, 10, 10, 10));
		rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel resultsPanel = new JPanel();
		resultsPanel.setLayout(new BorderLayout());

		titleLabel = new JLabel("Para a configuração gerada, obtemos:");
		titleLabel.setFont(fontTitles);
		resultsPanel.add(titleLabel, BorderLayout.NORTH);

		JPanel helpPanel = new JPanel();
		helpPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		helpPanel.setLayout(new GridLayout(0, 1));
		helpPanel.add(createHelpPanel(true));
		helpPanel.add(createHelpPanel(false));

		resultsPanel.add(helpPanel, BorderLayout.CENTER);

		rightPanel.add(resultsPanel);

		panel.add(rightPanel, BorderLayout.EAST);

		return centerPanel;
	}

	/**
	 * Cria o painel onde é realizado o display das regras e respetivos pesos
	 * 
	 * @param optimize
	 *            booleano que indica se estamos num cenário de Otimizacao
	 */
	public static void createRulesPanel(boolean optimize) {
		JPanel rulesPanel = new JPanel();
		rulesPanel.setLayout(new GridLayout(0, 1));
		rulesValues = new HashMap<>();
		for (HashMap.Entry<String, Double> entry : Utils.rulesWeights.entrySet()) {
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			JTextField value = new JDoubleInputTextField(entry.getValue().toString(), 8, -5, 5, 4, !optimize);
			rulesValues.put(entry.getKey(), value);
			panel.add(new JLabel(entry.getKey() + "     "), BorderLayout.CENTER);
			panel.add(value, BorderLayout.EAST);
			rulesPanel.add(panel);
		}
		scrollRulesPanel = new JScrollPane(rulesPanel);
		scrollRulesPanel.getVerticalScrollBar().setUnitIncrement(50);
		scrollRulesPanel.setWheelScrollingEnabled(true);
	}

	/**
	 * Cria o painel de ajuda onde podem ser consultados os FP e FN
	 * 
	 * @param fp
	 *            Indica se estamos a tratar dos FP (true) ou dos FN (false)
	 * @return painel de ajuda
	 */
	private static JPanel createHelpPanel(boolean fp) {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(20, 10, 10, 10));
		panel.setLayout(new BorderLayout());
		panel.add(formatHelpButton(fp ? 1 : 2), BorderLayout.WEST);

		if (fp) {
			helpLabelFp = new JLabel();
			helpLabelFp.setFont(fontLabels);
			panel.add(helpLabelFp, BorderLayout.CENTER);
			helpAreaFp = formatTextArea(newLine
					+ "Um Falso Positivo (FP) ocorre quando uma mensagem legítima é classificada como mensagem spam.");
			helpAreaFp.setFont(fontText);
			panel.add(helpAreaFp, BorderLayout.SOUTH);
		} else {
			helpLabelFn = new JLabel();
			helpLabelFn.setFont(fontLabels);
			panel.add(helpLabelFn, BorderLayout.CENTER);
			helpAreaFn = formatTextArea(newLine
					+ "Um Falso Negativo (FN) ocorre quando uma mensagem spam é classificada como mensagem legítima.");
			helpAreaFn.setFont(fontText);
			panel.add(helpAreaFn, BorderLayout.SOUTH);
		}
		return panel;
	}

	/**
	 * Formatar a textArea informativa
	 * 
	 * @param info
	 *            Texto de ajuda
	 * @return textArea formatada
	 */
	private static JTextArea formatTextArea(String info) {
		JTextArea textArea = new JTextArea(info);
		textArea.setForeground(new JPanel().getBackground());
		textArea.setBackground(new JPanel().getBackground());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		return textArea;
	}

	/**
	 * Formatar os botões de ajuda
	 * 
	 * @param number
	 *            Número do botão a formatar (number=1 para FP; FN, caso
	 *            contrário)
	 * @return botão de ajuda formatado
	 */
	public static JButton formatHelpButton(int number) {
		JButton button = new JButton(new ImageIcon(HomePage.class.getResource("icons/help_button.png")));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setOpaque(false);
		if (number != -1)
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					appearText(number == 1 ? helpAreaFp : helpAreaFn);
				}
			});
		return button;
	}

	/**
	 * Caso o texto informativo esteja visível, camufla-o com o background. Caso
	 * contrário, torna-o visível.
	 * 
	 * @param infoText
	 */
	private static void appearText(JTextArea infoText) {
		if (infoText.getForeground().equals(Color.BLACK))
			infoText.setForeground(new JPanel().getBackground());
		else
			infoText.setForeground(Color.BLACK);
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

	/**
	 * Default
	 *
	 */
	public static class HomePageClose implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
		}

		/**
		 * Definição do fecho da HomePage
		 */
		@Override
		public void windowClosing(WindowEvent e) {
			Utils.saveConfigFilesPath();
			System.exit(0);
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

	/**
	 * 
	 * Default
	 *
	 */
	public static class AfinacaoAutomaticaClose implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
		}

		/**
		 * Definição do fecho da página de Afinação
		 */
		@Override
		public void windowClosing(WindowEvent e) {
			Afinacao.backHome();
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

	/**
	 * 
	 * Default
	 *
	 */
	public static class OtimizacaoClose implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
		}

		/**
		 * Definição do fecho da página de Otimização
		 */
		@Override
		public void windowClosing(WindowEvent e) {
			Otimizacao.backHome();
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

	public static final class JDoubleInputTextField extends JTextField {
		/**
		 * Default
		 */
		private static final long serialVersionUID = 1L;

		private double max, min;
		private int precision;
		private boolean editable;

		/**
		 * Cria uma nova JTextField qua apenas permite doubles com valores entre
		 * <b>min</b> e <b>max</b>
		 * 
		 * @param text
		 *            como em {@link JTextField#JTextField(String, int)}
		 * @param columns
		 *            como em {@link JTextField#JTextField(String, int)}
		 * @param min
		 *            valor mínimo (inclusivé) para o input
		 * @param max
		 *            valor máximo (inclusivé) para o input
		 * @param precision
		 *            número de casas decimais
		 * 
		 * @see JTextField#JTextField(String, int)
		 */
		public JDoubleInputTextField(String text, int columns, double min, double max, int precision) {
			this(text, columns, min, max, precision, true);
		}

		/**
		 * Cria uma nova JTextField qua apenas permite doubles com valores entre
		 * {@code min} e {@code max}
		 * 
		 * @param text
		 *            como em {@link JTextField#JTextField(String, int)}
		 * @param columns
		 *            como em {@link JTextField#JTextField(String, int)}
		 * @param min
		 *            valor mínimo (inclusivé) para o input
		 * @param max
		 *            valor máximo (inclusivé) para o input
		 * @param precision
		 *            número de casas decimais
		 * @param editable
		 *            Este componente é editável caso {@code true}. Caso
		 *            contrário, o componente não é editável
		 * 
		 * @see JTextField#JTextField(String, int)
		 */
		public JDoubleInputTextField(String text, int columns, double min, double max, int precision,
				boolean editable) {
			super(text, columns);
			if ((min < 0 && max < 0) || (min > 0 && max > 0)) {
				throw new IllegalArgumentException("zero must be between min and max");
			}
			setFont(new Font("Consolas", Font.PLAIN, 16));
			this.min = min;
			this.max = max;
			this.precision = precision;
			this.editable = editable;
			if (isValidInput(text)) {
				setText(round(Double.valueOf(text), precision));
			} else
				setText(null);
		}

		/**
		 * Implementa proteções de escrita em campos editáveis
		 */
		@Override
		public void processKeyEvent(KeyEvent key) {
			if (!editable)
				return;

			String textBeforeProcess = getText();
			super.processKeyEvent(key);

			if (key.getKeyCode() == KeyEvent.VK_RIGHT || key.getKeyCode() == KeyEvent.VK_LEFT)
				return;

			if (key.getKeyCode() == KeyEvent.VK_BACK_SPACE || key.getKeyCode() == KeyEvent.VK_DELETE) {
				if (key.getID() == KeyEvent.KEY_PRESSED)
					try {
						setText(textBeforeProcess.substring(0, textBeforeProcess.length() - 1));
					} catch (StringIndexOutOfBoundsException e) {
					}
				return;
			}

			if (key.getKeyChar() == ',')
				key.setKeyChar('.');

			if ((key.getKeyChar() == '+' || key.getKeyChar() == '-') && textBeforeProcess.equals(""))
				return;

			int pointIndex = textBeforeProcess.indexOf(".") + 1;
			if (pointIndex > 0 && (textBeforeProcess.length() - pointIndex) == precision) {
				setText(textBeforeProcess);
				return;
			}

			if (isValidInput(getText()))
				rearrange(getText());
			else
				setText(textBeforeProcess);
		}

		/**
		 * Uniformiza o formato do input passado como argumento
		 * 
		 * @param text
		 *            variável a uniformizar
		 */
		private void rearrange(String text) {
			try {
				if (Double.valueOf(text) >= 0 && !text.startsWith("+")) {
					if (!(Double.valueOf(text) == 0 && text.startsWith("-")))
						text = '+' + text;
				}
				while (text.length() > 2 && text.charAt(1) == '0' && text.charAt(2) != '.')
					text = text.charAt(0) + text.substring(2);
			} catch (NumberFormatException e) {
			}
			setText(text);
		}

		/**
		 * Valida o formato e conteúdo do input
		 * 
		 * @param text
		 *            Input a validar
		 * @return válido
		 */
		private boolean isValidInput(String text) {
			try {
				double value = Double.valueOf(round(Double.valueOf(text), precision));
				return value >= min && value <= max && !(text.endsWith("d") || text.endsWith("f"));
			} catch (NumberFormatException e) {
				return text.equals("") || text.equals("+") || text.equals("-");
			}
		}

		/**
		 * Dado um double, a função retorna uma string com um valor resultante
		 * do arrendondamento do double original, respeitando uma determinada
		 * precisão
		 * 
		 * @param value
		 *            Double a arredondar
		 * @param precision
		 *            Precisão do arredondamento
		 * @return String com o número arredondado
		 */
		private String round(double value, int precision) {
			if (precision < 0)
				throw new IllegalArgumentException("Precision must be equal or higher then zero");

			String number = new BigDecimal(value).setScale(precision, RoundingMode.HALF_UP).toString();
			if (value >= 0)
				number = '+' + number;
			return number;
		}

	}

	/**
	 * Preenche os campos vazios com +0.0 para garantir a uniformidade
	 * 
	 * @return tudoVálido
	 */
	public static boolean checkValues() {
		for (Entry<String, JTextField> entry : GuiUtils.rulesValues.entrySet()) {
			try {
				double value = Double.valueOf(entry.getValue().getText());
				if (value < -5 || value > 5)
					return false;
			} catch (NumberFormatException e) {
				entry.getValue().setText("+0.0");
			}
		}
		return true;
	}

	/**
	 * Classe de tratamento de exceções decorrentes da compilação e abertura dos
	 * ficheiros .R
	 *
	 */
	public static class RException extends MouseAdapter {
		private static final String NEW_LINE = "<br/>";
		private JLabel label;
		private JFrame frame;

		/**
		 * Lançamento de um JOptionPane quando se verifica um erro de compilação
		 * com o pacote de software R
		 * 
		 * @param frame
		 *            JFrame mãe
		 */
		public RException(JFrame frame) {
			label = new JLabel("<html>Ocorreu um problema ao compilar com o Rscript.exe" + NEW_LINE + NEW_LINE
					+ "Sugestão de resolução:" + NEW_LINE
					+ "Por favor, verifique se tem uma aplicação para compilação de documentos .R instalada no seu computador e, caso"
					+ NEW_LINE
					+ "tenha, verifique ainda que o path para o executável Rscript.exe se encontra incluído na variável de ambiente PATH"
					+ NEW_LINE + NEW_LINE
					+ "(Poderá proceder ao download do pacote de software R em: <a href=\"\">https://cran.r-project.org/</a>)</html>");
			label.addMouseListener(this);
			this.frame = frame;
			JOptionPane.showMessageDialog(frame, label, "Indicador Hypervolume", JOptionPane.ERROR_MESSAGE);
		}

		/**
		 * Lançamento de um JOptionPane quando se verifica um erro de abertura
		 * do browser
		 */
		public void mousePressed(MouseEvent event) {
			if (event.getX() >= 342 && event.getX() <= 486 && event.getY() >= 96 && event.getY() <= 111)
				try {
					Desktop.getDesktop().browse(new URI("https://cran.r-project.org/"));
				} catch (IOException | URISyntaxException e) {
					JOptionPane.showMessageDialog(frame, "Por favor, verifique se tem um browser instalado.",
							"Indicador Hypervolume", JOptionPane.ERROR_MESSAGE);
				}
		}

	}

	/**
	 * Classe de tratamento de exceções decorrentes da compilação e abertura dos
	 * ficheiros .tex
	 *
	 */
	public static class LatexException extends MouseAdapter {
		private static final String NEW_LINE = "<br/>";
		private JFrame frame;
		private JLabel label;

		/**
		 * Lançamento de um JOptionPane quando se verifica um erro de compilação
		 * com o pacote de software MiKTeX
		 * 
		 * @param frame
		 *            JFrame mãe
		 */
		public LatexException(JFrame frame) {
			label = new JLabel("<html>Ocorreu um problema ao compilar com o pdflatex.exe" + NEW_LINE + NEW_LINE
					+ "Sugestão de resolução:" + NEW_LINE
					+ "Por favor, verifique se tem uma aplicação para compilação de documentos .tex instalada no seu computador e, caso"
					+ NEW_LINE
					+ "tenha, verifique ainda que o path para o executável pdflatex.exe se encontra incluído na variável de ambiente PATH"
					+ NEW_LINE + NEW_LINE
					+ "(Poderá proceder ao download do pacote de software MiKTeX em: <a href=\"\">https://miktex.org/download</a>)</html>");
			label.addMouseListener(this);
			this.frame = frame;
			JOptionPane.showMessageDialog(frame, label, "Indicador Hypervolume", JOptionPane.ERROR_MESSAGE);
		}

		/**
		 * Lançamento de um JOptionPane quando se verifica um erro de abertura
		 * do browser
		 */
		public void mousePressed(MouseEvent event) {
			if (event.getX() >= 373 && event.getX() <= 533 && event.getY() >= 96 && event.getY() <= 111)
				try {
					Desktop.getDesktop().browse(new URI("https://miktex.org/download"));
				} catch (IOException | URISyntaxException e) {
					JOptionPane.showMessageDialog(frame, "Por favor, verifique se tem um browser instalado.",
							"Indicador Hypervolume", JOptionPane.ERROR_MESSAGE);
				}
		}

	}

}
