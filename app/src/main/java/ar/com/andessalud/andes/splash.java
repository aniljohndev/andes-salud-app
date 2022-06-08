package ar.com.andessalud.andes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import ar.com.andessalud.andes.models.NotificationsModel;
import ar.com.andessalud.andes.twilio.MySingleton;
import ar.com.andessalud.andes.twilio.VideoCallDialogActivity;

public class splash extends AppCompatActivity {

    private Dialog dialog;
    final Activity actActual=this;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    SharedPreferences appPref;
    boolean esLaPrimeraVez = true;
    public static String myCustomKey = "";
    public static String myCustomKeyfordoctor = "";

    //    public static String event_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // Handlo background notification Calling.
        Bundle bundle = getIntent().getExtras();

//        Bundle dataNotification = new Bundle();
        if (bundle != null) {
//            Log.d("checkingBundle",""+bundle);

            myCustomKey = "";
            myCustomKeyfordoctor = "";
//            bundle.get
            if (bundle.containsKey("staff_meeting"))
            {
                Log.d("checkingdatastaff", String.valueOf(bundle.get("staff_meeting")));
                  myCustomKey = String.valueOf(bundle.get("room_name"));

            }
            if (bundle.containsKey("join_appointment"))
            {
                Log.d("checkingdata",bundle.getString("join_appointment"));
                myCustomKeyfordoctor = String.valueOf(bundle.get("room_name"));
            }


//            Log.d("datafor", "Notification DATA On App BackGroud" + myCustomKey);

        }

