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
 * suporte à contagem dos falsos positivos e falsos negativos, ao display da
 * configuração do vetor de pesos e do lançamento das aplicações externas para
 * compilação e visualização do gráfico do indicador de qualidade.
 * 
 * @author Ana Pestana, Diogo Reis, Guilherme Azevedo, Rafael Costa
 *
 */
public class Otimizacao {

	private static JFrame frame, progressFrame;
	private static final String algorithmOutputFilesPath = "./experimentBaseDirectory/referenceFronts/AntiSpamFilterProblem.r";

	/**
	 * Construtor da página de Otimização
	 */
	public Otimizacao() {
		optimize();
	}

	/**
	 * Construtor da GUI
	 */
	private void constructFrame() {
		frame = new JFrame();
		frame.setTitle("Otimização do filtro anti-spam");

		addContents();

		frame.setSize(750, 600);
		frame.setResizable(false);
		frame.addWindowListener(new GuiUtils.OtimizacaoClose());
		GuiUtils.frameAtCenter(frame);
		visible(true);
	}

	/**
	 * Display da progressBar
	 */
	private void optimize() {
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
	 * Execução do algoritmo genético de otimização, por invocação.
	 * 
	 * @return Thread que corre o algoritmo
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
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							ProcessBuilder builder = new ProcessBuilder(new String[] { "Rscript", "./HV.Boxplot.R" });
							builder.directory(new File("./experimentBaseDirectory/AntiSpamStudy/R/").getAbsoluteFile());
							builder.start().waitFor();
							File output = new File("./experimentBaseDirectory/AntiSpamStudy/R/HV.Boxplot.eps");
							if (output.exists())
								Desktop.getDesktop().open(output);
							else
								JOptionPane.showMessageDialog(progressFrame,
										"Ocorreu um problema ao compilar com o Rscript.exe", "Indicador Hypervolume",
										JOptionPane.ERROR_MESSAGE);

						} catch (IOException | InterruptedException e) {
							String message = "Ocorreu um problema ao compilar com o Rscript.exe" + GuiUtils.newLine
									+ GuiUtils.newLine
									+ "Sugestão: Por favor, verifique que o path para o executável Rscript.exe se encontra incluído na variável de ambiente PATH"
									+ GuiUtils.newLine;
							JOptionPane.showMessageDialog(progressFrame, message, "Indicador Hypervolume",
									JOptionPane.ERROR_MESSAGE);
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
	 * Leitura dos valores ótimos devolvidos pelo algoritmo genético
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
		ArrayList<String> rulesList = new ArrayList<>(Utils.rules_weights.keySet());
		Collections.sort(rulesList);
		for (int i = 0; i < values.length; i++)
			Utils.rules_weights.put(rulesList.get(i), Double.valueOf(values[i]));

		int decimal_places = String.valueOf(Utils.hamLogRules.size()).length();
		GuiUtils.help_label_fp.setText("  Falsos Positivos (FP):  "
				+ String.format("%0" + decimal_places + "d", (int) fp) + " / " + Utils.hamLogRules.size());
		decimal_places = String.valueOf(Utils.spamLogRules.size()).length();
		GuiUtils.help_label_fn.setText("  Falsos Negativos (FN):  "
				+ String.format("%0" + decimal_places + "d", (int) fn) + " / " + Utils.spamLogRules.size());
	}

	/**
	 * Adiciona os conteúdos à janela de Otimização
	 */
	private void addContents() {
		JPanel panel = new JPanel();
		GuiUtils.constructGUI(panel);

		JPanel buttons_panel = new JPanel();
		buttons_panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
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
						w.write(entry.getKey() + " " + entry.getValue().toString() + GuiUtils.newLine);
					w.close();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame,
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

		buttons_panel.add(save);
		buttons_panel.add(cancel);
		panel.add(buttons_panel, BorderLayout.SOUTH);

		frame.add(panel);
	}

	/**
	 * Descarta a GUI atual e retorna à Home Page
	 */
	public static void backHome() {
		frame.dispose();
		HomePage.visible(true);
	}

	/**
	 * Define a visibilidade da frame de Otimização
	 * 
	 * @param setVisible
	 */
	private void visible(boolean visible) {
		frame.setVisible(visible);
	}

	/**
	 * Lança uma nova janela de Otimização
	 */
	public static void launch() {
		new Otimizacao();
	}

}
