package com.example.monopoly_tm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class GamePlayController implements Initializable
{
    @FXML
    BorderPane gameplay_BP;

    @FXML
    Pane go_Pane, Cell_1_pane, Cell_2_pane, Cell_3_pane, Cell_4_pane, Cell_5_pane, Cell_6_pane, Cell_7_pane,
            Cell_8_pane, Cell_9_pane, jail_visit, Cell_11_pane, Cell_12_pane, Cell_13_pane, Cell_14_pane,
            Cell_15_pane, Cell_16_pane, Cell_17_pane, Cell_18_pane, Cell_19_pane, free_parking,
            Cell_21_pane, Cell_22_pane, Cell_23_pane, Cell_24_pane, Cell_25_pane, Cell_26_pane, Cell_27_pane,
            Cell_28_pane, Cell_29_pane, go_to_jail, Cell_31_pane, Cell_32_pane, Cell_33_pane, Cell_34_pane,
            Cell_35_pane, Cell_36_pane, Cell_37_pane, Cell_38_pane, Cell_39_pane;

    @FXML
    Label current_player_info_LBL, current_player_name_LBL,
        current_player_properties_LBL, player_land_space_LBL;

    @FXML
    ImageView die_one_IV, die_two_IV;


    static ArrayList<Players> playerList = new ArrayList<Players>();

//    these two arrays are parallel
    Pane[] paneList = {go_Pane, Cell_1_pane, Cell_2_pane, Cell_3_pane, Cell_4_pane, Cell_5_pane, Cell_6_pane, Cell_7_pane,
        Cell_8_pane, Cell_9_pane, jail_visit, Cell_11_pane, Cell_12_pane, Cell_13_pane, Cell_14_pane,
        Cell_15_pane, Cell_16_pane, Cell_17_pane, Cell_18_pane, Cell_19_pane, free_parking,
        Cell_21_pane, Cell_22_pane, Cell_23_pane, Cell_24_pane, Cell_25_pane, Cell_26_pane, Cell_27_pane,
        Cell_28_pane, Cell_29_pane, go_to_jail, Cell_31_pane, Cell_32_pane, Cell_33_pane, Cell_34_pane,
        Cell_35_pane, Cell_36_pane, Cell_37_pane, Cell_38_pane, Cell_39_pane};
    ArrayList<Cell> cellList = new ArrayList<>();



    public final Image[] ALL_ROLLS = new Image[6];

    static Players currentPlayer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        File jsonFile = new File("src/main/resources/com/example/monopoly_tm/Tiles.json");
        JSONParser parser = new JSONParser();
        try
        {
            Object obj = parser.parse(new FileReader(jsonFile));
            JSONArray jsonArray = (JSONArray) obj;

            for(Object x: jsonArray)
            {
                JSONObject currLineItems = (JSONObject) x;

                switch (currLineItems.size())
                {
                    case 2 ->
                    {
                        String name = (String) currLineItems.get("name");
                        String type = (String) currLineItems.get("type");
                        cellList.add(new Cell(type, name));
                    }

                    case 3 ->
                    {
                        String name = (String) currLineItems.get("name");
                        String type = (String) currLineItems.get("type");

                        int cost = Math.toIntExact((long) currLineItems.get("cost"));
                        cellList.add(new Cell(type, name, cost));
                    }

                    default ->
                    {
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
            setDieImageList();
        }
        catch(Exception ex){
            System.out.println(ex.toString());
        }

        changePlayer(currentPlayer);
    }

    private void setDieImageList() throws FileNotFoundException
    {
        ALL_ROLLS[0] = new Image(new FileInputStream("images/Die/Rolled_One.png"));
        ALL_ROLLS[1] = new Image(new FileInputStream("images/Die/Rolled_Two.png"));
        ALL_ROLLS[2] = new Image(new FileInputStream("images/Die/Rolled_Three.png"));
        ALL_ROLLS[3] = new Image(new FileInputStream("images/Die/Rolled_Four.png"));
        ALL_ROLLS[4] = new Image(new FileInputStream("images/Die/Rolled_Five.png"));
        ALL_ROLLS[5] = new Image(new FileInputStream("images/Die/Rolled_Six.png"));
    }

    private void changePlayer(Players currentPlayer)
    {
        int currentPlayerIndex = playerList.indexOf(currentPlayer);


        String name = playerList.get(currentPlayerIndex).getName();
        int balance = playerList.get(currentPlayerIndex).getBalance();
        ArrayList<Property> properties = playerList.get(currentPlayerIndex).getProperties();

        current_player_info_LBL.setText(name + " has $" + balance);
        current_player_name_LBL.setText(name + " owns:");

        StringBuilder currentPlayerOwns = new StringBuilder();
        for(Property p: properties)
        {
            currentPlayerOwns.append("• ").append(p.getName()).append("\n");
        }
        if(currentPlayerOwns.isEmpty())
            currentPlayerOwns.append("None LMAO HOMELESS");
        current_player_properties_LBL.setText(String.valueOf(currentPlayerOwns));

        player_land_space_LBL.setText("Roll the dice to move");
    }

    //
    private int[] getRentValues(Object rent)
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

    public static void showGameBoard() throws IOException
    {
//        gets rid of the top bar
        Stage stage = new Stage(StageStyle.UNDECORATED);

//        gets the screen dimensions
        Screen screen = Screen.getPrimary();
        Rectangle2D screenBounds = screen.getVisualBounds();

//        shows the new stage
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("Game.fxml"));

//        creates the scene with the size of the primary screen of the user
        Scene scene = new Scene(fxmlLoader.load(), screenBounds.getWidth(), screenBounds.getHeight());

//        sets the location to top left corner
        stage.setX(0);
        stage.setY(0);

        stage.setScene(scene);
        stage.show();
    }

//    initializes the list of players arraylist
    public static void setPlayerList(ArrayList<Players> players)
    {
        playerList = players;
        currentPlayer = playerList.get(0);
    }

    public void rollTheDice(ActionEvent e)
    {
      /* little roll dice "animation" (go through random dice sprites changing over interval of time)
      *  but after 1.5 seconds show result (determined by behind scenes random method)
      *  check for if doubles are rolled
      *  keep rolled dice false if true else change it
      *  player moves forward the sum of the two dice
      *  update the landing space label*/
        int diceSum = 0;
        try
        {
            diceSum = rollAnimation();
        } catch (InterruptedException ex)
        {
            throw new RuntimeException(ex);
        }

    }

    private int rollAnimation() throws InterruptedException
    {
        int dieOne = randDieNum();
        int dieTwo = randDieNum();

        int rollTime = 0;

        while(rollTime < 3000)
        {
            die_one_IV.setImage(ALL_ROLLS[randDieNum()]);
            die_two_IV.setImage(ALL_ROLLS[randDieNum()]);
            Thread.sleep(100);
            rollTime += 100;
        }


        return dieOne + dieTwo;
    }

    private static int randDieNum()
    {
        return (int) Math.floor(Math.random() * (6 - 1) * 1);
    }

    public void endTurn(ActionEvent e)
    {
//        TODO LATER make this only work if the player has moved this turn
        int indexOfCurrentPlayer = playerList.indexOf(currentPlayer);


        currentPlayer = (indexOfCurrentPlayer >= playerList.size()-1)
                ? playerList.get(0)
                : playerList.get(indexOfCurrentPlayer+1);

        changePlayer(currentPlayer);
    }





}

