package project.mobile.warships;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by coyle on 10/23/2015.
 */
public class WarShipsMain extends Activity {


    Button startMenuActivity;


    /**
     * Part of Application Lifecycle
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupItems();
//        setupLocation();
    }

    private void setupItems() {
        startMenuActivity = (Button) findViewById(R.id.startMenuActivity);
    }

    public void startTheMenuActivity(View view){
        Intent startIntent = new Intent(this, WarShipsMain.class);
        startActivity(startIntent);
    }
}//End Activity
