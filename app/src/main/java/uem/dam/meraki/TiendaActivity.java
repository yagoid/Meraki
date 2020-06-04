package uem.dam.meraki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uem.dam.meraki.fragments.CatalogoFragment;
import uem.dam.meraki.fragments.EditarFragment;
import uem.dam.meraki.fragments.ProfileTiendaFragment;
import uem.dam.meraki.model.Tienda;

public class TiendaActivity extends AppCompatActivity {

    public static final String CLAVE_UID = "uid";

    private FirebaseAuth fa;
    private DatabaseReference dr;

    private FragmentManager fm;
    private FragmentTransaction ft;

    private String uid;
    private String id;

    BottomNavigationView mBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda);

        mBottomNavigation = findViewById(R.id.bottomNavigation);

        // Inicializamos la firebase
        InicializarFirebase();

        // Cuando se habra la app veremos primero el fragment HomeFragment
        showSelectedFragment(new CatalogoFragment());

        // Método para cambiar los colores del bottom navigation view
        changeColor();

        // Controlamos cuando se pulsa el bottomNavigation para mostrar su correspondiente fragment
        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.menu_catalogo) {
                    // Abrimos el CatalogoFragment
                    showSelectedFragment(new CatalogoFragment());
                }

                if (menuItem.getItemId() == R.id.menu_editar) {
                    // Abrimos el EditarFragment
                    showSelectedFragment(new EditarFragment());

                    /*fm = getSupportFragmentManager();
                    ft = fm.beginTransaction();

                    EditarFragment editarFragment = new EditarFragment();

                    // Guardamos el nombre en CLAVE_NOMBRE para mandarlos al fragment
                    //Bundle bundle = new Bundle();
                    //bundle.putString(CLAVE_UID, id);

                    // Pasamos el nombre recojida con anterioridad a HomeFragment
                    //editarFragment.setArguments(bundle);


                    // Abrimos el mapsFragment
                    ft.add(R.id.container, editarFragment);
                    ft.commit();*/

                }

                if (menuItem.getItemId() == R.id.menu_perfil) {
                    // Abrimos el ProfileTiendaFragment
                    showSelectedFragment(new ProfileTiendaFragment());
                }

                return true;
            }
        });

    }

    // Recogemos la información del usuario logado de la base de datos
    private void getInfoUser() {
        String uid = fa.getCurrentUser().getUid();
        dr.child("Tiendas").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    id = dataSnapshot.child("uid").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void InicializarFirebase() {
        // Inicializamos la firebase Auth
        fa = FirebaseAuth.getInstance();

        // Inicializamos la Firebase Database
        dr = FirebaseDatabase.getInstance().getReference();
    }

    // Referenciamos el menú de cerrar sesión
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cerrar_sesion, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Controlamos si se ha pulsado cerrar sesión
        if (item.getItemId() == R.id.itmCerrarSesion){
            fa.signOut();
            Intent i = new Intent(TiendaActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // Método que permite elegir el fragment
    private void showSelectedFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // Metodo para cambiar el color del icono y texto cuando esté seleccionado en el bottom navigation view
    private void changeColor() {
        ColorStateList iconsColorStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        Color.parseColor("#F2F2F2"),
                        Color.parseColor("#F2B705")
                });

        ColorStateList textColorStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        Color.parseColor("#F2F2F2"),
                        Color.parseColor("#F2B705")
                });

        mBottomNavigation.setItemIconTintList(iconsColorStates);
        mBottomNavigation.setItemTextColor(textColorStates);
    }
}
