package com.example.monopoly_tm;

public class Property extends Cell
{
    boolean isOwned;

    public Property(String color, String name, int rent, int houseCost)
    {
        super(color, name);
        isOwned = false;
    }



}
