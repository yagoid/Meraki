package uem.dam.meraki.popups;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import uem.dam.meraki.R;
import uem.dam.meraki.RegUsuarioActivity;

public class PopupInvitado extends AppCompatActivity {

    TextView tvCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_invitado);

        tvCancelar = findViewById(R.id.tvCancelar);

        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);

        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;

        getWindow().setLayout((int) (ancho * 0.85), (int) (alto * 0.5));

        // Hacemos que el textView sea clickable
        tvCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void RegUsuario(View view) {
        Intent i = new Intent(PopupInvitado.this, RegUsuarioActivity.class);
        startActivity(i);
    }
}
