package uem.dam.meraki.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import uem.dam.meraki.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    View view;
    Spinner spnTiendas;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Se configuran las opciones que hay en el Spinner
        view = inflater.inflate(R.layout.fragment_home, container, false);
        spnTiendas = view.findViewById(R.id.spnTiendas);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.tiendas_usuarios_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTiendas.setAdapter(adapter);

        spnTiendas.setOnItemSelectedListener(this);

        return view;
    }

    // Métodos para registrar qué opción del Spinner se ha pulsado
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        parent.getItemAtPosition(pos);

        Toast.makeText(getContext(), spnTiendas.getSelectedItem().toString() , Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
