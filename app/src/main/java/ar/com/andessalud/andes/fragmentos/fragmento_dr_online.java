package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.FragmentManager;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.itextpdf.text.pdf.parser.Line;

import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.funciones;
import ar.com.andessalud.andes.principal;
import ar.com.andessalud.andes.twilio.MySingleton;

import static ar.com.andessalud.andes.splash.myCustomKey;

public class fragmento_dr_online extends Fragment {
    private Dialog dialog;
    FragmentChangeTrigger trigger;
    private Dialog dialogo;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    LinearLayout lnAfiliado, lnAfiliadoResultados, listaAfiliado, lnEspecialidad, lnEspecialidadResultados, lnEspecialidadProfesional, lnEspecialidadProfesionalResultados, lnCuestionario, lstEspecialiadProfesional;
    TextView lblAfiliadoSeleccionado, lblSeleccionarEspecialidadPaso, lblEspecialidadSeleccionado, lblSeleccionarEspecialidad, lblSeleccionarEspecialidadProfesional, lblSeleccionarEspecialidadProfesionalPaso, lblEspecialidadProfesionalSeleccionado;
    String _idEspecialidad = "", _idPagina = "00000000-0000-0000-0000-000000000000", _dtValores = "<root></root>", _muestraAnterior = "NO", _idCuestionarioActual = "00000000-0000-0000-0000-000000000000", _idAfiliado = "", _nombreAfiliado = "", _idConvenioSalaEspera = "";
    boolean especialidadHabilitado = false, especialidadProfesionalHabilitado = false;
    ListView lstEspecialiad;
    ListView lstDoctorShow, lstDoctorschedule;
    ImageView btnAnterior, btnSiguiente;
    List<respuestas> _respuestas = new ArrayList<respuestas>();
    principal pantPrincipal;
    LinearLayout doctorListlayout;
    TextView chooseTiming, doctorstimingstxt;
    Button btn_seetheDoctor;
    int medicalspecialityId  ;
    public fragmento_dr_online() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_dr_online, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pantPrincipal = (principal) getActivity();

        //#region seleccionar afiliado
        lnAfiliado = (LinearLayout) view.findViewById(R.id.lnAfiliado);
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultados = (LinearLayout) view.findViewById(R.id.lnAfiliadoResultados);
        lnAfiliadoResultados.setVisibility(View.GONE);
        lblAfiliadoSeleccionado = (TextView) view.findViewById(R.id.lblAfiliadoSeleccionado);
        listaAfiliado = (LinearLayout) view.findViewById(R.id.listaAfiliado);
        listaAfiliado.setVisibility(View.GONE);
        btn_seetheDoctor = view.findViewById(R.id.seetheDoctorBtn);
        btn_seetheDoctor.setVisibility(View.GONE);
        doctorListlayout = (LinearLayout) view.findViewById(R.id.doctorListlayout);
        doctorListlayout.setVisibility(View.GONE);
        chooseTiming = view.findViewById(R.id.chooseTiming);
        doctorstimingstxt = view.findViewById(R.id.doctorstimingstxt);
        dialog = new Dialog(getContext());

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

      /*  lnEspecialidadProfesional = (LinearLayout) view.findViewById(R.id.lnEspecialidadProfesional);
        lnEspecialidadProfesional.setVisibility(View.GONE);
        lnEspecialidadProfesionalResultados = (LinearLayout) view.findViewById(R.id.lnEspecialidadProfesionalResultados);
        lnEspecialidadProfesionalResultados.setVisibility(View.GONE);
        lblSeleccionarEspecialidadProfesional = (TextView) view.findViewById(R.id.lblSeleccionarEspecialidadProfesional);
        lblSeleccionarEspecialidadProfesional.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblSeleccionarEspecialidadProfesionalPaso = (TextView) view.findViewById(R.id.lblSeleccionarEspecialidadProfesionalPaso);
        lblSeleccionarEspecialidadProfesionalPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblEspecialidadProfesionalSeleccionado = (TextView) view.findViewById(R.id.lblEspecialidadProfesionalSeleccionado);
        lstEspecialiadProfesional = (LinearLayout) view.findViewById(R.id.lstEspecialiadProfesional);
        lstEspecialiadProfesional.setVisibility(View.GONE);*/

//        lnEspecialidadProfesional.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                leerEspecialidadesProfesional();
//            }
//        });

//        lnEspecialidadProfesionalResultados.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                leerEspecialidadesProfesional();
//            }
//        });

        //lnEspecialidadProfesional.setVisibility(View.GONE);
        //lnEspecialidadProfesionalResultados.setVisibility(View.GONE);
        //lstEspecialiadProfesional.setVisibility(View.GONE);

        lnEspecialidad = (LinearLayout) view.findViewById(R.id.lnEspecialidad);
        lnEspecialidad.setVisibility(View.VISIBLE);
        lnEspecialidadResultados = (LinearLayout) view.findViewById(R.id.lnEspecialidadResultados);
        lnEspecialidadResultados.setVisibility(View.GONE);
        lblSeleccionarEspecialidad = (TextView) view.findViewById(R.id.lblSeleccionarEspecialidad);
        lblSeleccionarEspecialidad.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblSeleccionarEspecialidadPaso = (TextView) view.findViewById(R.id.lblSeleccionarEspecialidadPaso);
        lblSeleccionarEspecialidadPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblEspecialidadSeleccionado = (TextView) view.findViewById(R.id.lblEspecialidadSeleccionado);
        lstEspecialiad = view.findViewById(R.id.lstEspecialiad);
        lstEspecialiad.setVisibility(View.GONE);
        lstDoctorShow = view.findViewById(R.id.lstDoctorlist);
//        lstDoctorShow.setVisibility(View.V);
        lstDoctorschedule = view.findViewById(R.id.lstDoctorschedule);
        lstDoctorschedule.setVisibility(View.GONE);

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

//        lnCuestionario = (LinearLayout) view.findViewById(R.id.lnCuestionario);
//        lnCuestionario.setVisibility(View.GONE);
//        btnSiguiente = (ImageView) view.findViewById(R.id.btnSiguiente);
//        btnSiguiente.setVisibility(View.GONE);
//        btnSiguiente.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                botonSiguiente();
//            }
//        });

//        btnAnterior = (ImageView) view.findViewById(R.id.btnAnterior);
//        btnAnterior.setVisibility(View.GONE);
//        btnAnterior.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                botonAnterior();
//            }
//        });
    }

    private void clickAfiliado() {
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick) {
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultados.setVisibility(View.GONE);
        lblAfiliadoSeleccionado.setText("Seleccionar Afiliado");
        listaAfiliado.setVisibility(View.GONE);
        _idAfiliado = "";
        _nombreAfiliado = "";
        deshabilitaEspecialidad();
//        lnCuestionario.setVisibility(View.GONE);
        _idEspecialidad = "";
//        btnSiguiente.setVisibility(View.GONE);
//        btnAnterior.setVisibility(View.GONE);
//        deshabilitaEspecialidadProfesional();
        buscarAfiliado();
    }

    private void clickEspecialidad() {
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick) {
            return;
        }

