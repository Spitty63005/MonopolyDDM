package com.example.monopoly_tm;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

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

    public static Alert makeAlert(int alertType, String alertBodyText)
    {
        Alert newAlert;
        switch (alertType)
        {
            case 1 -> newAlert = new Alert(Alert.AlertType.INFORMATION, alertBodyText);
            case 2 -> newAlert = new Alert(Alert.AlertType.CONFIRMATION, alertBodyText);
            case 3 -> newAlert = new Alert(Alert.AlertType.ERROR, alertBodyText);
            case 4 -> newAlert = new Alert(Alert.AlertType.WARNING, alertBodyText);
            default -> newAlert = new Alert(Alert.AlertType.NONE);

        }
        DialogPane dialogPane = newAlert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(DButils.class.getResource("Style.css")).toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        return newAlert;
    }
    // endregion

    // region Save Game
    public static void completeSave(
            ArrayList<Players> players,
            ObservableList<Cell> properties, String password, TextInputDialog td) throws SQLException
    {

        Connection conn = connectDB();

        // checks if password is used
        // if used already

        if (!verifyPwd(password))
        {
            saveGame(conn, password);
            savePlayers(conn, players, password);
            saveOwnedProperties(conn, players, properties, password);
            savePlayerLocations(conn, players, password);
        } else
            handlePasswordDialog(players, properties, td);

        conn.close();
    }

    private static void handlePasswordDialog(ArrayList<Players> players, ObservableList<Cell> properties,
                                             TextInputDialog td) throws SQLException
    {
        td.showAndWait();
        String password = td.getEditor().getText();
        completeSave(players, properties, password, td);
    }

    private static void savePlayerLocations(Connection conn, ArrayList<Players> players, String password) throws SQLException
    {
        PreparedStatement ps;

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
            ObservableList<Cell> properties, String password) throws SQLException
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
        int result;
        ps = conn.prepareStatement("SELECT gameId FROM Games WHERE gamePwd = ?");
        ps.setString(1, password);
        printGamesTable();
        rs = ps.executeQuery();

        if (rs.next())
        {
            result = rs.getInt("gameId");
            closeUtils(rs, ps);
            return result;
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

    private static boolean verifyPwd(String password) throws SQLException
    {
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
                Alert alert = makeAlert(3, "Password is Already in Use");
                alert.showAndWait();
                closeUtils(rs, ps);
                return false;
            }
            if (rs.next())
            {
                closeUtils(rs, ps);
                return true;
            }
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        closeUtils(rs, ps);
        return true;
    }

    private static void saveGame(Connection conn, String password) throws SQLException
    {
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
