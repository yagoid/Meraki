package uem.dam.meraki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import uem.dam.meraki.fragments.CatalogoFragment;
import uem.dam.meraki.fragments.EditarFragment;
import uem.dam.meraki.fragments.ProfileTiendaFragment;

public class TiendaActivity extends AppCompatActivity {

    private FirebaseAuth fa;

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
                }

                if (menuItem.getItemId() == R.id.menu_perfil) {
                    // Abrimos el ProfileTiendaFragment
                    showSelectedFragment(new ProfileTiendaFragment());
                }

                return true;
            }
        });

    }

    private void InicializarFirebase() {
        // Inicializamos la firebase Auth
        fa = FirebaseAuth.getInstance();
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
