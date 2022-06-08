package ar.com.andessalud.andes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.MenuCloser;
import ar.com.andessalud.andes.fragmentos.fragmento_cartilla;
import ar.com.andessalud.andes.fragmentos.fragmento_cartilla_medica;
import ar.com.andessalud.andes.fragmentos.fragmento_credencial_provisoria;
import ar.com.andessalud.andes.fragmentos.fragmento_credencial_virtual;
import ar.com.andessalud.andes.fragmentos.fragmento_credenciales_nueva;
import ar.com.andessalud.andes.fragmentos.fragmento_emergencias;
import ar.com.andessalud.andes.fragmentos.fragmento_emergencias_ambulancia;
import ar.com.andessalud.andes.fragmentos.fragmento_emergencias_guardias;
import ar.com.andessalud.andes.fragmentos.fragmento_estudios_medicos;
import ar.com.andessalud.andes.fragmentos.fragmento_ordenes;
import ar.com.andessalud.andes.fragmentos.fragmento_preguntas_frecuentes;
import ar.com.andessalud.andes.fragmentos.fragmento_prestador_orden;
import ar.com.andessalud.andes.fragmentos.fragmento_prestador_reportes;
import ar.com.andessalud.andes.fragmentos.fragmento_principal;
import ar.com.andessalud.andes.fragmentos.fragmento_principal_prestador;
import ar.com.andessalud.andes.fragmentos.fragmento_reclamos_sugerencia;
import ar.com.andessalud.andes.fragmentos.fragmento_rpt_pres_odontologicas;
import ar.com.andessalud.andes.fragmentos.fragmento_sedes;
import ar.com.andessalud.andes.fragmentos.fragmento_tramites;
import ar.com.andessalud.andes.fragmentos.fragmento_tramites_documentacion;
import ar.com.andessalud.andes.fragmentos.fragmento_tramites_formularios_especiales;
import ar.com.andessalud.andes.fragmentos.fragmento_tramites_incorporar_familiar;
import ar.com.andessalud.andes.fragmentos.fragmento_tramites_internacion;
import ar.com.andessalud.andes.fragmentos.fragmento_tramites_reintegros;
import ar.com.andessalud.andes.fragmentos.fragmento_turnos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;

public class principal_prestador extends AppCompatActivity implements MenuCloser, FragmentChangeTrigger {
    Activity actActual=this;
    private Dialog dialog;
    private static final int TIME_INTERVAL = 2000;
    private static final int TIME_INTERVAL_DESCARTAR = 300;
    private long mBackPressed;
    private DrawerLayout mDrawerLayout;
    NavigationView mDrawerPane;
    public static String pantActual="";
    FragmentManager fragmentManager;
    fragmento_principal_prestador pantInicial;
    fragmento_prestador_orden pantOrdenPrestador;
    fragmento_prestador_reportes pantReportesPrestador;
    fragmento_rpt_pres_odontologicas pantReportesPrestOdontologicas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actActual.overridePendingTransition(R.anim.entrar_derecha, R.anim.salir_izquierda);
        setContentView(R.layout.principal_prestador);

        fragmentManager = getSupportFragmentManager();
        armaSlideMenu();

        pantInicial = new fragmento_principal_prestador();
        pantInicial.setTrigger(this);
        pantActual="inicio";
        fragmentManager.beginTransaction()
                .add(R.id.frg_pantallaActual, pantInicial)
                .commit();
    }

    private void armaSlideMenu(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (mDrawerLayout != null) {
            mDrawerPane = (NavigationView) findViewById(R.id.drawerPane);
            mDrawerPane.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    return true;
                }
            });
        }

        LinearLayout btnMenu=(LinearLayout) findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.requestLayout();
                mDrawerLayout.openDrawer(mDrawerPane);
            }
        });

        fragmento_drawer_prestador mnuSlide = new fragmento_drawer_prestador();
        mnuSlide.setCloser(this);
        fragmentManager.beginTransaction()
                .add(R.id.drawerPane, mnuSlide)
                .commit();
        mnuSlide.setTrigger(this);
    }

    @Override
    public void fireChange(String i) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch(i) {
            case "principal_prestador_btnOrdenes":
                pantOrdenPrestador = new fragmento_prestador_orden();
                pantOrdenPrestador.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantOrdenPrestador);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "ordenesPrestador";
                break;
            case "principal_prestador_btnInformes":
                pantReportesPrestador = new fragmento_prestador_reportes();
                pantReportesPrestador.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantReportesPrestador);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "reportesPrestador";
                break;
            case "principal_prestador_rpt_prestaciones_odontologicas":
                pantReportesPrestOdontologicas = new fragmento_rpt_pres_odontologicas();
                pantReportesPrestOdontologicas.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantReportesPrestOdontologicas);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "reportesPrestOdontologicas";
                break;
            case "menu_lateral_btnCerrarSesion":
                datosPrestador.inicializaAreaPrestador();
                datosPrestador.guardarIdUsuario("");
                datosPrestador.guardarIdConvenio("");

                Intent intentRegistro = new Intent(getBaseContext(), inicio.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRegistro);
                finishAffinity();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCloseMenu() {
        mDrawerLayout.openDrawer(mDrawerPane);
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void fireChange(int i, Bundle bundle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch(i){
            case 2:
                String a="";
                break;
            case 3:
                String b="";
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL_DESCARTAR > System.currentTimeMillis()) {
            mBackPressed = System.currentTimeMillis();
            return;
        }else if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            finishAffinity();
            super.onBackPressed();
            return;
        }else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out);
            if (pantActual.equals("ordenesPrestador")
                    || pantActual.equals("reportesPrestador")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantOrdenPrestador=null;
                pantActual = "inicio";
                return;
            }else if (pantActual.equals("reportesPrestOdontologicas")) {
                transaction.replace(R.id.frg_pantallaActual, pantReportesPrestador);
                transaction.addToBackStack(null);
                transaction.commit();
                pantReportesPrestOdontologicas = null;
                pantActual = "reportesPrestador";
                return;
            }
        }
        mBackPressed = System.currentTimeMillis();
    }
}
