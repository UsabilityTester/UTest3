package at.ac.tuwien.big.we14.lab2.api.impl;

import at.ac.tuwien.big.we14.lab2.api.Choice;
import at.ac.tuwien.big.we14.lab2.api.QuizKi;

public class StochasticKi implements QuizKi {

	@Override
	public boolean getNextAnswer(Choice[] choice) {
		return Math.random()>=0.5;
	}

}
