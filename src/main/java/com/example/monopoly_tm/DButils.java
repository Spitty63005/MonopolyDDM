package com.example.monopoly_tm;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

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

    public static void closeUtils(PreparedStatement p) throws SQLException
    {
        p.close();
    }

    public static void closeUtils(ResultSet r, PreparedStatement p) throws SQLException
    {
        r.close();
        p.close();
    }

    public static void closeUtils(Connection c, PreparedStatement p, ResultSet r) throws SQLException
    {
        c.close();
        p.close();
        r.close();
    }
    // endregion

    // region Save Game
    public static void completeSave(
            ArrayList<Players> players,
            ArrayList<Cell> properties, String password, TextInputDialog td) throws SQLException
    {

        Connection conn = connectDB();

        // checks if password is used
        // if used already
        switch (verifyPwd(password))
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
                saveOwnedProperties(conn, players, properties, password);
                savePlayeLocations(conn, players, password);
            }
        }

        conn.close();
    }

    private static void savePlayeLocations(Connection conn, ArrayList<Players> players, String password) throws SQLException
    {
        PreparedStatement ps;
        ps = conn.prepareStatement("SET FOREIGN_KEY_CHECK=0;");
        ps.executeUpdate();

        ps = conn.prepareStatement("INSERT INTO Location VALUES(?, ?, ?)");
        ps.setInt(1, getGameId(password, conn));

        for (Players p : players)
        {
            ps.setInt(2, players.indexOf(p));
            ps.setInt(3, p.getPosition());
        }

        closeUtils(ps);
    }

    private static void saveOwnedProperties(
            Connection conn, ArrayList<Players> players,
            ArrayList<Cell> properties, String password) throws SQLException
    {
        PreparedStatement ps;

        ps = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=0");
        ps.executeUpdate();

        ps = conn.prepareStatement("INSERT INTO Owned VALUES(?, ?, ?)");

        ps.setInt(1, getGameId(password, conn));

        for (Cell c : properties)
        {
            if (c.isOwned)
            {
                ps.setInt(2, players.indexOf(c.getOwner()));
                ps.setInt(3, properties.indexOf(c));
                ps.executeUpdate();
            }
        }
        closeUtils(ps);
    }


    private static void savePlayers(Connection conn, ArrayList<Players> players, String password) throws SQLException
    {
        PreparedStatement ps;

        ps = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=0");
        ps.executeUpdate();

        ps = conn.prepareStatement("INSERT INTO players VALUES (?, ?, ?, ?)");

        // get gameId

        ps.setInt(1, getGameId(password, conn));

        for (int i = 0; i < players.size(); i++)
        {
            Players p = players.get(i);
            System.out.println(p.getName());

            ps.setInt(2, i);
            ps.setInt(3, players.get(i).getBalance());
            ps.setString(4, players.get(i).getName());
            ps.executeUpdate();
        }
        closeUtils(ps);

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

        if (rs.next())
        {
            System.out.println("autism creature yippie!");
            closeUtils(rs, ps);
            return rs.getInt("gameId");
        }
        closeUtils(rs, ps);
        return -1;
    }

    private static void printGamesTable() throws SQLException
    {
        Connection conn = connectDB();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Games");
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            System.out.println("Game ID = " + rs.getInt(1) + "\nGame Password = " + rs.getString(2));
            System.out.println();
        }
        closeUtils(conn, ps, rs);
    }

    private static int verifyPwd(String password) throws SQLException
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
            if (rs.isBeforeFirst())
            {
                System.out.println("failure");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Password is Already in Use");
                alert.showAndWait();
                closeUtils(rs, ps);
                return 0;
            }
            if (rs.next())
            {
                System.out.println("true from verifyPwd");
                closeUtils(rs, ps);
                return 1;
            }
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        closeUtils(rs, ps);
        return 2;
    }

    private static void saveGame(Connection conn, String password) throws SQLException
    {
        System.out.println("In saveGame");
        PreparedStatement ps;

        ps = conn.prepareStatement("INSERT INTO Games (gamePwd) VALUES (?)");
        ps.setString(1, password);
        ps.executeUpdate();
        closeUtils(ps);
    }
    //endregion


    // region load game

    //endregion
}
