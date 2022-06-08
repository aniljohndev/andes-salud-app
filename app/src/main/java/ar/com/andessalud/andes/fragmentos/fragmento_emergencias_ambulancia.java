package ar.com.andessalud.andes.fragmentos;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.fabricas.gifImageView;
import ar.com.andessalud.andes.funciones;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_emergencias_ambulancia extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo;

    LinearLayout loadingData, pantContenido, lnProvincia, lnProvinciaResultados, listaProvincias
            , listaTelefonos;
    TextView lblProvinciaSeleccionado;
    float ultimoClick = 0, tiempoMulticlick = 1000;

    public fragmento_emergencias_ambulancia() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_emergencias_ambulancia, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingData= (LinearLayout) view.findViewById(R.id.loadingData);
        pantContenido= (LinearLayout) view.findViewById(R.id.pantContenido);

        //#region seleccionar provincia
        lnProvincia = (LinearLayout) view.findViewById(R.id.lnProvincia);
        lnProvincia.setVisibility(View.VISIBLE);
        lnProvinciaResultados = (LinearLayout) view.findViewById(R.id.lnProvinciaResultados);
        lnProvinciaResultados.setVisibility(View.GONE);
        lblProvinciaSeleccionado = (TextView) view.findViewById(R.id.lblProvinciaSeleccionado);
        listaProvincias = (LinearLayout) view.findViewById(R.id.listaProvincias);
        listaProvincias.setVisibility(View.GONE);

        lnProvincia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickProvincia();
            }
        });

        lnProvinciaResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickProvincia();
            }
        });
        //#endregion

        ImageView imageView = view.findViewById(R.id.imgCargando);
        /*load from raw folder*/
        Glide.with(this).load(R.drawable.loading).into(imageView);

        pantContenido.setVisibility(View.GONE);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        iniciarPantalla();
                    }
                },
        1000);
    }

    private void iniciarPantalla(){
        actualizarEmergencias();
    }
    //#endregion

    private void clickProvincia(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnProvincia.setVisibility(View.VISIBLE);
        lnProvinciaResultados.setVisibility(View.GONE);
        lblProvinciaSeleccionado.setText("Seleccionar Provincia");
        listaProvincias.setVisibility(View.GONE);
        buscarZonas();
    }

    private void actualizarEmergencias(){
        try{
            Map<String, String> params = new HashMap<String, String>();
            params.put("codAdmin", "AND");
            params.put("lat", "0");
            params.put("lon", "0");

            fabrica_WS.APPObtieneEmergencias(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    loadingData.setVisibility(View.GONE);
                    pantContenido.setVisibility(View.VISIBLE);
                    SQLController database = new SQLController(getContext());
                    database.eliminarZonasEmergencias(getContext());
                    for (int i = 0; i < valores.getElementsByTagName("idZona").getLength(); i++) {
                        String idZonaEMZona = valores.getElementsByTagName("idZona").item(i).getTextContent();
                        String nombreZona = valores.getElementsByTagName("nombreZona").item(i).getTextContent();
                        database.agregarZonasEmergencias(getContext(), idZonaEMZona, nombreZona);
                    }

                    for (int i = 0; i < valores.getElementsByTagName("idEmergencia").getLength(); i++) {
                        String idEmergencia = valores.getElementsByTagName("idEmergencia").item(i).getTextContent();
                        String nombreConvenio = valores.getElementsByTagName("nombreEmergencia").item(i).getTextContent();
                        database.actualizarEmergencia(getContext(), idEmergencia, nombreConvenio);
                    }
                    database.eliminarEmergenciaDeZonas(getContext());
                    for (int i = 0; i < valores.getElementsByTagName("idEmergenciaZona").getLength(); i++) {
                        String  idEmergenciaGAPrest = valores.getElementsByTagName("idEmergenciaZona").item(i).getTextContent();
                        String idZonaGAPrest = valores.getElementsByTagName("idZonaEmergencia").item(i).getTextContent();
                        database.agregarEmergenciaAZona(getContext(), idEmergenciaGAPrest, idZonaGAPrest);
                    }

                    String idZonaAnt="";
                    for (int i = 0; i < valores.getElementsByTagName("idEmergenciaTel").getLength(); i++) {
                        String idEmergencia = valores.getElementsByTagName("idEmergenciaTel").item(i).getTextContent();
                        String idZonaTel = valores.getElementsByTagName("idZonaTel").item(i).getTextContent();
                        String telefono = valores.getElementsByTagName("telefono").item(i).getTextContent();

                        if (!idZonaAnt.equals(idZonaTel)){
                            database.eliminarTelefonosEmergencia(getContext(), idZonaTel);
                            idZonaAnt=idZonaTel;
                        }
                        database.agregarTelefonoEmergencia(getContext(), idEmergencia, idZonaTel, telefono);
                    }

                    String fecActualizacion = valores.getElementsByTagName("fechaAct").item(0).getTextContent();
                    database.actualizarParametro(getContext(), "ultFechaDatosEmergencia", fecActualizacion);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    loadingData.setVisibility(View.GONE);
                    pantContenido.setVisibility(View.VISIBLE);
                }
            });
        }catch (Exception e) {
            //dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al confirmar la orden de guardia:\n" + e.getMessage());
            loadingData.setVisibility(View.GONE);
            pantContenido.setVisibility(View.VISIBLE);
        }
    }

    //region buscar zonas
    private void buscarZonas(){
        try {
            SQLController database = new SQLController(getContext());
            Cursor zonasEmergencias = database.leerZonasEmergencias(getContext());

            if (zonasEmergencias!= null && zonasEmergencias.getCount()>0) {
                lnProvincia.setVisibility(View.GONE);
                lnProvinciaResultados.setVisibility(View.VISIBLE);
                listaProvincias.removeAllViews();
                for (int i = 0; i < zonasEmergencias.getCount(); i++) {
                    LayoutInflater inflater = this.getLayoutInflater();
                    View lnMacroZonas = inflater.inflate(R.layout.lista_un_renglon_tilde, null);

                    final String idMacroZonaLocal = zonasEmergencias.getString(0);
                    final String nombreMacroZonaLocal = zonasEmergencias.getString(1);

                    TextView idFila = (TextView) lnMacroZonas.findViewById(R.id.idFila);
                    TextView linea1 = (TextView) lnMacroZonas.findViewById(R.id.linea1);

                    idFila.setText(idMacroZonaLocal);
                    linea1.setText(nombreMacroZonaLocal);

                    lnMacroZonas.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buscarEmergencias(idMacroZonaLocal, nombreMacroZonaLocal);
                        }
                    });

                    listaProvincias.addView(lnMacroZonas);
                    zonasEmergencias.moveToNext();
                }
                listaProvincias.setVisibility(View.VISIBLE);
            }else{
                dialogo = fabrica_dialogos.dlgError(getContext(), "No hay provincias para mostrar");
            }
        }catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar la zona:\n" + e.getMessage());
        }
    }

    //region buscar emergencias en zona
    private void buscarEmergencias(String _idZona, String _nombreZona){
        try {
            SQLController database = new SQLController(getContext());
            Cursor cursor = database.leerEmergenciasPorZona(getContext(), _idZona);// db.rawQuery("SELECT _idPractica as _id, estado FROM practicas", null);
            lblProvinciaSeleccionado.setText(_nombreZona);
            lnProvincia.setVisibility(View.GONE);
            lnProvinciaResultados.setVisibility(View.VISIBLE);

            if (cursor.getCount() > 0) {
                listaProvincias.removeAllViews();
                for (int i = 0; i < cursor.getCount(); i++) {
                    LayoutInflater inflater = this.getLayoutInflater();
                    View lnEmergencia = inflater.inflate(R.layout.lista_tres_renglones_tilde, null);

                    TextView idFila = (TextView) lnEmergencia.findViewById(R.id.idFila);
                    TextView linea1 = (TextView) lnEmergencia.findViewById(R.id.linea1);
                    TextView linea2 = (TextView) lnEmergencia.findViewById(R.id.linea2);
                    TextView linea3 = (TextView) lnEmergencia.findViewById(R.id.linea3);

                    final String _idActual = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
                    final String _idZonaEmergencia = cursor.getString(cursor.getColumnIndexOrThrow("_idZona"));
                    final String _nombreEmergencia = cursor.getString(cursor.getColumnIndexOrThrow("nombreEmergencia"));

                    idFila.setText(_idActual);
                    linea1.setText(_nombreEmergencia);
                    linea3.setVisibility(View.GONE);

                    Cursor cursorTel = database.leerTelefonosEmergencia(getContext(), _idActual, _idZona);
                    if (cursorTel!=null && cursorTel.getCount()>0){
                        String telefonos="";
                        for (int j = 0; j < cursorTel.getCount(); j++) {
                            telefonos=telefonos+cursorTel.getString(1);
                            if (!(i==(cursorTel.getCount()-1))){
                                telefonos=telefonos+" / ";
                            }
                            cursorTel.moveToNext();
                        }
                        linea2.setText(telefonos);
                    }

                    lnEmergencia.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            llamarEmergencias(_idActual, _idZonaEmergencia, _nombreEmergencia);
                        }
                    });

                    listaProvincias.addView(lnEmergencia);
                    cursor.moveToNext();
                }
                listaProvincias.setVisibility(View.VISIBLE);
            } else {
                listaProvincias.setVisibility(View.GONE);
                //lblNoUrgencias.setVisibility(View.VISIBLE);
            }
        }catch (Exception err){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar las emergencias de la zona:\n"+err.getMessage());
        }
    }

    private void llamarEmergencias(String _idActual, String _idZona, String nombreEmergencia){
        SQLController database = new SQLController(getContext());
        Cursor cursorTel = database.leerTelefonosEmergencia(getContext(), _idActual, _idZona);
        if (cursorTel!=null && cursorTel.getCount()>0){
            if (cursorTel.getCount()==1){
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", cursorTel.getString(1), null));
                if (funciones.soportaLlamados(getContext(),intent)){
                    getContext().startActivity(intent);
                }else {
                    dialogo = fabrica_dialogos.dlgError(getContext(), "Este dispositvo no soporta llamados telefónicos");
                }
            } else{
                dialogo = fabrica_dialogos.dlgMuestraTelefonosMarcar(getContext(), nombreEmergencia, R.drawable.ambulance_side);
                listaTelefonos = (LinearLayout) dialogo.findViewById(R.id.lnLista);
                listaTelefonos.removeAllViews();

                for (int i = 0; i < cursorTel.getCount(); i++) {
                    LayoutInflater inflater = this.getLayoutInflater();
                    View lnEmergencia = inflater.inflate(R.layout.fila_lista_telefonos_llamar, null);

                    TextView txtTexto = (TextView) lnEmergencia.findViewById(R.id.txtTexto);
                    LinearLayout lnTelefono = (LinearLayout) lnEmergencia.findViewById(R.id.lnTelefono);

                    final String numTel=cursorTel.getString(1);
                    txtTexto.setText(numTel);

                    lnEmergencia.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", numTel, null));
                            if (funciones.soportaLlamados(getContext(),intent)){
                                getContext().startActivity(intent);
                            }else {
                                dialogo.dismiss();
                                dialogo = fabrica_dialogos.dlgError(getContext(), "Este dispositvo no soporta llamados telefónicos");
                            }
                        }
                    });
                    listaTelefonos.addView(lnEmergencia);
                    cursorTel.moveToNext();
                }
            }
        }
    }
    //endregion

    @Override
    public void onAttach (Context context){
        super.onAttach(context);
        tiempoMulticlick=Float.parseFloat(getString(R.string.tiempoMulticlick));
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
