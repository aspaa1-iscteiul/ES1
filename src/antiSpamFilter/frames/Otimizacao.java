package antiSpamFilter.frames;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Executors;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import antiSpamFilter.AntiSpamFilterAutomaticConfiguration;
import antiSpamFilter.utils.GuiUtils;
import antiSpamFilter.utils.Utils;

/**
 * Classe responsável por determinar o comportamento da aplicação no que
 * respeita à funcionalidade de otimização do filtro anti-spam, incluindo
 * suporte à contagem dos falsos positivos e falsos negativos e ao display da
 * configuração ótima do vetor de pesos
 * 
 * @author Ana Pestana, Guilherme Azevedo
 */

public class Otimizacao {

	private static JFrame otimização, progressFrame;
	private static final String algorithmOutputFilesPath = "./experimentBaseDirectory/referenceFronts/AntiSpamFilterProblem.r";

	/**
	 * Construtor da classe Otimização <br>
	 * Display de uma progressBar enquanto aguarda pelo output do algoritmo
	 * NSGAII
	 */
	public Otimizacao() {
		JProgressBar progressBar = new JProgressBar();
		progressBar.setString("A calcular...");
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
		progressFrame = new JFrame();
		progressFrame.add(progressBar);
		GuiUtils.frameAtCenter(progressFrame);
		progressFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		progressFrame.pack();
		progressFrame.setResizable(false);
		progressFrame.setAlwaysOnTop(true);
		progressFrame.setVisible(true);

		Executors.newSingleThreadExecutor().execute(executeAlgorithm());
	}

	/**
	 * Construtor da GUI da página de Afinação
	 */
	private void constructFrame() {
		otimização = new JFrame();
		otimização.setTitle("Otimização do filtro anti-spam");

		addContents();

		otimização.setSize(750, 600);
		otimização.setResizable(false);
		otimização.addWindowListener(new GuiUtils.OtimizacaoClose());
		GuiUtils.frameAtCenter(otimização);
		otimização.setVisible(true);
	}

	/**
	 * Invoca o procedimento runAlgoritm() da classe
	 * AntiSpamFilterAutomaticConfiguration e compila e abre os
	 * ficheiros HV.Boxplot e AntiSpamStudy
	 * 
	 * @return do runnable que invoca o procedimento runAlgoritm() da
	 *         classe AntiSpamFilterAutomaticConfiguration e compila e
	 *         abre os ficheiros HV.Boxplot e AntiSpamStudy
	 */
	private Runnable executeAlgorithm() {
		return new Runnable() {
			@Override
			public void run() {
				try {
					AntiSpamFilterAutomaticConfiguration.runAlgorithm();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(progressFrame,
							"Ocorreu um problema durante a execução da framework JMetal", "Erro",
							JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							ProcessBuilder processBuilder = new ProcessBuilder(
									new String[] { "Rscript", "HV.Boxplot.R" });
							processBuilder.directory(
									new File("./experimentBaseDirectory/AntiSpamStudy/R/").getAbsoluteFile());
							processBuilder.start().waitFor();

							Desktop.getDesktop()
									.open(new File("./experimentBaseDirectory/AntiSpamStudy/R/HV.Boxplot.eps"));
						} catch (IOException | InterruptedException e) {
							new GuiUtils.RException(progressFrame);
						}
						try {
							ProcessBuilder processBuilder = new ProcessBuilder(
									new String[] { "pdflatex", "AntiSpamStudy.tex" });
							processBuilder.directory(
									new File("./experimentBaseDirectory/AntiSpamStudy/latex/").getAbsoluteFile());
							processBuilder.start().waitFor();

							Desktop.getDesktop()
									.open(new File("./experimentBaseDirectory/AntiSpamStudy/latex/AntiSpamStudy.pdf"));
						} catch (InterruptedException | IOException e) {
							new GuiUtils.LatexException(progressFrame);
						}
						constructFrame();
						readAlgorithmOutputs();
						progressFrame.dispose();
					}
				});
			}
		};
	}

	/**
	 * Recupera a melhor solução produzida pelo algoritmo genético NSGAII
	 */
	private void readAlgorithmOutputs() {
		ArrayList<String> lines = Utils.lines(algorithmOutputFilesPath + "f");
		int fp = Integer.MAX_VALUE, fn = Integer.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < lines.size(); i++) {
			String[] values = lines.get(i).split(" ");
			double fpAux = Double.valueOf(values[0]), fnAux = Double.valueOf(values[1]);
			if (fpAux < fp || (fpAux == fp && fnAux < fn)) {
				fp = (int) fpAux;
				fn = (int) fnAux;
				index = i;
			}
		}
		lines = Utils.lines(algorithmOutputFilesPath + "s");
		String[] values = lines.get(index).split(" ");
		ArrayList<String> rulesList = new ArrayList<>(Utils.rulesWeights.keySet());
		Collections.sort(rulesList);
		for (int i = 0; i < values.length; i++)
			Utils.rulesWeights.put(rulesList.get(i), Double.valueOf(values[i]));

		int decimalPlaces = String.valueOf(Utils.hamLogRules.size()).length();
		GuiUtils.helpLabelFp.setText("  Falsos Positivos (FP):  " + String.format("%0" + decimalPlaces + "d", (int) fp)
				+ " / " + Utils.hamLogRules.size());
		decimalPlaces = String.valueOf(Utils.spamLogRules.size()).length();
		GuiUtils.helpLabelFn.setText("  Falsos Negativos (FN):  " + String.format("%0" + decimalPlaces + "d", (int) fn)
				+ " / " + Utils.spamLogRules.size());
	}

	/**
	 * Adiciona os conteúdos à janela de Otimização
	 */
	private void addContents() {
		JPanel panel = new JPanel();
		GuiUtils.constructGUI(panel, true);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton saveButton = new JButton("Guardar");
		saveButton.addActionListener(new ActionListener() {
			/*
			 * Sentinela no butão 'Guardar' responsável por guardar a
			 * configuração e retornar à Home Page
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileWriter writer = new FileWriter(Utils.configFilesPaths[0], false);
					for (HashMap.Entry<String, Double> entry : Utils.rulesWeights.entrySet())
						writer.write(entry.getKey() + " " + entry.getValue().toString() + GuiUtils.newLine);
					writer.close();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(otimização,
							"Não foi possível prosseguir! O ficheiro rules.cf está a ser editado.",
							"Configuração dos ficheiros", JOptionPane.WARNING_MESSAGE);
					System.exit(1);
				}
				backHome();
			}
		});

		JButton cancelButton = new JButton("Cancelar");
		cancelButton.addActionListener(new ActionListener() {
			/*
			 * Sentinela no butão 'Cancelar' responsável por retornar à Home
			 * Page quando o botão é pressionado
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				backHome();
			}
		});

		buttonsPanel.add(saveButton);
		buttonsPanel.add(cancelButton);
		panel.add(buttonsPanel, BorderLayout.SOUTH);

		otimização.add(panel);
	}

	/**
	 * Descarta a GUI atual e retorna à Home Page
	 */
	public static void backHome() {
		otimização.dispose();
		HomePage.visible(true);
	}

	/**
	 * Lança uma nova janela de Otimização
	 */
	public static void launch() {
		new Otimizacao();
	}

}
