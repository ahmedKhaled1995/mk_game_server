package com.iti.project;


public class Game {

    private final int gameId;
    private final String playerOne;
    private final String playerTwo;
    private boolean playerOneTurn;  // At the start of the game, 'playerOne' always starts
    private boolean hasSomeOneWon;
    private int gameTurns;   // Max is 9, if reached and no one has won, then it's a tie
    private String winner;
    private String loser;

    private final int[] buttons;


    public Game(int gameId, String playerOne, String playerTwo){
        this.gameId = gameId;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.buttons = new int[]{0,0,0,0,0,0,0,0,0};  // button id is array index, 'X' or 'O' is the array element
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
        this.gameTurns++;
        this.buttons[index] = symbol;
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
        if(buttons[0] != 0 && (buttons[0] == buttons[1] && buttons[0] == buttons[2])){
            this.hasSomeOneWon = true;
            return buttons[0];
        }else if(buttons[3] != 0 && (buttons[3] == buttons[4] && buttons[3] == buttons[5])){
            this.hasSomeOneWon = true;
            return buttons[3];
        }else if(buttons[6] != 0 && (buttons[6] == buttons[7] && buttons[6] == buttons[8])){
            this.hasSomeOneWon = true;
            return buttons[6];
        }
        // Checking vertically
        else if(buttons[0] != 0 && (buttons[0] == buttons[3] && buttons[0] == buttons[6])){
            this.hasSomeOneWon = true;
            return buttons[0];
        }else if(buttons[1] != 0 && (buttons[1] == buttons[4] && buttons[1] == buttons[7])){
            this.hasSomeOneWon = true;
            return buttons[1];
        }else if(buttons[2] != 0 && (buttons[2] == buttons[5] && buttons[2] == buttons[8])){
            this.hasSomeOneWon = true;
            return buttons[2];
        }
        // Checking diagonally
        else if(buttons[0] != 0 && (buttons[0] == buttons[4] && buttons[0] == buttons[8])){
            this.hasSomeOneWon = true;
            return buttons[0];
        }else if(buttons[2] != 0 && (buttons[2] == buttons[4] && buttons[2] == buttons[6])){
            this.hasSomeOneWon = true;
            return buttons[2];
        }
        return 0;
    }

    private boolean checkTie(){
        return !this.hasSomeOneWon && this.gameTurns >= 9;
    }
}
