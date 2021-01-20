package com.iti.project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDao {

    private final DatabaseManager databaseManager;

    public PlayerDao(){
        this.databaseManager = new DatabaseManager();
    }

    public List<Player> getFullPlayersData(){
        Connection connection = this.databaseManager.getDatabaseConnection();
        ArrayList<Player> players = new ArrayList<>();
        try {
            //Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM players";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                players.add( new Player(
                                resultSet.getInt("ID"),
                                resultSet.getString("name"),
                                resultSet.getString("userName"),
                                resultSet.getString("password"),
                                resultSet.getString("email"),
                                resultSet.getString("gender"),
                                resultSet.getString("status"),
                                //resultSet.getBlob("avatar"),
                                null,
                                resultSet.getInt("score"),
                                resultSet.getDate("last_login")
                        )
                );
            }
            connection.close();
            return players;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Player> getPlayersData(){
        Connection connection = this.databaseManager.getDatabaseConnection();
        ArrayList<Player> players = new ArrayList<>();
        try {
            //Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM players";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                players.add( new Player(
                                resultSet.getString("userName"),
                                resultSet.getInt("score")
                        )
                );
            }
            connection.close();
            return players;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int insertPlayer(Player player){
        Connection connection = this.databaseManager.getDatabaseConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO players (name, username, password, email, gender, avatar," +
                            " status, score, last_login) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            statement.setString(1, player.getName());
            statement.setString(2, player.getUserName());
            statement.setString(3, player.getPassword());
            statement.setString(4, player.getEmail());
            statement.setString(5, player.getGender());
            statement.setBlob(6, player.getAvatar());
            statement.setString(7, player.getStatus());
            statement.setInt(8, player.getScore());
            statement.setDate(9, player.getLastLogin());

            int res = statement.executeUpdate();
            connection.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Player getPlayer(String userName, String password){
        Connection connection = this.databaseManager.getDatabaseConnection();
        PreparedStatement statement = null;
        Player player = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM players WHERE userName = ? AND password = ?");
            statement.setString(1, userName);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery(); // userName is unique so only one result is returned
            if(!resultSet.next()) {   // User not found
                return null;
            }
            player = new Player(
                    resultSet.getInt("ID"),
                    resultSet.getString("name"),
                    resultSet.getString("userName"),
                    resultSet.getString("password"),
                    resultSet.getString("email"),
                    resultSet.getString("gender"),
                    resultSet.getString("status"),
                    //resultSet.getBlob("avatar"),
                    null,
                    resultSet.getInt("score"),
                    resultSet.getDate("last_login")
            );
        return player;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
