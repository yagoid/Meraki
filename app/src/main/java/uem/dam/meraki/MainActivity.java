package uem.dam.meraki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fa;
    private FirebaseUser fu;
    private DatabaseReference dr;

    TextView tvRegistrarse;
    EditText etPassword;
    EditText etEmail;

    String email;
    String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        tvRegistrarse = findViewById(R.id.tvRegistrarse);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);

        // Inicializamos la firebase
        InicializarFirebase();

        // Hacemos que el textView sea clickable
        tvRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RolActivity.class);
                startActivity(i);
            }
        });

    }

    private void InicializarFirebase() {
        // Inicializamos la firebase Auth
        fa = FirebaseAuth.getInstance();
        fu = fa.getCurrentUser();

        // Si ya se ha rellenado el email con anterioridad, se rellenará solo el edit text
        if(fu !=null){
            etEmail.setText(fu.getEmail());
        }

        // Inicializamos la Firebase Database
        dr = FirebaseDatabase.getInstance().getReference();
    }

    public void Iniciar(View view) {

        String msj = validarDatos();

        if (msj != null) {
            Toast.makeText(this, msj, Toast.LENGTH_LONG).show();
        } else {
            fa.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent i = new Intent(MainActivity.this, UsuarioActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.msj_no_accede), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //Intent i = new Intent(MainActivity.this, UsuarioActivity.class);
        //startActivity(i);
    }

    // Comprobamos que los datos introducidos no estén vacíos
    private String validarDatos()  {
        email = etEmail.getText().toString().trim();
        pwd = etPassword.getText().toString().trim();

        String msj = null;
        if (email.isEmpty()) {
            etEmail.setError("Required");
            msj = getString(R.string.no_datos);
        } else if (pwd.isEmpty()) {
            etPassword.setError("Required");
            msj = getString(R.string.no_datos);
        }

        return msj;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Mantenemos abierta la sesión para no tener que rellenar los campos de login de nuevo
        if (fa.getCurrentUser() != null) {
            Intent i = new Intent(MainActivity.this, UsuarioActivity.class);
            startActivity(i);
            finish();
        }
    }

}
