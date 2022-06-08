package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.datosPrestador;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.funciones;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_prestador_orden extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo, dialogoSec;
    MaterialSpinner spinnerTipoOrden;
    LinearLayout lnTotalTipoOrden, lnAfiliado, lnAfiliadoResultados, lnBuscarAfiliado, listaAfiliados
        , lnConfeccionarOrden, lnConfeccionarOrdenResultados, lnOrdenOdonto, listaPrestacionesOdonto
        , lnTituloPrestacionesOdonto, lnTxtPrestacionesOdonto, listaDientesOdonto, listaCarasOdonto
        , lnDetalleCarasSeleccionadas, lnDetallePrestacionOdontologiaAgregar
        , lnDetallePrestacionOdontologiaSolicitarTotal, lnCoseguroTotal, lnDetallePrestacionOdontologiaPrestacionEliminar
        , lnDetallePrestacionOdontologiaPrestacionSolicitar, listaPrestacionesOdontoBusqueda;
    String tipoOrdenActual, _idAfiliado="", _nombreAfiliado,_idPlantillavaloresDetalleOdonto
            ,_nombrePrestacionOdonto, _entornoActualOdonto, _diente, _codDiente, _codCara, _nombreCara
            ,coseguroTotal;
    private TextView lblAfiliadoSeleccionado, lblConfeccionarOrdenPaso, lblConfeccionarOrden
        ,lblPrestacionOdontoSeleccionada, lblOdontoDiente, lblOdontoCara, btnAgregarCara;
    private EditText edtAfiliado, edtPrestacionOdonto, txtOdontoCant;
    private TextView txtOdontoDiente, txtOdontoCara, txtCarasSeleccionadas, btnEliminarCara
        ,txtCoseguroTotal;
    private ImageView btnBuscarAfiliado, btnBuscarPrestacionOdonto;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    private boolean solicitarOrdenHabilitada=false;
    ArrayList<caras> listaCaras = new ArrayList<caras>();
    ArrayList<carasEnOrden> listaCarasEnOrden = new ArrayList<carasEnOrden>();
    ArrayList<prestaciones> listaPrestaciones = new ArrayList<prestaciones>();

    public fragmento_prestador_orden() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_prestador_orden, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> stringArrayList = new ArrayList<String>();
        if (datosPrestador.verificaAreaPrestador("ODONTO")==true){
            stringArrayList.add("Odontológica");
        }

        String[] update_str =stringArrayList.toArray(new String[stringArrayList.size()]);
        for (int i=0; i<stringArrayList.size(); i++) {
            update_str[i]=stringArrayList.get(i);
        }
        tipoOrdenActual=update_str[0];
        spinnerTipoOrden = (MaterialSpinner) view.findViewById(R.id.spinnerTipoOrden);
        spinnerTipoOrden.setItems(update_str);
        spinnerTipoOrden.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                tipoOrdenActual=item;
                inicializarNuevaOrden(getView());
            }
        });

        lnTotalTipoOrden = (LinearLayout) view.findViewById(R.id.lnTotalTipoOrden);
        if (stringArrayList.size()<=1){
            lnTotalTipoOrden.setVisibility(View.GONE);
        }else{
            lnTotalTipoOrden.setVisibility(View.VISIBLE);
        }

        //#region seleccionar afiliado
        lnAfiliado=(LinearLayout) view.findViewById(R.id.lnAfiliado);
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultados=(LinearLayout) view.findViewById(R.id.lnAfiliadoResultados);
        lnAfiliadoResultados.setVisibility(View.GONE);
        lblAfiliadoSeleccionado=(TextView) view.findViewById(R.id.lblAfiliadoSeleccionado);
        edtAfiliado=(EditText) view.findViewById(R.id.edtAfiliado);
        lnBuscarAfiliado=(LinearLayout) view.findViewById(R.id.lnBuscarAfiliado);
        lnBuscarAfiliado.setVisibility(View.GONE);
        btnBuscarAfiliado=(ImageView) view.findViewById(R.id.btnBuscarAfiliado);
        edtAfiliado.setVisibility(View.GONE);
        btnBuscarAfiliado.setVisibility(View.GONE);
        listaAfiliados=(LinearLayout) view.findViewById(R.id.listaAfiliados);
        listaAfiliados.setVisibility(View.GONE);

        lnAfiliado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicializarNuevaOrden(v);
                clickGrupoFamiliar();
            }
        });

        lnAfiliadoResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicializarNuevaOrden(v);
            }
        });

        btnBuscarAfiliado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarAfiliado();
            }
        });
        //#endregion

        //#region confeccionar orden
        lnConfeccionarOrden=(LinearLayout) view.findViewById(R.id.lnConfeccionarOrden);
        lnConfeccionarOrden.setVisibility(View.VISIBLE);
        lnConfeccionarOrdenResultados=(LinearLayout) view.findViewById(R.id.lnConfeccionarOrdenResultados);
        lnConfeccionarOrdenResultados.setVisibility(View.GONE);
        lnOrdenOdonto=(LinearLayout) view.findViewById(R.id.lnOrdenOdonto);
        lnOrdenOdonto.setVisibility(View.GONE);
        lblConfeccionarOrdenPaso=(TextView) view.findViewById(R.id.lblConfeccionarOrdenPaso);
        lblConfeccionarOrden=(TextView) view.findViewById(R.id.lblConfeccionarOrden);
        edtPrestacionOdonto=(EditText) view.findViewById(R.id.edtPrestacionOdonto);
        lnConfeccionarOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSolicitarOrden();
            }
        });
        btnBuscarPrestacionOdonto=(ImageView) view.findViewById(R.id.btnBuscarPrestacionOdonto);
        btnBuscarPrestacionOdonto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarPrestacionOdonto();
            }
        });
        listaPrestacionesOdontoBusqueda=(LinearLayout) view.findViewById(R.id.listaPrestacionesOdontoBusqueda);
        listaPrestacionesOdontoBusqueda.setVisibility(View.GONE);
        listaPrestacionesOdonto=(LinearLayout) view.findViewById(R.id.listaPrestacionesOdonto);
        listaPrestacionesOdonto.setVisibility(View.GONE);
        lblPrestacionOdontoSeleccionada=(TextView) view.findViewById(R.id.lblPrestacionOdontoSeleccionada);
        lblPrestacionOdontoSeleccionada.setVisibility(View.GONE);
        lnTituloPrestacionesOdonto=(LinearLayout) view.findViewById(R.id.lnTituloPrestacionesOdonto);
        lnTituloPrestacionesOdonto.setVisibility(View.GONE);
        lblOdontoDiente=(TextView) view.findViewById(R.id.lblOdontoDiente);
        lblOdontoDiente.setVisibility(View.GONE);
        lblOdontoDiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _diente="";
                _codDiente="";
                _codCara="";
                _nombreCara="";
                buscarDientes();
            }
        });
        lblOdontoCara=(TextView) view.findViewById(R.id.lblOdontoCara);
        lblOdontoCara.setVisibility(View.GONE);
        lblOdontoCara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarCaras();
            }
        });
        lnTxtPrestacionesOdonto=(LinearLayout) view.findViewById(R.id.lnTxtPrestacionesOdonto);
        lnTxtPrestacionesOdonto.setVisibility(View.GONE);
        txtOdontoCant=(EditText) view.findViewById(R.id.txtOdontoCant);
        txtOdontoDiente=(TextView) view.findViewById(R.id.txtOdontoDiente);
        txtOdontoDiente.setVisibility(View.GONE);
        txtOdontoDiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _diente="";
                _codDiente="";
                _codCara="";
                _nombreCara="";
                buscarDientes();
            }
        });
        txtOdontoCara=(TextView) view.findViewById(R.id.txtOdontoCara);
        txtOdontoCara.setVisibility(View.GONE);
        txtOdontoCara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarCaras();
            }
        });
        listaDientesOdonto=(LinearLayout) view.findViewById(R.id.listaDientesOdonto);
        listaDientesOdonto.setVisibility(View.GONE);
        listaCarasOdonto=(LinearLayout) view.findViewById(R.id.listaCarasOdonto);
        listaCarasOdonto.setVisibility(View.GONE);
        btnAgregarCara=(TextView) view.findViewById(R.id.btnAgregarCara);
        btnAgregarCara.setVisibility(View.GONE);
        btnAgregarCara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarCaraActual();
            }
        });
        lnDetalleCarasSeleccionadas=(LinearLayout) view.findViewById(R.id.lnDetalleCarasSeleccionadas);
        lnDetalleCarasSeleccionadas.setVisibility(View.GONE);
        txtCarasSeleccionadas=(TextView) view.findViewById(R.id.txtCarasSeleccionadas);
        btnEliminarCara=(TextView) view.findViewById(R.id.btnEliminarCara);
        btnEliminarCara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarUltimaCara();
            }
        });

        lnDetallePrestacionOdontologiaAgregar=(LinearLayout) view.findViewById(R.id.lnDetallePrestacionOdontologiaAgregar);
        lnDetallePrestacionOdontologiaAgregar.setVisibility(View.GONE);
        lnDetallePrestacionOdontologiaAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarPrestacion();
            }
        });

        lnDetallePrestacionOdontologiaSolicitarTotal=(LinearLayout) view.findViewById(R.id.lnDetallePrestacionOdontologiaSolicitarTotal);
        lnDetallePrestacionOdontologiaSolicitarTotal.setVisibility(View.GONE);
        lnCoseguroTotal=(LinearLayout) view.findViewById(R.id.lnCoseguroTotal);
        lnCoseguroTotal.setVisibility(View.GONE);
        txtCoseguroTotal=(TextView) view.findViewById(R.id.txtCoseguroTotal);
        lnDetallePrestacionOdontologiaPrestacionEliminar=(LinearLayout) view.findViewById(R.id.lnDetallePrestacionOdontologiaPrestacionEliminar);
        lnDetallePrestacionOdontologiaPrestacionEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarPrestaciones();
            }
        });

        lnDetallePrestacionOdontologiaPrestacionSolicitar=(LinearLayout) view.findViewById(R.id.lnDetallePrestacionOdontologiaPrestacionSolicitar);
        lnDetallePrestacionOdontologiaPrestacionSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solicitarOrdenOdontologica();
            }
        });
        //#endregion
    }

    private void inicializarNuevaOrden(View view){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnAfiliado.setVisibility(View.VISIBLE);
        lblAfiliadoSeleccionado.setText("Buscar afiliado");
        lnAfiliadoResultados.setVisibility(View.GONE);
        listaAfiliados.setVisibility(View.GONE);
        edtAfiliado.setVisibility(View.GONE);
        btnBuscarAfiliado.setVisibility(View.GONE);
        deshabilitaSolicitarOrden();
    }

    private void clickGrupoFamiliar(){
        lnAfiliado.setVisibility(View.GONE);
        edtAfiliado.setVisibility(View.VISIBLE);
        btnBuscarAfiliado.setVisibility(View.VISIBLE);
        lnBuscarAfiliado.setVisibility(View.VISIBLE);
        edtAfiliado.setVisibility(View.VISIBLE);
        btnBuscarAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultados.setVisibility(View.VISIBLE);
        lblAfiliadoSeleccionado.setText("Buscar afiliado");
        listaAfiliados.setVisibility(View.GONE);
        deshabilitaSolicitarOrden();
    }

    //#region afiliados
    private void buscarAfiliado() {
        try{
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los afiliados");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idUsuario", datosPrestador.leerIdUsuario());
            params.put("cadena", edtAfiliado.getText().toString());

            fabrica_WS.APPEXTBuscarAfiliadoGeneral(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idAfiliado = new String[valores.getElementsByTagName("idAfiliado").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idAfiliado").getLength(); i++) {
                        idAfiliado[i] = valores.getElementsByTagName("idAfiliado").item(i).getTextContent();
                    }
                    String[] apellNomb = new String[valores.getElementsByTagName("apellNomb").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("apellNomb").getLength(); i++) {
                        apellNomb[i] = valores.getElementsByTagName("apellNomb").item(i).getTextContent();
                    }
                    String[] nroAfiliado = new String[valores.getElementsByTagName("nroAfiliado").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nroAfiliado").getLength(); i++) {
                        nroAfiliado[i] = valores.getElementsByTagName("nroAfiliado").item(i).getTextContent();
                    }
                    String[] estado = new String[valores.getElementsByTagName("estado").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("estado").getLength(); i++) {
                        estado[i] = valores.getElementsByTagName("estado").item(i).getTextContent();
                    }
                    mostrarAfiliados(idAfiliado, apellNomb, nroAfiliado, estado);
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los afiliados:\n" + e.getMessage());
        }
    }

    private void mostrarAfiliados(String[] idAfiliado, String[] apellNomb, String[] nroAfiliado, String[] estado) {
        try {
            listaAfiliados.removeAllViews();
            for (int i = 0; i < idAfiliado.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnAfiliado = inflater.inflate(R.layout.lista_grupo_fam, null);

                final String _idAfiliadoAct=idAfiliado[i];
                final String _estadoAct=estado[i];
                final String _apellNombAct=apellNomb[i];
                final String _nroAfiliadoAct=nroAfiliado[i];

                TextView idFila = (TextView) lnAfiliado.findViewById(R.id.idFila);
                TextView oculto1 = (TextView) lnAfiliado.findViewById(R.id.oculto1);
                TextView linea1 = (TextView) lnAfiliado.findViewById(R.id.linea1);
                TextView linea2 = (TextView) lnAfiliado.findViewById(R.id.linea2);

                idFila.setText(_idAfiliadoAct);
                oculto1.setText(_estadoAct);
                linea1.setText(_apellNombAct);
                linea2.setText(_nroAfiliadoAct);

                lnAfiliado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (_estadoAct.equals("DE BAJA")) {
                            dialogo = fabrica_dialogos.dlgError(getContext(), "No se pueden brindar prestaciones a un afiliado de baja");
                            return;
                        }else{
                            _idAfiliado = _idAfiliadoAct;
                            _nombreAfiliado=_apellNombAct;
                            lnAfiliado.setVisibility(View.GONE);
                            lblAfiliadoSeleccionado.setText(_nombreAfiliado);
                            listaAfiliados.setVisibility(View.GONE);
                            lnBuscarAfiliado.setVisibility(View.GONE);
                            edtAfiliado.setText("");
                            habilitaSolicitarOrden();
                        }
                        //buscarRespuesta(_idPregunta, v);
                    }
                });
                listaAfiliados.addView(lnAfiliado);
            }
            listaAfiliados.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las preguntas frecuentes:\n" + e.getMessage());
        }
    }
    //#endregion

    //#region solicitar orden odonto
    private void deshabilitaSolicitarOrden(){
        listaPrestacionesOdonto.removeAllViews();
        listaPrestaciones.clear();
        listaCarasEnOrden.clear();
        listaCaras.clear();
        solicitarOrdenHabilitada=false;
        lblConfeccionarOrdenPaso.setTextColor(Color.parseColor("#7a7a7a"));
        lblConfeccionarOrden.setTextColor(Color.parseColor("#7a7a7a"));
        lnConfeccionarOrden.setVisibility(View.VISIBLE);
        lnConfeccionarOrdenResultados.setVisibility(View.GONE);
        lnOrdenOdonto.setVisibility(View.GONE);
        lnDetallePrestacionOdontologiaSolicitarTotal.setVisibility(View.GONE);
        lnCoseguroTotal.setVisibility(View.GONE);
        listaPrestacionesOdonto.setVisibility(View.GONE);
        lnDetallePrestacionOdontologiaAgregar.setVisibility(View.GONE);
        lblOdontoDiente.setVisibility(View.GONE);
        lblOdontoCara.setVisibility(View.GONE);
        txtOdontoDiente.setVisibility(View.GONE);
        txtOdontoCara.setVisibility(View.GONE);
        lblPrestacionOdontoSeleccionada.setVisibility(View.GONE);
        lnDetalleCarasSeleccionadas.setVisibility(View.GONE);
    }

    private void habilitaSolicitarOrden(){
        solicitarOrdenHabilitada=true;
        lblConfeccionarOrdenPaso.setTextColor(Color.parseColor("#130657"));
        lblConfeccionarOrden.setTextColor(Color.parseColor("#130657"));
    }

    private void mostrarSolicitarOrden(){
        if (!solicitarOrdenHabilitada){
            return;
        }
        lnConfeccionarOrden.setVisibility(View.GONE);
        lnConfeccionarOrdenResultados.setVisibility(View.VISIBLE);
        if (tipoOrdenActual.equals("Odontológica")){
            lnOrdenOdonto.setVisibility(View.VISIBLE);
        }
    }

    private void solicitarOrdenOdontologica(){
        final String idUsuario="T"+datosPrestador.leerIdUsuario();
        final String idAfiliado=_idAfiliado;
        final String xmlOrdenAmb=convierteXMLOdontoParaAgregar();
        final String xmlOrdenAmbCaras=convierteXMLOdontoParaAgregarCaras();
        final String idDiagnostico="00000000-0000-0000-0000-000000000000";
        final String idProfesional="00000000-0000-0000-0000-000000000000";
        final String idConvenio="00000000-0000-0000-0000-000000000000";
        final String tipoOrden="ODONTO";
        final String idMotivoConsulta="00000000-0000-0000-0000-000000000000";
        final String pedMedico="";
        final String comentario="";
        final String idTipoAccidente="00000000-0000-0000-0000-000000000000";
        final String idModalidadOrden="00000000-0000-0000-0000-000000000000";
        final String idProfesionalSolicitante="00000000-0000-0000-0000-000000000000";
        final String idProfDerivacion="00000000-0000-0000-0000-000000000000";
        final String chkFacturaManual="False";
        final String xmlOrdenOtrasPrestacionesPrestadores="<root></root>";
        final String chkHayPedidoMedico="False";
        final String chkEsGuardia="False";
        final String idOrdenAmbulatorio="";

        try{
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Intentando solicitar la orden");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idConexion", idUsuario);
            params.put("idAfiliado", idAfiliado);
            params.put("xmlOrdenAmb", xmlOrdenAmb);
            params.put("xmlOrdenAmbCaras", xmlOrdenAmbCaras);
            params.put("idDiagnostico", idDiagnostico);
            params.put("idProfesional", idProfesional);
            params.put("idConvenio", idConvenio);
            params.put("tipoOrden", tipoOrden);
            params.put("idMotivoConsulta", idMotivoConsulta);
            params.put("pedMedico", pedMedico);
            params.put("comentario", comentario);
            params.put("idTipoAccidente", idTipoAccidente);
            params.put("idModalidadOrden", idModalidadOrden);
            params.put("idProfesionalSolicitante", idProfesionalSolicitante);
            params.put("idProfDerivacion", idProfDerivacion);
            params.put("chkFacturaManual", chkFacturaManual);
            params.put("xmlOrdenOtrasPrestacionesPrestadores", xmlOrdenOtrasPrestacionesPrestadores);
            params.put("chkHayPedidoMedico", chkHayPedidoMedico);
            params.put("chkEsGuardia", chkEsGuardia);
            params.put("idOrdenAmbulatorio", idOrdenAmbulatorio);

            fabrica_WS.WEBAgregarOrdenOdonto(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                    if (valor.substring(0, 2).equals("00")) {
                        muestraConfirmacionSolicitud(valores.getElementsByTagName("mensaje").item(0).getTextContent(),"");
                    }else if (valor.substring(0, 2).equals("10")) {
                        muestraConfirmacionSolicitud(valores.getElementsByTagName("mensaje").item(0).getTextContent(),"amarillo");
                    }else if (valor.substring(0, 2).equals("15")) {
                        dialogo = fabrica_dialogos.dlgError(getContext(), valores.getElementsByTagName("mensaje").item(0).getTextContent());
                        return;
                    }

                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception ex) {
            dialogoSec = fabrica_dialogos.dlgError(getContext(), "Error al solicitar la orden: "+ex.getMessage());
        }
    }

    private void muestraConfirmacionSolicitud(String mensaje, String color) {
        mensaje=mensaje.replace("<br/>","");
        dialogo = fabrica_dialogos.dlgConfirmarDatosOcultaBotonNO(getContext(), "Solicitud de orden", mensaje,R.drawable.ordenes_left_side,color);
        ImageView btnSi=(ImageView)dialogo.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inicializarNuevaOrden(view);
                dialogo.dismiss();
            }
        });
    }

    private String convierteXMLOdonto(){
        String cadenaXML="<root>";
        Iterator<prestaciones> itrPrestaciones = listaPrestaciones.iterator();
        itrPrestaciones = listaPrestaciones.iterator();
        while(itrPrestaciones.hasNext()){
            prestaciones prestacionActualInf = itrPrestaciones.next();
            cadenaXML+="<table>";
            cadenaXML+="<Item0>"+prestacionActualInf.getTipoCodigo()+"</Item0>";
            cadenaXML+="<Item1>"+prestacionActualInf.getIdCodigo()+"</Item1>";
            cadenaXML+="<Item2>"+prestacionActualInf.getDescripcion()+"</Item2>";
            cadenaXML+="<Item3>"+prestacionActualInf.getCantidad()+"</Item3>";
            cadenaXML+="<Item4>"+prestacionActualInf.getPrecioUnit()+"</Item4>";
            cadenaXML+="<Item5>"+prestacionActualInf.getSubtotal()+"</Item5>";
            cadenaXML+="<Item6>"+prestacionActualInf.getIdPlantillaValores()+"</Item6>";
            cadenaXML+="<Item7>"+prestacionActualInf.getIdConvenio()+"</Item7>";
            cadenaXML+="<Item8>"+prestacionActualInf.getIdReglaCoseguro()+"</Item8>";
            cadenaXML+="<Item9>"+prestacionActualInf.getIdCoseguroAgrupado()+"</Item9>";
            cadenaXML+="<Item10>"+prestacionActualInf.getEsPorPresupuesto()+"</Item10>";
            cadenaXML+="<Item11>"+prestacionActualInf.getCodDiente()+"</Item11>";
            cadenaXML+="<Item12>"+prestacionActualInf.getCodCara()+"</Item12>";
            cadenaXML+="<Item13>"+prestacionActualInf.getDiente()+"</Item13>";
            //cadenaXML+="<Item14>"+prestacionActualInf.getCara()+"</Item14>";
            cadenaXML+="</table>";
        }
        cadenaXML+="</root>";
        return cadenaXML;
    }

    private String convierteXMLOdontoParaAgregar(){
        String cadenaXML="<root>";
        Iterator<prestaciones> itrPrestaciones = listaPrestaciones.iterator();
        itrPrestaciones = listaPrestaciones.iterator();
        while(itrPrestaciones.hasNext()){
            prestaciones prestacionActualInf = itrPrestaciones.next();
            cadenaXML+="<table>";
            cadenaXML+="<Item0>"+prestacionActualInf.getIdCodigo()+"</Item0>";
            cadenaXML+="<Item1>"+prestacionActualInf.getDescripcion()+"</Item1>";
            cadenaXML+="<Item2>"+prestacionActualInf.getCantidad()+"</Item2>";
            cadenaXML+="<Item3>"+prestacionActualInf.getPrecioUnit()+"</Item3>";
            cadenaXML+="<Item4>"+prestacionActualInf.getSubtotal()+"</Item4>";
            cadenaXML+="<Item5>"+prestacionActualInf.getIdPlantillaValores()+"</Item5>";
            cadenaXML+="<Item6>"+prestacionActualInf.getIdConvenio()+"</Item6>";
            cadenaXML+="<Item7>"+prestacionActualInf.getIdReglaCoseguro()+"</Item7>";
            cadenaXML+="<Item8>"+prestacionActualInf.getIdCoseguroAgrupado()+"</Item8>";
            cadenaXML+="<Item9>"+prestacionActualInf.getEsPorPresupuesto()+"</Item9>";
            cadenaXML+="<Item10>"+prestacionActualInf.getCodDiente()+"</Item10>";
            cadenaXML+="<Item11>"+prestacionActualInf.getCodCara()+"</Item11>";
            cadenaXML+="<Item12>"+prestacionActualInf.getDiente()+"</Item12>";
            //cadenaXML+="<Item13>"+prestacionActualInf.getCara()+"</Item13>";
            cadenaXML+="</table>";
        }
        cadenaXML+="</root>";
        return cadenaXML;
    }

    private String convierteXMLOdontoParaAgregarCaras() {
        String cadenaXML="<root>";
        Iterator<carasEnOrden> itrPrestaciones = listaCarasEnOrden.iterator();
        while(itrPrestaciones.hasNext()){
            carasEnOrden prestacionActualInf = itrPrestaciones.next();
            cadenaXML+="<table>";
            cadenaXML+="<Item0>"+prestacionActualInf.getIdPlantillaValores()+"</Item0>";
            cadenaXML+="<Item1>"+prestacionActualInf.getDiente()+"</Item1>";
            cadenaXML+="<Item2>"+prestacionActualInf.getCodCara()+"</Item2>";
            cadenaXML+="</table>";
        }
        cadenaXML+="</root>";
        return cadenaXML;
    }
    //endregion

    //region prestacion odontologia
    private void buscarPrestacionOdonto() {
        try{
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las prestaciones");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idUsuario", datosPrestador.leerIdUsuario());
            params.put("idAfiliado", _idAfiliado);
            params.put("cadena", edtPrestacionOdonto.getText().toString());

            fabrica_WS.APPEXTBuscarPrestacionOdonto(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idPlantillaValorDetalle = new String[valores.getElementsByTagName("idPlantillaValorDetalle").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idPlantillaValorDetalle").getLength(); i++) {
                        idPlantillaValorDetalle[i] = valores.getElementsByTagName("idPlantillaValorDetalle").item(i).getTextContent();
                    }
                    String[] prestacion = new String[valores.getElementsByTagName("prestacion").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("prestacion").getLength(); i++) {
                        prestacion[i] = valores.getElementsByTagName("prestacion").item(i).getTextContent();
                    }
                    String[] entorno = new String[valores.getElementsByTagName("entorno").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("entorno").getLength(); i++) {
                        entorno[i] = valores.getElementsByTagName("entorno").item(i).getTextContent();
                    }
                    mostrarPrestacionOdonto(idPlantillaValorDetalle, prestacion, entorno);
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las prestaciones odontológicas:\n" + e.getMessage());
        }
    }

    private void mostrarPrestacionOdonto(String[] idPlantillaValorDetalle, String[] prestacion, String[] entorno) {
        try {
            listaPrestacionesOdontoBusqueda.removeAllViews();
            for (int i = 0; i < idPlantillaValorDetalle.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnPrestacion = inflater.inflate(R.layout.lista_grupo_fam, null);

                final String _idPlantillaValorDetalle=idPlantillaValorDetalle[i];
                final String _prestacion=prestacion[i];
                final String _entorno=entorno[i];

                TextView idFila = (TextView) lnPrestacion.findViewById(R.id.idFila);
                TextView oculto1 = (TextView) lnPrestacion.findViewById(R.id.oculto1);
                TextView linea1 = (TextView) lnPrestacion.findViewById(R.id.linea1);
                TextView linea2 = (TextView) lnPrestacion.findViewById(R.id.linea2);

                idFila.setText(_idPlantillaValorDetalle);
                oculto1.setText(_entorno);
                linea1.setText(_prestacion);
                linea2.setText("");
                linea2.setVisibility(View.GONE);

                lnPrestacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listaPrestacionesOdontoBusqueda.setVisibility(View.GONE);
                        _idPlantillavaloresDetalleOdonto = _idPlantillaValorDetalle;
                        _nombrePrestacionOdonto=_prestacion;
                        _entornoActualOdonto=_entorno;

                        lblPrestacionOdontoSeleccionada.setText(_nombrePrestacionOdonto);
                        lblPrestacionOdontoSeleccionada.setVisibility(View.VISIBLE);
                        lnTituloPrestacionesOdonto.setVisibility(View.VISIBLE);
                        if (_entornoActualOdonto.equals("CARA")) {
                            lblOdontoDiente.setVisibility(View.VISIBLE);
                            lblOdontoCara.setVisibility(View.VISIBLE);
                            txtOdontoDiente.setVisibility(View.VISIBLE);
                            txtOdontoCara.setVisibility(View.VISIBLE);
                        }else if (_entornoActualOdonto.equals("DIENTE")) {
                            lblOdontoDiente.setVisibility(View.VISIBLE);
                            txtOdontoDiente.setVisibility(View.VISIBLE);
                        }
                        lnTxtPrestacionesOdonto.setVisibility(View.VISIBLE);
                        txtOdontoDiente.setText("---");
                        txtOdontoCara.setText("---");
                        _codDiente="";
                        _diente="";
                        _codCara="";
                        _nombreCara="";
                        lnDetallePrestacionOdontologiaAgregar.setVisibility(View.VISIBLE);
                    }
                });
                listaPrestacionesOdontoBusqueda.addView(lnPrestacion);
            }
            listaPrestacionesOdontoBusqueda.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las prestaciones odontológicas:\n" + e.getMessage());
        }
    }

    private void buscarDientes() {
        try{
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las piezas");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idUsuario", datosPrestador.leerIdUsuario());
            params.put("idPlantillaValoresDetalle", _idPlantillavaloresDetalleOdonto);

            fabrica_WS.APPEXTBuscarPrestacionOdontoDientes(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] numDiente = new String[valores.getElementsByTagName("numDiente").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("numDiente").getLength(); i++) {
                        numDiente[i] = valores.getElementsByTagName("numDiente").item(i).getTextContent();
                    }
                    mostrarDientes(numDiente);
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las piezas odontológicas:\n" + e.getMessage());
        }
    }

    private void mostrarDientes(String[] numDiente){
        try {
            _codCara="";
            _nombreCara="";
            listaDientesOdonto.removeAllViews();
            for (int i = 0; i < numDiente.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnDientes = inflater.inflate(R.layout.lista_un_renglon_tilde, null);

                final String _numDiente=numDiente[i];

                TextView linea1 = (TextView) lnDientes.findViewById(R.id.linea1);

                linea1.setText(_numDiente);

                lnDientes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _diente=_numDiente;
                        _codDiente=_numDiente;
                        txtOdontoDiente.setText(_numDiente);
                        listaDientesOdonto.setVisibility(View.GONE);
                    }
                });
                listaDientesOdonto.addView(lnDientes);
            }
            listaDientesOdonto.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las piezas odontológicas:\n" + e.getMessage());
        }
    }

    private void buscarCaras() {
        if (_diente.equals("")){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Antes de seleccionar la cara, tiene que indicar el diente");
            return;
        }
        _codCara="";
        _nombreCara="";

        try{
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las piezas");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idUsuario", datosPrestador.leerIdUsuario());
            params.put("idPlantillaValoresDetalle", _idPlantillavaloresDetalleOdonto);
            params.put("numDiente", _diente);

            fabrica_WS.APPEXTBuscarPrestacionOdontoCaras(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] codCara = new String[valores.getElementsByTagName("codCara").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("codCara").getLength(); i++) {
                        codCara[i] = valores.getElementsByTagName("codCara").item(i).getTextContent();
                    }
                    String[] cara = new String[valores.getElementsByTagName("cara").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("cara").getLength(); i++) {
                        cara[i] = valores.getElementsByTagName("cara").item(i).getTextContent();
                    }
                    mostrarCaras(codCara,cara);
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las piezas odontológicas:\n" + e.getMessage());
        }
    }

    private void mostrarCaras(String[] codCara, String[] cara){
        try {
            _codCara="";
            _nombreCara="";
            listaCarasOdonto.removeAllViews();
            for (int i = 0; i < codCara.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnCaras = inflater.inflate(R.layout.lista_un_renglon_tilde, null);

                final String _codCaraAct=codCara[i];
                final String _cara=cara[i];

                TextView idFila = (TextView) lnCaras.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnCaras.findViewById(R.id.linea1);

                idFila.setText(_codCaraAct);
                linea1.setText(_cara);

                lnCaras.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _codCara=_codCaraAct;
                        _nombreCara=_cara;
                        txtOdontoCara.setText(_nombreCara);
                        listaCarasOdonto.setVisibility(View.GONE);
                        btnAgregarCara.setVisibility(View.VISIBLE);
                    }
                });
                listaCarasOdonto.addView(lnCaras);
            }
            listaCarasOdonto.setVisibility(View.VISIBLE);
            btnAgregarCara.setVisibility(View.GONE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las caras odontológicas:\n" + e.getMessage());
        }
    }

    private void agregarCaraActual() {
        if (_codCara.equals("")){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Falta indicar la cara");
            return;
        }
        if (verificaCaraEsta(_codCara)) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "No se pueden agregar una cara que ya esta en la prestación");
            return;
        }
        caras prestacionActual = new caras();
        prestacionActual.setNombre(_nombreCara);
        prestacionActual.setCodCara(_codCara);
        listaCaras.add(prestacionActual);
        muestraListaCaras();
        txtOdontoCara.setText("---");
        _codCara="";
        btnAgregarCara.setVisibility(View.GONE);
    }

    private void muestraListaCaras() {
        if (listaCaras.size()>0) {
            String fraseCaras = "Caras: ";
            Iterator<caras> itrPrestaciones = listaCaras.iterator();
            itrPrestaciones = listaCaras.iterator();
            while (itrPrestaciones.hasNext()) {
                caras prestacionActualInf = itrPrestaciones.next();
                fraseCaras = fraseCaras + prestacionActualInf.getNombre() + "; ";
            }

            //TextView txtCarasSeleccionadas = (TextView) findViewById(R.id.txtCarasSeleccionadas);
            txtCarasSeleccionadas.setText(fraseCaras);
            lnDetalleCarasSeleccionadas.setVisibility(View.VISIBLE);
        }else{
            lnDetalleCarasSeleccionadas.setVisibility(View.GONE);
        }
    }

    private boolean verificaCaraEsta(String cara){
        Iterator<caras> itrPrestaciones = listaCaras.iterator();
        itrPrestaciones = listaCaras.iterator();
        while(itrPrestaciones.hasNext()){
            caras prestacionActualInf = itrPrestaciones.next();
            if(prestacionActualInf.getCodCara().equals(cara)
            )
                return true;
        }
        return false;
    }

    private void eliminarUltimaCara(){
        listaCaras.remove(listaCaras.size()-1);
        muestraListaCaras();
    }

    private void agregarPrestacion(){
        if (txtOdontoCant.getText().length()==0){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Le falta indicar la cantidad");
            return;
        }else if (_entornoActualOdonto.equals("CARA") && _diente.equals("")){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Le falta indicar el diente");
            return;
        }else if (_entornoActualOdonto.equals("CARA") && listaCaras.size()==0){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Le falta indicar la cara");
            return;
        }else if (_entornoActualOdonto.equals("DIENTE") && _diente.equals("")){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Le falta indicar el diente");
            return;
        }else if (verificaPrestacionOdontoEsta(_idPlantillavaloresDetalleOdonto, _diente)) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "No se pueden agregar una prestación que ya esta en la orden");
            return;
        }

        try{
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando el coseguro de la prestación");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idUsuario", datosPrestador.leerIdUsuario());
            params.put("idPlantillaValores", _idPlantillavaloresDetalleOdonto);
            params.put("xmlOrden", convierteXMLOdonto());
            params.put("idAfiliado", _idAfiliado);

            fabrica_WS.APPEXTBuscarPrestacionOdontoCoseguro(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    borrarTodasPrestacionesOdonto();
                    coseguroTotal=valores.getElementsByTagName("coseguroTotal").item(0).getTextContent();
                    for (int i = 0; i < valores.getElementsByTagName("idCodigoActual").getLength(); i++) {
                        prestaciones prestacionActual = new prestaciones();
                        prestacionActual.setTipoCodigo(valores.getElementsByTagName("tipoCodigoActual").item(i).getTextContent());
                        prestacionActual.setIdCodigo(valores.getElementsByTagName("idCodigoActual").item(i).getTextContent());
                        prestacionActual.setDescripcion(valores.getElementsByTagName("nombrePrestacionActual").item(i).getTextContent());
                        prestacionActual.setCantidad(txtOdontoCant.getText().toString());
                        prestacionActual.setPrecioUnit(valores.getElementsByTagName("coseguroPrestacionActual").item(i).getTextContent());
                        prestacionActual.setSubtotal(valores.getElementsByTagName("coseguroPrestacionActual").item(i).getTextContent());
                        prestacionActual.setIdPlantillaValores(valores.getElementsByTagName("idPlantillaValorDetalleActual").item(i).getTextContent());
                        prestacionActual.setIdConvenio(valores.getElementsByTagName("idConvenioActual").item(i).getTextContent());
                        prestacionActual.setIdReglaCoseguro(valores.getElementsByTagName("idReglaCoseguroActual").item(i).getTextContent());
                        prestacionActual.setIdCoseguroAgrupado(valores.getElementsByTagName("idCoseguroAgrupadoActual").item(i).getTextContent());
                        prestacionActual.setEsPorPresupuesto(valores.getElementsByTagName("esPorPresupuestoActual").item(i).getTextContent());
                        prestacionActual.setCodDiente(_codDiente);
                        prestacionActual.setDiente(_diente);
                        listaPrestaciones.add(prestacionActual);
                    }

                    for (int i = 0; i < valores.getElementsByTagName("idCodigoNuevaOrden").getLength(); i++) {
                        prestaciones prestacionActual = new prestaciones();
                        prestacionActual.setTipoCodigo(valores.getElementsByTagName("tipoCodigoNuevaOrden").item(i).getTextContent());
                        prestacionActual.setIdCodigo(valores.getElementsByTagName("idCodigoNuevaOrden").item(i).getTextContent());
                        prestacionActual.setDescripcion(valores.getElementsByTagName("prestacionNuevaOrden").item(i).getTextContent());
                        prestacionActual.setCantidad(valores.getElementsByTagName("cantidadNuevaOrden").item(i).getTextContent());
                        prestacionActual.setPrecioUnit(valores.getElementsByTagName("precUnitNuevaOrden").item(i).getTextContent());
                        prestacionActual.setSubtotal(valores.getElementsByTagName("subtotalNuevaOrden").item(i).getTextContent());
                        prestacionActual.setIdPlantillaValores(valores.getElementsByTagName("idPlantillaValorNuevaOrden").item(i).getTextContent());
                        prestacionActual.setIdConvenio(valores.getElementsByTagName("idConvenioNuevaOrden").item(i).getTextContent());
                        prestacionActual.setIdReglaCoseguro(valores.getElementsByTagName("idReglaCoseguroNuevaOrden").item(i).getTextContent());
                        prestacionActual.setIdCoseguroAgrupado(valores.getElementsByTagName("idCoseguroAgrupadoNuevaOrden").item(i).getTextContent());
                        prestacionActual.setEsPorPresupuesto(valores.getElementsByTagName("esPorPresupuestoNuevaOrden").item(i).getTextContent());
                        prestacionActual.setCodDiente(valores.getElementsByTagName("codDienteNuevaOrden").item(i).getTextContent());
                        prestacionActual.setCodCara(valores.getElementsByTagName("codCaraNuevaOrden").item(i).getTextContent());
                        prestacionActual.setDiente(valores.getElementsByTagName("dienteNuevaOrden").item(i).getTextContent());
                        listaPrestaciones.add(prestacionActual);
                    }

                    agregaCarasPrestacionActual(_idPlantillavaloresDetalleOdonto, _diente);
                    muestraPrestaciones();

                    listaPrestacionesOdonto.setVisibility(View.VISIBLE);
                    lblPrestacionOdontoSeleccionada.setVisibility(View.GONE);
                    lnTituloPrestacionesOdonto.setVisibility(View.GONE);
                    lnTxtPrestacionesOdonto.setVisibility(View.GONE);
                    lblOdontoCara.setVisibility(View.GONE);
                    txtOdontoCara.setVisibility(View.GONE);
                    btnAgregarCara.setVisibility(View.GONE);
                    lnDetalleCarasSeleccionadas.setVisibility(View.GONE);
                    borrarTodasLasCaras();
                    _codCara = "";
                    _codDiente = "";
                    _idPlantillavaloresDetalleOdonto = "00000000-0000-0000-0000-000000000000";
                    /*seleccionPrestacionOdonto.setVisibility(View.VISIBLE);
                    lnConsumoOdontologia.setVisibility(View.VISIBLE);
                    lstPrestacionesOdonto.setVisibility(View.GONE);
                    viewLstPrestacionOdonto.setVisibility(View.GONE);
                    lnDetallePrestacionOdontologia.setVisibility(View.GONE);
                    lnDetallePrestacionOdontologiaDiente.setVisibility(View.GONE);
                    lnDetallePrestacionOdontologiaCara.setVisibility(View.GONE);*/
                    lnDetallePrestacionOdontologiaAgregar.setVisibility(View.GONE);
                    lnDetallePrestacionOdontologiaSolicitarTotal.setVisibility(View.VISIBLE);
                    edtPrestacionOdonto.setText("");
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las prestaciones odontológicas:\n" + e.getMessage());
        }
    }

    private boolean verificaPrestacionOdontoEsta(String idPrestacion, String diente){
        Iterator<prestaciones> itrPrestaciones = listaPrestaciones.iterator();
        itrPrestaciones = listaPrestaciones.iterator();
        while(itrPrestaciones.hasNext()){
            prestaciones prestacionActualInf = itrPrestaciones.next();
            if(prestacionActualInf.getIdPlantillaValores().equals(idPrestacion)
                    && prestacionActualInf.getCodDiente().equals(diente)
            )
                return true;
        }
        return false;
    }

    private void muestraPrestaciones(){
        listaPrestacionesOdonto.removeAllViews();
        if (listaPrestaciones.size()>0) {
            Iterator<prestaciones> itrPrestaciones = listaPrestaciones.iterator();
            while (itrPrestaciones.hasNext()) {
                prestaciones prestacionActualInf = itrPrestaciones.next();
                LayoutInflater inflater = this.getLayoutInflater();
                View lnPrestaciones = inflater.inflate(R.layout.lista_un_renglon_prestaciones_odonto, null);
                final String idFilaS, linea1S;

                TextView txtCantidad = (TextView) lnPrestaciones.findViewById(R.id.txtCantidad);
                TextView txtPrestacion = (TextView) lnPrestaciones.findViewById(R.id.txtPrestacion);
                TextView txtDiente = (TextView) lnPrestaciones.findViewById(R.id.txtDiente);
                TextView txtCara = (TextView) lnPrestaciones.findViewById(R.id.txtCara);
                TextView idPrestacion = (TextView) lnPrestaciones.findViewById(R.id.idPrestacion);
                TextView codDiente = (TextView) lnPrestaciones.findViewById(R.id.codDiente);
                TextView codCara = (TextView) lnPrestaciones.findViewById(R.id.codCara);
                TextView txtCoseguro = (TextView) lnPrestaciones.findViewById(R.id.txtCoseguro);

                String fraseCaras = "";
                Iterator<carasEnOrden> itrCaraPrestaciones = listaCarasEnOrden.iterator();
                while (itrCaraPrestaciones.hasNext()) {
                    carasEnOrden prestacionCaraActualInf = itrCaraPrestaciones.next();
                    if (prestacionActualInf.getIdPlantillaValores().equals(prestacionCaraActualInf.getIdPlantillaValores())
                            && prestacionActualInf.getCodDiente().equals(prestacionCaraActualInf.getDiente())){
                        fraseCaras = fraseCaras + prestacionCaraActualInf.getCodCara() + "; ";
                    }
                }

                txtCantidad.setText(prestacionActualInf.getCantidad());
                txtPrestacion.setText(prestacionActualInf.getDescripcion());
                txtDiente.setText(prestacionActualInf.getDiente());
                txtCara.setText(fraseCaras);
                idPrestacion.setText(prestacionActualInf.getIdPlantillaValores());
                codDiente.setText(prestacionActualInf.getCodDiente());
                codCara.setText(prestacionActualInf.getCodCara());
                txtCoseguro.setText(prestacionActualInf.getSubtotal());

                lnPrestaciones.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox chkActual = (CheckBox) v.findViewById(R.id.chkPrestacion);
                        if (chkActual.isChecked()) {
                            chkActual.setChecked(false);
                        } else {
                            chkActual.setChecked(true);
                        }
                    }
                });
                listaPrestacionesOdonto.addView(lnPrestaciones);
            }
            lnDetallePrestacionOdontologiaSolicitarTotal.setVisibility(View.VISIBLE);
            lnCoseguroTotal.setVisibility(View.VISIBLE);
            txtCoseguroTotal.setText(coseguroTotal);
        }else{
            lnDetallePrestacionOdontologiaSolicitarTotal.setVisibility(View.GONE);
            lnCoseguroTotal.setVisibility(View.GONE);
        }
    }

    private void borrarTodasPrestacionesOdonto() {
        listaPrestaciones.clear();
        muestraPrestaciones();
    }

    private void agregaCarasPrestacionActual(String _idPlantillavaloresDetalleOdonto, String diente) {
        Iterator<caras> itrCarasActuales = listaCaras.iterator();
        while (itrCarasActuales.hasNext()) {
            carasEnOrden carasActual = new carasEnOrden();
            caras prestacionActualInf = itrCarasActuales.next();
            carasActual.setIdPlantillaValores(_idPlantillavaloresDetalleOdonto);
            carasActual.setCodCara(prestacionActualInf.codCara);
            carasActual.setNombre(prestacionActualInf.nombre);
            carasActual.setDiente(diente);
            listaCarasEnOrden.add(carasActual);
        }
        borrarTodasLasCaras();
    }

    private void borrarTodasLasCaras() {
        listaCaras.clear();
    }

    private void eliminarPrestaciones() {
        for (int i = 0; i < listaPrestacionesOdonto.getChildCount(); i++) {
            View v = listaPrestacionesOdonto.getChildAt(i);
            CheckBox chkActual=(CheckBox) v.findViewById(R.id.chkPrestacion);
            if (chkActual.isChecked()){
                TextView idPrestacion=(TextView) v.findViewById(R.id.idPrestacion);
                TextView codDiente=(TextView) v.findViewById(R.id.codDiente);
                TextView codCara=(TextView) v.findViewById(R.id.codCara);
                eliminarPrestacionActual(idPrestacion.getText().toString(),codDiente.getText().toString());
                eliminarCarasAlEmiliarPrestacion(idPrestacion.getText().toString(),codDiente.getText().toString());
            }
        }
        muestraPrestaciones();
    }

    private void eliminarPrestacionActual(String idFila, String codDiente){
        Iterator<prestaciones> itrPrestaciones = listaPrestaciones.iterator();
        itrPrestaciones = listaPrestaciones.iterator();
        while(itrPrestaciones.hasNext()){
            prestaciones prestacionActualInf = itrPrestaciones.next();
            if(prestacionActualInf.getIdPlantillaValores().equals(idFila)
                    && prestacionActualInf.getCodDiente().equals(codDiente))
                itrPrestaciones.remove();
        }
    }

    private void eliminarCarasAlEmiliarPrestacion(String _idPlantillaValores, String codDiente) {
        ArrayList<Integer> indicesEliminar = new ArrayList<Integer>();
        for (int i = 0; i < listaCarasEnOrden.size(); i++) {
            if (listaCarasEnOrden.get(i).getIdPlantillaValores().equals(_idPlantillaValores)
                    && listaCarasEnOrden.get(i).getDiente().equals(codDiente)){
                indicesEliminar.add(i);
            }
        }

        Collections.sort(indicesEliminar, Collections.reverseOrder());

        for (int i = 0; i < indicesEliminar.size(); i++) {
            listaCarasEnOrden.remove(Integer.parseInt(indicesEliminar.get(i).toString()));
        }
    }
    //#endregion

    private class caras{
        private String nombre;
        private String codCara;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String _nombre) {
            this.nombre = _nombre;
        }

        public String getCodCara() {
            return codCara;
        }

        public void setCodCara(String _codCara) {
            this.codCara = _codCara;
        }

    }

    private class carasEnOrden{
        private String nombre;
        private String codCara;
        private String idPlantillaValores;
        private String diente;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String _nombre) {
            this.nombre = _nombre;
        }

        public String getCodCara() {
            return codCara;
        }

        public void setCodCara(String _codCara) {
            this.codCara = _codCara;
        }

        public String getIdPlantillaValores() {
            return idPlantillaValores;
        }

        public void setIdPlantillaValores(String _idPlantillaValores) {
            this.idPlantillaValores = _idPlantillaValores;
        }

        public String getDiente() {
            return diente;
        }

        public void setDiente(String _diente) {
            this.diente = _diente;
        }
    }

    private class prestaciones{
        private String tipoCodigo;
        private String idCodigo;
        private String descripcion;
        private String cantidad;
        private String precioUnit;
        private String subtotal;
        private String idPlantillaValores;
        private String idConvenio;
        private String idReglaCoseguro;
        private String idCoseguroAgrupado;
        private String esPorPresupuesto;
        private String codDiente;
        private String codCara;
        private String diente;
        private String cara;

        public String getTipoCodigo() {
            return tipoCodigo;
        }

        public void setTipoCodigo(String _tipoCodigo) {
            this.tipoCodigo = _tipoCodigo;
        }

        public String getIdCodigo() {
            return idCodigo;
        }

        public void setIdCodigo(String _idCodigo) {
            this.idCodigo = _idCodigo;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String _descripcion) {
            this.descripcion = _descripcion;
        }

        public String getCantidad() {
            return cantidad;
        }

        public void setCantidad(String _cantidad) {
            this.cantidad = _cantidad;
        }

        public String getPrecioUnit() {
            return precioUnit;
        }

        public void setPrecioUnit(String _precioUnit) {
            this.precioUnit = _precioUnit;
        }

        public String getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(String _subtotal) {
            this.subtotal = _subtotal;
        }

        public String getIdPlantillaValores() {
            return idPlantillaValores;
        }

        public void setIdPlantillaValores(String _idPlantillaValores) {
            this.idPlantillaValores = _idPlantillaValores;
        }

        public String getIdConvenio() {
            return idConvenio;
        }

        public void setIdConvenio(String _idConvenio) {
            this.idConvenio = _idConvenio;
        }

        public String getIdReglaCoseguro() {
            return idReglaCoseguro;
        }

        public void setIdReglaCoseguro(String _idReglaCoseguro) {
            this.idReglaCoseguro = _idReglaCoseguro;
        }

        public String getIdCoseguroAgrupado() {
            return idCoseguroAgrupado;
        }

        public void setIdCoseguroAgrupado(String _idCoseguroAgrupado) {
            this.idCoseguroAgrupado = _idCoseguroAgrupado;
        }

        public String getEsPorPresupuesto() {
            return esPorPresupuesto;
        }

        public void setEsPorPresupuesto(String _esPorPresupuesto) {
            this.esPorPresupuesto = _esPorPresupuesto;
        }

        public String getCodDiente() {
            return codDiente;
        }

        public void setCodDiente(String _codDiente) {
            this.codDiente = _codDiente;
        }

        public String getCodCara() {
            return codCara;
        }

        public void setCodCara(String _codCara) {
            this.codCara = _codCara;
        }

        public String getDiente() {
            return diente;
        }

        public void setDiente(String _diente) {
            this.diente = _diente;
        }

        /*public String getCara() {
            return cara;
        }

        public void setCara(String _cara) {
            this.cara = _cara;
        }*/
    }



    @Override
    public void onAttach (Context context){
        super.onAttach(context);
        tiempoMulticlick=Float.parseFloat(getString(R.string.tiempoMulticlick));
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
