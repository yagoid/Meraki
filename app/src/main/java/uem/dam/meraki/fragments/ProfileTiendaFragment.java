package uem.dam.meraki.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uem.dam.meraki.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileTiendaFragment extends Fragment {

    public ProfileTiendaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_tienda, container, false);
    }
}