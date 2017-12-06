package antiSpamFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import antiSpamFilter.utils.Utils;

public class AntiSpamFilterProblem extends AbstractDoubleProblem {

	/**
	 * default
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> rules_list;

	public AntiSpamFilterProblem() {
		/*
		 * Gera um vetor de pesos com tamanho igual ao número de regras
		 * existentes
		 */
		this(Utils.lines(Utils.config_files_path[0]).size());
		rules_list = new ArrayList<>(Utils.rules_weights.keySet());
		Collections.sort(rules_list);
	}

	public AntiSpamFilterProblem(Integer numberOfVariables) {
		setNumberOfVariables(numberOfVariables);
		setNumberOfObjectives(2);
		setName("AntiSpamFilterProblem");

		List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
		List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

		for (int i = 0; i < getNumberOfVariables(); i++) {
			lowerLimit.add(-5.0);
			upperLimit.add(5.0);
		}

		setLowerLimit(lowerLimit);
		setUpperLimit(upperLimit);
	}

	public void evaluate(DoubleSolution solution) {		
		for (int i = 0; i < solution.getNumberOfVariables(); i++)
			Utils.rules_weights.put(rules_list.get(i), solution.getVariableValue(i));

		solution.setObjective(0, Utils.falses(true));
		solution.setObjective(1, Utils.falses(false));
	}
}
