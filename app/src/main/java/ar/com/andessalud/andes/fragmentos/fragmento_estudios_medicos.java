package ar.com.andessalud.andes.fragmentos;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.ActionMenuView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.Glide;
import com.jama.carouselview.CarouselView;
import com.jama.carouselview.CarouselViewListener;
import com.jama.carouselview.enums.IndicatorAnimationType;
import com.jama.carouselview.enums.OffsetType;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
public class fragmento_estudios_medicos extends Fragment{

    private FragmentChangeTrigger trigger;
    private Dialog dialogo;
    private static final int RESULT_LOAD_IMAGE = 111;
    private LinearLayout lnAfiliado, lnAfiliadoResultado, listaAfiliado, lnPrestador, lnPrestadorResultado
            , listaPrestador, lnAdjuntarImagenes, lnAdjuntarImagenesResultado
            , lnBotonesImagenes, lnBotonesConfirmar, lnPrestadorBusqueda, lnFotos;
    private TextView lblAfiliadoSeleccionado, lblSeleccionarPrestadorPaso, lblSeleccionarPrestador
            ,lblPrestadorSeleccionado, lblAjuntarImagenesPaso, lblAdjuntarImagenes;
    private String _idAfiliado="", _nombreAfiliado="", _idPrestador="", _nombrePrestador=""
            ,nombreImagen="", imgFin1="", imgFin2, imgFin3="", imgFin4="", imgFin5="";
    private CarouselView carouselView;
    private boolean prestadorHabilitado=false, adjuntarImagenesHabilitado=false;
    private ImageView imvFromGallery, imvTakePhoto, btnSiguiente;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    private EditText txtBuscarPrestador;
    boolean yaMostroCarrousel=false;
    private ArrayList<prestador> arrayPrestadorLocal;
    private ArrayList<imagenes> arrayImagenes;

