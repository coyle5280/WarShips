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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.*;
import static android.bluetooth.BluetoothDevice.*;

/**
 *
 */
public class MenuActivity extends Activity {

    ActionBar actionBar;
    final int REQUEST_ENABLE_BT = 1;
    final UUID uuid = UUID.fromString("37909982-7ad3-11e5-8bcf-feff819cdc9f");
    final String appName = "warShips";

    //Send to next Activity: WHO plays first
    private boolean hostStart = true;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_LOOKING = 3; // now looking for a device
    public static final int STATE_CONNECTED = 4;  // now connected to a remote device
    private int mConnectionState;

    //HOST OR JOIN
    boolean mIsHost;

    private ListView devicesFoundListView;

    Button hostButton;
    Button joinButton;

    BroadcastReceiver mReceiver;

    ArrayAdapter<String> deviceAdapterArray;
    ArrayList<BluetoothDevice> deviceArrayList;
    ArrayList<String> stringDeviceArrayList;
    BluetoothAdapter mBluetoothAdapter = getDefaultAdapter();

    final AcceptThread server = new AcceptThread();
    ConnectThread joinGame;


    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuactivity);
        setupItems();

    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     *
     */
    private void setupItems() {
        actionBar = getActionBar();
        //Bluetooth Adaptor

        hostButton = (Button) findViewById(R.id.hostButton);
        joinButton = (Button) findViewById(R.id.joinButton);

        devicesFoundListView = (ListView) findViewById(R.id.availableBluetooth);

        devicesFoundListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                joinGame = new ConnectThread(deviceArrayList.get(position));
                Log.e("WarShip: joinGame", "onItemClick: Devices L:98");
                joinGame.run();

            }
        });

        stringDeviceArrayList = new ArrayList<>();
        deviceAdapterArray = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, stringDeviceArrayList);
        devicesFoundListView.setAdapter(deviceAdapterArray);

        deviceArrayList = new ArrayList<>();



        //Message if No BlueTooth is Installed
        if(mBluetoothAdapter == null){
            CharSequence mess = ">>>>>>YOU DO NOT HAVE BLUETOOTH<<<<<<<";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(),mess, duration);
            toast.show();
        }

        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBlueTooth = new Intent(ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlueTooth, REQUEST_ENABLE_BT);
        }

        mConnectionState = STATE_NONE;

        setupReceiver();

    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     *
     */
    private void setupReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(ACTION_FOUND.equals(action)){
                    Log.e("WarShip: joinGame", "onReceive ACTION_FOUND: Devices L:143");
                    BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
                    deviceArrayList.add(device);
                    stringDeviceArrayList.add(device.getName() + " " + device.getAddress());
                    deviceAdapterArray.notifyDataSetChanged();

                }

                if(ACTION_SCAN_MODE_CHANGED.equals(action)){
//                    if(intent.getParcelableExtra(BluetoothAdapter.S))
                }

            }

        };
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //Button Host Game Click Call
    /**
     *
     */
    public void hostGame(View view){
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.waiting);
        mIsHost = true;
        enableDiscovery();
        server.run();



    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //Button Join Game Click Call
    /**
     *
     */
    public void joinGame(View view){
    //TODO Write the Join game bluetooth methods...

        mIsHost = false;
        Log.e("WarShip: joinGame", "Trying to Find Bluetooth Devices L:202");
//        setProgressBarIndeterminateVisibility(true);
//        setTitle(R.string.looking);
        mConnectionState = STATE_LOOKING;
        devicesFoundListView.setVisibility(View.VISIBLE);
        // Build Intents and Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FOUND);
        filter.addAction(ACTION_SCAN_MODE_CHANGED);
        Log.e("WarShip: joinGame", "Trying to Find Bluetooth Devices ");
        mBluetoothAdapter.startDiscovery();
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        Log.e("WarShip: joinGame", "Trying to Find Bluetooth Devices ");
     }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * Allows this device to be found by other BT devices
     * Needed if device is hosting a game
     */
    private void enableDiscovery(){
        mConnectionState = STATE_LISTEN;
        Intent discoverableIntent = new Intent(ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(EXTRA_DISCOVERABLE_DURATION, 200);
        startActivity(discoverableIntent);

    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        if(server != null){
            server.cancel();
        }
        if(joinGame != null) {
            joinGame.cancel();
        }
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        if(server != null){
            server.cancel();
        }
        if(joinGame != null) {
            joinGame.cancel();
        }
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }



//************************************************************************************************//
    //Server Host Thread
    /**
     *
     */
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
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(appName, uuid);
            } catch (IOException e) {
                //TODO Error Message to user
            }
            mmServerSocket = tmp;
        }
        /**
         *
         */
        public void run() {
            // Keep listening until exception occurs or a socket is returned
            while (true) {//Could change this while loop to test (mConnectionState == 1) Allow  cancel of loop from outside maybe settings
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    setProgressBarIndeterminateVisibility(false);
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    mBluetoothAdapter.cancelDiscovery();
                    mConnectionState = STATE_CONNECTED;
                    try {
                        mmServerSocket.close(); //Try to close BluetoothServerSocket no need to listen any more
                    }catch (IOException e){
                        Log.e("Server Host Error:", e.toString());
                        setProgressBarIndeterminateVisibility(false);
                    }
                    manageConnectedSocket(socket);
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
    /**
     *
     * @param socket
     */
    private void manageConnectedSocket(BluetoothSocket socket) {
        //I'm thinking that we call new activity here and pass socket to continue the "talk"
        //or setup methods that allow communication
        setProgressBarIndeterminateVisibility(false);
        GameSocket theSocket = GameSocket.getInstance();
        theSocket.setGameSocket(socket);
//        theSocket.socket;

        Log.e("WarShip: joinGame", "manageConnectionSocket: Devices Socket ");
        Intent startIntent = new Intent(this, WarShipGame.class);
        startActivity(startIntent);

    }
    /**
     *
     * @return
     */
    public synchronized int getState() {
        return mConnectionState;
    }

//************************************************************************************************//
    /**
     *
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        /**
         *
         * @param device
         */
        public ConnectThread(BluetoothDevice device) {
            mConnectionState = STATE_CONNECTING;
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // uuid is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
               Log.e("ConnectThread", e.toString());
//                  setProgressBarIndeterminateVisibility(false);
                CharSequence mess = ">>>>>>ERROR CONNECTING TO HOST<<<<<<<";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getApplicationContext(),mess, duration);
                toast.show();
            }
            mmSocket = tmp;
        }

        /**
         *
         */
        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
//                    setProgressBarIndeterminateVisibility(false);
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("WarShip:ConnectCLOSE", closeException.toString());
                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("WarShip:ConnectCLOSE", e.toString());
             }
        }
    }




}//End MenuActivity Class
