package uem.dam.meraki;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class RegTiendaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView ivFondo;
    Spinner spnTiendas;
    TextView tvCancelar;
    EditText etUbicacion;
    ImageView ivUbicacion;

    private double latitud;
    private double longitud;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_tienda);
        getSupportActionBar().hide();

        ivFondo = findViewById(R.id.ivFondo);
        tvCancelar = findViewById(R.id.tvCancelar);
        etUbicacion = findViewById(R.id.etUbicacion);
        ivUbicacion = findViewById(R.id.ivUbicacion);

        // Recojemos la ubicación y la escribimos en su sitio
        ElegirUbicacion();

        // Hacemos posible que los bordes de la imagen sean redondos
        ivFondo.setClipToOutline(true);

        // Hacemos que el textView sea clickable
        tvCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegTiendaActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        // Hacemos que el imageView sea clickable
        ivUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegTiendaActivity.this, MapaActivity.class);
                startActivity(i);
            }
        });

        // Se configuran las opciones que hay en el Spinner
        spnTiendas = findViewById(R.id.spnTiendas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tiendas_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnTiendas.setAdapter(adapter);

        spnTiendas.setOnItemSelectedListener(this);
    }

    private void ElegirUbicacion() {
        // Recojemos la latitud y longitud elegida en MapaActivity
        latitud = getIntent().getDoubleExtra(MapaActivity.CLAVE_LAT, latitud);
        longitud = getIntent().getDoubleExtra(MapaActivity.CLAVE_LONG, longitud);

        if (latitud != 0) {
            // Escribimos la latitud y longitud en el EditText
            String lat = String.valueOf(latitud);
            String lon = String.valueOf(longitud);

            etUbicacion.setText(lat + ", " + lon);
        }
    }


    public void Registrar(View view) {
    }


    // Métodos para registrar qué opción del Spinner se ha pulsado
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        parent.getItemAtPosition(pos);
        // String item = parent.getItemAtPosition(pos).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
