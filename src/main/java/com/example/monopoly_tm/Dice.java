
package com.example.monopoly_tm;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.util.Duration;


public class Dice
{

    GamePlayController j = new GamePlayController();

    String leftRoll, rightRoll;

/* Roll Dice
     * sets the roll button to disabled to prevent a bug
     * then checks if the player has moved
     * or if the player has rolled doubles
     * if either are true it will begin
     * the rolling animation once completed the animation
     * the updateDieImages method
     */

    public void rollDice(Button roll_btn)
    {
        roll_btn.setDisable(true);
        if(!GamePlayController.hasMoved || GamePlayController.doubles )
        {
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.07), e -> rollingFrame()));
            timeline.setCycleCount(10);
            timeline.play();
            timeline.setOnFinished((e) -> GamePlayController.movePiece(updateDieImages(roll_btn)));
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

    public int updateDieImages(Button roll_btn)
    {

        int currLeftDieValue = imageToInt(leftRoll);
        int currRightDieValue = imageToInt(rightRoll);


        System.out.println("Die one rolled: " + currLeftDieValue +"\nDie two rolled: " + currRightDieValue);

        int diceSum = currLeftDieValue + currRightDieValue;
        System.out.println(currentPlayer.getName() + " Moves Forward " + diceSum + " Spaces");

        GamePlayController.hasMoved = true;
        GamePlayController.doubles = currRightDieValue == currLeftDieValue;

        if (GamePlayController.isHaveMoved())
        {
            roll_btn.setDisable(false);
            currentPlayer.setInJail(false);
        }
        if(currentPlayer.isInJail())
            return 0;
        else
            return diceSum;
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

    private int imageToInt(String die)
    {
        String[] imageViewSplit = die.split("_");

        int index = imageViewSplit[2].indexOf('.');
        String number = imageViewSplit[2].substring(0, index);

        switch (number.toLowerCase())
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
        int left = randDieNum();
        int right = randDieNum();

        leftRoll = GamePlayController.filePaths[left];
        rightRoll = GamePlayController.filePaths[right];

        GamePlayController.left_die.setImage(all_rolls[left]);
        GamePlayController.right_die.setImage(all_rolls[right]);

    }

    // Rand Die Num: returns a random number from 1 to 6
    private int randDieNum()  { return (int) (Math.random() * (6 - 1) * 1); }

}