//        Log.d("access_tokenget", principal.access_token);


        ultimoClick = SystemClock.elapsedRealtime();
        lnEspecialidad.setVisibility(View.VISIBLE);
        lnEspecialidadResultados.setVisibility(View.GONE);
        lstEspecialiad.setVisibility(View.GONE);
//        lnCuestionario.setVisibility(View.GONE);
        _idEspecialidad = "";
//        btnSiguiente.setVisibility(View.GONE);
//        btnAnterior.setVisibility(View.GONE);
//        deshabilitaEspecialidadProfesional();
        btn_seetheDoctor.setVisibility(View.GONE);
        doctorListlayout.setVisibility(View.GONE);
        chooseTiming.setVisibility(View.GONE);
        leerEspecialidades();
    }

    //#region afiliado
    private void buscarAfiliado() {
        try {
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
        } catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar el grupo familiar:\n" + ex.getMessage());
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

                final String idAfiliadoLocal = idAfiliados[i];
                final String grupoFamLocal = grupoFam[i];
                final String numAfilLocal = numAfil[i];
                final String mensajeLocal = mensaje[i];

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
                        if (mensajeLocal.equals("--")) {
                            seleccionarAfiliado(idAfiliadoLocal, grupoFamLocal);
                        } else {
                            dialogo = fabrica_dialogos.dlgError(getContext(), mensajeLocal);
                        }
                    }
                });
                listaAfiliado.addView(lnPrestador);
            }
            listaAfiliado.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar los prestadores:\n" + e.getMessage());
        }
    }

    private void seleccionarAfiliado(String idAfiliado, String apellNomb) {
        _idAfiliado = idAfiliado;
        _nombreAfiliado = apellNomb;
        lblAfiliadoSeleccionado.setText(_nombreAfiliado);
        listaAfiliado.setVisibility(View.GONE);
        habilitaEspecialidad();
    }
    //#endregion

    private void deshabilitaEspecialidad() {
        especialidadHabilitado = false;
        lblSeleccionarEspecialidadPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblSeleccionarEspecialidad.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnEspecialidad.setVisibility(View.VISIBLE);
        lnEspecialidad.setVisibility(View.VISIBLE);
        lnEspecialidadResultados.setVisibility(View.GONE);
        lstEspecialiad.setVisibility(View.GONE);
    }

    private void habilitaEspecialidad() {
        especialidadHabilitado = true;
        lblSeleccionarEspecialidadPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblSeleccionarEspecialidad.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void leerEspecialidades() {
        if (!especialidadHabilitado) {
            return;
        }
        try {
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las especialidades");


            fabrica_WS.AppDrSpeciality(getContext(), new SuccessResponseHandler() {
                        @Override
                        public void onSuccess(Object response) {
                            dialogo.dismiss();
                            try {
                                JSONObject jsonObject = (JSONObject) response;
                                Log.d("countedow", String.valueOf(jsonObject));
                                String[] idEspecialidad = new String[jsonObject.getJSONObject("response").getJSONArray("detail").length()];

                                for (int i = 0; i < idEspecialidad.length; i++) {
                                    idEspecialidad[i] = jsonObject.getJSONObject("response").getJSONArray("detail").getJSONObject(i).getString("title");
                                    Log.d("countedow", idEspecialidad[i]);
                                }
                                String[] nombreEspecialidad = new String[jsonObject.getJSONObject("response").getJSONArray("detail").length()];

                                for (int i = 0; i < nombreEspecialidad.length; i++) {
                                    nombreEspecialidad[i] = jsonObject.getJSONObject("response").getJSONArray("detail").getJSONObject(i).getString("id");
                                    Log.d("countedow", nombreEspecialidad[i]);
                                }
                                mostrarEspecialidades(nombreEspecialidad, idEspecialidad);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    , new ErrorResponseHandler() {
                        @Override
                        public void onError(String msg) {
                            dialogo.dismiss();
                            dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                        }
                    }
            );


            /*fabrica_WS.APPBuscarDrOnlineLeerEspecialidad(getActivity(), idAfiliado, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idEspecialidad = new String[valores.getElementsByTagName("idEspecialidad").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idEspecialidad").getLength(); i++) {
                        idEspecialidad[i] = valores.getElementsByTagName("idEspecialidad").item(i).getTextContent();
                    }
                    String[] nombreEspecialidad = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        nombreEspecialidad[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    mostrarEspecialidades(idEspecialidad, nombreEspecialidad);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });*/
        } catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al las especialidades de Dr. Online:\n" + ex.getMessage());
        }
    }

    private void displayDoctors(String id) {

        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las especialidades");

        fabrica_WS.displayDoctor(getContext(), response -> {
                    dialogo.dismiss();
                    try {
                        JSONObject jsonObject = (JSONObject) response;
                        Log.d("countedow", String.valueOf(jsonObject));
                        String[] doctorsArr = new String[jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("doctors").length()];
                        String[] nombreEspecialidad = new String[jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("doctors").length()];
                        for (int i = 0; i < doctorsArr.length; i++) {
                            Log.d("checkingdata", String.valueOf(i));
                            if (jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("doctors").getJSONObject(i).getString("gender").equals("male")) {
                                doctorsArr[i] = "DR. " + jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("doctors").getJSONObject(i).getString("name") + " " +
                                        jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("doctors").getJSONObject(i).getString("surname");
                            } else if (jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("doctors").getJSONObject(i).getString("gender").equals("female")) {
                                doctorsArr[i] = "DRA. " + jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("doctors").getJSONObject(i).getString("name") + " " +
                                        jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("doctors").getJSONObject(i).getString("surname");

                            }
                            Log.d("checkingdata", doctorsArr[i]);


                            nombreEspecialidad[i] = jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("doctors").getJSONObject(i).getString("id");
                            Log.d("ads", nombreEspecialidad[i]);

//                                for (int i = 0; i <nombreEspecialidad.length; i++) {
//                                }

                            Log.d("ads", doctorsArr[i]);
                        }
                    /*String[] nombreEspecialidad = new String[jsonObject.getJSONObject("response").getJSONArray("detail").length()];

                    for (int i = 0; i <nombreEspecialidad.length; i++) {
                        nombreEspecialidad[i] = jsonObject.getJSONObject("response").getJSONArray("detail").getJSONObject(i).getString("id");
                        Log.d("countedow", nombreEspecialidad[i]);
                    }*/
//                                mostrarEspecialidades(doctorsArr,null);
                        displayDoctorList(nombreEspecialidad, doctorsArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                , new ErrorResponseHandler() {
                    @Override
                    public void onError(String msg) {
                        dialogo.dismiss();
                        dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                    }
                }, id
        );


            /*fabrica_WS.APPBuscarDrOnlineLeerEspecialidad(getActivity(), idAfiliado, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idEspecialidad = new String[valores.getElementsByTagName("idEspecialidad").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idEspecialidad").getLength(); i++) {
                        idEspecialidad[i] = valores.getElementsByTagName("idEspecialidad").item(i).getTextContent();
                    }
                    String[] nombreEspecialidad = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        nombreEspecialidad[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    mostrarEspecialidades(idEspecialidad, nombreEspecialidad);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });*/
       /* }catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al las especialidades de Dr. Online:\n"+ex.getMessage());
        }*/

    }

    private void displayDoctorSchedule(String id) {

        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las especialidades");

        fabrica_WS.displayDoctorSchedule(getContext(), response -> {
                    dialogo.dismiss();
                    try {
                        JSONObject jsonObject = (JSONObject) response;
                        Log.d("countedow", String.valueOf(jsonObject));
                        String[] doctorsArr = new String[jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("schedules").length()];
                        String[] nombreEspecialidad = new String[jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("schedules").length()];
                        for (int i = 0; i < doctorsArr.length; i++) {
                            Log.d("checkingdata", String.valueOf(i));
                            doctorsArr[i] = jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("schedules").getJSONObject(i).getString("day_in_spanish")
                                    .substring(0,1).toUpperCase()+jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("schedules").getJSONObject(i).getString("day_in_spanish").substring(1).toLowerCase()
                                    + ": " + jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("schedules").getJSONObject(i).getString("start_time") + " - "
                                    + jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("schedules").getJSONObject(i).getString("end_time");
                            Log.d("doctorschedule", doctorsArr[i]);

                            nombreEspecialidad[i] = jsonObject.getJSONObject("response").getJSONObject("detail").getJSONArray("schedules").getJSONObject(i).getString("id");
                            Log.d("doctorschedule", nombreEspecialidad[i]);

//                                for (int i = 0; i <nombreEspecialidad.length; i++) {
//                                }

//                            Log.d("doctorschedule", doctorsArr[i]);
                        }
                    /*String[] nombreEspecialidad = new String[jsonObject.getJSONObject("response").getJSONArray("detail").length()];

                    for (int i = 0; i <nombreEspecialidad.length; i++) {
                        nombreEspecialidad[i] = jsonObject.getJSONObject("response").getJSONArray("detail").getJSONObject(i).getString("id");
                        Log.d("countedow", nombreEspecialidad[i]);
                    }*/
//                                mostrarEspecialidades(doctorsArr,null);
                        displayDoctorScheduleList(nombreEspecialidad, doctorsArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                , new ErrorResponseHandler() {
                    @Override
                    public void onError(String msg) {
                        dialogo.dismiss();
                        dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                    }
                }, id
        );


            /*fabrica_WS.APPBuscarDrOnlineLeerEspecialidad(getActivity(), idAfiliado, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idEspecialidad = new String[valores.getElementsByTagName("idEspecialidad").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idEspecialidad").getLength(); i++) {
                        idEspecialidad[i] = valores.getElementsByTagName("idEspecialidad").item(i).getTextContent();
                    }
                    String[] nombreEspecialidad = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        nombreEspecialidad[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    mostrarEspecialidades(idEspecialidad, nombreEspecialidad);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });*/
       /* }catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al las especialidades de Dr. Online:\n"+ex.getMessage());
        }*/

    }

    /* private void displayDoctorScheduleList(String[] idEspecialidad, String[] nombreEspecialidad) {
         try {
             if (idEspecialidad.length >= 1) {
                 ArrayList<HashMap<String, String>> listaAMostrar = new ArrayList<>();
                 HashMap<String, String> map = new HashMap<String, String>();
                 for (int i = 0; i < idEspecialidad.length; i++) {
                     map = new HashMap<String, String>();
                     map.put("idFila", idEspecialidad[i]);
                     map.put("lineaImage2", nombreEspecialidad[i]);
                     listaAMostrar.add(map);
                 }
                 SimpleAdapter adaptador = new SimpleAdapter(getContext(), listaAMostrar, R.layout.lista_doctor_tlide, new String[]{"lineaImage2", "linea1"}, new int[]{R.id.linea1, R.id.doctorAvailimg});
                 lstDoctorShow.setAdapter(adaptador);
                 funciones.setListViewHeightBasedOnChildren(lstDoctorShow, 20);
                 lstDoctorShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                     @Override
                     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                         Log.d("datacge", idEspecialidad[position]);
 //
                         HashMap<String, Object> map = (HashMap<String, Object>) lstDoctorShow.getItemAtPosition(position);
 //                        _idEspecialidad = map.get("idFila").toString();
                         Log.d("datacge",map.get("idFila").toString());


 //                        habilitaEspecialidadProfesional();
                         Log.d("checkingnumber", idEspecialidad[position]);

 //                        displayDoctorSchedule(idEspecialidad[position]);
                     }
                 });
                 lstDoctorShow.setVisibility(View.VISIBLE);
             }
         } catch (Exception e) {
             dialogo.dismiss();
             dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las especialidades de Dr. Online:\n" + e.getMessage());
         }
     }
 */
    private void displayDoctorScheduleList(String[] idEspecialidad, String[] nombreEspecialidad ) {
        try {
         /*  lnEspecialidad.setVisibility(View.GONE);
           lnEspecialidadResultados.setVisibility(View.VISIBLE);
           lblEspecialidadSeleccionado.setText("Seleccionar Especialidad");
*/
            if (idEspecialidad.length > 0) {
                ArrayList<HashMap<String, String>> listaAMostrar = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < idEspecialidad.length; i++) {
                    map = new HashMap<String, String>();
                    map.put("idFila", idEspecialidad[i]);
                    map.put("linea1", nombreEspecialidad[i]);
                    listaAMostrar.add(map);
                }
                Log.d("checkingdatas", String.valueOf(listaAMostrar));
                SimpleAdapter adaptador = new SimpleAdapter(getContext(), listaAMostrar, R.layout.lista_un_renglon_tilde,
                        new String[]{"idFila", "linea1"}, new int[]{R.id.idFila, R.id.linea1});
                lstDoctorschedule.setAdapter(adaptador);
                lstDoctorschedule.setVisibility(View.VISIBLE);
                funciones.setListViewHeightBasedOnChildren(lstEspecialiad, 20);
                lstDoctorschedule.setOnItemClickListener((parent, view, position, id) -> {
                    HashMap<String, Object> map1 = (HashMap<String, Object>) lstDoctorschedule.getItemAtPosition(position);
                  /* _idEspecialidad = map1.get("idFila").toString();
                   lblEspecialidadSeleccionado.setText(map1.get("linea1").toString());*/
                    Log.d("doctorschedule:sched", "" + map1.get("idFila"));
//                   doctorstimingstxt.setVisibility(View.VISIBLE);
//                   lstEspecialiad.setVisibility(View.GONE);
                    try {
                        sendRequestForScheduleBooking(map1.get("idFila").toString(),medicalspecialityId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                   displayDoctors(idEspecialidad[position]);
                });
//               lstEspecialiad.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las especialidades de Dr. Online:\n" + e.getMessage());
        }
    }

    private void displayDoctorList(String[] idEspecialidad, String[] nombreEspecialidad) {
        try {
            if (idEspecialidad.length >= 1) {
                ArrayList<HashMap<String, String>> listaAMostrar = new ArrayList<>();
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < idEspecialidad.length; i++) {
                    map = new HashMap<String, String>();
                    map.put("idFila", idEspecialidad[i]);
                    map.put("lineaImage2", nombreEspecialidad[i]);
                    listaAMostrar.add(map);
                }
                SimpleAdapter adaptador = new SimpleAdapter(getContext(), listaAMostrar, R.layout.lista_doctor_tlide, new String[]{"lineaImage2", "linea1"}, new int[]{R.id.linea1, R.id.doctorAvailimg});
                lstDoctorShow.setAdapter(adaptador);
                funciones.setListViewHeightBasedOnChildren(lstDoctorShow, 20);
                lstDoctorShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


//                        Log.d("datacge", String.valueOf(id));

                        HashMap<String, Object> map = (HashMap<String, Object>) lstDoctorShow.getItemAtPosition(position);
//                        _idEspecialidad = map.get("idFila").toString();
                        Log.d("datacge", map.get("idFila").toString());

                        doctorstimingstxt.setVisibility(View.VISIBLE);
//                        habilitaEspecialidadProfesional();
                        Log.d("checkingnumber", idEspecialidad[position]);

                        displayDoctorSchedule(idEspecialidad[position]);
                    }
                });
                btn_seetheDoctor.setVisibility(View.VISIBLE);
                btn_seetheDoctor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseTiming.setVisibility(View.VISIBLE);
                        doctorListlayout.setVisibility(View.VISIBLE);

                    }
                });
            }
        } catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las especialidades de Dr. Online:\n" + e.getMessage());
        }
    }


    private void mostrarEspecialidades(String[] idEspecialidad, String[] nombreEspecialidad) {
        try {
            lnEspecialidad.setVisibility(View.GONE);
            lnEspecialidadResultados.setVisibility(View.VISIBLE);
            lblEspecialidadSeleccionado.setText("Seleccionar Especialidad");

            if (idEspecialidad.length >= 1) {
                ArrayList<HashMap<String, String>> listaAMostrar = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < idEspecialidad.length; i++) {
                    map = new HashMap<String, String>();
                    map.put("idFila", idEspecialidad[i]);
                    map.put("linea1", nombreEspecialidad[i]);
                    listaAMostrar.add(map);
                }

                SimpleAdapter adaptador = new SimpleAdapter(getContext(), listaAMostrar, R.layout.lista_un_renglon_tilde,
                        new String[]{"idFila", "linea1"}, new int[]{R.id.idFila, R.id.linea1});
                lstEspecialiad.setAdapter(adaptador);
                funciones.setListViewHeightBasedOnChildren(lstEspecialiad, 20);
                lstEspecialiad.setOnItemClickListener((parent, view, position, id) -> {
                    HashMap<String, Object> map1 = (HashMap<String, Object>) lstEspecialiad.getItemAtPosition(position);
                    _idEspecialidad = map1.get("idFila").toString();
                    lblEspecialidadSeleccionado.setText(map1.get("linea1").toString());
                    lstEspecialiad.setVisibility(View.GONE);
                    medicalspecialityId = Integer.parseInt(idEspecialidad[position]);
                    displayDoctors(idEspecialidad[position]);
                });
                lstEspecialiad.setVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las especialidades de Dr. Online:\n" + e.getMessage());
        }
    }


    //#region prestador
//    private void deshabilitaEspecialidadProfesional() {sta
//        especialidadProfesionalHabilitado = false;
//        lnEspecialidadProfesional.setVisibility(View.GONE);
//        lnEspecialidadProfesionalResultados.setVisibility(View.GONE);
//        lstEspecialiadProfesional.setVisibility(View.GONE);
//        lblSeleccionarEspecialidadProfesional.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
//        lblSeleccionarEspecialidadProfesionalPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
//    }
//
//    private void habilitaEspecialidadProfesional() {
//        especialidadProfesionalHabilitado = true;
//        lblSeleccionarEspecialidadProfesional.setTextColor(getResources().getColor(R.color.colorFuente));
//        lblSeleccionarEspecialidadProfesionalPaso.setTextColor(getResources().getColor(R.color.colorFuente));
//        lblEspecialidadProfesionalSeleccionado.setText("Seleccionar Prestador");
//    }
//
//    private void leerEspecialidadesProfesional() {
//        if (!especialidadProfesionalHabilitado) {
//            return;
//        }
//        lnCuestionario.setVisibility(View.GONE);
//        try {
//
//            SQLController database = new SQLController(getContext());
//            final String idAfiliado = database.obtenerIDAfiliado();
//            if (idAfiliado.equals("")) {
//                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
//                return;
//            }
//            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las especialidades");
//
//            Map<String, String> params = new HashMap<String, String>();
//            params.put("idAfiliado", idAfiliado);
//            params.put("idEspecialidad", _idEspecialidad);
//
//
//            fabrica_WS.APPDrOnlineLeerEspecialidadesProfesional(getActivity(), params, new SuccessResponseHandler<Document>() {
//                @Override
//                public void onSuccess(Document valores) {
//                    dialogo.dismiss();
//                    String[] idConvenio = new String[valores.getElementsByTagName("idConvenio").getLength()];
//                    for (int i = 0; i < valores.getElementsByTagName("idConvenio").getLength(); i++) {
//                        idConvenio[i] = valores.getElementsByTagName("idConvenio").item(i).getTextContent();
//                    }
//                    String[] nombreConvenio = new String[valores.getElementsByTagName("nombreConvenio").getLength()];
//                    for (int i = 0; i < valores.getElementsByTagName("nombreConvenio").getLength(); i++) {
//                        nombreConvenio[i] = valores.getElementsByTagName("nombreConvenio").item(i).getTextContent();
//                    }
//                    String[] atendiendo = new String[valores.getElementsByTagName("atendiendo").getLength()];
//                    for (int i = 0; i < valores.getElementsByTagName("atendiendo").getLength(); i++) {
//                        atendiendo[i] = valores.getElementsByTagName("atendiendo").item(i).getTextContent();
//                    }
//                    String[] cantPacientes = new String[valores.getElementsByTagName("cantPacientes").getLength()];
//                    for (int i = 0; i < valores.getElementsByTagName("cantPacientes").getLength(); i++) {
//                        cantPacientes[i] = valores.getElementsByTagName("cantPacientes").item(i).getTextContent();
//                    }
//                    String[] estado = new String[valores.getElementsByTagName("estado").getLength()];
//                    for (int i = 0; i < valores.getElementsByTagName("estado").getLength(); i++) {
//                        estado[i] = valores.getElementsByTagName("estado").item(i).getTextContent();
//                    }
//                    mostrarEspecialidadesProfesionales(idConvenio, nombreConvenio, atendiendo, cantPacientes, estado);
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
//            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al las especialidades de Dr. Online:\n" + ex.getMessage());
//        }
//    }

    private void mostrarEspecialidadesProfesionales(String[] idConvenio, String[] nombreConvenio
            , String[] atendiendo, String[] cantPacientes, String[] estado) {
        try {
            lnEspecialidadProfesional.setVisibility(View.GONE);
            lnEspecialidadProfesionalResultados.setVisibility(View.VISIBLE);
            lblEspecialidadProfesionalSeleccionado.setText("Seleccionar Prestador");
            lstEspecialiadProfesional.removeAllViews();

            for (int i = 0; i < idConvenio.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnPrestador = inflater.inflate(R.layout.lista_dr_online_prestador, null);

                final String idConvenioFin = idConvenio[i];
                final String nombreConvenioFin = nombreConvenio[i];

                TextView idFila = (TextView) lnPrestador.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnPrestador.findViewById(R.id.linea1);
                LinearLayout lnEstado = (LinearLayout) lnPrestador.findViewById(R.id.lnEstado);
                LinearLayout lnPacientesPorAtender = (LinearLayout) lnPrestador.findViewById(R.id.lnPacientesPorAtender);
                TextView txtEstado = (TextView) lnPrestador.findViewById(R.id.txtEstado);
                TextView txtCantPacientes = (TextView) lnPrestador.findViewById(R.id.txtCantPacientes);
                ImageView led = (ImageView) lnPrestador.findViewById(R.id.led);
                ImageView btnVerHorarios = (ImageView) lnPrestador.findViewById(R.id.btnVerHorarios);
                ImageView btnIngresarSala = (ImageView) lnPrestador.findViewById(R.id.btnIngresarSala);

                int tipoLed, muestraIngresoSalaEspera;
                if (atendiendo[i].equals("SI")) {
                    tipoLed = R.drawable.led_verde;
                    btnIngresarSala.setVisibility(View.VISIBLE);
                    if (estado[i].equals("Atendiendo")
                            || estado[i].equals("Médico en camino")) {
                        txtEstado.setText(estado[i]);
                        txtCantPacientes.setText(cantPacientes[i]);
                    } else if (estado[i].equals("Médico no disponible")) {
                        btnIngresarSala.setVisibility(View.GONE);
                        lnPacientesPorAtender.setVisibility(View.GONE);
                        txtEstado.setText(estado[i]);
                        tipoLed = R.drawable.led_rojo;
                    }
                } else {
                    tipoLed = R.drawable.led_rojo;
                    btnIngresarSala.setVisibility(View.GONE);
                    lnEstado.setVisibility(View.GONE);
                }

                idFila.setText(idConvenioFin);
                linea1.setText(nombreConvenioFin);
                led.setImageResource(tipoLed);
                final String _idConvenioFin = idConvenioFin;
                //linea2.setText(numAfilLocal);

                btnVerHorarios.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leerEspecialidadHorarios(idConvenioFin);
                    }
                });

                btnIngresarSala.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lblEspecialidadProfesionalSeleccionado.setText(nombreConvenioFin);
                        lstEspecialiadProfesional.setVisibility(View.GONE);
                        _idPagina = "00000000-0000-0000-0000-000000000000";
                        _dtValores = "<root></root>";
                        _idCuestionarioActual = "";
                        _idConvenioSalaEspera = _idConvenioFin;
                        leerEspecialidadesCuestionario();
                    }
                });
                lstEspecialiadProfesional.addView(lnPrestador);
            }
            lstEspecialiadProfesional.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar los prestadores de Dr. Online:\n" + e.getMessage());
        }
    }

    private void leerEspecialidadHorarios(String idConvenioFin) {
        try {
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            final fragmento_dr_online nose = this;

            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las especialidades");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", idAfiliado);
            params.put("idEspecialidad", _idEspecialidad);
            params.put("idPrestador", idConvenioFin);

            fabrica_WS.APPDrOnlineLeerEspecialidadesProfesionalHorarios(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String nombreConvenio = valores.getElementsByTagName("nombreConvenio").item(0).getTextContent();
                    String[] horario = new String[valores.getElementsByTagName("horario").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("horario").getLength(); i++) {
                        horario[i] = valores.getElementsByTagName("horario").item(i).getTextContent();
                    }
                    dialogo = fabrica_dialogos.dlgHorarioDrOnline(nose, getContext(), nombreConvenio, horario);
                    return;
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al las especialidades de Dr. Online:\n" + ex.getMessage());
        }
    }
    //#endregion

    private void botonSiguiente() {
        for (int i = 0; i < _respuestas.size(); i++) {
            if (_respuestas.get(i).tipoComponente.equals("elecMultipleOpcion")) {
                AppCompatCheckBox chkSeccion = (AppCompatCheckBox) getView().findViewWithTag(_respuestas.get(i).idComponente);
                String valorChk = "";
                if (chkSeccion.isChecked()) {
                    valorChk = "true";
                } else {
                    valorChk = "false";
                }
                for (int j = 0; j < _respuestas.size(); j++) {
                    if (_respuestas.get(j).idComponente.equals(_respuestas.get(i).idComponente)) {
                        _respuestas.get(j).valor = valorChk;
                    }
                }
            } else if (_respuestas.get(i).tipoComponente.equals("elecSimpleOpcion")) {
                RadioGroup radioGroup = (RadioGroup) getView().findViewWithTag(_respuestas.get(i).idSeccion);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                //RadioButton chkSeccion = (RadioButton) getView().findViewWithTag(selectedId);
                RadioButton chkSeccion = (RadioButton) getView().findViewById(selectedId);
                if (chkSeccion != null) {
                    String a = chkSeccion.getTag().toString();
                    for (int j = 0; j < _respuestas.size(); j++) {
                        if (_respuestas.get(j).idComponente.equals(a)) {
                            _respuestas.get(j).valor = "true";
                        }
                    }
                }
            }
        }
        _dtValores = "<root>";
        for (int i = 0; i < _respuestas.size(); i++) {
            _dtValores += "<table>";
            _dtValores += "<Item0>" + _respuestas.get(i).idSeccion + "</Item0>";
            _dtValores += "<Item1>" + _respuestas.get(i).tipoComponente + "</Item1>";
            _dtValores += "<Item2>" + _respuestas.get(i).idComponente + "</Item2>";
            _dtValores += "<Item3>" + _respuestas.get(i).valor + "</Item3>";
            _dtValores += "</table>";
        }
        _dtValores += "</root>";
        leerEspecialidadesCuestionario();
    }

    private void botonAnterior() {

    }

    private void leerEspecialidadesCuestionario() {
        try {
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los datos de la especialidad");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", _idAfiliado);
            params.put("idEspecialidad", _idEspecialidad + _idConvenioSalaEspera);
            params.put("idPagina", _idPagina);
            params.put("idCuestionarioActual", _idCuestionarioActual);
            params.put("dtValores", _dtValores);

            fabrica_WS.APPDrOnlineLeerEspecialidadesCuestionario(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String accionAPP = valores.getElementsByTagName("accionAPP").item(0).getTextContent();
                    _idPagina = valores.getElementsByTagName("idPaginaActual").item(0).getTextContent();
                    _muestraAnterior = valores.getElementsByTagName("muestraAnterior").item(0).getTextContent();
                    _idCuestionarioActual = valores.getElementsByTagName("idCuestionarioActual").item(0).getTextContent();
                    if (accionAPP.equals("muestraPagina")) {
                        String[] idComponenteSeccion = new String[valores.getElementsByTagName("idComponenteSeccion").getLength()];
                        for (int i = 0; i < valores.getElementsByTagName("idComponenteSeccion").getLength(); i++) {
                            idComponenteSeccion[i] = valores.getElementsByTagName("idComponenteSeccion").item(i).getTextContent();
                        }
                        String[] tipoComponenteSeccion = new String[valores.getElementsByTagName("tipoComponenteSeccion").getLength()];
                        for (int i = 0; i < valores.getElementsByTagName("tipoComponenteSeccion").getLength(); i++) {
                            tipoComponenteSeccion[i] = valores.getElementsByTagName("tipoComponenteSeccion").item(i).getTextContent();
                        }
                        String[] idComponenteSeccionValor = new String[valores.getElementsByTagName("idComponenteSeccionValor").getLength()];
                        for (int i = 0; i < valores.getElementsByTagName("idComponenteSeccionValor").getLength(); i++) {
                            idComponenteSeccionValor[i] = valores.getElementsByTagName("idComponenteSeccionValor").item(i).getTextContent();
                        }
                        String[] idValorSeccionValor = new String[valores.getElementsByTagName("idValorSeccionValor").getLength()];
                        for (int i = 0; i < valores.getElementsByTagName("idValorSeccionValor").getLength(); i++) {
                            idValorSeccionValor[i] = valores.getElementsByTagName("idValorSeccionValor").item(i).getTextContent();
                        }
                        String[] tipoValorSeccionValor = new String[valores.getElementsByTagName("tipoValorSeccionValor").getLength()];
                        for (int i = 0; i < valores.getElementsByTagName("tipoValorSeccionValor").getLength(); i++) {
                            tipoValorSeccionValor[i] = valores.getElementsByTagName("tipoValorSeccionValor").item(i).getTextContent();
                        }
                        String[] valorSeccionValor = new String[valores.getElementsByTagName("valorSeccionValor").getLength()];
                        for (int i = 0; i < valores.getElementsByTagName("valorSeccionValor").getLength(); i++) {
                            valorSeccionValor[i] = valores.getElementsByTagName("valorSeccionValor").item(i).getTextContent();
                        }
                        mostrarCuestionario(idComponenteSeccion, tipoComponenteSeccion, idComponenteSeccionValor, idValorSeccionValor
                                , tipoValorSeccionValor, valorSeccionValor);
                    } else if (accionAPP.equals("pasaSalaEspera")) {
                        String[] idDrOnlineSignalRLocal = new String[valores.getElementsByTagName("idPrestadorSignalRPrestador").getLength()];
                        for (int i = 0; i < valores.getElementsByTagName("idPrestadorSignalRPrestador").getLength(); i++) {
                            idDrOnlineSignalRLocal[i] = valores.getElementsByTagName("idPrestadorSignalRPrestador").item(i).getTextContent();
                        }
                        pantPrincipal.idDrOnlineSalaEspera = valores.getElementsByTagName("idSalaEspera").item(0).getTextContent();
                        ;
                        pantPrincipal.idDrOnlineSignalR = idDrOnlineSignalRLocal;
                        trigger.fireChange("drOnline_pasaSalaEspera");
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al las especialidades de Dr. Online:\n" + ex.getMessage());
        }
    }

    private void mostrarCuestionario(String[] idComponenteSeccion, String[] tipoComponenteSeccion, String[] idComponenteSeccionValor
            , String[] idValorSeccionValor, String[] tipoValorSeccionValor, String[] valorSeccionValor) {
//        lnCuestionario = (LinearLayout) getView().findViewById(R.id.lnCuestionario);
//        lnCuestionario.removeAllViews();
//        lnCuestionario.setVisibility(View.VISIBLE);
        _respuestas.clear();
        String _idSeccionLocal = "", _idComponenteLocal = "";

        for (int i = 0; i < idComponenteSeccion.length; i++) {
            LayoutInflater inflater = this.getLayoutInflater();
            final View lnSeccion = inflater.inflate(R.layout.componente_seccion, null);
            TextView lblTituloSeccion = (TextView) lnSeccion.findViewById(R.id.lblTituloSeccion);
            String tipoSeccion = tipoComponenteSeccion[i];//.equals("elecMultiple");
            for (int j = 0; j < idComponenteSeccionValor.length; j++) {
                if (tipoSeccion.equals("elecMultiple")
                        && idComponenteSeccion[i].equals(idComponenteSeccionValor[j])
                        && tipoValorSeccionValor[j].equals("elecMultiple")
                ) {
                    lblTituloSeccion.setText(valorSeccionValor[j]);
                    _idSeccionLocal = idComponenteSeccion[j];
                } else if (tipoSeccion.equals("elecSimple")
                        && idComponenteSeccion[i].equals(idComponenteSeccionValor[j])
                        && tipoValorSeccionValor[j].equals("elecSimple")
                ) {
                    lblTituloSeccion.setText(valorSeccionValor[j]);
                    _idSeccionLocal = idComponenteSeccion[j];
                }
            }
            LinearLayout lnDetalleSeccion = (LinearLayout) lnSeccion.findViewById(R.id.lnDetalleSeccion);
            if (tipoSeccion.equals("elecMultiple")) {
                for (int j = 0; j < idComponenteSeccionValor.length; j++) {
                    if (idComponenteSeccion[i].equals(idComponenteSeccionValor[j])
                            && tipoValorSeccionValor[j].equals("elecMultipleOpcion")
                    ) {
                        LayoutInflater inflaterDet = this.getLayoutInflater();
                        final View lnDetalle = inflater.inflate(R.layout.componente_seccion_check, null);
                        TextView lblChkSeccion = (TextView) lnDetalle.findViewById(R.id.lblChkSeccion);
                        lblChkSeccion.setText(valorSeccionValor[j]);
                        AppCompatCheckBox chkSeccion = (AppCompatCheckBox) lnDetalle.findViewById(R.id.chkSeccion);
                        chkSeccion.setTag(idValorSeccionValor[j]);
                        lnDetalleSeccion.addView(lnDetalle);
                        respuestas _local = new respuestas();
                        _local.idSeccion = _idSeccionLocal;
                        _local.tipoComponente = "elecMultipleOpcion";
                        _local.idComponente = idValorSeccionValor[j];
                        _local.valor = "false";
                        _respuestas.add(_local);
                    }
                }
            } else if (tipoSeccion.equals("elecSimple")) {
                //LayoutInflater inflaterRadioDet = this.getLayoutInflater();
                final View lnRadioGrp = inflater.inflate(R.layout.componente_seccion_radio_grupo, null);
                RadioGroup lnRadioGrpOpc = (RadioGroup) lnRadioGrp.findViewById(R.id.radioGrp);
                lnRadioGrpOpc.setTag(_idSeccionLocal);
                RadioButton button;
                for (int j = 0; j < idComponenteSeccionValor.length; j++) {
                    if (idComponenteSeccion[i].equals(idComponenteSeccionValor[j])
                            && tipoValorSeccionValor[j].equals("elecSimpleOpcion")
                    ) {
                        button = new RadioButton(getContext());
                        button.setText(valorSeccionValor[j]);
                        button.setTag(idValorSeccionValor[j]);
                        lnRadioGrpOpc.addView(button);
                        respuestas _local = new respuestas();
                        _local.idSeccion = _idSeccionLocal;
                        _local.tipoComponente = "elecSimpleOpcion";
                        _local.idComponente = idValorSeccionValor[j];
                        _local.valor = "false";
                        _respuestas.add(_local);
                    }
                }
                lnDetalleSeccion.addView(lnRadioGrpOpc);
            }
            lnCuestionario.addView(lnSeccion);
        }
        btnSiguiente.setVisibility(View.VISIBLE);
        if (_muestraAnterior.equals("SI")) {
            btnAnterior.setVisibility(View.VISIBLE);
        } else {
            btnAnterior.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tiempoMulticlick = Float.parseFloat(getString(R.string.tiempoMulticlick));
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }


    private void sendRequestForScheduleBooking(String id,int medicalspecialityid) throws JSONException {
        dialogo = fabrica_dialogos.dlgBuscandowait(getContext(), "Espere por favor...");
        dialogo.show();

        String url = "https://digitalech.com/doctor-panel/api/store-appointment";
        JSONObject deviceobj = new JSONObject();
        Log.d("statsval", id);
        deviceobj.put("slot_id", Integer.parseInt(id));
        deviceobj.put("medical_specialty_id",medicalspecialityid);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, deviceobj, response -> {

            try {
                Boolean stats = response.getBoolean("status");

                Log.d("statsval", "" + stats);
                if (stats.equals(true)) {
                    Log.d("responseasa", String.valueOf(response));

                    if (response.getJSONObject("response").has("detail"))
                    {
                        Log.d("resonsedetail", ""+response.getJSONObject("response").getJSONObject("detail"));
                        dialogo.dismiss();
                        dialogo = fabrica_dialogos.dlgBuscandowait(getContext(),"Espero favouro");
                        dialogo.show();
                        getwaitingRoomAPI(response.getJSONObject("response").getJSONObject("detail").getInt("appointment_id"));
                    }
                    else {
                        Log.d("resonsedetail", "Else-condition");

                        showSuccessDialog("Éxito", response.getJSONObject("response").getString("message"));

                    }
                }


            } catch (JSONException e) {
                Log.e("catchingJson", "" + e);

                e.printStackTrace();
            }
        }, error -> {

            // As of f605da3 the following should work
            NetworkResponse response = error.networkResponse;
            if (error instanceof ServerError && response != null) {
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    // Now you can use any deserializer to make sense of data
                    JSONObject obj = new JSONObject(res);
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), String.valueOf(obj.getJSONObject("error").getString("message")));
                    Log.d("responsedata", String.valueOf(obj));
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    // returned data is not JSONObject?
                    e2.printStackTrace();
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                pars.put("Authorization", "Bearer " + principal.access_token);

                return pars;
            }

        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void getwaitingRoomAPI(int id) {
        fabrica_WS.getWaitingRoomList(getContext(), response -> {
                    JSONObject jsonObject = (JSONObject) response;
                    Log.d("resonsedetailwr", String.valueOf(jsonObject));

                    fragmento_waitingroom fragmento_waitingroom = new fragmento_waitingroom();
                    Bundle args=new Bundle();
                    String userProfileString=jsonObject.toString();
                    args.putString("userProfileString", userProfileString);
                    fragmento_waitingroom.setArguments(args);
                    dialogo.dismiss();
                    if (getFragmentManager() != null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frg_pantallaActual, fragmento_waitingroom)
                                .commit();
                    }
                }, new ErrorResponseHandler() {
                    @Override
                    public void onError(String msg) {
                        dialogo.dismiss();
                        dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                    }
                }, id


        );

    }


    public void showSuccessDialog(String title, String body) {
        dialog.dismiss();
        dialog.setContentView(R.layout.appointment_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleString, bodyString;
        titleString = dialog.findViewById(R.id.appointment_title);
        bodyString = dialog.findViewById(R.id.appointment_body);
        titleString.setText(title);
        bodyString.setText(body);
        TextView yesBtn, noBtn;
        yesBtn = dialog.findViewById(R.id.appointment_Yesbtn);
        noBtn = dialog.findViewById(R.id.appointment_nobtn);
        yesBtn.setText("OK");
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showCalender(id);
                dialog.dismiss();
                dialogo.dismiss();
            }


        });
        noBtn.setVisibility(View.GONE);
        dialog.show();


    }


}


class respuestas {
    String idSeccion;
    String tipoComponente;
    String idComponente;
    String valor;
}