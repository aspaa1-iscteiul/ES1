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
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import antiSpamFilter.utils.OtherClasses;
import antiSpamFilter.utils.Utils;

public class AfinacaoAutomatica {

	private static JFrame afinacaoAuto;
	private JTextArea help_text_fp, help_text_fn;
	private JLabel help_label_fp, help_label_fn;
	private JScrollPane scroll_rules_panel;
	private Font font_titles = new Font("Helvetica", Font.PLAIN, 18),
			font_labels = new Font("Helvetica", Font.PLAIN, 14), font_text = new Font("Helvetica", Font.PLAIN, 12);

	/**
	 * Construtor da página de Afinação Automática
	 */
	public AfinacaoAutomatica() {
		afinacaoAuto = new JFrame();
		afinacaoAuto.setTitle("Afinação automática do filtro anti-spam");

		Utils.readConfigFiles();

		addContents();

		calculate_FP_FN();

		afinacaoAuto.pack();
		afinacaoAuto.setSize(750, 600);
		afinacaoAuto.setResizable(false);
		afinacaoAuto.addWindowListener(new OtherClasses.AfinacaoAutomaticaClose());
	}

	/**
	 * Atribui novos pesos de forma aleatória com valores entre -5 e 5 às regras
	 * mapeadas no HashMap
	 */
	private void changeWeights() {
		for (HashMap.Entry<String, Double> entry : Utils.rules_weights.entrySet())
			entry.setValue((Math.random() * 10) - 5);
		calculate_FP_FN();
	}

	/**
	 * Procede ao cálculo, por invocação, do número de Falsos Positivos e Falsos
	 * Negativos
	 */
	private void calculate_FP_FN() {
		int decimal_places = String.valueOf(Utils.hamLogRules.size()).length();
		help_label_fp.setText("  Falsos Positivos (FP):  "
				+ String.format("%0" + decimal_places + "d", Utils.falses(true)) + " / " + Utils.hamLogRules.size());
		decimal_places = String.valueOf(Utils.spamLogRules.size()).length();
		help_label_fn.setText("  Falsos Negativos (FN):  "
				+ String.format("%0" + decimal_places + "d", Utils.falses(false)) + " / " + Utils.spamLogRules.size());
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

		createRulesPanel();
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

		JPanel buttons_panel = new JPanel();
		buttons_panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton generate = new JButton("Gerar novo vetor de pesos");
		generate.addActionListener(new ActionListener() {
			/*
			 * Sentinela no butão 'Gerar novo vetor' responsável por invocar a
			 * função changeWeights() e refletir as alterações na interface
			 * gráfica da página de Afinação Automática
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				changeWeights();
				center_panel.remove(scroll_rules_panel);
				createRulesPanel();
				center_panel.add(scroll_rules_panel, BorderLayout.CENTER);
				afinacaoAuto.validate();
				afinacaoAuto.repaint();
			}
		});

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
					JOptionPane.showMessageDialog(afinacaoAuto,
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

		buttons_panel.add(generate);
		buttons_panel.add(save);
		buttons_panel.add(cancel);
		panel.add(buttons_panel, BorderLayout.SOUTH);

		afinacaoAuto.add(panel);
	}

	/**
	 * Descarta a GUI atual e retorna à Home Page
	 */
	public static void backHome() {
		afinacaoAuto.dispose();
		HomePage.visible(true);
	}

	/**
	 * Criar o painel scrollable com as regras e respetivos pesos
	 */
	private void createRulesPanel() {
		JPanel rules_panel = new JPanel();
		rules_panel.setLayout(new GridLayout(0, 1));
		for (HashMap.Entry<String, Double> entry : Utils.rules_weights.entrySet()) {
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.add(new JLabel(entry.getKey() + "     "), BorderLayout.CENTER);
			panel.add(new JLabel(String.format("%.4f", entry.getValue())), BorderLayout.EAST);
			rules_panel.add(panel);
		}
		scroll_rules_panel = new JScrollPane(rules_panel);
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

		JLabel count = new JLabel();
		count.setFont(font_labels);
		panel.add(count, BorderLayout.CENTER);

		JTextArea info = formatTextArea(" ");
		info.setFont(font_text);
		panel.add(info, BorderLayout.SOUTH);

		if (number == 1) {
			help_label_fp = count;
			info.setText(Utils.newLine
					+ "Um Falso Positivo (FP) ocorre quando uma mensagem legítima é classificada como mensagem spam.");
			help_text_fp = info;
		} else {
			help_label_fn = count;
			info.setText(Utils.newLine
					+ "Um Falso Negativo (FN) ocorre quando uma mensagem spam é classificada como mensagem legítima.");
			help_text_fn = info;
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

	private void appearText(JTextArea text) {
		if (text.getForeground().equals(Color.BLACK))
			text.setForeground(new JPanel().getBackground());
		else
			text.setForeground(Color.BLACK);
	}

	public void visible(boolean open) {
		afinacaoAuto.setVisible(open);
	}

	public static void launch() {
		new AfinacaoAutomatica().visible(true);
	}
}
