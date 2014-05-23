package at.ac.tuwien.big.we14.lab2.api.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import at.ac.tuwien.big.we14.lab2.api.Category;
import at.ac.tuwien.big.we14.lab2.api.Choice;
import at.ac.tuwien.big.we14.lab2.api.Question;
import at.ac.tuwien.big.we14.lab2.api.QuestionDataProvider;
import at.ac.tuwien.big.we14.lab2.api.QuestionManager;

public class DefaultQuestionManager implements QuestionManager {
	private List<Category> categories;
	private Category currentCategory;
	private Question currentQuestion;
	private List<Choice> currentChoices;
	
	public DefaultQuestionManager(QuestionDataProvider qdp){
		this.categories = qdp.loadCategoryData();
		nextCategory();
		nextQuestion();
	}
	
	public void nextCategory(){
		if(categories.size() == 0){
			throw new NoSuchElementException("There is no unused category left");
		}
		
		currentCategory = retrieveRandomItem(categories);
		nextQuestion();
	}
	
	/**
	 * Note that already used questions are no longer saved in getCurrentCategory().getQuestions() 
	 */
	@Override
	public Category getCurrentCategory() {
		return currentCategory;
	}
	
	public void nextQuestion(){
		if(currentCategory.getQuestions().size() == 0){
			throw new NoSuchElementException("There is no unused question left");
		}
		
		currentQuestion = retrieveRandomItem(currentCategory.getQuestions());
		
		//GetAllChoices already shuffles the list
		currentChoices = currentQuestion.getAllChoices();
	}

	@Override
	public Question getCurrentQuestion() {
		return currentQuestion;
	}


	@Override
	public List<Choice> getChoices() {
		return currentChoices;
	}

	@Override
	public boolean checkAnswer(boolean[] answers) {
		for(int i = 0; i < answers.length; i++){
			Choice c = currentChoices.get(i);
			
			if( answers[i] ){
				if ( !currentQuestion.getCorrectChoices().contains(c) ) 
					return false;
			} else {
				if ( currentQuestion.getCorrectChoices().contains(c) ) 
					return false;
			}	
		}
		return true;
	}
	
	/*
	 * Chooses a random item from the given list, removes that item from the list and returns it
	 */
	private <E> E retrieveRandomItem(List<E> list){
		int index = new Random().nextInt(list.size());
		E item = list.get(index);
		list.remove(index);
		return item;
	}


}
