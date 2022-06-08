package ar.com.andessalud.andes.fragmentos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.funciones;

public class fragmento_ordenes extends Fragment{

    public fragmento_ordenes() {
        // Required empty public constructor
    }

    ImageView imvOrdenes, imvEmergencias;
    FragmentChangeTrigger trigger;
    private Dialog dialogo, dialogoSec;

    private LinearLayout lnAfiliado, lnAfiliadoResultados, lnEspecialidad, lnEspecialidadResultados
            , lnPrestador, lnPrestadorResultado, lnBotonesConfirmar;
    private TextView lblAfiliadoSeleccionado, lblEspecialidadSeleccionado, lblPrestadorSeleccionado, lblSeleccionarEspecialidad
            ,lblSeleccionarEspecialidadPaso, lblSeleccionarPrestadorPaso, lblSeleccionarPrestador;
    private String _idAfiliado="", _idEspecialidad="", _idPrestador="", _nombreAfiliado="", _idPrestaciones="", _nombreEspecialidad=""
            ,_nombrePrestador, _idOrden="", _prestacionAutorizada="", _prestadorAutorizado="", _domicilio1Autorizado=""
            ,_domicilio2Autorizado="", _codAutorizacion="", _fechaVencimiento="", _coseguro="";
    private ImageView btnSiguiente;
    private ListView lstGrupoFam, lstEspecialidades, lstPrestador;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    private boolean especialidadHabilitada=false, prestadorHabilitado=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_ordenes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //#region seleccionar afiliado
        lnAfiliado=(LinearLayout) view.findViewById(R.id.lnAfiliado);
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultados=(LinearLayout) view.findViewById(R.id.lnAfiliadoResultados);
        lnAfiliadoResultados.setVisibility(View.GONE);
        lblAfiliadoSeleccionado=(TextView) view.findViewById(R.id.lblAfiliadoSeleccionado);
        lstGrupoFam=(ListView) view.findViewById(R.id.lstGrupoFam);
        lstGrupoFam.setVisibility(View.GONE);

