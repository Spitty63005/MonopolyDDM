package com.example.monopoly_tm;

public class Property extends Cell
{
    String propertyColor;

    int houseCost;

    int[] rent;

    public Property(String type, String name, String color, int[] rent, int tileCost, int houseCost)
    {
        super(type, name, tileCost);
        propertyColor = color;
        this.houseCost = houseCost;
        this.rent = rent;
    }


    public int getRent()
    {
        return rent[0];
    }
}
