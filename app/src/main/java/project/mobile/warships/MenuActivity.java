package project.mobile.warships;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by coyle on 10/24/2015.
 */
public class MenuActivity extends Activity {

    ActionBar actionBar;
    BluetoothAdapter mBlueToothAdapter;
    final int REQUEST_ENABLE_BT = 1;
    final UUID uuid = UUID.fromString("37909982-7ad3-11e5-8bcf-feff819cdc9f");
    final String appName = "warShips";

    final AcceptThread server = new AcceptThread();

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_LOOKING = 3; // now looking for a device
    public static final int STATE_CONNECTED = 4;  // now connected to a remote device
    private int mConnectionState;

    BroadcastReceiver mReceiver;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuactivity);
        setupItems();

    }

    private void setupItems() {
        actionBar = getActionBar();
        //Bluetooth Adaptor
        mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Message if No BlueTooth is Installed
        if(mBlueToothAdapter == null){
            CharSequence mess = ">>>>>>YOU DO NOT HAVE BLUETOOTH<<<<<<<";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(),mess, duration);
            toast.show();
        }

        if(!mBlueToothAdapter.isEnabled()){
            Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlueTooth, REQUEST_ENABLE_BT);
        }

        mConnectionState = STATE_NONE;

        setupReceiver();

    }

    private void setupReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                }

                if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)){
//                    if(intent.getParcelableExtra(BluetoothAdapter.S))
                }

            }

        };
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                //TODO Maybe? Maybe not? Everything was OK so....
            }else{
                CharSequence mess = ">>>>>>ERROR ENABLING BLUETOOTH<<<<<<<";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getApplicationContext(),mess, duration);
                toast.show();
            }
        }


    }
    //Button Host Game Click Call
    private void hostGame(){
        enableDiscovery();
        server.run();


    }

    private void joinGame(){
    //TODO Write the Join game bluetooth methods...
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.looking);
        mConnectionState = STATE_LOOKING;

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }


    /**
     * Allows this device to be found by other BT devices
     * Needed if device is hosting a game
     */
    private void enableDiscovery(){
        mConnectionState = STATE_LISTEN;
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);
        startActivity(discoverableIntent);

    }


    //Server Host Thread
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        BluetoothSocket socket = null;

        /*
        AcceptThread Constructor
         */
        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBlueToothAdapter.listenUsingRfcommWithServiceRecord(appName, uuid);
            } catch (IOException e) {
                //TODO Error Message to user
            }
            mmServerSocket = tmp;
        }

        public void run() {
            // Keep listening until exception occurs or a socket is returned
            while (true) {//Could change this while loop to test (mConnectionState == 1) Allow  cancel of loop from outside maybe settings
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
                    mBlueToothAdapter.cancelDiscovery();
                    mConnectionState = STATE_CONNECTED;
                    try {
                        mmServerSocket.close(); //Try to close BluetoothServerSocket no need to listen any more
                    }catch (IOException e){
                        Log.e("Server Host Error:", e.toString());
                    }
                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                //TODO ADD ERROR MESSAGE
            }
        }
    }
    //Called when blueTooth Socket accepted by server passed socket
    private void manageConnectedSocket(BluetoothSocket socket) {
        //I'm thinking that we call new activity here and pass socket to continue the "talk"
        //or setup methods that allow communication

    }

    public synchronized int getState() {
        return mConnectionState;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBlueToothAdapter != null) {
            mBlueToothAdapter.cancelDiscovery();
        }
        server.cancel();
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

}//End MenuActivity Class
