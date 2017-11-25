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

	private static JFrame frame;
	private JTextArea help_text_fp, help_text_fn;
	private JLabel help_label_fp, help_label_fn;
	private JScrollPane scroll_rules_panel;
	private Font font_titles = new Font("Helvetica", Font.PLAIN, 18),
			font_labels = new Font("Helvetica", Font.PLAIN, 14), font_text = new Font("Helvetica", Font.PLAIN, 12);

	public AfinacaoAutomatica() {
		frame = new JFrame();
		frame.setTitle("Afinação automática do filtro anti-spam");

		Utils.readConfigFiles();

		addContents();

		calculate();

		frame.pack();
		frame.setSize(750, 600);
		frame.setResizable(false);
		frame.addWindowListener(new OtherClasses.AfinacaoAutomaticaClose());
	}

	private void changeWeights() {
		for (HashMap.Entry<String, Double> entry : Utils.rules.entrySet())
			entry.setValue((Math.random() * 10) - 5);
		calculate();
	}

	private void calculate() {
		int var = String.valueOf(Utils.ham.size()).length();
		help_label_fp.setText("  Falsos Positivos (FP):  " + String.format("%0" + var + "d", Utils.falsePositives())
				+ " / " + Utils.ham.size());
		var = String.valueOf(Utils.spam.size()).length();
		help_label_fn.setText("  Falsos Negativos (FN):  " + String.format("%0" + var + "d", Utils.falseNegatives())
				+ " / " + Utils.spam.size());
	}

	private void addContents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JPanel center_panel = new JPanel();
		center_panel.setLayout(new BorderLayout());

		JLabel title = new JLabel("Configuração do vetor de pesos");
		title.setFont(font_titles);
		center_panel.add(title, BorderLayout.NORTH);

		createRulesPanel();
		center_panel.add(scroll_rules_panel, BorderLayout.CENTER);
		panel.add(center_panel, BorderLayout.CENTER);

		JPanel right_panel = new JPanel();
		right_panel.setBorder(new EmptyBorder(20, 10, 10, 10));
		right_panel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel results_panel = new JPanel();
		results_panel.setLayout(new BorderLayout());

		title = new JLabel("Para a configuração gerada, obtemos:");
		title.setFont(font_titles);
		results_panel.add(title, BorderLayout.NORTH);

		JPanel help_panels = new JPanel();
		help_panels.setBorder(new EmptyBorder(10, 10, 10, 10));
		help_panels.setLayout(new GridLayout(0, 1));
		help_panels.add(createHelpPanel(1));
		help_panels.add(createHelpPanel(2));

		results_panel.add(help_panels, BorderLayout.CENTER);

		right_panel.add(results_panel);

		panel.add(right_panel, BorderLayout.EAST);

		JPanel buttons_panel = new JPanel();
		buttons_panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton generate = new JButton("Gerar novo vetor de pesos");
		generate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeWeights();
				center_panel.remove(scroll_rules_panel);
				createRulesPanel();
				center_panel.add(scroll_rules_panel, BorderLayout.CENTER);
				frame.validate();
				frame.repaint();
			}
		});

		JButton save = new JButton("Guardar");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileWriter w = new FileWriter(Utils.config_files_path[0], false);
					for (HashMap.Entry<String, Double> entry : Utils.rules.entrySet())
						w.write(entry.getKey() + " " + entry.getValue().toString() + Utils.newLine);
					w.close();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame, "O ficheiro rules.cf já não está na diretoria indicada");
					System.exit(1);
				}
				backHome();
			}
		});

		JButton cancel = new JButton("Cancelar");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backHome();
			}
		});

		buttons_panel.add(generate);
		buttons_panel.add(save);
		buttons_panel.add(cancel);
		panel.add(buttons_panel, BorderLayout.SOUTH);

		frame.add(panel);
	}

	public static void backHome() {
		frame.dispose();
		HomePage.visible(true);
	}

	private void createRulesPanel() {
		JPanel rules_panel = new JPanel();
		rules_panel.setLayout(new GridLayout(0, 1));
		for (HashMap.Entry<String, Double> entry : Utils.rules.entrySet()) {
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.add(new JLabel(entry.getKey() + "     "), BorderLayout.CENTER);
			panel.add(new JLabel(String.format("%.4f", entry.getValue())), BorderLayout.EAST);
			rules_panel.add(panel);
		}
		scroll_rules_panel = new JScrollPane(rules_panel);
	}

	private JPanel createHelpPanel(int number) {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(20, 10, 10, 10));
		panel.setLayout(new BorderLayout());
		panel.add(helpButton(number), BorderLayout.WEST);
		JLabel label = new JLabel("  Falsos Positivos (FP): ");
		label.setFont(font_labels);
		panel.add(label, BorderLayout.CENTER);
		JTextArea text = myTextArea(Utils.newLine
				+ "Um Falso Positivo (FP) ocorre quando uma mensagem legítima é classificada como mensagem spam.");
		text.setFont(font_text);
		panel.add(text, BorderLayout.SOUTH);
		if (number == 1) {
			help_label_fp = label;
			help_text_fp = text;
		} else {
			help_label_fn = label;
			help_text_fn = text;
		}
		return panel;
	}

	private JTextArea myTextArea(String text) {
		JTextArea textarea = new JTextArea(text);
		textarea.setForeground(new JPanel().getBackground());
		textarea.setBackground(new JPanel().getBackground());
		textarea.setLineWrap(true);
		textarea.setWrapStyleWord(true);
		textarea.setEditable(false);
		return textarea;
	}

	private JButton helpButton(int number) {
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
		frame.setVisible(open);
	}

	public static void launch() {
		new AfinacaoAutomatica().visible(true);
	}
}
