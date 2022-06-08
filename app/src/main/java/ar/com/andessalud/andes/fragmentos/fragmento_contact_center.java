package ar.com.andessalud.andes.fragmentos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
//import ar.com.andessalud.andes.twilio.CreateUserModel;
import ar.com.andessalud.andes.twilio.MySingleton;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.principal;
import ar.com.andessalud.andes.twilio.VideoActivity;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.util.ArrayUtils;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import com.twilio.rest.monitor.v1.Alert;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_contact_center extends Fragment {

    //    OnSendingFragmentToVideo onSendingFragmentToVideo;
    FragmentChangeTrigger trigger;
    ImageView btnReclamoSugerencia, btnLlamadaVoz, btnVideoLlamada, btnChat, btnPregFrecuentes, btnChateaNosotros, btnYoutube;
    String idHubSignalR = "", errorSignalR = "";
    HubConnection hubConnection;
    private Dialog dialogo;
    //private boolean obteniedoIdSignalR=false;
    //Timer timer = new Timer();
    String msgSignalR, tipoLlamada = "", idLlamadaActual = "", nombrePersonaAtiende = "", idAPPKey = "", idSubKey = "", idUsuarioRecibe = "", idUsuarioLlama = "", idSignalRDestino = "", nombreOrigen = "";
    static String mensajeSignalR = "";
    static boolean buscandoIdSignalR = false;
    Handler mHandler = new android.os.Handler();
    MediaPlayer mp = new MediaPlayer();
    principal pantPrincipal;
    SharedPreferences appPref;
    public String idAfiliadoRegistro;
    public static JSONObject sendingJson;
    public Long cuilValue;

    public fragmento_contact_center() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragmento_contact_center, container, false);
        sendingJson = new JSONObject();

        appPref = Objects.requireNonNull(getActivity()).getSharedPreferences("datosAplicacion", MODE_PRIVATE);
//        prefs = appPref.getSharedPreferences("datosAplicacion", MODE_PRIVATE);
        idAfiliadoRegistro = appPref.getString("idAfiliadoRegistro", "");
        try {
            sendingJson.put("device_token", appPref.getString("senderid", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d("devi",sendingJson.get("device_token"));

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pantPrincipal = (principal) getActivity();
        btnReclamoSugerencia = (ImageView) view.findViewById(R.id.btnReclamoSugerencia);
        btnReclamoSugerencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trigger.fireChange("contactCenter_btnReclamoSugerencia");
            }
        });
        btnReclamoSugerencia.setVisibility(View.GONE);

        btnLlamadaVoz = (ImageView) view.findViewById(R.id.btnLlamadaVoz);
        btnLlamadaVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pantPrincipal.estadoLlamadaActiva()) {
                    pantPrincipal.muestraPantallaLamada();
                    return;
                }
                tipoLlamada = "voz";
                iniciaLlamada();
            }
        });
        btnLlamadaVoz.setVisibility(View.GONE);

        btnVideoLlamada = (ImageView) view.findViewById(R.id.btnVideoLlamada);
        btnVideoLlamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                Log.d("currentDateTime", currentDateTimeString.substring(11,13));
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                String days = String.valueOf(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime()));
                Log.d("currentDateTime1",   " "+ days);

                dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Espere por favor...");
                int time = Integer.parseInt(currentDateTimeString.substring(11,13));
                if(!days.equals("Saturday") && !days.equals("Sunday")){

                    if (time > 9 && time < 17){
                        new CountDownTimer(3000, 1000) {

                            public void onTick(long millisUntilFinished) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                                //here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
//                mTextField.setText("done!");
                                try {
                                    dialogo.dismiss();
                                    Intent intentforsend = new Intent(getContext(), VideoActivity.class);
                                    intentforsend.putExtra("connectMobileToWeb","connectedcall");
                                    startActivityForResult(intentforsend,100);
                                    pantPrincipal.overridePendingTransition(R.anim.entrar_arriba, R.anim.entrar_de_abajo);
                                     }
                                catch (Exception ignored)
                                {

                                }

                            }

                        }.start();

                    }
                    else {
                        Log.d("currentDateTime","yes");
                        dialogo.dismiss();
                        dialogo = fabrica_dialogos.dlgInfo(getContext(),"El horario de nuestra oficina es de 9:00 a. M. A 17:00 p. M. De lunes a viernes.");
                    }

                }


