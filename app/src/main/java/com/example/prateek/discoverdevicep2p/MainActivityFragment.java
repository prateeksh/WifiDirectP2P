package com.example.prateek.discoverdevicep2p;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        /*Intent intent = getActivity().getIntent();
        if(intent != null){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }*/
        return rootView;
    }


}
