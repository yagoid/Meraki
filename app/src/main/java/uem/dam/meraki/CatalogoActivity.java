package uem.dam.meraki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

import uem.dam.meraki.fragments.MapFragment;
import uem.dam.meraki.model.Producto;
import uem.dam.meraki.popups.PopupInvitado;

public class CatalogoActivity extends AppCompatActivity {

    private List<Producto> listaCatalogo = new ArrayList<Producto>();
    ArrayAdapter<Producto> arrayAdapterCatalogo;

    private FirebaseAuth fa;
    private DatabaseReference dr;

    TextView tvNombreTienda;
    TextView tvTipoTienda;
    ListView lvListaCatalogo;
    TextView tvTelefono;
    TextView tvDireccion;
    TextView tvVerificado;
    ImageView ivCorrect;
    ImageView ivFavoritos;
    ImageView ivLike;
    TextView tvCantLikes;

    private String idUsuario;
    private String uid;
    private String nombreT;
    private String tipoTienda;
    private String telefono;
    private String direccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);
        getSupportActionBar().hide();

        tvNombreTienda = findViewById(R.id.tvNombreTienda);
        tvTipoTienda = findViewById(R.id.tvTipoTienda);
        lvListaCatalogo = findViewById(R.id.lvListaCatalogo);
        tvTelefono = findViewById(R.id.tvTelefono);
        tvDireccion = findViewById(R.id.tvDireccion);
        tvVerificado = findViewById(R.id.tvVerificado);
        ivCorrect = findViewById(R.id.ivCorrect);
        ivFavoritos = findViewById(R.id.ivFavoritos);
        ivLike = findViewById(R.id.ivLike);
        tvCantLikes = findViewById(R.id.tvCantLikes);

        // Inicializamos la firebase
        inicializarFirebase();

        // Si existe un usuario logueado significa que el usuario está registrado
        if (fa.getCurrentUser() != null) {

            // Recogemos el id del usuario logado
            idUsuario = fa.getCurrentUser().getUid();

        }

        // Recogemos el id de la tienda mandado desde el MapFragment
        uid = getIntent().getStringExtra(MapFragment.CLAVE_UID);
        if (uid == null) {
            // Recogemos el id de la tienda mandado desde FavoritosActivity
            uid = getIntent().getStringExtra(FavoritosActivity.CLAVE_IDT);
        }



        // Recojemos los datos de la tienda
        getInfoTienda();

        // Añadimos los productos existentes a la lista
        listarDatos();

        // Modificamos si la tienda está en favoritos o no
        modificarFavoritos();

        // Modificamos los likes de la tienda
        modificarLikes();


        // Contamos los likes que tiene la tienda
        contarLikes();


    }

    private void contarLikes() {
        Query query = dr.child("Tiendas").child(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Comprobamos si el child likes existe
                    if (dataSnapshot.hasChild("likes")) {
                        Query query2 = dr.child("Tiendas").child(uid).child("likes");
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

    private void modificarLikes() {
        // Si existe un usuario logueado significa que el usuario está registrado
        if (fa.getCurrentUser() != null) {

            Query query = dr.child("Tiendas").child(uid);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Comprobamos si el child likes existe
                        if (dataSnapshot.hasChild("likes")) {

                            // Se comprueba si el id del usuario está en la rama de likes de la tienda
                            Query query2 = dr.child("Tiendas").child(uid).orderByChild(idUsuario).equalTo(idUsuario).limitToFirst(1);
                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                    if (dataSnapshot2.exists()) {
                                        ivLike.setImageResource(R.drawable.like);
                                        ivLike.setTag("like");
                                    } else {
                                        ivLike.setImageResource(R.drawable.no_like);
                                        ivLike.setTag("no like");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                        } else {
                            // Si por el contrario no existe el child "likes", se agrega la imagen no_like
                            ivLike.setImageResource(R.drawable.no_like);
                            ivLike.setTag("no like");
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } else {
            // Si el usuario no está logado, se agrega la imagen no_like
            ivLike.setImageResource(R.drawable.no_like);
            ivLike.setTag("no like");
        }

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si existe un usuario logueado significa que el usuario está registrado
                if (fa.getCurrentUser() != null) {

                    // Si el usuario ya ha dado like a la tienda, se elimina el like
                    if (ivLike.getTag().toString().equals("like")) {

                        dr.child("Tiendas").child(uid).child("likes").child(idUsuario).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "No me gusta", Toast.LENGTH_LONG).show();
                            }
                        });
                        ivLike.setImageResource(R.drawable.no_like);

                        // Si el id del usuario no está en likes de la tienda, se añade
                    } else if (ivLike.getTag().toString().equals("no like")) {
                        // Añadimos un like a la tienda
                        Map<String, Object> insertarLike = new HashMap<>();
                        insertarLike.put(idUsuario, idUsuario);

                        dr.child("Tiendas").child(uid).child("likes").updateChildren(insertarLike).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Me gusta", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_insercion_datos), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        ivLike.setImageResource(R.drawable.like);
                    }

                }  else {
                    // Si el usuario no está logado no se deja que tenga esta función
                    Intent i = new Intent(CatalogoActivity.this, PopupInvitado.class);
                    startActivity(i);
                }
            }
        });

    }

    private void modificarFavoritos() {
        // Si existe un usuario logueado significa que el usuario está registrado
        if (fa.getCurrentUser() != null) {

            Query query = dr.child("Usuarios").child(idUsuario);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Comprobamos si el child favoritos existe
                        if (dataSnapshot.hasChild("favoritos")) {

                            // Se comprueba si el id de la tienda en la que estamos la tenemos en favoritos
                            Query query2 = dr.child("Usuarios").child(idUsuario).orderByChild(uid).equalTo(uid).limitToFirst(1);
                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                    if (dataSnapshot2.exists()) {
                                        ivFavoritos.setImageResource(R.drawable.favoritos);
                                        ivFavoritos.setTag("favorito");
                                    } else {
                                        ivFavoritos.setImageResource(R.drawable.no_favoritos);
                                        ivFavoritos.setTag("no favorito");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Error en los datos", Toast.LENGTH_LONG).show();
                                }
                            });

                        } else {
                            // Si por el contrario no existe el child "favoritos", se agrega la imagen no_favoritos
                            ivFavoritos.setImageResource(R.drawable.no_favoritos);
                            ivFavoritos.setTag("no favorito");
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Error en los datos", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // Si el usuario no está logado, se agrega la imagen no_favoritos
            ivFavoritos.setImageResource(R.drawable.no_favoritos);
            ivFavoritos.setTag("no favorito");
        }

        ivFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si existe un usuario logueado significa que el usuario está registrado
                if (fa.getCurrentUser() != null) {

                    // Si la tienda ya está en favoritos, se elimina
                    if (ivFavoritos.getTag().toString().equals("favorito")) {

                        dr.child("Usuarios").child(idUsuario).child("favoritos").child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Eliminado de favoritos", Toast.LENGTH_LONG).show();
                            }
                        });
                        ivFavoritos.setImageResource(R.drawable.no_favoritos);

                        // Si la tienda no está en favoritos, se añade
                    } else if (ivFavoritos.getTag().toString().equals("no favorito")) {

                        // Añadimos a favoritos a la tienda
                        Map<String, Object> insertarFavorito = new HashMap<>();
                        insertarFavorito.put(uid, uid);

                        dr.child("Usuarios").child(idUsuario).child("favoritos").updateChildren(insertarFavorito).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Añadido a favoritos", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_insercion_datos), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        ivFavoritos.setImageResource(R.drawable.favoritos);
                    }
                } else {
                    // Si el usuario no está logado no se deja que tenga esta función
                    Intent i = new Intent(CatalogoActivity.this, PopupInvitado.class);
                    startActivity(i);
                }
            }
        });
    }

    private void inicializarFirebase() {
        // Inicializamos la firebase Auth
        fa = FirebaseAuth.getInstance();

        // Inicializamos la Firebase Database
        dr = FirebaseDatabase.getInstance().getReference();
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
                    if (listaCatalogo.isEmpty()) {
                        Toast.makeText(CatalogoActivity.this, "Esta tienda no tiene inventario", Toast.LENGTH_LONG).show();
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
        Query query = dr.child("Tiendas").child(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nombreT = dataSnapshot.child("nombre").getValue().toString();
                    tipoTienda = dataSnapshot.child("tienda").getValue().toString();

                    // Rellenamos los textView con el nombre y el tipo de tienda
                    tvNombreTienda.setText(nombreT);
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
                Toast.makeText(getApplicationContext(), "Error en los datos", Toast.LENGTH_LONG).show();
            }
        });
    }
}
