package com.iti.project;


public class Game {

    private int gameId;
    private GameHandler playerOne;
    private GameHandler playerTwo;

    private int[] buttons;

    public Game(){
        this.gameId = 0;
        this.playerOne = null;
        this.playerTwo = null;
        this.buttons = new int[]{0,0,0,0,0,0,0,0,0};
    }

    public Game(int gameId, GameHandler playerOne, GameHandler playerTwo){
        this.gameId = gameId;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.buttons = new int[]{0,0,0,0,0,0,0,0,0};
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public GameHandler getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(GameHandler playerOne) {
        this.playerOne = playerOne;
    }

    public GameHandler getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(GameHandler playerTwo) {
        this.playerTwo = playerTwo;
    }

    public int[] getButtons() {
        return buttons;
    }

    public void nextTurn(String position){
        int pos = Integer.parseInt(position) - 1;
        //System.out.println(pos);
        if(buttons[pos] == 0){
            buttons[pos] = pos + 1;
        }
    }
}
