package com.iti.project;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class GameHandler {

    private static final HashMap<Integer, Game> gameMap = new HashMap<>();
    // contains the user and and json object containing the gameId as well as the opponent name
    // Json object has two keys 'gameId' and 'opponentName'
    private static final HashMap<String, JSONObject> usersInGame = new HashMap<>();
    private static final HashMap<String, GameHandler> nameSocketMap = new HashMap<>();

    private static Logger logger = LoggerFactory.getLogger(GameHandler.class);

    // We use atomic boolean instead of regular boolean to be able to change
    // its value in different threads safely (to close threads gracefully)
    private volatile AtomicBoolean running = new AtomicBoolean(false);

    private Thread listenToClientThread;
    private Socket currentSocket;
    private String userName;
    private DataInputStream dis;
    private PrintStream ps;

    public GameHandler(Socket cs)  {
        try {
            this.currentSocket = cs;
            this.dis = new DataInputStream(cs.getInputStream());
            this.ps= new PrintStream(cs.getOutputStream());

            this.listenToClientThread = new Thread(()->{
                this.listenToClient();
            });
            this.listenToClientThread.start();

        } catch (IOException e) {
            e.printStackTrace();
            //closeConnection();
        }
    }

    /* Used to close connection with the client */
    private void closeConnection(){
        try {
            this.currentSocket.close();
            this.dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.ps.close();
        logger.info("Closed Connection user name: " + this.userName);
        logger.info("Connection count before closing connection: " + nameSocketMap.size());
        if(this.userName != null){
            nameSocketMap.remove(this.userName);
        }
        logger.info("Connection count after closing connection: " + nameSocketMap.size());
        handleClientLeaving();
    }

    /* Used to stop the listening thread when something goes wrong or we close the server */
    private void stopThread(){
        this.running.set(false);
    }

    /* This method is the ears of the sever, it's a method running in its own
    *  thread, all it does is listed to client requests */
    private void listenToClient(){
        this.running.set(true);
        while(this.running.get()) {
            String str= null;
            try {
                str = dis.readLine();
                handleClientReply(str);
            } catch (IOException e) {
                // Handle exception here if a client exits (generated by str = dis.readLine())
                //e.printStackTrace();
                stopThread();
                closeConnection();
            }
        }
    }

    /* Used to send a message to all connected clients */
    private void broadCast(String msg) {
        for (Map.Entry<String,GameHandler> entry : nameSocketMap.entrySet()){
            entry.getValue().ps.println(msg);
        }
    }

    /* This method is heart of the program, it takes to client requests (reply)
    * and reply according to the 'type' received is the request json object */
    private void handleClientReply(String reply){
        JSONObject replyJson = parseStringToJsonObject(reply);
        String type = replyJson.get("type").toString();
        if(type.equals("gameTurn")){
            int gameId = Integer.parseInt(replyJson.get("gameId").toString());
            int index = Integer.parseInt(replyJson.get("index").toString());
            int symbol = Integer.parseInt(replyJson.get("symbol").toString());
            handleNextTurn(gameId, index, symbol);
        }else if(type.equals("login")){
            String name = replyJson.get("userName").toString();
            boolean success = false;
            // Checking if the name exists in the database and the user is not already logged in
            if(GameServer.getUsers().contains(name) && nameSocketMap.get(name) == null){
                nameSocketMap.put(name, this);
                success = true;
            }
            handleLogin(success, name);
        }else if(type.equals("getUsers")){
            handleSendAllUsers();
        }else if(type.equals("tryGameWithOpponent")){
            String possibleOpponentName = replyJson.get("opponent").toString();
            if(nameSocketMap.get(possibleOpponentName) == null){  // opponent is not online
                handleGameRejection(possibleOpponentName + " is currently not online!", null);
            }else if(usersInGame.get(possibleOpponentName) == null){  // opponent is free to play
                askOpponentForGame(possibleOpponentName);
            }else{    // opponent is busy (in other game)
                handleGameRejection("Opponents is in another game!", null);
            }
        }else if(type.equals("startGameResponse")){
            String gameAccepted = replyJson.get("result").toString();
            String opponentName = replyJson.get("opponent").toString();
            if(gameAccepted.equals("true")){
                System.out.println("Game accepted");
                handleGameStart(opponentName);
            }else{
                System.out.println("Game rejected");
                handleGameRejection(this.userName + " declined the game!", opponentName);
            }
        }
    }

    /* Handles next turn in the multi player game,
    * index is the place on the board that was clicked (0->8)
    * symbol is either 1 (for 'X') or -1 (for 'O') */
    private void handleNextTurn(int gameId, int index, int symbol){
        Game game = gameMap.get(gameId);
        GameHandler possibleWinner = nameSocketMap.get(game.nextTurn(index, symbol));
        GameHandler playerToPlay = nameSocketMap.get(game.getPlayerToPlay());
        GameHandler playerToWait = nameSocketMap.get(game.getPlayerToWait());
        JSONObject playerToPlayJson = createJsonObject();
        playerToPlayJson.put("type", "gameTurnResult");
        playerToPlayJson.put("won", "false");
        playerToPlayJson.put("lost", "false");
        playerToPlayJson.put("tie", "false");
        playerToPlayJson.put("myTurn", "true");
        playerToPlayJson.put("index", index);
        JSONObject playerToWaitJson = new JSONObject(playerToPlayJson);
        playerToWaitJson.replace("myTurn", "false");
        if(possibleWinner == null && game.getGameTurns() < 9){  // Game still running
            playerToPlay.ps.println(playerToPlayJson.toJSONString());
            playerToWait.ps.println(playerToWaitJson.toJSONString());
        }else if(possibleWinner == null && game.getGameTurns() >= 9){  // Tie
            playerToPlayJson.replace("tie", "true");
            playerToWaitJson.replace("tie", "true");
            playerToPlay.ps.println(playerToPlayJson.toJSONString());
            playerToWait.ps.println(playerToWaitJson.toJSONString());
            removeGame(game);
        }else if(possibleWinner != null){  // Some one has won
            GameHandler winner = nameSocketMap.get(game.getWinner());
            GameHandler loser = nameSocketMap.get(game.getLoser());
            JSONObject winnerJson = null;
            JSONObject loserJson = null;
            if(winner.equals(playerToPlay)){
                winnerJson = playerToPlayJson;
                loserJson = playerToWaitJson;
            }else{
                winnerJson = playerToWaitJson;
                loserJson = playerToPlayJson;
            }
            winnerJson.replace("won", "true");
            loserJson.replace("lost", "true");
            winner.ps.println(winnerJson.toJSONString());
            loser.ps.println(loserJson.toJSONString());
            removeGame(game);
        }
    }

    /* Used to send all users to the client when the client logs in */
    private void handleSendAllUsers(){
        JSONObject sendToClient = this.createJsonObject();
        JSONArray allUsers = new JSONArray();
        JSONArray availableUsers = new JSONArray();
        for(String user : GameServer.getUsers()){
            allUsers.add(user);
        }
        for (Map.Entry<String,GameHandler> entry : nameSocketMap.entrySet()){
            availableUsers.add(entry.getKey());
        }
        sendToClient.put("type", "usersList");
        sendToClient.put("users", allUsers);
        sendToClient.put("availableUsers", availableUsers);
        this.ps.println(sendToClient.toJSONString());
    }

    /* Used to notify other clients when a new client connects */
    private void signalOnlineUser(String loggedInUser){
        JSONObject sendToClient = this.createJsonObject();
        sendToClient.put("type", "newLoggedInUser");
        sendToClient.put("loggedInUser", loggedInUser);
        broadCast(sendToClient.toJSONString());
    }

    /* Used to notify other clients when a client leaves */
    private void signalUserLogout(String loggedOutUser){
        JSONObject sendToClient = this.createJsonObject();
        sendToClient.put("type", "loggedOutUser");
        sendToClient.put("loggedOutUser", loggedOutUser);
        broadCast(sendToClient.toJSONString());
    }

    /* Used to notify the client attempting to login if the login was successful or not,
       also it signals the other clients the a new user has joined */
    private void handleLogin(boolean success, String userName){
        JSONObject object = createJsonObject();
        object.put("type", "loginResult");
        object.put("success", success);
        if(success){
            object.put("userName", userName);
            this.userName = userName;
            logger.info("{} has logged in", userName);
        }
        this.ps.println(object.toJSONString());
        // Notifying other clients a new player has joined
        if(success){
            signalOnlineUser(userName);
        }
    }

    /* Connections are already closed and user is removed from nameSocketMap when this methods is called,
    All we handle is check if the client that left is in game or not
    and if he is in game, we terminate the game and notify the other player */
    private void handleClientLeaving(){
        JSONObject gameInfo = usersInGame.get(this.userName);
        if(gameInfo != null){  // That means that the client left was in a game
            int gameId = Integer.parseInt(gameInfo.get("gameId").toString());
            String opponentName = gameInfo.get("opponentName").toString();
            // Now we remove the two users from the map, and we notify the other client
            // that the game has been terminated
            JSONObject sendToOtherClient = createJsonObject();
            sendToOtherClient.put("type", "gameTerminated");
            nameSocketMap.get(opponentName).ps.println(sendToOtherClient.toJSONString());
            removeGame(gameMap.get(gameId));
        }
        // Here, I am notifying other clients that client has left (to update the listview in the frontend)
        signalUserLogout(this.userName);
    }

    /* Used to remove a game when it's finished */
    private void removeGame(Game game){
        logger.info("Removed game with id {}", game.getGameId());
        gameMap.remove(game.getGameId());
        usersInGame.remove(game.getPlayerOne());
        usersInGame.remove(game.getPlayerTwo());
    }

    /* Handles game rejection, note if opponent is null, that means game was rejected
    * by the server because the user was either busy or offline. If opponent name is
    * provided, that means that this opponent rejected the game  */
    private void handleGameRejection(String error, String opponent){
        if(opponent != null){ // Means game was rejected because user declined
            // Note that the opponent String in the method argument is the player who requested the game
            GameHandler userWhoRequestedGame = nameSocketMap.get(opponent);
            JSONObject sendToClient = createJsonObject();
            sendToClient.put("type", "gameRejected");
            sendToClient.put("error", error);
            userWhoRequestedGame.ps.println(sendToClient.toJSONString());
        }else{   // Means user was rejected because opponent is busy (in another game) or offline
            JSONObject sendToClient = createJsonObject();
            sendToClient.put("type", "gameRejected");
            sendToClient.put("error", error);
            this.ps.println(sendToClient.toJSONString());
        }

    }

    /* Used to notify the client that some one wants to play with him*/
    private void askOpponentForGame(String opponentName) {
        GameHandler opponentClient = nameSocketMap.get(opponentName);
        JSONObject sendToOpponent = createJsonObject();
        sendToOpponent.put("type", "startGameRequest");
        sendToOpponent.put("opponentName", this.userName);
        opponentClient.ps.println(sendToOpponent.toJSONString());
    }

    /* Handles creation of the game between two players and stores the necessary information
    * Note that here 'this' refers to the player who ASKED for the game,
    * while 'opponent' is the person who accepted the game */
    private void handleGameStart(String opponent){
        // Creating game info
        int gameId = gameMap.size();  // gameId starts at zero

        // Storing game info
        JSONObject playerOneGameInfo = createJsonObject();
        playerOneGameInfo.put("gameId", gameId);
        playerOneGameInfo.put("opponentName", opponent);
        JSONObject playerTwoGameInfo = createJsonObject();
        playerTwoGameInfo.put("gameId", gameId);
        playerTwoGameInfo.put("opponentName", this.userName);
        usersInGame.put(this.userName, playerOneGameInfo);
        usersInGame.put(opponent, playerTwoGameInfo);

        // Starting game and adding it to games list
        Game newGame = new Game(gameId, this.userName, opponent);
        logger.info("Started game with id {}", gameId);
        gameMap.put(gameId ,newGame);

        // Sending to both players game start info
        JSONObject sendToPlayerOne = this.createJsonObject();
        JSONObject sendToPlayerTwo = this.createJsonObject();

        sendToPlayerOne.put("type", "startGame");
        sendToPlayerOne.put("gameId", gameId);
        sendToPlayerOne.put("opponent", opponent);
        sendToPlayerOne.put("myTurn", true);

        sendToPlayerTwo.put("type", "startGame");
        sendToPlayerTwo.put("gameId", gameId);
        sendToPlayerTwo.put("opponent", this.userName);
        sendToPlayerTwo.put("myTurn", false);

        nameSocketMap.get(newGame.getPlayerOne()).ps.println(sendToPlayerOne.toJSONString());
        nameSocketMap.get(newGame.getPlayerTwo()).ps.println(sendToPlayerTwo.toJSONString());
    }

    public boolean equals(GameHandler other){
        return this.currentSocket.equals(other.currentSocket);
    }

    private JSONObject createJsonObject(){
        return new JSONObject();
    }

    private JSONObject parseStringToJsonObject(String jsonString){
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(jsonString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}