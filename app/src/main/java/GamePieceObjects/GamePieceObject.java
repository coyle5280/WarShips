package GamePieceObjects;

/**
 * Created by coyle on 10/26/2015.
 */
public class GamePieceObject {


    private int length;
    private String type;


    public GamePieceObject(int length, String type){
        this.length = length;
        this.type = type;
    }

    public String getType() {
        return this.type;
    }


    public void setLength(int length) {
        this.length = length;
    }

    public int getLength(){
        return this.length;
    }

    public boolean hit(){
        length = length -1;
        return length == 0;
    }

}
