package project.mobile.warships;

/**
 * Created by coyle on 10/25/2015.
 */
public class AegisCruiserObject {
    private int health;
    private String positionRef;
    private String shapeRef;
    private boolean isAlive;
    private boolean isPlaced;

    public void AegisCruiserObject(){
        health = 100;
        positionRef = "";
        shapeRef = "";
        isAlive = true;
        isPlaced = false;
    }

    public int getHealth() {
        return health;
    }

    public String getPositionRef() {
        return positionRef;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public String getShapeRef() {
        return shapeRef;
    }
}
