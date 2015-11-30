package project.mobile.warships;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;



/**
 * The settings menu fragment used by the WarShips application,
 * has options to change settings in the WarShips
 * @author Josh Coyle
 * @author Robert Slavik
 */
public class SettingsFragment extends Fragment {
    //variables
    CheckBox attackHit;
    CheckBox attackMiss;
    CheckBox opAttackMiss;
    CheckBox shipLocation;
    TextView fragColorText;
    Button fragBackGroundButton;
    Button saveButton;
    settingsListener  callBack;
    //set text color to white
    int colorText = -1;

    /**
     * when the fragment view is created
     * @param inflater- to inflate the view
     * @param container- where to place the view
     * @param savedInstanceState- saved state
     * @return - the view
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_frag, container, false);
        return view;

    }

    /**
     * after the fragment view is created
     * @param view- the view
     * @param savedInstanceState - the saved state
     */
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        attackHit = (CheckBox) view.findViewById(R.id.setAttackHitColor);
        attackMiss = (CheckBox) view.findViewById(R.id.attackMissColor);
        opAttackMiss = (CheckBox) view.findViewById(R.id.opAttackMissColor);
        shipLocation = (CheckBox) view.findViewById(R.id.shipLocation);
        fragColorText = (TextView) view.findViewById(R.id.fragColorText);
        fragBackGroundButton = (Button) view.findViewById(R.id.fragBackGroundButton);
        saveButton = (Button) view.findViewById(R.id.saveButton);

        //called to setup Buttons used in app
        setupButtons();


    }

    /**
     * setup the buttons in the settings fragment
     */
    private void setupButtons() {
        fragBackGroundButton.setOnClickListener(new View.OnClickListener() {
                                                    /**
                                                     * opens the color finder to get a text color
                                                     * @param v the view
                                                     */
                                                    public void onClick(View v) {
                                                        int Fragmetcall = 1;
                                                        Activity activity = getActivity();
                                                        PackageManager pm = activity.getPackageManager();
                                                        Intent intent = pm.getLaunchIntentForPackage("Color.Finder.Assignment");
                                                        intent.setFlags(0);
                                                        intent.putExtra("calledByProgram", true);
                                                        startActivityForResult(intent, Fragmetcall);
                                                    }
                                                }
        );


        saveButton.setOnClickListener(new View.OnClickListener(){
            /**
             * return the values when settings is saved
             * @param v the view
             */
            public void onClick(View v) {
               //update
                callBack.updateInterface(attackHit.isChecked(), attackMiss.isChecked(), opAttackMiss.isChecked(), shipLocation.isChecked(), colorText);
                //reset the text color for next use
                colorText = -1;
            }
        });

    }

    /**
     * The result of the color finder activity (Getting Text Color)
     * @param request_Code- code requested
     * @param result_Code- the result of the activity
     * @param colorsData- the color choosen
     */
    public void onActivityResult(int request_Code, int result_Code, Intent colorsData) {

        if (request_Code == 1) {
            if (result_Code == getActivity().RESULT_OK) {
                colorText = colorsData.getIntExtra("color", 0);
                fragColorText.setBackgroundColor(colorsData.getIntExtra("color", 0));
            }
        }
    }

    /**
     * an Interface to update settings in the color blender main activity
     */
    public interface settingsListener{
        /**
         * change colors used in game
         * @param attackHit
         * @param attackMiss
         * @param opAttackMiss
         * @param ship
         * @param Color
         */
        void updateInterface(boolean attackHit, boolean attackMiss, boolean opAttackMiss, boolean ship, int Color);
    }

    /**
     * attach to the main activity
     * @param activity- the activity
     */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            callBack = (settingsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

}