        //region verifica si muestra presentacion
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaActual = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date()).toString();
        SQLController consultaBD= new SQLController(this);
        String fechaUltimoInicio = consultaBD.registraHoraInicioAplicacion(fechaActual.toString());

        try {
            Date ultimaFechaDate = dateFormat.parse(fechaUltimoInicio);
            Date nuevaFechaDate = dateFormat.parse(fechaActual);

            long diff = nuevaFechaDate.getTime() - ultimaFechaDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            if (minutes>180) {
                muestraPresentacion();
            }else{
                verificarPermisos();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //endregion
    }

    //region muestra presentacion
    private void muestraPresentacion(){
        final WebView webView = (WebView) this.findViewById(R.id.webView);
        webView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl("file:///android_asset/andeslogo.html");
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        verificarPermisos();
                    }
                }, 2400);
            }
        });
    }

    private void verificarPermisos(){
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                pedirPermisos();
            }else {
                esLaPrimeraVez();
            }
        } catch (Exception errorEx){
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dlg_modal_error);
            // set the custom dialog components - text, image and button
            TextView textMsg = (TextView) dialog.findViewById(R.id.lblMsg);
            textMsg.setText("Error en inicio:\n" + errorEx.getMessage());
            ImageView imgCerrar= (ImageView) dialog.findViewById(R.id.btnCerrar);
            imgCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishAffinity();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
    //endregion

    //region pedir permisos
    private void pedirPermisos(){
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!agregarPermiso(permissionsList, android.Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("\t-GPS\n");
        if (!agregarPermiso(permissionsList, android.Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("\t-Estado de la red\n");
        if (!agregarPermiso(permissionsList, android.Manifest.permission.CAMERA))
            permissionsNeeded.add("\t-Cámara\n");
        if (!agregarPermiso(permissionsList, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("\t-Leer del almacenamiento externo\n");
        if (!agregarPermiso(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("\t-Escribir en el almacenamiento externo\n");
        if (!agregarPermiso(permissionsList, android.Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("\t-Estado del teléfono\n");
        if (!agregarPermiso(permissionsList, android.Manifest.permission.GET_ACCOUNTS))
            permissionsNeeded.add("\t-Utilizar cuentas de email\n");
        if (!agregarPermiso(permissionsList, android.Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("\t-Contactos\n");
        if (!agregarPermiso(permissionsList, android.Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add("\t-Audio\n");
        if (!agregarPermiso(permissionsList, android.Manifest.permission.MODIFY_AUDIO_SETTINGS))
            permissionsNeeded.add("\t-Modificar el audio\n");
        if (!agregarPermiso(permissionsList, android.Manifest.permission.WAKE_LOCK))
            permissionsNeeded.add("\t-Activar el dispositivo\n");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "Para poder utilizar esta APP, se necesitan los siguientes permisos:\n " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++) {
                    message = message + permissionsNeeded.get(i);
                }
                mostrarMensajePermisos(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(actActual, permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        },new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                return;
            }

            ActivityCompat.requestPermissions(this,permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        esLaPrimeraVez();
    }

    private boolean agregarPermiso(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,permission))
                return false;
        }
        return true;
    }

    private void mostrarMensajePermisos(String message, DialogInterface.OnClickListener okListener
            , DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Continuar", okListener)
                .setNegativeButton("Cancelar", cancelListener)
                .setCancelable(false)
                .create()
                .show();
    }
    //endregion

    //region verificar si es la primera vez que se carga la app para preguntar si quiere agregar acceso directo
    private void esLaPrimeraVez(){
        appPref = getSharedPreferences("esLaPrimeraVez", 0);
        esLaPrimeraVez = appPref.getBoolean("esLaPrimeraVez", true);
        if (esLaPrimeraVez) {
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dlg_modal_crear_acceso_directo);

            final AppCompatCheckBox chkNoMostrarMas=(AppCompatCheckBox) dialog.findViewById(R.id.chkNoMostrarMas);
            final LinearLayout lnChkNoPreguntar=(LinearLayout) dialog.findViewById(R.id.lnChkNoPreguntar);

            lnChkNoPreguntar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chkNoMostrarMas.isChecked()){
                        chkNoMostrarMas.setChecked(false);
                    }else{
                        chkNoMostrarMas.setChecked(true);
                    }
                }
            });

            ImageView btnSi=(ImageView)dialog.findViewById(R.id.btnSi);
            btnSi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    agregarAccesoDirecto();
                    cargarAplicacion();
                    dialog.dismiss();
                }
            });

            ImageView btnNo=(ImageView)dialog.findViewById(R.id.btnNo);
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chkNoMostrarMas.isChecked()==true){
                        SharedPreferences.Editor editor = appPref.edit();
                        editor.putBoolean("esLaPrimeraVez", false);
                        editor.commit();
                    }
                    dialog.dismiss();
                    cargarAplicacion();
                }
            });
            dialog.show();

        }else{
            cargarAplicacion();
        }
    }

    private void agregarAccesoDirecto(){
        Intent shortcutIntent = new Intent(getApplicationContext(),splash.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        Intent intent = new Intent();

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "ANDES Salud");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.andeslogo));
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(intent);

        SharedPreferences.Editor editor = appPref.edit();
        editor.putBoolean("esLaPrimeraVez", false);
        editor.commit();
    }
    //endregion

    //region cargar aplicacion
    private void cargarAplicacion(){
        try {
            SQLController database = new SQLController(this);
            Cursor curEstadoRegistro = database.leerEstadoRegistro();
            if (curEstadoRegistro.getCount() == 0) {
                //No se comenzo el proceso de registración
                Intent intentRegistro = new Intent(getBaseContext(), inicio.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRegistro);
            } else if (curEstadoRegistro.getString(1).equals("INICIO")) {
                //Esta esperando la activación de la APP
                Intent intentRegistro = new Intent(getBaseContext(), inicio.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRegistro);
                finishAffinity();
            } else if (curEstadoRegistro.getString(1).equals("ESPACTIVACION")) {
                //Esta esperando la activación de la APP
                Intent intentRegistro = new Intent(getBaseContext(), soy_afiliado_sin_dni_activacion.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRegistro);
                finishAffinity();
            } else if (curEstadoRegistro.getString(1).equals("PANTACTIVAAPP")) {
                //Desde central activaron la APP
                Intent intentRegistro = new Intent(getBaseContext(), soy_afiliado_app_activada.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRegistro);
                finishAffinity();
            } else if (curEstadoRegistro.getString(1).equals("PANTACTIVAAPPRECH")) {
                //Esperando los nuevos datos de usuario y pass
                Intent intentRegistro = new Intent(getBaseContext(), soy_afiliado_sin_dni_rechazado.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRegistro);
                finishAffinity();
            } else if (curEstadoRegistro.getString(1).equals("YAINGRESO")) {
                //Completo el proceso de registro y esta accediendo a la APP
                Intent intentRegistro = new Intent(getBaseContext(), principal.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRegistro);
                finishAffinity();
            }else { //ya termino el proceso de registro
                Intent intentInicio = new Intent(getBaseContext(), inicio.class);
                intentInicio.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentInicio);
            }
            /*else if (curEstadoRegistro.getString(1).equals("11")) {
                //Se comenzo en proceso de registración pero no se termino, por lo que pudo haber recibido el SMS y va a ingresar el codigo
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                filter.setPriority(2147483647);
                recibeSMS receiver = new recibeSMS();
                this.registerReceiver(receiver, filter);
                this.startService(smsServiceIntent);
                Intent intentRegistro = new Intent(getBaseContext(), registro_mail_tel.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRegistro);
    //            startActivity(intentRegistro);
            }else if (curEstadoRegistro.getString(1).equals("12")) {
                //Se comenzo en proceso de registración pero no se termino, por lo que pudo haber recibido el SMS y va a ingresar el codigo
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                filter.setPriority(2147483647);
                recibeSMS receiver = new recibeSMS();
                this.registerReceiver(receiver, filter);
                this.startService(smsServiceIntent);
                Intent intentRegistro = new Intent(getBaseContext(), registro_mail_tel_1.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentRegistro);
            }else if (curEstadoRegistro.getString(1).equals("13")) {
                //Se comenzo en proceso de registración pero no se termino, por lo que pudo haber recibido el SMS y va a ingresar el codigo
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                filter.setPriority(2147483647);
                recibeSMS receiver = new recibeSMS();
                this.registerReceiver(receiver, filter);
                this.startService(smsServiceIntent);
                Intent intentRegistro = new Intent(getBaseContext(), registro_mail_tel_2.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentRegistro);
            }else if (curEstadoRegistro.getString(1).equals("21")) {
                //Se comenzo en proceso de registración pero no se termino, por lo que pudo haber recibido el SMS y va a ingresar el codigo
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                filter.setPriority(2147483647);
                recibeSMS receiver = new recibeSMS();
                this.registerReceiver(receiver, filter);
                this.startService(smsServiceIntent);
                Intent intentRegistro = new Intent(getBaseContext(), restablecer_pass.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRegistro);
            }else if (curEstadoRegistro.getString(1).equals("22")) {
                //Se comenzo en proceso de registración pero no se termino, por lo que pudo haber recibido el SMS y va a ingresar el codigo
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                filter.setPriority(2147483647);
                recibeSMS receiver = new recibeSMS();
                this.registerReceiver(receiver, filter);
                this.startService(smsServiceIntent);
                Intent intentRegistro = new Intent(getBaseContext(), restablecer_pass_1.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRegistro);
            }else if (curEstadoRegistro.getString(1).equals("23")) {
                //Se comenzo en proceso de registración pero no se termino, por lo que pudo haber recibido el SMS y va a ingresar el codigo
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                filter.setPriority(2147483647);
                recibeSMS receiver = new recibeSMS();
                this.registerReceiver(receiver, filter);
                this.startService(smsServiceIntent);
                Intent intentRegistro = new Intent(getBaseContext(), restablecer_pass_2.class);
                intentRegistro.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentRegistro);
            }else { //ya termino el proceso de registro
                curEstadoRegistro = database.leerEstadoSesion();
                if (curEstadoRegistro.getString(0).equals("1")) {
                    stopService(new Intent(this, ServiceCommunicator.class));
                    Intent intentInicio = new Intent(getBaseContext(), inicio.class);
                    intentInicio.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentInicio);
                }else{
                    stopService(new Intent(this, ServiceCommunicator.class));
                    Intent intentInicio = new Intent(getBaseContext(), login_mail_tel.class);
                    intentInicio.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentInicio);
                }
            }*/
            finish();
        }catch (Exception errorEx){
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dlg_modal_error);
            // set the custom dialog components - text, image and button
            TextView textMsg = (TextView) dialog.findViewById(R.id.lblMsg);
            textMsg.setText("Error en inicio:\n" + errorEx.getMessage());
            ImageView imgCerrar= (ImageView) dialog.findViewById(R.id.btnCerrar);
            imgCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishAffinity();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
    //endregion

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                ){
                    esLaPrimeraVez();
                } else {
                    // Permission Denied
                    dialog = new Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setContentView(R.layout.dlg_modal_error);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    TextView textMsg = (TextView) dialog.findViewById(R.id.lblMsg);
                    textMsg.setText("Para poder utilizar la aplicación se necesitan todos los permisos solicitados");
                    ImageView imgCerrar = (ImageView) dialog.findViewById(R.id.btnCerrar);
                    imgCerrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            finish();
                            actActual.overridePendingTransition(R.anim.entrar_izquierda, R.anim.salir_derecha);
                        }
                    });
                    dialog.show();
                    return;
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



}
