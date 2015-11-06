package project.mobile.warships;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class WarShipGame extends Activity {

    private BluetoothSocket socket;
    protected GameBoard board;
    protected ConnectedThread bluetoothConnection;
    private static Handler connectionHandler;
    public TextView messageView;
    private EditText setMessage;
    private Button sendTurn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_war_ship_game);
        setupConnectionItems();
        setupItems();
    }

    private void setupConnectionItems() {
        GameSocket gameSocket = GameSocket.getInstance();
        socket = gameSocket.getGameSocket();
        connectionHandler = new Handler(){
            @Override
            public void handleMessage(Message inputMessage){
                byte [] incoming = (byte[]) inputMessage.obj;
                try {
                    Log.e("WarGame:InGameInMes:", "handler Incoming message");
                    GameMessage incomingMessage = convertToGameMessage(incoming);
                    Log.e("WarGame:InGameInMes:", "Actual String: " + incomingMessage.getMessage());
                    messageView.setText(incomingMessage.getMessage());
                }catch(ClassNotFoundException e){
                    Log.e("WarGame:InGameInMesERR:", e.toString());
                }catch (IOException i){
                    Log.e("WarGame:InGameInMesERR:", i.toString());
                }


            }


        };
        bluetoothConnection  = new ConnectedThread(socket, connectionHandler);
        bluetoothConnection.run();
    }


    private void setupItems(){
        setMessage = (EditText) findViewById(R.id.setMessageView);
        sendTurn = (Button) findViewById(R.id.sendTurnButton);
        board = new GameBoard();
        messageView = (TextView) findViewById(R.id.message);

        sendTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameMessage gameMess = new GameMessage(0,0);
                gameMess.setMessage("Testing");
                try{
                    bluetoothConnection.write(convertGameMessageToByte(gameMess));
                    Log.e("WarShipGame:OutConnect:", "Message Sent");
                }catch(IOException e){
                    Log.e("WarShipGame:OutConnect:", e.toString());
                }



            }
        });

    }

    private static byte[] convertGameMessageToByte(GameMessage gameMess) throws IOException{
        ByteArrayOutputStream outGoing = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(outGoing);
        outStream.writeObject(gameMess);
        return outGoing.toByteArray();


    }

    private static GameMessage convertToGameMessage(byte[] data) throws IOException, ClassNotFoundException{
        ByteArrayInputStream inComing = new ByteArrayInputStream(data);
        ObjectInputStream inStream = new ObjectInputStream(inComing);
        return (GameMessage) inStream.readObject();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        bluetoothConnection.cancel();
    }



}
