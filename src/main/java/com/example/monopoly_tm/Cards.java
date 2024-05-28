package com.example.monopoly_tm;

public class Cards
{
    private final String title, action;

    public Cards(String title, String action)
    {
        this.title = title;
        this.action = action;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAction()
    {
        return action;
    }
}
