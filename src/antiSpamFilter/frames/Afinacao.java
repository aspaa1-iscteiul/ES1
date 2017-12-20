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
 * display edit�vel da configura��o do vetor de pesos
 * 
 * @author Ana Pestana, Diogo Reis, Guilherme Azevedo, Rafael Costa
 *
 */
public class Afinacao {

	private static JFrame afinacao;

	/**
	 * Construtor da classe Afina��o
	 */
	public Afinacao() {
		afinacao = new JFrame();
		afinacao.setTitle("Afina��o do filtro anti-spam");

		Utils.readConfigFiles();

		addContents();

		calculateFpAndFn();

		afinacao.pack();
		afinacao.setSize(750, 600);
		afinacao.setResizable(false);
		afinacao.addWindowListener(new GuiUtils.AfinacaoAutomaticaClose());
		GuiUtils.frameAtCenter(afinacao);
	}

	/**
	 * Adiciona os conte�dos � janela de Afina��o
	 */
	private void addContents() {
		JPanel panel = new JPanel();

		panel.add(createButtons(GuiUtils.constructGUI(panel, false)), BorderLayout.SOUTH);

		afinacao.add(panel);
	}

	/**
	 * Cria os but�es confirmButton, saveButton e cancelButton num painel (buttonsPanel)
	 * 
	 * @param centerPanel
	 *            Painel atualizado pela gera��o aleat�ria
	 * @return Painel com os but�es (buttonsPanel)
	 */
	private JPanel createButtons(JPanel centerPanel) {
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton confirmButton = new JButton("Confirmar altera��es");
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GuiUtils.checkValues()) {
					for (Entry<String, JTextField> entry : GuiUtils.rulesValues.entrySet())
						Utils.rulesWeights.put(entry.getKey(), Double.valueOf(entry.getValue().getText()));
					calculateFpAndFn();
				} else {
					JOptionPane.showMessageDialog(afinacao,
							"Os valores das rules n�o est�o corretos, s� s�o aceites valores entre -5 e 5",
							"Configura��o dos ficheiros", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		buttonsPanel.add(confirmButton);

		JButton saveButton = new JButton("Guardar");
		saveButton.addActionListener(new ActionListener() {
			/*
			 * Sentinela no but�o 'Guardar' respons�vel por guardar a
			 * configura��o e retornar � Home Page
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GuiUtils.checkValues()) {
					try {
						FileWriter writer = new FileWriter(Utils.configFilesPaths[0], false);
						for (Entry<String, JTextField> entry : GuiUtils.rulesValues.entrySet())
							writer.write(entry.getKey() + " " + entry.getValue().getText() + GuiUtils.newLine);
						writer.close();
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
		buttonsPanel.add(saveButton);

		JButton cancelButton = new JButton("Cancelar");
		cancelButton.addActionListener(new ActionListener() {
			/*
			 * Sentinela no but�o 'Cancelar' respons�vel por retornar � Home
			 * Page quando o bot�o � pressionado
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				backHome();
			}
		});
		buttonsPanel.add(cancelButton);

		return buttonsPanel;
	}

	/**
	 * Atribui novos pesos de forma aleat�ria com valores entre -5 e 5 �s regras
	 * mapeadas no HashMap
	 */
	public static void changeWeights() {
		for (HashMap.Entry<String, Double> entry : Utils.rulesWeights.entrySet())
			entry.setValue((Math.random() * 10) - 5);
		calculateFpAndFn();
	}

	/**
	 * Procede ao c�lculo, por invoca��o, do n�mero de Falsos Positivos e Falsos
	 * Negativos
	 */
	private static void calculateFpAndFn() {
		int decimalPlaces = String.valueOf(Utils.hamLogRules.size()).length();
		GuiUtils.helpLabelFp.setText("  Falsos Positivos (FP):  "
				+ String.format("%0" + decimalPlaces + "d", Utils.falses(true)) + " / " + Utils.hamLogRules.size());
		decimalPlaces = String.valueOf(Utils.spamLogRules.size()).length();
		GuiUtils.helpLabelFn.setText("  Falsos Negativos (FN):  "
				+ String.format("%0" + decimalPlaces + "d", Utils.falses(false)) + " / " + Utils.spamLogRules.size());
	}

	/**
	 * Descarta a GUI atual e retorna � Home Page
	 */
	public static void backHome() {
		afinacao.dispose();
		HomePage.visible(true);
	}

	/**
	 * Define a visibilidade da frame de Afina��o
	 * 
	 * @param setVisible
	 * 
	 * @see JFrame#setVisible(boolean)
	 */
	public void visible(boolean setVisible) {
		afinacao.setVisible(setVisible);
	}

	/**
	 * Lan�a uma nova janela de Afina��o
	 */
	public static void launch() {
		new Afinacao().visible(true);
	}

	/**
	 * Atualiza os conte�dos da janela de Afina��o quando os pesos s�o alterados
	 */
	public static void update() {
		afinacao.validate();
		afinacao.repaint();
	}
}
