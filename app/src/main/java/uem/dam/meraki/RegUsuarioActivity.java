package uem.dam.meraki;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import uem.dam.meraki.model.Tienda;
import uem.dam.meraki.model.Usuario;

public class RegUsuarioActivity extends AppCompatActivity {

    private FirebaseAuth fa;
    private FirebaseUser fu;
    private DatabaseReference dr;

    ImageView ivFondo;
    TextView tvCancelar;

    EditText etNombre;
    EditText etEmail;
    EditText etPassword;
    EditText etRepPassword;

    String nombre;
    String email;
    String pass;
    String rPass;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_usuario);
        getSupportActionBar().hide();

        ivFondo = findViewById(R.id.ivFondo);
        tvCancelar = findViewById(R.id.tvCancelar);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmailU);
        etPassword = findViewById(R.id.etPasswordU);
        etRepPassword = findViewById(R.id.etRepPasswordU);

        // Inicializamos la firebase
        InicializarFirebase();

        if(fu !=null){
            etEmail.setText(fu.getEmail());
        }

        // Hacemos posible que los bordes de la imagen sean redondos
        ivFondo.setClipToOutline(true);

        // Hacemos que el textView sea clickable
        tvCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegUsuarioActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

    }

    private void InicializarFirebase() {
        // Inicializamos la firebase Auth
        fa = FirebaseAuth.getInstance();
        fu = fa.getCurrentUser();

        if(fu !=null){
            etEmail.setText(fu.getEmail());
        }

        // Inicializamos la Firebase Database
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

                        Usuario u = new Usuario(id, nombre, email);

                        dr.child("Usuarios").child(id).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task2) {
                                if (task2.isSuccessful()) {
                                    Intent i = new Intent(RegUsuarioActivity.this, RolActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(RegUsuarioActivity.this, getString(R.string.error_insercion_datos), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        Toast.makeText(RegUsuarioActivity.this, getString(R.string.msj_registrado), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(RegUsuarioActivity.this, getString(R.string.msj_no_registrado), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private String validarDatos(){
        // Comprobamos que los datos introducidos no estén incorrectos o vacíos
        nombre = etNombre.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        pass = etPassword.getText().toString().trim();
        rPass = etRepPassword.getText().toString().trim();

        String msj = null;


        if(!validarEmail(email)){
            etEmail.setError("Email no válido");
        }

        if (nombre.isEmpty()) {
            etNombre.setError("Required");
            msj = getString(R.string.no_datos);

        } else if(email.isEmpty()){
            msj = getString(R.string.no_datos);
            etEmail.setError("Required");

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

}
