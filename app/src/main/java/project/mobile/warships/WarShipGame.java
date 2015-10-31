package project.mobile.warships;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class WarShipGame extends Activity {

    private BluetoothSocket socket;
    protected GameBoard board;
    protected ConnectedThread bluetoothConnection;
    private static Handler connectionHandler;
    public TextView messageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_war_ship_game);
        setupItems();
    }


    private void setupItems(){
        GameSocket gameSocket = GameSocket.getInstance();
        socket = gameSocket.getGameSocket();
        connectionHandler = new Handler(){
            @Override
            public void handleMessage(Message inputMessage){

                GameMessage newMessage = (GameMessage) inputMessage.obj;

                messageView.setText(newMessage.getMessage());
            }


        };
        board = new GameBoard();
        bluetoothConnection  = new ConnectedThread(socket, connectionHandler);

        messageView = (TextView) findViewById(R.id.message);
    }
}
