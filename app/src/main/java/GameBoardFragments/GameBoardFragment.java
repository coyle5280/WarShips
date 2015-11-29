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
    private int shipPlaceCounter;


    private int thisBoard;
    private boolean isHost;

    private ArrayList<GamePieceObject> arrayShipsNeedPlacing = new ArrayList<GamePieceObject>();
    private ArrayList<GamePieceObject> arrayMyShipsActive = new ArrayList<GamePieceObject>();
    private ArrayList<GamePieceObject> arrayMyShipsDead = new ArrayList<GamePieceObject>();

    private ArrayList<GamePieceObject> arrayOppShipsActive = new ArrayList<GamePieceObject>();
    private ArrayList<GamePieceObject> arrayOppShipsDead = new ArrayList<GamePieceObject>();

    private View fragView;

    private TextView shipList;
    private TextView placeShipHeader;
    private TextView header;
    private TextView shipPlaceCount;

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

    public void setUpBoardView(){
        if(isHost){
            if(thisBoard == 0){
                header = (TextView) fragView.findViewById(R.id.headerA);
                shipList = (TextView) fragView.findViewById(R.id.shipTextViewA);
                placeShipHeader = (TextView) fragView.findViewById(R.id.placeShipHeaderA);
                shipPlaceCount = (TextView) fragView.findViewById(R.id.shipCountA);
                shipList.setText(arrayShipsNeedPlacing.get(0).getType());
                placeShipHeader.setVisibility(View.VISIBLE);
                shipList.setVisibility(View.VISIBLE);
                shipPlaceCount.setVisibility(View.VISIBLE);
                shipPlaceCounter = arrayShipsNeedPlacing.get(0).getLength();
                shipPlaceCount.setText("Spots: " + shipPlaceCounter);
                header.setText("My Board");


            }else{
                header = (TextView) fragView.findViewById(R.id.headerB);
                header.setText("Opponent Board");
            }
        }else{
            if(thisBoard == 1){
                header = (TextView) fragView.findViewById(R.id.headerB);
                placeShipHeader = (TextView) fragView.findViewById(R.id.placeShipHeaderB);
                shipList = (TextView) fragView.findViewById(R.id.shipTextViewA);
                shipPlaceCount = (TextView) fragView.findViewById(R.id.shipCountA);
                shipList.setText(arrayShipsNeedPlacing.get(0).getType());
                placeShipHeader.setVisibility(View.VISIBLE);
                shipList.setVisibility(View.VISIBLE);
                shipPlaceCount.setVisibility(View.VISIBLE);
                shipPlaceCounter = arrayShipsNeedPlacing.get(0).getLength();
                shipPlaceCount.setText("Spots: " + shipPlaceCounter);
                header.setText("My Board");

            }else{
                header = (TextView) fragView.findViewById(R.id.headerA);
                header.setText("Opponent Board");

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

            fragView = inflater.inflate(R.layout.game_board_a, container, false);

        }else{
            fragView = inflater.inflate(R.layout.game_board_b, container, false);

        }
        setUpBoardView();
        return fragView;

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
                    mCallback.myShotHitToStats();
                    break;
                case "AttackSubmarine":
                    attackedTextView.setBackgroundColor(Color.parseColor(attackHit));
                    mCallback.myShotHitToStats();
                    break;
                case "PtBoat":
                    attackedTextView.setBackgroundColor(Color.parseColor(attackHit));
                    mCallback.myShotHitToStats();
                    break;
                case "AircraftCarrier":
                    attackedTextView.setBackgroundColor(Color.parseColor(attackHit));
                    mCallback.myShotHitToStats();
                    break;
                case "AegisCruiser":
                    attackedTextView.setBackgroundColor(Color.parseColor(attackHit));
                    mCallback.myShotHitToStats();
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

    public void setMyShip(String currentShotStringId, int currentShotIntId){
        TextView setColor = (TextView) fragView.findViewById(currentShotIntId);
        setColor.setBackgroundColor(Color.parseColor(shipLocation));
        shipPlaceCounter--;
        myGameBoard.setOccupied(arrayShipsNeedPlacing.get(0).getType(), currentShotStringId);
        if(shipPlaceCounter == 0){
            arrayMyShipsActive.add(arrayShipsNeedPlacing.get(0));
            arrayShipsNeedPlacing.remove(0);
            if(arrayShipsNeedPlacing.size() != 0) {
                shipPlaceCounter = arrayShipsNeedPlacing.get(0).getLength();
            }else{
                mCallback.sendMyBoardToOpp(myGameBoard);
                placeShipHeader.setVisibility(View.INVISIBLE);
                shipList.setVisibility(View.INVISIBLE);
                shipPlaceCount.setVisibility(View.INVISIBLE);

            }
        }


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
        void sendMyBoardToOpp(GameBoard board);
        void myShotHitToStats();
        String getArrayId(int id);

    }



    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
