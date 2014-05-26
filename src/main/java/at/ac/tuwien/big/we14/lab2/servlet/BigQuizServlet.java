package at.ac.tuwien.big.we14.lab2.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import at.ac.tuwien.big.we14.lab2.api.QuestionBean;
import at.ac.tuwien.big.we14.lab2.api.QuizFactory;
import at.ac.tuwien.big.we14.lab2.api.impl.ServletQuizFactory;

// Your Servlet implementation
public class BigQuizServlet extends HttpServlet {
	private static final long serialVersionUID = -5535359999476767368L;
	private static final Logger logger = Logger.getLogger(BigQuizServlet.class.getSimpleName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(QuizFactory.INSTANCE == null) {
			QuizFactory.INSTANCE = new ServletQuizFactory(getServletContext());
		}
		
		final HttpSession session = req.getSession();
		QuestionBean questionBean = (QuestionBean)session.getAttribute("question");
		
		if(questionBean == null) {
			questionBean = QuizFactory.INSTANCE.createQuestionBean();
			session.setAttribute("question", questionBean);
		}
		
		final String action = req.getParameter("action");
		final String dispachTo;
		
		if("restart".equals(action)) {
			dispachTo = "/question.jsp";
			session.invalidate();
		} else if("logout".equals(action)) {
			dispachTo = "/start.jsp";
			session.invalidate();
		} else if("question".equals(action)) {
			dispachTo = "/question.jsp";
		} else if("nextround".equals(action) && questionBean.isLastQuestionOfRound()) {
			dispachTo = "/question.jsp";
			questionBean.next();
		} else {
			dispachTo = "/start.jsp";
		}
		
		RequestDispatcher dispatcher =
				getServletContext()
				.getRequestDispatcher(dispachTo);
				dispatcher.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(QuizFactory.INSTANCE == null) {
			QuizFactory.INSTANCE = new ServletQuizFactory(getServletContext());
		}
		
		final HttpSession session = req.getSession();
		QuestionBean questionBean = (QuestionBean)session.getAttribute("question");
		
		if(questionBean == null) {
			questionBean = QuizFactory.INSTANCE.createQuestionBean();
			session.setAttribute("question", questionBean);
		}
		
		final String dispachTo;
		int answerSize = questionBean.getChoiceCount();
		boolean[] answers = new boolean[answerSize];
		
		for(int i = 0; i < answerSize; i++) {
			answers[i] = "on".equals(req.getParameter("option" + (i + 1)));
		}
		
		long timeLeft = 0;
		
		if(req.getParameter("timeleftvalue") != null) {
			try {
				timeLeft = Long.parseLong(req.getParameter("timeleftvalue"))*1000L;
			} catch(NumberFormatException ex) {
				logger.log(Level.WARNING, "Can't parse the value of the 'timeleftvalue'-parameter!", ex);
			}
		}
		
		//Set them as answers
		questionBean.setAnswers(answers);
		questionBean.setLeftAnswerTime(timeLeft);
		boolean nextQuestion = false;
		
		if(questionBean.isLastQuestionOfRound()) {
			if(questionBean.isLastRound()) {
				dispachTo = "/finish.jsp";
			} else {
				dispachTo = "/roundcomplete.jsp";
			}
		} else {
			dispachTo = "/question.jsp";
			nextQuestion = true;
		}
			
		//Check answers & go to next question
		questionBean.doLogic();
		
		if(nextQuestion) {
			questionBean.next();
		}
		
		RequestDispatcher dispatcher =
				getServletContext()
				.getRequestDispatcher(dispachTo);
				dispatcher.forward(req, resp);
	}

}