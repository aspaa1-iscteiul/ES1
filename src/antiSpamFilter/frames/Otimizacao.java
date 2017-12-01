package antiSpamFilter.frames;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.concurrent.Executor;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import antiSpamFilter.AntiSpamFilterAutomaticConfiguration;

public class Otimizacao {

	private static JFrame frame;

	public Otimizacao() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		calculateBest();
	}

	private void calculateBest() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JProgressBar progress = new JProgressBar();
		progress.setString("A calcular...");
		progress.setStringPainted(true);
		progress.setIndeterminate(true);

		frame.add(progress, BorderLayout.NORTH);
		frame.setSize(300, 200);
		frame.setVisible(true);

		Executor executor = java.util.concurrent.Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					AntiSpamFilterAutomaticConfiguration.algorithm();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// now fix the progress bar
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						progress.setIndeterminate(false);
						progress.setVisible(false);
					}
				});
			}
		});
	}

	private void visible(boolean visible) {
		frame.setVisible(visible);
	}

	public static void launch() {
		new Otimizacao().visible(true);
	}

}
