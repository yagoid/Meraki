package uem.dam.meraki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import uem.dam.meraki.adapter.FavoritosAdapter;
import uem.dam.meraki.model.Tienda;

public class FavoritosActivity extends AppCompatActivity {

    public static final String CLAVE_IDT = "UID";

    private FirebaseAuth fa;
    private DatabaseReference dr;

    FavoritosAdapter fAdapter;
    RecyclerView rvFavoritos;
    //LinearLayoutManager llm;
    ArrayList<Tienda> fFavoritosList = new ArrayList<>();

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        rvFavoritos = findViewById(R.id.rvFavoritos);

        rvFavoritos.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos la firebase
        inicializarFirebase();

        // Recogemos el id del usuario logado
        if (fa.getCurrentUser() != null) {
            id = fa.getCurrentUser().getUid();
        }

        agregarFavoritosARv();

    }

    private void agregarFavoritosARv() {
        // En primer lugar recogemos las tiendas de la firebase
        dr.child("Usuarios").child(id).child("favoritos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    fFavoritosList.clear();

                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        String idTienda = ds.getValue().toString();

                        dr.child("Tiendas").child(idTienda).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    String nombre = dataSnapshot.child("nombre").getValue().toString();
                                    String tienda = dataSnapshot.child("tienda").getValue().toString();
                                    String telefono = "";
                                    String direccion = "";

                                    // Si la tienda ha añadido su teléfono y dirección se muestran en el rv
                                    if (dataSnapshot.hasChild("telefono") && dataSnapshot.hasChild("direccion")) {
                                        telefono = dataSnapshot.child("telefono").getValue().toString();
                                        direccion = dataSnapshot.child("direccion").getValue().toString();
                                    }

                                    String uid = dataSnapshot.child("uid").getValue().toString();

                                    // Añadimos a la lista todas las tiendas que estén en favoritos del usuario
                                    fFavoritosList.add(new Tienda(nombre, tienda, telefono, direccion, uid));
                                    //Toast.makeText(getApplicationContext(), nombre + " " + telefono, Toast.LENGTH_LONG).show();

                                    // Enviamos la lista con los favoritos al RecyclerView
                                    fAdapter = new FavoritosAdapter(fFavoritosList);
                                    fAdapter.setListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Cuando pulsamos a una tienda se nos lleva a su catálogo
                                            Intent i = new Intent(FavoritosActivity.this, CatalogoActivity.class);
                                            // Con un put extra pasamos la posicion que hemos pulsado a la CLAVE_IDT
                                            i.putExtra(CLAVE_IDT, fFavoritosList.get(rvFavoritos.getChildAdapterPosition(v)).getUid());
                                            startActivity(i);
                                            //Toast.makeText(getApplicationContext(), fFavoritosList.get(rvFavoritos.getChildAdapterPosition(v)).getUid(), Toast.LENGTH_LONG).show();

                                        }
                                    });
                                    rvFavoritos.setAdapter(fAdapter);


                                } else {
                                    Toast.makeText(getApplicationContext(), "sdf", Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }//for


                    /*
                    fAdapter.setListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Cuando pulsamos a una tienda se nos lleva a su catálogo
                            //Intent i = new Intent(FavoritosActivity.this, CatalogoActivity.class);
                            // Con un put extra pasamos la posicion que hemos pulsado a la CLAVE_TITULO
                            Toast.makeText(getApplicationContext(), fFavoritosList.get(rvFavoritos.getChildAdapterPosition(v)).getUid(), Toast.LENGTH_LONG).show();
                            //i.putExtra(CLAVE_IDT, fFavoritosList.get(rvFavoritos.getChildAdapterPosition(v)).getUid());
                            //startActivity(i);
                        }
                    });

                    if (fFavoritosList.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Esta mierda no va", Toast.LENGTH_LONG).show();
                    }

                    fAdapter = new FavoritosAdapter(fFavoritosList);

                    // Controla el item del rv
                    llm = new LinearLayoutManager(getApplicationContext());

                    rvFavoritos.setLayoutManager(llm);
                    rvFavoritos.setHasFixedSize(true);
                    rvFavoritos.setAdapter(fAdapter);*/

                } else {
                    Toast.makeText(getApplicationContext(), "No hay ninguna tienda en favoritos", Toast.LENGTH_LONG).show();
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
