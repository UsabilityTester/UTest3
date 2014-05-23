<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="question" scope="session" class="at.ac.tuwien.big.we14.lab2.api.impl.DefaultQuestionBean" />

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Business Informatics Group Quiz</title>
        <link rel="stylesheet" type="text/css" href="style/screen.css" />
        <script src="js/jquery.js" type="text/javascript"></script>
        <script src="js/framework.js" type="text/javascript"></script>
    </head>
    <body id="questionpage">
        <a class="accessibility" href="#question">Zur Frage springen</a>
        <header role="banner" aria-labelledby="mainheading"><h1 id="mainheading"><span class="accessibility">Business Informatics Group</span> Quiz</h1></header>
        <nav role="navigation" aria-labelledby="navheading">
            <h2 id="navheading" class="accessibility">Navigation</h2>
            <ul>
                <li><a id="logoutlink" title="Klicke hier um dich abzumelden" href="quiz?action=logout" accesskey="l">Abmelden</a></li>
            </ul>
        </nav>
        
        <!-- round info -->
        <section role="main">
            <section id="roundinfo" aria-labelledby="roundinfoheading">
                <h2 id="roundinfoheading" class="accessibility">Spielerinformationen</h2>
                <div id="player1info">
                    <span id="player1name">Spieler 1</span>
                    <ul class="playerroundsummary">
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
                        <li><span class="accessibility">Frage 1:</span><span id="player1answer1" class="<%=pa[0][0]%>"><%=pa[0][1]%></span></li>
                        <li><span class="accessibility">Frage 2:</span><span id="player1answer2" class="<%=pa[1][0]%>"><%=pa[1][1]%></span></li>
                        <li><span class="accessibility">Frage 3:</span><span id="player1answer3" class="<%=pa[2][0]%>"><%=pa[2][1]%></span></li>
                    </ul>
                </div>
                <div id="player2info">
                    <span id="player2name">Spieler 2</span>
                    <ul class="playerroundsummary">
                        <li><span class="accessibility">Frage 1:</span><span id="player2answer1" class="<%=pa[3][0]%>"><%=pa[3][1]%></span></li>
                        <li><span class="accessibility">Frage 2:</span><span id="player2answer2" class="<%=pa[4][0]%>"><%=pa[4][1]%></span></li>
                        <li><span class="accessibility">Frage 3:</span><span id="player2answer3" class="<%=pa[5][0]%>"><%=pa[5][1]%></span></li>
                    </ul>
                </div>
                <div id="currentcategory"><span class="accessibility">Kategorie:</span> <%=question.getCategory()%></div>
            </section>
            
            <!-- Question -->
            <section id="question" aria-labelledby="questionheading">
                
                <form id="questionform" action="quiz" method="post">
                    <h2 id="questionheading" class="accessibility">Frage</h2>
                    <p id="questiontext"><%=question.getQuestionText() %></p>
                    <ul id="answers">
                    <% for(int i = 0; i < question.getChoiceCount(); i++){ int j = i+1; %>
                        <li><input id="option<%=j%>" name="option<%=j%>" type="checkbox"/><label for="option<%=j%>"><%=question.getChoice(i) %></label></li>
                        <% }%>
                     </ul>
                    <input id="timeleftvalue" name="timeleftvalue" type="hidden" value="100"/>
                    <input id="next" type="submit" value="weiter" accesskey="s"/>
                </form>
            </section>
            
            <section id="timer" aria-labelledby="timerheading">
                <h2 id="timerheading" class="accessibility">Timer</h2>
                <p><span id="timeleftlabel">Verbleibende Zeit:</span> <time id="timeleft">00:30</time></p>
                <meter id="timermeter" min="0" low="20" value="100" max="100"/>
            </section>
            
            <section id="lastgame">
                
            </section>
        </section>

        <!-- footer -->
        <footer role="contentinfo">© 2014 BIG Quiz</footer>
        
        <script type="text/javascript">
            //<![CDATA[
                       
                       
            
            // initialize time
            $(document).ready(function() {
                var maxtime = <%=question.getQuestionTime() %> ;
                var hiddenInput = $("#timeleftvalue");
                var meter = $("#timer meter");
                var timeleft = $("#timeleft");
				
                if(localStorage.lastGame){
                	$("#lastgame").append($("<p></p>").append("Letzes spiel: " +localStorage.lastGame ));
                }else{
                	$("#lastgame").append($("<p></p>").append("Letzes spiel: Nie "));
                }
                
                hiddenInput.val(maxtime);
                meter.val(maxtime);
                meter.attr('max', maxtime);
                meter.attr('low', maxtime/100*20);
                timeleft.text(secToMMSS(maxtime));
            });
            
            // update time
            function timeStep() {
                var hiddenInput = $("#timeleftvalue");
                var meter = $("#timer meter");
                var timeleft = $("#timeleft");
                
                var value = $("#timeleftvalue").val();
                if(value > 0) {
                    value = value - 1;   
                }
                
                hiddenInput.val(value);
                meter.val(value);
                timeleft.text(secToMMSS(value));
                
                if(value <= 0) {
                    $('#questionform').submit();
                }
            }
            
            window.setInterval(timeStep, 1000);
            
            //]]>
        </script>
    </body>
</html>
