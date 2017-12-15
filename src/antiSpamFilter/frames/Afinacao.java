package antiSpamFilter.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import antiSpamFilter.utils.GuiUtils;
import antiSpamFilter.utils.Utils;

/**
 * Classe respons�vel por determinar o comportamento da aplica��o no que
 * respeita � funcionalidade de afina��o autom�tica do filtro anti-spam,
 * incluindo suporte � contagem dos falsos positivos e falsos negativos e ao
 * display da configura��o do vetor de pesos
 * 
 * @author Ana Pestana, Diogo Reis, Guilherme Azevedo, Rafael Costa
 *
 */
public class Afinacao {

	private static JFrame afinacao;

	/**
	 * Construtor da p�gina de Afina��o Autom�tica
	 */
	public Afinacao() {
		afinacao = new JFrame();
		afinacao.setTitle("Afina��o autom�tica do filtro anti-spam");

		Utils.readConfigFiles();

		addContents();

		calculate_FP_FN();

		afinacao.pack();
		afinacao.setSize(750, 600);
		afinacao.setResizable(false);
		afinacao.addWindowListener(new GuiUtils.AfinacaoAutomaticaClose());
		GuiUtils.frameAtCenter(afinacao);
	}

	/**
	 * Adiciona os conte�dos � janela de Afina��o Autom�tica
	 */
	private void addContents() {
		JPanel panel = new JPanel();

		panel.add(createButtons(GuiUtils.constructGUI(panel, false)), BorderLayout.SOUTH);

		afinacao.add(panel);
	}

	/**
	 * Cria os bot�es generate, save e cancel num painel (buttons_panel)
	 * 
	 * @param center_panel
	 *            Painel atualizado pela gera��o aleat�ria
	 * @return Painel com os but�es (buttons_panel)
	 */
	private JPanel createButtons(JPanel center_panel) {
		JPanel buttons_panel = new JPanel();
		buttons_panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton calculate = new JButton("Confirmar altera��es");
		calculate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GuiUtils.checkValues()) {
					for (Entry<String, JTextField> entry : GuiUtils.rules_values.entrySet())
						Utils.rules_weights.put(entry.getKey(), Double.valueOf(entry.getValue().getText()));
					calculate_FP_FN();
				} else {
					JOptionPane.showMessageDialog(afinacao,
							"Os valores das rules n�o est�o corretos, s� s�o aceites valores entre -5 e 5",
							"Configura��o dos ficheiros", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		buttons_panel.add(calculate);

		JButton save = new JButton("Guardar");
		save.addActionListener(new ActionListener() {
			/*
			 * Sentinela no but�o 'Guardar' respons�vel por guardar a
			 * configura��o e retornar � Home Page
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GuiUtils.checkValues()) {
					try {
						FileWriter w = new FileWriter(Utils.config_files_path[0], false);
						for (Entry<String, JTextField> entry : GuiUtils.rules_values.entrySet())
							w.write(entry.getKey() + " " + entry.getValue().getText() + GuiUtils.newLine);
						w.close();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(afinacao,
								"N�o foi poss�vel prosseguir! O ficheiro rules.cf est� a ser editado.",
								"Configura��o dos ficheiros", JOptionPane.WARNING_MESSAGE);
						System.exit(1);
					}
					backHome();
				} else {
					JOptionPane.showMessageDialog(afinacao,
							"Os valores das rules n�o est�o corretos, s� s�o aceites valores entre -5 e 5",
							"Configura��o dos ficheiros", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		buttons_panel.add(save);

		JButton cancel = new JButton("Cancelar");
		cancel.addActionListener(new ActionListener() {
			/*
			 * Sentinela no but�o 'Cancelar' respons�vel por retornar � Home
			 * Page quando o bot�o � pressionado
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				backHome();
			}
		});
		buttons_panel.add(cancel);

		return buttons_panel;
	}

	/**
	 * Atribui novos pesos de forma aleat�ria com valores entre -5 e 5 �s regras
	 * mapeadas no HashMap
	 */
	public static void changeWeights() {
		for (HashMap.Entry<String, Double> entry : Utils.rules_weights.entrySet())
			entry.setValue((Math.random() * 10) - 5);
		calculate_FP_FN();
	}

	/**
	 * Procede ao c�lculo, por invoca��o, do n�mero de Falsos Positivos e Falsos
	 * Negativos
	 */
	private static void calculate_FP_FN() {
		int decimal_places = String.valueOf(Utils.hamLogRules.size()).length();
		GuiUtils.help_label_fp.setText("  Falsos Positivos (FP):  "
				+ String.format("%0" + decimal_places + "d", Utils.falses(true)) + " / " + Utils.hamLogRules.size());
		decimal_places = String.valueOf(Utils.spamLogRules.size()).length();
		GuiUtils.help_label_fn.setText("  Falsos Negativos (FN):  "
				+ String.format("%0" + decimal_places + "d", Utils.falses(false)) + " / " + Utils.spamLogRules.size());
	}

	/**
	 * Descarta a GUI atual e retorna � Home Page
	 */
	public static void backHome() {
		afinacao.dispose();
		HomePage.visible(true);
	}

	/**
	 * Define a visibilidade da frame de Afina��o Autom�tica
	 * 
	 * @param setVisible
	 */
	public void visible(boolean setVisible) {
		afinacao.setVisible(setVisible);
	}

	/**
	 * Lan�a uma nova janela de Afina��o Autom�tica
	 */
	public static void launch() {
		new Afinacao().visible(true);
	}

	public static void update() {
		afinacao.validate();
		afinacao.repaint();
	}
}
