package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.funciones;
import ar.com.andessalud.andes.principal;
import ar.com.andessalud.andes.twilio.VideoActivity;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_estado_afiliacion extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo;
    LinearLayout /*loadingData,*/ listaEstado, lnDetalle, lnDatosContactoVer, lnDatosContactoModificar, lnProvincia, listaProvincias, lnLocalidad, listaLocalidades;
    TextView lblEstadoLoading;
    String idAfiliadoActual = "", calle = "", num = "", piso = "", depto = "", provincia = "", localidad = "", _idLocalidad = "", celularCar = "", celularNum = "", mail = "", telFijoCar = "", telFijoNum = "", nombreAfiliado = "", dniAfiliado = "", numAfiliado = "", planPrestacionalAfiliado = "", estadoFiscalizacionAfiliado = "";

    TextView linear1, linear2, linear3, linear4, linear5, linear6;
    ImageView activeInactiveImg;
    public fragmento_estado_afiliacion() {
        // Required empty public constructor
    }

    View lnContacto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragmento_estado_afiliacion, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        linear1 = view.findViewById(R.id.linear1);
        linear2 = view.findViewById(R.id.linear2);
        linear3 = view.findViewById(R.id.linear3);
        linear4 = view.findViewById(R.id.linear4);
        linear5 = view.findViewById(R.id.linear5);
        linear6 = view.findViewById(R.id.linear6);
        activeInactiveImg = view.findViewById(R.id.activeInactiveIcom);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        lblEstadoLoading= (TextView) view.findViewById(R.id.lblEstadoLoading);
//        lblEstadoLoading.setText("Buscando el grupo familiar");
//        loadingData= (LinearLayout) view.findViewById(R.id.loadingData);
//        ImageView imageView = view.findViewById(R.id.imgCargando);
//        Glide.with(this).load(R.drawable.loading).into(imageView);
        listaEstado = (LinearLayout) view.findViewById(R.id.listaEstado);


//        secondGrupoFamiliar();
//        buscarDatosAfiliacion();
//        buscarDatosContactoAfiliado();
//        leerGrupoFamiliar();
        whatsAppRequiredData();
    }

    private void buscarDatosAfiliacion() {
        try {
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            Log.d("userafiliado", idAfiliado);
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliadoTitular", idAfiliado);

            fabrica_WS.APPObtenerEstadoAfiliacion(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    Log.d("successitem", String.valueOf(valores));
                    String[] idAfiliado = new String[valores.getElementsByTagName("idAfiliado").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idAfiliado").getLength(); i++) {
                        idAfiliado[i] = valores.getElementsByTagName("idAfiliado").item(i).getTextContent();
                    }
                    String[] nroAfiliado = new String[valores.getElementsByTagName("nroAfiliado").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nroAfiliado").getLength(); i++) {
                        nroAfiliado[i] = valores.getElementsByTagName("nroAfiliado").item(i).getTextContent();
                    }
                    String[] apellNomb = new String[valores.getElementsByTagName("apellNomb").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("apellNomb").getLength(); i++) {
                        apellNomb[i] = valores.getElementsByTagName("apellNomb").item(i).getTextContent();
                    }
                    String[] CUIL = new String[valores.getElementsByTagName("CUIL").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("CUIL").getLength(); i++) {
                        CUIL[i] = valores.getElementsByTagName("CUIL").item(i).getTextContent();
                    }
                    String[] estadoAfil = new String[valores.getElementsByTagName("estadoAfil").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("estadoAfil").getLength(); i++) {
                        estadoAfil[i] = valores.getElementsByTagName("estadoAfil").item(i).getTextContent();
                    }
                    mostrarEstadoAfiliacion(idAfiliado, nroAfiliado, apellNomb, CUIL, estadoAfil);
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar el grupo familiar:\n" + ex.getMessage());
        }
    }

    private void mostrarEstadoAfiliacion(String[] idAfiliado, String[] nroAfiliado, String[] apellNomb, String[] CUIL, String[] estadoAfil) {
        try {
            for (int i = 0; i < idAfiliado.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnAfiliados = inflater.inflate(R.layout.lista_dos_renglones_tilde, null);

                LinearLayout lnTotal = (LinearLayout) lnAfiliados.findViewById(R.id.lnTotal);
                TextView idFila = (TextView) lnAfiliados.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnAfiliados.findViewById(R.id.linea1);
                TextView linea2 = (TextView) lnAfiliados.findViewById(R.id.linea2);
                ImageView imgEstado = (ImageView) lnAfiliados.findViewById(R.id.imgEstado);
                final ImageView btnExpand = (ImageView) lnAfiliados.findViewById(R.id.btnExpand);

                if (estadoAfil[i].equals("ALTA")) {
                    imgEstado.setImageResource(R.drawable.ic_green_check);
                } else {
                    imgEstado.setImageResource(R.drawable.ic_red_cross);
                }
                idFila.setText(idAfiliado[i]);
                linea1.setText(apellNomb[i]);
                linea2.setText(nroAfiliado[i]);
                btnExpand.setVisibility(View.VISIBLE);

                lnDetalle = (LinearLayout) lnAfiliados.findViewById(R.id.lnDetalle);
                idAfiliadoActual = idAfiliado[i];

                lnTotal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lnDetalle.getChildCount() > 0) {
                            btnExpand.setRotation(90);
                            lnDetalle.removeAllViews();
                            return;
                        }
                        btnExpand.setRotation(270);
                        buscarDatosContactoAfiliado();
                    }
                });

                listaEstado.addView(lnAfiliados);
            }
            listaEstado.setVisibility(View.VISIBLE);
            lblEstadoLoading.setVisibility(View.GONE);
