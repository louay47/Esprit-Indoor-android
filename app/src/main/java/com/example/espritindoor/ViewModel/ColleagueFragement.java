package com.example.espritindoor.ViewModel;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.espritindoor.Adapters.ContactListAdapter;

import com.example.espritindoor.Model.Contact;
import com.example.espritindoor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColleagueFragement extends Fragment {

    List<Contact> mContacts = new ArrayList<>();


    public ColleagueFragement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_colleague_fragement, container, false);
        // Inflate the layout for this fragment
        RecyclerView recyclerView =  v.findViewById(R.id.contact_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new ContactListAdapter(getActivity() , getContacts()));

        return v;
    }

    public List<Contact> getContacts ()

    {
        Contact c = new Contact("Louay ", "Student", "Louay@gmail.com", "21344384", "louayIn", "#");
        Contact c1 = new Contact("Karim", "Student", "Karim@gmail.com", "21344384", "KarimIn", "#");
        Contact c2 = new Contact("Walid", "Student", "Walid@gmail.com", "21344384", "WalidIn", "#");
        Contact c3 = new Contact("Baha", "Student", "Baha@gmail.com", "21344384", "BahaIn", "#");
        mContacts.add(c);
        mContacts.add(c1);
        mContacts.add(c2);
        mContacts.add(c3);

        return mContacts;

    }
}
