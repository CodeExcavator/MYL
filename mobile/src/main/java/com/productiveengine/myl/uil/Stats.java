package com.productiveengine.myl.uil;

import android.content.Context;
import android.support.v4.app.Fragment;

public class Stats extends Fragment {
    private OnFragmentInteractionListener mListener;

    public Stats() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
