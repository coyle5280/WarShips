package project.mobile.warships;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


import GameBoardFragments.GameBoardFragment;
import GameBoardObjects.GameBoard;
import GameStatsFragments.GameStats;
import GameStatsFragments.PlayerStats;

/**
 * @author Robert Slavik
 * @author Josh Coyle
 */
public class WarShipGame extends Activity  implements SensorEventListener, GameBoardFragment.sendInfoToActivity{

    protected GameBoard myBoard;


    protected ConnectedThread bluetoothConnection;
    public TextView messageView;
    public TextView statusView;
    private EditText editMessage;


    final String GAMEBOARD = "gameBoard";
    final String GAMEMOVE = "gameMove";
    final String TAUNT = "messOnly";
    final String NO_MESSAGE = "No Message";

    private int myShotTextViewId;
    private String myShotArrayString;

    private boolean isHost;

    private FragmentManager fragManager;
    private FragmentTransaction fragmentTransaction;

    private static final int SETTING_UP_BOARD = -1;
    private static final int WAITING_ON_SELF = -2;
    private static final int WAITING_ON_PLAYER = 0;
    private static final int MY_TURN = 1;
    private static final int OPP_TURN = 2;
    private int STATUS;
    private boolean oppBoardReady = false;
    private boolean myBoardReady = false;

    private Button myGameBoardButton;
    private Button myOppBoardButton;

    protected ActionBar actionBar;

    private GameBoardFragment myGameBoardFrag;
    private GameBoardFragment oppGameBoardFrag;

    private SensorManager sensorManager;
    private Sensor shakeSensor;

    private Button sendTurn;
    private TextView currentMoveTextView;



    float gravity[];
    final private float MIN_ACCELERATION = 9.0f;

    private int activeStatsFragment = 0;

