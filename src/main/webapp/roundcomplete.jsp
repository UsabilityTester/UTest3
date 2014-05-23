<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="question" scope="session" class="at.ac.tuwien.big.we14.lab2.api.impl.DefaultQuestionBean" />

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Business Informatics Group Quiz - Zwischenstand</title>
        <link rel="stylesheet" type="text/css" href="style/screen.css" />
        <script src="js/jquery.js" type="text/javascript"></script>
        <script src="js/framework.js" type="text/javascript"></script>
    </head>
    <body id="winnerpage">
        <a class="accessibility" href="#roundwinner">Zur Rundenwertung springen</a>
        <header role="banner" aria-labelledby="mainheading"><h1 id="mainheading"><span class="accessibility">Business Informatics Group</span> Quiz</h1></header>
        <nav role="navigation" aria-labelledby="navheading">
            <h2 id="navheading" class="accessibility">Navigation</h2>
            <ul>
                <li><a id="logoutlink" title="Klicke hier um dich abzumelden" href="quiz?action=logout" accesskey="l">Abmelden</a></li>
            </ul>
        </nav>
        
        <section role="main">
            <!-- winner message -->
            <section id="roundwinner" aria-labelledby="roundwinnerheading">
                <h2 id="roundwinnerheading" class="accessibility">Rundenzwischenstand</h2>
                <p class="roundwinnermessage"><%=question.getRoundOutcome() %></p>
            </section>
        
            <!-- round info --> 
            <% 
            String[][] pa = new String[6][2];
            short player = 0;
        	short questi = 0;
        	
            for(int i = 0; i<6; i ++){
            	if(question.getPlayerQuestionScore(player, questi) == 1){
            		pa[i][0] = "correct";
            		pa[i][1] = "Richtig";
            	}else if(question.getPlayerQuestionScore(player, questi) == 0){
            		pa[i][0] = "incorrect";
            		pa[i][1] = "Falsch";
            	}else{
            		pa[i][0] = "unknown";
            		pa[i][1] = "Unbekannt";
            	}
            	questi +=1;
            	if(questi == 3){
            		player = 1;
            		questi = 0;
            	}	
            }
            %>   
            <section id="roundinfo" aria-labelledby="roundinfoheading">
                <h2 id="roundinfoheading" class="accessibility">Spielerinformationen</h2>
                <div id="player1info" class="playerinfo">
                    <span id="player1name" class="playername">Spieler 1</span>
                    <ul class="playerroundsummary">
                        <li><span class="accessibility">Frage 1:</span><span id="player1answer1" class="<%=pa[0][0]%>" ><%=pa[0][1]%></span></li>
                        <li><span class="accessibility">Frage 2:</span><span id="player1answer2" class="<%=pa[1][0]%>"><%=pa[1][1]%></span></li>
                        <li><span class="accessibility">Frage 3:</span><span id="player1answer3" class="<%=pa[2][0]%>"><%=pa[2][1]%></span></li>
                    </ul>
                    <p id="player1roundcounter" class="playerroundcounter">Gewonnene Runden: <span id="player1wonrounds" class="playerwonrounds"><%=question.getPlayerScore((short)0) %></span></p>
                </div>
                <div id="player2info" class="playerinfo">
                    <span id="player2name" class="playername">Spieler 2</span>
                    <ul class="playerroundsummary">
                        <li><span class="accessibility">Frage 1:</span><span id="player2answer1" class="<%=pa[3][0]%>" ><%=pa[3][1]%></span></li>
                        <li><span class="accessibility">Frage 2:</span><span id="player2answer2" class="<%=pa[4][0]%>"><%=pa[4][1]%></span></li>
                        <li><span class="accessibility">Frage 3:</span><span id="player2answer3" class="<%=pa[5][0]%>"><%=pa[5][1]%></span></li>
                    </ul>
                    <p id="player2roundcounter" class="playerroundcounter">Gewonnene Runden: <span id="player2wonrounds" class="playerwonrounds"><%=question.getPlayerScore((short)1) %></span></p>
                </div>
                <a id="next" href="quiz?action=nextround">Weiter</a>
            </section>
        </section>

        <!-- footer -->
        <footer role="contentinfo">© 2014 BIG Quiz</footer>
    </body>
</html>
