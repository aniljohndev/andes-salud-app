package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.twilio.twiml.voice.Dial;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.clases.AllDaysDisabledDecorator;
import ar.com.andessalud.andes.clases.DayEnableDecorator;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.fragment_menu_sociales;
import ar.com.andessalud.andes.funciones;
import ar.com.andessalud.andes.twilio.MySingleton;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
//import org.threeten.bp.LocalDate;

public class fragmento_turnos extends Fragment implements View.OnClickListener {
    FragmentChangeTrigger trigger;
    ArrayList<HashMap<String, String>> listaProfesionalesMostrar = new ArrayList<HashMap<String, String>>();
    LinearLayout btnAfiliado, btnEspecialidad, btnCentroMedic, btnTurnos;
    LinearLayout afiliadoResultWrap, especialidadResultWrap, centroMedicResultWrap;

    String _idAfiliado, _nombreAfiliado, _idEspecialidad,
            _nombreEspecialidad, _idCentro, _nombreCentro,
            _domicilioCentro, _idHorario, _idProfesional,
            _idHora, _nombreMedico, _horaTurnoDesde,
            _idTurno, _coseguro, _diaSeleccionado,
            _horaSelecionada;
    String _desde, _hasta, _day;
    public Dialog dialog;
    fragmento_contact_turno contact_turno;
    ImageView btn_confirm_contact;

    public fragmento_turnos() {
        // Required empty public constructor
    }

    Bundle args;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_turnos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAfiliado = (LinearLayout) view.findViewById(R.id.btnAfiliado);
        btnAfiliado.setOnClickListener(this);
        btnEspecialidad = (LinearLayout) view.findViewById(R.id.btnEspecialidad);
        btnEspecialidad.setOnClickListener(this);
        btnCentroMedic = (LinearLayout) view.findViewById(R.id.btnCentroMedico);
        btnCentroMedic.setOnClickListener(this);
        btnTurnos = (LinearLayout) view.findViewById(R.id.btnTurnos);
        btnTurnos.setOnClickListener(this);
        btn_confirm_contact = (ImageView) view.findViewById(R.id.confirm_contct);
        btn_confirm_contact.setOnClickListener(this);
        ;
        afiliadoResultWrap = (LinearLayout) view.findViewById(R.id.afiliadoResultWrap);
        afiliadoResultWrap.setOnClickListener(this);
        especialidadResultWrap = (LinearLayout) view.findViewById(R.id.especialidadResultWrap);
        especialidadResultWrap.setOnClickListener(this);
        centroMedicResultWrap = (LinearLayout) view.findViewById(R.id.centroMedicoResultWrap);
        centroMedicResultWrap.setOnClickListener(this);
        args = new Bundle();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAfiliado:
                loadAfiliadoOptions();
                break;
            case R.id.btnEspecialidad:
                if (_idAfiliado == null) {
                    dialog = fabrica_dialogos.dlgError(getActivity(), "Antes de seleccionar la especialidad debe seleccionar el afiliado");
                    dialog.show();
                    return;
                }
                ;
                leerEspecialidades();
                break;
            case R.id.btnCentroMedico:
                if (_idEspecialidad == null) {
                    dialog = fabrica_dialogos.dlgError(getActivity(), "Antes de seleccionar el centro debe seleccionar la especialidad");
                    dialog.show();
                    return;
                }
                leerCentros();
                break;
//            case R.id.btnTurnos:
//                if (_idCentro == null) {
//                    dialog = fabrica_dialogos.dlgError(getActivity(), "Antes de seleccionar el turno debe seleccionar el centro de atención");
//                    dialog.show();
//                    return;
//                }
//                leerTurnos();
//                break;

