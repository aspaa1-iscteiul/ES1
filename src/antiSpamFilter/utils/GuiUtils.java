package antiSpamFilter.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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

import antiSpamFilter.frames.AfinacaoAutomatica;
import antiSpamFilter.frames.Otimizacao;
//import antiSpamFilter.frames.AfinacaoManual;

public class GuiUtils {
	public static JScrollPane scroll_rules_panel;
	public static HashMap<String, JTextField> rules_values;
	public static JLabel help_label_fp, help_label_fn;
	public static String newLine = System.getProperty("line.separator");

	private static Font font_titles = new Font("Helvetica", Font.PLAIN, 18),
			font_labels = new Font("Helvetica", Font.PLAIN, 14), font_text = new Font("Helvetica", Font.PLAIN, 12);
	private static JTextArea help_area_fp, help_area_fn;

	public static void frameAtCenter(JFrame frame) {
		frame.setLocation(new Point((Toolkit.getDefaultToolkit().getScreenSize().width - frame.getWidth()) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height - frame.getHeight()) / 2));
	}

	public static JPanel constructGUI(JPanel panel, boolean optimize) {
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JPanel center_panel = new JPanel();
		center_panel.setLayout(new BorderLayout());

		JLabel panelTitle = new JLabel("Configuração do vetor de pesos");
		panelTitle.setFont(font_titles);
		center_panel.add(panelTitle, BorderLayout.NORTH);

		createRulesPanel(optimize);
		center_panel.add(scroll_rules_panel, BorderLayout.CENTER);
		panel.add(center_panel, BorderLayout.CENTER);

		JPanel right_panel = new JPanel();
		right_panel.setBorder(new EmptyBorder(20, 10, 10, 10));
		right_panel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel results_panel = new JPanel();
		results_panel.setLayout(new BorderLayout());

		panelTitle = new JLabel("Para a configuração gerada, obtemos:");
		panelTitle.setFont(font_titles);
		results_panel.add(panelTitle, BorderLayout.NORTH);

		JPanel help_panel = new JPanel();
		help_panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		help_panel.setLayout(new GridLayout(0, 1));
		help_panel.add(createHelpPanel(1));
		help_panel.add(createHelpPanel(2));

		results_panel.add(help_panel, BorderLayout.CENTER);

		right_panel.add(results_panel);

		panel.add(right_panel, BorderLayout.EAST);

		return center_panel;
	}

	public static void createRulesPanel(boolean optimize) {
		JPanel rules_panel = new JPanel();
		rules_panel.setLayout(new GridLayout(0, 1));
		rules_values = new HashMap<>();
		for (HashMap.Entry<String, Double> entry : Utils.rules_weights.entrySet()) {
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			String key = entry.getKey();
			JTextField value = onlyDoubles(entry.getValue());
			value.setEditable(!optimize);
			rules_values.put(key, value);
			panel.add(new JLabel(key + "     "), BorderLayout.CENTER);
			panel.add(value, BorderLayout.EAST);
			rules_panel.add(panel);
		}
		scroll_rules_panel = new JScrollPane(rules_panel);
		scroll_rules_panel.getVerticalScrollBar().setUnitIncrement(50);
		scroll_rules_panel.setWheelScrollingEnabled(true);
	}

	private static JPanel createHelpPanel(int number) {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(20, 10, 10, 10));
		panel.setLayout(new BorderLayout());
		panel.add(formatHelpButton(number), BorderLayout.WEST);

		if (number == 1) {
			help_label_fp = new JLabel();
			help_label_fp.setFont(font_labels);
			panel.add(help_label_fp, BorderLayout.CENTER);
			help_area_fp = formatTextArea(newLine
					+ "Um Falso Positivo (FP) ocorre quando uma mensagem legítima é classificada como mensagem spam.");
			help_area_fp.setFont(font_text);
			panel.add(help_area_fp, BorderLayout.SOUTH);
		} else {
			help_label_fn = new JLabel();
			help_label_fn.setFont(font_labels);
			panel.add(help_label_fn, BorderLayout.CENTER);
			help_area_fn = formatTextArea(newLine
					+ "Um Falso Negativo (FN) ocorre quando uma mensagem spam é classificada como mensagem legítima.");
			help_area_fn.setFont(font_text);
			panel.add(help_area_fn, BorderLayout.SOUTH);
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
		JTextArea textarea = new JTextArea(info);
		textarea.setForeground(new JPanel().getBackground());
		textarea.setBackground(new JPanel().getBackground());
		textarea.setLineWrap(true);
		textarea.setWrapStyleWord(true);
		textarea.setEditable(false);
		return textarea;
	}

	/**
	 * Formatar os botões de ajuda
	 * 
	 * @param number
	 *            Número do botão a formatar
	 * @return botão de ajuda formatado
	 */
	private static JButton formatHelpButton(int number) {
		JButton button = new JButton(new ImageIcon("./src/antiSpamFilter/frames/icons/help_button.png"));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setOpaque(false);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				appearText(number == 1 ? help_area_fp : help_area_fn);
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

	public static class HomePageClose implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
		}

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

	public static class AfinacaoAutomaticaClose implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
		}

		@Override
		public void windowClosing(WindowEvent e) {
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

	public static class OtimizacaoClose implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
		}

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

	private static JTextField onlyDoubles(double number) {
		if (number < -5 || number > 5)
			return null;
		String n = Double.toString(number);
		if (number >= 0)
			n = "+" + n;
		if (n.length() >= 7)
			n = n.substring(0, 7);

		JTextField t = new JTextField(n, 10) {
			private static final long serialVersionUID = 1L;

			@Override
			public void processKeyEvent(KeyEvent key) {
				char c = key.getKeyChar();
				String text = getText();

				int code = key.getKeyCode();
				if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_LEFT || code == KeyEvent.VK_BACK_SPACE
						|| code == KeyEvent.VK_DELETE)
					super.processKeyEvent(key);

				if (code == KeyEvent.VK_ENTER) {
					double v = Double.valueOf(text);
					if (v < -5 || v > 5)
						JOptionPane.showMessageDialog(new JFrame(), "you're good !");
					else
						JOptionPane.showMessageDialog(new JFrame(), "you're number is " + v);
				}

				if (c == ',') {
					c = '.';
					key.setKeyChar('.');
				}

				if (text.length() == 0) {
					if (c == '-' || c == '+')
						super.processKeyEvent(key);
					else if (c >= '0' && c <= '5') {
						setText("+");
						super.processKeyEvent(key);
					} else if (c == '.') {
						setText("+0");
						super.processKeyEvent(key);
					}
				} else if (text.length() == 1) {
					if (c >= '0' && c <= '5') {
						super.processKeyEvent(key);
					} else if (c == '.') {
						setText(text + '0');
						super.processKeyEvent(key);
					}
				} else if (text.length() == 2) {
					if (text.equals("+5") || text.equals("-5"))
						return;
					if (c == '.')
						super.processKeyEvent(key);
				} else if (text.length() < 7) {
					if (c >= '0' && c <= '9')
						super.processKeyEvent(key);
				}
			}
		};
		t.setFont(new Font("Consolas", Font.PLAIN, 16));
		return t;
	}

	public static boolean checkValues() {
		for (Entry<String, JTextField> entry : GuiUtils.rules_values.entrySet()) {
			double value = Double.valueOf(entry.getValue().getText());
			if (value < -5 || value > 5)
				return false;
		}
		return true;
	}

}
