package GameBoardObjects;

/**
 * Created by coyle on 10/28/2015.
 */
public class GameBoardSquare {
    private boolean shotAt = false;
    private boolean occupied = false;
    private String typeOccupied = "";

    public GameBoardSquare() {
    }



    public void setTypeOccupied(String typeOccupied) {
        this.typeOccupied = typeOccupied;
        this.occupied = true;
    }

    public boolean getShotAt() {
        return shotAt;
    }

    public boolean getOccupied() {
        return occupied;
    }

    public String getTypeOccupied() {
        return typeOccupied;
    }

    public void setShotAt() {
        shotAt = true;
    }
}
