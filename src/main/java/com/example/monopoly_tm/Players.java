package com.example.monopoly_tm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Players
{
    String name;
    int balance;
    ObservableList<Cell> properties = FXCollections.observableArrayList();

    boolean hasDiceRolled = false;

    Rectangle playerPiece;

    int position = 0;

    public Players(String name, Rectangle playerPiece)
    {
        this.name = name;
        this.playerPiece = playerPiece;
        balance = 1500;
    }

    public void buyProperty(Cell property)
    {
        properties.add(property);
    }

    public void sellProperty(Cell property)
    {
        properties.remove(property);
    }

    public ObservableList<Cell> getProperties()
    {
        return properties;
    }
    public ObservableList<Cell> getProps() { return (ObservableList<Cell>) properties;}

    public int getBalance()
    {
        return balance;
    }

    public void setBalance(int balance)
    {
        this.balance = balance;
    }

    public String getName()
    {
        return name;
    }


//    set instead of just setting it for when the turn changes and for if doubles are rolled
    public void setHasDiceRolled(boolean hasDiceRolled)
    {
        this.hasDiceRolled = hasDiceRolled;
    }

    public void movePlayer(int distance)
    {
        position += distance;
        if(position >= 40)
            position -= 40;
    }

    public int getPosition()
    {
        return position;
    }

    public Rectangle getPlayerPiece()
    {
        return playerPiece;
    }

    public void moveRectInCell(double x, double y)
    {
        playerPiece.setX(x);
        playerPiece.setY(y);
    }

    public Paint getColor()
    {
        return playerPiece.getFill();
    }
}
