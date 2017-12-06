package antiSpamFilter.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import antiSpamFilter.utils.GuiUtils;
import antiSpamFilter.utils.Utils;

/**
 * Classe responsável por determinar o comportamento da aplicação no que
 * respeita à funcionalidade de afinação automática do filtro anti-spam,
 * incluindo suporte à contagem dos falsos positivos e falsos negativos e ao
 * display da configuração do vetor de pesos
 * 
 * @author Ana Pestana, Diogo Reis, Guilherme Azevedo, Rafael Costa
 *
 */
public class AfinacaoAutomatica {

	private static JFrame afinacaoAuto;

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
		afinacaoAuto.addWindowListener(new GuiUtils.AfinacaoAutomaticaClose());
		GuiUtils.frameAtCenter(afinacaoAuto);
	}

	/**
	 * Adiciona os conteúdos à janela de Afinação Automática
	 */
	private void addContents() {
		JPanel panel = new JPanel();

		panel.add(createButtons(GuiUtils.constructGUI(panel)), BorderLayout.SOUTH);

		afinacaoAuto.add(panel);
	}

	private JPanel createButtons(JPanel center_panel) {
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
				center_panel.remove(GuiUtils.scroll_rules_panel);
				GuiUtils.createRulesPanel();
				center_panel.add(GuiUtils.scroll_rules_panel, BorderLayout.CENTER);
				afinacaoAuto.validate();
				afinacaoAuto.repaint();
			}
		});
		buttons_panel.add(generate);

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
		buttons_panel.add(save);

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
		buttons_panel.add(cancel);

		return buttons_panel;
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
		GuiUtils.help_label_fp.setText("  Falsos Positivos (FP):  "
				+ String.format("%0" + decimal_places + "d", Utils.falses(true)) + " / " + Utils.hamLogRules.size());
		decimal_places = String.valueOf(Utils.spamLogRules.size()).length();
		GuiUtils.help_label_fn.setText("  Falsos Negativos (FN):  "
				+ String.format("%0" + decimal_places + "d", Utils.falses(false)) + " / " + Utils.spamLogRules.size());
	}

	/**
	 * Descarta a GUI atual e retorna à Home Page
	 */
	public static void backHome() {
		afinacaoAuto.dispose();
		HomePage.visible(true);
	}

	/**
	 * Define a visibilidade da frame de Afinação Automática
	 * 
	 * @param setVisible
	 */
	public void visible(boolean setVisible) {
		afinacaoAuto.setVisible(setVisible);
	}

	/**
	 * Lança uma nova janela de Afinação Automática
	 */
	public static void launch() {
		new AfinacaoAutomatica().visible(true);
	}
}
