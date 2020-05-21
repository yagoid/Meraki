package uem.dam.meraki;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import uem.dam.meraki.model.Tienda;

public class RegTiendaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseAuth fa;
    private FirebaseUser fu;
    private DatabaseReference dr;
    //private FirebaseDatabase fd;

    ImageView ivFondo;
    Spinner spnTiendas;
    TextView tvCancelar;
    ImageView ivUbicacion;

    EditText etEmail;
    EditText etCIF;
    EditText etUbicacion;
    EditText etPassword;
    EditText etRepPassword;

    String email;
    String CIF;
    String ubicacion;
    String pass;
    String rPass;

    private String tienda;
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
        ivUbicacion = findViewById(R.id.ivUbicacion);

        etEmail = findViewById(R.id.etEmailT);
        etCIF = findViewById(R.id.etCIF);
        etUbicacion = findViewById(R.id.etUbicacion);
        etPassword = findViewById(R.id.etPasswordT);
        etRepPassword = findViewById(R.id.etRepPasswordT);

        // Inicializamos la firebase
        InicializarFirebase();

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

        tienda = spnTiendas.getSelectedItem().toString();

    }

    private void InicializarFirebase() {
        // Inicializamos la firebase Auth
        fa = FirebaseAuth.getInstance();
        fu = fa.getCurrentUser();

        if(fu !=null){
            etEmail.setText(fu.getEmail());
        }

        // Inicializamos la Firebase Database
        //FirebaseApp.initializeApp(this);
        dr = FirebaseDatabase.getInstance().getReference();
    }

    public void Registrar(View view) {
        // Registramos al usuario en la base de datos
        String msj = validarDatos();

        if (msj != null) {
            Toast.makeText(this, msj, Toast.LENGTH_LONG).show();
        } else {
            fa.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String id = fa.getCurrentUser().getUid();

                                Tienda t = new Tienda(id, email, CIF, tienda, latitud, longitud);

                                dr.child("Tiendas").child(id).setValue(t).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task2) {
                                        if (task2.isSuccessful()) {
                                            Intent i = new Intent(RegTiendaActivity.this, RolActivity.class);
                                            startActivity(i);
                                            finish();
                                        } else {
                                            Toast.makeText(RegTiendaActivity.this, getString(R.string.error_insercion_datos), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                Toast.makeText(RegTiendaActivity.this, getString(R.string.msj_registrado), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(RegTiendaActivity.this, getString(R.string.msj_no_registrado), Toast.LENGTH_SHORT).show();
                            }
                        }
            });
        }

    }

    private String validarDatos(){
        // Comprobamos que los datos introducidos no estén incorrectos o vacíos
        email = etEmail.getText().toString().trim();
        pass = etPassword.getText().toString().trim();
        CIF = etCIF.getText().toString().trim();
        rPass = etRepPassword.getText().toString().trim();
        ubicacion = etUbicacion.getText().toString().trim();

        String msj = null;

        if(!validarEmail(email)){
            etEmail.setError("Email no válido");
        }

        if (ubicacion.isEmpty()) {
            etUbicacion.setError("Required");
            msj = getString(R.string.no_datos);

        } else if(email.isEmpty()){
            msj = getString(R.string.no_datos);
            etEmail.setError("Required");

        } else if (CIF.isEmpty()) {
            msj = getString(R.string.no_datos);
            etCIF.setError("Required");

        } else if (pass.isEmpty()) {
            msj = getString(R.string.no_datos);
            etPassword.setError("Required");
        } else if (rPass.isEmpty()) {
            etRepPassword.setError("Required");
            msj = getString(R.string.no_datos);

        } else if (!pass.equals(rPass)){
            msj = getString(R.string.password_desigual);
        } else if (pass.length() <= 5){
            msj = getString(R.string.pass_pocos_caracteres);
        }

        return msj;
    }

    private boolean validarEmail(String email) {
        // Comprobamos que el email introducido es válido
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
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


    // Métodos para registrar qué opción del Spinner se ha pulsado
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        parent.getItemAtPosition(pos);

        tienda = spnTiendas.getSelectedItem().toString();
        // String item = parent.getItemAtPosition(pos).toString();
        //Toast.makeText(this, tienda , Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
