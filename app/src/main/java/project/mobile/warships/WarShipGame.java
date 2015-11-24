package project.mobile.warships;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
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
import java.util.Random;

import GameBoardFragments.GameBoardFragment;
import GameBoardFragments.OpponentGameBoardFragment;
import GameBoardObjects.GameBoard;

public class WarShipGame extends Activity  implements SensorEventListener{

    protected GameBoard myBoard;
    protected GameBoard oppBoard;

    protected ConnectedThread bluetoothConnection;
    public TextView messageView;
    private EditText editMessage;


    final String GAMEBOARD = "gameBoard";
    final String GAMEMOVE = "gameMove";
    final String TAUNT = "messOnly";
    final String NO_MESSAGE = "No Message";

    private boolean isHost;

    private FragmentManager fragManager;
    private FragmentTransaction fragmentTransaction;

    private static final int SETTING_UP_BOARD = -1;
    private static final int WAITING_ON_SELF = -2;
    private static final int WAITING_ON_PLAYER = 0;
    private static final int MY_TURN = 1;
    private static final int OPP_TURN = 2;
    private int STATUS;

    private Button myGameBoardButton;
    private Button myOppBoardButton;

    protected ActionBar actionBar;

    private GameBoardFragment myGameBoardFrag;
    private GameBoardFragment oppGameBoardFrag;

    private SensorManager sensorManager;
    private Sensor shakeSensor;

    private TextView currentMoveTextView;

    private Random random = new Random();

    float gravity[];
    final private float MIN_ACCELERATION = 1.5f;

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
        setupItems(); 

    }

    private void setupFragments() {
        if(isHost) {
            Bundle newFragBundle = new Bundle();
            newFragBundle.putInt("board", 0);
            myGameBoardFrag = new GameBoardFragment();
            myGameBoardFrag.setArguments(newFragBundle);

            Bundle newFragBundle2 = new Bundle();
            newFragBundle2.putInt("board", 1);
            oppGameBoardFrag = new GameBoardFragment();
            oppGameBoardFrag.setArguments(newFragBundle2);

        }else{
            Bundle newFragBundle = new Bundle();
            newFragBundle.putInt("board", 1);
            myGameBoardFrag = new GameBoardFragment();
            myGameBoardFrag.setArguments(newFragBundle);

            Bundle newFragBundle2 = new Bundle();
            newFragBundle2.putInt("board", 0);
            oppGameBoardFrag = new GameBoardFragment();
            oppGameBoardFrag.setArguments(newFragBundle2);
        }
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
                    GameMessage incomingMessage = convertToGameMessage(incoming);
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


        switch(newMessage.getMessage()){
            case GAMEBOARD:
                setupOppGameBoard(newMessage.getGameBoard());
                setOppMessage(newMessage.getMessage());
                updateStatus();
                break;
            case GAMEMOVE:
                break;
            case TAUNT:



        }
    }

    private void updateStatus() {
        if(STATUS == SETTING_UP_BOARD){
           STATUS = WAITING_ON_SELF;
        }
        if(STATUS == WAITING_ON_PLAYER){
            if(isHost){
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

    private void setupOppGameBoard(GameBoard gameBoard) {

        oppBoard = gameBoard;
    }

    /**
     *
     */
    private void setupItems(){
        editMessage = (EditText) findViewById(R.id.setMessageView);
        final Button sendTurn = (Button) findViewById(R.id.sendTurnButton);
        messageView = (TextView) findViewById(R.id.message);

        currentMoveTextView = (TextView) findViewById(R.id.currentMoveTextView);

        STATUS = SETTING_UP_BOARD;

        //Setup Shake Sensor
        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        shakeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, shakeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //--------

        fragManager = getFragmentManager();



        myBoard = new GameBoard();


        gravity = new float[3];

        myGameBoardButton = (Button) findViewById(R.id.myGameBoardButton);
        myOppBoardButton = (Button) findViewById(R.id.oppGameBoardButton);

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


            }
        });

        if(isHost){
            sendTurn.setVisibility(View.INVISIBLE);
        }


    }

    private GameMessage setupGameMessage() {



        GameMessage gameMess = null;

        String userMessage = editMessage.getText().toString();


        if (userMessage.equals("")) {
            userMessage = NO_MESSAGE;
        }
        gameMess.setMessage(userMessage);
        return gameMess;
    }



    /**
     *
     * @param gameMess
     * @return
     * @throws IOException
     */
    private static byte[] convertGameMessageToByte(GameMessage gameMess) throws IOException{
        ByteArrayOutputStream outGoing = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(outGoing);
        outStream.writeObject(gameMess);
        return outGoing.toByteArray();


    }

    /**
     *
     * @param data
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static GameMessage convertToGameMessage(byte[] data) throws IOException, ClassNotFoundException{
        ByteArrayInputStream inComing = new ByteArrayInputStream(data);
        ObjectInputStream inStream = new ObjectInputStream(inComing);
        return (GameMessage) inStream.readObject();
    }

    /**
     *
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        bluetoothConnection.cancel();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        float maxAxisAccel = calculateAcceleration(event);
        if(maxAxisAccel > MIN_ACCELERATION){
           //Call to Method
            Log.e("WarShipGame:Sensor:",  String.valueOf(maxAxisAccel));
        }




    }

    private float calculateAcceleration(SensorEvent event) {

        gravity[0] = calcGravityForce(event.values[0], 0);
        gravity[1] = calcGravityForce(event.values[1], 1);
        gravity[2] = calcGravityForce(event.values[2], 2);


        float xAcceleration = event.values[0] - gravity[0];
        float yAcceleration = event.values[1] - gravity[1];
        float zAcceleration = event.values[2] - gravity[2];

        float maxFirst = Math.max(xAcceleration,yAcceleration);
        return Math.max(maxFirst, zAcceleration);

    }

    float calcGravityForce(float value, int i) {
        return 0.8f * gravity[i] + 0.2f * value;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
