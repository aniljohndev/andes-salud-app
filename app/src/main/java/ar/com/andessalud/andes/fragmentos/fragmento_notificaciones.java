package ar.com.andessalud.andes.fragmentos;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.Eventos_Click_Interface;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.adaptadores.Eventos_Adapter;
import ar.com.andessalud.andes.adaptadores.EventsNotificationAdapter;
import ar.com.andessalud.andes.adaptadores.adaptador_infowindow_cartilla_prestadores;
import ar.com.andessalud.andes.adaptadores.adaptador_notificaciones;
import ar.com.andessalud.andes.clases.InfoWindowData;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.funciones;
import ar.com.andessalud.andes.models.AppointmentNotificationList;
import ar.com.andessalud.andes.models.EventNotificationList;
import ar.com.andessalud.andes.models.MedicialPrescriptionList;
import ar.com.andessalud.andes.models.Notifications;
import ar.com.andessalud.andes.models.NotificationsModel;
import ar.com.andessalud.andes.principal;
import ar.com.andessalud.andes.twilio.MySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_notificaciones extends Fragment implements OnMapReadyCallback, Eventos_Click_Interface {
    String _codTipoAbrir = "", _idAbrir = "", _actualizarOrdenAMB = "", _actualizarTURNOS = "NO", _actualizarCredProv = "NO", _actualizarOrdenPract = "NO", _actualizarNotificaciones = "NO";
    private Dialog dialogo, dialogoMapa, dialogoMapaDatos, dialogAccion, dialogAccionPracticas;
    ArrayList<adap_notificaciones> totalAviso;
    View _actActual;
    LinearLayout loadingData;
    TextView lblEstadoLoading;
    adaptador_notificaciones adapter;
    EventsNotificationAdapter notificationAdapter;
    String _idActual, _tabSeleccionada, idCartillaPrestadores = "";
    private GoogleMap mMap;
    String[] ordenAccion, idConvenio, nombre, idConvenioDom, idDomicilioDom, domicilio, localidad, provincia, idDomicilioTel, telefono, lat, longitud;
    SupportMapFragment mapFragment;
    ListView listaMensajes;
    RecyclerView lista1;
    LinearLayout rlmensajes;
    Bitmap credentialBitmap;
    ArrayList<NotificationsModel> notificationList = new ArrayList<>();
    int page = 0, limit = 10;
    NestedScrollView nestedSv;
    ProgressBar idPBLoading;

    public fragmento_notificaciones() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_notificaciones, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _actActual = view;
        _codTipoAbrir = "";
        _idAbrir = "";
        Bundle extras = getArguments();
        if (extras != null) {
            _codTipoAbrir = extras.getString("_codTipo", "");
            _idAbrir = extras.getString("_idAbrir", "");
        }
        nestedSv = (NestedScrollView) view.findViewById(R.id.idNestedSV);
        idPBLoading = (ProgressBar) view.findViewById(R.id.idPBLoading);
        lblEstadoLoading = (TextView) view.findViewById(R.id.lblEstadoLoading);
        lblEstadoLoading.setText("Sincronizando las notificaciones");
        loadingData = (LinearLayout) view.findViewById(R.id.loadingData);
        ImageView imageView = view.findViewById(R.id.imgCargando);
        Glide.with(this).load(R.drawable.loading).into(imageView);
        rlmensajes = (LinearLayout) _actActual.findViewById(R.id.rlmensajes);
        rlmensajes.setVisibility(View.GONE);
        listaMensajes = (ListView) _actActual.findViewById(R.id.lista);
        lista1 = (RecyclerView) view.findViewById(R.id.lista1);


        SQLController database = new SQLController(getContext());
        database.eliminarTodosLosAvisos();
        sincronizarBuzon();
        lista1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        notificationAdapter = new EventsNotificationAdapter(getContext(), notificationList, this);
        lista1.setAdapter(notificationAdapter);
        try {
            getAllnotifications(getActivity(), page, limit);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        nestedSv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    page++;
                    idPBLoading.setVisibility(View.VISIBLE);
                    try {
                        getAllnotifications(getActivity(), page, limit);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    //region sincronizar buzon
    private void sincronizarBuzon() {
        try {
            //intenta registrar el ID de FCM del telefono APP
            String yaRegistroID = funciones.verificaSenderID(getActivity(), 3);
            if (yaRegistroID.equals("SD") || yaRegistroID.equals("NO")) {
                String valorDevuelto = "";
                if (yaRegistroID.equals("SD")) {
                    valorDevuelto = "No se puede sincronizar el buzón porque todavía no se registra el dispositivo";
                } else {
                    valorDevuelto = "No se puede puede sincronizar el buzón porque no se pudo registrar el dispositivo";
                }
                dialogo = fabrica_dialogos.dlgError(getContext(), valorDevuelto);
                return;
            }

            SQLController database = new SQLController(getContext());
            final String idAfiliadoTitular = database.obtenerIDAfiliado();
            if (idAfiliadoTitular.equals("")) {
                loadingData.setVisibility(View.GONE);
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            final String IMEI = funciones.obtenerIMEI(getContext());

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", idAfiliadoTitular);
            params.put("IMEI", IMEI);

            //dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Iniciando la sincronización");
            fabrica_WS.APPBuzonUltActualizacion(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    SQLController database = new SQLController(getContext());
                    for (int i = 0; i < valores.getElementsByTagName("tipo").getLength(); i++) {
                        if (valores.getElementsByTagName("tipo").item(i).getTextContent().equals("TURNOS")) {
                            _actualizarTURNOS = database.verificaActTURNOS(valores.getElementsByTagName("ultActualizacion").item(i).getTextContent());
                        }
                        if (valores.getElementsByTagName("tipo").item(i).getTextContent().equals("ORDENAMB")) {
                            _actualizarOrdenAMB = database.verificaActORDENAMB(valores.getElementsByTagName("ultActualizacion").item(i).getTextContent());
                        } else if (valores.getElementsByTagName("tipo").item(i).getTextContent().equals("ORDENPRAC")) {
                            _actualizarOrdenPract = database.verificaActORDENPRAC(valores.getElementsByTagName("ultActualizacion").item(i).getTextContent());
                        }/*
                        else if (valores.getElementsByTagName("tipo").item(i).getTextContent().equals("INTERNAC")) {
                            _actualizarInternacion=database.verificaActInternacion(valores.getElementsByTagName("ultActualizacion").item(i).getTextContent());
                        }
                        else if (valores.getElementsByTagName("tipo").item(i).getTextContent().equals("PRESDOC")) {
                            _actualizarPresDoc=database.verificaActPresDoc(valores.getElementsByTagName("ultActualizacion").item(i).getTextContent());
                        }*/ else if (valores.getElementsByTagName("tipo").item(i).getTextContent().equals("CREDPROV")) {
                            _actualizarCredProv = database.verificaActCredProv(valores.getElementsByTagName("ultActualizacion").item(i).getTextContent());
                        }
                        /*else if (valores.getElementsByTagName("tipo").item(i).getTextContent().equals("INCGF")) {
                            _actualizarIncGF=database.verificaActIncGF(valores.getElementsByTagName("ultActualizacion").item(i).getTextContent());
                        }*/
                    }

                    /*_actualizarInternacion="SI";
                    _actualizarPresDoc="SI";*/
                    /*_actualizarIncGF="SI";
                    _actualizarCredNueva="SI";
                    */
                    /*else if (_actualizarInternacion.equals("SI")){
                        actualizarBuzonINTERNACION();
                    }else if (_actualizarPresDoc.equals("SI")){
                        actualizarBuzonPRESDOC();
                    }
                    */
                    /*else if (_actualizarIncGF.equals("SI")){
                        actualizarBuzonINCGF();
                    }else if (_actualizarCredNueva.equals("SI")){
                        actualizarCredNueva();
                    }*/

                    _actualizarTURNOS = "SI";
                    _actualizarOrdenAMB = "SI";
                    _actualizarOrdenPract = "SI";
                    _actualizarCredProv = "SI";
                    _actualizarNotificaciones = "SI";

                    if (_actualizarTURNOS.equals("SI")) {
                        actualizarBuzonTURNOS();
                    } else if (_actualizarOrdenAMB.equals("SI")) {
                        actualizarBuzonORDENAMB();
                    } else if (_actualizarOrdenPract.equals("SI")) {
                        actualizarBuzonORDENPRAC();
                    } else if (_actualizarCredProv.equals("SI")) {
                        actualizarBuzonCREDPROV();
                    } else if (_actualizarNotificaciones.equals("SI")) {
                        actualizarNotificaciones();
                    } else {
                        refrescarBuzon();
                    }
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    loadingData.setVisibility(View.GONE);
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                    refrescarBuzon();
                }
            });

        } catch (Exception e) {
            loadingData.setVisibility(View.GONE);
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al sincronizar las notificaciones:\n" + e.getMessage());
        }
    }

    //#region turnos
    private void actualizarBuzonTURNOS() {
        try {
            lblEstadoLoading.setText("Sincronizando los turnos");
            SQLController database = new SQLController(getContext());
            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", database.obtenerIDAfiliado());
            params.put("IMEI", funciones.obtenerIMEI(getContext()));

            fabrica_WS.APPBuzonActualizarTURNOS(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    SQLController database = new SQLController(getContext());
                    database.eliminarBuzonTURNOS();
                    String[] idTurno = new String[valores.getElementsByTagName("idTurno").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idTurno").getLength(); i++) {
                        String resultado = database.agregarTurnoAndes(valores.getElementsByTagName("idTurno").item(i).getTextContent()
                                , valores.getElementsByTagName("apellNomb").item(i).getTextContent()
                                , valores.getElementsByTagName("nombreEspecialidad").item(i).getTextContent()
                                , valores.getElementsByTagName("policonsultorio").item(i).getTextContent()
                                , valores.getElementsByTagName("direccion").item(i).getTextContent()
                                , valores.getElementsByTagName("nombreMedico").item(i).getTextContent()
                                , valores.getElementsByTagName("fecTurno").item(i).getTextContent()
                                , valores.getElementsByTagName("fecSolicitud").item(i).getTextContent()
                                , valores.getElementsByTagName("estado").item(i).getTextContent()
                                , valores.getElementsByTagName("coseguro").item(i).getTextContent());
                    }
                    for (int j = 0; j < valores.getElementsByTagName("idMensaje").getLength(); j++) {
                        database.agregarMensajeSincronizacion(valores.getElementsByTagName("idMensaje").item(j).getTextContent()
                                , valores.getElementsByTagName("idGeneral").item(j).getTextContent()
                                , valores.getElementsByTagName("mensajeAPP").item(j).getTextContent()
                                , valores.getElementsByTagName("filaImagen").item(j).getTextContent()
                                , valores.getElementsByTagName("desdeAPP").item(j).getTextContent()
                                , valores.getElementsByTagName("fechaMsg").item(j).getTextContent()
                                , valores.getElementsByTagName("contexto").item(j).getTextContent());
                    }

                    if (_actualizarOrdenAMB.equals("SI")) {
                        actualizarBuzonORDENAMB();
                    } else if (_actualizarOrdenPract.equals("SI")) {
                        actualizarBuzonORDENPRAC();
                    } else if (_actualizarCredProv.equals("SI")) {
                        actualizarBuzonCREDPROV();
                    } else if (_actualizarNotificaciones.equals("SI")) {
                        actualizarNotificaciones();
                    } else {
                        refrescarBuzon();
                    }
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    loadingData.setVisibility(View.GONE);
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                    refrescarBuzon();
                }
            });
        } catch (Exception e) {
            loadingData.setVisibility(View.GONE);
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al sincronizar los turnos de las notificaciones:\n" + e.getMessage());
        }
    }
    //#endregion

    private void actualizarBuzonORDENAMB() {
        try {
            lblEstadoLoading.setText("Sincronizando las ordenes");
            SQLController database = new SQLController(getContext());
            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", database.obtenerIDAfiliado());
            params.put("IMEI", funciones.obtenerIMEI(getContext()));

            fabrica_WS.APPBuzonActualizarORDENAMB(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    SQLController database = new SQLController(getContext());
                    database.eliminarBuzonOrdenAMB();
                    for (int i = 0; i < valores.getElementsByTagName("idOrden").getLength(); i++) {
                        String resultado = database.agregarORDENAMBEncabezado(valores.getElementsByTagName("idOrden").item(i).getTextContent()
                                , valores.getElementsByTagName("prestador").item(i).getTextContent()
                                , valores.getElementsByTagName("dom1Prestador").item(i).getTextContent()
                                , valores.getElementsByTagName("dom2Prestador").item(i).getTextContent()
                                , valores.getElementsByTagName("codAutorizacion").item(i).getTextContent()
                                , valores.getElementsByTagName("fecSolicitud").item(i).getTextContent()
                                , valores.getElementsByTagName("fecVencimiento").item(i).getTextContent()
                                , valores.getElementsByTagName("afiliado").item(i).getTextContent()
                                , valores.getElementsByTagName("codEstado").item(i).getTextContent()
                                , valores.getElementsByTagName("coseguro").item(i).getTextContent());
                    }
                    for (int i = 0; i < valores.getElementsByTagName("idOrdenDetalle").getLength(); i++) {
                        String resultado = database.agregarORDENAMBDetalle(valores.getElementsByTagName("idOrdenDetalle").item(i).getTextContent()
                                , valores.getElementsByTagName("nombrePrestacion").item(i).getTextContent()
                                , valores.getElementsByTagName("cantidad").item(i).getTextContent()
                                , valores.getElementsByTagName("coseguroPrestacion").item(i).getTextContent());
                    }

                    for (int j = 0; j < valores.getElementsByTagName("idMensaje").getLength(); j++) {
                        database.agregarMensajeSincronizacion(valores.getElementsByTagName("idMensaje").item(j).getTextContent()
                                , valores.getElementsByTagName("idGeneral").item(j).getTextContent()
                                , valores.getElementsByTagName("mensajeAPP").item(j).getTextContent()
                                , valores.getElementsByTagName("filaImagen").item(j).getTextContent()
                                , valores.getElementsByTagName("desdeAPP").item(j).getTextContent()
                                , valores.getElementsByTagName("fechaMsg").item(j).getTextContent()
                                , valores.getElementsByTagName("contexto").item(j).getTextContent());
                    }

                    if (_actualizarOrdenPract.equals("SI")) {
                        actualizarBuzonORDENPRAC();
                    } else if (_actualizarCredProv.equals("SI")) {
                        actualizarBuzonCREDPROV();
                    } else if (_actualizarNotificaciones.equals("SI")) {
                        actualizarNotificaciones();
                    } else {
                        refrescarBuzon();
                    }
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    loadingData.setVisibility(View.GONE);
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                    refrescarBuzon();
                }
            });
        } catch (Exception e) {
            loadingData.setVisibility(View.GONE);
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al sincronizar las ordenes ambulatorias de las notificaciones:\n" + e.getMessage());
        }
    }

    private void actualizarBuzonORDENPRAC() {
        try {
            lblEstadoLoading.setText("Sincronizando las ordenes de prácticas");
            SQLController database = new SQLController(getContext());
            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", database.obtenerIDAfiliado());
            params.put("IMEI", funciones.obtenerIMEI(getContext()));

            fabrica_WS.APPBuzonActualizarORDENPRAC(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    SQLController database = new SQLController(getContext());
                    database.eliminarBuzonOrdenPRAC();
                    for (int i = 0; i < valores.getElementsByTagName("idOrden").getLength(); i++) {
                        String resultado = database.agregarORDENPRACEncabezado(valores.getElementsByTagName("idOrden").item(i).getTextContent()
                                , valores.getElementsByTagName("idOrdenParcial").item(i).getTextContent()
                                , valores.getElementsByTagName("afiliado").item(i).getTextContent()
                                , valores.getElementsByTagName("fecSolicitud").item(i).getTextContent()
                                , valores.getElementsByTagName("estado").item(i).getTextContent()
                                , valores.getElementsByTagName("fecFinalizacion").item(i).getTextContent()
                                , valores.getElementsByTagName("comentarioRechazo").item(i).getTextContent()
                                , valores.getElementsByTagName("prestador").item(i).getTextContent()
                                , valores.getElementsByTagName("domicilio1Autorizado").item(i).getTextContent()
                                , valores.getElementsByTagName("coseguro").item(i).getTextContent()
                                , valores.getElementsByTagName("domicilio2Autorizado").item(i).getTextContent()
                                , valores.getElementsByTagName("codAutorizacion").item(i).getTextContent()
                                , valores.getElementsByTagName("fechaVencimiento").item(i).getTextContent()
                                , valores.getElementsByTagName("comentarioAfiliado").item(i).getTextContent());
                    }
                    for (int i = 0; i < valores.getElementsByTagName("idOrdenDetalle").getLength(); i++) {
                        String resultado = database.agregarORDENPRACDetalle(valores.getElementsByTagName("idOrdenDetalle").item(i).getTextContent()
                                , valores.getElementsByTagName("idOrdenPrestacion").item(i).getTextContent()
                                , valores.getElementsByTagName("idOrdenParcialPrestacion").item(i).getTextContent()
                                , valores.getElementsByTagName("cantidad").item(i).getTextContent()
                                , valores.getElementsByTagName("prestacion").item(i).getTextContent()
                                , valores.getElementsByTagName("coseguroPrestacion").item(i).getTextContent());
                    }

                    for (int j = 0; j < valores.getElementsByTagName("idMensaje").getLength(); j++) {
                        database.agregarMensajeSincronizacion(valores.getElementsByTagName("idMensaje").item(j).getTextContent()
                                , valores.getElementsByTagName("idGeneral").item(j).getTextContent()
                                , valores.getElementsByTagName("mensajeAPP").item(j).getTextContent()
                                , valores.getElementsByTagName("filaImagen").item(j).getTextContent()
                                , valores.getElementsByTagName("desdeAPP").item(j).getTextContent()
                                , valores.getElementsByTagName("fechaMsg").item(j).getTextContent()
                                , valores.getElementsByTagName("contexto").item(j).getTextContent());
                    }

                    if (_actualizarCredProv.equals("SI")) {
                        actualizarBuzonCREDPROV();
                    } else if (_actualizarNotificaciones.equals("SI")) {
                        actualizarNotificaciones();
                    } else {
                        refrescarBuzon();
                    }
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    loadingData.setVisibility(View.GONE);
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                    refrescarBuzon();
                }
            });
        } catch (Exception e) {
            loadingData.setVisibility(View.GONE);
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al sincronizar las ordenes ambulatorias de las notificaciones:\n" + e.getMessage());
        }
    }

    private void actualizarBuzonCREDPROV() {
        try {
            lblEstadoLoading.setText("Sincronizando las credenciales virtuales");
            SQLController database = new SQLController(getContext());
            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", database.obtenerIDAfiliado());
            params.put("IMEI", funciones.obtenerIMEI(getContext()));

            fabrica_WS.APPBuzonActualizarORDENAMB(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    SQLController database = new SQLController(getContext());
                    database.eliminarBuzonCREDPROV();
                    for (int i = 0; i < valores.getElementsByTagName("idAfiliado").getLength(); i++) {
                        String resultado = database.agregarCREDPROV(valores.getElementsByTagName("idAfiliado").item(i).getTextContent()
                                , valores.getElementsByTagName("fecSolicitud").item(i).getTextContent()
                                , valores.getElementsByTagName("tipoTarjeta").item(i).getTextContent()
                                , valores.getElementsByTagName("apellNomb").item(i).getTextContent()
                                , valores.getElementsByTagName("numTarjeta").item(i).getTextContent()
                                , valores.getElementsByTagName("fecVencimiento").item(i).getTextContent()
                                , valores.getElementsByTagName("idCredencial").item(i).getTextContent());
                    }

                    if (_actualizarNotificaciones.equals("SI")) {
                        actualizarNotificaciones();
                    } else {
                        refrescarBuzon();
                    }
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    loadingData.setVisibility(View.GONE);
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                    refrescarBuzon();
                }
            });
        } catch (Exception e) {
            loadingData.setVisibility(View.GONE);
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al sincronizar las credenciales virtuales de las notificaciones:\n" + e.getMessage());
        }
    }

    private void actualizarNotificaciones() {
        try {
            lblEstadoLoading.setText("Sincronizando las notificaciones");
            SQLController database = new SQLController(getContext());
            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", database.obtenerIDAfiliado());
            params.put("IMEI", funciones.obtenerIMEI(getContext()));

            fabrica_WS.APPBuzonActualizarNOTIFICACIONES(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    SQLController database = new SQLController(getContext());
                    database.eliminarNotificaciones();
                    for (int i = 0; i < valores.getElementsByTagName("tipoContenido").getLength(); i++) {
                        Cursor resultado = database.agregarMensajeAvidoGen(valores.getElementsByTagName("tipoContenido").item(i).getTextContent()
                                , valores.getElementsByTagName("contenido").item(i).getTextContent()
                                , valores.getElementsByTagName("idCampana").item(i).getTextContent()
                                , valores.getElementsByTagName("asunto").item(i).getTextContent()
                                , valores.getElementsByTagName("fecha").item(i).getTextContent());
                    }
                    refrescarBuzon();
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    loadingData.setVisibility(View.GONE);
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                    refrescarBuzon();
                }
            });
        } catch (Exception e) {
            loadingData.setVisibility(View.GONE);
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al sincronizar las notificaciones del buzon:\n" + e.getMessage());
        }
    }

    private void refrescarBuzon() {
        SQLController database = new SQLController(getContext());
        Cursor cursor = database.leerBuzonAvisos(getContext());

        if (cursor.getCount() > 0) {
            totalAviso = new ArrayList<adap_notificaciones>();
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                adap_notificaciones pregResp = new adap_notificaciones(cursor.getString(cursor.getColumnIndexOrThrow("_id"))
                        , cursor.getString(cursor.getColumnIndexOrThrow("codTipo"))
                        , cursor.getString(cursor.getColumnIndexOrThrow("descTipo"))
                        , cursor.getString(cursor.getColumnIndexOrThrow("afiliado"))
                        , cursor.getString(cursor.getColumnIndexOrThrow("fecEmision"))
                );
                totalAviso.add(pregResp);
                cursor.moveToNext();
            }

            adapter = new adaptador_notificaciones(getContext(), totalAviso);
            listaMensajes.setAdapter(adapter);

            listaMensajes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    adap_notificaciones map = (adap_notificaciones) listaMensajes.getItemAtPosition(position);
                    _idActual = map.id;
                    if (map.codTipo.equals("AMBAUT")
                            || map.codTipo.equals("AMBAUD")
                            || map.codTipo.equals("RECHAZADO")) {
                        mostrarDialogoAMB(map.codTipo);
                    } else if (map.codTipo.equals("RECHAZOPRACTICA")) {
                        mostrarDialogoPractRechazo(map.codTipo);
                    } else if (map.codTipo.equals("GUARDIAACTIVA")) {
                        mostrarDialogoGuardiaActiva(map.codTipo);
                    } else if (map.codTipo.equals("TURSINCONF")
                            || map.codTipo.equals("TURCANC")
                            || map.codTipo.equals("TURNRECH")
                            || map.codTipo.equals("TURAUDIT")
                    ) {
                        mostrarDialogoTURNOS(map.codTipo);
                    } else if (map.codTipo.equals("CREDPROV")) {
                        mostrarCredecialProvisoria();
                    } else if (map.codTipo.equals("NOTIFICACION")) {
                        mostrarNotificacion();
                    } else if (map.codTipo.equals("PRACTAUT")) {
                        mostrarDialogoPracticaAUT();
                    } else if (map.codTipo.equals("PRACTAUT")) {
                        mostrarDialogoPracticaAUT();
                    } else if (map.codTipo.equals("PRACTAUD")) {
                        mostrarDialogoPracticaAUD();
                    }
                }
            });
            //funciones.setListViewHeightBasedOnChildren(listaMensajes,totalAviso.size()*10);

            Intent intent = getActivity().getIntent();
            _idActual = intent.getStringExtra("_id");
            _tabSeleccionada = intent.getStringExtra("_tab");
            if (_idActual != null && !_idActual.equals("")) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    if (cursor.getString(cursor.getColumnIndexOrThrow("_id")).equals(_idActual)) {
                        //mostrarDialogo(cursor.getString(cursor.getColumnIndexOrThrow("codTipo")),_tabSeleccionada);
                        return;
                    }
                    cursor.moveToNext();
                }
            }
        }

        if (!_codTipoAbrir.equals("")) {
            _idActual = _idAbrir;
            //mostrarDialogo(_codTipoAbrir,"0");
        }
        loadingData.setVisibility(View.GONE);
        rlmensajes.setVisibility(View.VISIBLE);
    }

    //#region orden ambulatorio
    private void mostrarDialogoAMB(String codTipo) {
        dialogAccion = new Dialog(getContext());
        dialogAccion.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogAccion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAccion.setContentView(R.layout.dlg_buzon_orden);

        LinearLayout lnAMBAUT = (LinearLayout) dialogAccion.findViewById(R.id.lnAMBAUT);
        TextView txtCodAutorizacion = (TextView) dialogAccion.findViewById(R.id.txtCodAutorizacion);
        TextView txtFecVen = (TextView) dialogAccion.findViewById(R.id.txtFecVen);
        TextView txtPrestador = (TextView) dialogAccion.findViewById(R.id.txtPrestador);
        TextView txtDomPrestador = (TextView) dialogAccion.findViewById(R.id.txtDomPrestador);
        TextView lblDomicilioTitulo = (TextView) dialogAccion.findViewById(R.id.lblDomicilioTitulo);
        LinearLayout lnDomicilio = (LinearLayout) dialogAccion.findViewById(R.id.lnDomicilio);
        TextView txtCoseguro = (TextView) dialogAccion.findViewById(R.id.txtCoseguro);
        LinearLayout listaPrestacionesAMB = (LinearLayout) dialogAccion.findViewById(R.id.listaPrestacionesAMB);
        ImageView btnMapa = (ImageView) dialogAccion.findViewById(R.id.btnMapa);
        LinearLayout lnAMBAUD = (LinearLayout) dialogAccion.findViewById(R.id.lnAMBAUD);
        TextView txtAvisoAMBAUD = (TextView) dialogAccion.findViewById(R.id.txtAvisoAMBAUD);
        LinearLayout lnAMBRECH = (LinearLayout) dialogAccion.findViewById(R.id.lnAMBRECH);
        TextView txtFecRechazo = (TextView) dialogAccion.findViewById(R.id.txtFecRechazo);
        TextView txtMotivo = (TextView) dialogAccion.findViewById(R.id.txtMotivo);

        lnAMBAUT.setVisibility(View.GONE);
        lnAMBAUD.setVisibility(View.GONE);
        lnAMBRECH.setVisibility(View.GONE);

        if (codTipo.equals("AMBAUD")) {
            txtAvisoAMBAUD.setText("Nuestro equipo médico se encuentra evaluando tu solicitud. En breve recibirás novedades . Muchas gracias");
            lnAMBAUD.setVisibility(View.VISIBLE);
        } else if (codTipo.equals("AMBAUT")
                || (codTipo.equals("RECHAZADO"))) {
            SQLController database = new SQLController(getContext());
            database.eliminarAviso(_idActual);
            Cursor datDatos = database.leerDetalleEstadoAmbulatorio(_idActual);
            if (datDatos.getCount() > 0) {
                if (datDatos.getString(1).equals("AUT")) {
                    lnAMBAUT.setVisibility(View.VISIBLE);
                    datDatos = database.leerDetalleAmbulatorioAUT(_idActual);

                    txtPrestador.setText(datDatos.getString(1));
                    if (!datDatos.getString(2).equals("")) {
                        txtDomPrestador.setText(datDatos.getString(2).trim());
                        lnDomicilio.setVisibility(View.VISIBLE);
                        lblDomicilioTitulo.setVisibility(View.VISIBLE);
                        lnDomicilio.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                idCartillaPrestadores = "DOMBUZAMB" + _idActual;
                                mostrarMapa();
                            }
                        });
                    } else {
                        lnDomicilio.setVisibility(View.GONE);
                        lblDomicilioTitulo.setVisibility(View.GONE);
                    }
                    txtCodAutorizacion.setText(datDatos.getString(4));
                    txtFecVen.setText(datDatos.getString(6));
                    txtCoseguro.setText(datDatos.getString(8));

                    datDatos = database.leerDetalleAmbulatorioPrestacionesAUT(_idActual);
                    if (datDatos.getCount() > 0) {
                        do {
                            LayoutInflater inflater = this.getLayoutInflater();
                            View lnPrestaciones = inflater.inflate(R.layout.lista_un_renglon_prestaciones, null);
                            TextView txtCantidad = (TextView) lnPrestaciones.findViewById(R.id.txtCantidad);
                            TextView txtPrestacion = (TextView) lnPrestaciones.findViewById(R.id.txtPrestacion);
                            TextView txtCoseguroPrestacion = (TextView) lnPrestaciones.findViewById(R.id.txtCoseguro);

                            txtCantidad.setText(datDatos.getString(1));
                            txtPrestacion.setText(datDatos.getString(2));
                            txtCoseguroPrestacion.setText(datDatos.getString(3));
                            listaPrestacionesAMB.addView(lnPrestaciones);
                        } while (datDatos.moveToNext());
                    }
                    listaPrestacionesAMB.setVisibility(View.VISIBLE);
                } else if (datDatos.getString(1).equals("RECHAZADO")) {
                    datDatos = database.leerDetalleAmbulatorioREC(_idActual);
                    txtFecRechazo.setText(datDatos.getString(4));
                    txtMotivo.setText(datDatos.getString(3));
                    lnAMBRECH.setVisibility(View.VISIBLE);
                }
            }
        }
        dialogAccion.show();
    }
    //#endregion

    //#region orden de practica
    private void mostrarDialogoPractRechazo(String codTipo) {
        dialogAccion = new Dialog(getContext());
        dialogAccion.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogAccion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAccion.setContentView(R.layout.dlg_buzon_rechazado);

        TextView txtFecFin = (TextView) dialogAccion.findViewById(R.id.txtFecFin);
        TextView txtMensaje = (TextView) dialogAccion.findViewById(R.id.txtMensaje);
        ImageView imgMensaje = (ImageView) dialogAccion.findViewById(R.id.imgMensaje);

        if (codTipo.equals("RECHAZOPRACTICA")) {
            SQLController database = new SQLController(getContext());
            Cursor datDatos = database.leerDetalleEstadoPracticasREC(_idActual);

            txtFecFin.setText(datDatos.getString(3));
            txtMensaje.setText(datDatos.getString(4));
            imgMensaje.setImageResource(R.drawable.estudio_medico_sin_fondo);
            imgMensaje.getLayoutParams().width = 40;
        }
        dialogAccion.show();
    }

    private void mostrarDialogoPracticaAUD() {
        dialogAccion = new Dialog(getContext());
        dialogAccion.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogAccion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAccion.setContentView(R.layout.dlg_buzon_orden_pract_aud);
        dialogAccion.show();
    }

    private void mostrarDialogoPracticaAUT() {
        dialogAccion = new Dialog(getContext());
        dialogAccion.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogAccion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAccion.setContentView(R.layout.dlg_buzon_orden_pract);

        SQLController database = new SQLController(getContext());
        database.eliminarAviso(_idActual);
        Cursor datDatos = database.leerOrdenesPracticasAUT(_idActual);

        LinearLayout lnListaOrdenes = (LinearLayout) dialogAccion.findViewById(R.id.lnListaOrdenes);
        lnListaOrdenes.removeAllViews();

        if (datDatos.getCount() > 0) {
            while (datDatos.isAfterLast() == false) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnOrdenAUT = inflater.inflate(R.layout.fila_ordenes_pract_aut, null);

                final String idOrdenCh = datDatos.getString(0);
                final String lblPrestadorCh = datDatos.getString(1);
                final String txtCodigoCh = datDatos.getString(2);

                TextView idOrden = (TextView) lnOrdenAUT.findViewById(R.id.idOrden);
                TextView lblPrestador = (TextView) lnOrdenAUT.findViewById(R.id.lblPrestador);
                TextView txtCodigo = (TextView) lnOrdenAUT.findViewById(R.id.txtCodigo);

                idOrden.setText(idOrdenCh);
                lblPrestador.setText(lblPrestadorCh);
                txtCodigo.setText(txtCodigoCh);

                lnOrdenAUT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarOrdenPracticaAUT(idOrdenCh);
                    }
                });
                lnListaOrdenes.addView(lnOrdenAUT);
                datDatos.moveToNext();
            }
            lnListaOrdenes.setVisibility(View.VISIBLE);
        }
        dialogAccion.show();
    }

    private void mostrarOrdenPracticaAUT(String idOrden) {
        dialogAccionPracticas = new Dialog(getContext());
        dialogAccionPracticas.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogAccionPracticas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAccionPracticas.setContentView(R.layout.dlg_buzon_orden_pract_detalle);

        SQLController database = new SQLController(getContext());
        database.eliminarAviso(_idActual);
        Cursor datDatos = database.leerDetalleOrdenPracticas(idOrden);

        TextView txtCodAutorizacion = (TextView) dialogAccionPracticas.findViewById(R.id.txtCodAutorizacion);
        TextView txtFecVen = (TextView) dialogAccionPracticas.findViewById(R.id.txtFecVen);
        TextView txtPrestador = (TextView) dialogAccionPracticas.findViewById(R.id.txtPrestador);
        TextView txtDomPrestador = (TextView) dialogAccionPracticas.findViewById(R.id.txtDomPrestador);
        TextView lblDomicilioTitulo = (TextView) dialogAccionPracticas.findViewById(R.id.lblDomicilioTitulo);
        LinearLayout lnDomicilio = (LinearLayout) dialogAccionPracticas.findViewById(R.id.lnDomicilio);
        TextView txtCoseguro = (TextView) dialogAccionPracticas.findViewById(R.id.txtCoseguro);
        LinearLayout listaPrestacionesAMB = (LinearLayout) dialogAccionPracticas.findViewById(R.id.listaPrestacionesAMB);
        ImageView btnMapa = (ImageView) dialogAccionPracticas.findViewById(R.id.btnMapa);

        txtPrestador.setText(datDatos.getString(1));
        txtCodAutorizacion.setText(datDatos.getString(2));
        txtFecVen.setText(datDatos.getString(4));
        txtCoseguro.setText(datDatos.getString(8));
        if (!datDatos.getString(5).equals("")) {
            txtDomPrestador.setText(datDatos.getString(5).trim().replaceAll("\t", ""));
            lnDomicilio.setVisibility(View.VISIBLE);
            lblDomicilioTitulo.setVisibility(View.VISIBLE);
            lnDomicilio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    idCartillaPrestadores = "DOMBUZPRAC" + idOrden;
                    mostrarMapa();
                }
            });
        } else {
            lnDomicilio.setVisibility(View.GONE);
            lblDomicilioTitulo.setVisibility(View.GONE);
        }
        datDatos = database.leerPracticasEnOrden(idOrden);
        if (datDatos.getCount() > 0) {
            do {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnPrestaciones = inflater.inflate(R.layout.lista_un_renglon_prestaciones, null);
                TextView txtCantidad = (TextView) lnPrestaciones.findViewById(R.id.txtCantidad);
                TextView txtPrestacion = (TextView) lnPrestaciones.findViewById(R.id.txtPrestacion);
                TextView txtCoseguroPrestacion = (TextView) lnPrestaciones.findViewById(R.id.txtCoseguro);

                txtCantidad.setText(datDatos.getString(1));
                txtPrestacion.setText(datDatos.getString(2));
                txtCoseguroPrestacion.setText(datDatos.getString(3));
                listaPrestacionesAMB.addView(lnPrestaciones);
            } while (datDatos.moveToNext());
        }
        listaPrestacionesAMB.setVisibility(View.VISIBLE);
        dialogAccionPracticas.show();
    }
    //#endregion

    //#region turnos
    private void mostrarDialogoTURNOS(String codTipo) {
        dialogAccion = new Dialog(getContext());
        dialogAccion.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogAccion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAccion.setContentView(R.layout.dlg_buzon_turnos);

        TextView txtFecTurno = (TextView) dialogAccion.findViewById(R.id.txtFecTurno);
        TextView txtProfesional = (TextView) dialogAccion.findViewById(R.id.txtProfesional);
        TextView txtCentro = (TextView) dialogAccion.findViewById(R.id.txtCentro);
        LinearLayout lnDomicilio = (LinearLayout) dialogAccion.findViewById(R.id.lnDomicilio);
        TextView txtDomPrestador = (TextView) dialogAccion.findViewById(R.id.txtDomPrestador);
        TextView txtCoseguro = (TextView) dialogAccion.findViewById(R.id.txtCoseguro);
        ImageView btnMapa = (ImageView) dialogAccion.findViewById(R.id.btnMapa);
        LinearLayout lnCancelarTurno = (LinearLayout) dialogAccion.findViewById(R.id.lnCancelarTurno);
        lnCancelarTurno.setVisibility(View.GONE);
        LinearLayout lnLblTurnoCacelado = (LinearLayout) dialogAccion.findViewById(R.id.lnLblTurnoCacelado);
        lnLblTurnoCacelado.setVisibility(View.GONE);
        TextView lblTurnoCancelado = (TextView) dialogAccion.findViewById(R.id.lblTurnoCancelado);
        lblTurnoCancelado.setVisibility(View.GONE);

        SQLController database = new SQLController(getContext());
        database.eliminarAviso(_idActual);
        Cursor datDatos = database.leerDetalleTURANDES(_idActual);
        if (datDatos.getCount() > 0) {
            if (datDatos.getString(4).equals("--")) {
                btnMapa.setVisibility(View.GONE);
            }
            txtFecTurno.setText(datDatos.getString(1));
            txtProfesional.setText(datDatos.getString(2));
            txtCentro.setText(datDatos.getString(3));
            txtDomPrestador.setText(datDatos.getString(4));
            txtCoseguro.setText("Coseguro: $" + datDatos.getString(5));
            if (codTipo.equals("TURSINCONF")) {
                lnCancelarTurno.setVisibility(View.VISIBLE);
                lnCancelarTurno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelarTurnoANDES(_idActual);
                    }
                });
            } else if (codTipo.equals("TURCANC")) {
                lblTurnoCancelado.setText("TURNO CANCELADO");
                lblTurnoCancelado.setVisibility(View.VISIBLE);
                lnLblTurnoCacelado.setVisibility(View.VISIBLE);
            } else if (codTipo.equals("TURNRECH")) {
                lblTurnoCancelado.setText("TURNO NO AUTORIZADO");
                lblTurnoCancelado.setVisibility(View.VISIBLE);
                lnLblTurnoCacelado.setVisibility(View.VISIBLE);
            } else if (codTipo.equals("TURAUDIT")) {
                lblTurnoCancelado.setText("TURNO EN AUDITORIA");
                lblTurnoCancelado.setVisibility(View.VISIBLE);
                lnLblTurnoCacelado.setVisibility(View.VISIBLE);
            }

            btnMapa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    idCartillaPrestadores = "TURNO" + _idActual;
                    mostrarMapa();
                }
            });
        }
        dialogAccion.show();
    }

    private void cancelarTurnoANDES(String idTurno) {
        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Intentando cancelar el turno");
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("idTurno", idTurno);
            params.put("IMEI", funciones.obtenerIMEI(getContext()));

            fabrica_WS.APPCancelarTurno(getActivity(), params, new SuccessResponseHandler<Document>() {
                        @Override
                        public void onSuccess(Document valores) {
                            dialogo.dismiss();
                            dialogAccion.dismiss();
                            SQLController database = new SQLController(getContext());
                            database.cancelarTurnoANDES(_idActual);

                            String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                            dialogo = fabrica_dialogos.dlgAviso(getContext(), msgAviso);
                            LinearLayout lnMsgTotal = (LinearLayout) dialogo.findViewById(R.id.lnMsgTotal);
                            lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogo.dismiss();
                                    sincronizarBuzon();
                                    return;
                                }
                            });
                            return;
                        }
                    }, new ErrorResponseHandler() {
                        @Override
                        public void onError(String msg) {
                            dialogo.dismiss();
                            dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                        }
                    }
            );
        } catch (Exception ex) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error al cancelar el turno: \n" + ex.getMessage());
        }
    }
    //#endregion

    //#region guardia activa
    private void mostrarDialogoGuardiaActiva(String codTipo) {
        dialogAccion = new Dialog(getContext());
        dialogAccion.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogAccion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAccion.setContentView(R.layout.dlg_buzon_guardia_activa);

        TextView txtCodAutorizacion = (TextView) dialogAccion.findViewById(R.id.txtCodAutorizacion);
        TextView txtPrestador = (TextView) dialogAccion.findViewById(R.id.txtPrestador);
        TextView txtPrestacion = (TextView) dialogAccion.findViewById(R.id.txtPrestacion);
        TextView txtFecVencimiento = (TextView) dialogAccion.findViewById(R.id.txtFecVencimiento);

        SQLController database = new SQLController(getContext());
        database.eliminarAviso(_idActual);
        Cursor datDatos = database.leerDetalleGuardiaActiva(_idActual);
        if (datDatos.getCount() > 0) {
            txtCodAutorizacion.setText(datDatos.getString(6));
            txtPrestador.setText(datDatos.getString(2));
            txtPrestacion.setText(datDatos.getString(3));
            txtFecVencimiento.setText(datDatos.getString(5));
            dialogAccion.show();
        } else {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar el detalle de la guardia activa.");
        }
    }
    //#endregion

    //#region credencial provisoria
    private void mostrarCredecialProvisoria() {
        SQLController database = new SQLController(getContext());
        Cursor datDatos = database.leerDetalleCREDPROV(_idActual);
        if (datDatos.getCount() > 0) {
            String nombreAfiliadoCred = datDatos.getString(3);
            String numAfiliadoCred = datDatos.getString(4);
            String fecVencimientoCred = datDatos.getString(5);
            String tipoTarjetaCred = datDatos.getString(1);
            armarCredencial(nombreAfiliadoCred, numAfiliadoCred, fecVencimientoCred, tipoTarjetaCred);
        }
    }

    public void armarCredencial(String afiliado, String numAfiliado, String validez, String tipoTarjeta) {
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels - 50;
            Double d = new Double((width * 0.63));
            int height = d.intValue();
            int cardFrontImgRegID, cardBackImgRegID;
            if (tipoTarjeta.equals("TARJETA GOLD")) {
                cardFrontImgRegID = R.drawable.cred_gold_front;
                cardBackImgRegID = R.drawable.cred_gold_back;
            } else if (tipoTarjeta.equals("TARJETA BLACK")) {
                cardFrontImgRegID = R.drawable.cred_black_front;
                cardBackImgRegID = R.drawable.cred_black_back;
            } else if (tipoTarjeta.equals("TARJETA PLATINUM")) {
                cardFrontImgRegID = R.drawable.cred_platinum_front;
                cardBackImgRegID = R.drawable.cred_platinum_back;
            } else if (tipoTarjeta.equals("TARJETA GREEN")) {
                cardFrontImgRegID = R.drawable.cred_green_front;
                cardBackImgRegID = R.drawable.cred_green_back;
            } else if (tipoTarjeta.equals("TARJETA TITANIUM")) {
                cardFrontImgRegID = R.drawable.cred_green_front;
                cardBackImgRegID = R.drawable.cred_green_back;
            } else {
                dialogo = fabrica_dialogos.dlgError(getContext(), "No se pudo determinar su tipo de tarjeta:\n" + tipoTarjeta + ".\nPor favor avise a la delegación de ANDES sobre este mensaje.");
                return;
            }
            dialogo = new Dialog(getActivity());
            dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogo.setContentView(R.layout.dlg_modal_credencial_provisoria);


            ImageView imvCardFront = (ImageView) dialogo.findViewById(R.id.imvCardFront);
            imvCardFront.setImageResource(cardFrontImgRegID);
            ImageView imvCardBack = (ImageView) dialogo.findViewById(R.id.imvCardBack);
            imvCardBack.setImageResource(cardBackImgRegID);
            TextView tvNombreAfiliado = (TextView) dialogo.findViewById(R.id.tvNombreAfiliado);
            tvNombreAfiliado.setText(afiliado);
            TextView tvNumAfiliado = (TextView) dialogo.findViewById(R.id.tvNumAfiliado);
            tvNumAfiliado.setText(numAfiliado);
            TextView tvFecVenimiento = (TextView) dialogo.findViewById(R.id.tvFecVenimiento);
            tvFecVenimiento.setText(validez);

            LinearLayout btnConfirm = (LinearLayout) dialogo.findViewById(R.id.btnConfimar);
            LinearLayout btnCancel = (LinearLayout) dialogo.findViewById(R.id.btnCancel);

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout bmpArea = (LinearLayout) dialogo.findViewById(R.id.bmpArea);
                    credentialBitmap = loadBitmapFromView(bmpArea);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    credentialBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    dialogo.dismiss();
                    confirmarCredencial();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogo.dismiss();
                }
            });
            dialogo.show();
        } catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error desconocido al armar la credencial provisoria:\n" + ex.getMessage());
        }
    }

    private void confirmarCredencial() {
        try {
            dialogo = fabrica_dialogos.dlgDescargarCompartir(getContext(), "CREDENCIAL VIRTUAL", R.drawable.side_credencia);
            LinearLayout btnDownload = (LinearLayout) dialogo.findViewById(R.id.btnDownload);
            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    descargarImagen();
                }
            });
            LinearLayout btnShare = (LinearLayout) dialogo.findViewById(R.id.btnShare);
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    compartirImagen();
                }
            });
        } catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error desconocido al confirmar la credencial provisoria:\n" + ex.getMessage());
        }
    }

    private void descargarImagen() {
        Bitmap finalBitmap = credentialBitmap;
        String image_name = "Credencial";
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = java.util.UUID.randomUUID().toString() + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgAviso(getContext(), "La credencial se almacenó correctamente");
            LinearLayout lnMsgTotal = (LinearLayout) dialogo.findViewById(R.id.lnMsgTotal);
            lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogo.dismiss();
                    return;
                }
            });
        } catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error al almacenar la imagen");
        }
    }

    private void compartirImagen() {
        File mFile = savebitmap(credentialBitmap);

        Uri u = null;
        u = Uri.fromFile(mFile);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("image/*");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Credencial virtual. ANDES Salud");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Credencial virtual del afiliado");
        emailIntent.putExtra(Intent.EXTRA_STREAM, u);
        startActivity(Intent.createChooser(emailIntent, "Enviar credencial"));
        dialogo.dismiss();
        dialogo = fabrica_dialogos.dlgAviso(getContext(), "La credencial se envió correctamente");
        LinearLayout lnMsgTotal = (LinearLayout) dialogo.findViewById(R.id.lnMsgTotal);
        lnMsgTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogo.dismiss();
                return;
            }
        });
    }

    private File savebitmap(Bitmap bmp) {
        String temp = "credencial";
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, temp + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, temp + ".png");
        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public static Bitmap loadBitmapFromView(View v) {
        //Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Bitmap b = Bitmap.createBitmap(dpToPx(245), dpToPx(328), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    //#endregion

    //#region notificacion
    private void mostrarNotificacion() {
        dialogo = new Dialog(getContext());
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogo.setContentView(R.layout.dlg_modal_det_buzon_notificacion);

        TextView txtTitulo = (TextView) dialogo.findViewById(R.id.txtTitulo);
        TextView txtTexto = (TextView) dialogo.findViewById(R.id.txtTexto);
        WebView webview = (WebView) dialogo.findViewById(R.id.webview);

        SQLController databaseNotificacion = new SQLController(getContext());
        Cursor datDatosNotificacion = databaseNotificacion.leerDetalleEstadoNotificacion(_idActual);

        if (datDatosNotificacion.getCount() > 0) {
            txtTitulo.setText(datDatosNotificacion.getString(3));
            if (datDatosNotificacion.getString(1).equals("TEXTO")) {
                webview.setVisibility(View.GONE);
                txtTexto.setText(datDatosNotificacion.getString(2));
            } else {
                txtTexto.setVisibility(View.GONE);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.loadDataWithBaseURL("", datDatosNotificacion.getString(2), "text/html", "UTF-8", "");
            }
        }
        dialogo.show();
        Window window = dialogo.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        return;
    }
    //#endregion

    //#region mapa
    private void mostrarMapa() {
        //if (dialogoMapa==null) {
        dialogoMapa = new Dialog(getContext());
        dialogoMapa.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogoMapa.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogoMapa.setContentView(R.layout.dlg_modal_mapa);
        dialogoMapa.getWindow().setDimAmount(0.1f);
        mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.dlgMap);
        //final MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.dlgMap);

        ImageView btnCerrarMapa = (ImageView) dialogoMapa.findViewById(R.id.btnCerrarMapa);
        btnCerrarMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapFragment != null) {
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
                idAfiliadoAct = "00000000-0000-0000-0000-000000000000";
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
    //#endregion

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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los datos del mapa:\n" + e.getMessage());
            return;
        }
    }

    @Override
    public void onItemClick(int id, int position) {
        Log.d("ItemId", "" + id);

        fragmento_eventos eventos_frgmnt = new fragmento_eventos();
        FragmentTransaction transaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("idsfromnotification", id);
        eventos_frgmnt.setArguments(bundle);
        transaction.replace(R.id.frg_pantallaActual, eventos_frgmnt);
        transaction.addToBackStack("principal");
        transaction.commit();

    }

    @Override
    public void onItemClickUrl(String url) {
        Log.d("adads", "" + url);
//        medicalPrescriptionDialog( url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);

    }


    public class adap_notificaciones {
        public String id;
        public String codTipo;
        public String descTipo;
        public String afiliado;
        public String fecEmision;

        public adap_notificaciones(String id, String codTipo, String descTipo, String afiliado, String fecEmision) {
            this.id = id;
            this.codTipo = codTipo;
            this.descTipo = descTipo;
            this.afiliado = afiliado;
            this.fecEmision = fecEmision;
            //this.respuesta = respuesta;
        }
/*

        public adap_notificaciones(String descTipo) {

            this.descTipo=descTipo;
            //this.respuesta = respuesta;
        }
*/


    }


    void getAllnotifications(Activity context, int page, int limit) throws JSONException {
        if (page > limit) {
            // checking if the page number is greater than limit.
            // displaying toast message in this case when page>limit.
            Toast.makeText(getContext(), "That's all the data..", Toast.LENGTH_SHORT).show();

            // hiding our progress bar.
            idPBLoading.setVisibility(View.GONE);
            return;
        }

        JSONObject notificationJsonObject = new JSONObject();
//        notificationJsonObject.put("idAfiliado",idAffiliado);
        notificationJsonObject.put("per_page", limit);
        notificationJsonObject.put("page", page);

        String URLs = "https://digitalech.com/doctor-panel/api/".concat("get-user-notifications");
//                "http://discoveritech.org/staff-dashboard/api/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, notificationJsonObject, response -> {
            try {
                idPBLoading.setVisibility(View.GONE);

//                notificationList.clear();
                if (response.getBoolean("status")) {
                    Log.d("datas11", "" + response);

                    JSONObject responseJsonobject = response.getJSONObject("response");
                    Log.d("datas12", "" + responseJsonobject);

                    JSONObject detailJsonobject = responseJsonobject.getJSONObject("detail");
                    Log.d("datas13", "" + detailJsonobject);

                    JSONArray dataJsonArray = detailJsonobject.getJSONArray("data");
                    Log.d("datas14", "" + dataJsonArray);

                    for (int i = 0; i < dataJsonArray.length(); i++) {
                        JSONObject insideDataJsonObject = dataJsonArray.getJSONObject(i);
                        Log.d("datas15", "" + insideDataJsonObject);

                        JSONObject insideNotificationDataJsonobject = insideDataJsonObject.getJSONObject("notification");
/*
                        if (insideDataJsonObject.getBoolean("has_appointment"))
                        {
                            JSONObject eventJsonobject = insideNotificationDataJsonobject.getJSONObject("appointment");

                        }
                        if (insideDataJsonObject.getBoolean("has_medical_prescription"))
                        {
                            JSONObject eventJsonobject = insideNotificationDataJsonobject.getJSONObject("medical_prescription");

                        }*/


                        Log.d("datas16", "" + insideNotificationDataJsonobject);
                        List<Notifications> notificationListfordata = new ArrayList<>();
                        List<MedicialPrescriptionList> medicialPrescriptionLists = new ArrayList<>();
                        List<AppointmentNotificationList> appointmentNotificationLists = new ArrayList<>();
                        List<EventNotificationList> eventNotificationLists = new ArrayList<>();

                        notificationList.add(
                                new NotificationsModel(
                                        insideDataJsonObject.getInt("id"),
                                        insideDataJsonObject.getString("user_id"),
                                        insideDataJsonObject.getInt("notification_id"),
                                        insideDataJsonObject.getString("created_at"),
                                        insideDataJsonObject.getString("updated_at"),
                                        insideDataJsonObject.getBoolean("read"),
                                        notificationListfordata
                                ));
                        notificationListfordata.add(new Notifications(
                                insideNotificationDataJsonobject.getInt("id"),
                                insideNotificationDataJsonobject.getString("text"),
                                insideNotificationDataJsonobject.getInt("event_id"),
                                insideNotificationDataJsonobject.getInt("appointment_id"),
                                insideNotificationDataJsonobject.getInt("medical_prescription_id"),

                                insideNotificationDataJsonobject.getString("created_at"),
                                insideNotificationDataJsonobject.getString("updated_at"),
                                insideNotificationDataJsonobject.getBoolean("has_event"),

                                insideNotificationDataJsonobject.getBoolean("has_appointment"),
                                insideNotificationDataJsonobject.getBoolean("has_medical_prescription"),

                                insideNotificationDataJsonobject.getBoolean("is_clickable"),
                                insideNotificationDataJsonobject.getString("mp_link"),

                                eventNotificationLists,
                                appointmentNotificationLists,
                                medicialPrescriptionLists
                        ));
                        if (!insideDataJsonObject.isNull("event")) {
                            Log.d("has_Ev", "" + insideDataJsonObject.getBoolean("has_event"));


                            JSONObject eventJsonobject = insideNotificationDataJsonobject.getJSONObject("event");
                            eventNotificationLists.add(new EventNotificationList(
                                    eventJsonobject.getInt("id"),
                                    eventJsonobject.getString("text"),
                                    eventJsonobject.getString("event_date"),
                                    eventJsonobject.getString("created_at"),
                                    eventJsonobject.getString("updated_at")


                            ));
                        } else {
                            eventNotificationLists.add(new EventNotificationList(null, null, null, null, null));
                        }

                        if (!insideDataJsonObject.isNull("medical_prescription")) {
                            Log.d("has_med", "" + insideDataJsonObject.getBoolean("has_medical_prescription"));

                            JSONObject medical_prescription = insideNotificationDataJsonobject.getJSONObject("medical_prescription");
                            medicialPrescriptionLists.add(new MedicialPrescriptionList(
                                    medical_prescription.getInt("id"),
                                    medical_prescription.getInt("appointment_id"),
                                    medical_prescription.getString("medical_recipe"),
                                    medical_prescription.getString("medical_diagnosis"),
                                    medical_prescription.getString("created_at"),
                                    medical_prescription.getString("updated_at"),
                                    medical_prescription.getString("mp_link")


                            ));
                        } else {
                            medicialPrescriptionLists.add(new MedicialPrescriptionList(
                                    null, null, null, null, null, null, null
                            ));
                        }
                   /*     insideNotificationDataJsonobject.getBoolean("has_event"),

                                insideNotificationDataJsonobject.getBoolean("has_appointment"),
                                insideNotificationDataJsonobject.getBoolean("has_medical_prescription"),

*/

                            /*  if (insideNotificationDataJsonobject.getBoolean("has_event"))
                              {
                                  eventNotificationLists.add(new EventNotificationList(


                                  ));
                              }
                                Log.d("datas17",""+notificationListfordata.get(0).getText());
*/
                    }
                    notificationAdapter.notifyDataSetChanged();

                }


            } catch (JSONException e) {

                e.printStackTrace();
            }


        }, error -> {
            Log.d("eDASDADS", "" + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                Log.d("tokenvalue", "" + principal.access_token);
                pars.put("Authorization", "Bearer " + principal.access_token);


                return pars;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
//         return notificationList;
    }

    private void medicalPrescriptionDialog(String url) {
        dialogo = new Dialog(getContext());
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogo.setContentView(R.layout.dlg_modal_det_buzon_notificacion);

        TextView txtTitulo = (TextView) dialogo.findViewById(R.id.txtTitulo);
        TextView txtTexto = (TextView) dialogo.findViewById(R.id.txtTexto);
        WebView webview = (WebView) dialogo.findViewById(R.id.webview);


        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url);
        dialogo.show();
        Window window = dialogo.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        return;
    }


}
