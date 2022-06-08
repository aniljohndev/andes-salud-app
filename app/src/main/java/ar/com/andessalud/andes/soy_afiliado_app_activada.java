package ar.com.andessalud.andes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class soy_afiliado_app_activada extends AppCompatActivity {

    Activity actActual=this;
    LinearLayout lnConfimar;
    TextView lblTextoActivacion;
    String txtMensaje;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actActual.overridePendingTransition(R.anim.entrar_derecha, R.anim.salir_izquierda);
        setContentView(R.layout.soy_afiliado_app_activada);

        mostrarMenuRedesSociales();

        if (soy_afiliado_sin_dni_activacion.activityEsperandoActivacion!=null){
            soy_afiliado_sin_dni_activacion.activityEsperandoActivacion.finish();
        }

        SharedPreferences prefs = getSharedPreferences("datosAplicacion", MODE_PRIVATE);
        txtMensaje = prefs.getString("textoPantallaActivacion", "");

        lblTextoActivacion=(TextView) findViewById(R.id.lblTextoActivacion);
        lblTextoActivacion.setText(txtMensaje);

        lnConfimar=(LinearLayout) findViewById(R.id.lnConfimar);
        lnConfimar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLController database= new SQLController(actActual);
                database.actualizarEstadoRegistro("ESPUSUYPASSNUEVOS");

                Intent intent = new Intent(getBaseContext(), soy_afiliado_identidad_valida.class);
                startActivity(intent);
                finishAffinity();
            }
        });

    }

    private void mostrarMenuRedesSociales(){
        Bundle args = new Bundle();
        args.putString("muestraContactCenter", "NO");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment_menu_sociales contentFragment = new fragment_menu_sociales();
        contentFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .add(R.id.frg_sociales, contentFragment)
                .commit();
    }

    //region otros
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        actActual.overridePendingTransition(R.anim.entrar_izquierda, R.anim.salir_derecha);
    }
    //endregion
}
