package at.ac.tuwien.big.we14.lab2.api.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.ac.tuwien.big.we14.lab2.api.Category;
import at.ac.tuwien.big.we14.lab2.api.Choice;
import at.ac.tuwien.big.we14.lab2.api.Question;
import at.ac.tuwien.big.we14.lab2.api.QuestionBean;
import at.ac.tuwien.big.we14.lab2.api.QuestionManager;
import at.ac.tuwien.big.we14.lab2.api.QuizFactory;
import at.ac.tuwien.big.we14.lab2.api.QuizKi;

public class DefaultQuestionBean implements QuestionBean {
	private static final Logger logger = Logger.getLogger(DefaultQuestionBean.class.getName());
	
	private int currentQuestion;
	private int currentRound;
	private short[][][] playerScores;
	private boolean[] answers;
	private long[][] playerRoundTime;
	private long leftAnswerTime;
	
	private final QuestionManager questionManager;
	private final QuizKi quizKI;
	
	public DefaultQuestionBean() {
		currentQuestion = 0;
		currentRound = 0;
		// 2 players in 5 rounds with 3 questions
		playerScores = new short[2][5][3];
		playerRoundTime = new long[2][5];
		answers = null;
		
		for(int player = 0; player < playerScores.length; player++) {
			for(int round = 0; round < playerScores[player].length; round++) {
				for(int question = 0; question < playerScores[player][round].length; question++) {
					//Set all values to uninitialized
					playerScores[player][round][question] = 2;
				}
			}
		}
		
		questionManager = QuizFactory.INSTANCE.createQuestionManager();
		quizKI = QuizFactory.INSTANCE.createQuizKi();
	}

	@Override
	public void setAnswers(boolean[] answers) {
		this.answers = answers;
	}
	
	@Override
	public void setLeftAnswerTime(long leftTime) {
		leftAnswerTime = leftTime;
	}
	
	@Override
	public void next() {
		currentQuestion++;
		questionManager.nextQuestion();
		
		if(currentQuestion > 2) {
			currentQuestion = 0;
			currentRound++;
			questionManager.nextCategory();
		}
	}

	@Override
	public void doLogic() {
		if(answers == null) {
			return;
		}
		
		//check answers
		if(questionManager.checkAnswer(answers)) {
			playerScores[0][currentRound][currentQuestion] = 1;
		} else {
			playerScores[0][currentRound][currentQuestion] = 0;
		}
		
		playerRoundTime[0][currentRound] += questionManager.getCurrentQuestion().getMaxTime() - leftAnswerTime;
		
		if(quizKI.getNextAnswer(questionManager.getChoices().toArray(new Choice[0]))) {
			playerScores[1][currentRound][currentQuestion] = 1;
		} else {
			playerScores[1][currentRound][currentQuestion] = 0;
		}
		
		playerRoundTime[1][currentRound] += Math.round(questionManager.getCurrentQuestion().getMaxTime()*Math.random());
	}

	@Override
	public String getQuestionText() {
		Question question = questionManager.getCurrentQuestion();
		return question == null ? "-" : question.getText();
	}

	@Override
	public String getChoice(int index) {
		List<Choice> choiceList = questionManager.getChoices();
		
		if(index < 0 || choiceList.size() <= index) {
			logger.warning("The given index(" + index + ") for the choice is out of bounds!");
			return "-";
		}
		
		Choice choice = choiceList.get(index);
		return choice == null ? "-" : choice.getText();
	}
	
	@Override
	public int getChoiceCount() {
		List<Choice> choiceList = questionManager.getChoices();
		return choiceList.size();
	}
	
	@Override
	public List<Choice> getChoices() {
		return questionManager.getChoices();
	}

	@Override
	public String getCategory() {
		Category category = questionManager.getCurrentCategory();
		return category == null ? "-" : category.getName();
	}

	@Override
	public long getQuestionTime() {
		Question question = questionManager.getCurrentQuestion();
		return question == null ? 20000L : question.getMaxTime();
	}

	@Override
	public short getPlayerQuestionScore(short playerIndex, short questionIndex) {
		
		if(
				playerIndex >= 0 && playerIndex < 2 &&
				currentRound >= 0 && currentRound < 5 &&
				questionIndex >= 0 && questionIndex < 3) {
			
			return playerScores[playerIndex][currentRound][questionIndex];
		} else {
			logger.log(Level.WARNING, "Index out of bounds: playerIndex 0 <= " + playerIndex +
					" < 2, currentRound 0 <= " + currentRound +
					" < 5, questionIndex 0 <= " + questionIndex + " < 3");
			
			return -1;
		}
	}

