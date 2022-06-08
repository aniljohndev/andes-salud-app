package ar.com.andessalud.andes;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.twilio.twiml.voice.Dial;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.Visibility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ar.com.andessalud.andes.Interfaces.DialogButtonListener;
import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.MenuCloser;
//import ar.com.andessalud.andes.fabricas.fabrica_menu;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.fragmentos.fragmento_actualizacion_datos;
import ar.com.andessalud.andes.fragmentos.fragmento_calenderio;
import ar.com.andessalud.andes.fragmentos.fragmento_cartilla;
import ar.com.andessalud.andes.fragmentos.fragmento_cartilla_farmacia;
import ar.com.andessalud.andes.fragmentos.fragmento_cartilla_medica;
import ar.com.andessalud.andes.fragmentos.fragmento_cartilla_secundarias;
import ar.com.andessalud.andes.fragmentos.fragmento_contact_center;
import ar.com.andessalud.andes.fragmentos.fragmento_contact_turno;
import ar.com.andessalud.andes.fragmentos.fragmento_credencial_provisoria;
import ar.com.andessalud.andes.fragmentos.fragmento_credencial_virtual;
import ar.com.andessalud.andes.fragmentos.fragmento_credenciales;
import ar.com.andessalud.andes.fragmentos.fragmento_credenciales_nueva;
import ar.com.andessalud.andes.fragmentos.fragmento_dr_online;
import ar.com.andessalud.andes.fragmentos.fragmento_dr_online_llamada;
import ar.com.andessalud.andes.fragmentos.fragmento_dr_online_sala_espera;
import ar.com.andessalud.andes.fragmentos.fragmento_emergencias;
import ar.com.andessalud.andes.fragmentos.fragmento_emergencias_ambulancia;
import ar.com.andessalud.andes.fragmentos.fragmento_emergencias_guardias;
import ar.com.andessalud.andes.fragmentos.fragmento_estado_afiliacion;
import ar.com.andessalud.andes.fragmentos.fragmento_estudios_medicos;
import ar.com.andessalud.andes.fragmentos.fragmento_eventos;
import ar.com.andessalud.andes.fragmentos.fragmento_llamada;
import ar.com.andessalud.andes.fragmentos.fragmento_notificaciones;
import ar.com.andessalud.andes.fragmentos.fragmento_orden_con_prestador;
import ar.com.andessalud.andes.fragmentos.fragmento_ordenes;
import ar.com.andessalud.andes.fragmentos.fragmento_ordenes_estudios;
import ar.com.andessalud.andes.fragmentos.fragmento_pharmacy;
import ar.com.andessalud.andes.fragmentos.fragmento_preguntas_frecuentes;
import ar.com.andessalud.andes.fragmentos.fragmento_principal;
import ar.com.andessalud.andes.fragmentos.fragmento_principle_calenderio;
import ar.com.andessalud.andes.fragmentos.fragmento_reclamos_sugerencia;
import ar.com.andessalud.andes.fragmentos.fragmento_sedes;
import ar.com.andessalud.andes.fragmentos.fragmento_tramites;
import ar.com.andessalud.andes.fragmentos.fragmento_tramites_documentacion;
import ar.com.andessalud.andes.fragmentos.fragmento_tramites_formularios_especiales;
import ar.com.andessalud.andes.fragmentos.fragmento_tramites_incorporar_familiar;
import ar.com.andessalud.andes.fragmentos.fragmento_tramites_internacion;
import ar.com.andessalud.andes.fragmentos.fragmento_tramites_reintegros;
import ar.com.andessalud.andes.fragmentos.fragmento_turnos;
import ar.com.andessalud.andes.fragmentos.fragmento_turnos_prestador;
import ar.com.andessalud.andes.twilio.MySingleton;
import ar.com.andessalud.andes.twilio.VideoActivity;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

import static ar.com.andessalud.andes.splash.myCustomKey;
import static ar.com.andessalud.andes.splash.myCustomKeyfordoctor;

