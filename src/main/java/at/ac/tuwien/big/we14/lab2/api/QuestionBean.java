package at.ac.tuwien.big.we14.lab2.api;

import java.util.List;

public interface QuestionBean {
	
	void setAnswers(boolean[] answers);
	void setLeftAnswerTime(long leftTime);
	
	void doLogic();
	void next();
	String getQuestionText();
	String getChoice(int index);
	int getChoiceCount();
	List<Choice> getChoices();
	String getCategory();
	long getQuestionTime();
	short getPlayerQuestionScore(short playerIndex, short questionIndex);
	short getPlayerScore(short playerIndex);
	String getRoundOutcome();
	String getGameOutcome();
	boolean isLastQuestionOfRound();
	boolean isLastRound();

}
