package project.mobile.warships;

import android.bluetooth.BluetoothSocket;

/**
 * Created by coyle on 10/26/2015.
 */
public class GameSocket {
    private static GameSocket ourInstance = new GameSocket();
    private static BluetoothSocket socket = null;
    public static GameSocket getInstance() {
        return ourInstance;
    }

    private GameSocket() {
    }

    public void setGameSocket(BluetoothSocket newSocket){
        socket = newSocket;
    }


}
