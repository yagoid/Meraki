package uem.dam.meraki;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RolActivity extends AppCompatActivity {

    ImageView ivTienda;
    ImageView ivUsuario;
    TextView tvTxtTienda;
    TextView tvTxtUsuario;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rol);
        getSupportActionBar().hide();

        ivTienda = findViewById(R.id.ivTienda);
        ivUsuario = findViewById(R.id.ivUsuario);

        // Hacemos posible que los bordes de la imagen sean redondos
        ivTienda.setClipToOutline(true);
        ivUsuario.setClipToOutline(true);

        tvTxtTienda = findViewById(R.id.tvTxtTienda);
        tvTxtUsuario = findViewById(R.id.tvTxtUsuario);

        // Hacemos que los textView sean clickable
        tvTxtTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RolActivity.this, RegTiendaActivity.class);
                startActivity(i);
            }
        });
        tvTxtUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RolActivity.this, RegUsuarioActivity.class);
                startActivity(i);
            }
        });

    }
}
