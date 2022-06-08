package ar.com.andessalud.andes;

import android.text.TextUtils;

import com.google.android.gms.tasks.Task;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ar.com.andessalud.andes.fragmentos.fragmento_contact_center;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class signalR {
    static HubConnection hubConnection;
    static String mensajeSignalR="";
    static boolean buscandoIdSignalR=false;

    public static String conectarSignalR(){
        hubConnection = HubConnectionBuilder.create("https://andessalud.com.ar/hubsignal/notify")
                .build();
        hubConnection.on("recibirMensaje",(String idUsuarioOrigen, String[] dtMensaje)->{
            if (dtMensaje[0].toString().equals("VOZLlamadaCanceladaSinAtender")){
                fragmento_contact_center frgContactCenter=new fragmento_contact_center();
                frgContactCenter.llamadaSinAtender();
            }
        },String.class, String[].class);
        hubConnection.start().blockingAwait();
        return "Conecto";
    }

    public static String obtenerIdSignalR(){
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

    public static void buscarIdSignalR(){
        hubConnection.invoke(String.class,"getConnectionId")
                .subscribe(new SingleObserver<String>() {
                    public void onSubscribe(Disposable d) {
                    }
                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            mensajeSignalR =  result;
                        } else {
                            mensajeSignalR="ERROR: No se pudo conectar al servidor del contact center";
                        }
                    }
                    public void onError(Throwable e) {
                        mensajeSignalR="ERROR: Error al inicializar la conexión con el contact center";
                    }
                });
    }

    public static void enviarMensaje(String idDestino, String[] mensajes){
        hubConnection.send("enviarMensaje","", idDestino, mensajes);
    }
}
