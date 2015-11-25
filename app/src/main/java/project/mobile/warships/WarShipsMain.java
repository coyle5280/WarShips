package project.mobile.warships;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by coyle on 10/23/2015.
 */
public class WarShipsMain extends Activity {


    protected Button startMenuActivity;
    private SharedPreferences sharedPreferences;
    protected EditText userNameInput;

    /**
     * Part of Application Lifecycle
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sharedPreferences =  getSharedPreferences("User", 0);
        String userName = sharedPreferences.getString("UserName", null);
        if(userName != null){
            startTheMenuActivityShort();
        }else {
            setupItems();
        }

    }

    private void setupItems() {
        userNameInput = (EditText) findViewById(R.id.UserName);
        startMenuActivity = (Button) findViewById(R.id.startMenuActivity);

        startMenuActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTheMenuActivity();
            }
        });

        //sharedPreferences = getSharedPreferences("User", 0);



    }

    public void startTheMenuActivityShort(){
        Intent startIntent = new Intent(this, MenuActivity.class);
        startActivity(startIntent);
    }



    public void startTheMenuActivity(){
        Intent startIntent = new Intent(this, MenuActivity.class);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserName", userNameInput.getText().toString());
        editor.commit();
        startActivity(startIntent);
    }
}//End Activity
