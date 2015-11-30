package project.mobile.warships;

import android.bluetooth.BluetoothSocket;


public class GameSocket {
    private static GameSocket ourInstance = null;
    private static BluetoothSocket socket = null;

    private GameSocket() {
    }


    public static GameSocket getInstance() {
        if(ourInstance == null){
            ourInstance = new GameSocket();
        }
        return ourInstance;
    }



    protected static void setGameSocket(BluetoothSocket newSocket){
        socket = newSocket;
    }


    protected BluetoothSocket getGameSocket(){
        return socket;
    }


}
