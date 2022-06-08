package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
public class fragmento_sedes extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    FragmentChangeTrigger trigger;
    private Dialog dialogo, dialogoMapa, dialogoMapaDatos;
    LinearLayout listaSedes;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    String idCartillaPrestadores;
    String[] ordenAccion, idConvenio, nombre, idConvenioDom, idDomicilioDom, domicilio, localidad, provincia, idDomicilioTel
            ,telefono, lat, longitud;

    public fragmento_sedes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_sedes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listaSedes = (LinearLayout)view.findViewById(R.id.listaSedes);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        buscarSedes();
                    }
                },
                1000);
    }

    private void buscarSedes() {
        try {
            SQLController database = new SQLController(getContext());
            String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las sedes");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", database.obtenerIDAfiliado());

            fabrica_WS.APPObtenerDelegaciones(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idDelegacionDom = new String[valores.getElementsByTagName("idDelegacionDom").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idDelegacionDom").getLength(); i++) {
                        idDelegacionDom[i] = valores.getElementsByTagName("idDelegacionDom").item(i).getTextContent();
                    }
                    String[] nombre = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        nombre[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    String[] domicilio = new String[valores.getElementsByTagName("domicilio").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("domicilio").getLength(); i++) {
                        domicilio[i] = valores.getElementsByTagName("domicilio").item(i).getTextContent();
                    }
                    String[] localidad = new String[valores.getElementsByTagName("localidad").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("localidad").getLength(); i++) {
                        localidad[i] = valores.getElementsByTagName("localidad").item(i).getTextContent();
                    }

                    String[] telefono = new String[valores.getElementsByTagName("telefono").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("telefono").getLength(); i++) {
                        telefono[i] = valores.getElementsByTagName("telefono").item(i).getTextContent();
                    }
                    mostrarDelegaciones(idDelegacionDom, nombre, domicilio, localidad, telefono);
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las sedes\n"+ex.getMessage());
        }
    }

    private void mostrarDelegaciones(String[] idDelegacionDom, String[] nombre, String[] domicilio, String[] localidad
            ,String[] telefono) {
        try {
            listaSedes.removeAllViews();
            for (int i = 0; i < idDelegacionDom.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnSedes = inflater.inflate(R.layout.lista_delegacion, null);

                TextView idFila = (TextView) lnSedes.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnSedes.findViewById(R.id.linea1);
                TextView linea2 = (TextView) lnSedes.findViewById(R.id.linea2);
                TextView linea3 = (TextView) lnSedes.findViewById(R.id.linea3);
                TextView linea4 = (TextView) lnSedes.findViewById(R.id.linea4);
                ImageView btnTelefono = (ImageView) lnSedes.findViewById(R.id.btnTelefono);
                ImageView btnMapa = (ImageView) lnSedes.findViewById(R.id.btnMapa);

                idFila.setText(idDelegacionDom[i]);
                linea1.setText(nombre[i]);
                linea2.setText(domicilio[i]);
                linea3.setText(localidad[i]);
                linea4.setText(telefono[i]);

                final String _idDelegacion=idDelegacionDom[i];
                final String _telefonos=linea4.getText().toString();

                btnTelefono.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", _telefonos, null));
                        if (funciones.soportaLlamados(getContext(),intent)){
                            getActivity().startActivity(intent);
                        }else {
                            dialogo = fabrica_dialogos.dlgError(getContext(), "Este dispositvo no soporta llamados telefónicos");
                        }
                    }
                });

                btnMapa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        idCartillaPrestadores="DOMDELEG"+_idDelegacion;
                        mostrarMapa();
                    }
                });

                listaSedes.addView(lnSedes);
            }
            listaSedes.setVisibility(View.VISIBLE);
        } catch (Exception ex) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las sedes\n"+ex.getMessage());
            return;
        }
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
        mapFragment.getMapAsync(this);
        dialogoMapa.setCancelable(false);
        dialogoMapa.setCanceledOnTouchOutside(false);
        dialogoMapa.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
            dialogoMapaDatos = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los datos del mapa desde el buzon:\n" + e.getMessage());
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
                                    //dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Iniciando la sincronización");

                                    dialogo = new Dialog(getContext());
                                    dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                    dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialogo.setContentView(R.layout.dlg_modal_mapa_accion);
                                    /*RelativeLayout rlCerrar = (RelativeLayout) dialogo.findViewById(R.id.rlCerrar);
                                    ImageView btnCerrar = (ImageView) dialogo.findViewById(R.id.btnCerrar);

                                    rlCerrar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogo.dismiss();
                                        }
                                    });

                                    btnCerrar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogo.dismiss();
                                        }
                                    });

                                    LinearLayout lnLlamar = (LinearLayout) dialogo.findViewById(R.id.lnLlamar);
                                    LinearLayout lnSolOrden = (LinearLayout) dialogo.findViewById(R.id.lnSolOrden);
                                    LinearLayout lnSolTurno = (LinearLayout) dialogo.findViewById(R.id.lnSolTurno);
                                    LinearLayout lnSolFavorito = (LinearLayout) dialogo.findViewById(R.id.lnSolFavorito);
                                    lnSolTurno.setVisibility(View.GONE);
                                    lnSolOrden.setVisibility(View.GONE);

                                    for (int convenio = 0; convenio < idConvenio.length; convenio++) {
                                        if (idConvenio[convenio].equals(idConvenioLoc)) {
                                            if (ordenAccion[convenio].equals("05")) {
                                                lnSolTurno.setVisibility(View.VISIBLE);
                                            } else if (ordenAccion[convenio].equals("99")) {
                                                //Es una delegacion, dsolo teléfono
                                                lnSolFavorito.setVisibility(View.GONE);
                                            } else {
                                                lnSolOrden.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }

                                    lnLlamar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //llamarPorTelefono(idConvenioDomLoc);
                                        }
                                    });

                                    lnSolOrden.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //solOrden(idConvenioLoc);
                                        }
                                    });

                                    lnSolTurno.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //solTurno(idConvenioLoc);
                                        }
                                    });

                                    lnSolFavorito.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //agregarFavorido(idConvenioActual);
                                        }
                                    });*/
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los datos del mapa:\n" + e.getMessage());
            return;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnCartillaMedica){
            trigger.fireChange("cartilla_btnCartillaMedica");
        }else if(v.getId()==R.id.btnCartillaFarmacia){
            trigger.fireChange("cartilla_btnCartillaFarmacia");
        }
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
