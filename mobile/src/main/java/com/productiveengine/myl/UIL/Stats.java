package com.productiveengine.myl.UIL;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.productiveengine.myl.Async.RefreshSongListTask;
import com.productiveengine.myl.BLL.CriteriaBL;
import com.productiveengine.myl.BLL.SongBL;

import static com.productiveengine.myl.Common.RequestCodes.ACTION_STOP;

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
