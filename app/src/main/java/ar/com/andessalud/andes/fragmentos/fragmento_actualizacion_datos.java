package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.EditText;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_actualizacion_datos extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo;
    private LinearLayout lnAfiliado, lnAfiliadoResultados, listaAfiliado, lnActDatos
        ,lnDatosContactoResultados, lnBotonesConfirmar, lnDatosContacto, lnProvincia
        ,lnLocalidad, listaProvincias, listaLocalidades;
    private TextView lblAfiliadoSeleccionado, lblSeleccionarActDatos, lblSeleccionActDatos;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    private String _idAfiliado="", _nombreAfiliado="", provincia="", _idLocalidad="";
    boolean actDatosHabilitado=false;

    public fragmento_actualizacion_datos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_actualizacion_datos, container, false);
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
        lnActDatos = (LinearLayout) view.findViewById(R.id.lnActDatos);
        lnActDatos.setVisibility(View.VISIBLE);
        lnDatosContactoResultados = (LinearLayout) view.findViewById(R.id.lnDatosContactoResultados);
        lnDatosContactoResultados.setVisibility(View.GONE);
        lblSeleccionarActDatos = (TextView) view.findViewById(R.id.lblSeleccionarActDatos);
        lblSeleccionActDatos = (TextView) view.findViewById(R.id.lblSeleccionActDatos);

        lnActDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickActDatos();
            }
        });
        //#endregion

        lnProvincia = (LinearLayout) view.findViewById(R.id.lnProvincia);
        lnProvincia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarProvincia();
            }
        });
        listaProvincias = (LinearLayout) view.findViewById(R.id.listaProvincias);
        listaProvincias.setVisibility(View.GONE);

        lnLocalidad = (LinearLayout) view.findViewById(R.id.lnLocalidad);
        lnLocalidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarLocalidad();
            }
        });

        lnBotonesConfirmar= (LinearLayout) view.findViewById(R.id.lnBotonesConfirmar);
        lnBotonesConfirmar.setVisibility(View.GONE);
        lnBotonesConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                almacenarContacto();
            }
        });

        lnDatosContacto= (LinearLayout) view.findViewById(R.id.lnDatosContacto);
        lnDatosContacto.setVisibility(View.GONE);

        listaLocalidades= (LinearLayout) view.findViewById(R.id.listaLocalidades);
        listaLocalidades.setVisibility(View.GONE);
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
        deshabilitaActDatos();
        lnBotonesConfirmar.setVisibility(View.GONE);
        buscarAfiliado();
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
        habilitaActDatos();
    }
    //#endregion

    private void deshabilitaActDatos(){
        actDatosHabilitado=false;
        lblSeleccionarActDatos.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblSeleccionActDatos.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnActDatos.setVisibility(View.VISIBLE);
        lnDatosContactoResultados.setVisibility(View.GONE);
        lnDatosContacto.setVisibility(View.GONE);
        lnBotonesConfirmar.setVisibility(View.GONE);
    }

    private void habilitaActDatos(){
        actDatosHabilitado=true;
        lblSeleccionarActDatos.setTextColor(getResources().getColor(R.color.colorFuente));
        lblSeleccionActDatos.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void clickActDatos(){
        if (!actDatosHabilitado){
            return;
        }
        lnActDatos.setVisibility(View.GONE);
        lnDatosContactoResultados.setVisibility(View.VISIBLE);
        buscarDatosContacto();
    }

    private void buscarDatosContacto() {
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los datos de contacto");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", _idAfiliado);

            fabrica_WS.APPObtenerDatosDeContacto(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    if (valores.getElementsByTagName("calle").getLength() > 0) {
                        EditText txtCalle = (EditText) getView().findViewById(R.id.txtCalle);
                        txtCalle.setText(valores.getElementsByTagName("calle").item(0).getTextContent());
                    }
                    if (valores.getElementsByTagName("numero").getLength() > 0) {
                        EditText txtNum = (EditText) getView().findViewById(R.id.txtNum);
                        txtNum.setText(valores.getElementsByTagName("numero").item(0).getTextContent());
                    }

                    if (valores.getElementsByTagName("piso").getLength() > 0) {
                        EditText txtPiso = (EditText) getView().findViewById(R.id.txtPiso);
                        txtPiso.setText(valores.getElementsByTagName("piso").item(0).getTextContent());
                    }

                    if (valores.getElementsByTagName("depto").getLength() > 0) {
                        EditText txtDepto = (EditText) getView().findViewById(R.id.txtDepto);
                        txtDepto.setText(valores.getElementsByTagName("depto").item(0).getTextContent());
                    }

                    if (valores.getElementsByTagName("provincia").getLength() > 0) {
                        TextView txtProvincia = (TextView) getView().findViewById(R.id.txtProvincia);
                        txtProvincia.setText(valores.getElementsByTagName("provincia").item(0).getTextContent());
                        provincia=valores.getElementsByTagName("provincia").item(0).getTextContent();
                    }

                    if (valores.getElementsByTagName("localidad").getLength() > 0) {
                        TextView txtLocalidad = (TextView) getView().findViewById(R.id.txtLocalidad);
                        txtLocalidad.setText(valores.getElementsByTagName("localidad").item(0).getTextContent());
                        _idLocalidad=valores.getElementsByTagName("idLocalidad").item(0).getTextContent();
                    }

                    if (valores.getElementsByTagName("caractTel").getLength() > 0) {
                        EditText txtCelularCaract = (EditText) getView().findViewById(R.id.txtCelularCaract);
                        txtCelularCaract.setText(valores.getElementsByTagName("caractTel").item(0).getTextContent());
                    }

                    if (valores.getElementsByTagName("numTel").getLength() > 0) {
                        EditText txtCelularNum = (EditText) getView().findViewById(R.id.txtCelularNum);
                        txtCelularNum.setText(valores.getElementsByTagName("numTel").item(0).getTextContent());
                    }

                    if (valores.getElementsByTagName("mail").getLength() > 0) {
                        EditText txtMail = (EditText) getView().findViewById(R.id.txtMail);
                        txtMail.setText(valores.getElementsByTagName("mail").item(0).getTextContent().replaceAll("\n","").replaceAll(" ",""));
                    }

                    if (valores.getElementsByTagName("caractTelFijo").getLength() > 0) {
                        EditText txtFijoCaract = (EditText) getView().findViewById(R.id.txtFijoCaract);
                        txtFijoCaract.setText(valores.getElementsByTagName("caractTelFijo").item(0).getTextContent());
                    }

                    if (valores.getElementsByTagName("numTelFijo").getLength() > 0) {
                        EditText txtFijoNum = (EditText) getView().findViewById(R.id.txtFijoNum);
                        txtFijoNum.setText(valores.getElementsByTagName("numTelFijo").item(0).getTextContent());
                    }

                    if (valores.getElementsByTagName("idLocalidad").getLength() > 0) {
                        _idLocalidad=valores.getElementsByTagName("idLocalidad").item(0).getTextContent();
                    }
                    lnDatosContacto.setVisibility(View.VISIBLE);
                    lnBotonesConfirmar.setVisibility(View.VISIBLE);
                    //mostrarDelegacionesExpendio(identificador, nombre, direccion, localidad);
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

    private void buscarProvincia() {
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las provincias");
            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", idAfiliado);

            fabrica_WS.APPObtenerProvincias(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] provincia = new String[valores.getElementsByTagName("provincia").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("provincia").getLength(); i++) {
                        provincia[i] = valores.getElementsByTagName("provincia").item(i).getTextContent();
                    }
                    mostrarProvincias(provincia);
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las provincias:\n"+ex.getMessage());
        }
    }

    private void mostrarProvincias(String[] provincias){
        try {
            listaProvincias.removeAllViews();
            for (int i = 0; i < provincias.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnProvincia = inflater.inflate(R.layout.lista_un_renglon_tilde, null);

                final String nombreProvincia=provincias[i];

                TextView linea1 = (TextView) lnProvincia.findViewById(R.id.linea1);
                linea1.setText(nombreProvincia);

                lnProvincia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView txtProvincia = (TextView) getView().findViewById(R.id.txtProvincia);
                        txtProvincia.setText(nombreProvincia);
                        TextView txtLocalidad = (TextView) getView().findViewById(R.id.txtLocalidad);
                        txtLocalidad.setText("---");
                        provincia=nombreProvincia;
                        listaProvincias.setVisibility(View.GONE);
                    }
                });
                listaProvincias.addView(lnProvincia);
            }
            listaProvincias.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las provincias:\n" + e.getMessage());
        }
    }

    private void buscarLocalidad(){
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las localidades");
            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", idAfiliado);
            params.put("provincia", provincia);

            fabrica_WS.APPObtenerLocalidades(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] localidad = new String[valores.getElementsByTagName("localidad").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("localidad").getLength(); i++) {
                        localidad[i] = valores.getElementsByTagName("localidad").item(i).getTextContent();
                    }
                    String[] idlocalidad = new String[valores.getElementsByTagName("idLocalidad").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idLocalidad").getLength(); i++) {
                        idlocalidad[i] = valores.getElementsByTagName("idLocalidad").item(i).getTextContent();
                    }
                    mostrarLocalidades(localidad, idlocalidad);
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las localidades:\n"+ex.getMessage());
        }
    }

    private void mostrarLocalidades(String[] localidades, String[] idLocalidad) {
        try {
            listaLocalidades.removeAllViews();
            for (int i = 0; i < localidades.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnLocalidades = inflater.inflate(R.layout.lista_un_renglon_tilde, null);

                final String nombreLocalidad=localidades[i];
                final String id_Localidad=idLocalidad[i];

                TextView idFila = (TextView) lnLocalidades.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnLocalidades.findViewById(R.id.linea1);
                idFila.setText(id_Localidad);
                linea1.setText(nombreLocalidad);

                lnLocalidades.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView txtLocalidad = (TextView) getView().findViewById(R.id.txtLocalidad);
                        txtLocalidad.setText(nombreLocalidad);
                        _idLocalidad=id_Localidad;
                        listaLocalidades.setVisibility(View.GONE);
                    }
                });
                listaLocalidades.addView(lnLocalidades);
            }
            listaLocalidades.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las localidades:\n" + e.getMessage());
        }
    }

    private void almacenarContacto(){
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Intentando almacenar los datos de contacto");

            EditText txtMail = (EditText) getView().findViewById(R.id.txtMail);
            EditText txtCelularCaract = (EditText) getView().findViewById(R.id.txtCelularCaract);
            EditText txtCelularNum = (EditText) getView().findViewById(R.id.txtCelularNum);
            EditText txtCalle = (EditText) getView().findViewById(R.id.txtCalle);
            EditText txtNum = (EditText) getView().findViewById(R.id.txtNum);
            EditText txtPiso = (EditText) getView().findViewById(R.id.txtPiso);
            EditText txtDepto = (EditText) getView().findViewById(R.id.txtDepto);
            EditText txtFijoCaract = (EditText) getView().findViewById(R.id.txtFijoCaract);
            EditText txtFijoNum = (EditText) getView().findViewById(R.id.txtFijoNum);

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", _idAfiliado);
            params.put("mail", txtMail.getText().toString());
            params.put("celular", txtCelularCaract.getText().toString()+"-"+txtCelularNum.getText().toString());
            params.put("calle", txtCalle.getText().toString());
            params.put("num", txtNum.getText().toString());
            params.put("piso", txtPiso.getText().toString());
            params.put("depto", txtDepto.getText().toString());
            params.put("idLocalidad", _idLocalidad);
            params.put("fijoCar", txtFijoCaract.getText().toString());
            params.put("fijoNum", txtFijoNum.getText().toString());

            fabrica_WS.APPActualizaDatosContactoFijo(getActivity(), params, new SuccessResponseHandler<Document>() {
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
                            trigger.fireChange("actDatosContacto_btnTermino");
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
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al almacenar los datos de contacto:\n"+ex.getMessage());
        }
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
