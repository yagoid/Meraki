package uem.dam.meraki.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uem.dam.meraki.R;
import uem.dam.meraki.model.Producto;
import uem.dam.meraki.model.Tienda;

/**
 * A simple {@link Fragment} subclass.
 */
public class CatalogoFragment extends Fragment {

    private List<Producto> listaCatalogo = new ArrayList<Producto>();
    ArrayAdapter<Producto> arrayAdapterCatalogo;

    private FirebaseAuth fa;
    private DatabaseReference dr;

    View view;
    TextView tvNombreTienda;
    TextView tvTipoTienda;
    ListView lvListaCatalogo;

    private String id;
    private String nombre;
    private String tipoTienda;

    public CatalogoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_catalogo, container, false);

        tvNombreTienda = view.findViewById(R.id.tvNombreTienda);
        tvTipoTienda = view.findViewById(R.id.tvTipoTienda);
        lvListaCatalogo = view.findViewById(R.id.lvListaCatalogo);

        // Inicializamos la firebase
        inicializarFirebase();

        // Recogemos el id de la tienda logada
        id = fa.getCurrentUser().getUid();

        // Recojemos los datos de la tiedna logada
        getInfoTienda();

        // Añadimos los productos existentes a la lista
        listarDatos();

        return view;
    }

    private void listarDatos() {
        Query query = dr.child("Tiendas").child(id).child("catalogo");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaCatalogo.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                    Producto p = objSnaptshot.getValue(Producto.class);
                    listaCatalogo.add(p);

                    arrayAdapterCatalogo = new ArrayAdapter<Producto>(getContext(), android.R.layout.simple_list_item_1, listaCatalogo);
                    lvListaCatalogo.setAdapter(arrayAdapterCatalogo);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error en los datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getInfoTienda() {
        // Consultamos en la base de datos los datos de la tienda pulsada
        Query query = dr.child("Tiendas").orderByChild("uid").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tienda t = snapshot.getValue(Tienda.class);

                    nombre = t.getNombre();
                    tipoTienda = t.getTienda();

                    // Rellenamos los textView con el nombre y el tipo de tienda
                    tvNombreTienda.setText(nombre);
                    tvTipoTienda.setText("Tienda de " + tipoTienda);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error en los datos", Toast.LENGTH_LONG).show();
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