            case R.id.confirm_contct:


                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.entrar_derecha, R.anim.salir_izquierda);
                contact_turno = new fragmento_contact_turno();
                transaction.replace(R.id.frg_pantallaActual, contact_turno);
                transaction.addToBackStack(null);
                contact_turno.setArguments(args);

                transaction.commit();
    break;




            case R.id.afiliadoResultWrap:
                _idAfiliado=null;
                _idEspecialidad=null;
                _idCentro=null;
                afiliadoResultWrap.setVisibility(View.GONE);
                btnAfiliado.setVisibility(View.VISIBLE);
                especialidadResultWrap.setVisibility(View.GONE);
                btnEspecialidad.setVisibility(View.VISIBLE);
                centroMedicResultWrap.setVisibility(View.GONE);
                btnCentroMedic.setVisibility(View.VISIBLE);
                loadAfiliadoOptions();
                break;
            case R.id.especialidadResultWrap:
                _idEspecialidad=null;
                _idCentro=null;
                especialidadResultWrap.setVisibility(View.GONE);
                btnEspecialidad.setVisibility(View.VISIBLE);
                centroMedicResultWrap.setVisibility(View.GONE);
                btnCentroMedic.setVisibility(View.VISIBLE);
                leerEspecialidades();
                break;
            case R.id.centroMedicoResultWrap:
                _idCentro=null;
                centroMedicResultWrap.setVisibility(View.GONE);
                btnCentroMedic.setVisibility(View.VISIBLE);
                leerCentros();
                break;
        }
    }

    void loadAfiliadoOptions() {
        try {
            dialog = fabrica_dialogos.dlgBuscando(getContext(), "buscando afiliados");
            dialog.show();

            SQLController database = new SQLController(getActivity());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialog.dismiss();
                dialog = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                dialog.show();
                return;
            }
            Log.d("adadad", idAfiliado);

            fabrica_WS.APPBuscarGrupoFamiliar(getActivity(), idAfiliado, (SuccessResponseHandler<Document>) valores -> {
                dialog.dismiss();
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
//                Log.d("datadaa", String.valueOf(valores));
//                XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
//                Log.d("responseHandlerrssasdm2", "" + xmlToJson);

                mostrarGrupoFam(idAfiliados, grupoFam, numAfil, tipoMensaje, mensaje);
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialog.dismiss();
                    dialog = fabrica_dialogos.dlgError(getContext(), msg);
                    dialog.show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            dialog.dismiss();
            String a = ex.getMessage();
        }
    }

    private void  mostrarGrupoFam(String[] idAfiliados, String[] grupoFam, String[] nroAfiliado, String[] tipoMensaje, String[] mensaje) {
        try {
            if (idAfiliados.length >= 1) {
                ArrayList<HashMap<String, String>> listaAMostrar = new ArrayList<HashMap<String, String>>();
                ArrayList<ItemPickerDialogFragment.Item> pickerItems = new ArrayList<ItemPickerDialogFragment.Item>();

                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < grupoFam.length; i++) {
                    map = new HashMap<String, String>();
                    map.put("idFila", idAfiliados[i]);
                    map.put("oculto1", tipoMensaje[i]);
                    map.put("oculto2", mensaje[i]);
                    map.put("linea1", grupoFam[i]);
                    map.put("linea2", nroAfiliado[i]);
                    listaAMostrar.add(map);

                    //if(tipoMensaje[i].equalsIgnoreCase("00")){
                    Bundle b = new Bundle();
                    b.putString("oculto1", tipoMensaje[i]);
                    b.putString("oculto2", mensaje[i]);
                    b.putString("linea2", nroAfiliado[i]);
                    pickerItems.add(new ItemPickerDialogFragment.Item(grupoFam[i], idAfiliados[i], b));
                    //}
                }
                ItemPickerDialogFragment affiliadoDialog = ItemPickerDialogFragment.newInstance(
                        "Seleccionar Afiliado",
                        pickerItems,
                        -1
                );
                affiliadoDialog.show(getFragmentManager(), "ItemPicker");
//                affiliadoDialog.setDialogListener(this);
                affiliadoDialog.setListener(new ItemPickerDialogFragment.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(ItemPickerDialogFragment fragment, ItemPickerDialogFragment.Item item, int index) {
                        if (!item.getItemExtra().get("oculto2").equals("--")) {
                            _idAfiliado = null;
                            dialog = fabrica_dialogos.dlgError(getContext(), item.getItemExtra().get("oculto2").toString());
                            dialog.show();
                            return;
                        }
                        args.putString("apellidoYNombre", grupoFam[index]);

                        _idAfiliado = item.getStringValue();
                        Log.d("messagevalue", _idAfiliado);
                        args.putString("idAffiliado", _idAfiliado);
                        _nombreAfiliado = item.getTitle();

                        TextView lblAfiliadoSeleccionado = (TextView) afiliadoResultWrap.findViewById(R.id.tvAfiliado);
                        lblAfiliadoSeleccionado.setText(_nombreAfiliado);

                        afiliadoResultWrap.setVisibility(View.VISIBLE);
                        btnAfiliado.setVisibility(View.GONE);
                    }
                });
            } else {
                _idAfiliado = idAfiliados[0];
                _nombreAfiliado = grupoFam[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
            dialog = fabrica_dialogos.dlgError(getContext(), "Error general al buscar el grupo familiar:\n" + e.getMessage());
            dialog.show();
            return;
        }
    }

    void leerEspecialidades() {
        dialog = fabrica_dialogos.dlgBuscando(getContext(), "Buscando especialidades");
        dialog.show();

        SQLController database = new SQLController(getActivity());
        final String idAfiliadoTitular = database.obtenerIDAfiliado();
//        args.putString("idAffiliadoTitular",idAfiliadoTitular);
        if (idAfiliadoTitular.equals("")) {
            dialog.dismiss();
            dialog = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
            dialog.show();
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("idAfiliadoTitular", idAfiliadoTitular);
        params.put("idAfiliado", _idAfiliado);

        fabrica_WS.APPBuscarEspecialidadesTurnos(getActivity(), params, (SuccessResponseHandler<Document>) valores -> {
            dialog.dismiss();
            afiliadoResultWrap.setVisibility(View.VISIBLE);

            String[] idPrestacion = new String[valores.getElementsByTagName("idPrestacion").getLength()];
            for (int i = 0; i < valores.getElementsByTagName("idPrestacion").getLength(); i++) {
                idPrestacion[i] = valores.getElementsByTagName("idPrestacion").item(i).getTextContent();
            }
            String[] prestacion = new String[valores.getElementsByTagName("nombreParaAfiliado").getLength()];
            for (int i = 0; i < valores.getElementsByTagName("nombreParaAfiliado").getLength(); i++) {
                prestacion[i] = valores.getElementsByTagName("nombreParaAfiliado").item(i).getTextContent();
            }


            mostrarPrestaciones(idPrestacion, prestacion);
        }, new ErrorResponseHandler() {

            @Override
            public void onError(String msg) {
                dialog.dismiss();
                dialog = fabrica_dialogos.dlgError(getContext(), msg);
                dialog.show();
            }
        });
    }

    private void mostrarPrestaciones(String[] idPrestacion, String[] prestacion) {
        try {
            if (idPrestacion.length >= 1) {
                ArrayList<HashMap<String, String>> listaAMostrar = new ArrayList<HashMap<String, String>>();
                ArrayList<ItemPickerDialogFragment.Item> pickerItems = new ArrayList<>();

                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < idPrestacion.length; i++) {
                    map = new HashMap<String, String>();
                    map.put("idFila", idPrestacion[i]);
                    map.put("linea1", prestacion[i]);
                    listaAMostrar.add(map);

                    pickerItems.add(new ItemPickerDialogFragment.Item(prestacion[i], idPrestacion[i]));
                }

                ItemPickerDialogFragment mostrarDialog = ItemPickerDialogFragment.newInstance(
                        "Seleccionar Espacialidad",
                        pickerItems,
                        -1
                );
                mostrarDialog.show(getFragmentManager(), "ItemPicker");
                mostrarDialog.setListener(new ItemPickerDialogFragment.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(ItemPickerDialogFragment fragment, ItemPickerDialogFragment.Item item, int index) {
                        args.putString("Especialidad", prestacion[index]);
                        _idEspecialidad = item.getStringValue();
                        _nombreEspecialidad = item.getTitle();
                        args.putString("idAffiliadoTitular", idPrestacion[index]);
//                        args.putString("prestador", prestadors[index]);

                        TextView lblEspecialidadSeleccionado = (TextView) especialidadResultWrap.findViewById(R.id.tvEspecialidad);
                        lblEspecialidadSeleccionado.setText(_nombreEspecialidad);

                        btnEspecialidad.setVisibility(View.GONE);
                        especialidadResultWrap.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                dialog = fabrica_dialogos.dlgError(getContext(), "No hay especialidades para mostrar");
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            dialog = fabrica_dialogos.dlgError(getContext(), "Error general al buscar la especialidad:\n" + e.getMessage());
            dialog.show();
        }
    }

    private void leerCentros() {
        try {
            dialog = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los centros");
            dialog.show();

            SQLController database = new SQLController(getActivity());
            final String idAfiliadoTitular = database.obtenerIDAfiliado();
            if (idAfiliadoTitular.equals("")) {
                dialog.dismiss();
                dialog = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                dialog.show();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliadoTitular", idAfiliadoTitular);
            params.put("idAfiliado", _idAfiliado);
            params.put("idPrestacion", _idEspecialidad);

            fabrica_WS.APPBuscarConsultoriosTurnos(getActivity(), params, new SuccessResponseHandler<Document>() {

                @Override
                public void onSuccess(Document valores) {
                    dialog.dismiss();

                    String[] idPoliconsultorio = new String[valores.getElementsByTagName("idPrestador").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idPrestador").getLength(); i++) {
                        idPoliconsultorio[i] = valores.getElementsByTagName("idPrestador").item(i).getTextContent();
                    }
                    String[] policonsultorio = new String[valores.getElementsByTagName("prestador").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("prestador").getLength(); i++) {
                        policonsultorio[i] = valores.getElementsByTagName("prestador").item(i).getTextContent();
                    }
//                    String[] direccion = new String[valores.getElementsByTagName("direccion").getLength()];
//                    for (int i = 0; i < valores.getElementsByTagName("direccion").getLength(); i++) {
//                        direccion[i] = valores.getElementsByTagName("direccion").item(i).getTextContent();
//                    }
//                    String[] prestador = new String[valores.getElementsByTagName("prestador").getLength()];
//                    for (int i = 0; i < valores.getElementsByTagName("prestador").getLength(); i++) {
//                        prestador[i] = valores.getElementsByTagName("prestador").item(i).getTextContent();
//
//                    }

                    mostrarCentros(idPoliconsultorio, policonsultorio
//                            , direccion
                    );
                }
            }, new ErrorResponseHandler() {

                @Override
                public void onError(String msg) {
                    dialog.dismiss();
                    dialog = fabrica_dialogos.dlgError(getContext(), msg);
                    dialog.show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            dialog.dismiss();
            dialog = fabrica_dialogos.dlgError(getContext(), "Error desconocido al solicitar el centro:\n" + ex.getMessage());
            dialog.show();
        }
    }

    private void mostrarCentros(String[] identificador, String[] nombre
//            , String[] extra
    ) {
        try {
            if (identificador.length >= 1) {
                ArrayList<ItemPickerDialogFragment.Item> pickerItems = new ArrayList<>();
                ArrayList<HashMap<String, String>> listaAMostrar = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < identificador.length; i++) {
                    map = new HashMap<String, String>();
                    map.put("idFila", identificador[i]);
                    map.put("linea1", nombre[i]);
//                    map.put("linea2", extra[i]);
                    listaAMostrar.add(map);

//                    Bundle b = new Bundle();b.putString("linea2", extra[i]);
                    pickerItems.add(new ItemPickerDialogFragment.Item(nombre[i], identificador[i]
//                            , b
                    ));
                }
                ItemPickerDialogFragment centroMedicoDialog = ItemPickerDialogFragment.newInstance(
                        "Seleccionar Centro Medico",
                        pickerItems,
                        -1
                );
                centroMedicoDialog.show(getFragmentManager(), "ItemPicker");
                centroMedicoDialog.setListener(new ItemPickerDialogFragment.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(ItemPickerDialogFragment fragment, ItemPickerDialogFragment.Item item, int index) {
                        _idCentro = item.getStringValue();
                        try {
                            sendCentosToservero(_idCentro);
                            args.putString("espacialidoId",nombre[index]);
                            args.putString("prestadorcval",nombre[index]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("idvalue", _idCentro);
                        _nombreCentro = item.getTitle();
//                        Bundle b =item.getItemExtra();
//                        _domicilioCentro=b.getString("linea2");

                        TextView lblCentroSeleccionado = (TextView) centroMedicResultWrap.findViewById(R.id.tvCentroMedic);
                        lblCentroSeleccionado.setText(_nombreCentro);

                        btnCentroMedic.setVisibility(View.GONE);
                        centroMedicResultWrap.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                dialog = fabrica_dialogos.dlgError(getContext(), "No hay centros para la especialidad indicada");
                dialog.show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            dialog = fabrica_dialogos.dlgError(getContext(), "Error general al buscar el centro:\n" + e.getMessage());
            dialog.show();
        }
    }

    private void leerTurnos() {
        try {
            dialog = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los turnos");
            dialog.show();

            SQLController database = new SQLController(getActivity());
            final String idAfiliadoTitular = database.obtenerIDAfiliado();
            if (idAfiliadoTitular.equals("")) {
                dialog.dismiss();
                dialog = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                dialog.show();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliadoTitular", idAfiliadoTitular);
            params.put("idAfiliado", _idAfiliado);
            params.put("idPrestacion", _idEspecialidad);
            params.put("idPoliconsultorio", _idCentro);

            fabrica_WS.APPBuscarTurnosLibres(getActivity(), params, new SuccessResponseHandler<Document>() {

                @Override
                public void onSuccess(Document valores) {
                    dialog.dismiss();

                    String[] idProfesional = new String[valores.getElementsByTagName("idProfesional").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idProfesional").getLength(); i++) {
                        idProfesional[i] = valores.getElementsByTagName("idProfesional").item(i).getTextContent();
                    }
                    String[] profesional = new String[valores.getElementsByTagName("profesional").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("profesional").getLength(); i++) {
                        profesional[i] = valores.getElementsByTagName("profesional").item(i).getTextContent();
                    }
                    String[] dia = new String[valores.getElementsByTagName("dia").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("dia").getLength(); i++) {
                        dia[i] = valores.getElementsByTagName("dia").item(i).getTextContent();
                    }
                    String[] hora = new String[valores.getElementsByTagName("hora").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("hora").getLength(); i++) {
                        hora[i] = valores.getElementsByTagName("hora").item(i).getTextContent();
                    }
                    String[] idHorario = new String[valores.getElementsByTagName("idHorario").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idHorario").getLength(); i++) {
                        idHorario[i] = valores.getElementsByTagName("idHorario").item(i).getTextContent();
                    }
                    mostrarTurnos(idProfesional, profesional, dia, hora, idHorario);
                }
            }, new ErrorResponseHandler() {

                @Override
                public void onError(String msg) {
                    dialog.dismiss();
                    dialog = fabrica_dialogos.dlgError(getContext(), msg);
                    dialog.show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            dialog.dismiss();
            dialog = fabrica_dialogos.dlgError(getContext(), "Error desconocido al solicitar el centro:\n" + ex.getMessage());
            dialog.show();
        }
    }

    private void mostrarTurnos(String[] idProfesional, String[] profesional, String[] dia, String[] hora, String[] idHorario) {
        try {
            if (idProfesional.length >= 1) {
                ArrayList<ItemPickerDialogFragment.Item> pickerItems = new ArrayList<>();
                ArrayList<ItemPickerDialogFragment.Item> pickerItemsProfMostrar = new ArrayList<>();
                ArrayList<HashMap<String, String>> listaAMostrar = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> map = new HashMap<String, String>();
                HashMap<String, String> verificar = new HashMap<String, String>();
                ArrayList<HashMap<String, String>> listaVerificar = new ArrayList<HashMap<String, String>>();

                listaProfesionalesMostrar = new ArrayList<HashMap<String, String>>();

                for (int i = 0; i < idProfesional.length; i++) {
                    map = new HashMap<String, String>();
                    map.put("idProfesional", idProfesional[i]);
                    map.put("profesional", profesional[i]);
                    map.put("hora", hora[i]);
                    map.put("idHorario", idHorario[i]);
                    map.put("dia", dia[i]);

                    verificar = new HashMap<String, String>();
                    verificar.put("idProfesional", idProfesional[i]);

                    if (!listaVerificar.contains(verificar)) {
                        Bundle b = new Bundle();
                        b.putString("hora", hora[i]);
                        b.putString("idHorario", idHorario[i]);
                        b.putString("dia", dia[i]);
                        pickerItemsProfMostrar.add(new ItemPickerDialogFragment.Item(profesional[i], idProfesional[i], b));
                    }
                    listaVerificar.add(verificar);
                    listaProfesionalesMostrar.add(map);
                }
                ItemPickerDialogFragment provinciasDialog = ItemPickerDialogFragment.newInstance(
                        "Seleccionar Turno",
                        //pickerItems,
                        pickerItemsProfMostrar,
                        -1
                );
                provinciasDialog.show(getFragmentManager(), "ItemPicker");
                provinciasDialog.setListener(new ItemPickerDialogFragment.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(ItemPickerDialogFragment fragment, ItemPickerDialogFragment.Item item, int index) {
                        Bundle b = item.getItemExtra();
                        //_idHorario = b.getString("idHorario");
                        _idProfesional = item.getStringValue();
                        //_idHora = b.getString("hora");
                        _nombreMedico = item.getTitle();
                        //_horaTurnoDesde = b.getString("dia");

                        //btnTurnos.setVisibility(View.GONE);

                        showDatePickerDialog(_idProfesional);
                    }
                });
            } else {
                dialog.dismiss();
                dialog = fabrica_dialogos.dlgError(getContext(), "Por el momento no hay turnos para mostrar");
                dialog.show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
            dialog = fabrica_dialogos.dlgError(getContext(), "Error general al leer los turnos:\n" + e.getMessage());
            dialog.show();
            return;
        }
    }

    void showDatePickerDialog(final String _idProfesional) {
        ArrayList<CalendarDay> enabledDates = new ArrayList<>();
        CalendarDay minDate = CalendarDay.from(2900, 01, 01);
        CalendarDay maxDate = CalendarDay.from(1900, 01, 01);

        for (int i = 0; i < listaProfesionalesMostrar.size(); i++) {
            if (listaProfesionalesMostrar.get(i).containsValue(_idProfesional)) {
                String dia = listaProfesionalesMostrar.get(i).get("hora");
                CalendarDay diaActual = CalendarDay.from(Integer.parseInt(dia.substring(6, 10)), Integer.parseInt(dia.substring(3, 5)), Integer.parseInt(dia.substring(0, 2)));
                enabledDates.add(diaActual);
                if (diaActual.isBefore(minDate)) {
                    minDate = diaActual;
                }
                if (diaActual.isAfter(maxDate)) {
                    maxDate = diaActual;
                }
            }
        }

        dialog = fabrica_dialogos.makeGeneralDialog(getActivity(), R.layout.dlg_calendar_view1);
        MaterialCalendarView cv = dialog.findViewById(R.id.calendarID);
        cv.setEnabled(false);

        cv.state().edit()
                .setMinimumDate(minDate)
                .setMaximumDate(maxDate)
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();

        cv.addDecorator(new AllDaysDisabledDecorator());
        try {
            cv.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
            cv.setDateSelected(minDate, true);
            cv.addDecorator(new DayEnableDecorator(enabledDates));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();

        ImageView imvNext = (ImageView) dialog.findViewById(R.id.confirm);
        imvNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                _diaSeleccionado = ("00" + cv.getSelectedDate().getDay()).substring(("00" + cv.getSelectedDate().getDay()).length() - 2)
                        + "/" + ("00" + cv.getSelectedDate().getMonth()).substring(("00" + cv.getSelectedDate().getMonth()).length() - 2)
                        + "/" + cv.getSelectedDate().getYear();
                showTimeDialog(_diaSeleccionado);
            }
        });
    }

    ArrayAdapter<String> comboAdapter;

    void showTimeDialog(String diaSeleccionado) {
        _diaSeleccionado = diaSeleccionado;
        List<String> listaTurnos = new ArrayList<>();

        for (int i = 0; i < listaProfesionalesMostrar.size(); i++) {
            if (listaProfesionalesMostrar.get(i).containsValue(_idProfesional)
                    && listaProfesionalesMostrar.get(i).get("hora").contains(_diaSeleccionado)) {
                listaTurnos.add(listaProfesionalesMostrar.get(i).get("hora").substring(11, 16));
            }
        }

        dialog = fabrica_dialogos.makeGeneralDialog(getActivity(), R.layout.dlg_calendar_view2);
        Spinner tvDesde = (Spinner) dialog.findViewById(R.id.tvDesde);

        comboAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listaTurnos);
        tvDesde.setAdapter(comboAdapter);
        dialog.show();

        ImageView imvConfirm = (ImageView) dialog.findViewById(R.id.imvConfirm);
        imvConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                _horaSelecionada = tvDesde.getSelectedItem().toString();
                for (int i = 0; i < listaProfesionalesMostrar.size(); i++) {
                    if (listaProfesionalesMostrar.get(i).containsValue(_idProfesional)
                            && listaProfesionalesMostrar.get(i).get("hora").contains(_diaSeleccionado + " " + _horaSelecionada)) {
                        _idHorario = listaProfesionalesMostrar.get(i).get("idHorario");
                        _idHora = listaProfesionalesMostrar.get(i).get("hora");
                    }
                }
                auditarTurno();
                //showTurnosResultDialog();
            }
        });
    }

    private void auditarTurno() {
        try {
            dialog = fabrica_dialogos.dlgBuscando(getContext(), "Evaluando la solicitud");
            dialog.show();

            SQLController database = new SQLController(getActivity());
            final String idAfiliadoTitular = database.obtenerIDAfiliado();
            if (idAfiliadoTitular.equals("")) {
                dialog.dismiss();
                dialog = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                dialog.show();
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliadoTitular", idAfiliadoTitular);
            params.put("idAfiliado", _idAfiliado);
            params.put("idPrestacion", _idEspecialidad);
            params.put("idPoliconsultorio", _idCentro);
            params.put("idHorario", _idHorario);
            params.put("fecha", _idHora);

            fabrica_WS.APPSolicitarTurnoConCoseguro(getActivity(), params, new SuccessResponseHandler<Document>() {

                @Override
                public void onSuccess(Document valores) {
                    dialog.dismiss();
                    String valorDevuelto = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                    String mensaje = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                    String idTurno = valores.getElementsByTagName("idTurno").item(0).getTextContent();
                    String coseguro = valores.getElementsByTagName("coseguro").item(0).getTextContent();
                    showTurnosResultDialog(valorDevuelto, mensaje, idTurno, coseguro);
                    //mostrarAutorizado(mensaje, idTurno, coseguro);
                }
            }, new ErrorResponseHandler() {

                @Override
                public void onError(String msg) {
                    dialog.dismiss();
                    dialog = fabrica_dialogos.dlgError(getContext(), msg);
                    dialog.show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            dialog = fabrica_dialogos.dlgError(getContext(), "Error general al evaluar la solicitud de órden de consulta:\n" + ex.getMessage());
            dialog.show();
        }
    }

    void showTurnosResultDialog(String valorDevuelto, String mensaje, String idTurno, String coseguro) {
        _coseguro = coseguro;
        _idTurno = idTurno;

        dialog = fabrica_dialogos.makeGeneralDialog(getActivity(), R.layout.dlg_turnos_result);
        TextView tvMensaje = (TextView) dialog.findViewById(R.id.tvMensaje);
        TextView tvMensajeReservar = (TextView) dialog.findViewById(R.id.tvMensajeReservar);
        if (valorDevuelto.equals("10")) {
            tvMensaje.setText(mensaje);
        } else {
            tvMensaje.setVisibility(View.GONE);
            tvMensajeReservar.setVisibility(View.GONE);
        }
        final String _valorDevuelto = valorDevuelto;
        TextView tvAfiliado = (TextView) dialog.findViewById(R.id.tvAfiliado);
        tvAfiliado.setText(_nombreAfiliado);
        TextView tvEspecialidad = (TextView) dialog.findViewById(R.id.tvEspecialidad);
        tvEspecialidad.setText(_nombreEspecialidad);
        TextView tvCentroMedico = (TextView) dialog.findViewById(R.id.tvCentroMedico);
        tvCentroMedico.setText(_nombreCentro);
        TextView tvTurno1 = (TextView) dialog.findViewById(R.id.tvTurno1);
        tvTurno1.setText(String.format("%s hs", _horaSelecionada));
        TextView tvTurno2 = (TextView) dialog.findViewById(R.id.tvTurno2);
        tvTurno2.setText(_diaSeleccionado);
        TextView tvTurno3 = (TextView) dialog.findViewById(R.id.tvTurno3);
        tvTurno3.setText(_nombreMedico);
        TextView tvTurno4 = (TextView) dialog.findViewById(R.id.tvTurno4);
        tvTurno4.setText(String.format("Coseguro $%s", _coseguro));

        ImageView imvConfirm = (ImageView) dialog.findViewById(R.id.imvConfirm);
        imvConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                confirmarAutorizado(_valorDevuelto);
            }
        });

        ImageView imvClose = (ImageView) dialog.findViewById(R.id.imvClose);
        imvClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void confirmarAutorizado(String valorDevuelto) {
        try {
            dialog = fabrica_dialogos.dlgBuscando(getContext(), "Confirmando el turno autorizado");
            dialog.show();
            final String _valorDevuelto = valorDevuelto;
            SQLController database = new SQLController(getActivity());
            final String idAfiliadoTitular = database.obtenerIDAfiliado();
            if (idAfiliadoTitular.equals("")) {
                dialog.dismiss();
                dialog = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                dialog.show();
                return;
            }
            final String IMEI = funciones.obtenerIMEI(getActivity());

            Map<String, String> params = new HashMap<String, String>();
            params.put("idTurno", _idTurno);
            params.put("IMEI", IMEI);

            fabrica_WS.APPConfirmarTurnoAUT(getActivity(), params, new SuccessResponseHandler<Document>() {

                @Override
                public void onSuccess(Document valores) {
                    dialog.dismiss();
                    SQLController database = new SQLController(getActivity());
                    String fecEmision = valores.getElementsByTagName("fecEmision").item(0).getTextContent();
                    String estadoOrden = "AUT";
                    if (_valorDevuelto.equals("10")) {
                        estadoOrden = "AUD";
                    }
                    String resultado = database.agregarTurnoAndes(_idTurno, _nombreAfiliado, _nombreEspecialidad
                            , _nombreCentro, _domicilioCentro, _nombreMedico, _horaTurnoDesde, fecEmision, estadoOrden, _coseguro);
                    database.agregarAviso(_idTurno);

                    if (!resultado.equals("")) {
                        dialog = fabrica_dialogos.dlgError(getContext(), resultado);
                        dialog.show();
                    } else {
                        String msgAviso = "El turno se reservo correctamente.\n" +
                                "Verifiquelo en el Buzon de\n" +
                                "Nortificaciones.";
                        //dialog = DialogFactory.makeGeneralDialog(getActivity(), R.layout.dlg_modal_mensaje);
                        dialog = fabrica_dialogos.makeGeneralDialog(getActivity(), R.layout.dlg_modal_ordenes_success);
                        TextView lblAfiliado = (TextView) dialog.findViewById(R.id.lblMsg);
                        lblAfiliado.setText(msgAviso);
                        dialog.show();

                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            public void onDismiss(final DialogInterface dialog) {
                                trigger.fireChange("turno_btnTermino");
                                return;
                            }
                        });
                    }
                }
            }, new ErrorResponseHandler() {

                @Override
                public void onError(String msg) {
                    dialog.dismiss();
                    dialog = fabrica_dialogos.dlgError(getContext(), msg);
                    dialog.show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            dialog = fabrica_dialogos.dlgError(getContext(), "Error desconocido al confirmar el turno:\n" + ex.getMessage());
            dialog.show();
        }
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }

//    public void setDialogListenerData(String data, Dialog dialog) throws Exception {
//        Log.d("hello4",data);
//        dialog.show();
//
////        dialog = fabrica_dialogos.dlgBuscando(,"Buscando especialidades");
//        try {
//            new CountDownTimer(3000, 1000) {
//
//                public void onTick(long millisUntilFinished) {
////                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
//                    //here you can have your logic to set text to edittext
//                }
//
//                public void onFinish() {
////                mTextField.setText("done!");
//                    if(dialog != null)
//                    {
//                        dialog.dismiss();
//
//                    }
//
//                }
//
//            }.start();
//
//
//        }
//        catch (Exception e)
//        {
//            throw new Exception("Value is Invalid");
//        }
//
//    }


    private void sendCentosToservero(String idPrestedo) throws JSONException {
        Log.d("createadata", "" + idPrestedo);
        String URLs = this.getResources().getString(R.string.srv_localdiscoveritech).concat("get-health-providers");

//        String URLs = "https://digitalech.com/staff-dashboard/api/";
//        URLs = URLs + "get-health-providers";
        Log.d("urldata", "" + URLs);
        Map<String, String> params = new HashMap<String, String>();
        params.put("idConvenio",
//                "CC6A0544-C364-4947-B61D-F5EBE56C65AA"
                idPrestedo
        );
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, new JSONObject(params), response -> {
            Log.d("responseData",""+response);
            try {
                boolean stats = response.getBoolean("status");
                if (stats) {
                    Log.d("respnsea", String.valueOf(stats));

                    JSONObject jsonresponse = response.getJSONObject("response");
                    JSONObject detailObj = jsonresponse.getJSONObject("detail");

                    if (
                            idPrestedo
//                            "CC6A0544-C364-4947-B61D-F5EBE56C65AA"
                            .equals(detailObj.getString("idConvenio"))) {

                        Log.d("checking1","yes");
                        detailObj.getString("idConvenio");
                        if (detailObj.getInt("habilitado") != 1) {
                            Log.d("checking2","yes");

                            if (!detailObj.getString("whatsappNumber").equals("")
                                    || !detailObj.getString("callNumber").equals("") ||
                                    !detailObj.getString("website").equals("") ||
                                    !detailObj.getString("address").equals("")) {
//                            args = new Bundle();
                                Log.d("checking3","yes");

                                args.putString("nombreConvenio", detailObj.getString("nombreConvenio"));
                                args.putString("whatsappNumber", detailObj.getString("whatsappNumber"));
                                args.putString("callNumber", detailObj.getString("callNumber"));
                                args.putString("website", detailObj.getString("website"));
                                btn_confirm_contact.setVisibility(View.VISIBLE);


                            }
                            else {
                                dialog = fabrica_dialogos.dlgError(getContext(), "Datos no encontrados");
                                dialog.show();
                            }
                        }

                    }

                }
            } catch (JSONException e) {

                e.printStackTrace();
            }


        }, error -> {
//            dialogo.dismiss();
            Log.d("eDASDADS", "" + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                return pars;
            }


        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }


}