    private GameStats gameStats;
    private PlayerStats playerStats;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActionBar();
        setContentView(R.layout.war_ship_game);
        setHost();
        setupConnectionItems();
        setupFragments();
        setupColors();
        setupItems();
        setupSharedPrefs();

    }

    private void setupSharedPrefs() {
        Bundle theExtras = getIntent().getExtras();
        if (theExtras != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("User", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("Games", theExtras.getInt("Games"));


        }
    }

    private void setupFragments() {
        if(isHost) {
            Bundle newFragBundle = new Bundle();
            newFragBundle.putInt("board", 0);
            newFragBundle.putBoolean("isHost", isHost);
            myGameBoardFrag = new GameBoardFragment();
            myGameBoardFrag.setArguments(newFragBundle);

            Bundle newFragBundle2 = new Bundle();
            newFragBundle2.putInt("board", 1);
            newFragBundle2.putBoolean("isHost", isHost);
            oppGameBoardFrag = new GameBoardFragment();
            oppGameBoardFrag.setArguments(newFragBundle2);

        }else{
            Bundle newFragBundle = new Bundle();
            newFragBundle.putInt("board", 1);
            newFragBundle.putBoolean("isHost", isHost);
            myGameBoardFrag = new GameBoardFragment();
            myGameBoardFrag.setArguments(newFragBundle);

            Bundle newFragBundle2 = new Bundle();
            newFragBundle2.putInt("board", 0);
            newFragBundle2.putBoolean("isHost", isHost);
            oppGameBoardFrag = new GameBoardFragment();
            oppGameBoardFrag.setArguments(newFragBundle2);
        }
        gameStats = new GameStats();
        playerStats = new PlayerStats();
    }

    /**
     *
     */
    private void setHost() {
        Bundle theExtras = getIntent().getExtras();
        if (theExtras != null) {
            isHost = theExtras.getBoolean("isHost");

        }else {
            isHost = false;
            CharSequence mess = ">>>>>>ERROR GETTING HOST PLAYER SETTING<<<<<<<";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(),mess, duration);
            toast.show();
        }
    }

    private void setupColors(){
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            if (extras.getInt("attackHit") != 0) {
                int a = extras.getInt("attackHit");
                myGameBoardFrag.setAttackHitColor("#" + Integer.toHexString(a).substring(2));
                oppGameBoardFrag.setAttackHitColor("#" + Integer.toHexString(a).substring(2));
            }
            if (extras.getInt("attackMiss") != 0) {
                int b = extras.getInt("attackMiss");
                myGameBoardFrag.setMyAttackMissColor("#" + Integer.toHexString(b).substring(2));
                oppGameBoardFrag.setMyAttackMissColor("#" + Integer.toHexString(b).substring(2));
            }
            if (extras.getInt("opAttackMiss") != 0) {
                int c = extras.getInt("opAttackMiss");
                myGameBoardFrag.setOppAttackMissColor("#" + Integer.toHexString(c).substring(2));
                oppGameBoardFrag.setOppAttackMissColor("#" + Integer.toHexString(c).substring(2));
            }
            if (extras.getInt("ship") != 0) {
                int d = extras.getInt("ship");
                myGameBoardFrag.setShipLocationColor("#" + Integer.toHexString(d).substring(2));
                oppGameBoardFrag.setShipLocationColor("#" + Integer.toHexString(d).substring(2));
            }
        }
    }

    /**
     *
     */
    private void setupConnectionItems() {
        GameSocket gameSocket = GameSocket.getInstance();
        BluetoothSocket socket = gameSocket.getGameSocket();


        Handler connectionHandler = new Handler() {
            @Override
            public void handleMessage(Message inputMessage) {
                byte[] incoming = (byte[]) inputMessage.obj;
                try {
                    Log.e("WarGame:InGameInMes:", "handler Incoming message");
                    GameMessage incomingMessage = (GameMessage) convertToGameMessage(incoming);
                    //convertToGameMessage(incoming);
                    Log.e("WarGame:InGameInMes:", "Actual String: " + incomingMessage.getMessage());

                    processGameMessage(incomingMessage);

                 } catch (ClassNotFoundException | IOException e) {
                    Log.e("WarGame:InGameInMesERR:", e.toString());
                }
            }
        };
        bluetoothConnection  = new ConnectedThread(socket, connectionHandler);
        bluetoothConnection.start();
    }

    private void processGameMessage(GameMessage incomingMessage) {
        GameMessage newMessage = incomingMessage;


        switch(newMessage.getMessageType()){
            case GAMEBOARD:
                //oppGameBoardFrag.setOppGameBoard(newMessage.getGameBoard());
                Log.e("WarShip", "gameStringArray:" + incomingMessage.gameBoardString.toString());
                setOppMessage(newMessage.getMessage());
                oppBoardReady = true;
                updateStatus();
                break;
            case GAMEMOVE:
                myGameBoardFrag.setOppAttacked(newMessage.getShotArrayId(),newMessage.getTextViewId());
                updateStatus();
                setOppMessage(newMessage.getMessage());
                //TODO add counter for shared preferences and call stats fragment to update
                break;
            case TAUNT:
                messageView.setText(incomingMessage.getMessage());
        }
    }

    private void updateStatus() {
        if(STATUS == OPP_TURN){
            statusView.setText("My Turn");
            STATUS = MY_TURN;
        }else if(STATUS == MY_TURN){
            statusView.setText("Opp Turn");
            STATUS = OPP_TURN;
        }else if(STATUS == SETTING_UP_BOARD && oppBoardReady){
            statusView.setText("Waiting On Self");
           STATUS = WAITING_ON_SELF;
        }else if(STATUS == SETTING_UP_BOARD && !oppBoardReady && myBoardReady){
            statusView.setText("Waiting on PLayer");
            STATUS = WAITING_ON_PLAYER;
        }else if (STATUS == WAITING_ON_PLAYER && oppBoardReady && myBoardReady || STATUS == WAITING_ON_SELF && oppBoardReady && myBoardReady ){
            if(isHost){
                statusView.setText("Opp Turn");
                STATUS = OPP_TURN;
                myOppBoardButton.setEnabled(true);

            }else{
                statusView.setText("My Turn");
                STATUS = MY_TURN;
                myOppBoardButton.setEnabled(true);
            }
        }
    }

    private void setOppMessage(String message) {
        if(!message.equals(NO_MESSAGE)) {
            messageView.append(message);
        }
    }

    /**
     *
     */
    private void setupItems(){
        editMessage = (EditText) findViewById(R.id.setMessageView);
        sendTurn = (Button) findViewById(R.id.sendTurnButton);
        messageView = (TextView) findViewById(R.id.message);
        currentMoveTextView = (TextView) findViewById(R.id.currentMoveTextView);
        myGameBoardButton = (Button) findViewById(R.id.myGameBoardButton);
        myOppBoardButton = (Button) findViewById(R.id.oppGameBoardButton);
        statusView = (TextView) findViewById(R.id.statusTextView);


        //Setup Shake Sensor
        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        shakeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, shakeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //--------

        fragManager = getFragmentManager();
        STATUS = SETTING_UP_BOARD;
        statusView.setText("Setting up Board");
        myBoard = new GameBoard();
        gravity = new float[3];



        myGameBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = fragManager.beginTransaction()
                                    .add(R.id.mainFrame, myGameBoardFrag);
                fragmentTransaction.commit();

            }
        });

        myOppBoardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fragmentTransaction = fragManager.beginTransaction()
                        .add(R.id.mainFrame, oppGameBoardFrag);
                fragmentTransaction.commit();
            }
        });




        sendTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameMessage gameMess = setupGameMessage();
                try {
                    bluetoothConnection.write(convertGameMessageToByte(gameMess));
                    Log.e("WarShipGame:OutConnect:", "Message Sent");
                } catch (IOException e) {
                    Log.e("WarShipGame:OutConnect:", e.toString());
                }
                //After send move clear current shot view
                currentMoveTextView.setText("");
                sendTurn.setEnabled(false);
                updateStatus();
                gameStats.addShot();

            }
        });

        if(isHost){
            sendTurn.setEnabled(false);;
        }
        fragmentTransaction = fragManager.beginTransaction()
                .add(R.id.statsFrame, gameStats);
        fragmentTransaction.commit();


    }

    private GameMessage setupGameMessage() {
        GameMessage gameMess;
        gameMess = new GameMessage(GAMEMOVE, myShotArrayString, myShotTextViewId, "");
        String userMessage = editMessage.getText().toString();

        if (userMessage.equals("")) {
            userMessage = NO_MESSAGE;
        }
        gameMess.setMessage(userMessage);
        return gameMess;
    }



    /**
     *
     * @param obj
     * @return
     * @throws IOException
     */
    private static byte[] convertGameMessageToByte(Object obj) throws IOException{
        Log.e("WarShipGame:messToByte:", "converting to byte");

        ByteArrayOutputStream outGoing = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(outGoing);

        outStream.writeObject(obj);

        return outGoing.toByteArray();


    }

    /**
     *
     * @param data
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static Object convertToGameMessage(byte[] data) throws IOException, ClassNotFoundException{
        Log.e("WarShipGame:byteToMess:", "byte to gameMessage");
        ByteArrayInputStream inComing = new ByteArrayInputStream(data);
        ObjectInputStream inStream = new ObjectInputStream(inComing);
       // Log.e("WarShipGame:byteToMess:", "readObject: " + inStream.readObject().toString());
        //messageView.setText(inStream.readObject().toString());
        return inStream.readObject();
    }

    /**
     *
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        bluetoothConnection.cancel();
    }


    public void sendTaunt(View v){
        String tauntMess = editMessage.getText().toString();

        GameMessage taunt = new GameMessage(TAUNT, tauntMess);
        try {
            bluetoothConnection.write(convertGameMessageToByte(taunt));
            Log.e("WarShip: OutConnBoard:", "GameTaunt Sent");
        } catch (IOException e) {
            Log.e("WarShip: OutConnTaunt:", e.toString());
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        float maxAxisAccel = calculateAcceleration(event);
        if(maxAxisAccel > MIN_ACCELERATION){
           //TODO Call to Method
            if(activeStatsFragment == 0) {
                fragmentTransaction = fragManager.beginTransaction()
                        .remove(gameStats);
                fragmentTransaction.commit();
                fragmentTransaction = fragManager.beginTransaction()
                        .add(R.id.statsFrame, playerStats);
                fragmentTransaction.commit();
                activeStatsFragment = 1;
            }else{
                fragmentTransaction = fragManager.beginTransaction()
                        .remove(playerStats);
                fragmentTransaction.commit();
                fragmentTransaction = fragManager.beginTransaction()
                        .add(R.id.statsFrame, gameStats);
                fragmentTransaction.commit();
                activeStatsFragment = 0;
            }
            Log.e("WarShipGame:Sensor: ",  String.valueOf(maxAxisAccel));
        }




    }

    private float calculateAcceleration(SensorEvent event) {

        gravity[0] = calcGravityForce(event.values[0], 0);
        gravity[1] = calcGravityForce(event.values[1], 1);
        gravity[2] = calcGravityForce(event.values[2], 2);


        float xAcceleration = event.values[0] - gravity[0];
        float yAcceleration = event.values[1] - gravity[1];
        float zAcceleration = event.values[2] - gravity[2];

        float maxFirst = Math.max(xAcceleration, yAcceleration);
        return Math.max(maxFirst, zAcceleration);

    }

    float calcGravityForce(float value, int i) {
        return 0.8f * gravity[i] + 0.2f * value;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void sendMyBoardToOpp(GameBoard board){
        myBoard = board;
        myBoardReady = true;
        updateStatus();
        GameMessage gameMess;
        fragmentTransaction = fragManager.beginTransaction().remove(myGameBoardFrag);
        fragmentTransaction.commit();
        gameMess = new GameMessage(GAMEBOARD, myBoard.getGameBoardArrayString(),  "");
        try {
            bluetoothConnection.write(convertGameMessageToByte(gameMess));
            Log.e("WarShip: OutConnBoard:", "GameBoard Sent");
        } catch (IOException e) {
            Log.e("WarShip: OutConnBoard:", e.toString());
        }

    }

    @Override
    public void myShotHitToStats() {
        gameStats.addHit();
    }

    public void setMyShot(String stringId, int textId) {
        myShotArrayString = stringId;
        myShotTextViewId = textId;
        currentMoveTextView.setText(stringId);
        sendTurn.setEnabled(true);
    }
    @Override
    public String getArrayId(int currentShotIntId) {
        String currentShotStringId = getResources().getResourceEntryName(currentShotIntId);
        Log.e("WarShip", "getArrayId: " + currentShotStringId.toString());
        return currentShotStringId;
    }

    public void myBoardOnClick(View v){
        int currentShotIntId = v.getId();
        String currentShotStringId = getResources().getResourceEntryName(currentShotIntId);
        if(isHost){
            if(getCorrespondingLocation(currentShotStringId.charAt(0)) == 0){
                if(STATUS == SETTING_UP_BOARD || STATUS == WAITING_ON_SELF){
                    myGameBoardFrag.setMyShip(currentShotStringId, currentShotIntId);
                }
                Log.e("WarShip", "myBoardOnClick: Host Board A MyBoard");
            }else{
                //this should be Host board B
                Log.e("WarShip", "myBoardOnClick: Host Board B MyBoard");
                setMyShot(currentShotStringId, currentShotIntId);
                oppGameBoardFrag.setMyShot(currentShotStringId, currentShotIntId);
                fragmentTransaction = fragManager.beginTransaction().remove(oppGameBoardFrag);
                fragmentTransaction.commit();
            }
        }else{
            if(getCorrespondingLocation(currentShotStringId.charAt(0)) == 1){
                if(STATUS == SETTING_UP_BOARD || STATUS == WAITING_ON_SELF){
                    myGameBoardFrag.setMyShip(currentShotStringId, currentShotIntId);
                }
                Log.e("WarShip", "myBoardOnClick: Client Board B MyBoard");
            }else{
                Log.e("WarShip", "myBoardOnClick: Client Board A MyBoard");
                setMyShot(currentShotStringId, currentShotIntId);
                oppGameBoardFrag.setMyShot(currentShotStringId, currentShotIntId);
                fragmentTransaction = fragManager.beginTransaction().remove(oppGameBoardFrag);
                fragmentTransaction.commit();
            }
        }
    }
    private int getCorrespondingLocation(char shotChar) {
        switch (shotChar) {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
                return 0;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
                return 1;
            default:
                return -1;
        }
    }



}
