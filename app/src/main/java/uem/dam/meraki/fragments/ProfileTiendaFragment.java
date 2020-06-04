package uem.dam.meraki.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uem.dam.meraki.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileTiendaFragment extends Fragment {

    private FirebaseAuth fa;
    private DatabaseReference dr;

    View view;
    EditText etNombre;
    EditText etCorreo;
    EditText etUbicacion;

    private String id;
    private String email;
    private String nombre;
    private String latitud;
    private String longitud;

    public ProfileTiendaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_tienda, container, false);

        etNombre = view.findViewById(R.id.etNombre);
        etCorreo = view.findViewById(R.id.etEmail);
        etUbicacion = view.findViewById(R.id.etUbicacion);

        // Inicializamos la firebase
        inicializarFirebase();

        // Recogemos el id de la tienda logada
        id = fa.getCurrentUser().getUid();

        // Recogemos información de la tienda
        getInfoTien();

        return view;
    }

    // Recogemos la información de la tienda logada de la base de datos y rellenamos los EditText
    private void getInfoTien() {
        dr.child("Tiendas").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nombre = dataSnapshot.child("nombre").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    latitud = dataSnapshot.child("latitud").getValue().toString();
                    longitud = dataSnapshot.child("longitud").getValue().toString();

                    etNombre.setHint(nombre);
                    etCorreo.setHint(email);
                    etUbicacion.setHint(latitud + ", " + longitud);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void inicializarFirebase() {
        // Inicializamos la firebase Auth
        fa = FirebaseAuth.getInstance();

        // Inicializamos la Firebase Database
        dr = FirebaseDatabase.getInstance().getReference();
    }
}
