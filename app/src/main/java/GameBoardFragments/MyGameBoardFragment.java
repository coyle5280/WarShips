package GameBoardFragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import project.mobile.warships.R.id;
import org.w3c.dom.Text;

import project.mobile.warships.R;


public class MyGameBoardFragment extends Fragment {

    private int STATUS;

    private final int SETUPBOARD = 0;
    private final int VIEW = 1;


//    private OnFragmentInteractionListener mListener;



    public MyGameBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        STATUS = 0;

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_board_layout, container, false);
    }


    public void setAttacked(int id){



//       TextView attackedTextView = (TextView) getView().findViewById(R.id.id);
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
