package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.principal;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class fragmento_dr_online_sala_espera extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo, dialogoDino;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    private String idSalaEspera="", msgSignalR="";//, idDrOnlineSignalR="";
    private String[] idDrOnlineSignalR;
    Handler mHandler=new android.os.Handler();
    ImageView btnCancelarAtencion, btnJuego;
    private boolean cancelando=false;
    public fragmento_dr_online_sala_espera() {
        // Required empty public constructor
    }
    HubConnection hubConnection;
    static String mensajeSignalR="";
    static boolean buscandoIdSignalR=false;
    principal pantPrincipal;
    TextView lblProfesional, lblEstado, txtMotivo, lblMotivo, txtCantPacientes, lblCantPacientes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_dr_online_sala_espera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null) {
            idSalaEspera = extras.getString("idSalaEspera", "");
            idDrOnlineSignalR= extras.getStringArray("idDrOnlineSignalR");
        }

        btnJuego = (ImageView) view.findViewById(R.id.btnJuego);
        btnJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoDino = fabrica_dialogos.dlgDinoGame(getContext());
            }
        });

        btnCancelarAtencion = (ImageView) view.findViewById(R.id.btnCancelarAtencion);
        btnCancelarAtencion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelando=true;
                cancelarAtencion();
            }
        });
        conectarSignalR();
        pantPrincipal = (principal) getActivity();
        pantPrincipal.cambiaHaySalaEsperaActivaDrOnline(false);
        lblProfesional= (TextView) view.findViewById(R.id.lblProfesional);
        lblEstado= (TextView) view.findViewById(R.id.lblEstado);
        txtMotivo= (TextView) view.findViewById(R.id.txtMotivo);
        lblMotivo= (TextView) view.findViewById(R.id.lblMotivo);
        txtCantPacientes= (TextView) view.findViewById(R.id.txtCantPacientes);
        lblCantPacientes= (TextView) view.findViewById(R.id.lblCantPacientes);

        lblProfesional.setVisibility(View.GONE);
        lblEstado.setVisibility(View.GONE);
        txtMotivo.setVisibility(View.GONE);
        lblMotivo.setVisibility(View.GONE);
        txtCantPacientes.setVisibility(View.GONE);
        lblCantPacientes.setVisibility(View.GONE);
        pantPrincipal.estaEnSalaEspera="SI";
    }

    private void conectarSignalR(){
        msgSignalR=conectarHubSignalR();
        if (msgSignalR.equals("NOConecto")){
            if (dialogo==null
                || !dialogo.isShowing()){
                dialogo = fabrica_dialogos.dlgError(getContext(), "No se puede ingresar a la sala de espera por error de conexión.");
            }
            return;
        }
        msgSignalR=obtenerIdSignalR();
        if (msgSignalR.substring(0,6).equals("ERROR:")){
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), msgSignalR.substring(7));
            return;
        }
        if (!cancelando) {
            refrescaSalaEspera();
            actualizaSalaEspera();
            if (!idDrOnlineSignalR[0].equals("NoPrestador")){
                for (int i = 0; i < idDrOnlineSignalR.length; i++) {
                    String[] mensaje=new String[6];
                    mensaje[0]="SalaEsperaNuevoPaciente";
                    enviarMensaje(idDrOnlineSignalR[i],mensaje);
                }
            }
        }
    }

    //#region signalR
    public String conectarHubSignalR(){
        if (hubConnection!=null){
            return "Conecto";
        }
        try {
            hubConnection = HubConnectionBuilder.create("https://andessalud.com.ar/hubsignal/notify")
                    .build();
            hubConnection.on("recibirMensaje", (String idUsuarioOrigen, String[] dtMensaje) -> {
                if (dtMensaje[0].toString().equals("AtencionPaciente")) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (dialogoDino != null) {
                                dialogoDino.dismiss();
                            }
                            pantPrincipal.idSubKey = dtMensaje[1].toString();
                            pantPrincipal.idPubKey = dtMensaje[2].toString();
                            pantPrincipal.idDrOnlineSignalRPrestador = dtMensaje[3].toString();
                            pantPrincipal.cambiaHaySalaEsperaActivaDrOnline(false);
                            pantPrincipal.estaEnSalaEspera = "NO";
                            trigger.fireChange("drOnline_atiendePaciente");
                        }
                    });
                } else if (dtMensaje[0].toString().equals("CambiaEstadoConsultorio")) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            actualizaSalaEspera();
                        }
                    });
                }/*else if (dtMensaje[0].toString().equals("LlamadadaVozAtendida")){
                    idLlamadaActual=dtMensaje[1];1
                    nombrePersonaAtiende=dtMensaje[2];
                    idAPPKey=dtMensaje[3];
                    idSubKey=dtMensaje[4];
                    idUsuarioRecibe=dtMensaje[5];
                    idUsuarioLlama=dtMensaje[6];
                    idSignalRDestino=dtMensaje[7];
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mp.stop();
                            llamadaAtendida();
                        }
                    });
                }*/
            }, String.class, String[].class);
            hubConnection.start().blockingAwait();
            return "Conecto";
        }catch (Exception ex){
            return "NOConecto";
        }
    }

    public String obtenerIdSignalR(){
        mensajeSignalR="";
        buscandoIdSignalR=false;
        try{
            while (mensajeSignalR.equals("")){
                if (buscandoIdSignalR==false){
                    buscandoIdSignalR=true;
                    buscarIdSignalR();
                }
                Thread.sleep(300);
            }

        }catch (Exception ex){

        }
        return mensajeSignalR;
    }

    public void buscarIdSignalR(){
        hubConnection.invoke(String.class,"getConnectionId")
                .subscribe(new SingleObserver<String>() {
                    public void onSubscribe(Disposable d) {
                    }
                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            mensajeSignalR =  result;
                        } else {
                            mensajeSignalR="ERROR: No se pudo conectar al servidor de Dr. Online";
                        }
                    }
                    public void onError(Throwable e) {
                        mensajeSignalR="ERROR: Error al inicializar la conexión con el Dr. Online";
                    }
                });
    }

    public void enviarMensaje(String idDestino, String[] mensajes){
        hubConnection.send("enviarMensaje","", idDestino, mensajes);
    }
    //#endregion

    private void actualizaSalaEspera(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("idSalaEspera", idSalaEspera);

        fabrica_WS.APPDrOnlineActualizaSalaEspera(getContext(), params, new SuccessResponseHandler<Document>() {
            @Override
            public void onSuccess(Document valores) {
                lblProfesional.setText(valores.getElementsByTagName("profesional").item(0).getTextContent());
                lblEstado.setText(valores.getElementsByTagName("estado").item(0).getTextContent());
                lblCantPacientes.setText(valores.getElementsByTagName("cuentaPacientes").item(0).getTextContent());
                lblMotivo.setText(valores.getElementsByTagName("motivo").item(0).getTextContent());

                lblProfesional.setVisibility(View.VISIBLE);
                lblEstado.setVisibility(View.VISIBLE);
                lblCantPacientes.setVisibility(View.VISIBLE);
                txtCantPacientes.setVisibility(View.VISIBLE);
                txtMotivo.setVisibility(View.GONE);
                lblMotivo.setVisibility(View.GONE);

                if (!lblMotivo.getText().equals("--")){
                    lblMotivo.setVisibility(View.VISIBLE);
                    txtMotivo.setVisibility(View.VISIBLE);
                    lblCantPacientes.setVisibility(View.GONE);
                    txtCantPacientes.setVisibility(View.GONE);
                }
            }
        }, new ErrorResponseHandler() {
            @Override
            public void onError(String msg) {
                if (dialogo==null
                    || !dialogo.isShowing()){
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                    pantPrincipal.estaEnSalaEspera="NO";
                    pantPrincipal.cambiaHaySalaEsperaActivaDrOnline(false);
                    trigger.fireChange("drOnline_cancelaSalaEspera");
                }
            }
        });
    }

    private void refrescaSalaEspera(){
        if (hubConnection.getConnectionState()== HubConnectionState.DISCONNECTED){
            hubConnection=null;
            msgSignalR=conectarHubSignalR();
            msgSignalR=obtenerIdSignalR();
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("idSalaEspera", idSalaEspera);
        params.put("idSignal", msgSignalR);

        fabrica_WS.APPDrOnlineRefrescaSalaEspera(getContext(), params, new SuccessResponseHandler<Document>() {
            @Override
            public void onSuccess(Document valores) {
                Handler handler1 = new MyHandler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!cancelando) {
                            refrescaSalaEspera();
                            actualizaSalaEspera();
                        }
                    }
                }, 15000);
            }
        }, new ErrorResponseHandler() {
            @Override
            public void onError(String msg) {
                if (dialogo==null
                    || !dialogo.isShowing()){
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                    pantPrincipal.estaEnSalaEspera="NO";
                    pantPrincipal.cambiaHaySalaEsperaActivaDrOnline(false);
                    trigger.fireChange("drOnline_cancelaSalaEspera");
                }
            }
        });
    }

    private void cancelarAtencion(){
        SQLController database = new SQLController(getContext());
        final String idAfiliadoTitular = database.obtenerIDAfiliado();
        if (idAfiliadoTitular.equals("")) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
            return;
        }

        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Cancelando la solicitud de antención");

        Map<String, String> params = new HashMap<String, String>();
        params.put("idSalaEspera", idSalaEspera);
        params.put("idUsuarioCancela", idAfiliadoTitular);

        fabrica_WS.APPDrOnlineCancelaSalaEspera(getContext(), params, new SuccessResponseHandler<Document>() {
            @Override
            public void onSuccess(Document valores) {
                if (!idDrOnlineSignalR[0].equals("NoPrestador")){
                    for (int i = 0; i < idDrOnlineSignalR.length; i++) {
                        String[] mensaje=new String[6];
                        mensaje[0]="SalaEsperaCancelaPaciente";
                        try {
                            enviarMensaje(idDrOnlineSignalR[i], mensaje);
                        }catch (Exception errEx) {
                            errEx.printStackTrace();
                            //no hace nada
                        }
                    }
                }
                dialogo.dismiss();
                pantPrincipal.estaEnSalaEspera="NO";
                pantPrincipal.cambiaHaySalaEsperaActivaDrOnline(false);
                trigger.fireChange("drOnline_cancelaSalaEspera");
            }
        }, new ErrorResponseHandler() {
            @Override
            public void onError(String msg) {
                dialogo.dismiss();
                dialogo = fabrica_dialogos.dlgError(getContext(), msg);
            }
        });
    }

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //pantPrincipal = (principal) getActivity();
        if (pantPrincipal.estaEnSalaEspera.equals("SI")){
            pantPrincipal.cambiaHaySalaEsperaActivaDrOnline(true);
        }else{
            pantPrincipal.cambiaHaySalaEsperaActivaDrOnline(false);
        }
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        cancelarAtencion();
    }*/

    @Override
    public void onAttach (Context context){
        super.onAttach(context);
        tiempoMulticlick=Float.parseFloat(getString(R.string.tiempoMulticlick));
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}