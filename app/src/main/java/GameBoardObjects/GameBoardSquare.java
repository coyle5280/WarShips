package GameBoardObjects;

/**
 * Created by coyle on 10/28/2015.
 */
public class GameBoardSquare {
    private boolean enemyFire;
    private boolean occupied;
    private boolean firedAt;
    private String typeOccupied;

    public GameBoardSquare(boolean setShotAt, boolean setOccupied, boolean setFiredAt) {
        enemyFire = setShotAt;
        occupied = setOccupied;
        firedAt = setFiredAt;
    }

    public GameBoardSquare() {
    }

    public void setEnemyFire() {
        this.enemyFire = true;
    }


    public void setTypeOccupied(String typeOccupied) {
        this.typeOccupied = typeOccupied;
        this.occupied = true;
    }

    public boolean getShotAt() {
        return firedAt;
    }

    public boolean getOccupied() {
        return occupied;
    }

    public String getTypeOccupied() {
        return typeOccupied;
    }

    public void setShotAt() {
        firedAt = true;
    }
}
