package uem.dam.meraki.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class CatalogoFragment extends Fragment {

    private List<Producto> listaCatalogo = new ArrayList<Producto>();
    ArrayAdapter<Producto> arrayAdapterInventario;

    private FirebaseAuth fa;
    private DatabaseReference dr;

    View view;
    TextView tvNombreTienda;
    TextView tvTipoTienda;
    ListView lvListaCatalogo;
    TextView tvTelefono;
    TextView tvDireccion;
    TextView tvVerificado;
    ImageView ivCorrect;
    TextView tvCantLikes;

    private String id;
    private String nombre;
    private String tipoTienda;
    private String telefono;
    private String direccion;

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
        tvTelefono = view.findViewById(R.id.tvTelefono);
        tvDireccion = view.findViewById(R.id.tvDireccion);
        tvVerificado = view.findViewById(R.id.tvVerificado);
        ivCorrect = view.findViewById(R.id.ivCorrect);
        tvCantLikes = view.findViewById(R.id.tvCantLikes);

        // Inicializamos la firebase
        inicializarFirebase();

        // Si existe una tienda logueada se sigue adelante con el programa
        if (fa.getCurrentUser() != null) {

            // Recogemos el id de la tienda logada
            id = fa.getCurrentUser().getUid();

            // Recojemos los datos de la tiedna logada
            getInfoTienda();

            // Añadimos los productos existentes a la lista
            listarDatos();

            // Contamos los likes que tiene la tienda
            contarLikes();
        }

        return view;
    }

    private void contarLikes() {
        Query query = dr.child("Tiendas").child(id);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Comprobamos si el child likes existe
                    if (dataSnapshot.hasChild("likes")) {
                        Query query2 = dr.child("Tiendas").child(id).child("likes");
                        query2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long likes = dataSnapshot.getChildrenCount();
                                String cantLikes = Long.toString(likes);

                                tvCantLikes.setText(cantLikes);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    } else {
                        tvCantLikes.setText("0");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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

                    if (getActivity() != null) {
                        arrayAdapterInventario = new ArrayAdapter<Producto>(getActivity(), android.R.layout.simple_list_item_1, listaCatalogo);
                        lvListaCatalogo.setAdapter(arrayAdapterInventario);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error en listar los datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getInfoTienda() {
        // Consultamos en la base de datos los datos de la tienda pulsada
        Query query = dr.child("Tiendas").child(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nombre = dataSnapshot.child("nombre").getValue().toString();
                    tipoTienda = dataSnapshot.child("tienda").getValue().toString();

                    // Rellenamos los textView con el nombre y el tipo de tienda
                    tvNombreTienda.setText(nombre);
                    tvTipoTienda.setText("Tienda de " + tipoTienda);

                    // Si existe el teléfono y dirección, se rellenan en sus respectivos TextView
                    if (dataSnapshot.hasChild("telefono") && dataSnapshot.hasChild("direccion")) {
                        telefono = dataSnapshot.child("telefono").getValue().toString();
                        direccion = dataSnapshot.child("direccion").getValue().toString();

                        // Se rellenan los textView con el teléfono y dirección
                        tvTelefono.setText("Teléfono: " + telefono);
                        tvDireccion.setText("Dirección: " + direccion);

                        // Si se pulsa el número de teléfono se llevará a la pantalla de llamar al mismo número
                        tvTelefono.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String dial = "tel:" + telefono;
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                            }
                        });

                    }
                    // Si existe el nif, la tienda está verificada. Por lo que lo mostramos
                    if (dataSnapshot.hasChild("nif")) {
                        tvVerificado.setVisibility(View.VISIBLE);
                        ivCorrect.setVisibility(View.VISIBLE);
                    }
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