//                CollectData(idAfiliadoRegistro);
//                onSendingFragmentToVideo.videoFragmentToActivity("callingfrommain");

//                buscarDatosAfiliacion();
//              Log.d("cuilvalue",""+cuilValue);

                //                startActivity(NE);
            }
        });
//        btnVideoLlamada.setVisibility(View.GONE);

        btnChat = (ImageView) view.findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
        btnChat.setVisibility(View.GONE);

        btnPregFrecuentes = (ImageView) view.findViewById(R.id.btnPregFrecuentes);
        btnPregFrecuentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trigger.fireChange("contactCenter_btnPreguntasFrecuentes");
            }
        });

        btnChateaNosotros = (ImageView) view.findViewById(R.id.btnChateaNosotros);
        btnChateaNosotros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=(+542613300622";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        btnYoutube = (ImageView) view.findViewById(R.id.btnYoutube);
        btnYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://youtube.com/playlist?list=PLNZHpsw2sJcBTfxOS0M10aGZTMGNVZCus"));
                try {
                    startActivity(webIntent);
                } catch (ActivityNotFoundException ex) {
                }
            }
        });
    }

    private void iniciaLlamada() {
        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Iniciando la llamada de " + tipoLlamada);
        if (hubConnection != null
                && hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            hacerLlamada();
        } else {
            mHandler.postDelayed(
                    new Runnable() {
                        public void run() {
                            conectarSignalR();
                        }
                    },
                    300);
        }
    }

    private void conectarSignalR() {
        msgSignalR = conectarHubSignalR();
        msgSignalR = obtenerIdSignalR();
        if (msgSignalR.substring(0, 6).equals("ERROR:")) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), msgSignalR.substring(7));
            return;
        }
        hacerLlamada();
    }

    private void hacerLlamada() {
        SQLController database = new SQLController(getContext());
        final String idAfiliadoTitular = database.obtenerIDAfiliado();
        if (idAfiliadoTitular.equals("")) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("codAdministradora", "DOCTORDO");
        params.put("idUsuario", idAfiliadoTitular);
        params.put("idConexionSignal", msgSignalR);
        params.put("tipoLlamada", tipoLlamada);

        fabrica_WS.WEBGENCentroComunicacionesLlamar(getActivity(), params, new SuccessResponseHandler<Document>() {
            @Override
            public void onSuccess(Document valores) {
                TextView lblMensaje = (TextView) dialogo.findViewById(R.id.lblMsg);
                lblMensaje.setText("Avisando a los operadores");
                mp = MediaPlayer.create(getContext(), R.raw.llamada_saliente);
                mp.setLooping(true);
                mp.start();
                idLlamadaActual = valores.getElementsByTagName("idLlamadaActual").item(0).getTextContent();
                nombreOrigen = valores.getElementsByTagName("nombreOrigen").item(0).getTextContent();
                //String[] SignalRConnection = new String[valores.getElementsByTagName("SignalRConnection").getLength()];
                String[] mensaje = new String[6];
                mensaje[0] = "LlamadaVOZ";
                mensaje[1] = idAfiliadoTitular;
                mensaje[2] = msgSignalR;
                mensaje[3] = idLlamadaActual;
                mensaje[4] = tipoLlamada;
                mensaje[5] = nombreOrigen;
                for (int i = 0; i < valores.getElementsByTagName("SignalRConnection").getLength(); i++) {
                    /*signalR.enviarMensaje(valores.getElementsByTagName("SignalRConnection").item(i).getTextContent()
                        ,mensaje);*/
                    enviarMensaje(valores.getElementsByTagName("SignalRConnection").item(i).getTextContent()
                            , mensaje);
                }
            }
        }, new ErrorResponseHandler() {
            @Override
            public void onError(String msg) {
                dialogo.dismiss();
                dialogo = fabrica_dialogos.dlgError(getContext(), msg);
            }
        });
    }
