package antiSpamFilter.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class AfinacaoAutomatica {
	private JFrame frame;
	private JList<String> list;
	private HashMap<String, Integer> rules = new HashMap<String, Integer>();

	private final int x = 450;
	private final int y = 450;
	DefaultListModel<String> model = new DefaultListModel<>();
	private int FP = 0;
	private int FN = 0;

	public AfinacaoAutomatica(String path) {
		frame = new JFrame();
		frame.setTitle("Afinação automática do filtro anti-spam");

		mapRules(path);

		addContents();

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setSize(565, 459);
		frame.setResizable(false);

	}

	private void mapRules(String path) {
		Scanner sc;
		try {
			sc = new Scanner(new File(path));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (!line.equals(""))
					rules.put(line, 0);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void addContents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(38, 42, 38, 42));

		JLabel n = new JLabel("Configuração do vetor de pesos");
		n.setFont(new Font("Arial", Font.BOLD, 13));
		frame.add(n, BorderLayout.NORTH);

		// Panel 1 (Lado esquerdo da Frame, contem a Lista, falta adicionar o
		// peso)

		JPanel p1 = new JPanel();
		p1.setBounds(0, n.getHeight(), x / 2, y - n.getHeight());

		for (HashMap.Entry<String,Integer> pair : rules.entrySet()) {
			model.addElement(pair.getKey() + "       " + pair.getValue());
		}
		
//		Iterator<Entry<String, Integer>> it = rules.entrySet().iterator();
//		while(it.hasNext()){
//			model.addElement("" + it.next());
//		}
		
		JScrollPane scrollPane = new JScrollPane();
		list = new JList<>(model);
		scrollPane.setViewportView(list);
		scrollPane.setPreferredSize(new Dimension(228, 313));
		scrollPane.setMinimumSize(new Dimension(228, 313));
		scrollPane.setMaximumSize(new Dimension(228, 313));
		list.setLayoutOrientation(JList.VERTICAL);
		list.setEnabled(false);

		p1.add(scrollPane);
		frame.add(p1, BorderLayout.WEST);

		// Panel 2 (Lado direito da frame, falta ajustar as posições)

		JPanel p2 = new JPanel();
		p2.setPreferredSize(new Dimension(x / 2, y));
		p2.setBounds(x / 2 + 1, 0, x, y);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.add(Box.createVerticalGlue());
		JLabel n1 = new JLabel("Para a configuração gerada, obtemos:");
		p2.add(n1);

		JPanel p2h = new JPanel();
		p2h.setLayout(new FlowLayout());

		JButton help1 = new JButton("?");
		p2h.add(help1);

		JLabel l1 = new JLabel("Falsos Positivos (FP):   " + FP + "/100");
		p2h.add(l1);

		p2.add(p2h);

		JTextArea text1 = new JTextArea(
				"Um Falso Positivo (FP) ocorre quando uma mensagem legítima é classificada" + " como mensagem spam");
		text1.setLineWrap(true);
		text1.setPreferredSize(new Dimension(190, 75));
		text1.setMinimumSize(new Dimension(190, 75));
		text1.setMaximumSize(new Dimension(190, 75));
		p2.add(text1);

		help1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				text1.setVisible(!text1.isVisible());
				text1.revalidate();
				text1.repaint();
			}
		});

		JPanel p2h2 = new JPanel();
		p2h2.setLayout(new FlowLayout());

		JButton help2 = new JButton("?");
		p2h2.add(help2);

		JLabel l3 = new JLabel("Falsos Negativos (FN):   " + FN + "/100");
		p2h2.add(l3);

		p2.add(p2h2);

		JTextArea text2 = new JTextArea(
				"Um Falso Positivo (FP) ocorre quando uma mensagem legítima é classificada" + " como mensagem spam");
		text2.setLineWrap(true);
		p2.add(text2);

		text2.setPreferredSize(new Dimension(190, 75));
		text2.setMinimumSize(new Dimension(190, 75));
		text2.setMaximumSize(new Dimension(190, 75));

		help2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				text2.setVisible(!text2.isVisible());
				text2.revalidate();
				text2.repaint();
			}
		});

		frame.add(p2, BorderLayout.CENTER);

		// panel 3 (Botoes no fim da frame, falta adicionar as funcionalidades)

		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton gerar = new JButton("Gerar novo vetor de pesos");
		p3.add(gerar);

		JButton cancelar = new JButton("Cancelar");
		p3.add(cancelar);
		frame.add(p3, BorderLayout.PAGE_END);
	}

	public void open() {
		frame.setVisible(true);
	}

	public static void launch(String path) {
		AfinacaoAutomatica g = new AfinacaoAutomatica(path);
		g.open();
	}
}
