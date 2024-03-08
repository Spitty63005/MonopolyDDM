package com.example.monopoly_tm;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Players
{
    String name;
    int balance;
    ArrayList<Property> properties = new ArrayList<>();

    boolean hasDiceRolled = false;

    Rectangle playerPiece;

    int position = 0;

    public Players(String name, Rectangle playerPiece)
    {
        this.name = name;
        this.playerPiece = playerPiece;
        balance = 1500;
    }

    public void buyProperty(Property property)
    {
        properties.add(property);
    }

    public void sellProperty(Property property)
    {
        properties.remove(property);
    }

    public ArrayList<Property> getProperties()
    {
        return properties;
    }

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

    public int movePlayer(int distance)
    {
        position += distance;
        if(position >= 40)
            position -= 40;
        return position;
    }

    public int getPosition()
    {
        return position;
    }

    public Rectangle getPlayerPiece()
    {
        return playerPiece;
    }

    public Paint getColor()
    {
        return playerPiece.getFill();
    }
}
