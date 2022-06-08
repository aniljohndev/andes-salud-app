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

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_cartilla_farmacia extends Fragment implements OnMapReadyCallback {

    FragmentChangeTrigger trigger;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    private Dialog dialogo, dialogoMapa, dialogoMapaDatos;
    View _actActual;
    LinearLayout loadingData, listaMacroZona, ultLnParaZonasZona,
        ultLnParaZonasFarmacia, lnParaZonasFarmacia;
    TextView lblEstadoLoading;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    String idCartillaPrestadores="";
    String[] idFarmacia, nombreFarmacia, idFarmaciaDomicilio, domicilio, lat, lon, idFarmaciaTel
            , telefono;
    Map<Marker, MarkerInfo> mMarkerMap = new HashMap<>();
    boolean mostrandoMapa=false;
    ImageView ultImgParaZonasZona, ultImgExpandirZonaFarmacia;

    public fragmento_cartilla_farmacia() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_cartilla_farmacia, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _actActual=view;
        lblEstadoLoading= (TextView) view.findViewById(R.id.lblEstadoLoading);
        lblEstadoLoading.setText("Buscando las cartillas");
        loadingData= (LinearLayout) view.findViewById(R.id.loadingData);
        ImageView imageView = view.findViewById(R.id.imgCargando);
        Glide.with(this).load(R.drawable.loading).into(imageView);
        listaMacroZona = (LinearLayout) _actActual.findViewById(R.id.listaCartillas);
        listaMacroZona.setVisibility(View.GONE);
        buscarCartillasMacroZona();
    }

    private void buscarCartillasMacroZona(){
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
            params.put("idAfiliado", idAfiliado);

            fabrica_WS.APPObtenerFarmaciasMacroZona(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    String[] idMacroZona = new String[valores.getElementsByTagName("idZona").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idZona").getLength(); i++) {
                        idMacroZona[i] = valores.getElementsByTagName("idZona").item(i).getTextContent();
                    }
                    String[] nombreMacroZona = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        nombreMacroZona[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    mostrarMacroZonas(idMacroZona, nombreMacroZona);
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

    private void mostrarMacroZonas(String[] idMacroZona, String[] nombreMacroZona) {
        try {
            for (int i = 0; i < idMacroZona.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnMacroZona = inflater.inflate(R.layout.lista_un_renglon_cartilla_farmacias, null);
                final String idFilaS, linea1S;
                idFilaS=idMacroZona[i];
                linea1S=nombreMacroZona[i];

                TextView idFila = (TextView) lnMacroZona.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnMacroZona.findViewById(R.id.linea1);
                LinearLayout _lnMacroZona = (LinearLayout) lnMacroZona.findViewById(R.id.lnCartilla);
                final LinearLayout _lnParaZonasZona = (LinearLayout) lnMacroZona.findViewById(R.id.lnFarmacias);
                final ImageView imgExpandirZona = (ImageView) lnMacroZona.findViewById(R.id.imgExpandirZona);
                idFila.setText(idFilaS);
                linea1.setText(linea1S);

                _lnMacroZona.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ultLnParaZonasZona!=null){
                            ultImgParaZonasZona.setRotation(90);
                            _lnParaZonasZona.removeAllViews();
                            ultLnParaZonasZona.removeAllViews();
                            if (ultLnParaZonasFarmacia!=null){
                                lnParaZonasFarmacia.removeAllViews();
                                ultLnParaZonasFarmacia.removeAllViews();
                            }
                            if (ultLnParaZonasZona == _lnParaZonasZona){
                                ultLnParaZonasZona=null;
                                return;
                            }
                        }
                        buscarFarmaciasZonas(idFilaS, _lnParaZonasZona, imgExpandirZona);
                    }
                });
                listaMacroZona.addView(lnMacroZona);
                listaMacroZona.setVisibility(View.VISIBLE);
                loadingData.setVisibility(View.GONE);
                lblEstadoLoading.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las cartillas:\n" + e.getMessage());
        }
    }

    private void buscarFarmaciasZonas(String idMacroZona, LinearLayout lnMacroZona, ImageView imgExpandirZona) {
        final String _idMacroZona=idMacroZona;
        final LinearLayout _lnMacroZona=lnMacroZona;
        final ImageView _imgExpandirZona=imgExpandirZona;
        try {
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las zonas de farmacia");
            SQLController database = new SQLController(getContext());
            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", database.obtenerIDAfiliado());
            params.put("idZona", _idMacroZona);

            fabrica_WS.APPObtenerFarmaciasZona(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    String[] idZona = new String[valores.getElementsByTagName("idZona").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idZona").getLength(); i++) {
                        idZona[i] = valores.getElementsByTagName("idZona").item(i).getTextContent();
                    }
                    String[] nombre = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        nombre[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    mostrarZonas(idZona, nombre, _lnMacroZona, _imgExpandirZona);
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

    private void mostrarZonas(String[] idZona, String[] nombreZona, LinearLayout lnParaZona, ImageView imgExpandirZona) {
        try {
            for (int i = 0; i < idZona.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnZona = inflater.inflate(R.layout.lista_un_renglon_cartilla_farmacias_zonas, null);
                final String idFilaS, linea1S;
                idFilaS=idZona[i];
                linea1S=nombreZona[i];

                TextView idFila = (TextView) lnZona.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnZona.findViewById(R.id.linea1);
                idFila.setText(idFilaS);
                linea1.setText(linea1S);
                final LinearLayout _lnParaZonasZona = (LinearLayout) lnZona.findViewById(R.id.lnFarmaciasDetalles);
                final ImageView imgExpandirZonaFarmacia = (ImageView) lnZona.findViewById(R.id.imgExpandirZonaFarmacia);

                LinearLayout lnZonaAct = (LinearLayout) lnZona.findViewById(R.id.lnCartillaZonas);
                lnZonaAct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ultLnParaZonasFarmacia!=null){
                            if (ultImgExpandirZonaFarmacia!=null) {
                                ultImgExpandirZonaFarmacia.setRotation(90);
                            }
                            lnParaZonasFarmacia.removeAllViews();
                            ultLnParaZonasFarmacia.removeAllViews();
                            if (lnParaZonasFarmacia == _lnParaZonasZona){
                                ultLnParaZonasFarmacia=null;
                                lnParaZonasFarmacia=null;
                                return;
                            }
                        }
                        buscarFarmaciasZona(idFilaS, _lnParaZonasZona, imgExpandirZonaFarmacia);
                    }
                });

                ImageView btnMapa = (ImageView) lnZona.findViewById(R.id.btnMapa);
                btnMapa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        idCartillaPrestadores="FARMACIAZONA"+idFilaS;
                        mostrarMapa();
                    }
                });
                lnParaZona.addView(lnZona);
                ultLnParaZonasZona=lnParaZona;
                ultImgParaZonasZona=imgExpandirZona;
                ultImgParaZonasZona.setRotation(270);
                dialogo.dismiss();
            }
        } catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar la zona de la cartilla:\n" + e.getMessage());
            return;
        }
    }

    private void buscarFarmaciasZona(String idZona, LinearLayout zona, ImageView imgExpandirZonaFarmacia) {
        final String _idZona=idZona;
        final LinearLayout _lnZona=zona;
        final ImageView _imgExpandirZonaFarmacia=imgExpandirZonaFarmacia;
        try {
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las farmacias");
            SQLController database = new SQLController(getContext());
            Map<String, String> params = new HashMap<String, String>();
            params.put("IMEI", funciones.obtenerIMEI(getContext()));
            params.put("idAfiliado", database.obtenerIDAfiliado());
            params.put("idZona", _idZona);

            fabrica_WS.APPBuscarFarmaciasCartilla(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    String[] idFarmacia = new String[valores.getElementsByTagName("idFarmacia").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idFarmacia").getLength(); i++) {
                        idFarmacia[i] = valores.getElementsByTagName("idFarmacia").item(i).getTextContent();
                    }
                    String[] nombre = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        nombre[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    String[] domicilio = new String[valores.getElementsByTagName("domicilio").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("domicilio").getLength(); i++) {
                        domicilio[i] = valores.getElementsByTagName("domicilio").item(i).getTextContent();
                    }
                    String[] horario = new String[valores.getElementsByTagName("horario").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("horario").getLength(); i++) {
                        horario[i] = valores.getElementsByTagName("horario").item(i).getTextContent();
                    }

                    String[] idFarmaciaTel = new String[valores.getElementsByTagName("idFarmaciaTel").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idFarmaciaTel").getLength(); i++) {
                        idFarmaciaTel[i] = valores.getElementsByTagName("idFarmaciaTel").item(i).getTextContent();
                    }
                    String[] telefono = new String[valores.getElementsByTagName("telefono").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("telefono").getLength(); i++) {
                        telefono[i] = valores.getElementsByTagName("telefono").item(i).getTextContent();
                    }
                    mostrarDetalleZona(_idZona, _lnZona, idFarmacia, nombre, domicilio, horario, idFarmaciaTel, telefono, _imgExpandirZonaFarmacia);
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

    private void mostrarDetalleZona(String _idZona, LinearLayout lnParaFarmacias, String[] idFarmacia
            ,String[] nombre, String[] domicilio, String[] horario, String[] idFarmaciaTel, String[] telefono
            ,ImageView imgExpandirZonaFarmacia){
        try {
            lnParaZonasFarmacia=lnParaFarmacias;
            for (int i = 0; i < idFarmacia.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnFarmacia = inflater.inflate(R.layout.fila_cartilla_farmacia_detalle, null);

                final String idFilaS, linea1S;
                final String[] _idFarmaciaTel, _telefono;
                idFilaS=idFarmacia[i];
                linea1S=nombre[i];

                TextView txtIdElemento = (TextView) lnFarmacia.findViewById(R.id.idElemento);
                TextView txtFarmacia = (TextView) lnFarmacia.findViewById(R.id.txtFarmacia);
                TextView txtDomicilio = (TextView) lnFarmacia.findViewById(R.id.txtDomicilio);
                TextView txtHorario = (TextView) lnFarmacia.findViewById(R.id.txtHorario);
                txtIdElemento.setText(idFilaS);
                txtFarmacia.setText(linea1S);
                txtDomicilio.setText(domicilio[i]);
                txtHorario.setText(horario[i]);

                final ImageView imgTelefono = (ImageView) lnFarmacia.findViewById(R.id.imgTelefono);
                final ImageView imgBtnMapa = (ImageView) lnFarmacia.findViewById(R.id.imgBtnMapa);
                _idFarmaciaTel=idFarmaciaTel;
                _telefono=telefono;

                //LinearLayout lnZonaAct = (LinearLayout) lnZona.findViewById(R.id.lnCartillaZonas);
                imgTelefono.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        discarTelefono(idFilaS, _idFarmaciaTel, _telefono, linea1S);
                    }
                });

                imgBtnMapa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        idCartillaPrestadores="FARMACIAINDIV"+idFilaS;
                        mostrarMapa();
                    }
                });

                lnParaZonasFarmacia.addView(lnFarmacia);
                ultLnParaZonasFarmacia=lnParaFarmacias;
                ultImgExpandirZonaFarmacia=imgExpandirZonaFarmacia;
                ultImgExpandirZonaFarmacia.setRotation(270);
            }
            dialogo.dismiss();
        } catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las farmacias de la zona de la cartilla:\n" + e.getMessage());
        }
    }

    private void discarTelefono(String idFilaS, String[] _idFarmaciaTel, String[] _telefono, String nombreFarmacia){
        ArrayList<HashMap<String, String>> listaTelefonos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> mapTel = new HashMap<String, String>();
        for (int contTel = 0; contTel < _telefono.length; contTel++) {
            if (_idFarmaciaTel[contTel].equals(idFilaS)) {
                mapTel = new HashMap<String, String>();
                mapTel.put("idFila", _idFarmaciaTel[contTel]);
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
                dialogo = fabrica_dialogos.dlgMuestraTelefonosMarcar(getContext(), nombreFarmacia, R.drawable.side_farmacia);
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "No se posee ningun teléfono de contacto para esta farmacia");
        }
    }

    private void mostrarMapa() {
        if (mostrandoMapa){
            return;
        }
        mostrandoMapa=true;
        dialogoMapa = new Dialog(getContext());
        dialogoMapa.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogoMapa.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoMapa.setContentView(R.layout.dlg_modal_mapa);
        dialogoMapa.getWindow().setDimAmount(0.1f);
        mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.dlgMap);

        ImageView btnCerrarMapa=(ImageView) dialogoMapa.findViewById(R.id.btnCerrarMapa);
        btnCerrarMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapFragment!=null) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
                }
                dialogoMapa.dismiss();
                mostrandoMapa=false;
            }
        });
        mapFragment.getMapAsync(this);
        dialogoMapa.setCancelable(false);
        dialogoMapa.setCanceledOnTouchOutside(false);
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

                    idFarmacia = new String[valores.getElementsByTagName("idFarmacia").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idFarmacia").getLength(); i++) {
                        idFarmacia[i] = valores.getElementsByTagName("idFarmacia").item(i).getTextContent();
                    }

                    nombreFarmacia = new String[valores.getElementsByTagName("nombreFarmacia").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombreFarmacia").getLength(); i++) {
                        nombreFarmacia[i] = valores.getElementsByTagName("nombreFarmacia").item(i).getTextContent();
                    }

                    idFarmaciaDomicilio = new String[valores.getElementsByTagName("idFarmaciaDomicilio").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idFarmaciaDomicilio").getLength(); i++) {
                        idFarmaciaDomicilio[i] = valores.getElementsByTagName("idFarmaciaDomicilio").item(i).getTextContent();
                    }

                    domicilio = new String[valores.getElementsByTagName("domicilio").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("domicilio").getLength(); i++) {
                        domicilio[i] = valores.getElementsByTagName("domicilio").item(i).getTextContent();
                    }

                    lat = new String[valores.getElementsByTagName("lat").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("lat").getLength(); i++) {
                        lat[i] = valores.getElementsByTagName("lat").item(i).getTextContent();
                    }

                    lon = new String[valores.getElementsByTagName("lon").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("lon").getLength(); i++) {
                        lon[i] = valores.getElementsByTagName("lon").item(i).getTextContent();
                    }

                    idFarmaciaTel = new String[valores.getElementsByTagName("idFarmaciaTel").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idFarmaciaTel").getLength(); i++) {
                        idFarmaciaTel[i] = valores.getElementsByTagName("idFarmaciaTel").item(i).getTextContent();
                    }

                    telefono = new String[valores.getElementsByTagName("telefono").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("telefono").getLength(); i++) {
                        telefono[i] = valores.getElementsByTagName("telefono").item(i).getTextContent();
                    }

                    mostrarDetalleCartillaZona(idFarmacia, nombreFarmacia, idFarmaciaDomicilio, domicilio, lat, lon, idFarmaciaTel
                            , telefono);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogoMapaDatos.dismiss();
                    if (mapFragment!=null) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
                    }
                    dialogoMapa.dismiss();
                    mostrandoMapa=false;
                    dialogoMapaDatos = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        } catch (Exception e) {
            dialogoMapaDatos.dismiss();
            if (mapFragment!=null) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
            }
            dialogoMapa.dismiss();
            mostrandoMapa=false;
            dialogoMapaDatos = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los datos del mapa desde las cartillas:\n" + e.getMessage());
        }
    }

    private void mostrarDetalleCartillaZona(final String[] idFarmacia, final String[] nombreFarmacia, String[] idFarmaciaDomicilio
            , String[] domicilio , String[] lat, String[] lon, String[] idFarmaciaTel, String[] telefono) {
        try {
            for (int i = 0; i < idFarmacia.length; i++) {
                for (int j = 0; j < idFarmaciaDomicilio.length; j++) {
                    if (idFarmacia[i].equals(idFarmaciaDomicilio[j])) {
                        String telefonoIW = "";
                        LatLng posActual = new LatLng(Double.parseDouble(lat[j]), Double.parseDouble(lon[j]));
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(posActual)
                                .title("Farmacia")
                                .snippet(idFarmacia[i] + idFarmaciaDomicilio[j])
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.andes_logo_24))
                                ;
                        final ArrayList<String> listaTel= new ArrayList<String>();
                        for (int z = 0; z < idFarmaciaTel.length; z++) {
                            if (idFarmaciaTel[z].equals(idFarmaciaDomicilio[j])) {
                                listaTel.add(telefono[z]);
                                /*if (telefonoIW.equals("")) {
                                    telefonoIW = telefono[z];
                                } else {
                                    telefonoIW = telefonoIW + "; " + telefono[z];
                                }*/
                            }
                        }

                        final InfoWindowData info = new InfoWindowData();
                        info.setNombre(nombreFarmacia[i]);
                        info.setDomicilio(domicilio[j]);
                        info.setTelefono(telefonoIW);

                        adaptador_infowindow_cartilla_prestadores customInfoWindow = new adaptador_infowindow_cartilla_prestadores(getContext());
                        mMap.setInfoWindowAdapter(customInfoWindow);

                        Marker m = mMap.addMarker(markerOptions);
                        m.setTag(info);

                        float zoomLevel = 10.0f; //This goes up to 21
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posActual, zoomLevel));

                        final String _nombreFarmaciaTel= nombreFarmacia[i];
                        MarkerInfo markerInfo = new MarkerInfo(_nombreFarmaciaTel, listaTel);
                        mMarkerMap.put(m, markerInfo);

                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                MarkerInfo markerInfo = mMarkerMap.get(marker);
                                if (markerInfo.telefonos.size()==0){
                                    dialogo = fabrica_dialogos.dlgError(getContext(), "No hay teléfonos para mostrar");
                                    return;
                                }

                                dialogo = fabrica_dialogos.dlgMuestraTelefonosMarcar(getContext(), markerInfo.nombreFarmacia, R.drawable.side_farmacia);
                                LinearLayout listaTelefonos = (LinearLayout) dialogo.findViewById(R.id.lnLista);
                                listaTelefonos.removeAllViews();
                                for (int i = 0; i < markerInfo.telefonos.size(); i++) {
                                    LayoutInflater inflater = getActivity().getLayoutInflater();
                                    View lnEmergencia = inflater.inflate(R.layout.fila_lista_telefonos_llamar, null);

                                    TextView txtTexto = (TextView) lnEmergencia.findViewById(R.id.txtTexto);
                                    LinearLayout lnTelefono = (LinearLayout) lnEmergencia.findViewById(R.id.lnTelefono);

                                    final String numTel=markerInfo.telefonos.get(i);
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
                                    //cursorTel.moveToNext();
                                }
                                /*final String idConvenioActual = marker.getSnippet(); // 40.714224

                                dialogo = new Dialog(getContext());
                                dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogo.setContentView(R.layout.dlg_modal_mapa_accion);
                                dialogo.show();*/
                                return;
                            }
                        });
                    }
                }
            }
            //lnMapa.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostar los datos de la zona del mapa en la cartilla de farmacia:\n" + e.getMessage());
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

    public class MarkerInfo {
        public String nombreFarmacia;
        public ArrayList<String> telefonos;

        public MarkerInfo(String _nombreFarmacia, ArrayList<String> _telefonos) {
            nombreFarmacia = _nombreFarmacia;
            telefonos = _telefonos;
        }
    }
}
