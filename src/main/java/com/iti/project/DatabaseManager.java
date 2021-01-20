package com.iti.project;

import java.sql.*;

public class DatabaseManager {

    public Connection getDatabaseConnection () {
        Connection connection = null;
        String databaseUrl = "jdbc:sqlite:db\\database.db";
        try {
            connection = DriverManager.getConnection(databaseUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}