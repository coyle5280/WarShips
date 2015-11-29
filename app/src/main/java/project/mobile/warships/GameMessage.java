package project.mobile.warships;

import java.io.Serializable;

import GameBoardObjects.GameBoard;
import GameBoardObjects.GameBoardSquare;

/**
 * Created by coyle on 10/30/2015.
 */
public class GameMessage implements Serializable{

    protected String message = "";

    protected String shotArrayId = "";

    protected String messageType = "";

    protected GameBoardSquare[][] gameBoard;

    protected int textViewId = -1;


    public GameMessage(String type, String shot, int textId, String setMessage){
        //gameMove
        messageType = type;
        message = setMessage;
        shotArrayId = shot;
        textViewId = textId;

    }

    public GameMessage(String type, GameBoardSquare[][] myGameBoard, String setMessage){
//        GameBoard
        messageType = type;
        gameBoard = myGameBoard;
        message = setMessage;
    }

    public GameMessage(String type, String setMessage){
        //Taunt
        messageType = type;
        message = setMessage;
    }

    public String getMessageType() {
        return messageType;
    }

    public GameBoardSquare[][] getGameBoard() {
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
        return "Game Message " + message;

    }
}
