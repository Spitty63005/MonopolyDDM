package com.example.monopoly_tm;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class GamePlayController implements Initializable
{
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
    ImageView right_die_one, right_die_two, right_die_three, right_die_four, right_die_five, right_die_six,
            left_die_one, left_die_two, left_die_three, left_die_four, left_die_five, left_die_six;

    @FXML
    StackPane left_stack, right_stack, menus_stack;

    @FXML
    Button roll_btn;

    TextInputDialog td = new TextInputDialog();

    ImageView[] rightDie = new ImageView[6];
    ImageView[] leftDie = new ImageView[6];

    boolean hasMoved = false;

    boolean doubles = false;

    static ArrayList<Players> playerList = new ArrayList<>();

//    these two arrays are parallel
    Pane[] paneList;
    ObservableList<Cell> cellList = FXCollections.observableArrayList();

    static Players currentPlayer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        // gets the file of the JSON file to read all the cell's information from
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
            // gets all the images for the die and adds them to an array
            setDieLists();
        }
        catch(Exception ex){
            throw new RuntimeException(ex);
        }

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


    //region turn start and end

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


    //region startOfGame

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

        System.out.println(margin);

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

    private void dynamicPlayerPieces(double playerPieceSize,
                                     ArrayList<Rectangle> pieces, Pane selectedPane)
    {
        int numOfPieces = pieces.size();

        int rowCount = (int) Math.ceil((Math.sqrt(numOfPieces)));
        int colCount = (int) Math.ceil((double) numOfPieces / rowCount);

        int spacer = 10;
        int margin = 10;

        selectedPane.getChildren().clear();

        for(int row = 0; row < rowCount; row++)
            for(int col = 0; col < colCount; col++)
            {
                int pieceIndex = row * colCount + col;
                if(pieceIndex >= numOfPieces)
                    break;
                Rectangle rect = pieces.get(pieceIndex);

                double layoutX = (col * (playerPieceSize + spacer) + margin) - (playerPieceSize/2);
                double layoutY = (row * (playerPieceSize + spacer) + margin) - (playerPieceSize/2);

                rect.setLayoutX(layoutX);
                rect.setLayoutY(layoutY);
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

    /* Roll Dice
    * sets the roll button to disabled to prevent a bug
    * then checks if the player has moved
    * or if the player has rolled doubles
    * if either are true it will begin
    * the rolling animation once completed the animation
    * the updateDieImages method
    */
    public void rollDice(ActionEvent ignoredAe)
    {
        roll_btn.setDisable(true);

        if(!hasMoved || doubles )
        {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.07), e -> rollingFrame()));
            timeline.setCycleCount(10);
            timeline.play();
            timeline.setOnFinished((e) -> updateDieImages());
        }
    }

    /* Update Die Images
    * stores the number that the two die rolls
    * in two int variables and prints out what is rolled
    * then adds the two values together to find how much
    * the player will move, then checks if the two sides
    * are of equal value to set the doubles boolean
    * then checks if doubles is true and enables the roll button
    * and frees you if you were in jail
    * then checks if the player is not in jail
    * if true the player forward the sum amount*/
    private void updateDieImages()
    {

        int currLeftDieValue = imageToInt(left_stack);
        int currRightDieValue = imageToInt(right_stack);

        System.out.println("left img: " + imageToInt(left_stack));
        System.out.println("right img: " + imageToInt(right_stack));

        System.out.println("Die one rolled: " + currLeftDieValue +"\nDie two rolled: " + currRightDieValue);

        int diceSum = currLeftDieValue + currRightDieValue;
        System.out.println(currentPlayer.getName() + " Moves Forward " + diceSum + " Spaces");

        hasMoved = true;
        doubles = currRightDieValue == currLeftDieValue;

        setImages(currLeftDieValue, currRightDieValue);

        if (doubles)
        {
            roll_btn.setDisable(false);
            currentPlayer.setInJail(false);
        }
        if(!currentPlayer.isInJail())
            movePiece(diceSum);

    }

    private void setImages(int left_die, int right_die)
    {
        left_stack.getChildren().get(0).setVisible(false);
        right_stack.getChildren().get(0).setVisible(false);

        left_stack.getChildren().clear();
        right_stack.getChildren().clear();

        System.out.println();
        System.out.println(left_die + " well " + right_die);



        left_stack.getChildren().add(leftDie[left_die - 1]);
        right_stack.getChildren().add(rightDie[right_die - 1]);

        left_stack.getChildren().get(0).setVisible(true);
        right_stack.getChildren().get(0).setVisible(true);
    }

    /* Image To Int
    * gets the toString node of the image currently displayed as rolled
    * on the die of the stack pane given,
    * then splits the string wherever there is an underscore
    * then a switch statement on the index 2
    * with a substring from the start to wherever
    * the first word ends by setting the end of the substring to
    * a comma then returns the value of which word is listed
    * this switch statement defaults to 1 if there is an unknown value
    */
    private int imageToInt(StackPane stack)
    {
        String result = stack.getChildren().get(0).toString();
        String[] imageViewSplit = result.split("_");

        switch (imageViewSplit[2].substring(0, imageViewSplit[2].indexOf(",")))
        {
            case "two" -> { return 2; }
            case "three" ->  { return 3; }
            case "four" ->  { return 4; }
            case "five" ->  { return 5; }
            case "six" ->  { return 6; }
            default ->  { return 1; }
        }
    }

    /* Rolling Frame
    * this is the method that makes the dice roll animation
    * it does this by setting the current die not be visible
    * then removing the die from the stack panes
    * and adding a new die image selected by a random number
    * and sets the visibility values of the new die to true
    */
    private void rollingFrame()
    {
        left_stack.getChildren().get(0).setVisible(false);
        right_stack.getChildren().get(0).setVisible(false);

        left_stack.getChildren().clear();
        right_stack.getChildren().clear();

        int leftNum = randDieNum();
        int rightNum = randDieNum();

        System.out.println(" -------- \n Random Nums\n left: " + leftNum +"\nright: " + rightNum + "\n -----------");

        left_stack.getChildren().add(leftDie[leftNum]);
        right_stack.getChildren().add(rightDie[rightNum]);

        left_stack.getChildren().get(0).setVisible(true);
        right_stack.getChildren().get(0).setVisible(true);

    }

    // Rand Die Num: returns a random number from 1 to 6
    private static int randDieNum()  { return (int) (Math.random() * (6 - 1) * 1); }

    /* Set Die Lists
    * this method sets the arrays for the left and right die
    * each array is initialized to have an imageview in each index,
    * all of which has the die reflect the index in which it is placed
    */
    private void setDieLists()
    {
        rightDie = new ImageView[]{right_die_one, right_die_two, right_die_three, right_die_four,
                right_die_five, right_die_six};

        leftDie = new ImageView[] {left_die_one, left_die_two, left_die_three, left_die_four,
                left_die_five, left_die_six};
    }

