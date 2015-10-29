package project.mobile.warships;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.app.Activity;

public class WarShipGame extends Activity {

    BluetoothSocket socket;
    GameBoard board;
    ConnectedThread bluetoothConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_war_ship_game);


        setupItems();
    }


    private void setupItems(){
        GameSocket gameSocket = GameSocket.getInstance();
        socket = gameSocket.getGameSocket();
        board = new GameBoard();
        bluetoothConnection  = new ConnectedThread(socket);
    }





}
