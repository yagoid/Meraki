package uem.dam.meraki.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uem.dam.meraki.CatalogoActivity;
import uem.dam.meraki.FavoritosActivity;
import uem.dam.meraki.R;
import uem.dam.meraki.UsuarioActivity;
import uem.dam.meraki.popups.PopupInvitado;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FirebaseAuth fa;
    private DatabaseReference dr;

    View view;
    Spinner spnTiendas;
    TextView tvSaludo;
    TextView tvTienda;
    TextView tvProducto;
    EditText etProducto;
    ImageView ivListaFavoritos;

    private String tienda;
    private String producto;
    private String nombre;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Se configuran las opciones que hay en el Spinner
        view = inflater.inflate(R.layout.fragment_home, container, false);
        spnTiendas = view.findViewById(R.id.spnTiendas);
        tvSaludo = view.findViewById(R.id.tvSaludo);
        etProducto = view.findViewById(R.id.etBuscarProd);
        ivListaFavoritos = view.findViewById(R.id.ivListaFavoritos);

        // Inicializamos la firebase
        inicializarFirebase();

        // Configuramos el Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.tiendas_usuarios_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTiendas.setAdapter(adapter);
        spnTiendas.setOnItemSelectedListener(this);

        // Chequeamos si se he cambiado el texto del EditText
        etProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                // Cada vez que el texto cambie se guardará lo escrito en "producto"
                producto = etProducto.getText().toString();
                tvProducto = getActivity().findViewById(R.id.tvProducto);

                // Trasformamos el texto del producto a mayusculas
                producto = producto.toUpperCase();

                // Mandamos el producto escrito en el EditText al textView del UsuarioActivity
                tvProducto.setText(producto);
            }
        });


        ivListaFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si existe un usuario logueado significa que el usuario está registrado
                if (fa.getCurrentUser() != null) {
                    Intent i = new Intent(getActivity(), FavoritosActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getActivity(), PopupInvitado.class);
                    startActivity(i);
                }
            }
        });

        // Recibimos el nombre del usuario logado y lo escribimos en tvSaludo
        if (getArguments() != null) {
            nombre = getArguments().getString(UsuarioActivity.CLAVE_NOMBRE);
        }
        if (nombre != null) {
            tvSaludo.setText("¡HOLA " + nombre + "!");
        }

        return view;
    }

    // Métodos para registrar qué opción del Spinner se ha pulsado
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        parent.getItemAtPosition(pos);

        // Comprobamos que opción del spinner se ha pulsado y la guardamos en tienda
        tienda = spnTiendas.getSelectedItem().toString();

        tvTienda = getActivity().findViewById(R.id.tvTienda);
        tvTienda.setText(tienda);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void inicializarFirebase() {
        // Inicializamos la firebase Auth
        fa = FirebaseAuth.getInstance();

        // Inicializamos la Firebase Database
        dr = FirebaseDatabase.getInstance().getReference();
    }
}
