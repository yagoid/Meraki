package uem.dam.meraki.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import uem.dam.meraki.CatalogoActivity;
import uem.dam.meraki.R;
import uem.dam.meraki.UsuarioActivity;
import uem.dam.meraki.model.Tienda;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final String CLAVE_UID = "UID";

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    private TextView tvTiendaBuscadas;
    Marker m;

    private DatabaseReference dr;

    private String uid;
    private Double lat;
    private Double lon;
    private String tienda;
    private LatLng newTienda;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);

        tvTiendaBuscadas = mView.findViewById(R.id.tvTiendaBuscadas);

        // Inicializamos la Firebase Database
        dr = FirebaseDatabase.getInstance().getReference();

        // Recibimos las cordenadas de nuestra posición desde Usuario Activity
        if (getArguments() != null) {
            lat = getArguments().getDouble(UsuarioActivity.LAT_KEY);
            lon = getArguments().getDouble(UsuarioActivity.LON_KEY);

            // Recibimos los datos que se pulsaron en el Spinner de HomeFragment
            tienda = getArguments().getString(UsuarioActivity.CLAVE_TIENDA);
        }

        // Agragamos al textView las tiendas que se están buscando actualmente
        if (!tienda.equals("Todas")) {
            tvTiendaBuscadas.setText("Tiendas de " + tienda);
        } else {
            tvTiendaBuscadas.setText("Todas las tiendas");
        }

        return  mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Creamos el mapa
        mMapView = mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Configuramos todas las opciones de nuestro mapa
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        newTienda = null;

        if (tienda.equals("Todas")) {

            /* Buscamos en la base de datos TODAS las latitudes y longitudes de las tiendas para añadir un marcador en el mapa  */
            dr.child("Tiendas").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Tienda t = snapshot.getValue(Tienda.class);

                        uid = t.getUid();
                        String nombre = t.getNombre();
                        String tiendaElegida = t.getTienda();
                        Double latitud = t.getLatitud();
                        Double longitud = t.getLongitud();

                        newTienda = new LatLng(latitud, longitud);

                        // Añadimos la marca donde haya una tienda
                        m = mGoogleMap.addMarker(new MarkerOptions().position(newTienda).title(nombre)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_ic))
                                .snippet("Tienda de " + tiendaElegida));

                        m.setTag(uid);

                        // Si se pulsa en la información del marcador nos llevará a la pantalla de su catálogo
                        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                String id = marker.getTag().toString();

                                Intent i = new Intent(getActivity(), CatalogoActivity.class);
                                i.putExtra(CLAVE_UID, id);
                                startActivity(i);
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error en los datos", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            /* Buscamos en la base de datos las latitudes y longitudes de las tiendas filtradas por tipo de tienda para añadir un marcador en el mapa  */
            Query query = dr.child("Tiendas").orderByChild("tienda").equalTo(tienda);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Tienda t = snapshot.getValue(Tienda.class);

                        uid = t.getUid();
                        String nombre = t.getNombre();
                        String tiendaElegida = t.getTienda();
                        Double latitud = t.getLatitud();
                        Double longitud = t.getLongitud();

                        newTienda = new LatLng(latitud, longitud);

                        // Añadimos la marca donde haya una tienda
                        m = mGoogleMap.addMarker(new MarkerOptions().position(newTienda).title(nombre)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_ic))
                                .snippet("Tienda de " + tiendaElegida));

                        m.setTag(uid);

                        // Si se pulsa en la información del marcador nos llevará a la pantalla de su catálogo
                        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                String id = marker.getTag().toString();

                                Intent i = new Intent(getActivity(), CatalogoActivity.class);
                                i.putExtra(CLAVE_UID, id);
                                startActivity(i);
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error en los datos", Toast.LENGTH_LONG).show();
                }
            });
        }

        LatLng miLoc = null;

        if (lat != null) {
            // Movemos la cámara hacia nuestra ubicación
            miLoc = new LatLng(lat, lon);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miLoc, 14));
            //mGoogleMap.addMarker(new MarkerOptions().position(miLoc).title("MI UBICACION").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_logo)));
        }

        // Botones para que sea posible hacer zoom
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        // Círculo azul que nos indica nuestra posición
        LocationServices.getFusedLocationProviderClient(getContext()).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mGoogleMap.setMyLocationEnabled(true);
            }
        });

    }
}
