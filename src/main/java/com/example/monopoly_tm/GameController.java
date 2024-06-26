package com.example.monopoly_tm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class GameController implements Initializable
{
    @FXML
    Spinner<Integer> playerCount;

    @FXML
    TextField player1, player2, player3, player4, player5, player6, player7, player8;

    @FXML
    AnchorPane playerCount_AP, spinner_AP, base_AP, main_menu_AP;


    TextField[] nameInputs = new TextField[8];

    int amountOfPlayers;

    Color[] playerColors = {Color.BLUE, Color.RED, Color.GREEN, Color.PURPLE,
            Color.YELLOW, Color.TURQUOISE, Color.DEEPPINK, Color.DARKGOLDENROD};

    ArrayList<Players> currentPlayers = new ArrayList<>();

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
        main_menu_AP.setVisible(false);
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
        Rectangle rect;
        for (int i = 0; i < amountOfPlayers; i++)
        {
            if (nameInputs[i].getText().isEmpty() || nameInputs[i].getText().equals(" "))
            {
                Alert alert = DButils.makeAlert(3, "All Players Must Have A Valid Name");
                alert.show();
                return;
            }
            switch (amountOfPlayers)
            {
                case 2 -> rect = new Rectangle(50, 50, playerColors[i]);
                case 3 -> rect = new Rectangle(45, 45, playerColors[i]);
                case 4 -> rect = new Rectangle(40, 40, playerColors[i]);
                case 5 -> rect = new Rectangle(35, 35, playerColors[i]);
                case 6 -> rect = new Rectangle(30, 30, playerColors[i]);
                case 7 -> rect = new Rectangle(25, 25, playerColors[i]);
                case 8 -> rect = new Rectangle(20, 20, playerColors[i]);
                default -> rect = new Rectangle(34, 34, playerColors[i]);
            }
            currentPlayers.add(new Players(nameInputs[i].getText(), rect));
            currentPlayers.get(i).getPlayerPiece().setId("piece" + i);
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

    public void showMainMenu()
    {
        hideAllPages();
        main_menu_AP.setVisible(true);
    }

    public void showLoadGame()
    {

    }

    public void showSettings()
    {
        Alert alert = DButils.makeAlert(3,
                "This game is perfect as is, so there is no option to change part of this masterpiece");
        alert.show();
    }

    public void quit()
    {
        ButtonType confirm = new ButtonType("Yep");
        ButtonType decline = new ButtonType("Nope");
        Alert alert = DButils.makeAlert(2, "Are you afraid to lose?");
        alert.getButtonTypes().setAll(confirm, decline);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == confirm)
            System.exit(0);
    }
}