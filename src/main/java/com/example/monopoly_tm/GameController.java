package com.example.monopoly_tm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class GameController implements Initializable
{
    @FXML
    Spinner<Integer> playerCount;

    @FXML
    TextField player1, player2, player3, player4, player5, player6, player7, player8;

    @FXML
    AnchorPane playerCount_AP, spinner_AP, base_AP;


    TextField[] nameInputs = new TextField[8];

    int amountOfPlayers;

    ArrayList<Players> currentPlayers = new ArrayList<>();

    Stage stage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        SpinnerValueFactory<Integer> spinnerLimits = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 8);
        playerCount.setValueFactory(spinnerLimits);
        nameInputs[0] = player1;
        nameInputs[1] = player2;
        nameInputs[2] = player3;
        nameInputs[3] = player4;
        nameInputs[4] = player5;
        nameInputs[5] = player6;
        nameInputs[6] = player7;
        nameInputs[7] = player8;
    }

    /*
    * hides all parts of the setup scene
    * also hides all text fields in the player count page
    */
    public void hideAllPages()
    {
        playerCount_AP.setVisible(false);
        spinner_AP.setVisible(false);
        for(TextField tf : nameInputs)
        {
            tf.setVisible(false);
        }
    }

    /*
    * sets the list of players and passes it into other controller
    * where it will be used
    * also hides all the pages on the setup scene
    */
    public void showGame(ActionEvent e) throws IOException
    {
        for (int i = 0; i < amountOfPlayers; i++)
        {
            if (nameInputs[i].getText().isEmpty() || nameInputs[i].getText().equals(" "))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "All Players Must Have A Valid Name");
                alert.show();
                return;
            }
            currentPlayers.add(new Players(nameInputs[i].getText()));
        }

//        hides the previous window so the user cannot access it after they start the game
        ((Node)(e.getSource())).getScene().getWindow().hide();

//        sets the players in the game and switches to the game-board scene
        GamePlayController.setPlayerList(currentPlayers);
        GamePlayController.showGameBoard();
    }

    public void showPlayerCount()
    {
        hideAllPages();
        amountOfPlayers = playerCount.getValue();

        for(int i = 0; i < amountOfPlayers; i++)
        {
            nameInputs[i].setVisible(true);
        }

        playerCount_AP.setVisible(true);
    }

    public void showSpinner()
    {
        hideAllPages();
        spinner_AP.setVisible(true);
    }


}