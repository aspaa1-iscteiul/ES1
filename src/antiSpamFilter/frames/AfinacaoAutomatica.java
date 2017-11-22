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

import antiSpamFilter.utils.Utils;

public class AfinacaoAutomatica {

	private static JFrame frame;
	private JPanel help_panel_1, help_panel_2;
	private JScrollPane scroll_rules_panel;
	private JTextArea help_text1, help_text2;
	private JButton generate, save, cancel;
	private JLabel help_label_1, help_label_2;

	public AfinacaoAutomatica() {
		frame = new JFrame();
		frame.setTitle("Afina��o autom�tica do filtro anti-spam");

		Utils.rules();

		addContents();

		calculate();

		frame.pack();
		frame.setSize(750, 600);
		frame.setResizable(false);

	}

	private void changeWeights() {
		for (HashMap.Entry<String, Double> entry : Utils.rules.entrySet())
			entry.setValue((Math.random() * 10) - 5);
		calculate();
	}

	private void calculate() {
		help_label_1.setText("  Falsos Positivos (FP):  " + Utils.falsePositives() + " / " + Utils.ham.size());
		help_label_2.setText("  Falsos Negativos (FN):  " + Utils.falseNegatives() + " / " + Utils.spam.size());
	}

	private void addContents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JPanel pp = new JPanel();
		pp.setLayout(new BorderLayout());

		JLabel n = new JLabel("Configura��o do vetor de pesos");
		n.setFont(new Font("Arial", Font.BOLD, 13));
		pp.add(n, BorderLayout.NORTH);

		createRulesPanel();

		pp.add(scroll_rules_panel, BorderLayout.CENTER);
		panel.add(pp, BorderLayout.CENTER);

		/*
		 * lado direito
		 */

		JPanel p = new JPanel();
		p.setBorder(new EmptyBorder(20, 10, 10, 10));
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel results_panel = new JPanel();
		results_panel.setLayout(new BorderLayout());

		results_panel.add(new JLabel("Para a configura��o gerada, obtemos:"), BorderLayout.NORTH);

		JPanel help_panels = new JPanel();
		help_panels.setBorder(new EmptyBorder(10, 10, 10, 10));
		help_panels.setLayout(new GridLayout(0, 1));

		help_panel_1 = new JPanel();
		help_panel_1.setBorder(new EmptyBorder(20, 10, 10, 10));
		help_panel_1.setLayout(new BorderLayout());
		JButton help1 = helpButton();
		help1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				appearText(help_text1);
			}
		});
		help_panel_1.add(help1, BorderLayout.WEST);
		help_label_1 = new JLabel("  Falsos Positivos (FP): ");
		help_panel_1.add(help_label_1, BorderLayout.CENTER);
		help_text1 = myTextArea(Utils.newLine
				+ "Um Falso Positivo (FP) ocorre quando uma mensagem leg�tima � classificada como mensagem spam.                   ");
		help_panel_1.add(help_text1, BorderLayout.SOUTH);

		help_panels.add(help_panel_1);

		help_panel_2 = new JPanel();
		help_panel_2.setBorder(new EmptyBorder(20, 10, 10, 10));
		help_panel_2.setLayout(new BorderLayout());
		JButton help2 = helpButton();
		help2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				appearText(help_text2);
			}
		});
		help_panel_2.add(help2, BorderLayout.WEST);
		help_label_2 = new JLabel("  Falsos Negativos (FN): FN/Total");
		help_panel_2.add(help_label_2, BorderLayout.CENTER);
		help_text2 = myTextArea(Utils.newLine
				+ "Um Falso Negativo (FN) ocorre quando uma mensagem spam � classificada como mensagem leg�tima.");
		help_panel_2.add(help_text2, BorderLayout.SOUTH);

		help_panels.add(help_panel_2);

		results_panel.add(help_panels, BorderLayout.CENTER);

		p.add(results_panel);

		panel.add(p, BorderLayout.EAST);

		JPanel buttons_panel = new JPanel();
		buttons_panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		generate = new JButton("Gerar novo vetor de pesos");
		generate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeWeights();
				pp.remove(scroll_rules_panel);
				createRulesPanel();
				pp.add(scroll_rules_panel, BorderLayout.CENTER);
				frame.validate();
				frame.repaint();
			}
		});

		save = new JButton("Guardar");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileWriter w = new FileWriter(Utils.config_files_path[0], false);
					for (HashMap.Entry<String, Double> entry : Utils.rules.entrySet())
						w.write(entry.getKey() + " " + entry.getValue().toString() + Utils.newLine);
					w.close();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame, "O ficheiro rules.cf j� n�o est� na diretoria indicada");
					System.exit(1);
				}
				backHome();
			}
		});

		cancel = new JButton("Cancelar");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backHome();
			}
		});
		frame.addWindowListener(new Utils.WindowClose(false));
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

	public void createRulesPanel() {
		JPanel rules_panel = new JPanel();
		rules_panel.setLayout(new GridLayout(0, 1));
		for (HashMap.Entry<String, Double> entry : Utils.rules.entrySet()) {
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			p.add(new JLabel(entry.getKey() + "     "), BorderLayout.CENTER);
			p.add(new JLabel(String.format("%.6f", entry.getValue())), BorderLayout.EAST);
			rules_panel.add(p);
		}
		scroll_rules_panel = new JScrollPane(rules_panel);
	}

	public JTextArea myTextArea(String text) {
		JTextArea t = new JTextArea(text);
		t.setForeground(new JPanel().getBackground());
		t.setBackground(new JPanel().getBackground());
		t.setLineWrap(true);
		t.setWrapStyleWord(true);
		t.setEditable(false);
		return t;
	}

	public void appearText(JTextArea text) {
		if (text.getForeground().equals(Color.BLACK))
			text.setForeground(new JPanel().getBackground());
		else
			text.setForeground(Color.BLACK);
	}

	public JButton helpButton() {
		JButton b = new JButton(new ImageIcon("./src/antiSpamFilter/frames/icons/help_button.png"));
		b.setMargin(new Insets(0, 0, 0, 0));
		b.setBorderPainted(false);
		b.setContentAreaFilled(false);
		b.setFocusPainted(false);
		b.setOpaque(false);
		return b;
	}

	public void visible(boolean open) {
		frame.setVisible(open);
	}

	public static void launch() {
		new AfinacaoAutomatica().visible(true);
	}
}