//endregion

    //region actions after roll

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
    private void movePiece(int amountToMove)
    {
        Rectangle playerPiece = currentPlayer.getPlayerPiece();
        Pane startPane = paneList[getPlayerPosFromPaneList()];
        Pane endPane;

        int startPaneInt = getPlayerPosFromPaneList();
        int endPaneInt = getPlayerPosFromPaneList() + amountToMove;

        startPane.getChildren().remove(playerPiece);
        currentPlayer.movePlayer(amountToMove);
        movingAnimation(startPaneInt, endPaneInt);

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

            dynamicPlayerPieces(playerPiece.getWidth(), piecesOnEndPane, endPane);
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

    private void movingAnimation(int endLoc, int currLoc)
    {
        System.out.println(endLoc + currLoc);
    }
//endregion

    // region Other Info



    // endregion

    //region save game and close game
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

    //region QoL
    public void keybindings(KeyEvent e)
    {
        ActionEvent keyStrokeEvent = new ActionEvent();

        if(!menus_stack.isVisible())
        {
            switch (e.getCode())
            {
                case W -> rollDice(keyStrokeEvent);
                case S -> endTurn(keyStrokeEvent);
                case D -> purchase(keyStrokeEvent);
                case ESCAPE -> toggleSettings(keyStrokeEvent);
            }
        }
        if(e.getCode() == KeyCode.A)
            togglePropertyFunctions(keyStrokeEvent);
    }


    //endregion


    // region ui
    private void hideMenuPanes()
    {
        settings_pane.setVisible(false);
        property_functions_pane.setVisible(false);
        turn_switch_pane.setVisible(false);
    }

    public void toggleSettings(ActionEvent ignoredE)
    {
        hideMenuPanes();
        if(menus_stack.isVisible())
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

    //endregion
}




