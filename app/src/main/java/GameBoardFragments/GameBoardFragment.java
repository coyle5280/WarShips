package GameBoardFragments;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Console;
import java.util.ArrayList;

import GameBoardObjects.GameBoard;

import GamePieceObjects.GamePieceObject;

import project.mobile.warships.R;


public class GameBoardFragment extends Fragment {

    private int STATUS;

    private final int SETUPBOARD = 0;
    private final int VIEW = 1;
    private final int GAMEBOARD_A = 0;
    private final int GAMEBOARD_B = 1;
    private int thisBoard;
    private boolean isHost;

    private ArrayList<GamePieceObject> arrayShipsNeedPlacing = new ArrayList<GamePieceObject>();
    private ArrayList<GamePieceObject> arrayMyShipsActive = new ArrayList<GamePieceObject>();
    private ArrayList<GamePieceObject> arrayMyShipsDead = new ArrayList<GamePieceObject>();

    private ArrayList<GamePieceObject> arrayOppShipsActive = new ArrayList<GamePieceObject>();
    private ArrayList<GamePieceObject> arrayOppShipsDead = new ArrayList<GamePieceObject>();

    private TextView testingColors;
    private TextView testingColors2;

    private String oppAttackMissColor =  "#ffff00";
    private String myAttackMissColor = "#f8f8ff";
    private String shipLocation = "#7c7c7f";
    private String attackHit = "#cd2626";

    private GameBoard oppGameBoard;
    private GameBoard myGameBoard;

    private sendInfoToActivity mCallback;


    //Set Color Methods
    public void setAttackHitColor(String attackHit) {
        Log.e("WarShip", "ColorLog: " + attackHit);
        this.attackHit = attackHit;
        testingColors.setBackgroundColor(Color.parseColor(attackHit));
        testingColors2.setBackgroundColor(Color.parseColor(attackHit));
    }

    public void setMyAttackMissColor(String myAttackMissColor) {
        this.myAttackMissColor = myAttackMissColor;

    }

    public void setShipLocationColor(String shipLocation) {
        this.shipLocation = shipLocation;
    }

    public void setOppAttackMissColor(String oppAttackMissColor) {
        this.oppAttackMissColor = oppAttackMissColor;
    }

    public void myBoardOnClick(View v){
        if(isHost){
            if(thisBoard == 0){
                //No click event Visual Only




                Log.e("WarShip", "myBoardOnClick: Host Board A MyBoard");
            }else{
                //this should be Host board B
                Log.e("WarShip", "myBoardOnClick: Host Board B OppBoard");
                TextView clickedView = (TextView) v;
                int currentShotIntId = clickedView.getId();
                String currentShotStringId = getResources().getResourceEntryName(currentShotIntId);
                Log.e("WarShip", "myBoardOnClick: " + currentShotStringId.toString());
                mCallback.sendMyShotToActivity(currentShotStringId, currentShotIntId);
                setMyShot(currentShotStringId, currentShotIntId);
            }
        }else{
            if(thisBoard == 1){
                //No click event Visual Only
                Log.e("WarShip", "myBoardOnClick: Client Board B MyBoard");
            }else{
                //This should be client board A
                Log.e("WarShip", "myBoardOnClick: Client Board A OppBoard");
                TextView clickedView = (TextView) v;
                int currentShotIntId = clickedView.getId();
                String currentShotStringId = getResources().getResourceEntryName(currentShotIntId);
                Log.e("WarShip", "myBoardOnClick: " + currentShotStringId.toString());
                mCallback.sendMyShotToActivity(currentShotStringId, currentShotIntId);
                setMyShot(currentShotStringId, currentShotIntId);
            }
        }
    }




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
        STATUS = SETUPBOARD;

        setupInitalShipArray();

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            thisBoard = bundle.getInt("board");
            isHost = bundle.getBoolean("isHost");
        }

        if(thisBoard == 0){

            return inflater.inflate(R.layout.game_board_a, container, false);
        }else{
            return inflater.inflate(R.layout.game_board_b, container, false);
        }


    }

    private void setupInitalShipArray() {
        arrayShipsNeedPlacing.add(new GamePieceObject(5, "AircraftCarrier"));
        arrayShipsNeedPlacing.add(new GamePieceObject(4, "BattleShip"));
        arrayShipsNeedPlacing.add(new GamePieceObject(3, "AegisCruiser"));
        arrayShipsNeedPlacing.add(new GamePieceObject(3, "AttackSubmarine"));
        arrayShipsNeedPlacing.add(new GamePieceObject(2, "PtBoat"));

    }

    public void setOppGameBoard(GameBoard gameBoard){
        arrayOppShipsActive.add(new GamePieceObject(5, "AircraftCarrier"));
        arrayOppShipsActive.add(new GamePieceObject(4, "BattleShip"));
        arrayOppShipsActive.add(new GamePieceObject(3, "AegisCruiser"));
        arrayOppShipsActive.add(new GamePieceObject(3, "AttackSubmarine"));
        arrayOppShipsActive.add(new GamePieceObject(2, "PtBoat"));

        this.oppGameBoard = gameBoard;
    }


    public void setMyShot(String location, int textViewId){
        try{
            TextView attackedTextView = (TextView) getView().findViewById(textViewId);
            switch(oppGameBoard.getShotResult(location)){
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
                    attackedTextView.setBackgroundColor(Color.parseColor(myAttackMissColor));
                    break;
                case "ERROR":
                    Log.e("Warship", "setMyShot: ERROR FINDING SPOT IN oppGameBoard");
                    //TODO call end game
                    break;
            }
        }catch(NullPointerException e){
            Log.e("Warship", "setMyShot: NullPointer", e);
        }
        //TODO go back to activity??
    }


    public void setOppAttacked(String arrayShotId, int textViewId){

        try{
            TextView attackedTextView = (TextView) getView().findViewById(textViewId);
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
                    Log.e("Warship", "setOppAttacked: ERROR FINDING SPOT IN myGameBoard");
                    //TODO call end game Out of sync
                    break;
            }
        }catch(NullPointerException e){
            Log.e("Warship", "setOppAttacked: NullPointer", e);
        }



    }

    public interface sendInfoToActivity{
        void sendMyShotToActivity(String stringId, int textId);
        void sendMyBoardToOpp(GameBoard board);

    }



    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        testingColors = (TextView) view.findViewById(R.id.A1);
        testingColors2 = (TextView) view.findViewById(R.id.OA1);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallback = (sendInfoToActivity) context;
        }catch(ClassCastException e){
            throw new ClassCastException(getActivity().toString() + "not implementing Interface");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



}
