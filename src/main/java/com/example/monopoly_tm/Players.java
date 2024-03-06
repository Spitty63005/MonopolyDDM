package com.example.monopoly_tm;

import java.util.ArrayList;

public class Players
{
    String name;
    int balance;
    ArrayList<Property> properties = new ArrayList<>();

    boolean hasDiceRolled = false;

    public Players(String name)
    {
        this.name = name;
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


}
