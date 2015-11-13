package project.mobile.warships;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothSocket;
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

import GameBoardFragments.MyGameBoard;
import GameBoardFragments.OpponentGameBoard;
import GameBoardObjects.GameBoard;

public class WarShipGame extends Activity  {

    protected GameBoard myBoard;
    protected GameBoard oppBoard;

    protected ConnectedThread bluetoothConnection;
    public TextView messageView;
    private EditText editMessage;


    final String GAMEBOARD = "gameBoard";
    final String GAMEMOVE = "gameMove";
    final String TAUNT = "messOnly";

    private boolean isHost;

    private FragmentManager fragManager;
    private FragmentTransaction fragmentTransaction;

    private static final int SETTING_UP_BOARD = -1;
    private static final int WAITING_ON_PLAYER = 1;
    private static final int MY_TURN = 0;
    private int STATUS;

    private Button myGameBoardButton;
    private Button myOppBoardButton;

    protected ActionBar actionBar;

    private MyGameBoard myGameBoardFrag;
    private OpponentGameBoard oppGameBoardFrag;

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
        setupItems(); 
        setupFragments();
    }

    private void setupFragments() {

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
                    messageView.append(incomingMessage.getMessage() + "X: " + incomingMessage.getxAxisMove());
                } catch (ClassNotFoundException | IOException e) {
                    Log.e("WarGame:InGameInMesERR:", e.toString());
                }


            }


        };
        bluetoothConnection  = new ConnectedThread(socket, connectionHandler);
        bluetoothConnection.start();
    }

    /**
     *
     */
    private void setupItems(){
        editMessage = (EditText) findViewById(R.id.setMessageView);
        final Button sendTurn = (Button) findViewById(R.id.sendTurnButton);
        messageView = (TextView) findViewById(R.id.message);
        STATUS = SETTING_UP_BOARD;

        fragManager = getFragmentManager();


        oppBoard = new GameBoard();
        myBoard = new GameBoard();

        myGameBoardFrag = new MyGameBoard();
        oppGameBoardFrag = new OpponentGameBoard();


        myGameBoardButton = (Button) findViewById(R.id.myGameBoardButton);
        myOppBoardButton = (Button) findViewById(R.id.oppGameBoardButton);

        myGameBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = fragManager.beginTransaction();
                fragmentTransaction.add(R.id.mainFrame, myGameBoardFrag);
                fragmentTransaction.commit();
            }
        });

        myOppBoardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fragmentTransaction = fragManager.beginTransaction();
                fragmentTransaction.add(R.id.mainFrame, oppGameBoardFrag);
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
            userMessage = "NO MESSAGE";
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




}