public class principal extends AppCompatActivity implements MenuCloser, FragmentChangeTrigger
//        , DialogButtonListener
{
    Calendar calendar;
    Activity actActual = this;
    private Dialog dialog;
    private DrawerLayout mDrawerLayout;
    NavigationView mDrawerPane;
    private static final int TIME_INTERVAL = 2000;
    private static final int TIME_INTERVAL_DESCARTAR = 300;
    private long mBackPressed;
    ImageView btnNotificaciones, btnCalenderio;
    LinearLayout lnLlamadaActiva;
    public static String[] arrasend = new String[2];
    public static String[] arraConsultado = new String[6];
    SharedPreferences appPref;

    FragmentManager fragmentManager;
    public static String pantActual = "";
    fragmento_principal pantInicial;
    fragmento_ordenes pantOrdenes;
    fragmento_notificaciones pantNotificaciones;
    fragmento_principle_calenderio calenderio_frgmnt;
    fragmento_eventos eventos_frgmnt;
    fragmento_emergencias pantEmergencias;
    fragmento_emergencias_guardias pantEmergenciasGuardias;
    fragmento_emergencias_ambulancia pantEmergenciasAmbulancia;
    fragmento_estudios_medicos pantEstudiosMedicos;
    fragmento_credenciales pantCredenciales;
    fragmento_credenciales_nueva pantCredencialesNuevas;
    fragmento_credencial_provisoria pantCredencialProvisoria;
    fragmento_credencial_virtual pantCredencialVirtual;
    fragmento_tramites pantTramites;
    fragmento_tramites_internacion pantTramitesInternacion;
    fragmento_tramites_formularios_especiales pantTramitesFormulariosEspeciales;
    fragmento_tramites_documentacion pantTramitesDocumentacion;
    fragmento_tramites_reintegros pantTramitesReintegros;
    fragmento_tramites_incorporar_familiar pantTramitesIncorporarFamiliar;
    fragmento_cartilla pantCartillas;
    fragmento_cartilla_medica pantCartillasMedicas;
    fragmento_cartilla_farmacia pantCartillasFarmacia;
    fragmento_cartilla_secundarias pantCartillasSecundarias;
    fragmento_sedes pantSedes;
    fragment_menu_sociales pantMenuSociales;
    fragmento_contact_center pantContactCenter = new fragmento_contact_center();
    fragmento_llamada pantLlamada = new fragmento_llamada();
    fragmento_turnos pantTurno;
    fragmento_reclamos_sugerencia pantReclamoSugerencia;
    fragmento_preguntas_frecuentes pantPreguntaFrecuente;
    fragmento_turnos_prestador pantTurnosPrestador;
    fragmento_orden_con_prestador pantOrdenPrestador;
    fragmento_actualizacion_datos pantActDatos;
    fragmento_estado_afiliacion pantEstadoAfiliacion;
    fragmento_dr_online pantDrOnline;
    fragmento_dr_online_sala_espera pantDrOnlineSalaEspera;
    fragmento_ordenes_estudios pantOrdenesEstudios;
    fragmento_dr_online_llamada pantDrOnlineLlamada;
    fragmento_pharmacy pantPharmacy;
    JSONObject updateJsonObject;
    public String idLlamadaActual, nombrePersonaAtiende = "", idAPPKey = "", idSubKey = "", idPubKey = "", idUsuarioRecibe = "", idUsuarioLlama = "", tipoLlamadaActual = "", idSignalRRecibe = "", idConvenioTurnos = "", nombreConvenioTurnos = "", idDrOnlineSalaEspera = "", idDrOnlineSignalRPrestador = "", estaEnSalaEspera = "NO", estaEnDrOnline = "NO", drOnlineEnviaVideo = "SI";
    public String[] idDrOnlineSignalR;
    SharedPreferences appPrefs;
    Boolean isEventSet,isDoctorEvent = false;
    public JSONObject sendingJson = new JSONObject();
    ;
    public String idAfiliadoRegistro;
    public Long cuilValue;
    ExecutorService executorService;
    public static String access_token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
        actActual.overridePendingTransition(R.anim.entrar_derecha, R.anim.salir_izquierda);
        appPref = this.getSharedPreferences("datosAplicacion", MODE_PRIVATE);
        dialog = new Dialog(this);
        calendar = Calendar.getInstance();
        updateJsonObject = new JSONObject();
        appPref = this.getSharedPreferences("datosAplicacion", MODE_PRIVATE);

        executorService = Executors.newSingleThreadExecutor();
        try {
            updateJsonObject.put("idAfiliado", appPref.getString("idAfiliadoRegistro", ""));
            updateJsonObject.put("token", appPref.getString("senderid", ""));

            updateCustomero(updateJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        idAfiliadoRegistro = appPref.getString("idAfiliadoRegistro", "");
        try {
            sendingJson.put("device_token", appPref.getString("senderid", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executorService.execute(() -> {
            Log.d("checkingvalues", idAfiliadoRegistro);
            CollectData(idAfiliadoRegistro);
            Log.d("valueofTotalJson", "Running in Background");
//                getCustomers();
            runOnUiThread(() ->

                    executorService.execute(() -> {
                        getCustomers();
                        runOnUiThread(() ->
                                Log.d("dadsdasd", String.valueOf(sendingJson)));

                    }));

        });

        executorService.execute(() -> {
            whatsAppRequiredData();

            runOnUiThread(() ->
                    Log.d("stats", "sended"));

        });
/*
        executorService.execute(()->
                {
                    leerGrupoFamiliar();
                    runOnUiThread(() ->
                            Log.d("stats", "sended"));
                }
                );
*/
        setContentView(R.layout.principal);
        Log.d("tokenidd", String.valueOf(updateJsonObject));
        // Handlo background notification Calling.
        Bundle bundle = getIntent().getExtras();
        Bundle dataNotification = new Bundle();
        if (bundle != null) {
            String SearchType = bundle.getString("room_name");
            Log.d("datafor", "Notification DATA On App BackGroud" + SearchType);
        }

        fragmentManager = getSupportFragmentManager();
        mostrarMenuRedesSociales();
        armaSlideMenu();

        btnNotificaciones = (ImageView) findViewById(R.id.btnNotificaciones);
        btnCalenderio = (ImageView) findViewById(R.id.btnCalender);
//        btnCalenderio.setVisibility(View.GONE);
        btnNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muestraNotificaciones();
            }
        });

        btnCalenderio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCalenderNotifies();
            }
        });
        // Background notification redirect here and from here to the connected screen.

        if (myCustomKey.contains("meeting")) {
            myCustomKeyfordoctor = "";
            Intent intent = new Intent(this, VideoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if (myCustomKeyfordoctor.contains("meeting")) {
            myCustomKey = "";
            Intent intent = new Intent(this, VideoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        lnLlamadaActiva = (LinearLayout) findViewById(R.id.lnLlamadaActiva);
        lnLlamadaActiva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muestraPantallaLamada();
            }
        });

        pantInicial = new fragmento_principal();
        pantInicial.setTrigger(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String pantalla;
            pantalla = extras.getString("pantalla", "");
            if (pantalla.equals("buzon")) {
                muestraNotificaciones();
                return;
            }
        }

        pantActual = "inicio";
        fragmentManager.beginTransaction()
                .add(R.id.frg_pantallaActual, pantInicial)
                .commit();

        SQLController database = new SQLController(actActual);
        Cursor cursor = database.cuentaAvisos();

        if (cursor.getCount() > 0) {
            String cuenta = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
            if (!cuenta.equals("0")) {
                btnNotificaciones.setImageResource(R.drawable.notificacion_red);
            }
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("das") && !(extras.getString("das") == null)) {
//                Log.d("checkingdatafrom",extras.getString("das"));
//                viewEventoDetaillos(extras.getString("das"));
                isEventSet = true;
            }
            else if (extras.containsKey("medicalprescription") && !(extras.getString("medicalprescription") == null)) {
                isDoctorEvent = true;
            }

        }
    }



















    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (!(extras.getString("medicalprescription") == null))

            {

                if(isDoctorEvent.equals(true))
                {
                    moveToNotification();

                }
            }

            else if (!(extras.getString("das") == null))

            {

                if(isEventSet.equals(true))
                {
                    viewEventoDetaillos(extras.getString("das"));

                }
            }
         /*   else if (!(extras.getString("join_appointmentcall") == null)){
             if (isDoctorEvent.equals(true))
            {

                        try {
//                        dialogo.dismiss();
                            Intent intentforsend = new Intent(this, VideoActivity.class);
                            intentforsend.putExtra("joinappointment",extras.getString("join_appointmentcall"));
                            startActivity(intentforsend);
                            actActual.overridePendingTransition(R.anim.entrar_arriba, R.anim.entrar_de_abajo);

                        }
                        catch (Exception ignored)
                        {

                        }

            }
             }*/
        }
    }

    private void mostrarMenuRedesSociales() {
        pantMenuSociales = new fragment_menu_sociales();
        pantMenuSociales.setTrigger(this);
        fragmentManager.beginTransaction()
                .add(R.id.frg_sociales, pantMenuSociales)
                .commit();
    }

    private void armaSlideMenu() {
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

        LinearLayout btnMenu = (LinearLayout) findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.requestLayout();
                mDrawerLayout.openDrawer(mDrawerPane);
            }
        });

        fragmento_drawer mnuSlide = new fragmento_drawer();
        mnuSlide.setCloser(this);
        fragmentManager.beginTransaction()
                .add(R.id.drawerPane, mnuSlide)
                .commit();
        mnuSlide.setTrigger(this);
    }

    private void muestraNotificaciones() {
        if (pantActual.equals("notificaciones")) {
            return;
        }
        pantNotificaciones = new fragmento_notificaciones();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.entrar_derecha, R.anim.salir_izquierda, R.anim.entrar_izquierda, R.anim.salir_derecha);
        transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
        transaction.replace(R.id.frg_pantallaActual, pantNotificaciones);
        transaction.addToBackStack("nose");
        transaction.commit();
        pantActual = "notificaciones";
        btnNotificaciones.setImageResource(R.drawable.notificacion_transparent);
    }

    private void viewCalenderNotifies() {
        if (pantActual.equals("calenderio")) {
            return;
        }
        calenderio_frgmnt = new fragmento_principle_calenderio();
        calenderio_frgmnt = new fragmento_principle_calenderio();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.entrar_derecha, R.anim.salir_izquierda, R.anim.entrar_izquierda, R.anim.salir_derecha);
        transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
        transaction.replace(R.id.frg_pantallaActual, calenderio_frgmnt);
        transaction.addToBackStack("nose");
        transaction.commit();
        pantActual = "calenderio";
        btnNotificaciones.setImageResource(R.drawable.notificacion_transparent);

    }

    private void viewEventoDetaillos(String eventId) {
        if (pantActual.equals("eventosdetails")) {
            return;
        }

        eventos_frgmnt = new fragmento_eventos();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        eventId = eventId.substring(6);
        bundle.putString("event_id", eventId);
        //transaction.setCustomAnimations(R.anim.entrar_derecha, R.anim.salir_izquierda, R.anim.entrar_izquierda, R.anim.salir_derecha);
//        transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
        transaction.replace(R.id.frg_pantallaActual, eventos_frgmnt);
        transaction.addToBackStack(null);
        transaction.commit();
        eventos_frgmnt.setArguments(bundle);
        pantActual = "eventosdetails";
//        getSupportFragmentManager().beginTransaction().add(eventos_frgmnt,"fragmento_eventos").addToBackStack(null).commit();
//        Fragment fragment = eventos_frgmnt;
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frg_pantallaActual, fragment).commit();

