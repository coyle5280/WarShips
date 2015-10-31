package project.mobile.warships;

/**
 * Created by coyle on 10/30/2015.
 */
public class GameMessage {

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

    public int getyAxisMove() {
        return yAxisMove;
    }

    public int getxAxisMove() {
        return xAxisMove;
    }
}
