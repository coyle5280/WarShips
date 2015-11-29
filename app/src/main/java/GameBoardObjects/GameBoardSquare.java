package GameBoardObjects;

import java.io.Serializable;

/**
 * Created by coyle on 10/28/2015.
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
