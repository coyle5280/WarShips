package project.mobile.warships;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import GameBoardObjects.GameBoard;

public class WarShipGame extends Activity  {

    protected GameBoard board;
    protected ConnectedThread bluetoothConnection;
    public TextView messageView;
    private EditText editMessage;

    private boolean isHost;

    protected ActionBar actionBar;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActionBar();
        setContentView(R.layout.activity_war_ship_game);
        setHost();
        setupConnectionItems();
        setupItems();
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
                    messageView.setText(incomingMessage.getMessage());
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
        final Spinner yAxisAttack = (Spinner) findViewById(R.id.yAxisSpinner);
        final Spinner xAxisAttack = (Spinner) findViewById(R.id.xAxisSpinner);

        setupSpinners(xAxisAttack, yAxisAttack);



        board = new GameBoard();




        sendTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameMessage gameMess = new GameMessage(Integer.parseInt(xAxisAttack.getSelectedItem().toString()),
                        Integer.parseInt(yAxisAttack.getSelectedItem().toString()));

                String userMessage = editMessage.getText().toString();


                if (userMessage.equals("")) {
                    userMessage = "NO MESSAGE";
                }
                gameMess.setMessage(userMessage);

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

    private void setupSpinners(Spinner x, Spinner y) {
        ArrayAdapter<CharSequence> xAdapterDistance = ArrayAdapter.createFromResource(this,
                R.array.xAxis, android.R.layout.simple_spinner_item);

        xAdapterDistance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        x.setAdapter(xAdapterDistance);

        ArrayAdapter<CharSequence> yAdapterDistance = ArrayAdapter.createFromResource(this,
                R.array.yAxis, android.R.layout.simple_spinner_item);

        yAdapterDistance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        y.setAdapter(yAdapterDistance);
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
