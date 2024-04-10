package com.example.monopoly_tm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.shape.Rectangle;


public class Players
{
    String name;
    int balance;
    int position = 0;
    boolean inJail = false;
    ObservableList<Cell> properties = FXCollections.observableArrayList();
    Rectangle playerPiece;

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
    public ObservableList<Cell> getProps() { return properties;}

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

    public void setPosition(int pos)
    {
        position = pos;
    }

    public boolean isInJail()
    {
        return inJail;
    }
    public void setInJail(boolean isInJail)
    {
        inJail = isInJail;
    }
}
