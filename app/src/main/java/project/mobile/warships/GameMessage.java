package project.mobile.warships;

import java.io.Serializable;

/**
 * Created by coyle on 10/30/2015.
 */
public class GameMessage implements Serializable{

    protected String message;

    protected int yAxisMove;

    protected int xAxisMove;

    protected String messageType;

    public GameMessage(int xMove, int yMove, String type){
        yAxisMove = yMove;
        xAxisMove = xMove;
        messageType = type;
        message = "";
    }

    public GameMessage(String type, String setMessage){

        messageType = type;
        message = setMessage;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String set_message){
        message = set_message;
    }

    public int getyAxisMove() {
        return yAxisMove;
    }

    public int getxAxisMove() {
        return xAxisMove;
    }

    public String toString(){
        return "Game Message: " + message + " y: " + Integer.toString(yAxisMove) + " x: " +
                Integer.toString(xAxisMove);

    }
}
