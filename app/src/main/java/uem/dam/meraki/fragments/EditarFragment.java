package uem.dam.meraki.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uem.dam.meraki.R;
import uem.dam.meraki.model.Producto;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditarFragment extends Fragment {

    private List<Producto> listaProductos = new ArrayList<Producto>();
    ArrayAdapter<Producto> arrayAdapterProducto;

    private FirebaseAuth fa;
    private DatabaseReference dr;

    View view;
    EditText etProducto;
    EditText etPrecio;
    Button btnAgregar;
    Button btnEliminar;
    ListView lvListaProductos;

    private String producto;
    private Double precio;
    private String id;

    Producto productoSelecionado;

    public EditarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_editar, container, false);

        etProducto = view.findViewById(R.id.etProducto);
        etPrecio = view.findViewById(R.id.etPrecio);
        btnAgregar = view.findViewById(R.id.btnAgregar);
        btnEliminar = view.findViewById(R.id.btnEliminar);
        lvListaProductos = view.findViewById(R.id.lvListaProductos);

        // Inicializamos la firebase
        inicializarFirebase();

        // Si existe una tienda logueada se sigue adelante con el programa
        if (fa.getCurrentUser() != null) {

            // Recogemos el id de la tienda logada
            id = fa.getCurrentUser().getUid();

            // Añadimos los productos existentes a la lista
            listarDatos();

            // Hacemos que el botón sea clickable
            btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Registramos al producto en firebase
                    String msj = validarDatos();

                    if (msj != null) {
                        Toast.makeText(getContext(), msj, Toast.LENGTH_LONG).show();

                    } else {
                        // Añadimos el producto que se haya rellenado a la firebase
                        Map<String, Object> insertarProducto = new HashMap<>();
                        insertarProducto.put("producto", producto);
                        insertarProducto.put("precio", precio);

                        dr.child("Tiendas").child(id).child("catalogo").child(producto).updateChildren(insertarProducto).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Producto añadido", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), getString(R.string.error_insercion_datos), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        // Limpiamos los EditText
                        etProducto.setText("");
                        etPrecio.setText("");
                    }
                }
            });

            // Cuando se pulse un producto de la lista se rellenarán los campos de producto y precio con sus datos
            lvListaProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    productoSelecionado = (Producto) parent.getItemAtPosition(position);
                    etProducto.setText(productoSelecionado.getProducto());
                    etPrecio.setText(String.valueOf(productoSelecionado.getPrecio()));

                    btnEliminar.setEnabled(true);
                }
            });

            // Cuando se pulse "Eliminar" eliminamos el producto seleccionado
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (etProducto.getText().toString().length() != 0 && etPrecio.getText().toString().length() != 0) {
                        Producto p = new Producto();
                        p.setProducto(productoSelecionado.getProducto());

                        dr.child("Tiendas").child(id).child("catalogo").child(p.getProducto()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Eliminado", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        });

                        // Limpiamos los EditText
                        etProducto.setText("");
                        etPrecio.setText("");
                    }

                    btnEliminar.setEnabled(false);
                }
            });
        }

        return view;
    }

    private void inicializarFirebase() {
        // Inicializamos la firebase Auth
        fa = FirebaseAuth.getInstance();

        // Inicializamos la Firebase Database
        dr = FirebaseDatabase.getInstance().getReference();
    }

    private void listarDatos() {
        Query query = dr.child("Tiendas").child(id).child("catalogo");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listaProductos.clear();

                    for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                        Producto p = objSnaptshot.getValue(Producto.class);
                        listaProductos.add(p);

                        if (getActivity() != null) {
                            arrayAdapterProducto = new ArrayAdapter<Producto>(getActivity(), android.R.layout.simple_list_item_1, listaProductos);
                            lvListaProductos.setAdapter(arrayAdapterProducto);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error en listar los datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Comprobamos que los datos introducidos no estén vacíos
    private String validarDatos(){
        String msj = null;

        producto = etProducto.getText().toString().trim();
        if (etPrecio.getText().toString().length() != 0) {
            precio = Double.parseDouble(etPrecio.getText().toString().trim());
        } else {
            msj = getString(R.string.no_datos);
            etPrecio.setError("Required");
        }

        if (producto.isEmpty()) {
            etProducto.setError("Required");
            msj = getString(R.string.no_datos);
        }
        return msj;
    }
}
