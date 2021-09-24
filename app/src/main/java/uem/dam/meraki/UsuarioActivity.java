package uem.dam.meraki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uem.dam.meraki.fragments.HomeFragment;
import uem.dam.meraki.fragments.MapFragment;
import uem.dam.meraki.fragments.ProfileFragment;
import uem.dam.meraki.popups.PopupInvitado;

public class UsuarioActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 123;

    public static final String LAT_KEY = "LAT";
    public static final String LON_KEY = "LON";
    public static final String CLAVE_TIENDA = "Tienda";
    public static final String CLAVE_PRODUCTO = "Producto";
    public static final String CLAVE_EMAIL = "Email";
    public static final String CLAVE_NOMBRE = "Nombre";

    private FirebaseAuth fa;
    private DatabaseReference dr;

    private FragmentManager fm;
    private FragmentTransaction ft;

    BottomNavigationView mBottomNavigation;
    FusedLocationProviderClient flClient;
    Location miLoc;

    private TextView tvTienda;
    private TextView tvProducto;

    private String uid;
    private String tienda;
    private String producto;
    private String email;
    private String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        mBottomNavigation = findViewById(R.id.bottomNavigation);
        tvTienda = findViewById(R.id.tvTienda);
        tvProducto = findViewById(R.id.tvProducto);

        // Inicializamos la firebase
        InicializarFirebase();

        // Si existe un usuario logueado significa que el usuario está registrado
        if (fa.getCurrentUser() != null) {

            // Recogemos el id de la tienda logada
            uid = fa.getCurrentUser().getUid();

            // Recogemos información del usuario
            getInfoUser();

        }

        // Pedimos los permisos de ubicación
        pedirPermisos();

        // Cuando se habra UsuarioActivity veremos primero el fragment HomeFragment
        abrirHomeFragment();

        // Método para cambiar los colores del bottom navigation view
        changeColor();

        // Controlamos cuando se pulsa el bottomNavigation para mostrar su correspondiente fragment
        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.menu_home) {
                    // Abrimos el HomeFragment
                    abrirHomeFragment();

                    tvProducto.setText("");
                }

                if (menuItem.getItemId() == R.id.menu_maps) {
                    // Recibimos los datos del spinner mandados del HomeFragment
                    tienda = tvTienda.getText().toString().trim();
                    producto = tvProducto.getText().toString();

                    // Abrimos el MapFragment
                    abrirMapFragment();
                }

                if (menuItem.getItemId() == R.id.menu_profile) {
                    if (fa.getCurrentUser() != null) {
                        // Abrimos el ProfileFragment
                        abrirProfileFragment();
                    } else {
                        Intent i = new Intent(UsuarioActivity.this, PopupInvitado.class);
                        startActivity(i);
                    }
                }

                return true;
            }
        });

    }

    // Le pasamos datos a MapFragment y hacemos que se ejecute
    private void abrirMapFragment() {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        // Inicializamos un nuevo fragment
        MapFragment mapFragment = new MapFragment();

        // Guardamos la latitud y longitud de mi localización en LAT_KEY y LON_KEY para mandarlos al fragment
        Bundle bundle = new Bundle();
        bundle.putDouble(LAT_KEY, miLoc.getLatitude());
        bundle.putDouble(LON_KEY, miLoc.getLongitude());

        bundle.putString(CLAVE_TIENDA, tienda);
        bundle.putString(CLAVE_PRODUCTO, producto);

        // Pasamos la latitud y longitud de nuestra posición actual recojida con anterioridad a MapsFragment
        mapFragment.setArguments(bundle);

        //Toast.makeText(getApplicationContext(), "lat: " + miLoc.getLatitude() + " lon: " + miLoc.getLongitude(), Toast.LENGTH_LONG).show();

        // Abrimos el mapsFragment
        ft.add(R.id.container, mapFragment);
        ft.commit();
    }

    // Le pasamos datos a HomeFragment y hacemos que se ejecute el primero
    private void abrirHomeFragment() {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        //Toast.makeText(getApplicationContext(), nombre, Toast.LENGTH_LONG).show();

        // Inicializamos un nuevo fragment
        HomeFragment homeFragment = new HomeFragment();

        // Guardamos el nombre en CLAVE_NOMBRE para mandarlos al fragment
        Bundle bundleNombre = new Bundle();
        bundleNombre.putString(CLAVE_NOMBRE, nombre);

        // Pasamos el nombre recojida con anterioridad a HomeFragment
        homeFragment.setArguments(bundleNombre);

        // Abrimos el homeFragment
        ft.add(R.id.container, homeFragment);
        ft.commit();
    }

    // Le pasamos datos a ProfileFragment y hacemos que se ejecute
    private void abrirProfileFragment() {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        // Inicializamos un nuevo fragment
        ProfileFragment profileFragment = new ProfileFragment();

        // Guardamos el nombre en CLAVE_NOMBRE para mandarlos al fragment
        Bundle bundle = new Bundle();
        bundle.putString(CLAVE_NOMBRE, nombre);
        bundle.putString(CLAVE_EMAIL, email);

        // Pasamos el nombre recojida con anterioridad a HomeFragment
        profileFragment.setArguments(bundle);

        // Abrimos el homeFragment
        ft.add(R.id.container, profileFragment);
        ft.commit();
    }

    // Recogemos la información del usuario logado de la base de datos
    private void getInfoUser() {
        dr.child("Usuarios").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nombre = dataSnapshot.child("nombre").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
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
        if (item.getItemId() == R.id.itmCerrarSesion) {
            fa.signOut();
            Intent i = new Intent(UsuarioActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // Con este método verificamos si tenemos los permisos de la localización
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION:
                // Si los permisos de localización son aceptados entonces el programas sigue
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Obtenemos la ubicación actual
                    obtenerUbicacionActual();

                } else {
                    // Permission Denied
                    Toast.makeText(UsuarioActivity.this, "No se aceptó permisos", Toast.LENGTH_SHORT).show();
                    pedirPermisos();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Pedimos los permisos al usuario para acceder a su ubicación, si no los tenemos todavía
    private void pedirPermisos() {
        if (ContextCompat.checkSelfPermission(UsuarioActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(UsuarioActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(UsuarioActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
        } else if (ContextCompat.checkSelfPermission(UsuarioActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Obtenemos la ubicación actual
            obtenerUbicacionActual();

        }
    }

    // Obtenemos nuestra ubicacion actual
    private void obtenerUbicacionActual() {
        flClient = LocationServices.getFusedLocationProviderClient(UsuarioActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        flClient.getLastLocation().addOnSuccessListener(UsuarioActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.i("LOC", "onSuccess de localitation");
                if (location != null) {
                    miLoc = location;
                }
            }
        });
    }

    private void onNavigationItemSelected(MenuItem item) {
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
