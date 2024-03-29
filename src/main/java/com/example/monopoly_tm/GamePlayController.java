package com.example.monopoly_tm;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
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
    ImageView right_die_one, right_die_two, right_die_three, right_die_four, right_die_five, right_die_six,
            left_die_one, left_die_two, left_die_three, left_die_four, left_die_five, left_die_six;

    @FXML
    StackPane left_stack, right_stack;



    ImageView[] rightDie = new ImageView[6];
    ImageView[] leftDie = new ImageView[6];

    boolean hasMoved = false;

    boolean doubles = false;

    static ArrayList<Players> playerList = new ArrayList<Players>();

//    these two arrays are parallel
    Pane[] paneList;
    ArrayList<Cell> cellList = new ArrayList<>();

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
            setDieLists();
        }
        catch(Exception ex){
            System.out.println(ex.toString());
        }

        changePlayer(currentPlayer);

        paneList = new Pane[]{go_Pane, Cell_1_pane, Cell_2_pane, Cell_3_pane, Cell_4_pane, Cell_5_pane, Cell_6_pane, Cell_7_pane,
                Cell_8_pane, Cell_9_pane, jail_visit, Cell_11_pane, Cell_12_pane, Cell_13_pane, Cell_14_pane,
                Cell_15_pane, Cell_16_pane, Cell_17_pane, Cell_18_pane, Cell_19_pane, free_parking,
                Cell_21_pane, Cell_22_pane, Cell_23_pane, Cell_24_pane, Cell_25_pane, Cell_26_pane, Cell_27_pane,
                Cell_28_pane, Cell_29_pane, go_to_jail, Cell_31_pane, Cell_32_pane, Cell_33_pane, Cell_34_pane,
                Cell_35_pane, Cell_36_pane, Cell_37_pane, Cell_38_pane, Cell_39_pane};

        for (Players p:
             playerList)
        {
            paneList[0].getChildren().add(p.getPlayerPiece());
        }
    }

//region turn start and end
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

        hasMoved = false;
    }

    public void endTurn(ActionEvent e)
    {
        if(hasMoved && !doubles)
        {
            int indexOfCurrentPlayer = playerList.indexOf(currentPlayer);


            currentPlayer = (indexOfCurrentPlayer >= playerList.size() - 1)
                    ? playerList.get(0)
                    : playerList.get(indexOfCurrentPlayer + 1);

            changePlayer(currentPlayer);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Must Move To End Turn");
            alert.show();
        }
    }
    //endregion


//region startOfGame

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

    //endregion

//region dice

    public void rollDice(ActionEvent ae)
    {
        if(!hasMoved || doubles)
        {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.07), e -> rollingFrame()));

            timeline.setCycleCount(10);
            timeline.play();


            timeline.setOnFinished((e) ->
            {
                updateDieImages();
            });
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Can only roll once a turn unless rolled doubles");
        }
    }

    private void updateDieImages()
    {
        int currLeftDieValue = imageToInt(left_stack);
        int currRightDieValue = imageToInt(right_stack);
        System.out.println("Die one rolled: " + currLeftDieValue +"\nDie two rolled: " + currRightDieValue);

        int diceSum = currLeftDieValue + currRightDieValue;
        System.out.println(currentPlayer.getName() + " Moves Forward " + diceSum + " Spaces");

        hasMoved = true;

        doubles = currRightDieValue == currLeftDieValue;

        movePiece(diceSum);

    }

    private int imageToInt(StackPane stack)
    {
        String result = stack.getChildren().get(0).toString();
        String[] imageViewSplit = result.split("_");
        String test = imageViewSplit[2].substring(0, imageViewSplit[2].indexOf(","));

        switch (imageViewSplit[2].substring(0, imageViewSplit[2].indexOf(",")))
        {
            case "one" ->
            {
                return 1;
            }

            case "two" ->
            {
                return 2;
            }

            case "three" ->
            {
                return 3;
            }

            case "four" ->
            {
                return 4;
            }

            case "five" ->
            {
                return 5;
            }

            case "six" ->
            {
                return 6;
            }

            default ->
            {
                return 7;
            }
        }
    }

    private void rollingFrame()
    {
        left_stack.getChildren().get(0).setVisible(false);
        right_stack.getChildren().get(0).setVisible(false);

        left_stack.getChildren().clear();
        right_stack.getChildren().clear();

        left_stack.getChildren().add(leftDie[randDieNum()]);
        right_stack.getChildren().add(rightDie[randDieNum()]);

        left_stack.getChildren().get(0).setVisible(true);
        right_stack.getChildren().get(0).setVisible(true);

    }

    private static int randDieNum()
    {
        return (int) (Math.random() * (6 - 1) * 1);
    }

    private void setDieLists()
    {
        rightDie = new ImageView[]{right_die_one, right_die_two, right_die_three, right_die_four,
                right_die_five, right_die_six};

        leftDie = new ImageView[] {left_die_one, left_die_two, left_die_three, left_die_four,
                left_die_five, left_die_six};
    }

//    endregion


//region actions after roll
    private void movePiece(int amountToMove)
    {
        paneList[getPlayerPosFromPaneList()].getChildren()
                .remove(currentPlayer.getPlayerPiece());

        currentPlayer.movePlayer(amountToMove);

        movingAnimation(paneList[getPlayerPosFromPaneList()]);

        paneList[currentPlayer.getPosition()].getChildren().add(currentPlayer.getPlayerPiece());
    }

    private int getPlayerPosFromPaneList()
    {
        return currentPlayer.getPosition();
    }

    private void movingAnimation(Pane endLoc)
    {
        double endX = endLoc.getLayoutX();
        double endY = endLoc.getLayoutY();

        double currX = currentPlayer.getPlayerPiece().getLayoutX();
        double currY = currentPlayer.getPlayerPiece().getLayoutY();

        while(!(enx))
        {

        }
    }
//endregion






}

