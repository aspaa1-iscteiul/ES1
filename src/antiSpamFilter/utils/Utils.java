package antiSpamFilter.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

	public static String[] rules(String rules_path) {
		List<String> rules = new ArrayList<>();
		try {
			Scanner scn = new Scanner(new File(rules_path));
			while (scn.hasNextLine()) {
				String line = scn.nextLine();
				if (!line.equals(""))
					rules.add(line);
			}
			scn.close();
		} catch (FileNotFoundException e) { // if is not a file
		}
		return rules.toArray(new String[0]);
	}

}
