package uem.dam.meraki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uem.dam.meraki.fragments.MapFragment;
import uem.dam.meraki.model.Producto;
import uem.dam.meraki.model.Tienda;

public class CatalogoActivity extends AppCompatActivity {

    private List<Producto> listaCatalogo = new ArrayList<Producto>();
    ArrayAdapter<Producto> arrayAdapterCatalogo;

    private DatabaseReference dr;

    TextView tvNombreTienda;
    TextView tvTipoTienda;
    ListView lvListaCatalogo;

    private String uid;
    private String nombreT;
    private String tipoTienda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        tvNombreTienda = findViewById(R.id.tvNombreTienda);
        tvTipoTienda = findViewById(R.id.tvTipoTienda);
        lvListaCatalogo = findViewById(R.id.lvListaCatalogo);

        // Inicializamos la Firebase Database
        dr = FirebaseDatabase.getInstance().getReference();

        // Recojemos el id mandado desde el MapFragment
        uid = getIntent().getStringExtra(MapFragment.CLAVE_UID);
        //Toast.makeText(this, uid, Toast.LENGTH_LONG).show();

        // Recojemos los datos de la tiedna logada
        getInfoTienda();

        // Añadimos los productos existentes a la lista
        listarDatos();

    }

    private void listarDatos() {
        Query query = dr.child("Tiendas").child(uid).child("catalogo");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaCatalogo.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                    Producto p = objSnaptshot.getValue(Producto.class);

                    listaCatalogo.add(p);
                    if (p.getProducto().equals(null)) {
                        Toast.makeText(CatalogoActivity.this, "El propietario de esta tienda no ha añadido nada todavía", Toast.LENGTH_LONG).show();
                    }

                    arrayAdapterCatalogo = new ArrayAdapter<Producto>(CatalogoActivity.this, android.R.layout.simple_list_item_1, listaCatalogo);
                    lvListaCatalogo.setAdapter(arrayAdapterCatalogo);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CatalogoActivity.this, "Error en los datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getInfoTienda() {
        // Consultamos en la base de datos los datos de la tienda pulsada
        Query query = dr.child("Tiendas").orderByChild("uid").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tienda t = snapshot.getValue(Tienda.class);

                    nombreT = t.getNombre();
                    tipoTienda = t.getTienda();

                    //Toast.makeText(getApplicationContext(), uid + ", " + nombreT, Toast.LENGTH_LONG).show();

                    // Rellenamos los textView con el nombre y el tipo de tienda
                    tvNombreTienda.setText(nombreT);
                    tvTipoTienda.setText("Tienda de " + tipoTienda);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error en los datos", Toast.LENGTH_LONG).show();
            }
        });
    }
}
