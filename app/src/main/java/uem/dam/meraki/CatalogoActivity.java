package uem.dam.meraki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import uem.dam.meraki.fragments.MapFragment;
import uem.dam.meraki.model.Tienda;

public class CatalogoActivity extends AppCompatActivity {

    private DatabaseReference dr;

    TextView tvNombreTienda;
    TextView tvTipoTienda;

    private String uid;
    private String nombreT;
    private String tipoTienda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        tvNombreTienda = findViewById(R.id.tvNombreTienda);
        tvTipoTienda = findViewById(R.id.tvTipoTienda);

        // Inicializamos la Firebase Database
        dr = FirebaseDatabase.getInstance().getReference();

        uid = getIntent().getStringExtra(MapFragment.CLAVE_UID);
        //Toast.makeText(this, uid, Toast.LENGTH_LONG).show();

        getInfoTienda();

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
