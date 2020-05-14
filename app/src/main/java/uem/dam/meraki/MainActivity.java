package uem.dam.meraki;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tvRegistrarse;
    EditText etPassword;
    EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        tvRegistrarse = findViewById(R.id.tvRegistrarse);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);

        // Hacemos que el textView sea clickable
        tvRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RolActivity.class);
                startActivity(i);
            }
        });

    }

    public void Iniciar(View view) {
        Intent i = new Intent(MainActivity.this, UsuarioActivity.class);
        startActivity(i);
    }
}
