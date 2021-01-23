package com.iti.project;

import org.json.simple.JSONObject;

public class Game {

    private final int gameId;
    private final String playerOne;
    private final String playerTwo;
    private String winner;
    private String loser;
    private boolean playerOneTurn;  // At the start of the game, 'playerOne' always starts
    private boolean hasSomeOneWon;
    private int gameTurns;   // Max is 9, if reached and no one has won, then it's a tie

    private final int[] board;  // game board
    private final int[] orderOfPlay;

    public Game(int gameId, String playerOne, String playerTwo){
        this.gameId = gameId;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.board = new int[]{0,0,0,0,0,0,0,0,0};  // button id is array index, 'X' or 'O' is the array element
        this.orderOfPlay = new int[]{0,0,0,0,0,0,0,0,0};
        this.playerOneTurn = true;  // 'playerOne' is 'X', 'playerTwo' is 'O'
        this.hasSomeOneWon = false;
        this.gameTurns = 0;
        this.winner = null;
        this.loser = null;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public String getPlayerToPlay(){
        if(this.playerOneTurn){
            return playerOne;
        }else{
            return playerTwo;
        }
    }

    public String getPlayerToWait(){
        if(this.playerOneTurn){
            return playerTwo;
        }else{
            return playerOne;
        }
    }

    public int getGameTurns(){
        return this.gameTurns;
    }

    public String getWinner(){
        return this.winner;
    }

    public String getLoser(){
        return this.loser;
    }

    public int getGameId(){
        return this.gameId;
    }

    public String nextTurn(int index, int symbol){   // ex: [index, element] ex: [0->8, 1 or -1]
        this.playerOneTurn = !this.playerOneTurn;
        this.orderOfPlay[gameTurns] = index;
        this.gameTurns++;
        this.board[index] = symbol;
        return this.checkGameOver();
    }

    private String checkGameOver(){
        // First, we check if win condition is reached
        int winner = this.checkWin();  // 0 no one won, 1 playerOne has won, -1 playerTwo has won
        if(this.checkTie()){
            return null;  // Tie
        }else if (winner != 0){  // Won condition has been reached
            if(winner == 1){
                this.winner = playerOne;
                this.loser = playerTwo;
                return playerOne;
            }else{
                this.winner = playerTwo;
                this.loser = playerOne;
                return playerTwo;
            }
        }
        return null;
    }

    /*
    0 1 2
    3 4 5
    6 7 8
    */
    // Returns 0 if no one won, 1 if 'X' won and -1 if 'O' won
    private int checkWin(){
        // Checking horizontally
        if(board[0] != 0 && (board[0] == board[1] && board[0] == board[2])){
            this.hasSomeOneWon = true;
            return board[0];
        }else if(board[3] != 0 && (board[3] == board[4] && board[3] == board[5])){
            this.hasSomeOneWon = true;
            return board[3];
        }else if(board[6] != 0 && (board[6] == board[7] && board[6] == board[8])){
            this.hasSomeOneWon = true;
            return board[6];
        }
        // Checking vertically
        else if(board[0] != 0 && (board[0] == board[3] && board[0] == board[6])){
            this.hasSomeOneWon = true;
            return board[0];
        }else if(board[1] != 0 && (board[1] == board[4] && board[1] == board[7])){
            this.hasSomeOneWon = true;
            return board[1];
        }else if(board[2] != 0 && (board[2] == board[5] && board[2] == board[8])){
            this.hasSomeOneWon = true;
            return board[2];
        }
        // Checking diagonally
        else if(board[0] != 0 && (board[0] == board[4] && board[0] == board[8])){
            this.hasSomeOneWon = true;
            return board[0];
        }else if(board[2] != 0 && (board[2] == board[4] && board[2] == board[6])){
            this.hasSomeOneWon = true;
            return board[2];
        }
        return 0;
    }

    private boolean checkTie(){
        return !this.hasSomeOneWon && this.gameTurns >= 9;
    }

   /* public void resetGame(){
        this.gameId = gameId;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.board = new int[]{0,0,0,0,0,0,0,0,0};  // button id is array index, 'X' or 'O' is the array element
        this.orderOfPlay = new int[]{0,0,0,0,0,0,0,0,0};
        this.playerOneTurn = true;  // 'playerOne' is 'X', 'playerTwo' is 'O'
        this.hasSomeOneWon = false;
        this.gameTurns = 0;
        this.winner = null;
        this.loser = null;
    }*/

    public JSONObject getGameBoard(){
        JSONObject gameValues = new JSONObject();
        gameValues.put(this.orderOfPlay[0], this.board[0]);
        gameValues.put(this.orderOfPlay[1], this.board[1]);
        gameValues.put(this.orderOfPlay[2], this.board[2]);
        gameValues.put(this.orderOfPlay[3], this.board[3]);
        gameValues.put(this.orderOfPlay[4], this.board[4]);
        gameValues.put(this.orderOfPlay[5], this.board[5]);
        gameValues.put(this.orderOfPlay[6], this.board[6]);
        gameValues.put(this.orderOfPlay[7], this.board[7]);
        gameValues.put(this.orderOfPlay[8], this.board[8]);
        return gameValues;
    }
}
