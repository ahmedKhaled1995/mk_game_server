package com.iti.project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerController {

    private final DatabaseManager databaseManager;

    public PlayerController(){
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

    public int addPlayer(Player player){
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
        connection.close();
        return player;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verifyFields(Player player)
    {
        // check empty fields
        if(player.getName().trim().equals("") || player.getUserName().trim().equals("") ||
                player.getEmail().trim().equals("") || player.getPassword().trim().equals("") ||
                player.getGender().trim().equals("")) {
            //JOptionPane.showMessageDialog(null, "One Or More Fields Are Empty","Empty Fields",2);
            return false;
        } else{ // if everything is ok
            return true;
        }
    }


    public boolean checkUsername(String username) {
        Connection con=null;
        PreparedStatement st = null;
        ResultSet resultSet = null;
        boolean usernameExist = false;
        String queryString = "SELECT * FROM players WHERE userName = ?";
        try {
            con = this.databaseManager.getDatabaseConnection();
            st = con.prepareStatement(queryString);
            st.setString(1, username);
            resultSet= st.executeQuery();
            if(resultSet.next()) {
                usernameExist = true;
                //JOptionPane.showMessageDialog(null, "This Username is Already Taken, Choose Another One", "Username Failed", 2);
            }
            con.close();
        } catch (Exception ex) {
            //Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        //Database.DbConnection.endConnection(resultSet,st,con);
        return usernameExist;
    }


    /*public boolean PlayerLoginCheck(String username,String password)

    {
        PreparedStatement st;
        ResultSet rs = null;
        Connection con=null;

        //create a select query to check if the username and the password exist in the database
        String query = "SELECT * FROM `players` WHERE `email` = ?";

        // show a message if the username or the password fields are empty
        if(username.trim().equals("username"))
        {
            //JOptionPane.showMessageDialog(null, "Enter Your Username", "Empty Username", 2);
        }
        else if(password.trim().equals("password"))
        {
            //JOptionPane.showMessageDialog(null, "Enter Your Password", "Empty Password", 2);
        }
        else{

            try {

                con = this.databaseManager.getDatabaseConnection()
                st= con.prepareStatement(query);
                st.setString(1, username);
                rs = st.executeQuery();


                if(rs.next())
                {
                    String pass=rs.getString("password");
                    if(BCrypt.checkpw(password, pass)){

                        Integer id=rs.getInt("id");
                        Updatestatus(id,con);
                        System.out.println("Okaay..");
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Invalid Password","Login Error",2);

                    }


                }else{
                    // error message
                    JOptionPane.showMessageDialog(null, "Invalid Username","Login Error",2);
                }

                rs.close();
                st.close();
                con.close();

            } catch (SQLException ex) {
                Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return false;
    }
*/
    /*public int void Updatestatus(Integer id){
        String sqlInsert ="update players set status = ?"
                +"where id = ?";


        PreparedStatement stm;
        try {
            stm = con.prepareStatement(sqlInsert);
            stm.setString(1, "Online");
            stm.setInt(2, id);

            int updateCount = stm.executeUpdate();

            if(updateCount>=1){

                *//*Alert recordcreadted = new Alert(Alert.AlertType.INFORMATION);
                recordcreadted.setTitle("Record Status..");
                recordcreadted.setHeaderText(null);
                recordcreadted.setContentText("Record updated successfully.. ");
                recordcreadted.showAndWait();*//*

            }

        } catch (SQLException ex) {
            //System.out.println(ex);
            //Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }*/
}
