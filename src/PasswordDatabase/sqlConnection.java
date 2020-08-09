package PasswordDatabase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import javax.swing.*;
import javax.swing.plaf.nimbus.State;
import java.sql.*;

public class sqlConnection {

    String dbLocation = "jdbc:sqlite:D:\\Programs\\Java\\IntelliJ Projects\\password_generator\\src\\PasswordDatabase\\passwordsMKI.db";
    Connection conn = null;
    Statement stnt = null;

    public sqlConnection(){
        //this.dbLocation = dbLocation;
        // Connection to the database
        try{
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(this.dbLocation);
            System.out.println("Database Connected");
        }
        catch (Exception e){
            System.out.println(e);

        }
    }

    //Query all websites saved in the DB
    public ObservableList<String> queryWebsites(){
        try{
            this.stnt = conn.createStatement();
            ResultSet rs = stnt.executeQuery("SELECT website FROM passwords");
            ObservableList<String> websites = FXCollections.observableArrayList();

            while(rs.next()){
                String website = rs.getString("website");
                if(websites.contains(website)){
                    continue;
                }
                else{
                    websites.add(website);
                }

            }
            return websites;
        }
        catch (Exception e){
            System.out.println("queryWebsites Error: " + e.getMessage());
            return null;
        }
    }

    public ObservableList<String> queryUserNames(String website){
        try{
            this.stnt = conn.createStatement();
            ResultSet rs = stnt.executeQuery("SELECT username FROM passwords WHERE website='" + website + "'");
            ObservableList<String> userNames = FXCollections.observableArrayList();

            while(rs.next()){
                userNames.add(rs.getString("username"));
            }
            return userNames;
        }
        catch (Exception e){
            System.out.println("queryUserNames Error: " + e.getMessage());
            return null;
        }
    }

    // Query password for a website
    public String queryPasswordByWebsite(String website, String username){
        try{
            this.stnt = conn.createStatement();
            ResultSet rs = stnt.executeQuery("SELECT password FROM passwords WHERE website='"+website+"' AND username='" + username + "'");
            return rs.getString("password");
        }
        catch (Exception e){
            System.out.println("queryPasswordByWebsite Error: " + e.getMessage());
            return null;
        }
    }

    public void saveUserInput(String website, String username, String password){
        try{
            this.stnt = conn.createStatement();
            stnt.executeUpdate("INSERT INTO passwords (website, username, password) VALUES ('" +  website + "','"
                    + username + "','" + password + "');");
            System.out.println("User input saved.");
        }
        catch(Exception e){
            System.out.println("saveUserInput Error: " + e.getMessage());
        }
    }

    public void modifyPassword(String website, String username, String newPassword){
        try{
            this.stnt = conn.createStatement();
            System.out.println("UPDATE passwords SET password = '" + newPassword + "' WHERE website = '" + website + "' and username = '" + username + "';");
            stnt.executeUpdate("UPDATE passwords SET password = '" + newPassword + "' WHERE website = '" + website + "' and username = '" + username + "';");
            System.out.println("Password updated");
        }
        catch(Exception e){
            System.out.println("modifyPassword Error: " + e.getMessage());
        }

    }

    // Close connection
    public void closeConnection(){
        try{
            conn.close();
            System.out.println("Connection closed.");
        }
        catch (Exception e){
            System.out.println("Close Connection Error: " + e.getMessage());
        }
    }

}
