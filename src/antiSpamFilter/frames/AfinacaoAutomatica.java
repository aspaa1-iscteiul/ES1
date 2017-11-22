package antiSpamFilter.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import antiSpamFilter.utils.Utils;

public class AfinacaoAutomatica {

	private JFrame frame;
	private JPanel help_panel_1, help_panel_2;
	private JTextArea help_text1, help_text2;
	private HashMap<String, Double> rules = new HashMap<String, Double>();

	public AfinacaoAutomatica(String path) {
		frame = new JFrame();
		frame.setTitle("Afinação automática do filtro anti-spam");

		mapRules(path);

		addContents();

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(750, 600);
		frame.setResizable(false);

	}

	private void mapRules(String path) {
		String[] list = Utils.rules(path);
		for (String s : list)
			rules.put(s, ((Math.random() * 10) - 5));
		System.out.println(rules);
	}

	private void addContents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));

		JPanel pp = new JPanel();
		pp.setLayout(new BorderLayout());

		JLabel n = new JLabel("Configuração do vetor de pesos");
		n.setFont(new Font("Arial", Font.BOLD, 13));
		pp.add(n, BorderLayout.NORTH);

		JPanel rules_panel = new JPanel();
		rules_panel.setLayout(new GridLayout(0, 1));

		for (HashMap.Entry<String, Double> entry : rules.entrySet()) {
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			p.add(new JLabel(entry.getKey() + "     "), BorderLayout.CENTER);
			p.add(new JLabel(String.format("%.6f", entry.getValue())), BorderLayout.EAST);
			rules_panel.add(p);
		}

		JScrollPane scroll = new JScrollPane(rules_panel);
		pp.add(scroll, BorderLayout.CENTER);
		panel.add(pp, BorderLayout.CENTER);

		/*
		 * lado direito
		 */

		JPanel p = new JPanel();
		p.setBorder(new EmptyBorder(20, 10, 10, 10));
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel results_panel = new JPanel();
		results_panel.setLayout(new BorderLayout());

		results_panel.add(new JLabel("Para a configuração gerada, obtemos:"), BorderLayout.NORTH);

		JPanel help_panels = new JPanel();
		help_panels.setBorder(new EmptyBorder(10, 10, 10, 10));
		help_panels.setLayout(new GridLayout(0, 1));

		help_panel_1 = new JPanel();
		help_panel_1.setLayout(new BorderLayout());
		JButton help = helpButton();
		help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (help_text1.getForeground().equals(Color.BLACK))
					help_text1.setForeground(new JPanel().getBackground());
				else
					help_text1.setForeground(Color.BLACK);
			}
		});
		help_panel_1.add(help, BorderLayout.WEST);
		JLabel label = new JLabel("Falsos Positivos (FP): FP/Total");
		help_panel_1.add(label, BorderLayout.CENTER);
		help_text1 = new JTextArea("Um Falso Positivo (FP) ocorre quando uma mensagem legítima é classificada como mensagem spam");
		help_text1.setForeground(new JPanel().getBackground());
		help_text1.setBackground(new JPanel().getBackground());
		help_text1.setLineWrap(true);
		help_text1.setWrapStyleWord(true);
		help_text1.setEditable(false);
		help_panel_1.add(help_text1, BorderLayout.SOUTH);

		help_panels.add(help_panel_1);

		help_panel_2 = new JPanel();
		help_panel_2.setLayout(new BorderLayout());
		help = helpButton();
		help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (help_text2.getForeground().equals(Color.BLACK))
					help_text2.setForeground(new JPanel().getBackground());
				else
					help_text2.setForeground(Color.BLACK);
			}
		});
		help_panel_2.add(help, BorderLayout.WEST);
		label = new JLabel("Falsos Negativos (FN): FN/Total");
		help_panel_2.add(label, BorderLayout.CENTER);
		help_text2 = new JTextArea("Um Falso Negativo (FN) ocorre quando uma mensagem spam é classificada como mensagem legítima");
		help_text2.setForeground(new JPanel().getBackground());
		help_text2.setBackground(new JPanel().getBackground());
		help_text2.setLineWrap(true);
		help_text2.setWrapStyleWord(true);
		help_text2.setEditable(false);
		help_panel_2.add(help_text2, BorderLayout.SOUTH);

		help_panels.add(help_panel_2);

		results_panel.add(help_panels, BorderLayout.CENTER);

		p.add(results_panel);

		panel.add(p, BorderLayout.EAST);

		frame.add(panel);

		/*
		 * p1.setBounds(0, n.getHeight(), x / 2, y - n.getHeight());
		 * 
		 * for (HashMap.Entry<String, Integer> pair : rules.entrySet()) {
		 * model.addElement(pair.getKey() + "       " + pair.getValue()); }
		 * 
		 * // Iterator<Entry<String, Integer>> it = rules.entrySet().iterator();
		 * // while(it.hasNext()){ // model.addElement("" + it.next()); // }
		 * 
		 * JScrollPane scrollPane = new JScrollPane(); list = new
		 * JList<>(model); scrollPane.setViewportView(list);
		 * scrollPane.setPreferredSize(new Dimension(228, 313));
		 * scrollPane.setMinimumSize(new Dimension(228, 313));
		 * scrollPane.setMaximumSize(new Dimension(228, 313));
		 * list.setLayoutOrientation(JList.VERTICAL); list.setEnabled(false);
		 * 
		 * p1.add(scrollPane); frame.add(p1, BorderLayout.WEST);
		 * 
		 * 
		 * // Panel 2 (Lado direito da frame, falta ajustar as posições)
		 * 
		 * JPanel p2 = new JPanel(); p2.setPreferredSize(new Dimension(x / 2,
		 * y)); p2.setBounds(x / 2 + 1, 0, x, y); p2.setLayout(new BoxLayout(p2,
		 * BoxLayout.Y_AXIS)); p2.add(Box.createVerticalGlue()); JLabel n1 = new
		 * JLabel("Para a configuração gerada, obtemos:"); p2.add(n1);
		 * 
		 * JPanel p2h = new JPanel(); p2h.setLayout(new FlowLayout());
		 * 
		 * JButton help1 = new JButton("?"); p2h.add(help1);
		 * 
		 * JLabel l1 = new JLabel("Falsos Positivos (FP):   " + FP + "/100");
		 * p2h.add(l1);
		 * 
		 * p2.add(p2h);
		 * 
		 * JTextArea text1 = new JTextArea(
		 * "Um Falso Positivo (FP) ocorre quando uma mensagem legítima é classificada"
		 * + " como mensagem spam"); text1.setLineWrap(true);
		 * text1.setPreferredSize(new Dimension(190, 75));
		 * text1.setMinimumSize(new Dimension(190, 75));
		 * text1.setMaximumSize(new Dimension(190, 75)); p2.add(text1);
		 * 
		 * help1.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent arg0) {
		 * text1.setVisible(!text1.isVisible()); text1.revalidate();
		 * text1.repaint(); } });
		 * 
		 * JPanel p2h2 = new JPanel(); p2h2.setLayout(new FlowLayout());
		 * 
		 * JButton help2 = new JButton("?"); p2h2.add(help2);
		 * 
		 * JLabel l3 = new JLabel("Falsos Negativos (FN):   " + FN + "/100");
		 * p2h2.add(l3);
		 * 
		 * p2.add(p2h2);
		 * 
		 * JTextArea text2 = new JTextArea(
		 * "Um Falso Positivo (FP) ocorre quando uma mensagem legítima é classificada"
		 * + " como mensagem spam"); text2.setLineWrap(true); p2.add(text2);
		 * 
		 * text2.setPreferredSize(new Dimension(190, 75));
		 * text2.setMinimumSize(new Dimension(190, 75));
		 * text2.setMaximumSize(new Dimension(190, 75));
		 * 
		 * help2.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent arg0) {
		 * text2.setVisible(!text2.isVisible()); text2.revalidate();
		 * text2.repaint(); } });
		 * 
		 * frame.add(p2, BorderLayout.CENTER);
		 * 
		 * // panel 3 (Botoes no fim da frame, falta adicionar as
		 * funcionalidades)
		 * 
		 * JPanel p3 = new JPanel(); p3.setLayout(new
		 * FlowLayout(FlowLayout.RIGHT)); JButton gerar = new
		 * JButton("Gerar novo vetor de pesos"); p3.add(gerar);
		 * 
		 * JButton cancelar = new JButton("Cancelar"); p3.add(cancelar);
		 * frame.add(p3, BorderLayout.PAGE_END);
		 */
	}

	public JButton helpButton() {
		JButton b = new JButton(new ImageIcon("./src/antiSpamFilter/frames/icons/help_button.png"));
		b.setMargin(new Insets(0, 0, 0, 5));
		b.setBorderPainted(false);
		b.setContentAreaFilled(false);
		b.setFocusPainted(false);
		b.setOpaque(false);
		return b;
	}

	public void open() {
		frame.setVisible(true);
	}

	public static void launch(String path) {
		new AfinacaoAutomatica(path).open();
	}
}
