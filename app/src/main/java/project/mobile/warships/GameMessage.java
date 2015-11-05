package project.mobile.warships;

import java.io.Serializable;

/**
 * Created by coyle on 10/30/2015.
 */
public class GameMessage implements Serializable{

    protected String message;

    protected int yAxisMove;

    protected int xAxisMove;

    public GameMessage(int yMove, int xMove){
        yAxisMove = yMove;
        xAxisMove = xMove;
        message = "";
    }

    public GameMessage(int yMove, int xMove, String setMessage){
        yAxisMove = yMove;
        xAxisMove = xMove;
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
