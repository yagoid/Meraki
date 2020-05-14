package uem.dam.meraki.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import uem.dam.meraki.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    View view;
    EditText etNombre;
    EditText etCorreo;
    EditText etPassword;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        etNombre = view.findViewById(R.id.etNombre);
        etCorreo = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);

        etNombre.setHint("Paco");
        etCorreo.setHint("Paco.recambios@gmail.com");
        etPassword.setHint("8caracteres");

        return view;
    }
}
