package antiSpamFilter;

import javax.swing.JButton;
import javax.swing.JFileChooser;

public class fileChooser {
	
	public static void main(String[] args){
		JButton open = new JButton();
		JFileChooser fc = new JFileChooser();
		// Inicia a GUI na diretoria do projeto
		fc.setCurrentDirectory(new java.io.File(".")); 
		fc.setDialogTitle("Selecionar ficheiro");
		if(fc.showOpenDialog(open)== JFileChooser.APPROVE_OPTION){
			//
		}
		System.out.println(fc.getSelectedFile().getAbsolutePath());
		
	}
	
}