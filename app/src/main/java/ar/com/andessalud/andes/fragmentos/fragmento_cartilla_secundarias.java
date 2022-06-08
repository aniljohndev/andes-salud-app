package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.adaptadores.adaptador_infowindow_cartilla_prestadores;
import ar.com.andessalud.andes.clases.InfoWindowData;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.funciones;
import ar.com.andessalud.andes.principal;

public class fragmento_cartilla_secundarias extends Fragment implements OnMapReadyCallback {

    FragmentChangeTrigger trigger;
    private Dialog dialogo, dialogoMapa, dialogoMapaDatos;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    View _actActual;
    LinearLayout loadingData, listaCartillas, ultLnParaZonasZona;
    TextView lblEstadoLoading;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    String idCartillaPrestadores="";
    String[] ordenAccion, idConvenio, nombre, idConvenioDom, idDomicilioDom, domicilio, localidad, provincia, idDomicilioTel
            ,telefono, lat, longitud;
    principal pantPrincipal;
    ImageView ultImgParaZonasZona;

    public fragmento_cartilla_secundarias() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_cartilla_secundarias, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _actActual=view;
        pantPrincipal = (principal) getActivity();
        lblEstadoLoading= (TextView) view.findViewById(R.id.lblEstadoLoading);
        lblEstadoLoading.setText("Buscando las cartillas");
        loadingData= (LinearLayout) view.findViewById(R.id.loadingData);
        ImageView imageView = view.findViewById(R.id.imgCargando);
        Glide.with(this).load(R.drawable.loading).into(imageView);
        listaCartillas = (LinearLayout) _actActual.findViewById(R.id.listaCartillas);
        listaCartillas.setVisibility(View.GONE);
        buscarCartillas("*TODAS*");
    }

    private void buscarCartillas(String cadena) {
        try {
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                loadingData.setVisibility(View.GONE);
                lblEstadoLoading.setVisibility(View.GONE);
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("IMEI", funciones.obtenerIMEI(getContext()));
            params.put("idAfiliado", idAfiliado);
            params.put("otrasCartillas", "1");
            params.put("cadena", cadena);

            fabrica_WS.APPObtenerCartillasBusqueda(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                    String[] idCartilla = new String[valores.getElementsByTagName("idCartilla").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idCartilla").getLength(); i++) {
                        idCartilla[i] = valores.getElementsByTagName("idCartilla").item(i).getTextContent();
                    }
                    String[] nombre = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        nombre[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    mostrarCartillas(idCartilla, nombre);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    loadingData.setVisibility(View.GONE);
                    lblEstadoLoading.setVisibility(View.GONE);
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        } catch (Exception ex) {
            loadingData.setVisibility(View.GONE);
            lblEstadoLoading.setVisibility(View.GONE);
            dialogo = fabrica_dialogos.dlgError(getContext(), ex.getMessage());
        }
    }

    private void mostrarCartillas(final String[] idCartilla, String[] nombre) {
        try {
            for (int i = 0; i < idCartilla.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                final View lnPrestador = inflater.inflate(R.layout.fila_cartilla_medica, null);

                final String _idCartilla=idCartilla[i];
                final String _nombreCartilla=nombre[i];

                TextView nombreCartilla = (TextView) lnPrestador.findViewById(R.id.nombreCartilla);
                final ImageView imgExpandir= (ImageView) lnPrestador.findViewById(R.id.imgExpandir);
                ImageView imgMapa = (ImageView) lnPrestador.findViewById(R.id.imgMapa);
                final LinearLayout lnDetalleCartilla= (LinearLayout) lnPrestador.findViewById(R.id.lnDetalleCartilla);

                nombreCartilla.setText(_nombreCartilla);

                nombreCartilla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buscarDetalleCartilla(lnDetalleCartilla, _idCartilla, imgExpandir);
                    }
                });

                imgExpandir.setTag("cerrado");
                imgExpandir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buscarDetalleCartilla(lnDetalleCartilla, _idCartilla, imgExpandir);
                    }
                });

                imgMapa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        idCartillaPrestadores="CARTPREST"+_idCartilla;
                        mostrarMapa();
                    }
                });
                listaCartillas.addView(lnPrestador);
            }
            listaCartillas.setVisibility(View.VISIBLE);
            loadingData.setVisibility(View.GONE);
            lblEstadoLoading.setVisibility(View.GONE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las cartillas:\n" + e.getMessage());
        }
    }

    private void buscarDetalleCartilla(LinearLayout lnDetalleCartilla, String _idCartilla, ImageView imgExpandir){
        if (ultLnParaZonasZona!=null){
            ultImgParaZonasZona.setRotation(90);
            lnDetalleCartilla.removeAllViews();
            ultLnParaZonasZona.removeAllViews();
        }
        if (imgExpandir.getTag().equals("cerrado")) {
            ultImgParaZonasZona=imgExpandir;
            imgExpandir.setRotation(270);
            imgExpandir.setTag("abierto");
            lnDetalleCartilla.setVisibility(View.VISIBLE);
            mostrarCartilla(lnDetalleCartilla, _idCartilla);
        }else{
            imgExpandir.setRotation(90);
            lnDetalleCartilla.setVisibility(View.GONE);
            imgExpandir.setTag("cerrado");
        }
    }

    private void mostrarCartilla(LinearLayout lnPrestador, String idCartilla){
        final String _idCartilla=idCartilla;
        final LinearLayout _lnPrestador=lnPrestador;

        if (idCartilla.equals("00000000-0000-0000-0000-000000000000")) {
//            Intent inent = new Intent(getBaseContext(), cartillas_secundarias.class);
            //          startActivity(inent);
        }else {
            try {
                dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los prestadores de la cartilla");
                SQLController database = new SQLController(getContext());
                final String idAfiliado = database.obtenerIDAfiliado();

                Map<String, String> params = new HashMap<String, String>();
                params.put("IMEI", funciones.obtenerIMEI(getContext()));
                params.put("idAfiliado", idAfiliado);
                params.put("idCartilla", _idCartilla);
                params.put("cadena", "");

                fabrica_WS.APPBuscarPrestadoresCartillaBusquedas(getContext(), params, new SuccessResponseHandler<Document>() {
                    @Override
                    public void onSuccess(Document valores) {
                        String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                        if (valor.substring(0, 2).equals("00")) {
                            String[] ordenAccion = new String[valores.getElementsByTagName("ordenAccion").getLength()];
                            for (int i = 0; i < valores.getElementsByTagName("ordenAccion").getLength(); i++) {
                                ordenAccion[i] = valores.getElementsByTagName("ordenAccion").item(i).getTextContent();
                            }
                            String[] idConvenio = new String[valores.getElementsByTagName("idConvenio").getLength()];
                            for (int i = 0; i < valores.getElementsByTagName("idConvenio").getLength(); i++) {
                                idConvenio[i] = valores.getElementsByTagName("idConvenio").item(i).getTextContent();
                            }
                            String[] nombre = new String[valores.getElementsByTagName("nombre").getLength()];
                            for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                                nombre[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                            }
                            String[] idConvenioDom = new String[valores.getElementsByTagName("idConvenioDom").getLength()];
                            for (int i = 0; i < valores.getElementsByTagName("idConvenioDom").getLength(); i++) {
                                idConvenioDom[i] = valores.getElementsByTagName("idConvenioDom").item(i).getTextContent();
                            }
                            String[] idDomicilioDom = new String[valores.getElementsByTagName("idDomicilioDom").getLength()];
                            for (int i = 0; i < valores.getElementsByTagName("idDomicilioDom").getLength(); i++) {
                                idDomicilioDom[i] = valores.getElementsByTagName("idDomicilioDom").item(i).getTextContent();
                            }
                            String[] domicilio = new String[valores.getElementsByTagName("domicilio").getLength()];
                            for (int i = 0; i < valores.getElementsByTagName("domicilio").getLength(); i++) {
                                domicilio[i] = valores.getElementsByTagName("domicilio").item(i).getTextContent();
                            }
                            String[] localidad = new String[valores.getElementsByTagName("localidad").getLength()];
                            for (int i = 0; i < valores.getElementsByTagName("localidad").getLength(); i++) {
                                localidad[i] = valores.getElementsByTagName("localidad").item(i).getTextContent();
                            }
                            String[] provincia = new String[valores.getElementsByTagName("provincia").getLength()];
                            for (int i = 0; i < valores.getElementsByTagName("provincia").getLength(); i++) {
                                provincia[i] = valores.getElementsByTagName("provincia").item(i).getTextContent();
                            }
                            String[] idDomicilioTel = new String[valores.getElementsByTagName("idDomicilioTel").getLength()];
                            for (int i = 0; i < valores.getElementsByTagName("idDomicilioTel").getLength(); i++) {
                                idDomicilioTel[i] = valores.getElementsByTagName("idDomicilioTel").item(i).getTextContent();
                            }
                            String[] telefono = new String[valores.getElementsByTagName("telefono").getLength()];
                            for (int i = 0; i < valores.getElementsByTagName("telefono").getLength(); i++) {
                                telefono[i] = valores.getElementsByTagName("telefono").item(i).getTextContent();
                            }
                            mostrarDetalleCartilla(_lnPrestador, _idCartilla, ordenAccion, idConvenio, nombre, idConvenioDom, idDomicilioDom, domicilio, localidad
                                    , provincia, idDomicilioTel, telefono);
                        } else if (valor.substring(0, 2).equals("10")) {
                            String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                            dialogo = fabrica_dialogos.dlgAviso(getContext(), msgAviso);
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
            } catch (Exception ex) {
                dialogo.dismiss();
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al busar el detalle de la cartilla:\n" + ex.getMessage());
            }
        }
    }

    private void mostrarDetalleCartilla( LinearLayout _lnCartilla, String _idCartilla, String[] ordenAccion
            ,String[] idConvenio, String[] nombre, String[] idConvenioDom, String[] idDomicilioDom
            ,String[] domicilio, String[] localidad, String[] provincia,String[] idDomicilioTel, String[] telefono){

        final String[] _idDomicilioTel=idDomicilioTel;
        final String[] _idDomicilioDom=idDomicilioDom;
        final String[] _telefono=telefono;
        final String _codAccion;
        final String[] _idConvenioDom=idConvenioDom, _domicilio=domicilio, _localidad=localidad, _provincia=provincia;

        for (int i = 0; i < idConvenio.length; i++) {
            View filaPrestador =LayoutInflater.from(getContext()).inflate(R.layout.fila_cartilla_medica_detalle, null);
            //View filaPrestador = getLayoutInflater().inflate(R.layout.fila_cartilla_detalle, null);
            TextView txtIdElemento = (TextView) filaPrestador.findViewById(R.id.idElemento);
            TextView txtCodAccion = (TextView) filaPrestador.findViewById(R.id.codAccion);
            TextView txtPrestador = (TextView) filaPrestador.findViewById(R.id.txtPrestador);
            ImageView imgAccion=(ImageView) filaPrestador.findViewById(R.id.imgAccion);
            ImageView imgFavorito=(ImageView) filaPrestador.findViewById(R.id.imgFavorito);
            imgAccion.setVisibility(View.GONE);
            txtIdElemento.setText(idConvenio[i]);
            txtCodAccion.setText(ordenAccion[i]);
            txtPrestador.setText(nombre[i]);
            if (ordenAccion[i].equals("00")){
                imgAccion.setVisibility(View.GONE);
                imgFavorito.setVisibility(View.GONE);
            }else if (ordenAccion[i].equals("05")){
                imgAccion.setImageResource(R.drawable.turnos_sin_blanco);
                imgAccion.setVisibility(View.VISIBLE);
            }else if (ordenAccion[i].equals("10")){
                imgAccion.setImageResource(R.drawable.orden_sin_fondo_blanco);
                imgAccion.setVisibility(View.VISIBLE);
            }else if (ordenAccion[i].equals("20")){
                imgAccion.setImageResource(R.drawable.orden_sin_fondo);
                imgAccion.setVisibility(View.VISIBLE);
            }else if (ordenAccion[i].equals("30")){
                imgAccion.setImageResource(R.drawable.sedes_sin_fondo);
                imgAccion.setVisibility(View.VISIBLE);
            }

            imgFavorito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tipoAccion="", idElemento, prestador, idDom, linea1Dom, linea2Dom, idTelefonoDom, telefono, _codAccion;
                    TextView txtIdElemento = (TextView) ((LinearLayout)view.getParent().getParent()).findViewById(R.id.idElemento);
                    TextView txtCodAccion = (TextView) ((LinearLayout)view.getParent().getParent()).findViewById(R.id.codAccion);
                    TextView txtPrestador = (TextView) ((LinearLayout)view.getParent().getParent()).findViewById(R.id.txtPrestador);
                    _codAccion= txtCodAccion.getText().toString();
                    if (_codAccion.equals("05")){
                        tipoAccion="TURNOS";
                    }else if (_codAccion.equals("10")){
                        tipoAccion="ORDENES";
                    }else if (_codAccion.equals("20")){
                        tipoAccion="PRESTADOR";
                    }else if (_codAccion.equals("30")){
                        tipoAccion="DELEGACION";
                    }
                    idElemento=txtIdElemento.getText().toString();
                    prestador= txtPrestador.getText().toString();

                    /*SQLController database = new SQLController(getContext());
                    String retorno=database.agregarFavorito(idElemento, "Cartilla", tipoAccion, prestador);
                    if (retorno.equals("0")) {
                        for (int contDom = 0; contDom < _idConvenioDom.length; contDom++) {
                            if (_idConvenioDom[contDom].equals(idElemento)) {
                                idDom = _idDomicilioDom[contDom];
                                linea1Dom = _domicilio[contDom];
                                linea2Dom = _localidad[contDom] + " - " + _provincia[contDom];
                                database.agregarFavoritoDomicilio(idElemento, idDom, linea1Dom, linea2Dom);

                                for (int contTel = 0; contTel < _telefono.length; contTel++) {
                                    if (_idDomicilioTel[contTel].equals(idDom)) {
                                        idTelefonoDom = _idDomicilioTel[contTel];
                                        telefono = _telefono[contTel];
                                        database.agregarFavoritoDomicilioTelefono(idElemento, idDom, telefono);
                                    }
                                }
                            }
                        }
                        dialog = new Dialog(mContext);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setContentView(R.layout.dlg_modal_mensaje);
                        TextView lblAfiliado = (TextView) dialog.findViewById(R.id.lblMsg);
                        lblAfiliado.setText("El prestador se agregó correctamente.");
                        dialog.show();

                        LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
                        lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }else {
                        dialog = new Dialog(mContext);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setContentView(R.layout.dlg_modal_mensaje);
                        TextView lblAfiliado = (TextView) dialog.findViewById(R.id.lblMsg);
                        lblAfiliado.setText("El prestador ya esta entre sus favoritos.");
                        dialog.show();

                        LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
                        lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }*/
                    return;
                }
            });

            final String idConvenioLoc=idConvenio[i];
            final String codAccionLoc=ordenAccion[i];
            final String nombrePrestadorLoc=nombre[i];

            imgAccion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView txtIdElemento = (TextView) v.findViewById(R.id.idElemento);
                    TextView txtCodAccion = (TextView) v.findViewById(R.id.codAccion);
                    TextView txtPrestador = (TextView) v.findViewById(R.id.txtPrestador);
                    String _idConvenio= idConvenioLoc;// txtIdElemento.getText().toString();
                    String _codAccion= codAccionLoc;//txtCodAccion.getText().toString();
                    if (_codAccion.equals("00")){
                        return;
                    }
                    else if (_codAccion.equals("05")){
                        pantPrincipal.idConvenioTurnos=idConvenioLoc;
                        pantPrincipal.nombreConvenioTurnos=nombrePrestadorLoc;
                        trigger.fireChange("cartilla_medica_btnTurnoConPrestador");
                    }else if (_codAccion.equals("10")){
                        pantPrincipal.idConvenioTurnos=idConvenioLoc;
                        pantPrincipal.nombreConvenioTurnos=nombrePrestadorLoc;
                        trigger.fireChange("cartilla_medica_btnOrdenConPrestador");
                    }else if (_codAccion.equals("20")){
                        /*Intent inent = new Intent(mContext, ordenes_practicas.class);
                        inent.putExtra("_idConvenio", _idConvenio);
                        inent.putExtra("_convenio", nombrePrestadorLoc);//txtPrestador.getText().toString());
                        mContext.startActivity(inent);*/
                    }else if (_codAccion.equals("30")){
                        /*Intent inent = new Intent(mContext, ordenes_practicas.class);
                        inent.putExtra("_idConvenio", _idConvenio);
                        inent.putExtra("_convenio", nombrePrestadorLoc);//txtPrestador.getText().toString());
                        mContext.startActivity(inent);
                        */
                    }
                }
            });

            for (int contDom = 0; contDom < idConvenioDom.length; contDom++) {
                if (idConvenioDom[contDom].equals(idConvenio[i])) {
                    LinearLayout listaDomicilio = (LinearLayout)filaPrestador.findViewById(R.id.lnDomicilios);
                    View filaDetalle =LayoutInflater.from(getContext()).inflate(R.layout.fila_cartilla_medica_domicilio, null);
                    //View filaDetalle = getLayoutInflater().inflate(R.layout.fila_cartilla_detalle_domicilio, null);
                    TextView idFila = (TextView) filaDetalle.findViewById(R.id.idFila);
                    TextView linea1 = (TextView) filaDetalle.findViewById(R.id.linea1);
                    TextView linea2 = (TextView) filaDetalle.findViewById(R.id.linea2);
                    ImageView btnTelefono = (ImageView) filaDetalle.findViewById(R.id.btnTelefono);
                    ImageView btnMapa = (ImageView) filaDetalle.findViewById(R.id.btnMapa);
                    View espacio = filaDetalle.findViewById(R.id.espacio);

                    final String locActual= localidad[contDom];
                    idFila.setText( idDomicilioDom[contDom]);
                    linea1.setText( domicilio[contDom]);
                    linea2.setText( localidad[contDom]+" - "+provincia[contDom]);
                    if (contDom==(idConvenioDom.length-1)){
                        espacio.setVisibility(View.GONE);
                    }else{
                        espacio.setVisibility(View.VISIBLE);
                    }
                    listaDomicilio.addView(filaDetalle);
                    final String idFilaLoc=idDomicilioDom[contDom];
                    final String idConvenioDomLoc=idConvenio[i];
                    btnTelefono.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<HashMap<String, String>> listaTelefonos = new ArrayList<HashMap<String, String>>();
                            HashMap<String, String> mapTel = new HashMap<String, String>();
                            //TextView _idFila = (TextView) v.findViewById(R.id.idFila);
                            String _idDomicilioDom= idFilaLoc;//_idFila.getText().toString();
                            for (int contTel = 0; contTel < _telefono.length; contTel++) {
                                if (_idDomicilioTel[contTel].equals(_idDomicilioDom)) {
                                    mapTel = new HashMap<String, String>();
                                    mapTel.put("idFila", _idDomicilioTel[contTel]);
                                    mapTel.put("linea1", _telefono[contTel]);
                                    listaTelefonos.add(mapTel);
                                }
                            }
                            if (listaTelefonos.size()>0){
                                if (listaTelefonos.size()==1){
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", listaTelefonos.get(0).get("linea1"), null));
                                    if (funciones.soportaLlamados(getContext(),intent)){
                                        getContext().startActivity(intent);
                                    }else {
                                        dialogo = fabrica_dialogos.dlgError(getContext(), "Este dispositvo no soporta llamados telefónicos");
                                    }
                                } else{
                                    dialogo = fabrica_dialogos.dlgMuestraTelefonosMarcar(getContext(), nombrePrestadorLoc, R.drawable.side_catilla_medica);
                                    LinearLayout lnListaTelefonos = (LinearLayout) dialogo.findViewById(R.id.lnLista);
                                    lnListaTelefonos.removeAllViews();

                                    for (int i = 0; i < listaTelefonos.size(); i++) {
                                        LayoutInflater inflater = getActivity().getLayoutInflater();
                                        View lnEmergencia = inflater.inflate(R.layout.fila_lista_telefonos_llamar, null);

                                        TextView txtTexto = (TextView) lnEmergencia.findViewById(R.id.txtTexto);
                                        LinearLayout lnTelefono = (LinearLayout) lnEmergencia.findViewById(R.id.lnTelefono);

                                        final String numTel=listaTelefonos.get(i).get("linea1");
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
                                        lnListaTelefonos.addView(lnEmergencia);
                                    }
                                }
                            }else{
                                dialogo = fabrica_dialogos.dlgError(getContext(), "No se posee ningun teléfono de contacto para este prestador");
                            }
                        }
                    });

                    btnMapa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (locActual.equals("S/D")){
                                dialogo = fabrica_dialogos.dlgError(getContext(), "No se posee información sobre el domicilio del prestador");
                                return;
                            }
                            idCartillaPrestadores="DOMPREST"+idFilaLoc+idConvenioDomLoc;
                            mostrarMapa();
                        }
                    });
                }
            }
            _lnCartilla.addView(filaPrestador);
            ultLnParaZonasZona=_lnCartilla;
        }
        dialogo.dismiss();
    }

    private void mostrarMapa() {
        //if (dialogoMapa==null) {
        dialogoMapa = new Dialog(getContext());
        dialogoMapa.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogoMapa.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoMapa.setContentView(R.layout.dlg_modal_mapa);
        dialogoMapa.getWindow().setDimAmount(0.1f);
        mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.dlgMap);
        //final MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.dlgMap);

        ImageView btnCerrarMapa=(ImageView) dialogoMapa.findViewById(R.id.btnCerrarMapa);
        btnCerrarMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapFragment!=null) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
                }
                dialogoMapa.dismiss();
            }
        });
            /*FragmentManager fm = getFragmentManager();
            SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.dlgMap);
            if (mapFragment == null) {
                mapFragment = new SupportMapFragment();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.mapFragmentContainer, mapFragment, "mapFragment");
                ft.commit();
                fm.executePendingTransactions();
            }*/
        mapFragment.getMapAsync(this);
        dialogoMapa.setCancelable(false);
        dialogoMapa.setCanceledOnTouchOutside(false);
        //}
        dialogoMapa.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //dialogo.dismiss();
        mMap = googleMap;
        //View v = getFragmentManager().findFragmentById(R.id.dlgMap).getView();
        //v.setAlpha(100f); // Change this value to set the desired alpha
        buscarDetalleCartilla();
    }

    private void buscarDetalleCartilla() {
        try {
            dialogoMapaDatos = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los datos del mapa");

            SQLController database = new SQLController(getContext());
            String idAfiliadoAct = database.obtenerIDAfiliado();
            if (idAfiliadoAct.equals("")) {
                //Asumimos que es abierta
                idAfiliadoAct="00000000-0000-0000-0000-000000000000";
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("IMEI", funciones.obtenerIMEI(getContext()));
            params.put("idAfiliado", idAfiliadoAct);
            params.put("idCartilla", idCartillaPrestadores);
            params.put("cadena", "");

            fabrica_WS.APPBuscarPrestadoresCartillaBusquedas(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogoMapaDatos.dismiss();
                    ordenAccion = new String[valores.getElementsByTagName("ordenAccion").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("ordenAccion").getLength(); i++) {
                        ordenAccion[i] = valores.getElementsByTagName("ordenAccion").item(i).getTextContent();
                    }
                    idConvenio = new String[valores.getElementsByTagName("idConvenio").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idConvenio").getLength(); i++) {
                        idConvenio[i] = valores.getElementsByTagName("idConvenio").item(i).getTextContent();
                    }

                    nombre = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        nombre[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }

                    idConvenioDom = new String[valores.getElementsByTagName("idConvenioDom").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idConvenioDom").getLength(); i++) {
                        idConvenioDom[i] = valores.getElementsByTagName("idConvenioDom").item(i).getTextContent();
                    }

                    idDomicilioDom = new String[valores.getElementsByTagName("idDomicilioDom").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idDomicilioDom").getLength(); i++) {
                        idDomicilioDom[i] = valores.getElementsByTagName("idDomicilioDom").item(i).getTextContent();
                    }

                    domicilio = new String[valores.getElementsByTagName("domicilio").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("domicilio").getLength(); i++) {
                        domicilio[i] = valores.getElementsByTagName("domicilio").item(i).getTextContent();
                    }

                    localidad = new String[valores.getElementsByTagName("localidad").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("localidad").getLength(); i++) {
                        localidad[i] = valores.getElementsByTagName("localidad").item(i).getTextContent();
                    }

                    provincia = new String[valores.getElementsByTagName("provincia").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("provincia").getLength(); i++) {
                        provincia[i] = valores.getElementsByTagName("provincia").item(i).getTextContent();
                    }

                    idDomicilioTel = new String[valores.getElementsByTagName("idDomicilioTel").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idDomicilioTel").getLength(); i++) {
                        idDomicilioTel[i] = valores.getElementsByTagName("idDomicilioTel").item(i).getTextContent();
                    }

                    telefono = new String[valores.getElementsByTagName("telefono").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("telefono").getLength(); i++) {
                        telefono[i] = valores.getElementsByTagName("telefono").item(i).getTextContent();
                    }

                    lat = new String[valores.getElementsByTagName("lat").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("lat").getLength(); i++) {
                        lat[i] = valores.getElementsByTagName("lat").item(i).getTextContent();
                    }

                    longitud = new String[valores.getElementsByTagName("long").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("long").getLength(); i++) {
                        longitud[i] = valores.getElementsByTagName("long").item(i).getTextContent();
                    }

                    mostrarDetalleCartilla(ordenAccion, idConvenio, nombre, idConvenioDom, idDomicilioDom, domicilio, localidad
                            , provincia, idDomicilioTel, telefono, lat, longitud);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogoMapaDatos.dismiss();
                    dialogoMapaDatos = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        } catch (Exception e) {
            dialogoMapaDatos.dismiss();
            dialogoMapaDatos = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los datos del mapa desde las cartillas:\n" + e.getMessage());
        }
    }

    private void mostrarDetalleCartilla(final String[] ordenAccion, final String[] idConvenio, String[] nombre, String[] idConvenioDom
            , String[] idDomicilioDom, String[] domicilio, String[] localidad, String[] provincia, String[] idDomicilioTel
            , String[] telefono, String[] lat, String[] longitud) {
        try {
            for (int i = 0; i < idConvenio.length; i++) {
                for (int j = 0; j < idDomicilioDom.length; j++) {
                    if (idConvenio[i].equals(idConvenioDom[j])) {
                        if (!lat[j].equals("0")) {
                            String telefonoIW = "";
                            LatLng posActual = new LatLng(Double.parseDouble(lat[j]), Double.parseDouble(longitud[j]));
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(posActual)
                                    .title("Prestador")
                                    .snippet(idConvenio[i] + idDomicilioDom[j])
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.andes_logo_24));

                            for (int z = 0; z < idDomicilioTel.length; z++) {
                                if (idDomicilioTel[z].equals(idDomicilioDom[j])) {
                                    if (telefonoIW.equals("")) {
                                        telefonoIW = telefono[z];
                                    } else {
                                        telefonoIW = telefonoIW + "; " + telefono[z];
                                    }
                                }
                            }
                            InfoWindowData info = new InfoWindowData();
                            info.setNombre(nombre[i]);
                            info.setDomicilio(domicilio[j] + " [" + localidad[j] + " - " + provincia[j] + "]");
                            info.setTelefono(telefonoIW);

                            adaptador_infowindow_cartilla_prestadores customInfoWindow = new adaptador_infowindow_cartilla_prestadores(getContext());
                            mMap.setInfoWindowAdapter(customInfoWindow);

                            Marker m = mMap.addMarker(markerOptions);
                            m.setTag(info);

                            float zoomLevel = 10.0f; //This goes up to 21
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posActual, zoomLevel));

                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    final String idConvenioActual = marker.getSnippet(); // 40.714224
                                    final String idConvenioLoc = idConvenioActual.substring(0, 36);
                                    final String idConvenioDomLoc = idConvenioActual.substring(36, 72);

                                    dialogo = new Dialog(getContext());
                                    dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                    dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialogo.setContentView(R.layout.dlg_modal_mapa_accion);
                                    dialogo.show();
                                    return;
                                }
                            });
                        }
                    }
                }
            }
            //lnMapa.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los datos del mapa en la cartilla:\n" + e.getMessage());
            return;
        }
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