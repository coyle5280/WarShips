package project.mobile.warships;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by coyle on 10/28/2015.
 */
public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler mHandler;
    private int outCount = 0;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mHandler = handler;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            Log.e("WarShipGame:ConnThread:", "Trying to open inputOutput");
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("WarShipGame:ConnThread:", e.toString());
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        int count = 0;


        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                count++;
                Log.e("WarGame:ConnThread.run:", "Listening: # " + count);
                bytes = mmInStream.read(buffer);
                Log.e("WarGame:ConnThread.run:", "afterReadBUffer");
                // Send the obtained bytes to the UI activity
                mHandler.obtainMessage(0, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e("WarGame:ConnThdERR:", e.toString());
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            outCount++;
            Log.e("WarGame:ConnThread.wri", "writeCalled");
            Log.e("WarGame:ConnThread:", "bytes: " + bytes + "count Out: " + outCount);
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e("WarGame:ConnThread:WriE", e.toString());
        }
    }


    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("WarGame:ConnThread:Can", e.toString());
        }
    }
}
