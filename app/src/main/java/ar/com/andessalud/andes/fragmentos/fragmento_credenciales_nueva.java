package ar.com.andessalud.andes.fragmentos;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.HashMap;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_credenciales_nueva extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo;
    private LinearLayout lnAfiliado, lnAfiliadoResultados, listaAfiliado, lnDelegacion
            ,lnDelegacionResultados, listaDelegacion, lnBotonesConfirmar;
    private TextView lblAfiliadoSeleccionado, lblSeleccionarDelegacionPaso, lblSeleccionarDelegacion
            ,lblDelegacionSeleccionado;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    private String _idAfiliado="", _nombreAfiliado="", _idDelegacion="", _nombreDelegacion=""
            ,_domicilioDelegacion="";
    private boolean delegacionHabilitado=false;
    public fragmento_credenciales_nueva() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_credenciales_nueva, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //#region seleccionar afiliado
        lnAfiliado = (LinearLayout) view.findViewById(R.id.lnAfiliado);
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultados = (LinearLayout) view.findViewById(R.id.lnAfiliadoResultados);
        lnAfiliadoResultados.setVisibility(View.GONE);
        lblAfiliadoSeleccionado = (TextView) view.findViewById(R.id.lblAfiliadoSeleccionado);
        listaAfiliado = (LinearLayout) view.findViewById(R.id.listaAfiliado);
        listaAfiliado.setVisibility(View.GONE);

        lnAfiliado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAfiliado();
            }
        });

        lnAfiliadoResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAfiliado();
            }
        });
        //#endregion

        //#region seleccionar delegacion
        lnDelegacion = (LinearLayout) view.findViewById(R.id.lnDelegacion);
        lnDelegacion.setVisibility(View.VISIBLE);
        lnDelegacionResultados = (LinearLayout) view.findViewById(R.id.lnDelegacionResultados);
        lnDelegacionResultados.setVisibility(View.GONE);
        lblSeleccionarDelegacionPaso = (TextView) view.findViewById(R.id.lblSeleccionarDelegacionPaso);
        lblDelegacionSeleccionado = (TextView) view.findViewById(R.id.lblDelegacionSeleccionado);
        lblSeleccionarDelegacion= (TextView) view.findViewById(R.id.lblSeleccionarDelegacion);
        listaDelegacion = (LinearLayout) view.findViewById(R.id.listaDelegacion);
        listaDelegacion.setVisibility(View.GONE);

        lnDelegacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDelegacion();
            }
        });

        lnDelegacionResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDelegacion();
            }
        });
        //#endregion

        lnBotonesConfirmar= (LinearLayout) view.findViewById(R.id.lnBotonesConfirmar);
        lnBotonesConfirmar.setVisibility(View.GONE);
        lnBotonesConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarDatos();
            }
        });
    }

    private void clickAfiliado(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultados.setVisibility(View.GONE);
        lblAfiliadoSeleccionado.setText("Seleccionar Afiliado");
        listaAfiliado.setVisibility(View.GONE);
        _idAfiliado="";
        _nombreAfiliado="";
        deshabilitaDelegacion();
        lnBotonesConfirmar.setVisibility(View.GONE);
        buscarAfiliado();
    }

    private void clickDelegacion(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        if (!delegacionHabilitado){
            return;
        }
        lnDelegacion.setVisibility(View.VISIBLE);
        lnDelegacionResultados.setVisibility(View.GONE);
        lblDelegacionSeleccionado.setText("Seleccionar delegación");
        listaDelegacion.setVisibility(View.GONE);
        _idDelegacion="";
        _nombreDelegacion="";
        lnBotonesConfirmar.setVisibility(View.GONE);
        buscarDelegacion();
    }

    //#region afiliado
    private void buscarAfiliado(){
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

    private void mostrarGrupoFam(String[] idAfiliados, String[] grupoFam, String[] numAfil, String[] tipoMensaje, String[] mensaje) {
        try {
            lnAfiliado.setVisibility(View.GONE);
            lnAfiliadoResultados.setVisibility(View.VISIBLE);
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
        habilitaDelegacion();
    }
    //#endregion

    //#region delegacion
    private void deshabilitaDelegacion(){
        delegacionHabilitado=false;
        lblSeleccionarDelegacionPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblSeleccionarDelegacion.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnDelegacion.setVisibility(View.VISIBLE);
        lnDelegacionResultados.setVisibility(View.GONE);
        listaDelegacion.setVisibility(View.GONE);
    }

    private void habilitaDelegacion(){
        delegacionHabilitado=true;
        lblSeleccionarDelegacionPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblSeleccionarDelegacion.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void buscarDelegacion() {
        try{
            lnDelegacion.setVisibility(View.GONE);
            lnDelegacionResultados.setVisibility(View.VISIBLE);
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las zonas");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", idAfiliado);

            fabrica_WS.APPBuscarDelegacionesExpendio(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] identificador = new String[valores.getElementsByTagName("idExpendioInterno").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idExpendioInterno").getLength(); i++) {
                        identificador[i] = valores.getElementsByTagName("idExpendioInterno").item(i).getTextContent();
                    }
                    String[] nombre = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        nombre[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    String[] direccion = new String[valores.getElementsByTagName("domicilio").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("domicilio").getLength(); i++) {
                        direccion[i] = valores.getElementsByTagName("domicilio").item(i).getTextContent();
                    }
                    String[] localidad = new String[valores.getElementsByTagName("localidad").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("localidad").getLength(); i++) {
                        localidad[i] = valores.getElementsByTagName("localidad").item(i).getTextContent();
                    }
                    mostrarDelegacionesExpendio(identificador, nombre, direccion, localidad);
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las delegaciones para la nueva credencial:\n"+ex.getMessage());
        }
    }

    private void mostrarDelegacionesExpendio(String[] identificador, String[] nombre, String[] direccion, String[] localidad) {
        try {
            listaDelegacion.removeAllViews();
            for (int i = 0; i < identificador.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnDelegacionActualS = inflater.inflate(R.layout.lista_tres_renglones_tilde, null);
                final String idDelegacionS, nombreS, domicilioS;
                idDelegacionS=identificador[i];
                nombreS=nombre[i];
                domicilioS=direccion[i]+" - "+localidad[i];

                TextView idFila = (TextView) lnDelegacionActualS.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnDelegacionActualS.findViewById(R.id.linea1);
                TextView linea2 = (TextView) lnDelegacionActualS.findViewById(R.id.linea2);
                TextView linea3 = (TextView) lnDelegacionActualS.findViewById(R.id.linea3);

                idFila.setText(idDelegacionS);
                linea1.setText(nombreS);
                linea2.setText(domicilioS);
                linea3.setVisibility(View.GONE);

                lnDelegacionActualS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seleccionarDelegacion(idDelegacionS, nombreS, domicilioS);
                    }
                });
                listaDelegacion.addView(lnDelegacionActualS);
            }
            listaDelegacion.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las delegaciones para la nueva credencial:\n"+e.getMessage());
        }
    }

    private void seleccionarDelegacion(String idDelegacionS, String nombreS, String domicilioS){
        _idDelegacion = idDelegacionS;
        _nombreDelegacion=nombreS;
        _domicilioDelegacion=domicilioS;

        lblDelegacionSeleccionado.setText(_nombreDelegacion);
        listaDelegacion.setVisibility(View.GONE);
        lnBotonesConfirmar.setVisibility(View.VISIBLE);
    }
    //#endregion

    //region confirmar datos
    private void confirmarDatos() {
        String mensajeDlg=_nombreAfiliado+"\n\n"+_nombreDelegacion;
        dialogo = fabrica_dialogos.dlgConfirmarDatos(getContext(), "Confirmar datos", mensajeDlg,R.drawable.side_credencial_nueva,"");
        ImageView btnSi=(ImageView)dialogo.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogo.dismiss();
                solicitarCredencialExpendio();
            }
        });
    }

    private void solicitarCredencialExpendio(){
        try{
            String yaRegistroID=funciones.verificaSenderID(getActivity(),3);
            if (yaRegistroID.equals("SD") || yaRegistroID.equals("NO")){
                String valorDevuelto="";
                if (yaRegistroID.equals("SD"))
                    valorDevuelto = "No se puede solicitar la nueva credencial porque todavía no se registra el dispositivo";
                else{
                    valorDevuelto="No se puede solicitar la nueva credencial porque no se pudo registrar el dispositivo";
                }
                dialogo = fabrica_dialogos.dlgError(getContext(), valorDevuelto);
                return;
            }

            SQLController database = new SQLController(getContext());
            final String idAfiliadoTitular = database.obtenerIDAfiliado();
            if (idAfiliadoTitular.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", idAfiliadoTitular+funciones.obtenerIMEI(getContext()));
            params.put("idDelegacion", _idDelegacion);
            params.put("idAfiliadoCredencial", _idAfiliado);

            fabrica_WS.APPAgregarCredencialNueva(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                    if (valor.substring(0, 2).equals("00")) {
                        String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                        dialogo = fabrica_dialogos.dlgAviso(getContext(), msgAviso);
                        LinearLayout lnMsgTotal=(LinearLayout)dialogo.findViewById(R.id.lnMsgTotal);
                        lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogo.dismiss();
                                trigger.fireChange("credencial_credencial_nueva_btnTermino");
                                return;
                            }
                        });
                    }else if (valor.substring(0, 2).equals("02")) {
                        String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                        dialogo = fabrica_dialogos.dlgAviso(getContext(), msgAviso);
                        LinearLayout lnMsgTotal=(LinearLayout)dialogo.findViewById(R.id.lnMsgTotal);
                        lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogo.dismiss();
                                return;
                            }
                        });
                    }else if (valor.substring(0, 2).equals("03")) {
                        String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                        dialogo = fabrica_dialogos.dlgConfirmarDatos(getContext()
                                , "Confirmar datos", msgAviso
                                , R.drawable.side_credencial_nueva
                                , "");

                        ImageView btnSi = (ImageView) dialogo.findViewById(R.id.btnSi);
                        btnSi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogo.dismiss();
                                confirmarCredencialCambioDelegacion();
                            }
                        });
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error desconocido al confirmar la credencial nueva:\n" + ex.getMessage());
            return;
        }
    }

    private void confirmarCredencialCambioDelegacion() {
        try{
            String yaRegistroID=funciones.verificaSenderID(getActivity(),3);
            if (yaRegistroID.equals("SD") || yaRegistroID.equals("NO")){
                String valorDevuelto="";
                if (yaRegistroID.equals("SD"))
                    valorDevuelto = "No se puede solicitar el cambio de delegación de la nueva credencial porque todavía no se registra el dispositivo";
                else{
                    valorDevuelto="No se puede solicitar el cambio de delegación de la nueva credencial porque no se pudo registrar el dispositivo";
                }
                dialogo = fabrica_dialogos.dlgError(getContext(), valorDevuelto);
                return;
            }

            SQLController database = new SQLController(getContext());
            final String idAfiliadoTitular = database.obtenerIDAfiliado();
            if (idAfiliadoTitular.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", idAfiliadoTitular);
            params.put("idDelegacion", _idDelegacion);
            params.put("idAfiliadoCredencial", _idAfiliado);

            fabrica_WS.APPConfirmaCredencialNuevaCambiaSede(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                    dialogo = fabrica_dialogos.dlgAviso(getContext(), msgAviso);
                    LinearLayout lnMsgTotal=(LinearLayout)dialogo.findViewById(R.id.lnMsgTotal);
                    lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogo.dismiss();
                            trigger.fireChange("credencial_credencial_nueva_btnTermino");
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
        }catch (Exception ex) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error desconocido al confirmar el cambio de delegación de la credencial nueva:\n" + ex.getMessage());
            return;
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
