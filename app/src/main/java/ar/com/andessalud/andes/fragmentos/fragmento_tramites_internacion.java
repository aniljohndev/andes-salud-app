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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class fragmento_tramites_internacion extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo;
    private static final int RESULT_LOAD_IMAGE = 111;
    private LinearLayout lnAfiliado, lnAfiliadoResultado, listaAfiliado, lnAdjuntarImagenes
            , lnAdjuntarImagenesResultado, lnBotonesImagenes, lnBotonesConfirmar, lnFotos
            , lnTipoInternacion, lnTipoInternacionResultado, listaTipoInternacion;
    private TextView lblAfiliadoSeleccionado, lblAjuntarImagenesPaso, lblAdjuntarImagenes
            ,lblTipoInternacionSeleccionado, lblTipoInternacionPaso, lblSelecionarTipoInternacion;
    private String _idAfiliado="", _nombreAfiliado="",nombreImagen="", imgFin1="", imgFin2
            , imgFin3="", imgFin4="", imgFin5="", _idTipoInternacion="", _nombreTipoInternacion="";
    private CarouselView carouselView;
    private boolean adjuntarImagenesHabilitado=false, tipoInternacionHabilitado=false;
    private ImageView imvFromGallery, imvTakePhoto, btnSiguiente;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    private ArrayList<fragmento_tramites_internacion.imagenes> arrayImagenes;

    public fragmento_tramites_internacion() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_tramites_internacion, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lnAfiliado = (LinearLayout) view.findViewById(R.id.lnAfiliado);
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultado = (LinearLayout) view.findViewById(R.id.lnAfiliadoResultado);
        lnAfiliadoResultado.setVisibility(View.GONE);
        lblAfiliadoSeleccionado = (TextView) view.findViewById(R.id.lblAfiliadoSeleccionado);
        listaAfiliado = (LinearLayout) view.findViewById(R.id.listaAfiliado);
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

        lnTipoInternacion = (LinearLayout) view.findViewById(R.id.lnTipoInternacion);
        lnTipoInternacion.setVisibility(View.VISIBLE);
        lnTipoInternacionResultado = (LinearLayout) view.findViewById(R.id.lnTipoInternacionResultado);
        lnTipoInternacionResultado.setVisibility(View.GONE);
        lblTipoInternacionSeleccionado = (TextView) view.findViewById(R.id.lblTipoInternacionSeleccionado);
        lblTipoInternacionPaso = (TextView) view.findViewById(R.id.lblTipoInternacionPaso);
        lblSelecionarTipoInternacion = (TextView) view.findViewById(R.id.lblSelecionarTipoInternacion);
        listaTipoInternacion = (LinearLayout) view.findViewById(R.id.listaTipoInternacion);
        listaTipoInternacion.setVisibility(View.GONE);
        lnTipoInternacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTipoInternacion();
            }
        });
        lnTipoInternacionResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTipoInternacion();
            }
        });

        lnTipoInternacion= (LinearLayout) view.findViewById(R.id.lnTipoInternacion);
        lnTipoInternacion.setVisibility(View.VISIBLE);

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

        arrayImagenes = new ArrayList<fragmento_tramites_internacion.imagenes>();

        lnFotos=(LinearLayout) view.findViewById(R.id.lnFotos);
        lnFotos.setVisibility(View.GONE);

        btnSiguiente= (ImageView) view.findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informarInternacion();
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
        lblAfiliadoSeleccionado.setText("Seleccionar afiliado");
        listaAfiliado.setVisibility(View.GONE);
        _idAfiliado="";
        _nombreAfiliado="";
        deshabilitaTipoInternacion();
        deshabilitaAdjuntarImagenes();
        buscarAfiliado();
    }

    private void clickTipoInternacion(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnTipoInternacion.setVisibility(View.VISIBLE);
        lnTipoInternacionResultado.setVisibility(View.GONE);
        lblTipoInternacionSeleccionado.setText("Seleccionar \\nTipo de\\Internacion");
        listaTipoInternacion.setVisibility(View.GONE);
        _idTipoInternacion="";
        _nombreTipoInternacion="";
        deshabilitaAdjuntarImagenes();
        buscarTipoInternacion();
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
            lblAfiliadoSeleccionado.setText("Seleccionar afiliado");
            listaAfiliado.removeAllViews();
            for (int i = 0; i < idAfiliados.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnGrupoFam = inflater.inflate(R.layout.lista_grupo_fam, null);

                final String idAfiliadoLocal=idAfiliados[i];
                final String grupoFamLocal=grupoFam[i];
                final String numAfilLocal=numAfil[i];
                final String tipoMensajeLocal=tipoMensaje[i];
                final String mensajeLocal=mensaje[i];

                TextView idFila = (TextView) lnGrupoFam.findViewById(R.id.idFila);
                TextView oculto1 = (TextView) lnGrupoFam.findViewById(R.id.oculto1);
                TextView oculto2 = (TextView) lnGrupoFam.findViewById(R.id.oculto2);
                TextView linea1 = (TextView) lnGrupoFam.findViewById(R.id.linea1);
                TextView linea2 = (TextView) lnGrupoFam.findViewById(R.id.linea2);
                ImageView image = (ImageView) lnGrupoFam.findViewById(R.id.imgEstado);

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
        habilitaTipoInternacion();
    }
    //#endregion

    //#region buscar tipo de internacioin
    private void deshabilitaTipoInternacion(){
        tipoInternacionHabilitado=false;
        lblTipoInternacionPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblSelecionarTipoInternacion.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnTipoInternacion.setVisibility(View.VISIBLE);
        lnTipoInternacionResultado.setVisibility(View.GONE);
        listaTipoInternacion.setVisibility(View.GONE);
    }

    private void habilitaTipoInternacion(){
        tipoInternacionHabilitado=true;
        lblTipoInternacionPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblSelecionarTipoInternacion.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void buscarTipoInternacion(){
        if (!tipoInternacionHabilitado){
            return;
        }
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los tipos de internaci??n");

            fabrica_WS.APPBuscarTipoInternacionAPP(getActivity(), idAfiliado, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idTipoInternacion = new String[valores.getElementsByTagName("idTipoInternacion").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idTipoInternacion").getLength(); i++) {
                        idTipoInternacion[i] = valores.getElementsByTagName("idTipoInternacion").item(i).getTextContent();
                    }
                    String[] tipoInternacion = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        tipoInternacion[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    mostrarTipoInternacion(idTipoInternacion, tipoInternacion);
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los tipos de internaci??n:\n"+ex.getMessage());
        }

    }

    private void mostrarTipoInternacion(String[] idTipoInternacion, String[] tipoInternacion) {
        try {
            lnTipoInternacion.setVisibility(View.GONE);
            lnTipoInternacionResultado.setVisibility(View.VISIBLE);
            lblTipoInternacionSeleccionado.setText("Seleccionar tipo de internaci??n");
            listaTipoInternacion.removeAllViews();
            for (int i = 0; i < idTipoInternacion.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnTipoInternacion = inflater.inflate(R.layout.lista_grupo_fam, null);

                final String idTipoInternacionLocal=idTipoInternacion[i];
                final String nombreInternacionLocal=tipoInternacion[i];

                TextView idFila = (TextView) lnTipoInternacion.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnTipoInternacion.findViewById(R.id.linea1);
                TextView linea2 = (TextView) lnTipoInternacion.findViewById(R.id.linea2);
                ImageView image = (ImageView) lnTipoInternacion.findViewById(R.id.imgEstado);

                idFila.setText(idTipoInternacionLocal);
                linea1.setText(nombreInternacionLocal);
                linea2.setVisibility(View.GONE);

                lnTipoInternacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seleccionarTipoInternacion(idTipoInternacionLocal, nombreInternacionLocal);
                    }
                });
                listaTipoInternacion.addView(lnTipoInternacion);
            }
            listaTipoInternacion.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar el tipo de internaci??n:\n" + e.getMessage());
        }
    }

    private void seleccionarTipoInternacion(String idTipoInternacion, String nombreTipoInternacion){
        _idTipoInternacion=idTipoInternacion;
        _nombreTipoInternacion=nombreTipoInternacion;
        lblTipoInternacionSeleccionado.setText(_nombreTipoInternacion);
        listaTipoInternacion.setVisibility(View.GONE);
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
        if (arrayImagenes.size()>=5){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Se puede enviar hasta 5 im??genes");
            return;
        }
        nombreImagen = UUID.randomUUID().toString();
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
        //startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 111);
    }

    private void buscarImagenDeCamara() {
        if (arrayImagenes.size()>=5){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Se puede enviar hasta 5 im??genes");
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
                fragmento_tramites_internacion.imagenes imagenLocal = new fragmento_tramites_internacion.imagenes(nombreImagen, BitmapFactory.decodeStream(imageStream));
                arrayImagenes.add(arrayImagenes.size(),imagenLocal);
                mostrarImagenes();
            } catch (Exception errorEx) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al agregar la im??gen la galer??a:\n\t" + errorEx.getMessage());
            }
        } else if (requestCode == 112 && resultCode == -1) {
            try {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + this.nombreImagen + ".jpg");
                fragmento_tramites_internacion.imagenes imagenLocal = new fragmento_tramites_internacion.imagenes(nombreImagen, BitmapFactory.decodeFile(file.getAbsolutePath()));
                arrayImagenes.add(arrayImagenes.size(), imagenLocal);
                mostrarImagenes();
            }catch (Exception errorEx) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al agregar la im??gen la c??mara:\n\t" + errorEx.getMessage());
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
        ArrayList<fragmento_tramites_internacion.imagenes> arrayImagenesLocales= new ArrayList<fragmento_tramites_internacion.imagenes>();
        for (int i = 0; i < arrayImagenes.size(); i++) {
            if (!arrayImagenes.get(i).idImagen.equals(idImagen)) {
                fragmento_tramites_internacion.imagenes imagenLocal = new fragmento_tramites_internacion.imagenes(arrayImagenes.get(i).idImagen,arrayImagenes.get(i).imagen);
                arrayImagenesLocales.add(imagenLocal);
            }
        }
        arrayImagenes=arrayImagenesLocales;
        mostrarImagenes();
    }
    //#endregion

    //region informar internacion
    private void informarInternacion(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();

        String yaRegistroID=funciones.verificaSenderID(getActivity(),3);
        if (yaRegistroID.equals("SD") || yaRegistroID.equals("NO")){
            String valorDevuelto="";
            if (yaRegistroID.equals("SD")) {
                valorDevuelto="No se puede informar la internaci??n porque todav??a no se registra el dispositivo";
            }else{
                valorDevuelto="No se puede informar la internaci??n porque no se pudo registrar el dispositivo";
            }
            dialogo = fabrica_dialogos.dlgError(getContext(), valorDevuelto);
            return;
        }

        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Procesando la solicitud de internaci??n");

        if (arrayImagenes.size()>=1) {
            ByteArrayOutputStream bao=null;
            bao = new ByteArrayOutputStream();
            arrayImagenes.get(0).imagen.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            imgFin1 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
            //imgFin1 = arrayImagenes.get(0).imagen.toString();
        } else{
            imgFin1="";
        }

        if (arrayImagenes.size()>=2) {
            ByteArrayOutputStream bao=null;
            bao = new ByteArrayOutputStream();
            arrayImagenes.get(1).imagen.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            imgFin2 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
        } else{
            imgFin2="";
        }

        if (arrayImagenes.size()>=3) {
            ByteArrayOutputStream bao=null;
            bao = new ByteArrayOutputStream();
            arrayImagenes.get(2).imagen.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            imgFin3 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
        } else{
            imgFin3="";
        }

        if (arrayImagenes.size()>=4) {
            ByteArrayOutputStream bao=null;
            bao = new ByteArrayOutputStream();
            arrayImagenes.get(3).imagen.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            imgFin4 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
        } else{
            imgFin4="";
        }

        if (arrayImagenes.size()>=5) {
            ByteArrayOutputStream bao=null;
            bao = new ByteArrayOutputStream();
            arrayImagenes.get(4).imagen.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            imgFin5 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
        } else{
            imgFin5="";
        }

        SQLController database = new SQLController(getContext());
        Map<String, String> params = new HashMap<String, String>();
        params.put("IMEI", funciones.obtenerIMEI(getContext()));
        params.put("idAfiliadoTitular", database.obtenerIDAfiliado());
        params.put("idAfiliado", _idAfiliado);
        params.put("imgPractica1", imgFin1);
        params.put("imgPractica2", imgFin2);
        params.put("imgPractica3", imgFin3);
        params.put("imgPractica4", imgFin4);
        params.put("imgPractica5", imgFin5);

        fabrica_WS.APPInformarInternacionTitular(getContext(), params, new SuccessResponseHandler<Document>() {
            @Override
            public void onSuccess(Document valores) {
                dialogo.dismiss();
                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                String idSolInternacion = valores.getElementsByTagName("idSolInternacion").item(0).getTextContent();
                String fecSolicitud = valores.getElementsByTagName("fecSolicitud").item(0).getTextContent();

                SQLController database= new SQLController(getContext());
                database.agregarInternacionSolicitud(idSolInternacion, fecSolicitud, _nombreAfiliado);
                database.agregarAviso(idSolInternacion);

                dialogo = fabrica_dialogos.dlgAviso(getContext(), msgAviso);
                LinearLayout lnMsgTotal=(LinearLayout)dialogo.findViewById(R.id.lnMsgTotal);
                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogo.dismiss();
                        trigger.fireChange("tramites_intarnacion_btnTermino");
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

    @Override
    public void onAttach (Context context){
        super.onAttach(context);
        tiempoMulticlick=Float.parseFloat(getString(R.string.tiempoMulticlick));
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
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
