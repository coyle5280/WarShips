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
        gameBoardArray = new GameBoardSquare[8][8];
        for(int row = 0; row < 8; row++){
            for(int col = 0; col < 8; col++){
                gameBoardArray[row][col] = new GameBoardSquare();
            }
        }
    }


    public void setOccupied(String ship, String location) {
        int xShot = convertCharToInt(location.charAt(0));
        int yShot = convertCharToInt(location.charAt(1));
        if (xShot == -1 || yShot == -1) {
            Log.e("WarShip", "setOccupied: xShot: " + xShot + " yShot: " + yShot);
        }
        Log.e("WarShip", "setOccupied: xShot: " + xShot + " yShot: " + yShot);
        GameBoardSquare current = gameBoardArray[xShot][yShot];
        Log.e("WarShip", "setOccupied: " + current);
        current.setTypeOccupied(ship);
    }

//    public String setMyShot(String location){
//        int xShot = convertCharToInt(location.charAt(0));
//        int yShot = convertCharToInt(location.charAt(1));
//        if (xShot == -1 || yShot == -1) {
//            Log.e("WarShip", "setMyShot: xShot: " + xShot + "yShot: " + yShot);
//        }
//        if (gameBoardArray[xShot][yShot].getOccupied()) {
//            gameBoardArray[xShot][yShot].setShotAt();
//            return gameBoardArray[xShot][yShot].getTypeOccupied();
//        } else {
//            gameBoardArray[xShot][yShot].setShotAt();
//            return "Empty";
//        }
//    }


    public String getShotResult(String location) {
        int xShot = convertCharToInt(location.charAt(0));
        int yShot = convertCharToInt(location.charAt(1));
        if (xShot == -1 || yShot == -1) {
            Log.e("WarShip", "getShotResult: xShot: " + xShot + "yShot: " + yShot);
            return "ERROR";
        }
        if (gameBoardArray[xShot][yShot].getOccupied()) {
            gameBoardArray[xShot][yShot].setShotAt();
            return gameBoardArray[xShot][yShot].getTypeOccupied();
        } else {
            gameBoardArray[xShot][yShot].setShotAt();
            return "Empty";
        }
    }


    private int convertCharToInt(char shotChar) {
        switch (shotChar) {
            case '1':
            case 'A':
            case 'a':
                return 0;
            case '2':
            case 'B':
            case 'b':
                return 1;
            case '3':
            case 'C':
            case 'c':
                return 2;
            case '4':
            case 'D':
            case 'd':
                return 3;
            case '5':
            case 'E':
            case 'e':
                return 4;
            case '6':
            case 'F':
            case 'f':
                return 5;
            case '7':
            case 'G':
            case 'g':
                return 6;
            case '8':
            case 'H':
            case 'h':
                return 7;
            default:
                return -1;
        }
    }
}
