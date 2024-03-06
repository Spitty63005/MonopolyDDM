package com.example.monopoly_tm;

import java.util.ArrayList;

public class Players
{
    String name;
    ArrayList<Property> properties = new ArrayList<>();


    public Players(String name)
    {
        this.name = name;
    }

    public void buyProperty(Property property)
    {
        properties.add(property);
    }

}
