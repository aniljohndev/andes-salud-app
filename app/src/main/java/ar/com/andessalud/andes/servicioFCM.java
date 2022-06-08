package ar.com.andessalud.andes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.koushikdutta.ion.builder.Builders;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.w3c.dom.Document;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Map;
import java.util.Objects;

import ar.com.andessalud.andes.twilio.VideoActivity;
import ar.com.andessalud.andes.twilio.VideoCallDialogActivity;

import static ar.com.andessalud.andes.splash.myCustomKey;
import static ar.com.andessalud.andes.splash.myCustomKeyfordoctor;

/**
 * Created by Usuario on 22/08/2016.
 */
public class servicioFCM extends FirebaseMessagingService {
    String mensajeWS;
    Intent notificationIntent;
    PendingIntent intent;
    principal pantPrincipal;
    AlarmManager alarmManager;

    @Override
    public void onNewToken(String token) {
        almacenarToken(token);
    }

    private void almacenarToken(String token) {
        SharedPreferences datosLocales = getSharedPreferences("datosAplicacion", MODE_PRIVATE);
        SharedPreferences.Editor editor = datosLocales.edit();
        editor.putString("senderid", token);
        editor.putString("senderidEnviado", "0");
        editor.commit();

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long timeofReceiver = System.currentTimeMillis() + 1000 * 2;

        mensajeWS = remoteMessage.getData().toString();

        Log.d("messagereceiver", mensajeWS);
        Map<String, String> data = remoteMessage.getData();
        Log.d("messagereceiver", mensajeWS);


        //Checking if the notification contain data ->  contains meeting
        if (data.containsKey("staff_meeting"))
        {
                myCustomKey = "";
                myCustomKey = data.get("room_name");

                Intent intent = new Intent(this, VideoCallDialogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
        }
        if (data.containsKey("join_appointment")){
//            Intent intent = new Intent(this, VideoActivity.class);
            Log.d("roomno",data.get("room_name"));
//            myCustomKey = "doctor"+data.get("room_name");
//            intent.putExtra("joinappointment",data.get("r
//            oom_name"));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
            myCustomKeyfordoctor  = "";
            myCustomKeyfordoctor = data.get("room_name");

//            notificationIntent.putExtra("joinappointment",data.get("room_name"));
            EventBus.getDefault().post(data.get("room_name"));
            foregroundDisplay(data.get("title"), data.get("body"), null,data.get("room_name"));

        }
        if (data.containsKey("medical_prescription"))
        {
            foregroundDisplay(data.get("title"), data.get("body"),null,"movetonotification");

        }
        if (data.containsKey("appointment_notification")) {

            foregroundDisplay(data.get("title"), data.get("body"), data.get("appointment_notification"),null);
        }
        if (data.containsKey("event_id")) {

            foregroundDisplay(data.get("title"), data.get("body"), data.get("event_id"),null);
        }
        if (data.containsKey("missed_call")) {
            EventBus.getDefault().post("callmissed");
            myCustomKey = "";
            foregroundDisplay(data.get("title"), data.get("body"), null,null);
        }
        if (data.containsKey("end_call")) {
            if (Objects.requireNonNull(data.get("end_call")).contains("1")) {
                myCustomKey = "";
                EventBus.getDefault().post("callended");
            }
        }
        if (data.containsKey("end_appointment")) {
            if (Objects.requireNonNull(data.get("end_appointment")).contains("1")) {
                myCustomKeyfordoctor="";
                EventBus.getDefault().post("callendedforappointment");
            }

        }

        mensajeWS = funciones.decodificaTextoAPP(mensajeWS);
        if (mensajeWS != null) {
            Document valores = funciones.getDomElement(mensajeWS);
            try {
                String accion = valores.getElementsByTagName("accion").item(0).getTextContent();
                if (accion.equals("ACTAPPSINDOC")) {
                    String titulo = valores.getElementsByTagName("titulo").item(0).getTextContent();
                    String mensajePopUp = valores.getElementsByTagName("mensajePopUp").item(0).getTextContent();
                    String idPedido = valores.getElementsByTagName("idPedido").item(0).getTextContent();
                    String idAfiliado = valores.getElementsByTagName("idAfiliado").item(0).getTextContent();
                    String mensajeAfiliado = valores.getElementsByTagName("mensajeAfiliado").item(0).getTextContent();
                    String usuAfiliado = valores.getElementsByTagName("usuAfiliado").item(0).getTextContent();
                    String passAfiliado = valores.getElementsByTagName("passAfiliado").item(0).getTextContent();
                    String textoPantallaActivacion = valores.getElementsByTagName("textoPantallaActivacion").item(0).getTextContent();

                    SQLController database = new SQLController(this);
                    database.actualizarEstadoRegistro("PANTACTIVAAPP");

                    SharedPreferences datosLocales = getSharedPreferences("datosAplicacion", MODE_PRIVATE);
                    SharedPreferences.Editor editor = datosLocales.edit();
                    editor.putString("idAfiliadoRegistro", idAfiliado);
                    editor.putString("mensajeUsuYPass", mensajeAfiliado);
                    editor.putString("usuarioUsuYPass", usuAfiliado);
                    editor.putString("passUsuYPass", passAfiliado);
                    editor.putString("textoPantallaActivacion", textoPantallaActivacion);
                    editor.commit();

                    muestraNotificacion("ACTAPPSINDOC", titulo, mensajePopUp, null, null, null);
                } else if (accion.equals("ACTAPPSINDOCRECHAZO")) {
                    String mensajePopUp = valores.getElementsByTagName("mensajePopUp").item(0).getTextContent();
                    String titulo = valores.getElementsByTagName("titulo").item(0).getTextContent();
                    String idPedido = valores.getElementsByTagName("idPedido").item(0).getTextContent();
                    String codMotRechazo = valores.getElementsByTagName("codMotRechazo").item(0).getTextContent();

                    SharedPreferences datosLocales = getSharedPreferences("datosAplicacion", MODE_PRIVATE);
                    SharedPreferences.Editor editor = datosLocales.edit();
                    editor.putString("idPedido", idPedido);
                    editor.putString("codMotRechazo", codMotRechazo);
                    editor.commit();

                    SQLController database = new SQLController(this);
                    database.actualizarEstadoRegistro("PANTACTIVAAPPRECH");

                    if (!principal.pantActual.equals("notificaciones")) {
                        muestraNotificacion("ACTAPPSINDOCRECHAZO", titulo, mensajePopUp, null, null, null);
                    } else {

                    }
                } else if (accion.equals("RECHAZOORDENAMB")) {
                    String mensajePopUp = valores.getElementsByTagName("mensajePopUp").item(0).getTextContent();
                    String titulo = valores.getElementsByTagName("titulo").item(0).getTextContent();
                    String idOrden = valores.getElementsByTagName("idOrden").item(0).getTextContent();
                    String mensajeRechazo = valores.getElementsByTagName("mensajeRechazo").item(0).getTextContent();
                    String fecMsg = valores.getElementsByTagName("fecMsg").item(0).getTextContent();

                    SQLController database = new SQLController(this);
                    database.agregarAviso(idOrden);
                    String muestraMensaje = database.agregarRechazoOrdenAMB(idOrden, mensajeRechazo, fecMsg);
                    if (!principal.pantActual.equals("notificaciones")) {
                        muestraNotificacion("RECHAZOORDENAMB", titulo, mensajePopUp, idOrden, fecMsg, null);
                    } else {

                    }
                    EventBus.getDefault().post("llegaMensaje");
                } else if (accion.equals("AUTORDENAMB")) {
                    String mensajePopUp = valores.getElementsByTagName("mensajePopUp").item(0).getTextContent();
                    String titulo = valores.getElementsByTagName("titulo").item(0).getTextContent();
                    String idOrden = valores.getElementsByTagName("idOrden").item(0).getTextContent();
                    String fecVenc = valores.getElementsByTagName("fecVenc").item(0).getTextContent();
                    String fecMsg = valores.getElementsByTagName("fecMsg").item(0).getTextContent();

                    SQLController database = new SQLController(this);
                    database.agregarAviso(idOrden);
                    String muestraMensaje = database.agregarAutorizacionOrdenAMB(idOrden, fecVenc, fecMsg);
                    if (!principal.pantActual.equals("notificaciones")) {
                        muestraNotificacion("AUTORDENAMB", titulo, mensajePopUp, idOrden, fecMsg, null);
                    } else {

                    }
                    EventBus.getDefault().post("llegaMensaje");
                } else if (accion.equals("AVISOGEN")) {
                    String titulo = valores.getElementsByTagName("titulo").item(0).getTextContent();
                    String mensajePopUp = valores.getElementsByTagName("mensajePopUp").item(0).getTextContent();
                    String idAfiliadoMensaje = valores.getElementsByTagName("idAfiliadoMensaje").item(0).getTextContent();
                    String tipoContenido = valores.getElementsByTagName("tipoContenido").item(0).getTextContent();
                    String contenido = valores.getElementsByTagName("contenido").item(0).getTextContent();
                    String idEnvio = valores.getElementsByTagName("idEnvio").item(0).getTextContent();
                    String fecMsg = valores.getElementsByTagName("fecMsg").item(0).getTextContent();

                    SQLController database = new SQLController(this);
                    database.agregarAviso(idEnvio);
                    String idAfiliadoLoc = database.obtenerIDAfiliado();
                    if (idAfiliadoLoc.equals(idAfiliadoMensaje)) {
                        database.agregarMensajeAvisoGen(tipoContenido, contenido, idEnvio, "", fecMsg);
                        muestraNotificacion("AVISOGEN", titulo, mensajePopUp, null, fecMsg, null);
                    }
                    EventBus.getDefault().post("llegaMensaje");
                }
            } catch (Exception err) {
                //Se recibe un mensaje que no tiene el elemento "accion"
                String a = "";
                a = "1";
            }
        }
    }

    private void muestraNotificacion(String Accion, String titulo, String mensajePopUp, String id
            , String idGeneral, String idGeneral1) {
        String[] separated = mensajePopUp.split("!:!");
        Bitmap icon1;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Accion.equals("AUTORDENAMB")
                || Accion.equals("RECHAZOORDENAMB")) {
            icon1 = BitmapFactory.decodeResource(getResources(), R.drawable.orden_notificacion);
        } else if (Accion.equals("ACTAPPSINDOCRECHAZO")) {
            icon1 = BitmapFactory.decodeResource(getResources(), R.drawable.andeslogo);
        } else {
            icon1 = BitmapFactory.decodeResource(getResources(), R.drawable.andeslogo);
        }
        Uri alarmSound = Uri.parse("android.resource://ar.com.andessalud.andes/" + R.raw.alarma_as);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle(titulo);

        // Moves events into the big view
        for (String s : separated) {

            inboxStyle.addLine(s);
        }

        if (Accion.equals("ACTAPPSINDOC")) {
            notificationIntent = new Intent(this, soy_afiliado_app_activada.class);
        } else if (Accion.equals("ACTAPPSINDOCRECHAZO")) {
            notificationIntent = new Intent(this, soy_afiliado_sin_dni_rechazado.class);
        } else {
            notificationIntent = new Intent(this, principal.class);
            if (Accion.equals("AUTORDENAMB")
                    || Accion.equals("RECHAZOORDENAMB")) {
                notificationIntent.putExtra("pantalla", "buzon");
            }

        }

        intent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create channel in which to send push notifications
        String CHANNEL_ID = "my_channel_01";

        // only use NotificationChannel when Api Level >= 26
        if (Build.VERSION.SDK_INT >= 26) {
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }


        Notification notify = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(titulo)
                .setContentText(mensajePopUp)
                .setSmallIcon(R.drawable.marca_negativa)
                //.setBadgeIconType(R.drawable.marca_negativa)
                .setLargeIcon(icon1)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setColor(getResources().getColor(R.color.colorNotificacion))
                .setContentIntent(intent)
                .build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notify);


    }

    private void foregroundDisplay(String titles, String bodys, String eventId,String doctorno) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap icon1 = BitmapFactory.decodeResource(getResources(), R.drawable.andeslogo);
        Uri alarmSound = Uri.parse("android.resource://ar.com.andessalud.andes/" + R.raw.alarma_as);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        Intent notificationIntent = new Intent(this, principal.class);
        notificationIntent.putExtra("das", eventId);
        notificationIntent.putExtra("medicalprescription", doctorno);

//        notificationIntent.putExtra("join_appointmentcall",doctorno);
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            intent.pu
        //Create channel in which to send push notifications
        String CHANNEL_ID = "my_channel_01";

        // only use NotificationChannel when Api Level >= 26
        if (Build.VERSION.SDK_INT >= 26) {
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }


        Notification notify = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(titles)
                .setContentText(bodys)
                .setSmallIcon(R.drawable.marca_negativa)
                //.setBadgeIconType(R.drawable.marca_negativa)
                .setLargeIcon(icon1)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setColor(getResources().getColor(R.color.colorNotificacion))
                .setContentIntent(intent)
                .setAutoCancel(true)
                .build();

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notify);

    }


}