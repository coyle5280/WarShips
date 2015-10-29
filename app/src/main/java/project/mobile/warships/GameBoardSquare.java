package project.mobile.warships;

/**
 * Created by coyle on 10/28/2015.
 */
public class GameBoardSquare {
    private boolean shotAt;
    private boolean occupied;
    private boolean firedAt;

    public GameBoardSquare (boolean setShotAt, boolean setOccupied, boolean setFiredAt){
        shotAt = setShotAt;
        occupied = setOccupied;
        firedAt = setFiredAt;
    }


    public boolean getShotAt(){
        return shotAt;
    }

    public boolean getOccupied(){
        return occupied;
    }

    public boolean getFiredAt(){
        return firedAt;
    }

}
