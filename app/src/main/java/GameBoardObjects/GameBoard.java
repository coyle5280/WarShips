package GameBoardObjects;

/**
 * Created by coyle on 10/26/2015.
 */
public class GameBoard {


private int sizeX;

private int sizeY;


private GameBoardSquare gameBoard [][];



    public void GameBoard(int playerSizeX, int playerSizeY){

        sizeX = playerSizeX;
        sizeY = playerSizeY;

        gameBoard = new GameBoardSquare[sizeX][sizeY];


    }


    public void GameBoard(){

        sizeX = 8;
        sizeY = 8;

        gameBoard = new GameBoardSquare[sizeX][sizeY];


    }




}
