package com.example.rps.models;

//Karin har skrivit den här klassen
public class Game {

    private int gameID;
    private Player player1;
    private Player player2;
    private int scorePlayer1;
    private int scorePlayer2;
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

    public void setScorePlayer1(int score) {
        scorePlayer1 = score;
    }

    public int getScorePlayer2() {
        return scorePlayer2;
    }

    public void setScorePlayer2(int score) {
        scorePlayer2 = score;
    }

    public void increaseScorePlayer1() {
        scorePlayer1++;
    }

    public void increaseScorePlayer2() {
        scorePlayer2++;
    }

    @Override
    public String toString() {
        return player1.getUserName() + " - " + player2.getUserName() + "   Result: " + scorePlayer1 + " - " + scorePlayer2;
    }

    public int compareChoices(int choice1, int choice2) {

        int result;

        if ((choice2 != ROCK) && (choice2 != PAPER) && (choice2 !=SCISSORS)) {
            result = INVALID_INPUT;
        } else if (choice1 == choice2) {
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
