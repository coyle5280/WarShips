package GameBoardObjects;

import java.io.Serializable;

/**
 * @author Josh Coyle
 * @author Robert Slavik
 */

public class GameBoardSquare implements Serializable{
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
    public String toString(){
        return ("Occupied: " + occupied + "shot at: " + shotAt + "Type: " + typeOccupied);
    }
}
