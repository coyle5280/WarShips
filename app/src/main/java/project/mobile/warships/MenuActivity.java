package project.mobile.warships;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by coyle on 10/24/2015.
 */
public class MenuActivity extends Activity {

    ActionBar actionBar;
    BluetoothAdapter mBlueToothAdapter;
    int REQUEST_ENABLE_BT = 1;


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

        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                }
            }
        };
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                //TODO
            }else{
                CharSequence mess = ">>>>>>ERROR ENABLING BLUETOOTH<<<<<<<";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getApplicationContext(),mess, duration);
                toast.show();
            }
        }


    }






}//End MenuActivity Class
