package at.ac.tuwien.big.we14.lab2.api;

import java.util.List;

public interface QuestionManager {
	
	Category getCurrentCategory();
	void nextCategory();
	Question getCurrentQuestion();
	List<Choice> getChoices();
	void nextQuestion();
	boolean checkAnswer(boolean[] answers);
}
