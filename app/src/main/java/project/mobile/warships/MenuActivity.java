package project.mobile.warships;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.*;
import static android.bluetooth.BluetoothDevice.*;

/**
 *
 * @author Josh Coyle
 * @author Robert Slavik
 */
public class MenuActivity extends Activity implements SettingsFragment.settingsListener{

    ActionBar actionBar;
    final int REQUEST_ENABLE_BT = 1;
    final int REQUEST_DISCOVERABLE = 2;
    final UUID uuid = UUID.fromString("37909982-7ad3-11e5-8bcf-feff819cdc9f");
    final String appName = "warShips";


    boolean settingsBoolean = false;
    SettingsFragment settings = new SettingsFragment();


    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_LOOKING = 3; // now looking for a device
    public static final int STATE_CONNECTED = 4;  // now connected to a remote device
    private int mConnectionState;

    //HOST OR JOIN
    boolean mIsHost;

    private SharedPreferences sharedPreferences;
    private TextView userNameTextView;
    private ListView devicesFoundListView;
    private TextView status;

    Button hostButton;
    Button joinButton;
    Button stopButton;

    BroadcastReceiver mReceiver;

    ArrayAdapter<String> deviceAdapterArray;
    ArrayList<BluetoothDevice> deviceArrayList;
    ArrayList<String> stringDeviceArrayList;
    BluetoothAdapter mBluetoothAdapter = getDefaultAdapter();

    AcceptThread server = null;
    ConnectThread joinGame = null;

    private int gameCount;

    private String userName;

