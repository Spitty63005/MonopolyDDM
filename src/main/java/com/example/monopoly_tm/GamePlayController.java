package com.example.monopoly_tm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GamePlayController implements Initializable
{
    @FXML
    BorderPane gameplay_BP;




    static ArrayList<Players> playerList = new ArrayList<Players>();

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
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        File jsonFile = new File("resources//Tiles.json");
        JSONParser parser = new JSONParser();

        try
        {
            Object obj = parser.parse(new FileReader(jsonFile));
            JSONObject jsonObject = (JSONObject) obj;
            String name = (String) jsonObject.get("Name");
            String type = (String) jsonObject.get("Type");

        }
        catch(Exception ex){
            System.out.println(ex.toString());
        }
    }

}

