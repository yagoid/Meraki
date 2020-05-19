package uem.dam.meraki;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String CLAVE_LAT = "LAT";
    public static final String CLAVE_LONG = "LON";

    private GoogleMap mMap;
    private int contCkick;
    Button btnElegir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        btnElegir = findViewById(R.id.btnElegir);

        // Referenciamos el maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        contCkick = 0;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {

                // Contador para que cuando se pulse 2 veces ene l mapa se quite la última marca
                contCkick++;
                if (contCkick == 1) {
                    mMap.clear();
                }
                // Añadimos un marcador donde pinchemos
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .title("Aquí se ubica mi tienda")
                        .snippet("Lat: " + latLng.latitude + ", Long: " + latLng.longitude)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_logo)));

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Cuando se pulse el botón se guardarán las coordenadas seleccionadas
                btnElegir.setEnabled(true);
                btnElegir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MapaActivity.this, RegTiendaActivity.class);
                        i.putExtra(CLAVE_LAT, latLng.latitude);
                        i.putExtra(CLAVE_LONG, latLng.longitude);
                        startActivity(i);
                    }
                });
                contCkick = 0;
            }
        });

        // Añadimos el icono redondo azul en la posición actual
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mMap.setMyLocationEnabled(true);
            }
        });

    }
}
