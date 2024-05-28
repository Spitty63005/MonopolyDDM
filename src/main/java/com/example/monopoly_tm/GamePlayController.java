package com.example.monopoly_tm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GamePlayController implements Initializable
{
    //region FXML Variables
    @FXML
    BorderPane gameplay_BP;

    @FXML
    TableView<Cell> sell_tbv;

    @FXML
    TableColumn<String, Cell> sell_property_name, sell_property_price;

    @FXML
    Pane go_Pane, Cell_1_pane, Cell_2_pane, Cell_3_pane, Cell_4_pane, Cell_5_pane, Cell_6_pane, Cell_7_pane,
            Cell_8_pane, Cell_9_pane, jail_visit, Cell_11_pane, Cell_12_pane, Cell_13_pane, Cell_14_pane,
            Cell_15_pane, Cell_16_pane, Cell_17_pane, Cell_18_pane, Cell_19_pane, free_parking,
            Cell_21_pane, Cell_22_pane, Cell_23_pane, Cell_24_pane, Cell_25_pane, Cell_26_pane, Cell_27_pane,
            Cell_28_pane, Cell_29_pane, go_to_jail, Cell_31_pane, Cell_32_pane, Cell_33_pane, Cell_34_pane,
            Cell_35_pane, Cell_36_pane, Cell_37_pane, Cell_38_pane, Cell_39_pane, settings_pane, game_log_pane,
            jail_Pane, property_functions_pane, turn_switch_pane;

    @FXML
    AnchorPane dimming_ap, anchor_ap;
    @FXML
    Label current_player_info_LBL, current_player_name_LBL,
        current_player_properties_LBL, player_land_space_LBL;

    @FXML
    public static ImageView right_die;
    @FXML
    public static ImageView left_die;

    @FXML
    StackPane menus_stack;

    @FXML
    Button roll_btn;
    //endregion
    //region Variables
    private final TextInputDialog td = new TextInputDialog();

    boolean hasMoved = false;

    boolean doubles = false;

    Logger log;

    static Players currentPlayer;
    //endregion
    //region Arrays
    static ArrayList<Players> playerList = new ArrayList<>();

//    these two arrays are parallel
    Pane[] paneList;
    ObservableList<Cell> cellList = FXCollections.observableArrayList();


    final static Image[] all_rolls = new Image[6];
    final static String[] filePaths = new String[6];
    //endregion
    //region Objects
    final Dice dice = new Dice();

    public static boolean isHaveMoved()
    {
        return hasMoved;
    }
    //endregion

    public boolean isDoubles()
    {
        return doubles;
    }

    //region Initialization
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        // gets the file of the JSON file to read all the cell's information from
        cellList = JsonUtils.getTiles();

        setAllRolls();
        // set the game to start on the first play's turn
        changeGUIPlayerText(currentPlayer);

        // adds every standard cell that you pass through without jail
        paneList = new Pane[]{go_Pane, Cell_1_pane, Cell_2_pane, Cell_3_pane, Cell_4_pane, Cell_5_pane, Cell_6_pane, Cell_7_pane,
                Cell_8_pane, Cell_9_pane, jail_visit, Cell_11_pane, Cell_12_pane, Cell_13_pane, Cell_14_pane,
                Cell_15_pane, Cell_16_pane, Cell_17_pane, Cell_18_pane, Cell_19_pane, free_parking,
                Cell_21_pane, Cell_22_pane, Cell_23_pane, Cell_24_pane, Cell_25_pane, Cell_26_pane, Cell_27_pane,
                Cell_28_pane, Cell_29_pane, go_to_jail, Cell_31_pane, Cell_32_pane, Cell_33_pane, Cell_34_pane,
                Cell_35_pane, Cell_36_pane, Cell_37_pane, Cell_38_pane, Cell_39_pane};


        // make the image of the player pieces show up on go
        makePlayerPieces(playerList.size());

        // sets the table within the property functions pane
        // will be empty at the start, but it fills as the game goes
        setPropertiesTable();
    }

    private void setAllRolls()
    {
        try
        {
            filePaths[0] = "src\\main\\resources\\com\\example\\monopoly_tm\\images\\Die\\Rolled_One.png";
            filePaths[1] = "src\\main\\resources\\com\\example\\monopoly_tm\\images\\Die\\Rolled_Two.png";
            filePaths[2] = "src\\main\\resources\\com\\example\\monopoly_tm\\images\\Die\\Rolled_Three.png";
            filePaths[3] = "src\\main\\resources\\com\\example\\monopoly_tm\\images\\Die\\Rolled_Four.png";
            filePaths[4] = "src\\main\\resources\\com\\example\\monopoly_tm\\images\\Die\\Rolled_Five.png";
            filePaths[5] = "src\\main\\resources\\com\\example\\monopoly_tm\\images\\Die\\Rolled_Six.png";

            all_rolls[0] = new Image(new FileInputStream(filePaths[0]));
            all_rolls[1] = new Image(new FileInputStream(filePaths[1]));
            all_rolls[2] = new Image(new FileInputStream(filePaths[2]));
            all_rolls[3] = new Image(new FileInputStream(filePaths[3]));
            all_rolls[4] = new Image(new FileInputStream(filePaths[4]));
            all_rolls[5] = new Image(new FileInputStream(filePaths[5]));
        }
        catch (FileNotFoundException e)
        {
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }
    //endregion

//region Turn Start/End

    /* Change Player
    * this method changes the text on the GUI
    * to reflect the play whose turn it currently is
    * then resets boolean has moved to false so the next player can move
    * and enables the roll button so the player is able to roll the die
    */
    private void changeGUIPlayerText(Players currentPlayer)
    {
        int currentPlayerIndex = playerList.indexOf(currentPlayer);

        String name = playerList.get(currentPlayerIndex).getName();
        int balance = playerList.get(currentPlayerIndex).getBalance();

        current_player_info_LBL.setText(name + " has $" + balance);
        current_player_name_LBL.setText(name + " owns:");

        updateAllText();
        player_land_space_LBL.setText("Roll the dice to move");

        hasMoved = false;
        roll_btn.setDisable(false);
    }

    /* End Turn
    * checks for if the player has moved and not rolled doubles
    * if true then it updates the current player to be whose ever turn is next
    * it does this by adding 1 to the index of player list if the index exceeds the length
    * the player at index of 0 is selected
    *
    * if the player has not moved or did roll doubles then this method will
    * make an alert pop up with the information that is stopping the player
    * from ending their turn
    */
    public void endTurn(ActionEvent ignoredE)
    {
        if(hasMoved && !doubles)
        {
            int indexOfCurrentPlayer = playerList.indexOf(currentPlayer);


            currentPlayer = (indexOfCurrentPlayer >= playerList.size() - 1)
                    ? playerList.get(0)
                    : playerList.get(indexOfCurrentPlayer + 1);

            changeGUIPlayerText(currentPlayer);
        }
        else
        {
            Alert alert = DButils.makeAlert(2, "Must Move To End Turn");
            alert.show();
        }
    }
    //endregion

//region Start Of Game

    /* Dynamic Player Pieces
    * this method sets the layout for the player pieces to space evenly in the start cell
    * this version of this method is only called at the start of the game
    */
    private void dynamicPlayerPieces(double playerPieceSize, int numOfPieces)
    {



        int rowCount = (int) Math.ceil((Math.sqrt(numOfPieces)));
        int colCount = (int) Math.ceil((double) numOfPieces / rowCount);

        Pane startingPane = paneList[0];
        int spacer = numOfPieces;
        int margin = (numOfPieces >= 4) ? (int) Math.ceil(startingPane.getPrefWidth() / numOfPieces) : 10;

        for(int row = 0; row < rowCount; row++)
            for(int col = 0; col < colCount; col++)
            {
                if(numOfPieces < 1)
                    break;
                Rectangle rect =  playerList.get(numOfPieces-1).getPlayerPiece();

                rect.setLayoutX(col * (playerPieceSize + spacer) + margin);
                rect.setLayoutY(row * (playerPieceSize + spacer) + margin);

                startingPane.getChildren().add(rect);

                numOfPieces--;
            }
    }

    /* Dynamic Player Pieces
    * this method will take a cell that the player is moving too
    * and check how many players are on that cell
    * clears anything child of the cell that will be moved too
    * and creates/recreates the piece moving to the cell
    * and rearranges the old and new player pieces
    * then rearranges the player pieces in the cell
    * to have them space evenly and stay within the cell
    */

    private void dynamicPlayerPieces(int playerPieceSize,
                                     ArrayList<Rectangle> pieces, Pane selectedPane)
    {
        selectedPane.getChildren().clear();

        int numOfPieces = pieces.size();
        int paneWidth = (int) selectedPane.getPrefWidth();
        int paneHeight = (int) selectedPane.getPrefHeight();

        int rowCount = (int) Math.ceil((Math.sqrt(numOfPieces)));
        int colCount = (int) Math.ceil((double) numOfPieces / rowCount);

        int horizontalSpacer = (paneWidth - (colCount * playerPieceSize)) / (colCount+1);
        int verticalSpacer = (paneHeight - (rowCount * playerPieceSize)) / (rowCount+1);

        for(int i = 0; i < numOfPieces; i++)
        {
            Rectangle rect = pieces.get(i);

            int row = i / colCount;
            int col = i % colCount;

            rect.setLayoutX((col * playerPieceSize) + ((col + 1) * horizontalSpacer));
            rect.setLayoutY((row * playerPieceSize) + ((row + 1) * verticalSpacer));

            selectedPane.getChildren().add(rect);
        }

    }

    /* Make Player Pieces
    * checks how many plays are playing the game
    * and changes the size of the player pieces
    * depending on the player count
    * then calls dynamicPlayerPiece, which uses just the start pane
    */
    void makePlayerPieces(int playerCount)
    {
        double playerPieceSize;
        switch (playerList.size())
        {
            case 2 -> playerPieceSize = 50;
            case 3 ->  playerPieceSize = 45;
            case 4 ->  playerPieceSize = 40;
            case 5 ->  playerPieceSize = 35;
            case 6 ->  playerPieceSize = 30;
            case 7 ->  playerPieceSize = 25;
            default ->  playerPieceSize = 20;
        }
        dynamicPlayerPieces(playerPieceSize, playerCount);
    }

    /* Show Game Board
    * makes a new stage that will take the full screen
    * this new stage will show the playable game
    */
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


    //endregion



//region Actions After Roll

    /* Move Piece
    * takes an int value for how many spaces the player will move
    * then calculates the where the player will land
    * removing the current player pieces from the cell it is on
    * if the end pane has any child object
    * an arraylist will be created to get all the player
    * pieces on the end pane cell
    * then sets an id to each of the cells
    * the dynamic player pieces method is called passing in
    * the player piece width, the arraylist that was just made
    * piecesOnEndPane, and the pane the cell is parallel to
    * and places the player Piece to where it is going
    * then calls the landingLocationFunctions method
    */
    public void movePiece(int amountToMove)
    {
        Rectangle playerPiece = currentPlayer.getPlayerPiece();
        Pane startPane = paneList[getPlayerPosFromPaneList()];
        Pane endPane;

        startPane.getChildren().remove(playerPiece);
        currentPlayer.movePlayer(amountToMove);

        endPane = paneList[currentPlayer.getPosition()];
        if(!endPane.getChildren().isEmpty())
        {
            ArrayList<Rectangle> piecesOnEndPane = new ArrayList<>();

            for (Node n : endPane.getChildren())
            {
                if (n instanceof Rectangle)
                {
                    piecesOnEndPane.add((Rectangle) n);
                }
            }

            dynamicPlayerPieces((int) playerPiece.getWidth(), piecesOnEndPane, endPane);
        }

        endPane.getChildren().add(playerPiece);
        landingLocationFunctions();
    }

    /* Landing Location Functions
    * this method takes the cell that the current player is on
    * and checks the cell type, depending on the type of cell the player is on
    * a different method is called
    */
    private void landingLocationFunctions()
    {
        Cell cell = cellList.get(currentPlayer.getPosition());

        switch (cell.getType())
        {
            case "property" ->
            {
                if (cell.isOwned() && !cell.getOwnerName().equals(currentPlayer.getName()))
                    payRent(cell);
            }
            case "utility" -> payUtility();
            case "railroad" -> payRailRoad();
            case "tax" -> payTax();
            case "community-chest" -> openCommunityChest();
            case "chance" -> drawChanceCard();
            case "go-to-jail" -> goToJail();
        }
        player_land_space_LBL.setText(
                landSpaceInfo(cell));
        updateAllText();
    }

    /* Update All Text
    * changes all the text to be current
    * such as when users change or
    * when money is spent
    */
    private void updateAllText()
    {
        current_player_info_LBL.setText(currentPlayer.getName() + " has $" + currentPlayer.getBalance());
        current_player_properties_LBL.setText("");

        for (int i = 0; i < currentPlayer.getProperties().size(); i++)
        {
            current_player_properties_LBL.setText(current_player_properties_LBL.getText()
                    + "\n" + currentPlayer.getProperties().get(i).getName());
        }
        setPropertiesTable();
    }

    /* Landing Space Info
    * this method takes the cell that the player is going to land on
    * then takes the name of the cell and puts it in a String
    * then checks if the cell is owned by a player which will change the text
    * to show who owns the cell
    * then returns the string value
    */
    private String landSpaceInfo(Cell currLoc)
    {
        String textForLanding = "You have landed on:\n"+ currLoc.getName();
        boolean isPropertyType = currLoc.isOwned && (currLoc.getClass().getSimpleName())
                                .equalsIgnoreCase("property");
        if(currLoc.isOwned() && isPropertyType)
        {
            textForLanding += "\nThis space is owned by:\n" + currLoc.getOwnerName()
                    + "\nRent is $" + currLoc.getRent();
        }
        return textForLanding;
    }

    /* Go To Jail
    * sends the player to jail
    * setting the play's boolean value of inJail true
    * and setting the doubles boolean to false
    * so the player can no longer move
    * then moves the player piece to visually be in the jail pane
    * while setting the player to have their position set to 10,
    * which is the index of just visiting jail,
    */
    private void goToJail()
    {
        doubles = false;
        currentPlayer.setInJail(true);
        Rectangle playerPiece = currentPlayer.getPlayerPiece();

        paneList[currentPlayer.getPosition()].getChildren().remove(playerPiece);
        currentPlayer.setPosition(10);

        jail_Pane.getChildren().add(playerPiece);
        System.out.println(currentPlayer.getName() + " went to Jail");

    }

    private void drawChanceCard()
    {

    }

    private void openCommunityChest()
    {

    }

    private void payTax()
    {

    }

    private void payRailRoad()
    {

    }

    private void payUtility()
    {

    }

    /* Pay Rent
    * checks the cost of the cell and
    * takes money equal to the cost from the current players' balance
    * and adds it to the owner of the property's balance
    */
    private void payRent(Cell cell)
    {
        int amountPaid = cell.getCost();
        currentPlayer.setBalance(currentPlayer.getBalance() - amountPaid);
        cell.getOwner().setBalance(currentPlayer.getBalance() + amountPaid);
        updateGameLog(currentPlayer.getName(), cell.getName(), "pay-rent");
    }

    /* Purchase
    * gets the cell the player is on,
    * if the player has not moved then the game will send an alert
    * saying you can only buy properties after you move,
    * then checks if the selected cell is owned
    * makes and alert saying you cannot buy someone else's properties
    * if the cell is a property type and is not owned
    * and if the player can afford the cell
    * the cost is removed from the player's balance
    * and sets the cell to be owned and adds the cell to be
    * part of the player's own properties
    * then calls updateAllText
    */
    public void purchase(ActionEvent ignoredE)
    {
        Cell cellToBuy = cellList.get(currentPlayer.getPosition());
        if(!hasMoved)
        {
            Alert alert = DButils.makeAlert(2, "Players can only purchase after they move.");
            alert.show();
            return;
        }
        if(cellToBuy.isOwned())
        {
            Alert alert = DButils.makeAlert(3,
                    ("This property is already owned by " + cellToBuy.getOwnerName()));
            alert.show();
            return;
        }

        if(cellToBuy.getType().equalsIgnoreCase("property") && !cellToBuy.isOwned())
        {
            if(cellToBuy.getCost() <= currentPlayer.getBalance())
            {
                currentPlayer.setBalance(currentPlayer.getBalance()-cellToBuy.getCost());
                cellToBuy.setOwned(true, currentPlayer);
                currentPlayer.buyProperty(cellToBuy);

                updateGameLog(currentPlayer.getName(), cellToBuy.getName(), "buy-prop");
                updateAllText();
            }
        }
    }
    /* Set Properties Table
    * adds the properties owned by the current player to a table view
    **/
    private void setPropertiesTable()
    {
        ObservableList<Cell> listData;
        listData = currentPlayer.getProps();


        sell_property_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        sell_property_price.setCellValueFactory(new PropertyValueFactory<>("cost"));

        sell_tbv.setItems(listData);
    }
    public void ownedPropertyFunctions(ActionEvent e)
    {
        /*
        * once you select one a sell, a trade, and a mortgage button will appear
        * each will call a different method
        * trade and mortgage will close the current menu and show a new respective one
        * sell will just remove the property from the players class and make all instances of ownership for it false
        * */
        if(e.getSource().toString().contains("Sell"))
            sellProperty(sell_tbv.getSelectionModel().getSelectedItem());

    }

    private void sellProperty(Cell selectedItem)
    {
        Players ownerSelling = selectedItem.getOwner();

        ownerSelling.setBalance(ownerSelling.getBalance() + selectedItem.getCost());
        selectedItem.setOwned(false, null);

        updateGameLog(ownerSelling.getName(), selectedItem.getName(), "sell-prop");
    }

    private void updateGameLog(String playerName, String propertyName, String type)
    {
        Label firstLog = ((Label)game_log_pane.getChildren().get(0));
        Label secondLog = ((Label)game_log_pane.getChildren().get(1));
        Label thirdLog = ((Label)game_log_pane.getChildren().get(2));
        Label fourthLog = ((Label)game_log_pane.getChildren().get(3));

        ((Label)game_log_pane.getChildren().get(4)).setText(fourthLog.getText());
        ((Label)game_log_pane.getChildren().get(3)).setText(thirdLog.getText());
        ((Label)game_log_pane.getChildren().get(2)).setText(secondLog.getText());
        ((Label)game_log_pane.getChildren().get(1)).setText(firstLog.getText());

        switch (type)
        {
            case "buy-prop" -> firstLog.setText(playerName + " has bought " + propertyName + "!");
            case "sell-prop" -> firstLog.setText(playerName + " has sold " + propertyName + "!");
            case "pay-rent" -> firstLog.setText(playerName + "paid rent on " + propertyName + "!");
            case "chance" -> firstLog.setText(playerName + " drew a chance card!");
            case "jail" -> firstLog.setText(playerName + " got arrested!");
            case "chest" -> firstLog.setText(playerName + " got the community chest!");
            case "tax" -> firstLog.setText(playerName + " paid a tax!");
        }

    }

    private int getPlayerPosFromPaneList() { return currentPlayer.getPosition(); }

    //endregion

// region Other Info



    // endregion

//region Save/Close Game
    public void save(ActionEvent e) throws SQLException
    {
        String pressedButton = e.getSource().toString();

        Optional<String> result = td.showAndWait();

        if (result.isPresent())
        {
            String pass = td.getEditor().getText();

            DButils.completeSave(playerList, cellList, pass, td);

            if (pressedButton.contains("&"))
            {
                try
                {
                    quit(e);
                } catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    // Returns user to main menu
    public void quit(ActionEvent e) throws IOException
    {
        ((Node)(e.getSource())).getScene().getWindow().hide();

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Main Menu");

        Screen screen = Screen.getPrimary();
        Rectangle2D screenBounds = screen.getVisualBounds();

        FXMLLoader loader = new FXMLLoader(GameApplication.class.getResource("SetUp.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);
        stage.setY(screenBounds.getMaxY()/3);
        stage.setX(screenBounds.getMaxX()/3);
        stage.setScene(scene);
        stage.show();
    }
    //endregion

//region Keybindings
    public void keybindings(KeyEvent e)
    {
        ActionEvent keyStrokeEvent = new ActionEvent();

        if(!menus_stack.isVisible())
        {
            switch (e.getCode())
            {
//                case W -> dice.rollDice(roll_btn);
                case A -> togglePropertyFunctions(keyStrokeEvent);
                case S -> endTurn(keyStrokeEvent);
                case D -> purchase(keyStrokeEvent);
                case J -> goToJail();
            }
        }

        if(e.getCode() == KeyCode.ESCAPE)
            toggleSettings(keyStrokeEvent);
    }


    //endregion

// region Menu UI
    private void hideMenuPanes()
    {
        settings_pane.setVisible(false);
        property_functions_pane.setVisible(false);
        turn_switch_pane.setVisible(false);
    }

    public void toggleSettings(ActionEvent ignoredE)
    {
        hideMenuPanes();
        if(dimming_ap.isVisible())
        {
            menus_stack.setVisible(false);
            dimming_ap.setVisible(false);
            settings_pane.setVisible(false);
        }
        else
        {
            menus_stack.setVisible(true);
            dimming_ap.setVisible(true);
            settings_pane.setVisible(true);
        }
    }

    public void togglePropertyFunctions(ActionEvent ignoredKeyStrokeEvent)
    {
        if(menus_stack.isVisible())
        {
            menus_stack.setVisible(false);
            dimming_ap.setVisible(false);
            property_functions_pane.setVisible(false);
        }
        else
        {
            menus_stack.setVisible(true);
            dimming_ap.setVisible(true);
            property_functions_pane.setVisible(true);
        }

    }

    public void rollBtnOnClick(ActionEvent ignoredE)
    {
//        dice.rollDice(roll_btn);
    }

    //endregion
}




