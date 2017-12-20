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
 * Classe responsável por determinar o comportamento da aplicação no que
 * respeita à funcionalidade de afinação automática do filtro anti-spam,
 * incluindo suporte à contagem dos falsos positivos e falsos negativos e ao
 * display editável da configuração do vetor de pesos
 * 
 * @author Ana Pestana, Diogo Reis, Guilherme Azevedo, Rafael Costa
 *
 */
public class Afinacao {

    private static JFrame afinacao;

    /**
     * Construtor da classe Afinação
     */
    public Afinacao() {
	afinacao = new JFrame();
	afinacao.setTitle("Afinação do filtro anti-spam");

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
     * Adiciona os conteúdos à janela de Afinação
     */
    private void addContents() {
	JPanel panel = new JPanel();

	panel.add(createButtons(GuiUtils.constructGUI(panel, false)), BorderLayout.SOUTH);

	afinacao.add(panel);
    }

    /**
     * Cria os butões confirmButton, saveButton e cancelButton num painel
     * (buttonsPanel)
     * 
     * @param centerPanel
     *            Painel atualizado pela geração aleatória
     * @return Painel com os butões (buttonsPanel)
     */
    private JPanel createButtons(JPanel centerPanel) {
	JPanel buttonsPanel = new JPanel();
	buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

	JButton confirmButton = new JButton("Confirmar alterações");
	confirmButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (GuiUtils.checkValues()) {
		    for (Entry<String, JTextField> entry : GuiUtils.rulesValues.entrySet())
			Utils.rulesWeights.put(entry.getKey(), Double.valueOf(entry.getValue().getText()));
		    calculateFpAndFn();
		} else {
		    JOptionPane.showMessageDialog(afinacao,
			    "Os valores das rules não estão corretos, só são aceites valores entre -5 e 5",
			    "Configuração dos ficheiros", JOptionPane.WARNING_MESSAGE);
		}
	    }
	});
	buttonsPanel.add(confirmButton);

	JButton saveButton = new JButton("Guardar");
	saveButton.addActionListener(new ActionListener() {
	    /*
	     * Sentinela no butão 'Guardar' responsável por guardar a
	     * configuração e retornar à Home Page
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
				"Não foi possível prosseguir! O ficheiro rules.cf está a ser editado.",
				"Configuração dos ficheiros", JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		    }
		    backHome();
		} else {
		    JOptionPane.showMessageDialog(afinacao,
			    "Os valores das rules não estão corretos, só são aceites valores entre -5 e 5",
			    "Configuração dos ficheiros", JOptionPane.WARNING_MESSAGE);
		}
	    }
	});
	buttonsPanel.add(saveButton);

	JButton cancelButton = new JButton("Cancelar");
	cancelButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		backHome();
	    }
	});
	buttonsPanel.add(cancelButton);

	return buttonsPanel;
    }

    /**
     * Atribui novos pesos de forma aleatória com valores entre -5 e 5 às regras
     * mapeadas no HashMap
     */
    public static void changeWeights() {
	for (HashMap.Entry<String, Double> entry : Utils.rulesWeights.entrySet())
	    entry.setValue((Math.random() * 10) - 5);
	calculateFpAndFn();
    }

    /**
     * Procede ao cálculo, por invocação, do número de Falsos Positivos e Falsos
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
     * Descarta a GUI atual e retorna à Home Page
     */
    public static void backHome() {
	afinacao.dispose();
	HomePage.visible(true);
    }

    /**
     * Define a visibilidade da frame de Afinação
     * 
     * @param setVisible
     * 
     * @see JFrame#setVisible(boolean)
     */
    public void visible(boolean setVisible) {
	afinacao.setVisible(setVisible);
    }

    /**
     * Lança uma nova janela de Afinação
     */
    public static void launch() {
	new Afinacao().visible(true);
    }

    /**
     * Atualiza os conteúdos da janela de Afinação quando os pesos são alterados
     */
    public static void update() {
	afinacao.validate();
	afinacao.repaint();
    }
}
