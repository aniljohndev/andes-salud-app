package ar.com.andessalud.andes.fragmentos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jama.carouselview.CarouselView;
import com.jama.carouselview.CarouselViewListener;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.funciones;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_tramites_incorporar_familiar extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo;
    final Calendar myCalendar = Calendar.getInstance();
    private static final int RESULT_LOAD_IMAGE = 111;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    CarouselView carouselViewDNI, carouselViewConstanciaCUIL, carouselViewTercera
        ,carouselViewCuarta, carouselViewQuinta;
    private LinearLayout lnParentesco, lnParentescoResultado, listaParentesco, lnDatosFam
        ,lnDatosFamResultado, lnDatosFamiliar, lnDatosFamCheck, lnAdjuntarDocumentacion
        ,lnAdjuntarDocumentacionResultado, lnFechaNacimiento, lnCompletoImagenDNI, lnImagenDNIBotontes
        ,lnCompletoImagenConstanciaCUIL, lnImagenConsCUILBotontes, lnFotosDNI, lnFotosConstanciaCUIL
        ,lnCompletoImagenTercera, lnImagenTerceraBotontes, lnFotosTercera, lnCompletoImagenCuarta
        , lnFotosCuarta, lnImagenCuartoBotontes, lnCompletoImagenQuinta, lnImagenQuintoBotontes
        ,lnFotosQuinta, lnBotonesConfirmar;
    private TextView lblParentescoSeleccionado, lblDatosFamPaso, lblDatosFam
            ,lblDatosFamSeleccionado, txtFecNac, lblAjuntarDocumentacionPaso, lblAdjuntarDocumentacion
            ,lblDatosAdjuntarDocSeleccionado, lblImagenTercera, lblImagenCuarta, lblImagenQuinta;
    private ImageView imvFromGalleryDNI, imvTakePhotoDNI, imvFromGalleryConstanciaCUIL
        , imvTakePhotoConstanciaCUIL, imvFromGalleryTercera, imvTakePhotoTercera, imvFromGalleryCuarta
        , imvTakePhotoCuarta, imvFromGalleryQuinta, imvTakePhotoQuinta;
    private EditText txtApellNomb, txtCuil;
    private Spinner spinnerNacionalidad, spinnerEstadoCivil;
    private RadioButton optFemenino, optMasculino, optEstudiaSi, optEstudiaNo;
    private String _idTipoParentesco, _nombreParentesco, _apellNombFam, _fecNacFam, _cuilFam
            , _nacionalidadFam, _sexoFam, _estadoCivilFam, _estudiaFam, nombreImagen, _entornoImagen;
    private boolean datosFamiliarHabilitado=false, adjuntarDocumentacionHabilitado=false;
    private int valCalendar=0;
    private ArrayList<Bitmap> arrayImagenDNI, arrayImagenConstanciaCUIL, arrayImagenTercera
            ,arrayImagenCuarta, arrayImagenQuinta;

    public fragmento_tramites_incorporar_familiar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_tramites_incorporar_familiar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lnParentesco = (LinearLayout) view.findViewById(R.id.lnParentesco);
        lnParentesco.setVisibility(View.VISIBLE);
        lnParentescoResultado = (LinearLayout) view.findViewById(R.id.lnParentescoResultado);
        lnParentescoResultado.setVisibility(View.GONE);
        lblParentescoSeleccionado = (TextView) view.findViewById(R.id.lblParentescoSeleccionado);
        listaParentesco = (LinearLayout) view.findViewById(R.id.listaParentesco);
        listaParentesco.setVisibility(View.GONE);
        lnParentesco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickParentesco();
            }
        });

        lnParentescoResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickParentesco();
            }
        });

        lnDatosFam = (LinearLayout) view.findViewById(R.id.lnDatosFam);
        lnDatosFam.setVisibility(View.VISIBLE);
        lblDatosFamPaso = (TextView) view.findViewById(R.id.lblDatosFamPaso);
        lblDatosFam= (TextView) view.findViewById(R.id.lblDatosFam);
        lnDatosFamResultado = (LinearLayout) view.findViewById(R.id.lnDatosFamResultado);
        lnDatosFamResultado.setVisibility(View.GONE);
        lblDatosFamSeleccionado = (TextView) view.findViewById(R.id.lblDatosFamSeleccionado);
        lnDatosFamiliar = (LinearLayout) view.findViewById(R.id.lnDatosFamiliar);
        lnDatosFamiliar.setVisibility(View.GONE);
        lnDatosFam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDatosFamiliar();
            }
        });

        lnDatosFamResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDatosFamiliar();
            }
        });
        txtApellNomb = (EditText) view.findViewById(R.id.txtApellNomb);
        txtFecNac = (TextView) view.findViewById(R.id.txtFecNac);
        txtCuil= (EditText) view.findViewById(R.id.txtCuil);

        lnFechaNacimiento = (LinearLayout) view.findViewById(R.id.lnFechaNacimiento);
        lnFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        spinnerNacionalidad = (Spinner)  view.findViewById(R.id.spinnerNacionalidad);
        spinnerEstadoCivil = (Spinner)  view.findViewById(R.id.spinnerEstadoCivil);
        optFemenino = (RadioButton)  view.findViewById(R.id.optFemenino);
        optMasculino = (RadioButton)  view.findViewById(R.id.optMasculino);
        optEstudiaSi = (RadioButton)  view.findViewById(R.id.optEstudiaSi);
        optEstudiaNo = (RadioButton)  view.findViewById(R.id.optEstudiaNo);

        lnDatosFamCheck= (LinearLayout) view.findViewById(R.id.lnDatosFamCheck);
        lnDatosFamCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionaFamiliar();
            }
        });

        lnAdjuntarDocumentacion= (LinearLayout) view.findViewById(R.id.lnAdjuntarDocumentacion);
        lnAdjuntarDocumentacion.setVisibility(View.VISIBLE);
        lblAjuntarDocumentacionPaso = (TextView) view.findViewById(R.id.lblAjuntarDocumentacionPaso);
        lblAdjuntarDocumentacion = (TextView) view.findViewById(R.id.lblAdjuntarDocumentacion);
        lnAdjuntarDocumentacionResultado = (LinearLayout) view.findViewById(R.id.lnAdjuntarDocumentacionResultado);
        lnAdjuntarDocumentacionResultado.setVisibility(View.GONE);
        lnAdjuntarDocumentacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDocumentacion();
            }
        });
        lnAdjuntarDocumentacionResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDocumentacion();
            }
        });
        lblDatosAdjuntarDocSeleccionado = (TextView) view.findViewById(R.id.lblDatosAdjuntarDocSeleccionado);

        //region imagenes
        //region dni
        lnCompletoImagenDNI = (LinearLayout) view.findViewById(R.id.lnCompletoImagenDNI);
        lnCompletoImagenDNI.setVisibility(View.GONE);
        lnImagenDNIBotontes = (LinearLayout) view.findViewById(R.id.lnImagenDNIBotontes);
        lnImagenDNIBotontes.setVisibility(View.GONE);
        imvFromGalleryDNI = (ImageView) view.findViewById(R.id.imvFromGalleryDNI);
        imvFromGalleryDNI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _entornoImagen="DNI";
                buscarImagenDeGaleria();
            }
        });
        imvTakePhotoDNI = (ImageView) view.findViewById(R.id.imvTakePhotoDNI);
        imvTakePhotoDNI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _entornoImagen="DNI";
                buscarImagenDeCamara();
            }
        });
        lnFotosDNI = (LinearLayout) view.findViewById(R.id.lnFotosDNI);
        lnFotosDNI.setVisibility(View.GONE);
        carouselViewDNI= (CarouselView) view.findViewById(R.id.carouselViewDNI);
        //endregion

        //region imagen constancia de CUIL
        lnCompletoImagenConstanciaCUIL = (LinearLayout) view.findViewById(R.id.lnCompletoImagenConstanciaCUIL);
        lnCompletoImagenConstanciaCUIL.setVisibility(View.GONE);
        lnImagenConsCUILBotontes = (LinearLayout) view.findViewById(R.id.lnImagenConsCUILBotontes);
        lnImagenConsCUILBotontes.setVisibility(View.GONE);
        imvFromGalleryConstanciaCUIL = (ImageView) view.findViewById(R.id.imvFromGalleryConstanciaCUIL);
        imvFromGalleryConstanciaCUIL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _entornoImagen="CONSCUIL";
                buscarImagenDeGaleria();
            }
        });
        imvTakePhotoConstanciaCUIL = (ImageView) view.findViewById(R.id.imvTakePhotoConstanciaCUIL);
        imvTakePhotoConstanciaCUIL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _entornoImagen="CONSCUIL";
                buscarImagenDeCamara();
            }
        });
        lnFotosConstanciaCUIL = (LinearLayout) view.findViewById(R.id.lnFotosConstanciaCUIL);
        lnFotosConstanciaCUIL.setVisibility(View.GONE);
        carouselViewConstanciaCUIL= (CarouselView) view.findViewById(R.id.carouselViewConstanciaCUIL);
        //endregion

        //region tercera imagen
        lnCompletoImagenTercera = (LinearLayout) view.findViewById(R.id.lnCompletoImagenTercera);
        lnCompletoImagenTercera.setVisibility(View.GONE);
        lblImagenTercera = (TextView) view.findViewById(R.id.lblImagenTercera);
        lnImagenTerceraBotontes = (LinearLayout) view.findViewById(R.id.lnImagenTerceraBotontes);
        lnImagenTerceraBotontes.setVisibility(View.GONE);
        imvFromGalleryTercera = (ImageView) view.findViewById(R.id.imvFromGalleryTercera);
        imvFromGalleryTercera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _entornoImagen="TERCERA";
                buscarImagenDeGaleria();
            }
        });
        imvTakePhotoTercera = (ImageView) view.findViewById(R.id.imvTakePhotoTercera);
        imvTakePhotoTercera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _entornoImagen="TERCERA";
                buscarImagenDeCamara();
            }
        });
        lnFotosTercera = (LinearLayout) view.findViewById(R.id.lnFotosTercera);
        lnFotosTercera.setVisibility(View.GONE);
        carouselViewTercera= (CarouselView) view.findViewById(R.id.carouselViewTercera);
        //endregion

        //region cuarta imagen
        lnCompletoImagenCuarta = (LinearLayout) view.findViewById(R.id.lnCompletoImagenCuarta);
        lnCompletoImagenCuarta.setVisibility(View.GONE);
        lblImagenCuarta = (TextView) view.findViewById(R.id.lblImagenCuarta);
        lnImagenCuartoBotontes = (LinearLayout) view.findViewById(R.id.lnImagenCuartoBotontes);
        lnImagenCuartoBotontes.setVisibility(View.GONE);
        imvFromGalleryCuarta = (ImageView) view.findViewById(R.id.imvFromGalleryCuarta);
        imvFromGalleryCuarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _entornoImagen="CUARTA";
                buscarImagenDeGaleria();
            }
        });
        imvTakePhotoCuarta = (ImageView) view.findViewById(R.id.imvTakePhotoCuarta);
        imvTakePhotoCuarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _entornoImagen="CUARTA";
                buscarImagenDeCamara();
            }
        });
        lnFotosCuarta = (LinearLayout) view.findViewById(R.id.lnFotosCuarta);
        lnFotosCuarta.setVisibility(View.GONE);
        carouselViewCuarta= (CarouselView) view.findViewById(R.id.carouselViewCuarta);
        //endregion

        //region quinta imagen
        lnCompletoImagenQuinta = (LinearLayout) view.findViewById(R.id.lnCompletoImagenQuinta);
        lnCompletoImagenQuinta.setVisibility(View.GONE);
        lblImagenQuinta = (TextView) view.findViewById(R.id.lblImagenQuinta);
        lnImagenQuintoBotontes= (LinearLayout) view.findViewById(R.id.lnImagenQuintoBotontes);
        lnImagenQuintoBotontes.setVisibility(View.GONE);
        imvFromGalleryQuinta = (ImageView) view.findViewById(R.id.imvFromGalleryQuinta);
        imvFromGalleryQuinta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _entornoImagen="QUINTA";
                buscarImagenDeGaleria();
            }
        });
        imvTakePhotoQuinta = (ImageView) view.findViewById(R.id.imvTakePhotoQuinta);
        imvTakePhotoQuinta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _entornoImagen="QUINTA";
                buscarImagenDeCamara();
            }
        });
        lnFotosQuinta = (LinearLayout) view.findViewById(R.id.lnFotosQuinta);
        lnFotosQuinta.setVisibility(View.GONE);
        carouselViewQuinta= (CarouselView) view.findViewById(R.id.carouselViewQuinta);
        //endregion
        //endregion

        lnBotonesConfirmar = (LinearLayout) view.findViewById(R.id.lnBotonesConfirmar);
        lnBotonesConfirmar.setVisibility(View.GONE);
        lnBotonesConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarGF();
            }
        });
    }

    private void clickParentesco(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnParentesco.setVisibility(View.VISIBLE);
        lnParentescoResultado.setVisibility(View.GONE);
        lblParentescoSeleccionado.setText("Seleccionar parentesco");
        listaParentesco.setVisibility(View.GONE);
        _nombreParentesco="";
        deshabilitaDatosFamiliar();
        deshabilitaAdjuntarDocumentacion();
        mostrarParentescos();
    }

    private void clickDatosFamiliar(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        if (!datosFamiliarHabilitado){
            return;
        }
        lnDatosFam.setVisibility(View.GONE);
        lnDatosFamResultado.setVisibility(View.VISIBLE);
        lnDatosFamiliar.setVisibility(View.VISIBLE);
        deshabilitaAdjuntarDocumentacion();
    }

    private void clickDocumentacion(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        mostrarAdjuntarDoc();
    }

    //region parentesco
    private void mostrarParentescos() {
        try {
            lnParentesco.setVisibility(View.GONE);
            lnParentescoResultado.setVisibility(View.VISIBLE);
            lblParentescoSeleccionado.setText("Seleccionar parentesco");
            listaParentesco.removeAllViews();
            SQLController database= new SQLController(getContext());
            Cursor datDatos=database.leerParentescos();
            if (datDatos.getCount()>0) {
                while (datDatos.isAfterLast() == false) {
                    LayoutInflater inflater = this.getLayoutInflater();
                    View lnTipoParentesco = inflater.inflate(R.layout.lista_un_renglon_tilde, null);

                    final String idTipoParentescoLocal=datDatos.getString(0);
                    final String nombreParentescoLocal=datDatos.getString(1);

                    TextView idFila = (TextView) lnTipoParentesco.findViewById(R.id.idFila);
                    TextView linea1 = (TextView) lnTipoParentesco.findViewById(R.id.linea1);

                    idFila.setText(idTipoParentescoLocal);
                    linea1.setText(nombreParentescoLocal);

                    lnTipoParentesco.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            seleccionarParentesco(idTipoParentescoLocal, nombreParentescoLocal);
                        }
                    });
                    listaParentesco.addView(lnTipoParentesco);
                    datDatos.moveToNext();
                }
                listaParentesco.setVisibility(View.VISIBLE);
            }
            else {
                dialogo = fabrica_dialogos.dlgError(getContext(), "No se encontraron parentescos");
                return;
            }
        }catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar los parentescos:\n" + e.getMessage());
            return;
        }
    }

    private void seleccionarParentesco(String idTipoParentescoLocal, String nombreParentescoLocal){
        _idTipoParentesco=idTipoParentescoLocal;
        _nombreParentesco=nombreParentescoLocal;
        lblParentescoSeleccionado.setText(nombreParentescoLocal);
        listaParentesco.setVisibility(View.GONE);
        habilitaDatosFamiliar();
    }
    //endregion

    //region datos familiar
    private void deshabilitaDatosFamiliar(){
        datosFamiliarHabilitado=false;
        lblDatosFamPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblDatosFam.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnDatosFam.setVisibility(View.VISIBLE);
        lnDatosFamResultado.setVisibility(View.GONE);
        lnDatosFamiliar.setVisibility(View.GONE);
    }

    private void habilitaDatosFamiliar(){
        datosFamiliarHabilitado=true;
        lblDatosFamPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblDatosFam.setTextColor(getResources().getColor(R.color.colorFuente));
        llenarSpinnerNacionalidad();
    }

    private void llenarSpinnerNacionalidad(){
        try {
            List<String> spinnerArray =  new ArrayList<String>();
            SQLController database= new SQLController(getContext());
            Cursor datDatos=database.leerNacionalidad();
            if (datDatos.getCount()>0) {
                while (datDatos.isAfterLast() == false) {
                    spinnerArray.add(datDatos.getString(1));
                    datDatos.moveToNext();
                }
            }else{
                dialogo = fabrica_dialogos.dlgError(getContext(), "No se encontraron nacionalidades");
                return;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getContext(), android.R.layout.simple_spinner_item, spinnerArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerNacionalidad.setAdapter(adapter);
        }catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar las nacionalidades:\n" + e.getMessage());
            return;
        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarFecNac();
        }
    };

    private void actualizarFecNac() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtFecNac.setText(sdf.format(myCalendar.getTime()));
    }

    private void seleccionaFamiliar(){
        lblDatosFamSeleccionado.setText(_apellNombFam);
        lnDatosFamiliar.setVisibility(View.GONE);
        habilitaAdjuntarDocumentacion();
        if (txtApellNomb.getText().toString().equals("")){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Faltó indicar el nombre del familiar");
            return;
        }else{
            _apellNombFam=txtApellNomb.getText().toString();
        }
        if (txtFecNac.getText().toString().equals("---")){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Faltó indicar la fecha de nacimiento del familiar");
            return;
        }else{
            _fecNacFam=txtFecNac.getText().toString();
        }
        if (txtCuil.getText().toString().equals("")){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Faltó indicar el cuil del familiar");
            return;
        }else{
            _cuilFam=txtCuil.getText().toString();
            if (_cuilFam.length()!=11){
                dialogo = fabrica_dialogos.dlgError(getContext(), "El CUIL del familiar tiene que tener 11 dígitos");
                return;
            }
        }

        _nacionalidadFam=spinnerNacionalidad.getSelectedItem().toString();
        _sexoFam="";
        if(optFemenino.isChecked())
        {
            _sexoFam="F";
        }
        else if(optMasculino.isChecked())
        {
            _sexoFam="M";
        }
        if (_sexoFam.equals("")){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Faltó indicar el sexo del familiar");
            return;
        }
        _estadoCivilFam=spinnerEstadoCivil.getSelectedItem().toString();
        _estudiaFam="";
        if(optEstudiaSi.isChecked())
        {
            _estudiaFam="Si";
        }
        else if(optEstudiaNo.isChecked())
        {
            _estudiaFam="No";
        }
        if (_estudiaFam.equals("")){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Faltó indicar si el familiar estudia");
            return;
        }
        lblDatosFamSeleccionado.setText(_apellNombFam);
        lnDatosFamiliar.setVisibility(View.GONE);
        habilitaAdjuntarDocumentacion();
    }
    //endregion

    //region documentacion
    private void deshabilitaAdjuntarDocumentacion(){
        adjuntarDocumentacionHabilitado=false;
        lblAjuntarDocumentacionPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblAdjuntarDocumentacion.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnAdjuntarDocumentacion.setVisibility(View.VISIBLE);
        lnAdjuntarDocumentacionResultado.setVisibility(View.GONE);
        lnCompletoImagenDNI.setVisibility(View.GONE);
        lnCompletoImagenConstanciaCUIL.setVisibility(View.GONE);
        lnCompletoImagenTercera.setVisibility(View.GONE);
        lnCompletoImagenCuarta.setVisibility(View.GONE);
        lnCompletoImagenQuinta.setVisibility(View.GONE);
    }

    private void habilitaAdjuntarDocumentacion(){
        adjuntarDocumentacionHabilitado=true;
        lblAjuntarDocumentacionPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblAdjuntarDocumentacion.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void mostrarAdjuntarDoc(){
        if (!adjuntarDocumentacionHabilitado){
            return;
        }
        lnAdjuntarDocumentacion.setVisibility(View.GONE);
        lnAdjuntarDocumentacionResultado.setVisibility(View.VISIBLE);
        lblDatosAdjuntarDocSeleccionado.setText("Imágenes para "+_nombreParentesco);
        lnCompletoImagenDNI.setVisibility(View.VISIBLE);
        lnImagenDNIBotontes.setVisibility(View.VISIBLE);
        lnFotosDNI.setVisibility(View.GONE);
        lnCompletoImagenConstanciaCUIL.setVisibility(View.VISIBLE);
        lnImagenConsCUILBotontes.setVisibility(View.VISIBLE);
        lnFotosDNI.setVisibility(View.GONE);
        lnFotosConstanciaCUIL.setVisibility(View.GONE);
        lnFotosTercera.setVisibility(View.GONE);
        lnCompletoImagenCuarta.setVisibility(View.GONE);
        lnCompletoImagenQuinta.setVisibility(View.GONE);
        arrayImagenDNI= new ArrayList<Bitmap>();
        arrayImagenConstanciaCUIL= new ArrayList<Bitmap>();
        arrayImagenTercera= new ArrayList<Bitmap>();
        arrayImagenCuarta= new ArrayList<Bitmap>();
        arrayImagenQuinta= new ArrayList<Bitmap>();
        if (_nombreParentesco.toUpperCase().equals("CONCUBINO/A")){
            lnCompletoImagenTercera.setVisibility(View.VISIBLE);
            lblImagenTercera.setText("Acta de concubinato");
            lnImagenTerceraBotontes.setVisibility(View.VISIBLE);
        } else if (_nombreParentesco.toUpperCase().equals("CÓNYUGE")){
            lnCompletoImagenTercera.setVisibility(View.VISIBLE);
            lblImagenTercera.setText("Acta de matrimonio");
            lnImagenTerceraBotontes.setVisibility(View.VISIBLE);
        } else if (_nombreParentesco.toUpperCase().equals("HIJO/A")){
            lnCompletoImagenTercera.setVisibility(View.VISIBLE);
            lblImagenTercera.setText("Partida de nacimiento");
            lnImagenTerceraBotontes.setVisibility(View.VISIBLE);
        } else if (_nombreParentesco.toUpperCase().equals("HIJO/A DEL CÓNYUGE O CONCUBINO/A")){
            lnCompletoImagenTercera.setVisibility(View.VISIBLE);
            lblImagenTercera.setText("Partida de nacimiento");
            lnImagenTerceraBotontes.setVisibility(View.VISIBLE);
            lnCompletoImagenCuarta.setVisibility(View.VISIBLE);
            lblImagenCuarta.setText("Acta de matrimonio o concubinato");
            lnImagenCuartoBotontes.setVisibility(View.VISIBLE);
            lnCompletoImagenQuinta.setVisibility(View.VISIBLE);
            lblImagenQuinta.setText("Informacion sumaria");
            lnImagenQuintoBotontes.setVisibility(View.VISIBLE);
        }else if (_nombreParentesco.toUpperCase().equals("NIETO/A")){
            lnCompletoImagenTercera.setVisibility(View.VISIBLE);
            lblImagenTercera.setText("Partida de nacimiento");
            lnImagenTerceraBotontes.setVisibility(View.VISIBLE);
            lnCompletoImagenCuarta.setVisibility(View.VISIBLE);
            lblImagenCuarta.setText("Partida de nacimiento de padre o madre");
            lnImagenCuartoBotontes.setVisibility(View.VISIBLE);
            lnCompletoImagenQuinta.setVisibility(View.VISIBLE);
            lblImagenQuinta.setText("Informacion sumaria");
            lnImagenQuintoBotontes.setVisibility(View.VISIBLE);
        }
    }

    //region imagenes
    private void buscarImagenDeGaleria() {
        nombreImagen = UUID.randomUUID().toString();
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void buscarImagenDeCamara() {
        nombreImagen = UUID.randomUUID().toString();
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
        cameraIntent.putExtra("output", Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator + this.nombreImagen + ".jpg")));
        startActivityForResult(cameraIntent, 112);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            try {
                Uri selectedImage = data.getData();
                InputStream imageStream = getContext().getContentResolver().openInputStream(selectedImage);
                agregarImagen(BitmapFactory.decodeStream(imageStream));
            } catch (Exception errorEx) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al agregar la imágen la galería:\n\t" + errorEx.getMessage());
            }
        } else if (requestCode == 112 && resultCode == -1) {
            try {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + this.nombreImagen + ".jpg");
                agregarImagen(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }catch (Exception errorEx) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al agregar la imágen la cámara:\n\t" + errorEx.getMessage());
            }
        }
    }

    private void agregarImagen(Bitmap imagen){
        if (_entornoImagen.equals("DNI")){
            lnImagenDNIBotontes.setVisibility(View.GONE);
            arrayImagenDNI.add(imagen);
            carouselViewDNI.setSize(arrayImagenDNI.size());
            carouselViewDNI.setResource(R.layout.center_carousel_item);
            carouselViewDNI.setAutoPlay(false);
            carouselViewDNI.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
            carouselViewDNI.setCarouselOffset(OffsetType.CENTER);
            carouselViewDNI.setCarouselViewListener(new CarouselViewListener() {
                @Override
                public void onBindView(View view, int position) {
                    // Example here is setting up a full image carousel
                    ImageView imageView = view.findViewById(R.id.imageView);
                    Glide.with(getContext()).load(arrayImagenDNI.get(position)).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eliminarFoto("DNI");
                        }
                    });
                }
            });
            try {
                carouselViewDNI.show();
            }catch (Exception ex){
                String a="";
            }
            lnFotosDNI.setVisibility(View.VISIBLE);
        } else if (_entornoImagen.equals("CONSCUIL")){
            lnImagenConsCUILBotontes.setVisibility(View.GONE);
            arrayImagenConstanciaCUIL.add(imagen);
            carouselViewConstanciaCUIL.setSize(arrayImagenConstanciaCUIL.size());
            carouselViewConstanciaCUIL.setResource(R.layout.center_carousel_item);
            carouselViewConstanciaCUIL.setAutoPlay(false);
            carouselViewConstanciaCUIL.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
            carouselViewConstanciaCUIL.setCarouselOffset(OffsetType.CENTER);
            carouselViewConstanciaCUIL.setCarouselViewListener(new CarouselViewListener() {
                @Override
                public void onBindView(View view, int position) {
                    // Example here is setting up a full image carousel
                    ImageView imageView = view.findViewById(R.id.imageView);
                    Glide.with(getContext()).load(arrayImagenConstanciaCUIL.get(position)).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eliminarFoto("CONSCUIL");
                        }
                    });
                }
            });
            try {
                carouselViewConstanciaCUIL.show();
            }catch (Exception ex){
                String a="";
            }
            lnFotosConstanciaCUIL.setVisibility(View.VISIBLE);
        }else if (_entornoImagen.equals("TERCERA")){
            lnImagenTerceraBotontes.setVisibility(View.GONE);
            arrayImagenTercera.add(imagen);
            carouselViewTercera.setSize(arrayImagenTercera.size());
            carouselViewTercera.setResource(R.layout.center_carousel_item);
            carouselViewTercera.setAutoPlay(false);
            carouselViewTercera.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
            carouselViewTercera.setCarouselOffset(OffsetType.CENTER);
            carouselViewTercera.setCarouselViewListener(new CarouselViewListener() {
                @Override
                public void onBindView(View view, int position) {
                    // Example here is setting up a full image carousel
                    ImageView imageView = view.findViewById(R.id.imageView);
                    Glide.with(getContext()).load(arrayImagenTercera.get(position)).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eliminarFoto("TERCERA");
                        }
                    });
                }
            });
            try {
                carouselViewTercera.show();
            }catch (Exception ex){
                String a="";
            }
            lnFotosTercera.setVisibility(View.VISIBLE);
        } else if (_entornoImagen.equals("CUARTA")){
            lnImagenCuartoBotontes.setVisibility(View.GONE);
            arrayImagenCuarta.add(imagen);
            carouselViewCuarta.setSize(arrayImagenCuarta.size());
            carouselViewCuarta.setResource(R.layout.center_carousel_item);
            carouselViewCuarta.setAutoPlay(false);
            carouselViewCuarta.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
            carouselViewCuarta.setCarouselOffset(OffsetType.CENTER);
            carouselViewCuarta.setCarouselViewListener(new CarouselViewListener() {
                @Override
                public void onBindView(View view, int position) {
                    // Example here is setting up a full image carousel
                    ImageView imageView = view.findViewById(R.id.imageView);
                    Glide.with(getContext()).load(arrayImagenCuarta.get(position)).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eliminarFoto("CUARTA");
                        }
                    });
                }
            });
            try {
                carouselViewCuarta.show();
            }catch (Exception ex){
                String a="";
            }
            lnFotosCuarta.setVisibility(View.VISIBLE);
        }else if (_entornoImagen.equals("quinta")){
            lnImagenQuintoBotontes.setVisibility(View.GONE);
            arrayImagenQuinta.add(imagen);
            carouselViewQuinta.setSize(arrayImagenQuinta.size());
            carouselViewQuinta.setResource(R.layout.center_carousel_item);
            carouselViewQuinta.setAutoPlay(false);
            carouselViewQuinta.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
            carouselViewQuinta.setCarouselOffset(OffsetType.CENTER);
            carouselViewQuinta.setCarouselViewListener(new CarouselViewListener() {
                @Override
                public void onBindView(View view, int position) {
                    // Example here is setting up a full image carousel
                    ImageView imageView = view.findViewById(R.id.imageView);
                    Glide.with(getContext()).load(arrayImagenQuinta.get(position)).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eliminarFoto("CUARTA");
                        }
                    });
                }
            });
            try {
                carouselViewQuinta.show();
            }catch (Exception ex){
                String a="";
            }
            lnFotosQuinta.setVisibility(View.VISIBLE);
        }

        if (_nombreParentesco.toUpperCase().equals("CONCUBINO/A")
                && arrayImagenDNI.size()>0
                && arrayImagenConstanciaCUIL.size()>0
                && arrayImagenTercera.size()>0){
            lnBotonesConfirmar.setVisibility(View.VISIBLE);
        } else if (_nombreParentesco.toUpperCase().equals("CÓNYUGE")
                && arrayImagenDNI.size()>0
                && arrayImagenConstanciaCUIL.size()>0
                && arrayImagenTercera.size()>0){
            lnBotonesConfirmar.setVisibility(View.VISIBLE);
        } else if (_nombreParentesco.toUpperCase().equals("HIJO/A")&& arrayImagenDNI.size()>0
                && arrayImagenConstanciaCUIL.size()>0
                && arrayImagenTercera.size()>0){
            lnBotonesConfirmar.setVisibility(View.VISIBLE);
        } else if (_nombreParentesco.toUpperCase().equals("HIJO/A DEL CÓNYUGE O CONCUBINO/A")
                && arrayImagenDNI.size()>0
                && arrayImagenConstanciaCUIL.size()>0
                && arrayImagenTercera.size()>0
                && arrayImagenCuarta.size()>0
                && arrayImagenQuinta.size()>0){
            lnBotonesConfirmar.setVisibility(View.VISIBLE);
        }else if (_nombreParentesco.toUpperCase().equals("NIETO/A") && arrayImagenDNI.size()>0
                && arrayImagenConstanciaCUIL.size()>0
                && arrayImagenTercera.size()>0
                && arrayImagenCuarta.size()>0
                && arrayImagenQuinta.size()>0){
            lnBotonesConfirmar.setVisibility(View.VISIBLE);
        }
    }

    private void eliminarFoto(String entorno){
        if (_entornoImagen.equals("DNI")){
            lnImagenDNIBotontes.setVisibility(View.VISIBLE);
            arrayImagenDNI.remove(0);
            lnFotosDNI.setVisibility(View.GONE);
        } else if (_entornoImagen.equals("CONSCUIL")){
            lnImagenConsCUILBotontes.setVisibility(View.VISIBLE);
            arrayImagenConstanciaCUIL.remove(0);
            lnFotosConstanciaCUIL.setVisibility(View.GONE);
        } else if (_entornoImagen.equals("TERCERA")){
            lnImagenTerceraBotontes.setVisibility(View.VISIBLE);
            arrayImagenTercera.remove(0);
            lnFotosTercera.setVisibility(View.GONE);
        } else if (_entornoImagen.equals("CUARTA")){
            lnImagenCuartoBotontes.setVisibility(View.VISIBLE);
            arrayImagenCuarta.remove(0);
            lnFotosCuarta.setVisibility(View.GONE);
        } else if (_entornoImagen.equals("QUINTA")){
            lnImagenQuintoBotontes.setVisibility(View.VISIBLE);
            arrayImagenQuinta.remove(0);
            lnFotosQuinta.setVisibility(View.GONE);
        }
        lnBotonesConfirmar.setVisibility(View.GONE);
    }
    //endregion

    private void agregarGF(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        String yaRegistroID= funciones.verificaSenderID(getActivity(),3);
        if (yaRegistroID.equals("SD") || yaRegistroID.equals("NO")){
            String valorDevuelto="";
            if (yaRegistroID.equals("SD")) {
                valorDevuelto="No se puede informar la internación porque todavía no se registra el dispositivo";
            }else{
                valorDevuelto="No se puede informar la internación porque no se pudo registrar el dispositivo";
            }
            dialogo = fabrica_dialogos.dlgError(getContext(), valorDevuelto);
            return;
        }

        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Procesando la incorporación del grupo familiar");
        String imgFin1, imgFin2, imgFin3, imgFin4, imgFin5;

        if (arrayImagenDNI.size()>0) {
            ByteArrayOutputStream bao=null;
            bao = new ByteArrayOutputStream();
            arrayImagenDNI.get(0).compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            imgFin1 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
            //imgFin1 = arrayImagenDNI.get(0).toString();
        } else{
            imgFin1="";
        }

        if (arrayImagenConstanciaCUIL.size()>0) {
            ByteArrayOutputStream bao=null;
            bao = new ByteArrayOutputStream();
            arrayImagenConstanciaCUIL.get(0).compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            imgFin2 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
            //imgFin2 = arrayImagenConstanciaCUIL.get(0).toString();
        } else{
            imgFin2="";
        }

        if (arrayImagenTercera.size()>0) {
            ByteArrayOutputStream bao=null;
            bao = new ByteArrayOutputStream();
            arrayImagenTercera.get(0).compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            imgFin3 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
            //imgFin3 = arrayImagenTercera.get(0).toString();
        } else{
            imgFin3="";
        }

        if (arrayImagenCuarta.size()>0) {
            ByteArrayOutputStream bao=null;
            bao = new ByteArrayOutputStream();
            arrayImagenCuarta.get(0).compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            imgFin4 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
            //imgFin4 = arrayImagenCuarta.get(0).toString();
        } else{
            imgFin4="";
        }

        if (arrayImagenQuinta.size()>0) {
            ByteArrayOutputStream bao=null;
            bao = new ByteArrayOutputStream();
            arrayImagenQuinta.get(0).compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            imgFin5 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
            //imgFin5 = arrayImagenQuinta.get(0).toString();
        } else{
            imgFin5="";
        }

        SQLController database = new SQLController(getContext());
        Map<String, String> params = new HashMap<String, String>();
        params.put("idAfiliadotitular", database.obtenerIDAfiliado());
        params.put("apellido", _apellNombFam);
        params.put("nombre", "");
        params.put("cuil", _cuilFam);
        params.put("fecNac", _fecNacFam);
        params.put("parentesco", _nombreParentesco);
        params.put("estadoCivil", _nombreParentesco);
        params.put("nacionalidad", _nacionalidadFam);
        params.put("sexo", _sexoFam);
        params.put("codParent", _idTipoParentesco);
        params.put("imgFin1DNI", imgFin1);
        params.put("imgFin2DNI", "");
        params.put("imgFin3DNI", "");
        params.put("imgFin1CUIL", imgFin2);
        params.put("imgFin5_1", imgFin3);
        params.put("imgFin5_2", "");
        params.put("imgFin5_3", "");
        params.put("imgFin6_1", imgFin4);
        params.put("imgFin6_2", "");
        params.put("imgFin6_3", "");
        params.put("imgFin7_1", imgFin5);
        params.put("imgFin7_2", "");
        params.put("imgFin7_3", "");
        params.put("imgFin7_4", "");
        params.put("imgFin7_5", "");
        params.put("comentarioURL", "");
        params.put("IMEI", funciones.obtenerIMEI(getContext()));

        fabrica_WS.APPInformarIncGF(getContext(), params, new SuccessResponseHandler<Document>() {
            @Override
            public void onSuccess(Document valores) {
                dialogo.dismiss();
                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                String idIncGF = valores.getElementsByTagName("idIngGF").item(0).getTextContent();
                String fecSolicitud = valores.getElementsByTagName("fecSolicitud").item(0).getTextContent();
                String idEstado = valores.getElementsByTagName("idEstado").item(0).getTextContent();

                SQLController database = new SQLController(getContext());
                database.agregarIngresoGF(idIncGF, fecSolicitud, idEstado, _cuilFam, _apellNombFam, ""
                        , _nombreParentesco, _sexoFam, _fecNacFam, "");
                database.agregarAviso(idIncGF);

                dialogo = fabrica_dialogos.dlgAviso(getContext(), msgAviso);
                LinearLayout lnMsgTotal=(LinearLayout)dialogo.findViewById(R.id.lnMsgTotal);
                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogo.dismiss();
                        trigger.fireChange("tramites_documentacion_btnTermino");
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
    }
    //endregion


    //region otros
    @Override
    public void onAttach (Context context){
        super.onAttach(context);
        tiempoMulticlick=Float.parseFloat(getString(R.string.tiempoMulticlick));
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }

    //endregion
}