//        pantActual = "eventosdetails";
//        btnNotificaciones.setImageResource(R.drawable.notificacion_transparent);

    }
    private void moveToNotification( ) {
        if (pantActual.equals("medicalPrescription")) {
            return;
        }

        pantNotificaciones = new fragmento_notificaciones();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //transaction.setCustomAnimations(R.anim.entrar_derecha, R.anim.salir_izquierda, R.anim.entrar_izquierda, R.anim.salir_derecha);
//        transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
        transaction.replace(R.id.frg_pantallaActual, pantNotificaciones);
        transaction.addToBackStack(null);
        transaction.commit();
        pantActual = "eventosdetails";
//        getSupportFragmentManager().beginTransaction().add(eventos_frgmnt,"fragmento_eventos").addToBackStack(null).commit();
//        Fragment fragment = eventos_frgmnt;
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frg_pantallaActual, fragment).commit();

//        pantActual = "eventosdetails";
//        btnNotificaciones.setImageResource(R.drawable.notificacion_transparent);

    }


    public void muestraPantallaLamada() {
        if (estaEnSalaEspera.equals("SI")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
            transaction.replace(R.id.frg_pantallaActual, pantDrOnlineSalaEspera);
            transaction.addToBackStack("nose");
            transaction.commit();
            pantActual = "pantDrOnlineSalaEspera";
        } else if (pantDrOnlineLlamada != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
            transaction.replace(R.id.frg_pantallaActual, pantDrOnlineLlamada);
            transaction.addToBackStack("nose");
            transaction.commit();
            pantActual = "pantDrOnlineLlamada";
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
            transaction.replace(R.id.frg_pantallaActual, pantLlamada);
            transaction.addToBackStack("nose");
            transaction.commit();
            pantActual = "contactCenterLlamada";
        }
    }

    public void cambiaHayLlamadaActiva(Boolean estadoLlamada) {
        if (estadoLlamada
                && !pantActual.equals("contactCenterLlamada")) {
            lnLlamadaActiva.setVisibility(View.VISIBLE);
        } else {
            lnLlamadaActiva.setVisibility(View.GONE);
        }
    }

    public void cambiaHayLlamadaActivaDrOnline(Boolean estadoLlamada) {
        if (estadoLlamada
                && !pantActual.equals("pantDrOnlineSalaEspera")) {
            lnLlamadaActiva.setVisibility(View.VISIBLE);
        } else {
            lnLlamadaActiva.setVisibility(View.GONE);
        }
    }

    public void cambiaHaySalaEsperaActivaDrOnline(Boolean estadoLlamada) {
        if (estadoLlamada
                && !pantActual.equals("pantDrOnlineSalaEspera")) {
            lnLlamadaActiva.setVisibility(View.VISIBLE);
        } else {
            lnLlamadaActiva.setVisibility(View.GONE);
        }
    }

    public boolean estadoLlamadaActiva() {
        if (lnLlamadaActiva.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    @Override
    public void onCloseMenu() {
        //mDrawerLayout.openDrawer(mDrawerPane);
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void fireChange(String i) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (i) {

            case "principle_btnpharmacyChange":
                pantPharmacy = new fragmento_pharmacy();
                pantPharmacy.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantPharmacy);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "pharmacyEstudios";
                break;



            case "principal_btnOrdenesEstudios":
                pantOrdenesEstudios = new fragmento_ordenes_estudios();
                pantOrdenesEstudios.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantOrdenesEstudios);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "ordenesEstudios";
                break;
            case "principal_btnOrdenes":
                pantOrdenes = new fragmento_ordenes();
                pantOrdenes.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantOrdenes);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "ordenes";
                break;
            case "ordenes_btnTermino":
            case "estudios_medicos_btnTermino":
            case "credencial_credencial_nueva_btnTermino":
            case "credencial_credencial_provisoria_btnTermino":
            case "tramites_intarnacion_btnTermino":
            case "tramites_documentacion_btnTermino":
            case "tramites_reintegros_btnTermino":
            case "tramites_incorporar_familiar_btnTermino":
            case "credencial_credencial_virtual_btnTermino":
            case "llamada_finalizada":
            case "actDatosContacto_btnTermino":
            case "drOnline_cancelaSalaEspera":
            case "llamada_dr_online_finalizada":
                /*lnLlamadaActiva.setVisibility(View.GONE);
                if (!pantActual.equals("contactCenterLlamada")){
                    return;
                }*/
                pantInicial = new fragmento_principal();
                pantInicial.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out);
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantOrdenes = null;
                pantEstudiosMedicos = null;
                pantCredenciales = null;
                pantCredencialesNuevas = null;
                pantCredencialProvisoria = null;
                pantCredencialVirtual = null;
                pantTramites = null;
                pantTramitesInternacion = null;
                pantTramitesDocumentacion = null;
                pantTramitesReintegros = null;
                pantTramitesIncorporarFamiliar = null;
                pantActDatos = null;
                pantDrOnlineSalaEspera = null;
                pantDrOnlineLlamada = null;
                pantActual = "inicio";
                break;
            case "principal_btnEmergencias":
                pantEmergencias = new fragmento_emergencias();
                pantEmergencias.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantEmergencias);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "emergencias";
                break;
            case "emergencias_btnServicioGuardia":
                pantEmergenciasGuardias = new fragmento_emergencias_guardias();
                pantEmergenciasGuardias.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantEmergenciasGuardias);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "emergencias_guardias";
                break;
            case "emergencia_guardia_btnTermino":
                pantEmergencias = new fragmento_emergencias();
                pantEmergencias.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out);
                transaction.replace(R.id.frg_pantallaActual, pantEmergencias);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "emergencias";
                break;
            case "emergencias_btnAmbulancia":
                pantEmergenciasAmbulancia = new fragmento_emergencias_ambulancia();
                pantEmergenciasAmbulancia.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantEmergenciasAmbulancia);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "emergencias_ambulancia";
                break;
            case "principal_btnEstudiosMedicos":
                pantEstudiosMedicos = new fragmento_estudios_medicos();
                pantEstudiosMedicos.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantEstudiosMedicos);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "estudios_medicos";
                break;
            case "principal_btnCredencial":
                pantCredencialVirtual = new fragmento_credencial_virtual();
                pantCredencialVirtual.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantCredencialVirtual);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "credencial";
                break;
            case "credenciales_btnCredencialNueva":
                pantCredencialesNuevas = new fragmento_credenciales_nueva();
                pantCredencialesNuevas.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantCredencialesNuevas);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "credencial_nueva";
                break;
            case "credenciales_btnCredencialProvisoria":
                pantCredencialProvisoria = new fragmento_credencial_provisoria();
                pantCredencialProvisoria.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantCredencialProvisoria);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "credencial_provisoria";
                break;
            case "principal_btnTramites":
                pantTramites = new fragmento_tramites();
                pantTramites.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantTramites);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "tramites";
                break;
            case "tramites_btnInternacion":
                pantTramitesInternacion = new fragmento_tramites_internacion();
                pantTramitesInternacion.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantTramitesInternacion);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "tramites_internacion";
                break;
            case "tramites_btnFormularios":
                pantTramitesFormulariosEspeciales = new fragmento_tramites_formularios_especiales();
                pantTramitesFormulariosEspeciales.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantTramitesFormulariosEspeciales);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "tramites_formularios_especiales";
                break;
            case "tramites_btnDocumentacion":
                pantTramitesDocumentacion = new fragmento_tramites_documentacion();
                pantTramitesDocumentacion.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantTramitesDocumentacion);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "tramites_Documentacion";
                break;
            case "tramites_btnReintegros":
                pantTramitesReintegros = new fragmento_tramites_reintegros();
                pantTramitesReintegros.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantTramitesReintegros);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "tramites_Reintegros";
                break;
            case "tramites_btnIncorporarFam":
                pantTramitesIncorporarFamiliar = new fragmento_tramites_incorporar_familiar();
                pantTramitesIncorporarFamiliar.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantTramitesIncorporarFamiliar);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "tramites_Incorporar_Familiar";
                break;
            case "principal_btnTurnos":
                pantTurno = new fragmento_turnos();
                pantTurno.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantTurno);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "turnos";
                break;
            case "turno_btnTermino":
                pantInicial = new fragmento_principal();
                pantInicial.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out);
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantTurno = null;
                break;
            case "turnoPrestador_btnTermino":
                pantInicial = new fragmento_principal();
                pantInicial.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out);
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantTurnosPrestador = null;
                break;
            case "principal_btnCartilla":
                pantCartillas = new fragmento_cartilla();
                pantCartillas.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantCartillas);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "cartillas";
                break;
            case "principal_btnDrOnline":
                if (estaEnSalaEspera.equals("SI")) {
                    transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                    transaction.replace(R.id.frg_pantallaActual, pantDrOnlineSalaEspera);
                    transaction.addToBackStack("nose");
                    transaction.commit();
                    pantActual = "pantDrOnlineSalaEspera";
                    return;
                } else if (estaEnDrOnline.equals("SI")) {
                    transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                    transaction.replace(R.id.frg_pantallaActual, pantDrOnlineLlamada);
                    transaction.addToBackStack("nose");
                    transaction.commit();
                    pantActual = "pantDrOnlineLlamada";
                    return;
                }
                pantDrOnline = new fragmento_dr_online();
                pantDrOnline.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantDrOnline);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "drOnline";
                break;
            case "cartilla_btnCartillaMedica":
                pantCartillasMedicas = new fragmento_cartilla_medica();
                pantCartillasMedicas.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantCartillasMedicas);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "cartillas_medicas";
                break;
            case "cartilla_btnCartillaSecundaria":
                pantCartillasSecundarias = new fragmento_cartilla_secundarias();
                pantCartillasSecundarias.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantCartillasSecundarias);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "cartillas_medicas_secundarias";
                break;
            case "cartilla_btnCartillaFarmacia":
                pantCartillasFarmacia = new fragmento_cartilla_farmacia();
                pantCartillasFarmacia.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantCartillasFarmacia);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "cartillas_farmacias";
                break;
            case "menu_lateral_btnSedes":
                if (pantActual.equals("sedes")) {
                    return;
                }
                pantSedes = new fragmento_sedes();
                pantSedes.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantSedes);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "sedes";
                break;
            case "menu_lateral_btnCerrarSesion":
                SharedPreferences datosLocales = getSharedPreferences("datosAplicacion", MODE_PRIVATE);
                SharedPreferences.Editor editor = datosLocales.edit();
                editor.putString("idAfiliadoRegistro", "00000000-0000-0000-0000-000000000000");
                editor.commit();

                SQLController database = new SQLController(actActual);
                database.actualizarEstadoRegistro("INICIO");
                database.actualizarDatosIDAfiliado("00000000-0000-0000-0000-000000000000");

                Intent intentRegistro = new Intent(getBaseContext(), inicio.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRegistro);
                finishAffinity();
                break;
            case "contactCenter_btnAbrir":
                if (pantActual.equals("contactCenter")
                        || pantActual.equals("contactCenterLlamada")) {
                    return;
                }
                //pantContactCenter = new fragmento_contact_center();
                pantContactCenter.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantContactCenter);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "contactCenter";
                break;
            case "contactCenter_seAtendioLlamada":
                Bundle bundle = new Bundle();
                bundle.putString("idLlamada", idLlamadaActual);
                bundle.putString("tipoLlamada", tipoLlamadaActual);
                bundle.putString("signalRDestino", idSignalRRecibe);
                //pantLlamada = new fragmento_llamada();
                pantLlamada.setTrigger(this);
                pantLlamada.setArguments(bundle);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantLlamada);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "contactCenterLlamada";
                break;
            case "contactCenter_btnReclamoSugerencia":
                pantReclamoSugerencia = new fragmento_reclamos_sugerencia();
                pantReclamoSugerencia.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantReclamoSugerencia);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "contactCenterReclamoSugerencia";
                break;
            case "contactCenter_btnTerminoReclamoSugerencia":
                transaction.setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out);
                transaction.replace(R.id.frg_pantallaActual, pantContactCenter);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantReclamoSugerencia = null;
                pantActual = "contactCenter";
                break;
            case "contactCenter_btnPreguntasFrecuentes":
                pantPreguntaFrecuente = new fragmento_preguntas_frecuentes();
                pantPreguntaFrecuente.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantPreguntaFrecuente);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "contactCenterPreguntasFrecuentes";
                break;
            case "cartilla_medica_btnTurnoConPrestador":
                Bundle bundleTurnoConPrestador = new Bundle();
                bundleTurnoConPrestador.putString("idConvenio", idConvenioTurnos);
                bundleTurnoConPrestador.putString("nombrePrestador", nombreConvenioTurnos);
                pantTurnosPrestador = new fragmento_turnos_prestador();
                pantTurnosPrestador.setTrigger(this);
                pantTurnosPrestador.setArguments(bundleTurnoConPrestador);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantTurnosPrestador);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "turnosPrestador";
                break;
            case "cartilla_medica_btnOrdenConPrestador":
                Bundle bundleOrdenConPrestador = new Bundle();
                bundleOrdenConPrestador.putString("idConvenio", idConvenioTurnos);
                bundleOrdenConPrestador.putString("nombrePrestador", nombreConvenioTurnos);
                pantOrdenPrestador = new fragmento_orden_con_prestador();
                pantOrdenPrestador.setTrigger(this);
                pantOrdenPrestador.setArguments(bundleOrdenConPrestador);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantOrdenPrestador);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "ordenPrestador";
                break;
            case "menu_lateral_btnActDatos":
                if (pantActual.equals("pantActDatos")) {
                    return;
                }
                pantActDatos = new fragmento_actualizacion_datos();
                pantActDatos.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantActDatos);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "pantActDatos";
                break;
            case "menu_lateral_btnEstadoAfiliacion":
                if (pantActual.equals("pantEstadoAfiliacion")) {
                    return;
                }
                pantEstadoAfiliacion = new fragmento_estado_afiliacion();
                pantEstadoAfiliacion.setTrigger(this);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantEstadoAfiliacion);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "pantEstadoAfiliacion";
                break;
            case "drOnline_pasaSalaEspera":
                Bundle bundleSalaEsperaDrOnline = new Bundle();
                bundleSalaEsperaDrOnline.putString("idSalaEspera", idDrOnlineSalaEspera);
                bundleSalaEsperaDrOnline.putStringArray("idDrOnlineSignalR", idDrOnlineSignalR);
                if (estaEnSalaEspera.equals("NO")) {
                    pantDrOnlineSalaEspera = new fragmento_dr_online_sala_espera();
                    pantDrOnlineSalaEspera.setTrigger(this);
                    pantDrOnlineSalaEspera.setArguments(bundleSalaEsperaDrOnline);
                }
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantDrOnlineSalaEspera);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "pantDrOnlineSalaEspera";
                break;
            case "drOnline_atiendePaciente":
                Bundle bundleLlamadaDrOnline = new Bundle();
                bundleLlamadaDrOnline.putString("idLlamadaDrOnline", idDrOnlineSalaEspera);
                bundleLlamadaDrOnline.putString("idSubKey", idSubKey);
                bundleLlamadaDrOnline.putString("idDrOnlineSignalRPrestador", idDrOnlineSignalRPrestador);
                pantDrOnlineLlamada = new fragmento_dr_online_llamada();
                pantDrOnlineLlamada.setTrigger(this);
                pantDrOnlineLlamada.setArguments(bundleLlamadaDrOnline);
                transaction.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out);
                transaction.replace(R.id.frg_pantallaActual, pantDrOnlineLlamada);
                transaction.addToBackStack("nose");
                transaction.commit();
                pantActual = "pantDrOnlineLlamada";
                break;
            default:
                break;
        }
        ;//pantDrOnlineLlamada
    }

    @Override
    public void fireChange(int i, Bundle bundle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (i) {
            case 2:
                String a = "";
                break;
            case 3:
                String b = "";
                break;
        }
    }

    //region otros
    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL_DESCARTAR > System.currentTimeMillis()) {
            mBackPressed = System.currentTimeMillis();
            return;
        } else if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            finishAffinity();
            super.onBackPressed();
            return;
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.anim.entrar_izquierda, R.anim.salir_derecha, R.anim.entrar_derecha, R.anim.salir_izquierda);
            transaction.setCustomAnimations(R.anim.card_flip_left_in, R.anim.card_flip_left_out, R.anim.card_flip_right_in, R.anim.card_flip_right_out);
            if (pantActual.equals("ordenes")) {
                transaction.replace(R.id.frg_pantallaActual, pantOrdenesEstudios);
                transaction.addToBackStack(null);
                transaction.commit();
                pantOrdenes = null;
                pantActual = "ordenesEstudios";
                return;
            }

            else if (pantActual.equals("pharmacyEstudios")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantOrdenesEstudios = null;
                pantActual = "inicio";
                return;
            }
            else if (pantActual.equals("ordenesEstudios")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantOrdenesEstudios = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("notificaciones")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantNotificaciones = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("calenderio")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantNotificaciones = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("eventos_det")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantNotificaciones = null;
                pantActual = "calenderio";
                return;
            } else if (pantActual.equals("eventosdetails")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantNotificaciones = null;
                pantActual = "inicio";
                return;
            }

            else if (pantActual.equals("medicalPrescription")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantNotificaciones = null;
                pantActual = "inicio";
                return;
            }

            else if (pantActual.equals("emergencias")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantEmergencias = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("emergencias_guardias")) {
                transaction.replace(R.id.frg_pantallaActual, pantEmergencias);
                transaction.addToBackStack(null);
                transaction.commit();
                pantEmergenciasGuardias = null;
                pantActual = "emergencias";
                return;
            } else if (pantActual.equals("emergencias_ambulancia")) {
                transaction.replace(R.id.frg_pantallaActual, pantEmergencias);
                transaction.addToBackStack(null);
                transaction.commit();
                pantEmergenciasAmbulancia = null;
                pantActual = "emergencias";
                return;
            } else if (pantActual.equals("estudios_medicos")) {
                transaction.replace(R.id.frg_pantallaActual, pantOrdenesEstudios);
                transaction.addToBackStack(null);
                transaction.commit();
                pantEstudiosMedicos = null;
                pantActual = "ordenesEstudios";
                return;
            } else if (pantActual.equals("credencial")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantCredenciales = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("credencial_nueva")
                    || pantActual.equals("credencial_provisoria")) {
                transaction.replace(R.id.frg_pantallaActual, pantCredenciales);
                transaction.addToBackStack(null);
                transaction.commit();
                pantCredencialProvisoria = null;
                pantActual = "credencial";
                return;
            } else if (pantActual.equals("tramites")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantTramites = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("tramites_internacion")
                    || pantActual.equals("tramites_formularios_especiales")
                    || pantActual.equals("tramites_Documentacion")
                    || pantActual.equals("tramites_Reintegros")
                    || pantActual.equals("tramites_Incorporar_Familiar")
            ) {
                transaction.replace(R.id.frg_pantallaActual, pantTramites);
                transaction.addToBackStack(null);
                transaction.commit();
                pantTramitesInternacion = null;
                pantTramitesFormulariosEspeciales = null;
                pantTramitesDocumentacion = null;
                pantTramitesReintegros = null;
                pantTramitesIncorporarFamiliar = null;
                pantActual = "tramites";
                return;
            } else if (pantActual.equals("cartillas")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantCartillas = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("turnos")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantTurno = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("cartillas_medicas")
                    || pantActual.equals("cartillas_farmacias")
            ) {
                transaction.replace(R.id.frg_pantallaActual, pantCartillas);
                transaction.addToBackStack(null);
                transaction.commit();
                pantCartillasMedicas = null;
                pantActual = "cartillas";
                return;
            } else if (pantActual.equals("cartillas_medicas_secundarias")
            ) {
                transaction.replace(R.id.frg_pantallaActual, pantCartillasMedicas);
                transaction.addToBackStack(null);
                transaction.commit();
                pantCartillasSecundarias = null;
                pantActual = "cartillas_medicas";
                return;
            } else if (pantActual.equals("sedes")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantSedes = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("contactCenter")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                //pantContactCenter = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("contactCenterLlamada")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                //pantLlamada = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("contactCenterReclamoSugerencia")) {
                transaction.replace(R.id.frg_pantallaActual, pantContactCenter);
                transaction.addToBackStack(null);
                transaction.commit();
                pantReclamoSugerencia = null;
                pantActual = "contactCenter";
                return;
            } else if (pantActual.equals("contactCenterPreguntasFrecuentes")) {
                transaction.replace(R.id.frg_pantallaActual, pantContactCenter);
                transaction.addToBackStack(null);
                transaction.commit();
                pantPreguntaFrecuente = null;
                pantActual = "contactCenter";
                return;
            } else if (pantActual.equals("turnosPrestador")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantTurnosPrestador = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("ordenPrestador")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantOrdenPrestador = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("pantActDatos")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantActDatos = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("pantEstadoAfiliacion")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantEstadoAfiliacion = null;
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("drOnline")
                    || pantActual.equals("pantDrOnlineSalaEspera")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                pantDrOnline = null;
                if (estaEnSalaEspera.equals("NO")) {
                    pantDrOnlineSalaEspera = null;
                }
                pantActual = "inicio";
                return;
            } else if (pantActual.equals("pantDrOnlineLlamada")) {
                transaction.replace(R.id.frg_pantallaActual, pantInicial);
                transaction.addToBackStack(null);
                transaction.commit();
                //pantLlamada = null;
                pantActual = "inicio";
                return;
            }
        }
        mBackPressed = System.currentTimeMillis();
    }//
    //endregion

    @Subscribe
    public void onEvent(String evento) {
        if (evento.equals("llegaMensaje")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnNotificaciones.setImageResource(R.drawable.notificacion_red);
                }
            });
        }
    }

    private void updateCustomero(JSONObject params) throws JSONException {
        Log.d("createadata", "" + params);
//        dialogo.dismiss();
        String URLs =
                this.getResources().getString(R.string.srv_localdiscoveritech).concat("update-device-token");
//                "http://discoveritech.org/staff-dashboard/api/update-device-token";
        Log.d("urldata", "" + URLs);

//        JSONObject jsonObject = new JSONObject(params);
//        Log.d("taffa",""+jsonObject);
//        Log.d("createCUstomer", "" + params);
//        jsonObject.put("CUIL",12343);

//        Log.d("createJson",)
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, params, response -> {

            try {
                Log.d("responseError", "" + response);
                boolean stats = response.getBoolean("status");
                Log.d("createCustomer", "" + stats);

            } catch (JSONException e) {

                e.printStackTrace();
            }


        }, error -> {
            Log.d("eDASDADS", "" + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                return pars;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    public void CollectData(String idAfiliado) {
        method1(idAfiliado);
        method2(idAfiliado);
//        method3();
    }

    public void method1(String idAfiliado) {

//        dialogo.show();
        String URLs = this.getResources().getString(R.string.srvLocProduccion);
        URLs = URLs + "APPObtenerEstadoAfiliacion";
        Map<String, String> params = new HashMap<String, String>();
        params.put("idAfiliadoTitular", idAfiliado);
        StringRequest req = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responseInfo", response);
                        try {
                            XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                            Log.d("responseHandlerrs2", "" + xmlToJson);

                            JSONObject rootObj = xmlToJson.toJson().getJSONObject("root");
                            JSONObject filaObj = rootObj.getJSONObject("fila");


                            if (filaObj.has("tablaGrupoFam")) {
                                JSONObject tablaGrupo = filaObj.getJSONObject("tablaGrupoFam");
                                cuilValue = Long.parseLong(tablaGrupo.getString("CUIL").trim());
                                sendingJson.put("idAfiliado", tablaGrupo.get("idAfiliado"));
                                sendingJson.put("apellNomb", tablaGrupo.get("apellNomb"));
                                sendingJson.put("nroAfiliado", tablaGrupo.get("nroAfiliado"));

                            } else {
                                cuilValue = Long.parseLong("0");
                                sendingJson.put("idAfiliado", "");
                                sendingJson.put("apellNomb", "");
                                sendingJson.put("nroAfiliado", "");

                            }

                        } catch (Exception ignored) {

                        }
                    }
                },
                error -> {

                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
//    req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//    queue.add(req);

        MySingleton.getInstance(this).addToRequestQueue(req);
    }

    public void method2(String idAfiliado) {
        Log.d("checkingvalues11", "" + sendingJson);
        String URLs = this.getResources().getString(R.string.srvLocProduccion);
        URLs = URLs + "APPObtenerDatosDeContacto";
        Map<String, String> params = new HashMap<String, String>();
        params.put("idAfiliado", idAfiliado);
        StringRequest req = new StringRequest(Request.Method.POST, URLs,
                response -> {
//                        dialogo.dismiss();
                    Log.d("responseInfo", response);
                    try {
                        XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                        Log.d("responseHandlerrsm2", "" + xmlToJson);
                        JSONObject rootObjs = Objects.requireNonNull(xmlToJson.toJson()).getJSONObject("Resultado");
                        Log.d("checkfila", "" + rootObjs);


                        JSONObject filaObj = rootObjs.getJSONObject("fila");

                        if (filaObj.has("domicilio")) {
                            JSONObject domicilioObj = filaObj.getJSONObject("domicilio");
                            Log.d("domicilioObjss", "" + domicilioObj);
                            sendingJson.put("piso", domicilioObj.get("piso"));
                            sendingJson.put("numero", domicilioObj.get("numero"));
                            sendingJson.put("calle", domicilioObj.get("calle"));
                            sendingJson.put("depto", domicilioObj.get("depto"));
                            sendingJson.put("localidad", domicilioObj.get("localidad"));
                            sendingJson.put("provincia", domicilioObj.get("provincia"));
                            sendingJson.put("idLocalidad", domicilioObj.get("idLocalidad"));

                        } else {
                            sendingJson.put("piso", "");
                            sendingJson.put("numero", "");
                            sendingJson.put("calle", "");
                            sendingJson.put("depto", "");
                            sendingJson.put("localidad", "");
                            sendingJson.put("provincia", "");
                            sendingJson.put("idLocalidad", "");

                            Log.d("domicilioObjssw", "" + sendingJson);

                        }


                        if (filaObj.has("tablaresultado")) {
                            JSONObject tablaresultado = filaObj.getJSONObject("tablaresultado");
                            sendingJson.put("numAfiliado", tablaresultado.get("numAfiliado"));
                            sendingJson.put("estadoFiscalizacionAfiliado", tablaresultado.get("estadoFiscalizacionAfiliado"));
                            sendingJson.put("valorDevuelto", tablaresultado.get("valorDevuelto"));
                            sendingJson.put("planPrestacionalAfiliado", tablaresultado.get("planPrestacionalAfiliado"));
                            sendingJson.put("dniAfiliado", tablaresultado.get("dniAfiliado"));
                            sendingJson.put("mensaje", tablaresultado.get("mensaje"));
                            sendingJson.put("nombreAfiliado", tablaresultado.get("nombreAfiliado"));
                            Log.d("domicilioObjssw", "" + sendingJson);

                        } else {
                            sendingJson.put("numAfiliado", "");
                            sendingJson.put("estadoFiscalizacionAfiliado", "");
                            sendingJson.put("valorDevuelto", "");
                            sendingJson.put("planPrestacionalAfiliado", "");
                            sendingJson.put("dniAfiliado", "");
                            sendingJson.put("mensaje", "");
                            sendingJson.put("nombreAfiliado", "");
                        }

                        if (filaObj.has("mail")) {
                            JSONObject mailObj = filaObj.getJSONObject("mail");
                            sendingJson.put("email", mailObj.get("mail"));
                            Log.d("domicilioObjssw2", "" + sendingJson);

                        } else {
                            sendingJson.put("email", "");

                        }

                        if (filaObj.has("telefonos")) {
                            JSONObject telefonos = filaObj.getJSONObject("telefonos");
                            sendingJson.put("caractTel", telefonos.get("caractTel"));
                            sendingJson.put("numTel", telefonos.get("numTel"));
                            sendingJson.put("CUIL", cuilValue);
                            Log.d("domicilioObjssw2", "" + sendingJson);

                        } else {
                            sendingJson.put("caractTel", "");
                            sendingJson.put("numTel", "");
                            sendingJson.put("CUIL", "");

                        }


                        if (filaObj.has("telefonosFijso")) {
                            JSONObject telefonosFijos = filaObj.getJSONObject("telefonosFijos");
                            sendingJson.put("numTelFijo", telefonosFijos.get("numTelFijo"));
                            sendingJson.put("caractTelFijo", telefonosFijos.get("caractTelFijo"));

                        } else {
                            sendingJson.put("numTelFijo", "");
                            sendingJson.put("caractTelFijo", "");

                        }


//                        Log.d("responsw4ad", domicilioObj.getString("piso").toString());

//                        Log.d("pisovalue", "" +domicilioObj.getString("piso"));


                        Log.d("responsw4", "" + sendingJson);

                        Log.d("responsw4", "" + sendingJson);


                        Log.d("responsw4", "" + sendingJson);

                        Log.d("c", "" + sendingJson);
                        Log.d("totalasdf", "" + sendingJson);
                       /* Intent intentforsend = new Intent(getContext(), VideoActivity.class);
                        intentforsend.putExtra("connectMobileToWeb","connectedcall");
                        startActivity(intentforsend);
                        pantPrincipal.overridePendingTransition(R.anim.entrar_arriba, R.anim.entrar_de_abajo);
*/


                        Log.d("valueofTotalJson222", "" + sendingJson);

                    } catch (Exception ignored) {

                    }
                },
                error -> {

                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(req);
    }


    public void getCustomers() {
//        dialogo = fabrica_dialogos.dlgBuscando(this, "Espere por favor...");
        String URLs = this.getResources().getString(R.string.srv_localdiscoveritech).concat("get-customer");

//        String URLs = "https://digitalech.com/staff-dashboard/api/get-customer";
        Log.d("urldata", "" + URLs);
        JSONObject deviceobjs = new JSONObject();
//        Log.d("gtidAfiliado", idAfiliadoRegistro);
//        Log.d("gtidAfiliado", idAfiliadoRegistro);
//        Log.d("devictoken",tokenid);
        try {
            deviceobjs.put("idAfiliado", idAfiliadoRegistro);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("devicetag", "" + deviceobjs);

        JsonObjectRequest jsonObjectRequests = new JsonObjectRequest(Request.Method.POST, URLs, deviceobjs, response -> {

            try {
                Log.d("eDASDADS", "" + response);
                Boolean stats = response.getBoolean("status");
                Log.d("sdadasda", "" + stats);
                Log.d("statsdadsval", "" + response);
                if (stats.equals(true)) {

                    access_token = response.getJSONObject("response").getJSONObject("detail").getJSONObject("token_detail").getString("access_token");
                    Log.d("access_token_getgt", access_token);

                    if (response.getJSONObject("response").getJSONObject("detail").has("appointment_detail")) {

                        if (response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointment_detail").get("user_went_to_appointment").equals(false)) {
                            Log.d("chekcingdata","iHola " +
//                                    response.getJSONObject("response").getJSONObject("detail").getJSONObject("customer_data").get("apellNomb")
                                    response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointment_detail").get("apellidoYNombre")
                                    + "! Sacaste turno con " +
                                    response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointment_detail").get("prestador")

                                    + "?");

//                        showMyDialouge("Appointment Alert", "Did you make an appointment with " + response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointment_detail").get("apellidoYNombre")+"?");
                            showMyDialog("Alerta de turno", "iHola " +
//                                    response.getJSONObject("response").getJSONObject("detail").getJSONObject("customer_data").get("apellNomb")
                                            response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointment_detail").get("apellidoYNombre")
                                            + "! Sacaste turno con " +
                                            response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointment_detail").get("prestador")

                                            + "?",

                                    (Integer) response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointment_detail").get("id")
                            );
//                        showMyDialog("Appointment Alert!","Did you make an appointment with " + response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointment_detail").get("apellidoYNombre")+"?");

                        }
                    }
                }

            } catch (JSONException e) {
                Log.d("catchingJson", "" + e);
                //                    createCustomer(sendingJson);

                e.printStackTrace();
            }
        }, error -> {
//            dialogo.dismiss();
            try {

                Log.d("eDASDADS", "" + error.getMessage());
                createCustomer(sendingJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Log.d("getError", String.valueOf(error.networkResponse.statusCode));
        }) {

        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequests);

    }

    /*
        public void showMyDialouge(String title, String message) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
            builder1.setIcon(R.drawable.andeslogo);
            builder1.setTitle(title);
            builder1.setMessage(message);
            builder1.setCancelable(false);

            builder1.setPositiveButton(
                    "Yes",
                    (dialog, id) -> {
    //                    if (isEmailExist.equals(true)) {
    //
    //                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
    //                            @Override
    //                            public void run() {
    //                            }
    //                        }, 1000);
    //
    ////                        Global.changeFragmentSplash(getContext(), new LoginFragment(), "LoginFragment", false);
    //
    //                    }
    //                    mKProgressHUD.dismiss();

                        dialog.cancel();
                    });
            builder1.setNegativeButton("No",(dialog,id) -> {

            });

            AlertDialog alert11 = builder1.create();
            if (alert11.getWindow() != null)
            {
                Window window = alert11.getWindow();
                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.getDecorView().setTop(100);
                window.getDecorView().setLeft(100);
                window.setGravity(Gravity.CENTER);
    //            alert11.getWindow().setBackgroundDrawableResource(android.R.color.background_dark);
            }
            try {
                alert11.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    */
    public void showMyDialog(String title, String body, int id) {
        dialog.dismiss();
        dialog.setContentView(R.layout.appointment_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleString, bodyString;
        titleString = dialog.findViewById(R.id.appointment_title);
        bodyString = dialog.findViewById(R.id.appointment_body);
        titleString.setText(title);
        bodyString.setText(body);
        TextView yesBtn, noBtn;
        yesBtn = dialog.findViewById(R.id.appointment_Yesbtn);
        noBtn = dialog.findViewById(R.id.appointment_nobtn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalender(id);
               /*
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("appointment_id", id);
                    updateAppointment(jsonObject);
                    dialog.dismiss();
                    Log.d("istata", "" + jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }


        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("appointment_id", id);
                    updateAppointment(jsonObject);
                    dialog.dismiss();
                    Log.d("istata", "" + jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                showMyDialog1("Alerta de cita", "¿Te gstaria agendarlo en el calendario?", id);
            }
        });
        dialog.show();


    }

    public void showMyDialog1(String title, String body, Integer id) {
        dialog.dismiss();
        dialog.setContentView(R.layout.appointment_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleString, bodyString;
        titleString = dialog.findViewById(R.id.appointment_title);
        bodyString = dialog.findViewById(R.id.appointment_body);
        titleString.setText(title);
        bodyString.setText(body);
        TextView yesBtn, noBtn;
        yesBtn = dialog.findViewById(R.id.appointment_Yesbtn);
        noBtn = dialog.findViewById(R.id.appointment_nobtn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //showCalender(id);


            }


        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("appointment_id", id);
                    updateAppointment(jsonObject);
                    dialog.dismiss();
                    Log.d("istata", "" + jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();


    }

    public void showSuccessDialog(String title, String body) {
        dialog.dismiss();
        dialog.setContentView(R.layout.appointment_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleString, bodyString;
        titleString = dialog.findViewById(R.id.appointment_title);
        bodyString = dialog.findViewById(R.id.appointment_body);
        titleString.setText(title);
        bodyString.setText(body);
        TextView yesBtn, noBtn;
        yesBtn = dialog.findViewById(R.id.appointment_Yesbtn);
        noBtn = dialog.findViewById(R.id.appointment_nobtn);
        yesBtn.setText("OK");
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showCalender(id);
                dialog.dismiss();

            }


        });
        noBtn.setVisibility(View.GONE);
        dialog.show();


    }


    public void showCalender(int id) {
        final String[] dates = new String[1];
        dialog.dismiss();
        dialog.setContentView(R.layout.fragment_fragmento_calenderio);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//           TextView titleString,bodyString;
//        titleString = dialog.findViewById(R.id.appointment_title);
//        bodyString = dialog.findViewById(R.id.appointment_body);
//        titleString.setText(title);
//        bodyString.setText(body);
        TextView yesBtn, noBtn;
        yesBtn = dialog.findViewById(R.id.calender_yesBtn);
        noBtn = dialog.findViewById(R.id.calender_nobtn);
        CalendarView calendarView = dialog.findViewById(R.id.calendarView);
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                dates[0] = eventDay.getCalendar().get(Calendar.YEAR)
                        + "-" + Integer.parseInt(String.valueOf(eventDay.getCalendar().get(Calendar.MONTH)+1)) +
                        "-" + eventDay.getCalendar().get(Calendar.DATE)
                ;
                Log.d("checkingdata",dates[0]);
//                Toast.makeText(this,dates[0].toString(),Toast.LENGTH_LONG).show();
//Toast.makeText(this,dates[0],Toast.LENGTH_LONG).show();
//            Log.d("datesvalue",dates[0]);
            }
        });
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("appointment_id",id);
                    updateAppointment(jsonObject);
                    dialog.dismiss();
                    Log.d("istata",""+jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
              */
//            }

                Log.d("datesvalue", dates[0]);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("date", dates[0]);
                    jsonObject.put("appointment_id", id);
                    addDateAppointment(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    private void createCustomer(JSONObject params) throws JSONException {
        Log.d("createadata", "" + params);

        String URLs =
                this.getResources().getString(R.string.srv_localdiscoveritech).concat("customers");
//                "http://discoveritech.org/staff-dashboard/api/customers";
        Log.d("urldata", "" + URLs);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, params, response -> {

            try {
                Log.d("eDASDADS", "" + response);
                boolean stats = response.getBoolean("status");
                Log.d("createCustomer", "" + stats);
                if (stats) {
                    access_token = response.getJSONObject("response").getJSONObject("detail").getJSONObject("token_detail").getString("access_token");
                    Log.d("access_token_getCr", access_token);

                }
            } catch (JSONException e) {

                e.printStackTrace();
            }


        }, error -> {
//            dialogo.dismiss();
            Log.d("eDASDADS", "" + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                return pars;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

//    @Override
//    public void setOkDialog(String name, Dialog dialog) throws Exception {
////        this.dialog = dialog;
//        dialog = this.dialog;
//        dialog = fabrica_dialogos.dlgBuscando(this,"Buscando especialidades");
////        dialog.show();
//
//        fragmento_turnos fragmento_turnosobj = new fragmento_turnos();
//        fragmento_turnosobj.setDialogListenerData(name,dialog);
//
//    }


    private void updateAppointment(JSONObject params) throws JSONException {
        Log.d("createadata", "" + params);

        String URLs =
                this.getResources().getString(R.string.srv_localdiscoveritech).concat("update-appointment-status");
//                "http://discoveritech.org/staff-dashboard/api/";
        Log.d("urldata", "" + URLs);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, params, response -> {

            try {
                Log.d("responseError", "" + response);
                boolean stats = response.getBoolean("status");
                Log.d("createCustomer", "" + stats);
                if (stats) {

                }
            } catch (JSONException e) {

                e.printStackTrace();
            }


        }, error -> {
//            dialogo.dismiss();
            Log.d("eDASDADS", "" + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                return pars;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    private void addDateAppointment(JSONObject params) throws JSONException {
        Log.d("createadata", "" + params);

        String URLs =
//        "http://discoveritech.org/staff-dashboard/api/";
                this.getResources().getString(R.string.srv_localdiscoveritech).concat("set-appointment-date");

        Log.d("urldata", "" + URLs);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, params, response -> {
            try {
                if (response.getString("status").equals("true")) {
                    dialog.dismiss();
                    showSuccessDialog("Turno guardado", response.getJSONObject("response").getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("datesvalue", "Success");
//              dialog.dismiss();


        }, error -> {
//            dialogo.dismiss();
            Log.d("eDASDADS", "" + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                return pars;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    public void whatsAppRequiredData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = this.getResources().getString(R.string.srvLocProduccion);
        SQLController database = new SQLController(this);
        final String idAfiliado = database.obtenerIDAfiliado();


        url = url + "consultarAfiliado";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                        Log.d("wwr2", "" + xmlToJson);
                        JSONObject rootObj = xmlToJson.toJson().getJSONObject("Resultado");
                        Log.d("chakaein Nro-Document", rootObj.getString("nroDocumento") + "Plan Prestacional" + "" +
                                rootObj.getString("planPrestacional"));

                        arrasend[0] = rootObj.getString("nroDocumento");
                        arrasend[1] = rootObj.getString("planPrestacional");
                        arraConsultado[0] = rootObj.getString("apellNomb");
                        arraConsultado[1] = rootObj.getString("nroDocumento");
                        arraConsultado[2]= rootObj.getString("nroAfiliado");
                        arraConsultado[3]= rootObj.getString("parentesco");
                        arraConsultado[4] = rootObj.getString("provincia");
                        arraConsultado[5]= rootObj.getString("estadoAfiliacion");

                        Log.d("checkingdata",arraConsultado[0]);
                        Log.d("checkingdata",arraConsultado[1]);
                        Log.d("checkingdata",arraConsultado[2]);
                        Log.d("checkingdata",arraConsultado[3]);
                        Log.d("checkingdata",arraConsultado[5]);
//                        Log.d("checkingdata",arraConsultado[6]);

                    } catch (Exception errEx) {
//                            errorHandler.onError("Error desconocido al buscar los tipos de internación:\n\t" + errEx.getMessage());
                    }
                },
                error -> {

                }) {

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("usuario", "CHATBOT");
                params.put("password", "DrtEchat%");
                params.put("administradora", "F100376F-33F9-49FD-AFB9-EE53616E7F0C");
                params.put("datosAfiliado", idAfiliado);
                return params;
            }
        };
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

}