    Intent startIntent;

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuactivity);
        actionBar = getActionBar();
        startIntent = new Intent(this, WarShipGame.class);


        server = new AcceptThread();
        setupItems();
        sharedPreferences =  getSharedPreferences("User", 0);
        String userName = sharedPreferences.getString("UserName", null);
        gameCount = sharedPreferences.getInt("Games", 0);



        if(userName != null){
            if(gameCount  < 6){
                userName = "Welcome Ensign: " + userName + "\n Game Count: " + gameCount;
            }else if(gameCount < 11){
                userName = "Welcome Captain: " + userName + "\n Game Count: " + gameCount;
            }else{
                userName = "Welcome Admiral: " + userName + "\n Game Count: " + gameCount;
            }
            userNameTextView.setText(userName);
        }else {
            userNameTextView.setText(R.string.welcomeUser);
        }



    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * Creates the menu bar at the top of the application
     * @param menu- the menu bar
     * @return the inflated menu
     */
    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //Menu

    /**
     * Used for the settings feature of the color blender application
     * @param item- the menu bar item that was selected
     * @return - return true if item exists or false if not available
     */
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.settings:
                callSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }

    }

    /**
     * if the settings is clicked, create fragment and open or close if open
     */
    private void callSettings() {
        if(!settingsBoolean) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.menuActivity, settings);
            fragmentTransaction.commit();
            settingsBoolean = true;
        }else{
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(settings);
            fragmentTransaction.commit();
            settingsBoolean = false;
        }
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     *
     */
    private void setupItems() {
        actionBar = getActionBar();
        //Bluetooth Adaptor
        status = (TextView) findViewById(R.id.Status);
        hostButton = (Button) findViewById(R.id.hostButton);
        joinButton = (Button) findViewById(R.id.joinButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        userNameTextView = (TextView) findViewById(R.id.UserName);

        devicesFoundListView = (ListView) findViewById(R.id.availableBluetooth);

        devicesFoundListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                joinGame = new ConnectThread(deviceArrayList.get(position));
                Log.e("WarShip: joinGame", "onItemClick: ");
                joinGame.run();

            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeConnections();
                status.setText("");
                devicesFoundListView.setVisibility(View.INVISIBLE);
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



    }

    private void closeConnections() {
        if (joinGame != null) {
            joinGame.cancel();
        }
        if (server != null) {
            server.cancel();
        }
        joinButton.setVisibility(View.VISIBLE);
        hostButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);

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
                    Log.e("WarShip: joinGame", "onReceive ACTION_FOUND");
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
        hostButton.setVisibility(View.INVISIBLE);
        joinButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        mIsHost = true;
        Log.e("WarShip: HostGame", "beforeEnable");
        enableDiscovery();
        Log.e("WarShip: HostGame", "afterEnable");
        status.setText("Waiting On Player.....");


    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //Button Join Game Click Call
    /**
     *
     */
    public void joinGame(View view){
        setupReceiver();
        devicesFoundListView.setVisibility(View.VISIBLE);
        hostButton.setVisibility(View.INVISIBLE);
        status.setText("Searching For Player.....");
        joinButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        mIsHost = false;
        Log.e("WarShip: joinGame", "Trying to Find Bluetooth Devices L:202");
        mConnectionState = STATE_LOOKING;
        devicesFoundListView.setVisibility(View.VISIBLE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FOUND);
        filter.addAction(ACTION_SCAN_MODE_CHANGED);
        Log.e("WarShip: joinGame", "Trying to Find Bluetooth Devices ");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }

        mBluetoothAdapter.startDiscovery();
        registerReceiver(mReceiver, filter);
        Log.e("WarShip: joinGame", "Trying to Find Bluetooth Devices ");
     }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * Allows this device to be found by other BT devices
     * Needed if device is hosting a game
     */
    private void enableDiscovery(){
        mConnectionState = STATE_LISTEN;
        Log.e("WarShip: HostGame", "afterStartActivity");
        server.start();
        Log.e("WarShip: HostGame", "afterServer.run()");
        Intent discoverableIntent = new Intent(ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(EXTRA_DISCOVERABLE_DURATION, 200);
        startActivity(discoverableIntent);

    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();


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
        try {
            if (mReceiver != null) {
                this.unregisterReceiver(mReceiver);
            }
        }catch (IllegalArgumentException e){

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        if (server != null) {
            server.cancel();
        }
        if (joinGame != null) {
            joinGame.cancel();
        }
        try {
            if (mReceiver != null) {
                this.unregisterReceiver(mReceiver);
            }
        }catch (IllegalArgumentException e){

        }
    }


    @Override
    public void updateInterface(boolean attackHit, boolean attackMiss, boolean opAttackMiss, boolean ship, int Color) {
        if(attackHit){
            startIntent.putExtra("attackHit", Color);
        } else if (attackMiss){
            startIntent.putExtra("attackMiss", Color);
        } else if(opAttackMiss){
            startIntent.putExtra("opAttackMiss", Color);
        } else if(ship) {
            startIntent.putExtra("ship", Color);
        }
        callSettings();

    }


//************************************************************************************************//
    //Server Host Thread
    /**
     *
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        BluetoothSocket socket;
        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(appName, uuid);
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
                    Log.e("WarShip: HostGame acce",e.toString());
                    break;
                } catch(NullPointerException n){
                    Log.e("WarShip: HostGame nulP",n.toString());
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
//                    mBluetoothAdapter.cancelDiscovery();
                    mConnectionState = STATE_CONNECTED;
                    try {
                        mmServerSocket.close();
                        //Try to close BluetoothServerSocket no need to listen any more
                    }catch (IOException e){
                        Log.e("Warship: Host .close:", e.toString());
                    }
                    manageConnectedSocket(socket);

                    break;
                }else{
                    Log.e("WarShip: hostGame", "socket == null?? ");
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("WarShip:ServerClose", e.toString());
            }
        }
    }
    /**
     *
     * @param socket
     */
    private void manageConnectedSocket(BluetoothSocket socket) {
        try {
            if (mReceiver != null) {// Unregister broadcast listeners
                this.unregisterReceiver(mReceiver);
            }
        }catch (IllegalArgumentException e){
            Log.e("WarShip: ManageERR", e.toString());
        }
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }

        GameSocket.setGameSocket(socket);


        Log.e("WarShip: joiningGames", "manageConnectionSocket: Devices Socket ");
        gameCount++;
        startIntent.putExtra("isHost", mIsHost);
        startIntent.putExtra("Games", gameCount);

        SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("Games", gameCount);
            editor.apply();


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
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
               Log.e("ConnectThread", e.toString());
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
            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("WarShip:ConnectCLOSE", closeException.toString());
                }
                return;
            }
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
}
