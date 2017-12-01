package antiSpamFilter.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Executor;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import antiSpamFilter.AntiSpamFilterAutomaticConfiguration;
import antiSpamFilter.utils.OtherClasses;
import antiSpamFilter.utils.Utils;

public class Otimizacao {

	private static JFrame frame;
	private static final String algorithm_files = "./experimentBaseDirectory/referenceFronts/AntiSpamFilterProblem.r";
	private Font font_titles = new Font("Helvetica", Font.PLAIN, 18),
			font_labels = new Font("Helvetica", Font.PLAIN, 14), font_text = new Font("Helvetica", Font.PLAIN, 12);
	private JTextArea help_text_fp, help_text_fn;
	private String help_label_fp_text, help_label_fn_text;

	public Otimizacao() {
		calculateBest();
	}

	private void finishConstruct() {
		frame = new JFrame();

		addContents();

		frame.setSize(750, 600);
		frame.setResizable(false);
		frame.addWindowListener(new OtherClasses.OtimizacaoClose());
		Utils.frameAtCenter(frame);
	}

	private void calculateBest() {
		JProgressBar progress = new JProgressBar();
		progress.setString("A calcular...");
		progress.setStringPainted(true);
		progress.setIndeterminate(true);
		JFrame frame = new JFrame();
		frame.add(progress);
		Utils.frameAtCenter(frame);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);

		Executor executor = java.util.concurrent.Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					AntiSpamFilterAutomaticConfiguration.algorithm();
					readAlgorithmOutputs();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frame,
							"Aconteceu alguma coisa durante a execução do código dos profs...");
				}
				// now fix the progress bar
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						frame.dispose();
						finishConstruct();
						visible(true);
					}
				});
			}
		});
	}

	private void readAlgorithmOutputs() {
		ArrayList<String> lines = Utils.lines(algorithm_files + "f");
		double fp = Double.MAX_VALUE, fn = Double.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < lines.size(); i++) {
			String[] values = lines.get(i).split(" ");
			double fp_aux = Double.valueOf(values[0]), fn_aux = Double.valueOf(values[1]);
			if (fp_aux < fp || (fp_aux == fp && fn_aux < fn)) {
				fp = fp_aux;
				fn = fn_aux;
				index = i;
			}
		}
		lines = Utils.lines(algorithm_files + "s");
		String[] values = lines.get(index).split(" ");
		ArrayList<String> rules_list = new ArrayList<>(Utils.rules_weights.keySet());
		Collections.sort(rules_list);
		for (int i = 0; i < values.length; i++)
			Utils.rules_weights.put(rules_list.get(i), Double.valueOf(values[i]));

		int decimal_places = String.valueOf(Utils.hamLogRules.size()).length();
		help_label_fp_text = "  Falsos Positivos (FP):  " + String.format("%0" + decimal_places + "d", (int) fp) + " / "
				+ Utils.hamLogRules.size();
		decimal_places = String.valueOf(Utils.spamLogRules.size()).length();
		help_label_fn_text = "  Falsos Negativos (FN):  " + String.format("%0" + decimal_places + "d", (int) fn) + " / "
				+ Utils.spamLogRules.size();
	}

	/**
	 * Adiciona os conteúdos à janela de Afinação Automática
	 */
	private void addContents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JPanel center_panel = new JPanel();
		center_panel.setLayout(new BorderLayout());

		JLabel panelTitle = new JLabel("Configuração do vetor de pesos");
		panelTitle.setFont(font_titles);
		center_panel.add(panelTitle, BorderLayout.NORTH);

		center_panel.add(createRulesPanel(), BorderLayout.CENTER);
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

		JPanel buttons_panel = new JPanel();
		buttons_panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton save = new JButton("Guardar");
		save.addActionListener(new ActionListener() {
			/*
			 * Sentinela no butão 'Guardar' responsável por guardar a
			 * configuração e retornar à Home Page
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileWriter w = new FileWriter(Utils.config_files_path[0], false);
					for (HashMap.Entry<String, Double> entry : Utils.rules_weights.entrySet())
						w.write(entry.getKey() + " " + entry.getValue().toString() + Utils.newLine);
					w.close();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame,
							"Não foi possível prosseguir! O ficheiro rules.cf está a ser editado.",
							"Configuração dos ficheiros", JOptionPane.WARNING_MESSAGE);
					System.exit(1);
				}
				backHome();
			}
		});

		JButton cancel = new JButton("Cancelar");
		cancel.addActionListener(new ActionListener() {
			/*
			 * Sentinela no butão 'Cancelar' responsável por retornar à Home
			 * Page quando o botão é pressionado
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				backHome();
			}
		});

		buttons_panel.add(save);
		buttons_panel.add(cancel);
		panel.add(buttons_panel, BorderLayout.SOUTH);

		frame.add(panel);
	}

	/**
	 * Criar o painel scrollable com as regras e respetivos pesos
	 * 
	 * @return
	 */
	private JScrollPane createRulesPanel() {
		JPanel rules_panel = new JPanel();
		rules_panel.setLayout(new GridLayout(0, 1));
		for (HashMap.Entry<String, Double> entry : Utils.rules_weights.entrySet()) {
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.add(new JLabel(entry.getKey() + "     "), BorderLayout.CENTER);
			panel.add(new JLabel(String.format("%.4f", entry.getValue())), BorderLayout.EAST);
			rules_panel.add(panel);
		}
		return new JScrollPane(rules_panel);
	}

	/**
	 * Criar o painel com a contagem de FP ou FN, o botão de ajuda e a respetiva
	 * textArea informativa
	 * 
	 * @param number
	 *            Indica qual dos dois cenários tratar (1 para FP e 2 para FN)
	 * @return
	 */
	private JPanel createHelpPanel(int number) {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(20, 10, 10, 10));
		panel.setLayout(new BorderLayout());
		panel.add(formatHelpButton(number), BorderLayout.WEST);

		JLabel count = new JLabel(number == 1 ? help_label_fp_text : help_label_fn_text);
		count.setFont(font_labels);
		panel.add(count, BorderLayout.CENTER);

		if (number == 1) {
			help_text_fp = formatTextArea(Utils.newLine
					+ "Um Falso Positivo (FP) ocorre quando uma mensagem legítima é classificada como mensagem spam.");
			help_text_fp.setFont(font_text);
			panel.add(help_text_fp, BorderLayout.SOUTH);
		} else {
			help_text_fn = formatTextArea(Utils.newLine
					+ "Um Falso Negativo (FN) ocorre quando uma mensagem spam é classificada como mensagem legítima.");
			help_text_fn.setFont(font_text);
			panel.add(help_text_fn, BorderLayout.SOUTH);
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
	private JTextArea formatTextArea(String info) {
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
	private JButton formatHelpButton(int number) {
		JButton button = new JButton(new ImageIcon("./src/antiSpamFilter/frames/icons/help_button.png"));
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setOpaque(false);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				appearText(number == 1 ? help_text_fp : help_text_fn);
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
	private void appearText(JTextArea infoText) {
		if (infoText.getForeground().equals(Color.BLACK))
			infoText.setForeground(new JPanel().getBackground());
		else
			infoText.setForeground(Color.BLACK);
	}

	/**
	 * Descarta a GUI atual e retorna à Home Page
	 */
	public static void backHome() {
		frame.dispose();
		HomePage.visible(true);
	}

	private void visible(boolean visible) {
		frame.setVisible(visible);
	}

	public static void launch() {
		new Otimizacao();
	}

}
