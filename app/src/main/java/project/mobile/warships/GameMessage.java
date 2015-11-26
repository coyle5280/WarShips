package project.mobile.warships;

import java.io.Serializable;

import GameBoardObjects.GameBoard;

/**
 * Created by coyle on 10/30/2015.
 */
public class GameMessage implements Serializable{

    protected String message;

    protected String shotArrayId;

    protected String messageType;

    protected GameBoard gameBoard;

    protected int textViewId;


    public GameMessage(String type, String shot, int textId, String setMessage){

        messageType = type;
        message = setMessage;
        shotArrayId = shot;
        textViewId = textId;

    }

    public GameMessage(String type, GameBoard myGameBoard, String setMessage){

        messageType = type;
        gameBoard = myGameBoard;
        message = setMessage;
    }

    public String getMessageType() {
        return messageType;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String set_message){
        message = set_message;
    }

    public String getShotArrayId() {
        return shotArrayId;
    }

    public int getTextViewId() {
        return textViewId;
    }

    public String toString(){
        return "Game Message: " + message;

    }
}
