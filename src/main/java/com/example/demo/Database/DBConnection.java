package com.example.demo.Database;
import java.sql.*;
public class DBConnection {
    private String url; // Database details
    private String username; // MySQL credentials
    private String password;

    public DBConnection() { // Default constructor
        this.url = "jdbc:mysql://localhost:3306/socialmedia";
        this.username = "root";
        this.password = "";
    }

    public DBConnection(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection()  {
        Connection con = null;
        try {
            // Load and register the driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish connection
            con = DriverManager.getConnection(this.url, this.username, this.password);
            System.out.println("Connection Established successfully");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
        return con;
    }

 public void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
                System.out.println("Connection closed successfully");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
