package com.example.monopoly_tm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;

public class JsonUtils
{

    public static ObservableList<Cell> getTiles()
    {
        ObservableList<Cell> cellList = FXCollections.observableArrayList();
        File jsonFile = new File("src/main/resources/com/example/monopoly_tm/Tiles.json");
        JSONParser parser = new JSONParser();
        try
        {
            Object obj = parser.parse(new FileReader(jsonFile));
            JSONArray jsonArray = (JSONArray) obj;

            for(Object x: jsonArray)
            {
                JSONObject currLineItems = (JSONObject) x;

                // checks for kind of cell by checking the length
                // of each array stored in the JSON file
                switch (currLineItems.size())
                {
                    case 2 ->
                    {
                        // a standard cell without any way to buy or own it
                        String name = (String) currLineItems.get("name");
                        String type = (String) currLineItems.get("type");
                        cellList.add(new Cell(type, name));
                    }
                    case 3 ->
                    {
                        // a cell like the railroads or utilities
                        String name = (String) currLineItems.get("name");
                        String type = (String) currLineItems.get("type");

                        int cost = Math.toIntExact((long) currLineItems.get("cost"));
                        cellList.add(new Cell(type, name, cost));
                    }
                    default ->
                    {
                        /*
                         * a property cell which is made as a property object
                         * which is a child of cell
                         * and adds the new property object to the cell array list*/
                        String name = (String) currLineItems.get("name");
                        String type = (String) currLineItems.get("type");

                        int cost = Math.toIntExact((long) currLineItems.get("cost"));
                        String color = (String) currLineItems.get("color");

                        int[] rent = getRentValues(currLineItems.get("rent"));
                        int houseCost = Math.toIntExact((long) currLineItems.get("house"));

                        cellList.add(new Property(type, name, color, rent, cost, houseCost));
                    }
                }
            }
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }
        return cellList;
    }

    public static ObservableList<Cards> getCards(String type)
    {

    }

    private static int[] getRentValues(Object rent)
    {
        int[] rentArr = new int[6];
        String[] splitString = ((String) rent).split(",");

//        takes all the parts of the inputted string and introduces them to the array
        for(int i = 0; i < 6; i++)
        {
            long part1 = (Long.parseLong(splitString[i]));
            rentArr[i] = Math.toIntExact(part1);
        }

        return rentArr;
    }
}
