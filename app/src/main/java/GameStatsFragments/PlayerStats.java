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

import project.mobile.warships.R;


public class PlayerStats extends Fragment {

    SharedPreferences sharedPreferences;

    TextView numberWins;
    TextView numberGames;

    public PlayerStats() {
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
        sharedPreferences = getActivity().getSharedPreferences("User", 0);
        int wins = sharedPreferences.getInt("Wins", -1);
        int games = sharedPreferences.getInt("Games", -1);

        numberWins = (TextView) view.findViewById(R.id.winsTextView);
        numberGames = (TextView) view.findViewById(R.id.gamesTextView);

        numberWins.setText(String.valueOf(wins));
        numberGames.setText(String.valueOf(games));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_stats_fragment, container, false);
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