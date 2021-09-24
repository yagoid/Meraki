package uem.dam.meraki.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import uem.dam.meraki.R;
import uem.dam.meraki.model.ValidarNif;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileTiendaFragment extends Fragment {

    private FirebaseAuth fa;
    private DatabaseReference dr;

    View view;
    EditText etNombre;
    EditText etTelefono;
    EditText etDireccion;
    EditText etNIF;
    Button btnGuardar;
    Button btnGuardarNIF;

    private String id;
    private String nombre;
    private String telefono;
    private String direccion;
    private String nif;

    public ProfileTiendaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_tienda, container, false);

        etNombre = view.findViewById(R.id.etNombre);
        etTelefono = view.findViewById(R.id.etTelefono);
        etDireccion = view.findViewById(R.id.etDireccion);
        etNIF = view.findViewById(R.id.etNIF);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnGuardarNIF = view.findViewById(R.id.btnVerificar);

        // Inicializamos la firebase
        inicializarFirebase();

        // Recogemos el id de la tienda logada
        if (fa.getCurrentUser() != null) {
            id = fa.getCurrentUser().getUid();
        }

        // Recogemos información de la tienda
        getInfoTien();

        // Cuando se pulse "Guardar" se guardarán en la base de datos la información escrita
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Registramos los datos en firebase
                String msj = validarDatos();

                // Método que guarda los datos rellenados
                guardarDatos(msj);
            }
        });

        // Cuando se pulse "Verificar" se verificá si el NIF es correcto, y si es así se guardará en la base de datos
        btnGuardarNIF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Método que guarda el NIF en la base de datos
                guardarNif();
            }
        });

        return view;
    }

    // Si es válido el NIF se guarda en la Firebase
    private void guardarNif() {
        nif = etNIF.getText().toString();
        ValidarNif validarNif = new ValidarNif();

        if (validarNif.isOK(nif) == false){ // Aviso y mantengo el foco
            etNIF.setError("NIF no válido");
            etNIF.requestFocus();
            Toast.makeText(getContext(), "NIF incorrecto",Toast.LENGTH_LONG).show();

        } else {
            // Añadimos los datos rellenados en la database
            Map<String, Object> insertarNIF = new HashMap<>();
            insertarNIF.put("nif", nif);

            dr.child("Tiendas").child(id).updateChildren(insertarNIF).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "NIF añadido", Toast.LENGTH_LONG).show();

                        // Limpiamos los EditText
                        etNIF.setText("");

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_insercion_datos), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Si los datos de teléfono y dirección son válidos, se guardan los datos en la Firebase
    private void guardarDatos(String msj) {
        if (msj != null) {
            Toast.makeText(getContext(), msj, Toast.LENGTH_LONG).show();

        } else {
            // Añadimos los datos rellenados en la database
            Map<String, Object> insertarDatos = new HashMap<>();
            insertarDatos.put("telefono", telefono);
            insertarDatos.put("direccion", direccion);

            dr.child("Tiendas").child(id).updateChildren(insertarDatos).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Datos añadidos", Toast.LENGTH_LONG).show();

                        // Limpiamos los EditText
                        etTelefono.setText("");
                        etDireccion.setText("");
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_insercion_datos), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Recogemos la información de la tienda logada de la base de datos y rellenamos los EditText
    private void getInfoTien() {
        dr.child("Tiendas").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nombre = dataSnapshot.child("nombre").getValue().toString();

                    // Si existe el teléfono y dirección, se rellenan con hint en sus respectivos EditText
                    if (dataSnapshot.hasChild("telefono") && dataSnapshot.hasChild("direccion")) {
                        telefono = dataSnapshot.child("telefono").getValue().toString();
                        direccion = dataSnapshot.child("direccion").getValue().toString();

                        etTelefono.setHint(telefono);
                        etDireccion.setHint(direccion);
                    }
                    // Si existe el nif, se rellenan con hint en su respectivo EditText
                    if (dataSnapshot.hasChild("nif")) {
                        nif = dataSnapshot.child("nif").getValue().toString();

                        etNIF.setHint(nif);
                    }

                    etNombre.setHint(nombre);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // Comprobamos que los datos introducidos no estén vacíos o incorrectos
    private String validarDatos(){
        telefono = etTelefono.getText().toString().trim();
        direccion = etDireccion.getText().toString().trim();

        String msj = null;

        if (telefono.isEmpty()) {
            etTelefono.setError("Required");
            msj = getString(R.string.no_datos);

        } else if (direccion.isEmpty()) {
            etDireccion.setError("Required");
            msj = getString(R.string.no_datos);

        } else if (telefono.length() != 9){
            etDireccion.setError("Número no válido");
            msj = getString(R.string.telefono_incorrecto);
        }

        return msj;
    }

    private void inicializarFirebase() {
        // Inicializamos la firebase Auth
        fa = FirebaseAuth.getInstance();

        // Inicializamos la Firebase Database
        dr = FirebaseDatabase.getInstance().getReference();
    }
}
