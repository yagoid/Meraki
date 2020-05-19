package uem.dam.meraki.fragments;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import uem.dam.meraki.R;
import uem.dam.meraki.UsuarioActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    
    private Double lat;
    private Double lon;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);

        // Recibimos las cordenadas de nuestra posición desde Usuario Activity
        if (getArguments() != null) {
            lat = getArguments().getDouble(UsuarioActivity.LAT_KEY);
            lon = getArguments().getDouble(UsuarioActivity.LON_KEY);
        }

        Toast.makeText(getContext(), "lat: " + lat + " lon: " + lon, Toast.LENGTH_LONG).show();

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
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        LatLng miLoc = null;

        if (lat == null) {
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(40.689247, -74.044502)).title("Estatua de la libertad").snippet("I hope to go there some day"));
            CameraPosition Liberty = CameraPosition.builder().target(new LatLng(40.689247, -74.044502)).zoom(14).bearing(0).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
        } else {
            // Movemos la cámara hacia nuestra ubicación
            miLoc = new LatLng(lat, lon);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miLoc, 14));
            //mGoogleMap.addMarker(new MarkerOptions().position(miLoc).title("MI UBICACION").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_logo)));
        }

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        LocationServices.getFusedLocationProviderClient(getContext()).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mGoogleMap.setMyLocationEnabled(true);
            }
        });

    }
}
