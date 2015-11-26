package GameBoardObjects;

import android.util.Log;

/**
 * Created by coyle on 10/26/2015.
 */
public class GameBoard {


    private int sizeX;

    private int sizeY;


    private GameBoardSquare gameBoardArray[][];


    public GameBoard(int playerSizeX, int playerSizeY) {

        sizeX = playerSizeX;
        sizeY = playerSizeY;

        gameBoardArray = new GameBoardSquare[sizeX][sizeY];


    }


    public GameBoard() {

        sizeX = 8;
        sizeY = 8;

        gameBoardArray = new GameBoardSquare[sizeX][sizeY];


    }


    public void setOccupied(String ship, String location) {
        int xShot = convertCharToInt(location.charAt(0));
        int yShot = convertCharToInt(location.charAt(1));
        if (xShot == -1 || yShot == -1) {
            Log.e("WarShip", "setOccupied: xShot: " + xShot + "yShot: " + yShot);
        }
        gameBoardArray[xShot][yShot].setTypeOccupied(ship);
    }

    public void setMyShot(String location){
        int xShot = convertCharToInt(location.charAt(0));
        int yShot = convertCharToInt(location.charAt(1));
        if (xShot == -1 || yShot == -1) {
            Log.e("WarShip", "setMyShot: xShot: " + xShot + "yShot: " + yShot);
        }
        gameBoardArray[xShot][yShot].setShotAt();
    }


    public String getShotResult(String location) {
        int xShot = convertCharToInt(location.charAt(0));
        int yShot = convertCharToInt(location.charAt(1));
        if (xShot == -1 || yShot == -1) {
            Log.e("WarShip", "getShotResult: xShot: " + xShot + "yShot: " + yShot);
            return "ERROR";
        }
        if (gameBoardArray[xShot][yShot].getOccupied()) {
            gameBoardArray[xShot][yShot].setEnemyFire();
            return gameBoardArray[xShot][yShot].getTypeOccupied();
        } else {
            gameBoardArray[xShot][yShot].setEnemyFire();
            return "Empty";
        }
    }


    private int convertCharToInt(char shotChar) {
        switch (shotChar) {
            case '1':
            case 'A':
                return 0;
            case '2':
            case 'B':
                return 1;
            case '3':
            case 'C':
                return 2;
            case '4':
            case 'D':
                return 3;
            case '5':
            case 'E':
                return 4;
            case '6':
            case 'F':
                return 5;
            case '7':
            case 'G':
                return 6;
            case '8':
            case 'H':
                return 7;
            default:
                return -1;
        }
    }
}
