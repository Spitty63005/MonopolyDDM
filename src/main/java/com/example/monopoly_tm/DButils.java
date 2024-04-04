package com.example.monopoly_tm;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.lang.invoke.SwitchPoint;
import java.sql.*;
import java.util.ArrayList;

public class DButils
{
    // region common function methods
    public static Connection connectDB()
    {
        try
        {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/monoploy", "root", "password");
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void closeUtils(Connection c, PreparedStatement p) throws SQLException
    {
        c.close();
        p.close();
    }
    public void closeUtils(Connection c, PreparedStatement p, ResultSet r) throws SQLException
    {
        c.close();
        p.close();
        r.close();
    }
    // end region

    // region Save Game
    public static void completeSave(ArrayList<Players> players,
                                    ArrayList<Cell> properties, String password, TextInputDialog td) throws SQLException
    {
        /* TODO this will be a general method that calls other methods for different parts of saving the game*/

        Connection conn = connectDB();

        // checks if password is used
        // if used already
        switch(verifyPwd(password))
        {
            case 0 ->
            {
                td.showAndWait();
                password = td.getEditor().getText();
                completeSave(players, properties, password, td);
            }
            case 1, 2 ->
            {
                saveGame(conn, password);
                savePlayers(conn, players, password);
                // TODO make a savedOwnedProperties
                //saveOwnedProperties(conn, players, properties);
            }
        }
    }

    private static void saveOwnedProperties(Connection conn, ArrayList<Players> players, ArrayList<Cell> properties)
    {
    }


    private static void savePlayers(Connection conn, ArrayList<Players> players, String password) throws SQLException
    {
        PreparedStatement ps;

        ps = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=0");
        ps.executeUpdate();

        ps = conn.prepareStatement("INSERT INTO players VALUES (?, ?, ?, ?)");

        // get gameId

        ps.setInt(1 ,getGameId(password, conn));

        for (int i = 0; i < players.size(); i++)
        {
            Players p = players.get(i);
            System.out.println(p.getName());

            ps.setInt(2, i);
            ps.setInt(3, players.get(i).getBalance());
            ps.setString(4, players.get(i).getName());
            ps.executeUpdate();
        }
        ps.close();

    }

    private static int getGameId(String password, Connection conn) throws SQLException
    {
        PreparedStatement ps;
        ResultSet rs;
        ps = conn.prepareStatement("SELECT gameId FROM Games WHERE gamePwd = ?");
        System.out.println(password);
        ps.setString(1, password);
        printGamesTable();
        rs = ps.executeQuery();

        if(rs.next())
        {
            System.out.println("autism creature yippie!");
            return rs.getInt("gameId");
        }
        return -1;
    }

    private static void printGamesTable() throws SQLException
    {
        Connection conn = connectDB();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Games");
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            System.out.println("Game ID = " + rs.getInt(1) + "\nGame Password = " + rs.getString(2));
            System.out.println();
        }
    }

    private static int verifyPwd (String password)
    {
        System.out.println("In verifyPwd");
        Connection conn = connectDB();
        PreparedStatement ps;
        ResultSet rs;
        try
        {
            ps = conn.prepareStatement("SELECT gameId FROM Games WHERE gamePwd = ?");
            ps.setString(1, password);
            rs = ps.executeQuery();
            if(rs.isBeforeFirst())
            {
                System.out.println("failure");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Password is Already in Use");
                alert.showAndWait();
                return 0;
            }
            if(rs.next())
            {
                System.out.println("true from verifyPwd");
                return 1;
            }
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return 2;
    }

    private static void saveGame(Connection conn, String password) throws SQLException
    {
        System.out.println("In saveGame");
        PreparedStatement ps;

        ps = conn.prepareStatement("INSERT INTO Games (gamePwd) VALUES (?)");
        ps.setString(1, password);
        ps.executeUpdate();
        ps.close();
    }
    //endregion



    // region load game

    //endregion



    // region update during game

    //endregion
}