	@Override
	public short getPlayerScore(short playerIndex) {
		
		if(playerIndex >= 0 && playerIndex < 2) {
			short score = 0;
			
			for(short i = 0; i < playerScores[playerIndex].length; i++) {
				if(hasPlayerWonRound(playerIndex, i))
					score += 1;
			}
			
			return score;
		} else {
			logger.log(Level.WARNING, "Index out of bounds: playerIndex 0 <= " + playerIndex + " < 2");
			
			return -1;
		}
	}
	
	private boolean hasPlayerWonRound(short playerIndex, short roundIndex) {
		
		if(
				playerIndex >= 0 && playerIndex < 2 &&
				roundIndex >= 0 && roundIndex < 5) {
			
			return getRoundWinningPlayer(roundIndex) == playerIndex;
		} else {
			logger.log(Level.WARNING, "Index out of bounds: playerIndex 0 <= " + playerIndex +
					" < 2, currentRound 0 <= " + roundIndex + " < 5");
			
			return false;
		}
	}
	
	private int getRoundWinningPlayer(int roundIndex) {
		if(roundIndex >= 0 && roundIndex < 5) {
			
			int bestPlayerPoints = Integer.MIN_VALUE;
			int bestPlayerIndex = -1;
			
			for(int player = 0; player < playerScores.length; player++) {
				int currentPlayerPoints = 0;
				for(int question = 0; question < playerScores[player][roundIndex].length; question++) {
					if(playerScores[player][roundIndex][question] == 2) {
						//A question has not been answered yet -> round is not finished
						return -1;
					} else if(playerScores[player][roundIndex][question] == 1) {
						currentPlayerPoints++;
					}
				}
				
				if(currentPlayerPoints > bestPlayerPoints) {
					bestPlayerIndex = player;
					bestPlayerPoints = currentPlayerPoints;
				} else if(currentPlayerPoints == bestPlayerPoints) {
					bestPlayerIndex = -1;
				}
			}
			
			if(bestPlayerIndex >= 0)
				return bestPlayerIndex;
			
			//There is no best player -> go for player with the least time
			long bestPlayerTime = Long.MAX_VALUE;
			
			for(int player = 0; player < playerRoundTime.length; player++) {
				if(bestPlayerTime > playerRoundTime[player][roundIndex]) {
					bestPlayerIndex = player;
					bestPlayerTime = playerRoundTime[player][roundIndex];
				}
			}
			
			return bestPlayerIndex;
		} else {
			logger.log(Level.WARNING, "Index out of bounds:  currentRound 0 <= " + roundIndex + " < 5");
			
			return -1;
		}
	}

	@Override
	public String getRoundOutcome() {
		int winningIndex = getRoundWinningPlayer(currentRound);
		
		if(winningIndex < 0) {
			return "Kein Spieler hat gewonnen!";
		} else {
			return "Spieler " + (1 + winningIndex) + " hat gewonnen!";
		}
	}
	
	private int getGameWinningPlayer() {
		int[] playerWins = new int[playerScores.length];
		
		for(int round = 0; round < 5; round++) {
			final int winningPlayer = getRoundWinningPlayer(round);
			
			if(winningPlayer != -1) {
				playerWins[winningPlayer]++;
			}
		}
		
		int bestPlayerIndex = -1;
		int bestPlayerWins = 0;
		
		for(int player = 0; player < playerWins.length; player++) {
			if(playerWins[player] > bestPlayerWins) {
				bestPlayerIndex = player;
				bestPlayerWins = playerWins[player];
			} else if(playerWins[player] == bestPlayerWins) {
				bestPlayerIndex = -1;
			}
		}
		
		return bestPlayerIndex;
	}

	@Override
	public String getGameOutcome() {
		final int winningPlayerIndex = getGameWinningPlayer();
		
		if(winningPlayerIndex < 0) {
			return "Kein Spieler hat das Spiel gewonnen!";
		} else {
			return "Spieler " + (1 + winningPlayerIndex) + " hat das Spiel gewonnen!";
		}
	}

	@Override
	public boolean isLastQuestionOfRound() {
		return currentQuestion == 2;
	}
	
	@Override
	public boolean isLastRound() {
		return currentRound == 4;
	}

}
