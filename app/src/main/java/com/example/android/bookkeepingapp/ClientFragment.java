package com.example.android.bookkeepingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Use the {@link ClientFragment} factory method to
 * create an instance of this fragment.
 */
public class ClientFragment extends Fragment {



    public ClientFragment() {
        // Required empty public constructor
    }

    // Inflate the layout for this fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_client, container, false);

    }



}