//            loadingData.setVisibility(View.GONE);
        } catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los estados de afiliación:\n" + e.getMessage());
        }
    }

    private void buscarDatosContactoAfiliado() {
        LayoutInflater inflater = this.getLayoutInflater();
        lnContacto = inflater.inflate(R.layout.lista_actualizacion_contacto, null);
        TextView linea1 = (TextView) lnContacto.findViewById(R.id.linea1);
        TextView linea2 = (TextView) lnContacto.findViewById(R.id.linea2);
        TextView linea3 = (TextView) lnContacto.findViewById(R.id.linea3);
        TextView linea4 = (TextView) lnContacto.findViewById(R.id.linea4);
        TextView linea5 = (TextView) lnContacto.findViewById(R.id.linea5);
        TextView linea6 = (TextView) lnContacto.findViewById(R.id.linea6);

        linea1.setText("Nombre: " + principal.arraConsultado[0]);
        linea2.setText("Documento: " + principal.arraConsultado[1]);
        linea3.setText("Nº Afiliado: " + principal.arraConsultado[2]);
        linea4.setText("Parentesco: " + principal.arraConsultado[3]);
        linea5.setText("Provincia: " + principal.arraConsultado[4]);
        linea6.setText("Estado: " + principal.arraConsultado[5]);
        linea1.setVisibility(View.VISIBLE);
        linea2.setVisibility(View.VISIBLE);
        linea3.setVisibility(View.VISIBLE);
        linea4.setVisibility(View.VISIBLE);
        linea5.setVisibility(View.VISIBLE);
        linea6.setVisibility(View.VISIBLE);

        /*linea7.setText("Dirección: "+domicilio);
        linea8.setText("Tel. Fijo: "+telFijoCar+telFijoNum);
        linea9.setText("Celular: "+celularCar+celularNum);*/


        //        try{
//
//
//            SQLController database = new SQLController(getContext());
//            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los datos de contacto");
//
//            Map<String, String> params = new HashMap<String, String>();
//            params.put("idAfiliado", idAfiliadoActual);
//
//            fabrica_WS.APPObtenerDatosDeContacto(getActivity(), params, new SuccessResponseHandler<Document>() {
//                @Override
//                public void onSuccess(Document valores) {
//                    dialogo.dismiss();
//
//                    if (valores.getElementsByTagName("calle").getLength() > 0) {
//                        calle=valores.getElementsByTagName("calle").item(0).getTextContent();
//                    }
//                    if (valores.getElementsByTagName("numero").getLength() > 0) {
//                        num=valores.getElementsByTagName("numero").item(0).getTextContent();
//                    }
//
//                    if (valores.getElementsByTagName("piso").getLength() > 0) {
//                        piso=valores.getElementsByTagName("piso").item(0).getTextContent();
//                    }
//
//                    if (valores.getElementsByTagName("depto").getLength() > 0) {
//                        depto=valores.getElementsByTagName("depto").item(0).getTextContent();
//                    }
//
//                    if (valores.getElementsByTagName("provincia").getLength() > 0) {
//                        provincia=valores.getElementsByTagName("provincia").item(0).getTextContent();
//                    }
//
//                    if (valores.getElementsByTagName("localidad").getLength() > 0) {
//                        localidad=valores.getElementsByTagName("localidad").item(0).getTextContent();
//                        _idLocalidad=valores.getElementsByTagName("idLocalidad").item(0).getTextContent();
//                    }
//
//                    if (valores.getElementsByTagName("caractTel").getLength() > 0) {
//                        celularCar=valores.getElementsByTagName("caractTel").item(0).getTextContent();
//                    }
//
//                    if (valores.getElementsByTagName("numTel").getLength() > 0) {
//                        celularNum=valores.getElementsByTagName("numTel").item(0).getTextContent();
//                    }
//
//                    if (valores.getElementsByTagName("mail").getLength() > 0) {
//                        mail=valores.getElementsByTagName("mail").item(0).getTextContent().replaceAll("\n","").replaceAll(" ","");
//                    }
//
//                    if (valores.getElementsByTagName("caractTelFijo").getLength() > 0) {
//                        telFijoCar=valores.getElementsByTagName("caractTelFijo").item(0).getTextContent();
//                    }
//
//                    if (valores.getElementsByTagName("numTelFijo").getLength() > 0) {
//                        telFijoNum=valores.getElementsByTagName("numTelFijo").item(0).getTextContent();
//                    }
//
//                    if (valores.getElementsByTagName("idLocalidad").getLength() > 0) {
//                        _idLocalidad=valores.getElementsByTagName("idLocalidad").item(0).getTextContent();
//                    }
//
//                    if (valores.getElementsByTagName("nombreAfiliado").getLength() > 0) {
//                        nombreAfiliado=valores.getElementsByTagName("nombreAfiliado").item(0).getTextContent();
//                    }
//
//                    if (valores.getElementsByTagName("dniAfiliado").getLength() > 0) {
//                        dniAfiliado=valores.getElementsByTagName("dniAfiliado").item(0).getTextContent();
//                    }
//                    if (valores.getElementsByTagName("numAfiliado").getLength() > 0) {
//                        numAfiliado=valores.getElementsByTagName("numAfiliado").item(0).getTextContent();
//                    }
//                    if (valores.getElementsByTagName("planPrestacionalAfiliado").getLength() > 0) {
//                        planPrestacionalAfiliado=valores.getElementsByTagName("planPrestacionalAfiliado").item(0).getTextContent();
//                    }
//                    if (valores.getElementsByTagName("estadoFiscalizacionAfiliado").getLength() > 0) {
//                        estadoFiscalizacionAfiliado=valores.getElementsByTagName("estadoFiscalizacionAfiliado").item(0).getTextContent();
//                    }
//                    mostrarDatosContacto();
//                }
//            }, new ErrorResponseHandler() {
//                @Override
//                public void onError(String msg) {
//                    dialogo.dismiss();
//                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
//                }
//            });
//        }catch (Exception ex) {
//            dialogo.dismiss();
//            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las delegaciones para la nueva credencial:\n"+ex.getMessage());
//        }


    }

    private void mostrarDatosContacto() {
        try {
            lnDetalle.removeAllViews();
            LayoutInflater inflater = this.getLayoutInflater();
            lnContacto = inflater.inflate(R.layout.lista_actualizacion_contacto, null);
            TextView linea1 = (TextView) lnContacto.findViewById(R.id.linea1);
            TextView linea2 = (TextView) lnContacto.findViewById(R.id.linea2);
            TextView linea3 = (TextView) lnContacto.findViewById(R.id.linea3);
            TextView linea4 = (TextView) lnContacto.findViewById(R.id.linea4);
            TextView linea5 = (TextView) lnContacto.findViewById(R.id.linea5);
            TextView linea6 = (TextView) lnContacto.findViewById(R.id.linea6);
            TextView linea7 = (TextView) lnContacto.findViewById(R.id.linea7);
            TextView linea8 = (TextView) lnContacto.findViewById(R.id.linea8);
            TextView linea9 = (TextView) lnContacto.findViewById(R.id.linea9);
            lnDatosContactoVer = (LinearLayout) lnContacto.findViewById(R.id.lnDatosContactoVer);
            String domicilio = "Dirección:";
            if (!calle.equals("")) {
                domicilio = domicilio + calle + ",";
            }
            if (!num.equals("")) {
                domicilio = domicilio + num + ",";
            }
            if (!piso.equals("")) {
                domicilio = domicilio + "P:" + piso + ",";
            }
            if (!depto.equals("")) {
                domicilio = domicilio + "Dpto:" + depto + ",";
            }
            if (!localidad.equals("")) {
                domicilio = domicilio + localidad + ",";
            }
            if (!provincia.equals("")) {
                domicilio = domicilio + provincia;
            }
            linea1.setText("Nombre: " + nombreAfiliado);
            linea2.setText("D.N.I.: " + dniAfiliado);
            linea3.setText("Nº Afiliado: " + numAfiliado);
            linea4.setText("Plan Actual: " + planPrestacionalAfiliado);
            linea5.setText("Estado: " + estadoFiscalizacionAfiliado);
            linea6.setText("Email: " + mail);
            linea7.setText("Dirección: " + domicilio);
            linea8.setText("Tel. Fijo: " + telFijoCar + telFijoNum);
            linea9.setText("Celular: " + celularCar + celularNum);
            ImageView btnModificar = (ImageView) lnContacto.findViewById(R.id.btnModificar);
            btnModificar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lnDatosContactoVer.setVisibility(View.GONE);
                    lnDatosContactoModificar.setVisibility(View.VISIBLE);
                }
            });

            lnDatosContactoModificar = (LinearLayout) lnContacto.findViewById(R.id.lnDatosContactoModificar);
            lnDatosContactoModificar.setVisibility(View.GONE);
            TextView txtMail = (TextView) lnContacto.findViewById(R.id.txtMail);
            txtMail.setText(mail);
            TextView txtCelularCaract = (TextView) lnContacto.findViewById(R.id.txtCelularCaract);
            txtCelularCaract.setText(celularCar);
            TextView txtCelularNum = (TextView) lnContacto.findViewById(R.id.txtCelularNum);
            txtCelularNum.setText(celularNum);
            TextView txtFijoCaract = (TextView) lnContacto.findViewById(R.id.txtFijoCaract);
            txtFijoCaract.setText(telFijoCar);
            TextView txtFijoNum = (TextView) lnContacto.findViewById(R.id.txtFijoNum);
            txtFijoNum.setText(telFijoNum);
            TextView txtCalle = (TextView) lnContacto.findViewById(R.id.txtCalle);
            txtCalle.setText(calle);
            TextView txtNum = (TextView) lnContacto.findViewById(R.id.txtNum);
            txtNum.setText(num);
            TextView txtPiso = (TextView) lnContacto.findViewById(R.id.txtPiso);
            txtPiso.setText(piso);
            TextView txtDepto = (TextView) lnContacto.findViewById(R.id.txtDepto);
            txtDepto.setText(depto);
            TextView txtProvincia = (TextView) lnContacto.findViewById(R.id.txtDepto);
            txtProvincia = (TextView) lnContacto.findViewById(R.id.txtProvincia);
            txtProvincia.setText(provincia);
            lnProvincia = (LinearLayout) lnContacto.findViewById(R.id.lnProvincia);
            lnProvincia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buscarProvincia();
                }
            });
            listaProvincias = (LinearLayout) lnContacto.findViewById(R.id.listaProvincias);
            TextView txtLocalidad = (TextView) lnContacto.findViewById(R.id.txtLocalidad);
            txtLocalidad.setText(localidad);
            listaLocalidades = (LinearLayout) lnContacto.findViewById(R.id.listaLocalidades);
            lnLocalidad = (LinearLayout) lnContacto.findViewById(R.id.lnLocalidad);
            lnLocalidad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buscarLocalidad();
                }
            });
            ImageView btnCerrar = (ImageView) lnContacto.findViewById(R.id.btnCerrar);
            btnCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lnDatosContactoVer.setVisibility(View.VISIBLE);
                    lnDatosContactoModificar.setVisibility(View.GONE);
                }
            });
            ImageView btnAceptar = (ImageView) lnContacto.findViewById(R.id.btnAceptar);
            btnAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    almacenarContacto();
                }
            });
            lnDetalle.addView(lnContacto);
        } catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las delegaciones para la nueva credencial:\n" + ex.getMessage());
        }
    }

    private void buscarProvincia() {
        try {
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
        } catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las provincias:\n" + ex.getMessage());
        }
    }

    private void mostrarProvincias(String[] provincias) {
        try {
            listaProvincias.removeAllViews();
            for (int i = 0; i < provincias.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnProvincia = inflater.inflate(R.layout.lista_un_renglon_tilde, null);

                final String nombreProvincia = provincias[i];

                TextView linea1 = (TextView) lnProvincia.findViewById(R.id.linea1);
                linea1.setText(nombreProvincia);

                lnProvincia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView txtProvincia = (TextView) getView().findViewById(R.id.txtProvincia);
                        txtProvincia.setText(nombreProvincia);
                        TextView txtLocalidad = (TextView) getView().findViewById(R.id.txtLocalidad);
                        txtLocalidad.setText("---");
                        provincia = nombreProvincia;
                        listaProvincias.setVisibility(View.GONE);
                    }
                });
                listaProvincias.addView(lnProvincia);
            }
            listaProvincias.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las provincias:\n" + e.getMessage());
        }
    }

    private void buscarLocalidad() {
        try {
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
        } catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las localidades:\n" + ex.getMessage());
        }
    }

    private void mostrarLocalidades(String[] localidades, String[] idLocalidad) {
        try {
            listaLocalidades.removeAllViews();
            for (int i = 0; i < localidades.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnLocalidades = inflater.inflate(R.layout.lista_un_renglon_tilde, null);

                final String nombreLocalidad = localidades[i];
                final String id_Localidad = idLocalidad[i];

                TextView idFila = (TextView) lnLocalidades.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnLocalidades.findViewById(R.id.linea1);
                idFila.setText(id_Localidad);
                linea1.setText(nombreLocalidad);

                lnLocalidades.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView txtLocalidad = (TextView) getView().findViewById(R.id.txtLocalidad);
                        txtLocalidad.setText(nombreLocalidad);
                        _idLocalidad = id_Localidad;
                        listaLocalidades.setVisibility(View.GONE);
                    }
                });
                listaLocalidades.addView(lnLocalidades);
            }
            listaLocalidades.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las localidades:\n" + e.getMessage());
        }
    }

    private void almacenarContacto() {
        try {
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Intentando almacenar los datos de contacto");

            final EditText txtMail = (EditText) getView().findViewById(R.id.txtMail);
            final EditText txtCelularCaract = (EditText) getView().findViewById(R.id.txtCelularCaract);
            final EditText txtCelularNum = (EditText) getView().findViewById(R.id.txtCelularNum);
            final EditText txtCalle = (EditText) getView().findViewById(R.id.txtCalle);
            final EditText txtNum = (EditText) getView().findViewById(R.id.txtNum);
            final EditText txtPiso = (EditText) getView().findViewById(R.id.txtPiso);
            final EditText txtDepto = (EditText) getView().findViewById(R.id.txtDepto);
            final EditText txtFijoCaract = (EditText) getView().findViewById(R.id.txtFijoCaract);
            final EditText txtFijoNum = (EditText) getView().findViewById(R.id.txtFijoNum);

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", idAfiliadoActual);
            params.put("mail", txtMail.getText().toString());
            params.put("celular", txtCelularCaract.getText().toString() + "-" + txtCelularNum.getText().toString());
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
                    LinearLayout lnMsgTotal = (LinearLayout) dialogo.findViewById(R.id.lnMsgTotal);
                    lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogo.dismiss();
                            buscarDatosContactoAfiliado();
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
        } catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al almacenar los datos de contacto:\n" + ex.getMessage());
        }
    }
    //endregion

 /*   private void leerGrupoFamiliar() {
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            fabrica_WS.APPBuscarGrupoFamiliar(getContext(), idAfiliado, new SuccessResponseHandler<String>() {
                @Override
                public void onSuccess(String valores) {
//                    dialogo.dismiss();
                    XmlToJson xmlToJson = new XmlToJson.Builder(valores).build();
                    Log.d("1311", "" + xmlToJson);       }

                 *//*   String[] numAfil = new String[valores.getElementsByTagName("nroAfiliado").getLength()];
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
                    }*//*
//                    mostrarGrupoFam(idAfiliados, grupoFam, numAfil, tipoMensaje, mensaje);

            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                }
            });
        }catch (Exception ex) {
        }
    }
*/