    public fragmento_estudios_medicos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_estudios_medicos, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //#region seleccionar afiliado
        lnAfiliado=(LinearLayout) view.findViewById(R.id.lnAfiliado);
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultado=(LinearLayout) view.findViewById(R.id.lnAfiliadoResultado);
        lnAfiliadoResultado.setVisibility(View.GONE);
        lblAfiliadoSeleccionado=(TextView) view.findViewById(R.id.lblAfiliadoSeleccionado);
        listaAfiliado=(LinearLayout) view.findViewById(R.id.listaAfiliado);
        listaAfiliado.setVisibility(View.GONE);
        lnAfiliado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAfiliado();
            }
        });

        lnAfiliadoResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAfiliado();
            }
        });

        //#region seleccionar prestador
        lnPrestador=(LinearLayout) view.findViewById(R.id.lnPrestador);
        lnPrestador.setVisibility(View.VISIBLE);
        lblSeleccionarPrestadorPaso=(TextView) view.findViewById(R.id.lblSeleccionarPrestadorPaso);
        lblSeleccionarPrestador=(TextView) view.findViewById(R.id.lblSeleccionarPrestador);
        lnPrestadorResultado=(LinearLayout) view.findViewById(R.id.lnPrestadorResultado);
        lnPrestadorResultado.setVisibility(View.GONE);
        lblPrestadorSeleccionado=(TextView) view.findViewById(R.id.lblPrestadorSeleccionado);
        listaPrestador=(LinearLayout) view.findViewById(R.id.listaPrestador);
        listaPrestador.setVisibility(View.GONE);
        lnPrestador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPrestador();
            }
        });
        lnPrestadorBusqueda=(LinearLayout) view.findViewById(R.id.lnPrestadorBusqueda);
        lnPrestadorBusqueda.setVisibility(View.GONE);
        lnPrestadorResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPrestador();
            }
        });

        //prueba para ver si detectamos cuando carga completamente el linear layout
        ViewTreeObserver vto = listaPrestador.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    listaPrestador.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                } else {
                    listaPrestador.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                }
                if (dialogo!=null){
                    dialogo.dismiss();
                }
            }
        });
        lnAdjuntarImagenes=(LinearLayout) view.findViewById(R.id.lnAdjuntarImagenes);
        lblAjuntarImagenesPaso=(TextView) view.findViewById(R.id.lblAjuntarImagenesPaso);
        lblAdjuntarImagenes=(TextView) view.findViewById(R.id.lblAdjuntarImagenes);
        lnAdjuntarImagenesResultado=(LinearLayout) view.findViewById(R.id.lnAdjuntarImagenesResultado);
        lnAdjuntarImagenesResultado.setVisibility(View.GONE);

        lnBotonesImagenes=(LinearLayout) view.findViewById(R.id.lnBotonesImagenes);
        lnBotonesImagenes.setVisibility(View.GONE);
        lnAdjuntarImagenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAdjuntarImagenes();
            }
        });

        carouselView = view.findViewById(R.id.carouselView);
        carouselView.setVisibility(View.GONE);

        lnBotonesConfirmar=(LinearLayout) view.findViewById(R.id.lnBotonesConfirmar);
        lnBotonesConfirmar.setVisibility(View.GONE);

        imvFromGallery = (ImageView) view.findViewById(R.id.imvFromGallery);
        imvFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarImagenDeGaleria();
            }
        });

        imvTakePhoto = (ImageView) view.findViewById(R.id.imvTakePhoto);
        imvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarImagenDeCamara();
            }
        });

        arrayPrestadorLocal = new ArrayList<prestador>();
        arrayImagenes = new ArrayList<imagenes>();

        txtBuscarPrestador=(EditText) view.findViewById(R.id.txtBuscarPrestador);
        txtBuscarPrestador.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {
                    muestraPrestadoresConBusqueda(s.toString());
                }
            }
        });

        lnFotos=(LinearLayout) view.findViewById(R.id.lnFotos);
        lnFotos.setVisibility(View.GONE);

        btnSiguiente= (ImageView) view.findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarPedidoMedico();
            }
        });
    }

    private void clickAfiliado(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultado.setVisibility(View.GONE);
        lblAfiliadoSeleccionado.setText("Seleccionar Afiliado");
        listaAfiliado.setVisibility(View.GONE);
        _idAfiliado="";
        _nombreAfiliado="";
        deshabilitaPrestador();
        deshabilitaAdjuntarImagenes();
        buscarAfiliado();
    }

    private void clickPrestador(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnPrestador.setVisibility(View.VISIBLE);
        lnPrestadorResultado.setVisibility(View.GONE);
        listaPrestador.setVisibility(View.GONE);
        lblPrestadorSeleccionado.setText("Seleccionar Prestador");
        _idPrestador="";
        _nombrePrestador="";
        deshabilitaAdjuntarImagenes();
        leerPrestadores();
    }

    private void clickAdjuntarImagenes(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        if (!adjuntarImagenesHabilitado){
            return;
        }
        lnAdjuntarImagenes.setVisibility(View.GONE);
        lnAdjuntarImagenesResultado.setVisibility(View.VISIBLE);
        lnBotonesImagenes.setVisibility(View.VISIBLE);
    }

    //#region afiliado
    private void buscarAfiliado(){
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();

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
            lnAfiliadoResultado.setVisibility(View.VISIBLE);
            lblAfiliadoSeleccionado.setText("Seleccionar \nafiliado");
            listaAfiliado.removeAllViews();
            for (int i = 0; i < idAfiliados.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnGrupoFam = inflater.inflate(R.layout.lista_grupo_fam, null);

                final String idAfiliadoLocal=idAfiliados[i];
                final String grupoFamLocal=grupoFam[i];
                final String numAfilLocal=numAfil[i];
                final String mensajeLocal=mensaje[i];

                TextView idFila = (TextView) lnGrupoFam.findViewById(R.id.idFila);
                TextView oculto1 = (TextView) lnGrupoFam.findViewById(R.id.oculto1);
                TextView linea1 = (TextView) lnGrupoFam.findViewById(R.id.linea1);
                TextView linea2 = (TextView) lnGrupoFam.findViewById(R.id.linea2);

                idFila.setText(idAfiliadoLocal);
                oculto1.setText(mensajeLocal);
                linea1.setText(grupoFamLocal);
                linea2.setText(numAfilLocal);

                lnGrupoFam.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mensajeLocal.equals("--")){
                            seleccionarAfiliado(idAfiliadoLocal, grupoFamLocal);
                        }else{
                            dialogo = fabrica_dialogos.dlgError(getContext(), mensajeLocal);
                        }
                    }
                });
                listaAfiliado.addView(lnGrupoFam);
            }
            listaAfiliado.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar el grupo familiar:\n" + e.getMessage());
        }
    }

    private void seleccionarAfiliado(String idAfiliado, String apellNomb){
        _idAfiliado=idAfiliado;
        _nombreAfiliado=apellNomb;
        lblAfiliadoSeleccionado.setText(_nombreAfiliado);
        listaAfiliado.setVisibility(View.GONE);
        habilitaPrestador();
    }
    //#endregion

    //#region prestador
    private void deshabilitaPrestador(){
        prestadorHabilitado=false;
        lblSeleccionarPrestadorPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblSeleccionarPrestador.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnPrestador.setVisibility(View.VISIBLE);
        lnPrestadorResultado.setVisibility(View.GONE);
        listaPrestador.setVisibility(View.GONE);
        lnPrestadorBusqueda.setVisibility(View.GONE);
    }

    private void habilitaPrestador(){
        prestadorHabilitado=true;
        lblSeleccionarPrestadorPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblSeleccionarPrestador.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void leerPrestadores() {
        if (!prestadorHabilitado){
            return;
        }

        if (arrayPrestadorLocal.size()>0){
            lnPrestador.setVisibility(View.GONE);
            lnPrestadorResultado.setVisibility(View.VISIBLE);
            lblPrestadorSeleccionado.setText("Seleccionar Prestador");
            lnPrestadorBusqueda.setVisibility(View.VISIBLE);
            txtBuscarPrestador.setText("");
            muestraPrestadoresConBusqueda("");
            return;
        }
        try{
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los prestadores");

            Map<String, String> params = new HashMap<String, String>();
            params.put("IMEI", funciones.obtenerIMEI(getContext()));
            params.put("idAfiliado", _idAfiliado);
            params.put("cadena", "---");

            fabrica_WS.APPBuscarCartillaPrestadoresPracticas(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idPrestador = new String[valores.getElementsByTagName("idConvenio").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idConvenio").getLength(); i++) {
                        idPrestador[i] = valores.getElementsByTagName("idConvenio").item(i).getTextContent();
                    }
                    String[] prestador = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        prestador[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    mostrarPrestador(idPrestador, prestador);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los prestadores:\n" + e.getMessage());
        }
    }

    private void mostrarPrestador(String[] idPrestador, String[] prestador) {
        try {
            lnPrestador.setVisibility(View.GONE);
            lnPrestadorResultado.setVisibility(View.VISIBLE);
            lblPrestadorSeleccionado.setText("Seleccionar Prestador");
            lnPrestadorBusqueda.setVisibility(View.VISIBLE);
            for (int i = 0; i < idPrestador.length; i++) {
                final String idPrestadorLocal=idPrestador[i];
                final String prestadorLocal=prestador[i];
                prestador prest = new prestador(idPrestadorLocal, prestadorLocal);
                arrayPrestadorLocal.add(prest);
            }
            muestraPrestadoresConBusqueda("");
            listaPrestador.setVisibility(View.VISIBLE);

            //dialogo.dismiss();
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar los prestadores:\n" + e.getMessage());
        }
    }

    private void muestraPrestadoresConBusqueda(String palabra){
        listaPrestador.removeAllViews();
        for (int i = 0; i < arrayPrestadorLocal.size(); i++) {
            if (arrayPrestadorLocal.get(i).nombrePrestador.toUpperCase().contains(palabra.toString().toUpperCase())) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View lnPrestador = inflater.inflate(R.layout.lista_un_renglon_tilde, null);
                TextView idFila = (TextView) lnPrestador.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnPrestador.findViewById(R.id.linea1);

                final String idPrestador=arrayPrestadorLocal.get(i).idPrestador;
                final String nombrePrestador=arrayPrestadorLocal.get(i).nombrePrestador;
                idFila.setText(idPrestador);
                linea1.setText(nombrePrestador);
                lnPrestador.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seleccionarPrestador(idPrestador, nombrePrestador);
                    }
                });
                listaPrestador.addView(lnPrestador);
            }
        }
        listaPrestador.setVisibility(View.VISIBLE);
    }

    private void seleccionarPrestador(String idPrestador, String nombrePrestador){
        _idPrestador=idPrestador;
        _nombrePrestador=nombrePrestador;
        lblPrestadorSeleccionado.setText(_nombrePrestador);
        listaPrestador.setVisibility(View.GONE);
        lnPrestadorBusqueda.setVisibility(View.GONE);
        habilitaAdjuntarImagenes();
    }
    //#endregion

    //#region adjuntar imagenes
    private void deshabilitaAdjuntarImagenes(){
        adjuntarImagenesHabilitado=false;
        lblAjuntarImagenesPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblAdjuntarImagenes.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnAdjuntarImagenes.setVisibility(View.VISIBLE);
        lnAdjuntarImagenesResultado.setVisibility(View.GONE);
        lnBotonesImagenes.setVisibility(View.GONE);
        lnBotonesConfirmar.setVisibility(View.GONE);
    }

    private void habilitaAdjuntarImagenes(){
        adjuntarImagenesHabilitado=true;
        lblAjuntarImagenesPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblAdjuntarImagenes.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void buscarImagenDeGaleria() {
        if (arrayImagenes.size()>5){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Se puede enviar hasta 5 imágenes");
            return;
        }
        nombreImagen = UUID.randomUUID().toString();
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
        //startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 111);
    }

    private void buscarImagenDeCamara() {
        if (arrayImagenes.size()>5){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Se puede enviar hasta 5 imágenes");
            return;
        }
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
                imagenes imagenLocal = new imagenes(nombreImagen, BitmapFactory.decodeStream(imageStream));
                arrayImagenes.add(0,imagenLocal);
                mostrarImagenes();
            } catch (Exception errorEx) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al agregar la imágen la galería:\n\t" + errorEx.getMessage());
            }
        } else if (requestCode == 112 && resultCode == -1) {
            try {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + this.nombreImagen + ".jpg");
                imagenes imagenLocal = new imagenes(nombreImagen, BitmapFactory.decodeFile(file.getAbsolutePath()));
                arrayImagenes.add(0, imagenLocal);
                mostrarImagenes();
            }catch (Exception errorEx) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al agregar la imágen la cámara:\n\t" + errorEx.getMessage());
            }
        }
    }

    private void mostrarImagenes(){
        if (arrayImagenes.size()==0){
            lnFotos.setVisibility(View.GONE);
            lnBotonesConfirmar.setVisibility(View.GONE);
            return;
        }
        carouselView.setSize(arrayImagenes.size());
        carouselView.setResource(R.layout.center_carousel_item);
        carouselView.setAutoPlay(false);
        carouselView.setIndicatorAnimationType(IndicatorAnimationType.THIN_WORM);
        carouselView.setCarouselOffset(OffsetType.CENTER);
        carouselView.setCarouselViewListener(new CarouselViewListener() {
            @Override
            public void onBindView(View view, int position) {
                // Example here is setting up a full image carousel
                final String idImagen=arrayImagenes.get(position).idImagen;
                ImageView imageView = view.findViewById(R.id.imageView);
                Glide.with(getContext()).load(arrayImagenes.get(position).imagen).into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eliminarFoto(idImagen);
                    }
                });
            }
        });
        // After you finish setting up, show the CarouselView
        try {
            carouselView.show();
        }catch (Exception ex){
            String a="";
        }
        carouselView.setVisibility(View.VISIBLE);
        lnFotos.setVisibility(View.VISIBLE);
        lnBotonesConfirmar.setVisibility(View.VISIBLE);
    }

    private void eliminarFoto(String idImagen){
        ArrayList<imagenes> arrayImagenesLocales= new ArrayList<imagenes>();
        for (int i = 0; i < arrayImagenes.size(); i++) {
            if (!arrayImagenes.get(i).idImagen.equals(idImagen)) {
                imagenes imagenLocal = new imagenes(arrayImagenes.get(i).idImagen,arrayImagenes.get(i).imagen);
                arrayImagenesLocales.add(imagenLocal);
            }
        }
        arrayImagenes=arrayImagenesLocales;
        mostrarImagenes();
    }
    //#endregion

    //region confirma datos
    private void confirmarPedidoMedico(){
        String mensajeDlg=_nombreAfiliado+"\n\n"+_nombrePrestador;
        dialogo = fabrica_dialogos.dlgConfirmarDatos(getContext(), "Confirmar datos", mensajeDlg,R.drawable.side_estudiomedicos,"");
        ImageView btnSi=(ImageView)dialogo.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogo.dismiss();
                solicitaPractica();
            }
        });
    }
    //endregion

    //region solicita practica
    private void solicitaPractica(){
        try{
            String yaRegistroID=funciones.verificaSenderID(getActivity(),3);
            if (yaRegistroID.equals("SD") || yaRegistroID.equals("NO")){
                String valorDevuelto="";
                if (yaRegistroID.equals("SD"))
                    valorDevuelto = "No se puede solicitar el estudio médico porque todavía no se registra el dispositivo";
                else{
                    valorDevuelto="No se puede solicitar el estudio médico porque no se pudo registrar el dispositivo";
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

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Procesando la solicitud");

            ByteArrayOutputStream bao=null;
            bao = new ByteArrayOutputStream();
            if (arrayImagenes.size()>=1) {
                arrayImagenes.get(0).imagen.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                byte[] ba = bao.toByteArray();
                imgFin1 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
                //String base64String = funciones.convertirImagen(arrayImagenes.get(0).imagen);
                //imgFin1 = base64String;
            } else{
                imgFin1="";
            }

            if (arrayImagenes.size()>=2) {
                arrayImagenes.get(1).imagen.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                byte[] ba = bao.toByteArray();
                imgFin2 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
            } else{
                imgFin2="";
            }

            if (arrayImagenes.size()>=3) {
                arrayImagenes.get(2).imagen.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                byte[] ba = bao.toByteArray();
                imgFin3 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
            } else{
                imgFin3="";
            }

            if (arrayImagenes.size()>=4) {
                arrayImagenes.get(3).imagen.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                byte[] ba = bao.toByteArray();
                imgFin4 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
            } else{
                imgFin4="";
            }

            if (arrayImagenes.size()>=5) {
                arrayImagenes.get(4).imagen.compress(Bitmap.CompressFormat.JPEG, 50, bao);
                byte[] ba = bao.toByteArray();
                imgFin4 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
            } else{
                imgFin5="";
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliadoTitular", idAfiliadoTitular);
            params.put("idAfiliado", _idAfiliado);
            params.put("imagen1", imgFin1);
            params.put("imagen2", imgFin2);
            params.put("imagen3", imgFin3);
            params.put("imagen4", imgFin4);
            params.put("imagen5", imgFin5);
            params.put("IMEI", funciones.obtenerIMEI(getContext()));
            params.put("idConvenio", _idPrestador);

            fabrica_WS.APPSolicitarPractica(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                    String idOrden = valores.getElementsByTagName("idOrden").item(0).getTextContent();
                    String fecSolicitud = valores.getElementsByTagName("fecSolicitud").item(0).getTextContent();

                    SQLController database= new SQLController(getContext());
                    database.agregarOrdenPractica(idOrden,fecSolicitud,_nombreAfiliado);
                    database.agregarAviso(idOrden);
                    dialogo = fabrica_dialogos.dlgAviso(getContext(), msgAviso);
                    LinearLayout lnMsgTotal=(LinearLayout)dialogo.findViewById(R.id.lnMsgTotal);
                    lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogo.dismiss();
                            trigger.fireChange("estudios_medicos_btnTermino");
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
        }catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al confirmar la orden de estudios médicos:\n" + e.getMessage());
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

    public class prestador {
        private String idPrestador;
        private String nombrePrestador;

        public prestador(String idPrestador, String nombrePrestador) {

            this.idPrestador = idPrestador;
            this.nombrePrestador = nombrePrestador;
        }

        public String getIdPrestador() {

            return idPrestador;
        }

        public void setIdPrestador(String idPrestador) {

            this.idPrestador = idPrestador;
        }

        public String getNombrePrestador() {

            return nombrePrestador;
        }

        public void setNombrePrestador(String nombrePrestador) {

            this.nombrePrestador = nombrePrestador;
        }
    }

    public class imagenes {
        private String idImagen;
        private Bitmap imagen;

        public imagenes(String idImagen, Bitmap imagen) {

            this.idImagen = idImagen;
            this.imagen = imagen;
        }

        public String getIdImagen() {

            return idImagen;
        }

        public void setIdImagen(Bitmap imagen) {

            this.imagen = imagen;
        }

        public Bitmap getIamgen() {

            return imagen;
        }

        public void setImagen(Bitmap imagen) {

            this.imagen = imagen;
        }
    }
}


