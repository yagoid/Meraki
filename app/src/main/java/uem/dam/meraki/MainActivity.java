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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


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
        // Validamos si los datos introducidos son correctos, y si es así se inicia sesión en con su respectivo rol
        String msj = validarDatos();

        if (msj != null) {
            Toast.makeText(this, msj, Toast.LENGTH_LONG).show();
        } else {
            fa.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Recojemos el id del usuario logado
                        String id = fa.getCurrentUser().getUid();

                        Query queryU = dr.child("Usuarios").child(id);

                        queryU.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Si los datos introducidos son de usuario, se le lleva a la pantalla UsuarioActivity
                                if (dataSnapshot.exists()) {
                                    Intent i = new Intent(MainActivity.this, UsuarioActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    // Si por el contrario, los datos introducidos son de cliente, se le lleva a la pantalla ClienteActivity
                                    Intent iT = new Intent(MainActivity.this, TiendaActivity.class);
                                    startActivity(iT);
                                    finish();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.msj_error_iniciar_sesion), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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

            String id = fa.getCurrentUser().getUid();
            Query queryU = dr.child("Usuarios").child(id);

            queryU.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Intent i = new Intent(MainActivity.this, UsuarioActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent iT = new Intent(MainActivity.this, TiendaActivity.class);
                        startActivity(iT);
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}
