package ar.com.andessalud.andes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class soy_afiliado_sin_dni_rechazado extends AppCompatActivity {
    final Activity actActual=this;
    public static Activity activityEsperandoActivacion;
    TextView lblMensaje;
    static SharedPreferences appPref;
    LinearLayout lnSiguiente;
    String txtMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actActual.overridePendingTransition(R.anim.entrar_derecha, R.anim.salir_izquierda);
        setContentView(R.layout.soy_afiliado_sin_dni_rechazado);

        activityEsperandoActivacion = this;
        mostrarMenuRedesSociales();

        appPref = actActual.getSharedPreferences("datosAplicacion", MODE_PRIVATE);
        String codMotRechazo=appPref.getString("codMotRechazo", "");
        if (codMotRechazo.equals("DOCINVALIDO")){
            txtMensaje ="La imagen enviada no corresponde a un documento válido, te pedimos que realices " +
                    "la registración nuevamente adjuntando una imagen válida. Recordá que los ÚNICOS " +
                    "documentos que validan tu identidad son DNI, Pasaporte o Licencia de conducir (Nacional)";
        } else if (codMotRechazo.equals("DOCILEGIBLE")){
            txtMensaje ="La imagen enviada no es correctamente legible o lo suficientemente nítida, te " +
                    "pedimos que realices la registración nuevamente adjuntando una imagen clara y legible, sin " +
                    "cortar los bordes de tu documento. Recordá que los ÚNICOS " +
                    "documentos que validan tu identidad son DNI, Pasaporte o Licencia de conducir (Nacional)";
        }else if (codMotRechazo.equals("NOAFILIADO")){
            txtMensaje ="La información enviada no coincide con un afiliado registrado en nuesto sistema, la " +
                    "aplicación móvil solo es un servicio habilitado sólo para afiliados de Andes Salud. " +
                    "Si sos afiliado por favor ingresa nuevamente tus datos sin agregar espacios ni números " +
                    "en tu DNI para intentar validar nuevamente tu identidad";
        }else{
            txtMensaje="No se puedo realizar tu activación, por favor intenta nuevamente.";
        }

        lblMensaje = (TextView) findViewById(R.id.lblMensaje);
        lblMensaje.setText(txtMensaje);

        lnSiguiente = (LinearLayout) findViewById(R.id.lnSiguiente);
        lnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLController database= new SQLController(actActual);
                database.actualizarEstadoRegistro("INICIO");
                Intent intent = new Intent(getBaseContext(), inicio.class);
                startActivity(intent);
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
}
