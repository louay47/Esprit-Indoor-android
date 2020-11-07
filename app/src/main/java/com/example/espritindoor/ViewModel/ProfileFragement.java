package com.example.espritindoor.ViewModel;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.espritindoor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragement extends Fragment {


    public ProfileFragement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v = inflater.inflate(R.layout.fragment_profil_fragement, container, false);

        return v;
    }

}