        lnAfiliado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickGrupoFamiliar();
            }
        });

        lnAfiliadoResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickGrupoFamiliar();
            }
        });
        //#endregion

        //#region seleccionar especialidad
        lnEspecialidad=(LinearLayout) view.findViewById(R.id.lnEspecialidad);
        lnEspecialidad.setVisibility(View.VISIBLE);
        lnEspecialidadResultados=(LinearLayout) view.findViewById(R.id.lnEspecialidadResultados);
        lnEspecialidadResultados.setVisibility(View.GONE);
        lblEspecialidadSeleccionado=(TextView) view.findViewById(R.id.lblEspecialidadSeleccionado);
        lblSeleccionarEspecialidad=(TextView) view.findViewById(R.id.lblSeleccionarEspecialidad);
        lblSeleccionarEspecialidadPaso=(TextView) view.findViewById(R.id.lblSeleccionarEspecialidadPaso);
        lstEspecialidades=(ListView) view.findViewById(R.id.lstEspecialidades);
        lstEspecialidades.setVisibility(View.GONE);

        lnEspecialidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEspecialidad();
            }
        });

        lnEspecialidadResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEspecialidad();
            }
        });

        deshabilitaEspecialidad();
        //#endregion

        //#region seleccionar prestador
        lnPrestador=(LinearLayout) view.findViewById(R.id.lnPrestador);
        lnPrestador.setVisibility(View.VISIBLE);
        lnPrestadorResultado=(LinearLayout) view.findViewById(R.id.lnPrestadorResultado);
        lnPrestadorResultado.setVisibility(View.GONE);
        lblPrestadorSeleccionado=(TextView) view.findViewById(R.id.lblPrestadorSeleccionado);
        lblSeleccionarPrestador=(TextView) view.findViewById(R.id.lblSeleccionarPrestador);
        lblSeleccionarPrestadorPaso=(TextView) view.findViewById(R.id.lblSeleccionarPrestadorPaso);
        lstPrestador=(ListView) view.findViewById(R.id.lstPrestador);
        lstPrestador.setVisibility(View.GONE);

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

        lnBotonesConfirmar=(LinearLayout) view.findViewById(R.id.lnBotonesConfirmar);
        lnBotonesConfirmar.setVisibility(View.GONE);
        btnSiguiente=(ImageView) view.findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previsualizarOrden();
            }
        });
    }

    private void clickGrupoFamiliar(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultados.setVisibility(View.GONE);
        lstGrupoFam.setVisibility(View.GONE);
        deshabilitaEspecialidad();
        lnPrestador.setVisibility(View.VISIBLE);
        lnPrestadorResultado.setVisibility(View.GONE);
        deshabilitaPrestador();
        lnBotonesConfirmar.setVisibility(View.GONE);
        _idAfiliado="";
        _idEspecialidad="";
        _idPrestador="";
        leerGrupoFamiliar();
    }

    private void clickEspecialidad(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnEspecialidad.setVisibility(View.VISIBLE);
        lnEspecialidadResultados.setVisibility(View.GONE);
        lstEspecialidades.setVisibility(View.GONE);
        deshabilitaPrestador();
        _idEspecialidad="";
        lnBotonesConfirmar.setVisibility(View.GONE);
        leerEspecialidades();
    }

    private void clickPrestador(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnPrestador.setVisibility(View.VISIBLE);
        lnPrestadorResultado.setVisibility(View.GONE);
        lstPrestador.setVisibility(View.GONE);
        _idPrestador="";
        lnBotonesConfirmar.setVisibility(View.GONE);
        leerPrestadores();
    }

    //region buscar grupo familiar
    private void leerGrupoFamiliar() {
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }
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

    private void mostrarGrupoFam(String[] idAfiliados, String[] grupoFam, String[] nroAfiliado
            ,String[] tipoMensaje, String[] mensaje) {
        try {
            lnAfiliado.setVisibility(View.GONE);
            lnAfiliadoResultados.setVisibility(View.VISIBLE);
            lblAfiliadoSeleccionado.setText("Seleccionar Afiliado");

            if (idAfiliados.length>=1) {
                ArrayList<HashMap<String, String>> listaAMostrar = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < grupoFam.length; i++) {
                    map = new HashMap<String, String>();
                    map.put("idFila", idAfiliados[i]);
                    map.put("oculto1", mensaje[i]);
                    map.put("linea1", grupoFam[i]);
                    map.put("linea2", nroAfiliado[i]);
                    listaAMostrar.add(map);
                }

                SimpleAdapter adaptador = new SimpleAdapter(getContext(), listaAMostrar, R.layout.lista_grupo_fam,
                        new String[] {"idFila","oculto1","linea1","linea2"}, new int[] {R.id.idFila,R.id.oculto1,R.id.linea1,R.id.linea2});
                lstGrupoFam.setAdapter(adaptador);
                funciones.setListViewHeightBasedOnChildren(lstGrupoFam, 50);
                lstGrupoFam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, Object> map = (HashMap<String, Object>) lstGrupoFam.getItemAtPosition(position);
                        if (map.get("oculto1").toString().equals("--")) {
                            _idAfiliado = map.get("idFila").toString();
                            _nombreAfiliado=map.get("linea1").toString();
                            lblAfiliadoSeleccionado.setText(_nombreAfiliado);
                            lstGrupoFam.setVisibility(View.GONE);
                            habilitaEspecialidad();
                        }else{
                            dialogo = fabrica_dialogos.dlgError(getContext(), map.get("oculto1").toString());
                        }
                    }
                });
                //dialog.show();
                lstGrupoFam.setVisibility(View.VISIBLE);
                dialogo.dismiss();
            }else{
                _idAfiliado=idAfiliados[0];
                _nombreAfiliado=grupoFam[0];
                lblAfiliadoSeleccionado.setText(_nombreAfiliado);
                lstGrupoFam.setVisibility(View.GONE);
                habilitaEspecialidad();
                dialogo.dismiss();
            }
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar el grupo familiar:\n" + e.getMessage());
        }
    }
    //endregion

    //#region especialidad
    private void deshabilitaEspecialidad(){
        especialidadHabilitada=false;
        lblSeleccionarEspecialidadPaso.setTextColor(Color.parseColor("#7a7a7a"));
        lblSeleccionarEspecialidad.setTextColor(Color.parseColor("#7a7a7a"));
        lnEspecialidad.setVisibility(View.VISIBLE);
        lnEspecialidadResultados.setVisibility(View.GONE);
        lstEspecialidades.setVisibility(View.GONE);
    }

    private void habilitaEspecialidad(){
        especialidadHabilitada=true;
        lblSeleccionarEspecialidadPaso.setTextColor(Color.parseColor("#130657"));
        lblSeleccionarEspecialidad.setTextColor(Color.parseColor("#130657"));
    }

    private void leerEspecialidades() {
        try{
            if (!especialidadHabilitada){
                return;
            }

            SQLController database = new SQLController(getContext());
            final String idAfiliadoTitular = database.obtenerIDAfiliado();
            if (idAfiliadoTitular.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las especialidades");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliadoTitular", idAfiliadoTitular);
            params.put("idAfiliado", _idAfiliado);

            fabrica_WS.APPBuscarPrestacionesAMB(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idPrestacion = new String[valores.getElementsByTagName("idPrestacion").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idPrestacion").getLength(); i++) {
                        idPrestacion[i] = valores.getElementsByTagName("idPrestacion").item(i).getTextContent();
                    }
                    String[] prestacion = new String[valores.getElementsByTagName("nombreParaAfiliado").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombreParaAfiliado").getLength(); i++) {
                        prestacion[i] = valores.getElementsByTagName("nombreParaAfiliado").item(i).getTextContent();
                    }
                    mostrarPrestaciones(idPrestacion, prestacion);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las especialidades:\n" + e.getMessage());
        }
    }

    private void mostrarPrestaciones(String[] idPrestacion, String[] prestacion) {
        try {
            lnEspecialidad.setVisibility(View.GONE);
            lnEspecialidadResultados.setVisibility(View.VISIBLE);
            lblEspecialidadSeleccionado.setText("Seleccionar especialidad");

            if (idPrestacion.length>=1) {
                ArrayList<HashMap<String, String>> listaAMostrar = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < idPrestacion.length; i++) {
                    map = new HashMap<String, String>();
                    map.put("idFila", idPrestacion[i]);
                    map.put("linea1", prestacion[i]);
                    listaAMostrar.add(map);
                }

                SimpleAdapter adaptador = new SimpleAdapter(getContext(), listaAMostrar, R.layout.lista_un_renglon_tilde,
                        new String[] {"idFila","linea1"}, new int[] {R.id.idFila,R.id.linea1});
                lstEspecialidades.setAdapter(adaptador);
                funciones.setListViewHeightBasedOnChildren(lstEspecialidades,20);
                lstEspecialidades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, Object> map = (HashMap<String, Object>) lstEspecialidades.getItemAtPosition(position);
                        _idPrestaciones = map.get("idFila").toString();
                        _nombreEspecialidad = map.get("linea1").toString();

                        if (_idPrestaciones.equals("00000000-0000-0000-0000-000000000000")){
                            trigger.fireChange("ordenes_btnCartillas");
                            return;
                        }
                        lblEspecialidadSeleccionado.setText(_nombreEspecialidad);
                        lstEspecialidades.setVisibility(View.GONE);
                        habilitaPrestador();
                    }
                });
                lstEspecialidades.setVisibility(View.VISIBLE);
            }else{
                _idPrestaciones=idPrestacion[0];
                _nombreEspecialidad=prestacion[0];
                if (_idPrestaciones.equals("00000000-0000-0000-0000-000000000000")){
                    trigger.fireChange("ordenes_btnCartillas");
                    return;
                }
                lblEspecialidadSeleccionado.setText(_nombreEspecialidad);
                lstEspecialidades.setVisibility(View.GONE);
                habilitaPrestador();
            }
            dialogo.dismiss();
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las especialidades:\n" + e.getMessage());
        }
    }
    //#endregion

    //#region prestador
    private void deshabilitaPrestador(){
        prestadorHabilitado=false;
        lblSeleccionarPrestadorPaso.setTextColor(Color.parseColor("#7a7a7a"));
        lblSeleccionarPrestador.setTextColor(Color.parseColor("#7a7a7a"));
        lnPrestador.setVisibility(View.VISIBLE);
        lnPrestadorResultado.setVisibility(View.GONE);
        lstPrestador.setVisibility(View.GONE);
    }

    private void habilitaPrestador(){
        prestadorHabilitado=true;
        lblSeleccionarPrestadorPaso.setTextColor(Color.parseColor("#130657"));
        lblSeleccionarPrestador.setTextColor(Color.parseColor("#130657"));
    }

    private void leerPrestadores() {
        if (!prestadorHabilitado){
            return;
        }

        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliadoTitular = database.obtenerIDAfiliado();
            if (idAfiliadoTitular.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los prestadores");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliadoTitular", idAfiliadoTitular);
            params.put("idAfiliado", _idAfiliado);
            params.put("idPrestacion", _idPrestaciones);

            fabrica_WS.APPBuscarPrestadorPorPrestacion(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idPrestador = new String[valores.getElementsByTagName("idPrestador").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idPrestador").getLength(); i++) {
                        idPrestador[i] = valores.getElementsByTagName("idPrestador").item(i).getTextContent();
                    }
                    String[] prestador = new String[valores.getElementsByTagName("prestador").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("prestador").getLength(); i++) {
                        prestador[i] = valores.getElementsByTagName("prestador").item(i).getTextContent();
                    }
                    mostrarPrestador(idPrestador, prestador);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los prestadores:\n" + e.getMessage());
        }
    }

    private void mostrarPrestador(String[] idPrestador, String[] prestador) {
        try {
            lnPrestador.setVisibility(View.GONE);
            lnPrestadorResultado.setVisibility(View.VISIBLE);
            lblPrestadorSeleccionado.setText("Seleccionar \nprestador");

            if (idPrestador.length>=1) {
                ArrayList<HashMap<String, String>> listaAMostrar = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < idPrestador.length; i++) {
                    map = new HashMap<String, String>();
                    map.put("idFila", idPrestador[i]);
                    map.put("linea1", prestador[i]);
                    listaAMostrar.add(map);
                }

                SimpleAdapter adaptador = new SimpleAdapter(getContext(), listaAMostrar, R.layout.lista_un_renglon_tilde,
                        new String[] {"idFila","linea1"}, new int[] {R.id.idFila,R.id.linea1});
                lstPrestador.setAdapter(adaptador);
                funciones.setListViewHeightBasedOnChildren(lstPrestador,listaAMostrar.size()*10);
                lstPrestador.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, Object> map = (HashMap<String, Object>) lstPrestador.getItemAtPosition(position);
                        _idPrestador = map.get("idFila").toString();
                        _nombrePrestador=map.get("linea1").toString();

                        lblPrestadorSeleccionado.setText(_nombrePrestador);
                        lstPrestador.setVisibility(View.GONE);
                        lnBotonesConfirmar.setVisibility(View.VISIBLE);
                    }
                });
                lstPrestador.setVisibility(View.VISIBLE);
            }else{
                _idPrestador=idPrestador[0];
                _nombreEspecialidad=prestador[0];
                lnBotonesConfirmar.setVisibility(View.VISIBLE);
            }
            dialogo.dismiss();
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar los prestadores:\n" + e.getMessage());
        }
    }
    //#endregion

    //#region previsualizar orden
    private void  previsualizarOrden(){
        String mensajeDlg=_nombreAfiliado+"\n\n"+_nombreEspecialidad+"\n\n"+_nombrePrestador;
        dialogo = fabrica_dialogos.dlgConfirmarDatos(getContext(), "Confirmar datos", mensajeDlg,R.drawable.ordenes_left_side,"");
        ImageView btnSi=(ImageView)dialogo.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogo.dismiss();
                auditarOrden();
            }
        });
    }
    //#endregion

    //region auditar orden
    private void auditarOrden(){
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliadoTitular = database.obtenerIDAfiliado();
            if (idAfiliadoTitular.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Evaluando la solicitud");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliadoTitular", idAfiliadoTitular);
            params.put("idAfiliado", _idAfiliado);
            params.put("idPrestacion", _idPrestaciones);
            params.put("idPrestador", _idPrestador);

            fabrica_WS.APPSolicitarOrdenDeConsulta(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    _idOrden = valores.getElementsByTagName("idOrden").item(0).getTextContent();
                    String valorDevuelto = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                    String mensaje = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                    String convenio = valores.getElementsByTagName("convenio").item(0).getTextContent();
                    String prestacion = valores.getElementsByTagName("prestacion").item(0).getTextContent();
                    String domRenglon1 = valores.getElementsByTagName("domRenglon1").item(0).getTextContent();
                    String domRenglon2 = valores.getElementsByTagName("domRenglon2").item(0).getTextContent();
                    String codAutorizacion = valores.getElementsByTagName("codAutorizacion").item(0).getTextContent();
                    String fecVencimiento = valores.getElementsByTagName("fecVencimiento").item(0).getTextContent();
                    String coseguro = valores.getElementsByTagName("coseguro").item(0).getTextContent();
                    if (valorDevuelto.equals("00")){
                        mostrarAutorizado(mensaje, convenio, prestacion, domRenglon1, domRenglon2, codAutorizacion, fecVencimiento, coseguro);
                    }else{
                        mostrarAuditoria(mensaje, convenio, prestacion, domRenglon1, domRenglon2, codAutorizacion, fecVencimiento, coseguro);
                    }

                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al evaluar la orden ambulatoria:\n" + e.getMessage());
        }
    }
    //#endregion

    //#region orden para auditar
    private void mostrarAuditoria(String mensaje,String convenio,String prestacion,String domRenglon1,String domRenglon2
            ,String codAutorizacion,String fecVencimiento, String coseguro){

        _prestacionAutorizada=prestacion;
        _prestadorAutorizado=convenio;
        _domicilio1Autorizado=domRenglon1;
        _domicilio2Autorizado=domRenglon2;
        _codAutorizacion=codAutorizacion;
        _fechaVencimiento=fecVencimiento;
        _coseguro=coseguro;

        String mensajeDlg=_nombreAfiliado+"\n\n"+_nombreEspecialidad+"\n\n"+_nombrePrestador;

        if (!_coseguro.equals("0")){
            mensajeDlg+="\n\nCoseguro $: "+_coseguro;
        }

        dialogo = fabrica_dialogos.dlgConfirmarDatos(getContext(), "Confirmar orden para auditoria", mensajeDlg,R.drawable.ordenes_left_side,"amarillo");
        ImageView btnSi=(ImageView)dialogo.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarAuditado();
            }
        });
    }

    private void confirmarAuditado(){
        try{
            //intenta registrar el ID de FCM del telefono APP
            String yaRegistroID=funciones.verificaSenderID(getActivity(),3);
            if (yaRegistroID.equals("SD") || yaRegistroID.equals("NO")){
                String valorDevuelto="";
                if (yaRegistroID.equals("SD")) {
                    valorDevuelto="No se puede solicitar la orden de consulta porque todavía no se registra el dispositivo";
                }else{
                    valorDevuelto="No se puede solicitar la orden de consulta porque no se pudo registrar el dispositivo";
                }
                dialogo = fabrica_dialogos.dlgError(getContext(), valorDevuelto);
                return;
            }

            dialogoSec = fabrica_dialogos.dlgBuscando(getContext(), "Confirmando la orden ambulatoria auditada");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idOrden", _idOrden);
            params.put("IMEI", funciones.obtenerIMEI(getContext()));

            fabrica_WS.APPConfirmarOrdenDeConsultaAUD(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    dialogoSec.dismiss();
                    SQLController database= new SQLController(getContext());
                    String fecEmision = valores.getElementsByTagName("fecEmision").item(0).getTextContent();
                    String resultado= database.agregarOrdenConsultaAMB(_idOrden, _prestacionAutorizada
                            , _prestadorAutorizado,_domicilio1Autorizado,_domicilio2Autorizado, _codAutorizacion
                            , _fechaVencimiento, _nombreAfiliado, fecEmision, "AUD", _coseguro);
                    database.agregarAviso(_idOrden);

                    if (!resultado.equals("")) {
                        dialogoSec = fabrica_dialogos.dlgError(getContext(), resultado);
                    }else{
                        String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                        dialogoSec = fabrica_dialogos.dlgAviso(getContext(), msgAviso);
                        LinearLayout lnMsgTotal=(LinearLayout)dialogoSec.findViewById(R.id.lnMsgTotal);
                        lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogoSec.dismiss();
                                trigger.fireChange("ordenes_btnTermino");
                                return;
                            }
                        });
                    }
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogoSec.dismiss();
                    dialogoSec = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception e) {
            dialogoSec = fabrica_dialogos.dlgError(getContext(), "Error general al confirmar la orden ambulatoria para auditoria:\n" + e.getMessage());
        }
    }
    //#endregion

    //#region orden sin auditar
    private void mostrarAutorizado(String mensaje,String convenio,String prestacion,String domRenglon1,String domRenglon2
            ,String codAutorizacion,String fecVencimiento, String coseguro){

        _prestacionAutorizada=prestacion;
        _prestadorAutorizado=convenio;
        _domicilio1Autorizado=domRenglon1;
        _domicilio2Autorizado=domRenglon2;
        _codAutorizacion=codAutorizacion;
        _fechaVencimiento=fecVencimiento;
        _coseguro=coseguro;

        String mensajeDlg=_nombreAfiliado+"\n\n"+_nombreEspecialidad+"\n\n"+_nombrePrestador;

        if (!_coseguro.equals("0")){
            mensajeDlg+="\n\nCoseguro $: "+_coseguro;
        }

        dialogo = fabrica_dialogos.dlgConfirmarDatos(getContext(), "Confirmar orden autorizada", mensajeDlg,R.drawable.ordenes_left_side,"");
        ImageView btnSi=(ImageView)dialogo.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarAutorizado();
            }
        });
    }

    private void confirmarAutorizado(){
        try{
            String yaRegistroID=funciones.verificaSenderID(getActivity(),3);
            if (yaRegistroID.equals("SD") || yaRegistroID.equals("NO")){
                String valorDevuelto="";
                if (yaRegistroID.equals("SD")) {
                    valorDevuelto="No se puede solicitar la orden de consulta porque todavía no se registra el dispositivo";
                }else{
                    valorDevuelto="No se puede solicitar la orden de consulta porque no se pudo registrar el dispositivo";
                }
                dialogo = fabrica_dialogos.dlgError(getContext(), valorDevuelto);
                return;
            }

            dialogoSec = fabrica_dialogos.dlgBuscando(getContext(), "Confirmando la orden ambulatoria");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idOrden", _idOrden);
            params.put("IMEI", funciones.obtenerIMEI(getContext()));

            fabrica_WS.APPConfirmarOrdenDeConsultaAUT(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    dialogoSec.dismiss();
                    SQLController database= new SQLController(getContext());
                    String fecEmision = valores.getElementsByTagName("fecEmision").item(0).getTextContent();
                    String resultado= database.agregarOrdenConsultaAMB(_idOrden, _prestacionAutorizada
                            , _prestadorAutorizado,_domicilio1Autorizado,_domicilio2Autorizado, _codAutorizacion
                            , _fechaVencimiento, _nombreAfiliado, fecEmision, "AUT", _coseguro);
                    database.agregarAviso(_idOrden);

                    if (!resultado.equals("")) {
                        dialogoSec = fabrica_dialogos.dlgError(getContext(), resultado);
                    }else{
                        String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                        dialogoSec = fabrica_dialogos.dlgAviso(getContext(), msgAviso);
                        LinearLayout lnMsgTotal=(LinearLayout)dialogoSec.findViewById(R.id.lnMsgTotal);
                        lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogoSec.dismiss();
                                trigger.fireChange("ordenes_btnTermino");
                                return;
                            }
                        });
                    }
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogoSec.dismiss();
                    dialogoSec = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception e) {
            dialogoSec = fabrica_dialogos.dlgError(getContext(), "Error general al confirmar la orden ambulatoria:\n" + e.getMessage());
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
