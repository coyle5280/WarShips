package project.mobile.warships;

/**
 * Created by coyle on 10/28/2015.
 */
public class GameBoardSquare {
    private boolean enemyFire;
    private boolean occupied;
    private boolean firedAt;

    public GameBoardSquare (boolean setShotAt, boolean setOccupied, boolean setFiredAt){
        enemyFire = setShotAt;
        occupied = setOccupied;
        firedAt = setFiredAt;
    }

    public GameBoardSquare(){

    }

    public void setEnemyFire(boolean enemyFire) {
        this.enemyFire = enemyFire;
    }

    public void setFiredAt(boolean firedAt) {
        this.firedAt = firedAt;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean getShotAt(){
        return enemyFire;
    }

    public boolean getOccupied(){
        return occupied;
    }

    public boolean getFiredAt(){
        return firedAt;
    }

}
