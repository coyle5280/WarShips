package GameBoardFragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import project.mobile.warships.R;


public class GameBoardFragment extends Fragment {

    private int STATUS;

    private final int SETUPBOARD = 0;
    private final int VIEW = 1;
    private final int GAMEBOARD_A = 0;
    private final int GAMEBOARD_B = 1;
    private int thisBoard;


//    private OnFragmentInteractionListener mListener;



    public GameBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        STATUS = 0;

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            thisBoard = bundle.getInt("board");
        }

        if(thisBoard == 0){
            return inflater.inflate(R.layout.game_board_a, container, false);
        }else{
            return inflater.inflate(R.layout.game_board_b, container, false);
        }


    }


    public void setAttacked(String id){

      TextView attackedTextView = (TextView) getView().findViewWithTag(id);
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
