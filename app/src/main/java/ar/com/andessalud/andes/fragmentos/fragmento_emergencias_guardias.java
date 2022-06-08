package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import ar.com.andessalud.andes.funciones;


public class fragmento_emergencias_guardias extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo, dialogoSec;

    private LinearLayout lnProvincia, lnProvinciaResultados, lnZona, lnZonaResultados, lnPrestador
            , lnPrestadorResultado, lnAfiliado, lnAfiliadoResultado, lnBotonesConfirmar
            ,listaProvincias, listaZonas, listaPrestador, listaAfiliado;
    private TextView lblProvinciaSeleccionado, lblSeleccionarZonaPaso, lblSeleccionarZona
            ,lblZonaSeleccionado, lblSeleccionarPrestadorPaso, lblSeleccionarPrestador
            ,lblPrestadorSeleccionado, lblSeleccionarAfiliadoPaso, lblSeleccionarAfiliado
            ,lblAfiliadoSeleccionado;
    private ImageView btnSiguiente;
    private ListView lstGrupoFam, lstEspecialidades, lstPrestador;
    private String _idMacroZona="", _nombreMacroZona="", _idZona="", _nombreZona="", _idPrestador=""
            , _nombrePrestador="", _idAfiliado="", _nombreAfiliado="";
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    private boolean zonaHabilitada=false, prestadorHabilitado=false, afiliadoHabilitado=false;

    public fragmento_emergencias_guardias() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_emergencias_guardias, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //#region seleccionar provincia
        lnProvincia=(LinearLayout) view.findViewById(R.id.lnProvincia);
        lnProvincia.setVisibility(View.VISIBLE);
        lnProvinciaResultados=(LinearLayout) view.findViewById(R.id.lnProvinciaResultados);
        lnProvinciaResultados.setVisibility(View.GONE);
        lblProvinciaSeleccionado=(TextView) view.findViewById(R.id.lblProvinciaSeleccionado);
        listaProvincias=(LinearLayout) view.findViewById(R.id.listaProvincias);
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

        //#region seleccionar zona
        lnZona=(LinearLayout) view.findViewById(R.id.lnZona);
        lnZona.setVisibility(View.VISIBLE);
        lblSeleccionarZonaPaso=(TextView) view.findViewById(R.id.lblSeleccionarZonaPaso);
        lblSeleccionarZona=(TextView) view.findViewById(R.id.lblSeleccionarZona);
        lnZonaResultados=(LinearLayout) view.findViewById(R.id.lnZonaResultados);
        lnZonaResultados.setVisibility(View.GONE);
        lblZonaSeleccionado=(TextView) view.findViewById(R.id.lblZonaSeleccionado);
        listaZonas=(LinearLayout) view.findViewById(R.id.listaZonas);
        listaZonas.setVisibility(View.GONE);
        lnZona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickZona();
            }
        });

        lnZonaResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickZona();
            }
        });
        //#endregion

        //#region seleccionar prestador
        lnPrestador=(LinearLayout) view.findViewById(R.id.lnPrestador);
        lnPrestador.setVisibility(View.VISIBLE);
        lblSeleccionarPrestadorPaso=(TextView) view.findViewById(R.id.lblSeleccionarPrestadorPaso);
        lblSeleccionarPrestador=(TextView) view.findViewById(R.id.lblSeleccionarPrestador);
        lnPrestadorResultado=(LinearLayout) view.findViewById(R.id.lnPrestadorResultado);
        lnPrestadorResultado.setVisibility(View.GONE);
        lblPrestadorSeleccionado=(TextView) view.findViewById(R.id.lblPrestadorSeleccionado);
        listaPrestador=(LinearLayout) view.findViewById(R.id.listaPrestador);
        listaPrestador.setVisibility(View.GONE);
        lnPrestador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPrestador();
            }
        });

        lnPrestadorResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPrestador();
            }
        });
        //#endregion

        //#region seleccionar afiliado
        lnAfiliado=(LinearLayout) view.findViewById(R.id.lnAfiliado);
        lnAfiliado.setVisibility(View.VISIBLE);
        lblSeleccionarAfiliadoPaso=(TextView) view.findViewById(R.id.lblSeleccionarAfiliadoPaso);
        lblSeleccionarAfiliado=(TextView) view.findViewById(R.id.lblSeleccionarAfiliado);
        lnAfiliadoResultado=(LinearLayout) view.findViewById(R.id.lnAfiliadoResultado);
        lnAfiliadoResultado.setVisibility(View.GONE);
        lblAfiliadoSeleccionado=(TextView) view.findViewById(R.id.lblAfiliadoSeleccionado);
        listaAfiliado=(LinearLayout) view.findViewById(R.id.listaAfiliado);
        listaAfiliado.setVisibility(View.GONE);
        lnAfiliado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAfiliado();
            }
        });

        lnAfiliadoResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAfiliado();
            }
        });
        //#endregion

        btnSiguiente=(ImageView) view.findViewById(R.id.btnSiguiente);
        btnSiguiente.setVisibility(View.GONE);

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarConfirmacion();
            }
        });
    }

    private void clickProvincia(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnProvincia.setVisibility(View.VISIBLE);
        lnProvinciaResultados.setVisibility(View.GONE);
        lblProvinciaSeleccionado.setText("Seleccionar Provincia");
        listaProvincias.setVisibility(View.GONE);
        deshabilitaZona();
        deshabilitaPrestador();
        deshabilitaAfiliado();
        btnSiguiente.setVisibility(View.GONE);
        _idMacroZona="";
        _nombreMacroZona="";
        buscarMacroZona();
    }

    private void clickZona(){
        if (!zonaHabilitada){
            return;
        }
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnZona.setVisibility(View.VISIBLE);
        lnZonaResultados.setVisibility(View.GONE);
        lblZonaSeleccionado.setText("Seleccionar Zona");
        listaZonas.setVisibility(View.GONE);
        deshabilitaPrestador();
        deshabilitaAfiliado();
        btnSiguiente.setVisibility(View.GONE);
        _idZona="";
        _nombreZona="";
        buscarZona();
    }

    private void clickPrestador(){
        if (!prestadorHabilitado){
            return;
        }
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnPrestador.setVisibility(View.VISIBLE);
        lnPrestadorResultado.setVisibility(View.GONE);
        lblPrestadorSeleccionado.setText("Seleccionar Prestador");
        listaPrestador.setVisibility(View.GONE);
        deshabilitaAfiliado();
        btnSiguiente.setVisibility(View.GONE);
        _idPrestador="";
        _nombrePrestador="";
        buscarPrestador();
    }

    private void clickAfiliado(){
        if (afiliadoHabilitado==false){
            return;
        }
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultado.setVisibility(View.GONE);
        lblAfiliadoSeleccionado.setText("Seleccionar Afiliado");
        listaAfiliado.setVisibility(View.GONE);
        _idAfiliado="";
        _nombreAfiliado="";
        btnSiguiente.setVisibility(View.GONE);
        buscarAfiliado();
    }

    //region macro zonas
    private void buscarMacroZona(){
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las provincias");

            fabrica_WS.APPBuscarGuardiaActivaMacroZonas(getActivity(), idAfiliado, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idMacroZona = new String[valores.getElementsByTagName("idMacroZona").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idMacroZona").getLength(); i++) {
                        idMacroZona[i] = valores.getElementsByTagName("idMacroZona").item(i).getTextContent();
                    }
                    String[] nombre = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        nombre[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    mostrarMacroZonas(idMacroZona, nombre);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar la zona:\n"+ex.getMessage());
        }
    }

    private void mostrarMacroZonas(String[] idMacroZona, String[] macroZona) {
        try {
            lnProvincia.setVisibility(View.GONE);
            lnProvinciaResultados.setVisibility(View.VISIBLE);
            listaProvincias.removeAllViews();
            for (int i = 0; i < idMacroZona.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnMacroZonas = inflater.inflate(R.layout.lista_un_renglon_tilde, null);

                final String idMacroZonaLocal=idMacroZona[i];
                final String nombreMacroZonaLocal=macroZona[i];

                TextView idFila = (TextView) lnMacroZonas.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnMacroZonas.findViewById(R.id.linea1);

                idFila.setText(idMacroZonaLocal);
                linea1.setText(nombreMacroZonaLocal);

                lnMacroZonas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seleccionarProvincia(idMacroZonaLocal, nombreMacroZonaLocal);
                    }
                });

                listaProvincias.addView(lnMacroZonas);
            }
            listaProvincias.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las zonas:\n" + e.getMessage());
        }
    }

    private void seleccionarProvincia(String idMacroZona, String nombreMacroZona){
        _idMacroZona=idMacroZona;
        _nombreMacroZona=nombreMacroZona;
        lblProvinciaSeleccionado.setText(_nombreMacroZona);
        listaProvincias.setVisibility(View.GONE);
        habilitaZona();
    }
    //endregion

    //#region zona
    private void deshabilitaZona(){
        zonaHabilitada=false;
        lblSeleccionarZonaPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblSeleccionarZona.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnZona.setVisibility(View.VISIBLE);
        lnZonaResultados.setVisibility(View.GONE);
        listaZonas.setVisibility(View.GONE);
    }

    private void habilitaZona(){
        zonaHabilitada=true;
        lblSeleccionarZonaPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblSeleccionarZona.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void buscarZona(){
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las zonas");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", idAfiliado);
            params.put("idMacroZona", _idMacroZona);

            fabrica_WS.APPBuscarGuardiaActivaZonas(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idZona = new String[valores.getElementsByTagName("idZona").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idZona").getLength(); i++) {
                        idZona[i] = valores.getElementsByTagName("idZona").item(i).getTextContent();
                    }
                    String[] zona = new String[valores.getElementsByTagName("zona").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("zona").getLength(); i++) {
                        zona[i] = valores.getElementsByTagName("zona").item(i).getTextContent();
                    }
                    mostrarZonas(idZona, zona);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar la zona:\n"+ex.getMessage());
        }
    }

    private void mostrarZonas(String[] idZona, String[] zona) {
        try {
            lnZona.setVisibility(View.GONE);
            lnZonaResultados.setVisibility(View.VISIBLE);
            listaZonas.removeAllViews();
            for (int i = 0; i < idZona.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnMacroZonas = inflater.inflate(R.layout.lista_un_renglon_tilde, null);

                final String idZonaLocal=idZona[i];
                final String nombreZonaLocal=zona[i];

                TextView idFila = (TextView) lnMacroZonas.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnMacroZonas.findViewById(R.id.linea1);

                idFila.setText(idZonaLocal);
                linea1.setText(nombreZonaLocal);

                lnMacroZonas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seleccionarZona(idZonaLocal, nombreZonaLocal);
                    }
                });

                listaZonas.addView(lnMacroZonas);
            }
            listaZonas.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las zonas:\n" + e.getMessage());
        }
    }

    private void seleccionarZona(String idZona, String nombreZona){
        _idZona=idZona;
        _nombreZona=nombreZona;
        lblZonaSeleccionado.setText(_nombreZona);
        listaZonas.setVisibility(View.GONE);
        habilitaPrestador();
    }
    //#endregion

    //#region prestador
    private void deshabilitaPrestador(){
        prestadorHabilitado=false;
        lblSeleccionarPrestadorPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblSeleccionarPrestador.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnPrestador.setVisibility(View.VISIBLE);
        lnPrestadorResultado.setVisibility(View.GONE);
        listaPrestador.setVisibility(View.GONE);
    }

    private void habilitaPrestador(){
        prestadorHabilitado=true;
        lblSeleccionarPrestadorPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblSeleccionarPrestador.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void buscarPrestador(){
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los prestadores");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", idAfiliado);
            params.put("idZona", _idZona);

            fabrica_WS.APPBuscarGuardiaActivaEnZona(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idConvenio = new String[valores.getElementsByTagName("idConvenio").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idConvenio").getLength(); i++) {
                        idConvenio[i] = valores.getElementsByTagName("idConvenio").item(i).getTextContent();
                    }
                    String[] convenio = new String[valores.getElementsByTagName("nombreConvenio").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombreConvenio").getLength(); i++) {
                        convenio[i] = valores.getElementsByTagName("nombreConvenio").item(i).getTextContent();
                    }
                    String[] localidad = new String[valores.getElementsByTagName("localidad").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("localidad").getLength(); i++) {
                        localidad[i] = valores.getElementsByTagName("localidad").item(i).getTextContent();
                    }
                    String[] domicilio = new String[valores.getElementsByTagName("domicilio").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("domicilio").getLength(); i++) {
                        domicilio[i] = valores.getElementsByTagName("domicilio").item(i).getTextContent();
                    }
                    mostrarPrestadores(idConvenio, convenio, localidad, domicilio);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar la zona:\n"+ex.getMessage());
        }
    }

    private void mostrarPrestadores(String[] idConvenio, String[] convenio, String[] localidad, String[] domicilio) {
        try {
            lnPrestador.setVisibility(View.GONE);
            lnPrestadorResultado.setVisibility(View.VISIBLE);
            listaPrestador.removeAllViews();
            for (int i = 0; i < idConvenio.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnPrestador = inflater.inflate(R.layout.lista_tres_renglones_tilde, null);

                final String idPrestadorLocal=idConvenio[i];
                final String nombrePrestadorLocal=convenio[i];
                final String domicilio1PrestadorLocal=localidad[i];
                final String domicilio2PrestadorLocal=domicilio[i];

                TextView idFila = (TextView) lnPrestador.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnPrestador.findViewById(R.id.linea1);
                TextView linea2 = (TextView) lnPrestador.findViewById(R.id.linea2);
                TextView linea3 = (TextView) lnPrestador.findViewById(R.id.linea3);

                idFila.setText(idPrestadorLocal);
                linea1.setText(nombrePrestadorLocal);
                linea2.setText(domicilio1PrestadorLocal);
                linea3.setText(domicilio2PrestadorLocal);

                lnPrestador.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seleccionarPrestador(idPrestadorLocal, nombrePrestadorLocal);
                    }
                });

                listaPrestador.addView(lnPrestador);
            }
            listaPrestador.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar los prestadores:\n" + e.getMessage());
        }
    }

    private void seleccionarPrestador(String idPrestador, String nombrePrestador){
        _idPrestador=idPrestador;
        _nombrePrestador=nombrePrestador;
        lblPrestadorSeleccionado.setText(_nombrePrestador);
        listaPrestador.setVisibility(View.GONE);
        habilitaAfiliado();
    }
    //#endregion

    //#region afiliado
    private void deshabilitaAfiliado(){
        afiliadoHabilitado=false;
        lblSeleccionarAfiliadoPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblSeleccionarAfiliado.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultado.setVisibility(View.GONE);
        listaAfiliado.setVisibility(View.GONE);
    }

    private void habilitaAfiliado(){
        afiliadoHabilitado=true;
        lblSeleccionarAfiliadoPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblSeleccionarAfiliado.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void buscarAfiliado(){
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando el grupo familiar");

            fabrica_WS.APPBuscarGrupoFamiliar(getActivity(), idAfiliado, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] grupoFam = new String[valores.getElementsByTagName("apellidoYNombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("apellidoYNombre").getLength(); i++) {
                        grupoFam[i] = valores.getElementsByTagName("apellidoYNombre").item(i).getTextContent();
                    }
                    String[] numAfil = new String[valores.getElementsByTagName("nroAfiliado").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nroAfiliado").getLength(); i++) {
                        numAfil[i] = valores.getElementsByTagName("nroAfiliado").item(i).getTextContent();
                    }
                    String[] idAfiliados = new String[valores.getElementsByTagName("idAfiliado").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idAfiliado").getLength(); i++) {
                        idAfiliados[i] = valores.getElementsByTagName("idAfiliado").item(i).getTextContent();
                    }
                    String[] tipoMensaje = new String[valores.getElementsByTagName("tipoMensaje").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("tipoMensaje").getLength(); i++) {
                        tipoMensaje[i] = valores.getElementsByTagName("tipoMensaje").item(i).getTextContent();
                    }
                    String[] mensaje = new String[valores.getElementsByTagName("mensaje").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("mensaje").getLength(); i++) {
                        mensaje[i] = valores.getElementsByTagName("mensaje").item(i).getTextContent();
                    }
                    mostrarGrupoFam(idAfiliados, grupoFam, numAfil, tipoMensaje, mensaje);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar el grupo familiar:\n"+ex.getMessage());
        }

    }

    private void mostrarGrupoFam(String[] idAfiliados, String[] grupoFam, String[] numAfil, String[] tipoMensaje, String[] mensaje) {
        try {
            lnAfiliado.setVisibility(View.GONE);
            lnAfiliadoResultado.setVisibility(View.VISIBLE);
            listaAfiliado.removeAllViews();
            for (int i = 0; i < idAfiliados.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnPrestador = inflater.inflate(R.layout.lista_grupo_fam, null);

                final String idAfiliadoLocal=idAfiliados[i];
                final String grupoFamLocal=grupoFam[i];
                final String numAfilLocal=numAfil[i];
                final String mensajeLocal=mensaje[i];

                TextView idFila = (TextView) lnPrestador.findViewById(R.id.idFila);
                TextView oculto1 = (TextView) lnPrestador.findViewById(R.id.oculto1);
                TextView linea1 = (TextView) lnPrestador.findViewById(R.id.linea1);
                TextView linea2 = (TextView) lnPrestador.findViewById(R.id.linea2);

                idFila.setText(idAfiliadoLocal);
                oculto1.setText(mensajeLocal);
                linea1.setText(grupoFamLocal);
                linea2.setText(numAfilLocal);

                lnPrestador.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mensajeLocal.equals("--")){
                            seleccionarAfiliado(idAfiliadoLocal, grupoFamLocal);
                        }else{
                            dialogo = fabrica_dialogos.dlgError(getContext(), mensajeLocal);
                        }
                    }
                });
                listaAfiliado.addView(lnPrestador);
            }
            listaAfiliado.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar los prestadores:\n" + e.getMessage());
        }
    }

    private void seleccionarAfiliado(String idAfiliado, String apellNomb){
        _idAfiliado=idAfiliado;
        _nombreAfiliado=apellNomb;
        lblAfiliadoSeleccionado.setText(_nombreAfiliado);
        listaAfiliado.setVisibility(View.GONE);
        btnSiguiente.setVisibility(View.VISIBLE);
    }
    //#endregion

    //#region solicitar guardia activa
    private void mostrarConfirmacion(){
        String mensajeDlg=_nombreMacroZona+"\n"+_nombreZona+"\n"+_nombrePrestador+"\n"+_nombreAfiliado;
        dialogoSec = fabrica_dialogos.dlgConfirmarDatos(getContext()
                , "Confirmar orden de guardia"
                , mensajeDlg,R.drawable.side_servicios_de_guardia,"");
        ImageView btnSi=(ImageView)dialogoSec.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarDatos();
            }
        });
    }

    private void confirmarDatos(){
        try{
            //intenta registrar el ID de FCM del telefono APP
            String yaRegistroID=funciones.verificaSenderID(getActivity(),3);
            if (yaRegistroID.equals("SD") || yaRegistroID.equals("NO")){
                if (yaRegistroID.equals("SD")) {
                    dialogo = fabrica_dialogos.dlgError(getContext(), "No se puede solicitar la orden de consulta porque todavía no se registra el dispositivo");
                }else{
                    dialogo = fabrica_dialogos.dlgError(getContext(), "No se puede solicitar la orden de consulta porque no se pudo registrar el dispositivo");
                }
                dialogo.show();
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Almacenando la orden de guardia");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", _idAfiliado);
            params.put("idConvenio", _idPrestador);
            params.put("IMEI", funciones.obtenerIMEI(getContext()));
            params.put("idAfiliadoSolicita", _idAfiliado);

            fabrica_WS.APPSolicitarOrdenGuardiaActiva(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    SQLController database= new SQLController(getContext());
                    String idOrden=valores.getElementsByTagName("idOrden").item(0).getTextContent();
                    String prestacion=valores.getElementsByTagName("nombreParaAfiliado").item(0).getTextContent();
                    String fecEmision=valores.getElementsByTagName("fecEmision").item(0).getTextContent();
                    String fecVencimiento=valores.getElementsByTagName("fecVencimiento").item(0).getTextContent();
                    String codigo=valores.getElementsByTagName("codigo").item(0).getTextContent();
                    String mensaje=valores.getElementsByTagName("mensaje").item(0).getTextContent();

                    database.agregarGuardiActiva(idOrden, _nombrePrestador
                            ,prestacion, _nombreAfiliado, fecEmision, fecVencimiento, codigo, "1");
                    database.agregarAviso(idOrden);
                    dialogoSec.dismiss();
                    dialogo = fabrica_dialogos.dlgAviso(getContext(), mensaje);
                    LinearLayout lnMsgTotal=(LinearLayout)dialogo.findViewById(R.id.lnMsgTotal);
                    lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogo.dismiss();
                            trigger.fireChange("emergencia_guardia_btnTermino");
                            return;
                        }
                    });
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al confirmar la orden de guardia:\n" + e.getMessage());
        }
    }
    //#endregion

    @Override
    public void onAttach (Context context){
        super.onAttach(context);
        tiempoMulticlick=Float.parseFloat(getString(R.string.tiempoMulticlick));
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
