package uem.dam.meraki;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RegUsuarioActivity extends AppCompatActivity {

    ImageView ivFondo;
    TextView tvCancelar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_usuario);
        getSupportActionBar().hide();

        ivFondo = findViewById(R.id.ivFondo);
        tvCancelar = findViewById(R.id.tvCancelar);

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

    public void Registrar(View view) {
    }
}
