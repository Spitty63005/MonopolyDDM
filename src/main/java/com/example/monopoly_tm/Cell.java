package com.example.monopoly_tm;

public class Cell
{
    String type, name;
    int cost;
    boolean isOwned = false;

    public Cell(String type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public Cell(String type, String name, int cost)
    {
        this.type = type;
        this.name = name;
        this.cost = cost;
    }

    public String getName()
    {
        return name;
    }

    public int getCost()
    {
        return cost;
    }

    public String getType()
    {
        return type;
    }

    public void setOwned(boolean owned)
    {
        isOwned = owned;
    }


}
