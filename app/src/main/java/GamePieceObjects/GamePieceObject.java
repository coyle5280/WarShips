package GamePieceObjects;

/**
 * Created by coyle on 10/26/2015.
 */
public class GamePieceObject {


    private int xPosition;
    private int yPosition;
    private boolean isAlive;
    private boolean isPlaced;
    private int length;


    public void GamePiece(){
        xPosition = -1;
        yPosition = -1;
        isAlive = true;
        isPlaced = false;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
