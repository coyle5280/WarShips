package GameBoardFragments;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import GameBoardObjects.GameBoard;
import project.mobile.warships.R;


public class GameBoardFragment extends Fragment {

    private int STATUS;

    private final int SETUPBOARD = 0;
    private final int VIEW = 1;
    private final int GAMEBOARD_A = 0;
    private final int GAMEBOARD_B = 1;
    private int thisBoard;

    private String oppAttackMissColor =  "#ffff00";
    private String myAttackMissColor = "#f8f8ff";
    private String shipLocation = "#7c7c7f";
    private String attackHit = "cd2626";

    private GameBoard oppGameBoard;
    private GameBoard myGameBoard;

    public void setAttackHit(String attackHit) {
        this.attackHit = attackHit;
    }

    public void setMyAttackMissColor(String myAttackMissColor) {
        this.myAttackMissColor = myAttackMissColor;
    }

    public void setShipLocation(String shipLocation) {
        this.shipLocation = shipLocation;
    }

    public void setOppAttackMissColor(String oppAttackMissColor) {
        this.oppAttackMissColor = oppAttackMissColor;
    }


//    private OnFragmentInteractionListener mListener;



    public GameBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        STATUS = 0;

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            thisBoard = bundle.getInt("board");
        }

        if(thisBoard == 0){
            return inflater.inflate(R.layout.game_board_a, container, false);
        }else{
            return inflater.inflate(R.layout.game_board_b, container, false);
        }


    }

    public void setOppGameBoard(GameBoard gameBoard){
        this.oppGameBoard = gameBoard;
    }


    public void setMyShot(String location){

    }


    public void setOppAttacked(String arrayShotId, int textViewId){
      TextView attackedTextView = (TextView) getView().findViewWithTag(textViewId);

        switch(myGameBoard.getShotResult(arrayShotId)){
            case "BattleShip":
                attackedTextView.setBackgroundColor(Color.parseColor(attackHit));
                break;
            case "AttackSubmarine":
                attackedTextView.setBackgroundColor(Color.parseColor(attackHit));
                break;
            case "PtBoat":
                attackedTextView.setBackgroundColor(Color.parseColor(attackHit));
                break;
            case "AircraftCarrier":
                attackedTextView.setBackgroundColor(Color.parseColor(attackHit));
                break;
            case "AegisCruiser":
                attackedTextView.setBackgroundColor(Color.parseColor(attackHit));
                break;
            case "Empty":
                attackedTextView.setBackgroundColor(Color.parseColor(oppAttackMissColor));
                break;
            case "ERROR":
                //TODO call end game and log error
                break;
        }
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



}