//    private void secondGrupoFamiliar() {
//
//        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando el grupo familiar");
//        new CountDownTimer(8000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            public void onFinish() {
//                Log.d("checkingdata",principal.arraConsultado[0]);
//                Log.d("checkingdata",principal.arraConsultado[1]);
//                Log.d("checkingdata",principal.arraConsultado[2]);
//                Log.d("checkingdata",principal.arraConsultado[3]);
//                Log.d("checkingdata",principal.arraConsultado[4]);
//                Log.d("checkingdata",principal.arraConsultado[5]);
////                Log.d("checkingdata",principal.arraConsultado[0]);
//
//                linear1.setText(MessageFormat.format("Nombre: {0}", principal.arraConsultado[0]));
//                linear2.setText(MessageFormat.format("Documento: {0}", principal.arraConsultado[1]));
//                linear3.setText(MessageFormat.format("Nº Afiliado: {0}", principal.arraConsultado[2]));
//                linear4.setText(MessageFormat.format("Parentesco: {0}", principal.arraConsultado[3]));
//                linear5.setText(MessageFormat.format("Provincia: {0}", principal.arraConsultado[4]));
//                linear6.setText(MessageFormat.format("Estado: {0}", principal.arraConsultado[5]));
//                linear1.setVisibility(View.VISIBLE);
//                linear2.setVisibility(View.VISIBLE);
//                linear3.setVisibility(View.VISIBLE);
//                linear4.setVisibility(View.VISIBLE);
//                linear5.setVisibility(View.VISIBLE);
//                linear6.setVisibility(View.VISIBLE);
//
//                dialogo.dismiss();
//                }
//
//
//        }.start();
//
//
//      /*    try {
//            SQLController database = new SQLController(getContext());
//            final String idAfiliado = database.obtenerIDAfiliado();
//            Log.d("responseHandlerrs21",idAfiliado);
//            if (idAfiliado.equals("")) {
//                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
//                return;
//            }
//
//            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando el grupo familiar");
//
//
//            fabrica_WS.APPBuscarGrupoFamiliar(getActivity(), idAfiliado, new SuccessResponseHandler<String>() {
//                @Override
//                public void onSuccess(String response) {
//                    dialogo.dismiss();
//
//                    XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
////                    Log.d("wwr2", "" + xmlToJson);
//                    JSONObject rootObj = null;
//                    try {
//                        rootObj = Objects.requireNonNull(xmlToJson.toJson()).getJSONObject("Resultado");
//                        Log.d("wewa", String.valueOf(rootObj));
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                            Log.d("responseHandlerrs21", "" + xmlToJson);
//
//                   *//* JSONObject rootObj = xmlToJson.toJson().getJSONObject("root");
//                    JSONObject filaObj = rootObj.getJSONObject("fila");
//*//*//                    mostrarGrupoFam(idAfiliados, grupoFam, numAfil, tipoMensaje, mensaje);
//                }
//            }, new ErrorResponseHandler() {
//                @Override
//                public void onError(String msg) {
//                    dialogo.dismiss();
//                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
//                }
//            });
//        } catch (Exception ex) {
//            dialogo.dismiss();
//            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar el grupo familiar:\n" + ex.getMessage());
//        }
//*/
//    }


    public void whatsAppRequiredData() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = this.getResources().getString(R.string.srvLocProduccion);
        SQLController database = new SQLController(getContext());
        final String idAfiliado = database.obtenerIDAfiliado();

        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando el grupo familiar");

        url = url + "consultarAfiliado";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                        Log.d("wwr2", "" + xmlToJson);
                        JSONObject rootObj = xmlToJson.toJson().getJSONObject("Resultado");
                        Log.d("chakaein Nro-Document", ""+rootObj);

                        Log.d("chakaein ApellNomb", rootObj.getString("apellNomb") + "affiliacion" + "" +
                                rootObj.getString("estadoAfiliacion"));
                        if (rootObj.getString("estadoAfiliacion").equals("AL DIA"))
                        {
                            activeInactiveImg.setVisibility(View.VISIBLE);
//                            activeInactiveImg.setImageResource(R.drawable.round_green_btn);
                            activeInactiveImg.setImageDrawable(getResources().getDrawable(R.drawable.round_green_btn));

                        }
                        else {
                            if (rootObj.getString("estadoAfiliacion").equals("AL DIA"))
                            {
                                activeInactiveImg.setVisibility(View.VISIBLE);
//                                activeInactiveImg.setImageResource(R.drawable.round_red_btn);
                                activeInactiveImg.setImageDrawable(getResources().getDrawable(R.drawable.round_red_btn));

                            }
                        }
                        linear1.setVisibility(View.VISIBLE);
                        linear2.setVisibility(View.VISIBLE);
                        linear3.setVisibility(View.VISIBLE);
                        linear4.setVisibility(View.VISIBLE);
                        linear5.setVisibility(View.VISIBLE);
                        linear6.setVisibility(View.VISIBLE);
                        linear1.setText(MessageFormat.format("Nombre: {0}", rootObj.getString("apellNomb")));
                        linear2.setText(MessageFormat.format("Documento: {0}", rootObj.getString("nroDocumento")));
                        linear3.setText(MessageFormat.format("Nº Afiliado: {0}", rootObj.getString("nroAfiliado")));
                        linear4.setText(MessageFormat.format("Parentesco: {0}", rootObj.getString("parentesco")));
                        linear5.setText(MessageFormat.format("Provincia: {0}", rootObj.getString("provincia")));
                        linear6.setText(MessageFormat.format("Estado: {0}", rootObj.getString("estadoAfiliacion")));

//                        linear3.setText(MessageFormat.format("Nº Afiliado: {0}", principal.arraConsultado[2]));
//                linear4.setText(MessageFormat.format("Parentesco: {0}", principal.arraConsultado[3]));
//                linear5.setText(MessageFormat.format("Provincia: {0}", principal.arraConsultado[4]));
//                linear6.setText(MessageFormat.format("Estado: {0}", principal.arraConsultado[5]));

                        dialogo.dismiss();
                    } catch (Exception errEx) {
//                            errorHandler.onError("Error desconocido al buscar los tipos de internación:\n\t" + errEx.getMessage());
                    }
                },
                error -> {

                }) {

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("usuario", "CHATBOT");
                params.put("password", "DrtEchat%");
                params.put("administradora", "F100376F-33F9-49FD-AFB9-EE53616E7F0C");
                params.put("datosAfiliado", idAfiliado);
                return params;
            }
        };
        req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }


    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
