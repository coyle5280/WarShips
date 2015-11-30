package GameStatsFragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import project.mobile.warships.R;

/**
 * @author Robert Slavik
 * @author Josh Coyle
 */
public class GameStats extends Fragment {

    int numberOfShots = 0;
    int numberOfHits = 0;

    TextView shotsTextView;
    TextView hitsTextView;



    SharedPreferences sharedPreferences;

    public GameStats() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shotsTextView = (TextView) view.findViewById(R.id.shotsTextView);
        hitsTextView = (TextView) view.findViewById(R.id.hitTextView);

        shotsTextView.setText(String.valueOf(numberOfShots));
        hitsTextView.setText(String.valueOf(numberOfHits));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_stats_fragment, container, false);
    }

    public void addHit(){
        numberOfHits++;
        hitsTextView.setText(String.valueOf(numberOfHits));
    }

    public void addShot(){
        numberOfShots++;
        shotsTextView.setText(String.valueOf(numberOfShots));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
   }
}
