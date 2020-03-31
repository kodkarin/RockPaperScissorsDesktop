package com.example.rps.models;

import java.sql.Connection;

public class Game {

    private int gameID;
    private Player player1;
    private Player player2;
    private int scorePlayer1;
    private int scorePlayer2;
    private int currentChoicePlayer1;
    private int currentChoicePlayer2;
    private int[] roundWinners;
    public static final int ROCK = 1;
    public static final int SCISSORS = 2;
    public static final int PAPER = 3;
    public static final int DRAW = 0;
    public static final int PLAYER1_WINS = 1;
    public static final int PLAYER2_WINS = 2;
    public static final int INVALID_INPUT = -1;

    public Game( int gameID, Player player1, Player player2) {
        this.gameID = gameID;
        this.player1 = player1;
        this.player2 = player2;
        scorePlayer1 = 0;
        scorePlayer2 = 0;
        currentChoicePlayer1 = 0;
        currentChoicePlayer2 = 0;
        int[] roundWinners = new int[15];
    }


    public int getGameID() {
        return gameID;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public int getScorePlayer1() {
        return scorePlayer1;
    }

    public int getScorePlayer2() {
        return scorePlayer2;
    }

    public void increaseScorePlayer1() {
        scorePlayer1++;
    }

    public void increaseScorePlayer2() {
        scorePlayer2++;
    }

    @Override
    public String toString() {
        String resultat1 = String.valueOf(scorePlayer1);
        String resultat2 = String.valueOf(scorePlayer2);

        return player1.getUserName() + " - " + player2.getUserName() + " resultat: " + scorePlayer1 + " - " + scorePlayer2;
    }

    public void setRoundWinners(int round, int player) {
        if(round > roundWinners.length) {
            int[] temp = new int[roundWinners.length * 2];
            for(int i = 0; i < roundWinners.length; i++) {
                temp[i] = roundWinners[i];
            }
            roundWinners = temp;
        }
        roundWinners[round-1] = player;
    }

    public int getRoundWinner(int round){
        if((round >0) && (round<= roundWinners.length)) {
            return roundWinners[round-1];
        } else {
            return 0;
        }
    }

    public void refreshScore(Connection conn) {

    }


    public String makeChoice(Player player, int choice) {

        String message = "OK";
        if (player == player1) {
            if (currentChoicePlayer1 == 0) {
                currentChoicePlayer1 = choice;
                // skriv in i databasen
            } else {
                message = "Kan inte göra ett drag. Väntar på motståndaren.";
            }
        } else if (player == player2) {
            if (currentChoicePlayer2 == 0) {
                currentChoicePlayer2 = choice;
                // skriv in i databasen
            } else {
                message = "Kan inte göra ett drag. Väntar på motståndaren.";
            }
        } else {
            return "Spelaren deltar ej i den här spelomgången";
        }

        if (currentChoicePlayer1 != 0 && currentChoicePlayer2 != 0) {
            int winner = compareChoices(currentChoicePlayer1, currentChoicePlayer2);
            currentChoicePlayer1 = 0;
            currentChoicePlayer2 = 0;
            if (winner == PLAYER1_WINS) {
                scorePlayer1++;
            } else if (winner == PLAYER2_WINS) {
                scorePlayer2++;
            }
        }
        return message;
    }

    public int compareChoices(int choice1, int choice2) {

        int result;

        if (choice1 == choice2) {
            result = DRAW;
        } else  switch(choice1) {
            case ROCK:
                result = choice2 == SCISSORS ?  PLAYER1_WINS :  PLAYER2_WINS;
                break;
            case SCISSORS:
                result = choice2 == PAPER ?  PLAYER1_WINS :  PLAYER2_WINS;
                break;
            case PAPER:
                result = choice2 == ROCK ?  PLAYER1_WINS :  PLAYER2_WINS;
                break;
            default:
                result = INVALID_INPUT;
        }
        return result;
    }
}
