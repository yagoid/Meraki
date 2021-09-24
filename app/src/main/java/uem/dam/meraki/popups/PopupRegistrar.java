package uem.dam.meraki.popups;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uem.dam.meraki.MainActivity;
import uem.dam.meraki.R;
import uem.dam.meraki.RegTiendaActivity;
import uem.dam.meraki.RegUsuarioActivity;
import uem.dam.meraki.RolActivity;

public class PopupRegistrar extends AppCompatActivity {

    LinearLayout llRegistrar;
    TextView tvCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_registrar);

        llRegistrar = findViewById(R.id.llRegistrar);
        tvCancelar = findViewById(R.id.tvCancelar);

        int llAncho = llRegistrar.getWidth();
        int llAlto = llRegistrar.getHeight();

        DisplayMetrics medidasVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVentana);

        int ancho = medidasVentana.widthPixels;
        int alto = medidasVentana.heightPixels;

        getWindow().setLayout((int) (ancho * 0.85), (int) (alto * 0.7));
        //getWindow().setLayout(llAncho, llAlto);

        // Hacemos que el textView sea clickable
        tvCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void RegTienda(View view) {
        Intent i = new Intent(PopupRegistrar.this, RegTiendaActivity.class);
        startActivity(i);
    }

    public void RegUsuario(View view) {
        Intent i = new Intent(PopupRegistrar.this, RegUsuarioActivity.class);
        startActivity(i);
    }
}