//    private void obtenerSignalRId(){
//        //hubConnection.invoke(String.class, "GetConnectionId");
//        dialogo.dismiss();
//        dialogo = fabrica_dialogos.dlgError(getContext(), errorSignalR);
//    }
//
//    private void conectarSignalR(){
//        obteniedoIdSignalR=true;
//        hubConnection = HubConnectionBuilder.create("https://andessalud.com.ar/hubsignal/notify")
//                .build();
//        hubConnection.on("recibirMensaje",(mensaje)->{
//            System.out.println("New Message: " + mensaje);
//        },String.class);
//        hubConnection.start().blockingAwait();
//        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
//            hubConnection.invoke(String.class,"getConnectionId")
//                    .subscribe(new SingleObserver<String>() {
//                        public void onSubscribe(Disposable d) {
//                        }
//                        public void onSuccess(String result) {
//                            if (!TextUtils.isEmpty(result)) {
//                                idHubSignalR=result;
//                            } else {
//                                errorSignalR="No se pudo conectar al servidor del contact center";
//                            }
//                        }
//                        public void onError(Throwable e) {
//                            errorSignalR="Error al inicializar la conexión con el contact center";
//                        }
//                    });
//        }
//
//
//
//        /*Single valor;
//        valor = hubConnection.invoke(String.class,"getConnectionId");
//        hubConnection.send("getConnectionId");
//        this.idHubSignalR=((SingleSubject) valor).getValue().toString();
//        Toast.makeText(getActivity(), this.idHubSignalR,Toast.LENGTH_LONG).show();*/
//    }
//
//    private void muestraErrorSignalR(){
//        dialogo.dismiss();
//        dialogo = fabrica_dialogos.dlgError(getContext(), errorSignalR);
//    }
//
//    private void contactarOperadores(){
//        dialogo.dismiss();
//        dialogo = fabrica_dialogos.dlgError(getActivity(), "Llego"+idHubSignalR);
//    }

    public void llamadaSinAtender() {
        dialogo.dismiss();
        dialogo = fabrica_dialogos.dlgError(getContext(), "Su llamada no puede ser atendida en estos momentos, por favor reintente " +
                "mas tarde.");
        //dialogo.show();
    }

    private void llamadaAtendida() {
        dialogo.dismiss();
        pantPrincipal.idLlamadaActual = idLlamadaActual;
        pantPrincipal.nombrePersonaAtiende = nombrePersonaAtiende;
        pantPrincipal.idAPPKey = idAPPKey;
        pantPrincipal.idSubKey = idSubKey;
        pantPrincipal.idUsuarioRecibe = idUsuarioRecibe;
        pantPrincipal.idUsuarioLlama = idUsuarioLlama;
        pantPrincipal.tipoLlamadaActual = tipoLlamada;
        pantPrincipal.idSignalRRecibe = idSignalRDestino;
        trigger.fireChange("contactCenter_seAtendioLlamada");
    }

    //#region signalR
    public String conectarHubSignalR() {
        hubConnection = HubConnectionBuilder.create("https://andessalud.com.ar/hubsignal/notify")
                .build();
        hubConnection.on("recibirMensaje", (String idUsuarioOrigen, String[] dtMensaje) -> {
            if (dtMensaje[0].toString().equals("VOZLlamadaCanceladaSinAtender")) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mp.stop();
                        llamadaSinAtender();
                    }
                });
            } else if (dtMensaje[0].toString().equals("LlamadadaVozAtendida")) {
                idLlamadaActual = dtMensaje[1];
                nombrePersonaAtiende = dtMensaje[2];
                idAPPKey = dtMensaje[3];
                idSubKey = dtMensaje[4];
                idUsuarioRecibe = dtMensaje[5];
                idUsuarioLlama = dtMensaje[6];
                idSignalRDestino = dtMensaje[7];
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mp.stop();
                        llamadaAtendida();
                    }
                });
            }
        }, String.class, String[].class);
        hubConnection.start().blockingAwait();
        return "Conecto";
    }

    public String obtenerIdSignalR() {
        try {
            while (mensajeSignalR.equals("")) {
                if (buscandoIdSignalR == false) {
                    buscandoIdSignalR = true;
                    buscarIdSignalR();
                }
                Thread.sleep(300);
            }

        } catch (Exception ex) {

        }
        return mensajeSignalR;
    }

    public void buscarIdSignalR() {
        hubConnection.invoke(String.class, "getConnectionId")
                .subscribe(new SingleObserver<String>() {
                    public void onSubscribe(Disposable d) {
                    }

                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            mensajeSignalR = result;
                        } else {
                            mensajeSignalR = "ERROR: No se pudo conectar al servidor del contact center";
                        }
                    }

                    public void onError(Throwable e) {
                        mensajeSignalR = "ERROR: Error al inicializar la conexión con el contact center";
                    }
                });
    }

    public void enviarMensaje(String idDestino, String[] mensajes) {
        hubConnection.send("enviarMensaje", "", idDestino, mensajes);
    }
    //#endregion

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }


   /* public void CollectData(String idAfiliado) {
        method1(idAfiliado);
        method2(idAfiliado);

    }

    public void method1(String idAfiliado) {
        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Espere por favor...");

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
                            Log.d("responseHandlerrs", "" + xmlToJson);

                            JSONObject rootObj = xmlToJson.toJson().getJSONObject("root");
                            JSONObject filaObj = rootObj.getJSONObject("fila");
                            JSONObject tablaGrupo = filaObj.getJSONObject("tablaGrupoFam");
                            cuilValue = Long.parseLong(tablaGrupo.getString("CUIL").trim());

//                            sendingJson.put("CUIL",cuil);
                            Log.d("responsw", "" + sendingJson.toString());

                            sendingJson.put("idAfiliado", tablaGrupo.getString("idAfiliado"));
                            Log.d("responsw1", "" + sendingJson.get("idAfiliado"));

                            sendingJson.put("apellNomb", tablaGrupo.getString("apellNomb"));
                            Log.d("responsw2", "" + sendingJson.get("apellNomb"));
                            sendingJson.put("nroAfiliado", tablaGrupo.getString("nroAfiliado"));
                            Log.d("responsss", "" + sendingJson);
                            Log.d("responsw3", "" + sendingJson.get("nroAfiliado"));


                        } catch (Exception ignored) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // Add the request to the RequestQueue.
//    req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//    queue.add(req);

        MySingleton.getInstance(getContext()).addToRequestQueue(req);
    }

    public void method2(String idAfiliado) {

        String URLs = this.getResources().getString(R.string.srvLocProduccion);
        URLs = URLs + "APPObtenerDatosDeContacto";
        Map<String, String> params = new HashMap<String, String>();
        params.put("idAfiliado", idAfiliado);
        StringRequest req = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialogo.dismiss();
                        Log.d("responseInfo", response);
                        try {
                            XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                            Log.d("responseHandlerrs", "" + xmlToJson);
                            JSONObject rootObjs = Objects.requireNonNull(xmlToJson.toJson()).getJSONObject("Resultado");
                            Log.d("checkfila", "" + rootObjs);

                            JSONObject filaObj = rootObjs.getJSONObject("fila");
                            Log.d("csdad", "" + filaObj);
                            JSONObject domicilioObj = filaObj.getJSONObject("domicilio");
                            JSONObject tablaresultado = filaObj.getJSONObject("tablaresultado");
                            Log.d("tabla", "" + tablaresultado);
                            JSONObject mailObj = filaObj.getJSONObject("mail");
                            JSONObject telefonosFijos = filaObj.getJSONObject("telefonosFijos");
                            JSONObject telefonos = filaObj.getJSONObject("telefonos");


                            sendingJson.put("piso", domicilioObj.getString("piso"));
                            sendingJson.put("numero", domicilioObj.getString("numero"));
                            sendingJson.put("calle", domicilioObj.getString("calle"));
                            sendingJson.put("depto", domicilioObj.getString("depto"));
                            sendingJson.put("localidad", domicilioObj.getString("localidad"));
                            sendingJson.put("provincia", domicilioObj.getString("provincia"));
                            sendingJson.put("idLocalidad", domicilioObj.getString("idLocalidad"));

                            Log.d("responsw4", "" + sendingJson);

                            sendingJson.put("numAfiliado", tablaresultado.getString("numAfiliado"));
                            sendingJson.put("estadoFiscalizacionAfiliado", tablaresultado.getString("estadoFiscalizacionAfiliado"));
                            sendingJson.put("valorDevuelto", tablaresultado.getString("valorDevuelto"));
                            sendingJson.put("planPrestacionalAfiliado", tablaresultado.getString("planPrestacionalAfiliado"));
                            sendingJson.put("dniAfiliado", tablaresultado.getString("dniAfiliado"));
                            sendingJson.put("mensaje", tablaresultado.getString("mensaje"));
                            sendingJson.put("nombreAfiliado", tablaresultado.getString("nombreAfiliado"));
                            sendingJson.put("email", mailObj.getString("mail"));
                            sendingJson.put("numTelFijo", telefonosFijos.getString("numTelFijo"));
                            sendingJson.put("caractTelFijo", telefonosFijos.getString("caractTelFijo"));
                            Log.d("c", "" + sendingJson);
                            sendingJson.put("caractTel", telefonos.getString("caractTel"));
                            sendingJson.put("numTel", telefonos.getString("numTel"));
                            Log.d("totalasdf", "" + sendingJson);
                            sendingJson.put("CUIL", cuilValue);
                            Intent intentforsend = new Intent(getContext(), VideoActivity.class);
                            intentforsend.putExtra("connectMobileToWeb","connectedcall");
                            startActivity(intentforsend);
                            pantPrincipal.overridePendingTransition(R.anim.entrar_arriba, R.anim.entrar_de_abajo);



                            Log.d("totalJson", "" + sendingJson);

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
        MySingleton.getInstance(getContext()).addToRequestQueue(req);
    }
*/

//    public interface OnSendingFragmentToVideo {
//        void videoFragmentToActivity(String data);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultcode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultcode, data);
        try{
            if(requestCode == 100)
            {
                String key = data.getStringExtra("missedcalls");
                if(resultcode == 1001 && key!= null && key.equals("callmissed")) {
                    showMyDialouge("No responde","El personal de Andes Salud no responde. Inténtelo más tarde.");
                }
            }

        }
        catch (Exception ignored)
        {

        }

    }
    public void showMyDialouge(String title, String message) {
        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder1.setIcon(R.drawable.andes_logo_24);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "SI",
                (dialog, id) -> {

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }, 1000);

//                    mKProgressHUD.dismiss();

                    dialog.cancel();
                });

        androidx.appcompat.app.AlertDialog alert11 = builder1.create();
        if (alert11.getWindow() != null)
        {
            Window window = alert11.getWindow();
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

            window.setGravity(Gravity.CENTER);
//            alert11.getWindow().setBackgroundDrawableResource(android.R.color.background_dark);
        }
        try {
            alert11.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            alert11.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}





















