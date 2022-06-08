package ar.com.andessalud.andes.fragmentos;

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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
public class fragmento_tramites_documentacion extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo;
    private static final int RESULT_LOAD_IMAGE = 111;
    private LinearLayout lnAfiliado, lnAfiliadoResultado, listaAfiliado, lnTipoDocumentacion
            ,lnDocumentoResultado, listaDocumentos, lnAdjuntarImagenes, lnAdjuntarImagenesResultado
            ,lnBotonesImagenes, lnFotos, lnBotonesConfirmar;
    private TextView lblAfiliadoSeleccionado, lblDocumentoSeleccionado, lblTipoDocumentacionPaso
            , lblTipoDocumentacion, lblAjuntarImagenesPaso, lblAdjuntarImagenes
            , lblAdjuntarImagenesSeleccionado;
    private String _idAfiliado="", _nombreAfiliado="", _idTipoDocumento="", _nombreTipoDocumento=""
            , nombreImagen="", imgFin1="", imgFin2, imgFin3="";
    private CarouselView carouselView;
    private boolean adjuntarImagenesHabilitado=false, tipoDocumentacionHabilitado=false;
    private ImageView imvFromGallery, imvTakePhoto, btnSiguiente;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    private ArrayList<fragmento_tramites_documentacion.imagenes> arrayImagenes;

    public fragmento_tramites_documentacion() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_tramites_documentacion, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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

        lnTipoDocumentacion = (LinearLayout) view.findViewById(R.id.lnTipoDocumentacion);
        lnTipoDocumentacion.setVisibility(View.VISIBLE);
        lnDocumentoResultado = (LinearLayout) view.findViewById(R.id.lnDocumentoResultado);
        lnDocumentoResultado.setVisibility(View.GONE);
        lblTipoDocumentacionPaso=(TextView) view.findViewById(R.id.lblTipoDocumentacionPaso);
        lblTipoDocumentacion=(TextView) view.findViewById(R.id.lblTipoDocumentacion);
        lblDocumentoSeleccionado = (TextView) view.findViewById(R.id.lblDocumentoSeleccionado);
        listaDocumentos = (LinearLayout) view.findViewById(R.id.listaDocumentos);
        listaDocumentos.setVisibility(View.GONE);
        lnTipoDocumentacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTipoDocumentacion();
            }
        });

        lnDocumentoResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTipoDocumentacion();
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

        arrayImagenes = new ArrayList<fragmento_tramites_documentacion.imagenes>();

        lnFotos=(LinearLayout) view.findViewById(R.id.lnFotos);
        lnFotos.setVisibility(View.GONE);

        btnSiguiente= (ImageView) view.findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarDocumentacion();
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
        deshabilitaTipoDocumentacion();
        deshabilitaAdjuntarImagenes();
        buscarAfiliado();
    }

    private void clickTipoDocumentacion(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        if (!tipoDocumentacionHabilitado){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnTipoDocumentacion.setVisibility(View.VISIBLE);
        lnDocumentoResultado.setVisibility(View.GONE);
        lblDocumentoSeleccionado.setText("Seleccionar tipo de documento");
        listaDocumentos.setVisibility(View.GONE);
        _idTipoDocumento="";
        _nombreTipoDocumento="";
        deshabilitaAdjuntarImagenes();
        buscarTipoDocumentacion();
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
                        seleccionarAfiliado(idAfiliadoLocal, grupoFamLocal);
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
        habilitaTipoDocumentacion();
    }
    //#endregion

    //#region tipo de documentacion
    private void deshabilitaTipoDocumentacion(){
        tipoDocumentacionHabilitado=false;
        lblTipoDocumentacionPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblTipoDocumentacion.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnTipoDocumentacion.setVisibility(View.VISIBLE);
        lnDocumentoResultado.setVisibility(View.GONE);
        listaDocumentos.setVisibility(View.GONE);
    }

    private void habilitaTipoDocumentacion(){
        tipoDocumentacionHabilitado=true;
        lblTipoDocumentacionPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblTipoDocumentacion.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void buscarTipoDocumentacion(){
        try {
            lnTipoDocumentacion.setVisibility(View.GONE);
            lnDocumentoResultado.setVisibility(View.VISIBLE);
            lblDocumentoSeleccionado.setText("Seleccionar tipo de documento");
            listaDocumentos.removeAllViews();
            SQLController database= new SQLController(getContext());
            Cursor datDatos=database.leerTipoDocumentacion();
            if (datDatos.getCount()>0) {
                while (datDatos.isAfterLast() == false) {
                    LayoutInflater inflater = this.getLayoutInflater();
                    View lnTipoDoc = inflater.inflate(R.layout.lista_un_renglon_tilde, null);

                    final String idTipoDocLocal=datDatos.getString(0);
                    final String nombreDocLocal=datDatos.getString(1);

                    TextView idFila = (TextView) lnTipoDoc.findViewById(R.id.idFila);
                    TextView linea1 = (TextView) lnTipoDoc.findViewById(R.id.linea1);

                    idFila.setText(idTipoDocLocal);
                    linea1.setText(nombreDocLocal);

                    lnTipoDoc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        seleccionarDocumento(idTipoDocLocal, nombreDocLocal);
                        }
                    });
                    listaDocumentos.addView(lnTipoDoc);
                    datDatos.moveToNext();
                }
                listaDocumentos.setVisibility(View.VISIBLE);
            }
            else {
                dialogo = fabrica_dialogos.dlgError(getContext(), "No se encontraron tipos de documentos");
                return;
            }

        }catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar los tipo de documentación:\n" + e.getMessage());
            return;
        }
    }

    private void seleccionarDocumento(String idTipoDocLocal, String nombreDocLocal){
        _idTipoDocumento=idTipoDocLocal;
        _nombreTipoDocumento=nombreDocLocal;
        lblDocumentoSeleccionado.setText(_nombreTipoDocumento);
        listaDocumentos.setVisibility(View.GONE);
        habilitaAdjuntarImagenes();
    }
    //endregion

    //#region adjuntar imagenes
    private void deshabilitaAdjuntarImagenes(){
        adjuntarImagenesHabilitado=false;
        lblAjuntarImagenesPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblAdjuntarImagenes.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnAdjuntarImagenes.setVisibility(View.VISIBLE);
        lnAdjuntarImagenesResultado.setVisibility(View.GONE);
        lnBotonesImagenes.setVisibility(View.GONE);
        lnBotonesConfirmar.setVisibility(View.GONE);
        lnFotos.setVisibility(View.GONE);
    }

    private void habilitaAdjuntarImagenes(){
        adjuntarImagenesHabilitado=true;
        lblAjuntarImagenesPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblAdjuntarImagenes.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void buscarImagenDeGaleria() {
        if (arrayImagenes.size()>3){
            dialogo = fabrica_dialogos.dlgError(getContext(), "Se puede enviar hasta 3 imágenes");
            return;
        }
        nombreImagen = UUID.randomUUID().toString();
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void buscarImagenDeCamara() {
        if (arrayImagenes.size()>3){
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
                fragmento_tramites_documentacion.imagenes imagenLocal = new fragmento_tramites_documentacion.imagenes(nombreImagen, BitmapFactory.decodeStream(imageStream));
                arrayImagenes.add(0,imagenLocal);
                mostrarImagenes();
            } catch (Exception errorEx) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al agregar la imágen la galería:\n\t" + errorEx.getMessage());
            }
        } else if (requestCode == 112 && resultCode == -1) {
            try {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + this.nombreImagen + ".jpg");
                fragmento_tramites_documentacion.imagenes imagenLocal = new fragmento_tramites_documentacion.imagenes(nombreImagen, BitmapFactory.decodeFile(file.getAbsolutePath()));
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
        ArrayList<fragmento_tramites_documentacion.imagenes> arrayImagenesLocales= new ArrayList<fragmento_tramites_documentacion.imagenes>();
        for (int i = 0; i < arrayImagenes.size(); i++) {
            if (!arrayImagenes.get(i).idImagen.equals(idImagen)) {
                fragmento_tramites_documentacion.imagenes imagenLocal = new fragmento_tramites_documentacion.imagenes(arrayImagenes.get(i).idImagen,arrayImagenes.get(i).imagen);
                arrayImagenesLocales.add(imagenLocal);
            }
        }
        arrayImagenes=arrayImagenesLocales;
        mostrarImagenes();
    }
    //#endregion

    //region emviar documentacion
    private void enviarDocumentacion(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();

        String yaRegistroID=funciones.verificaSenderID(getActivity(),3);
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

        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Procesando la presentación de documentación");

        if (arrayImagenes.size()>=1) {
            ByteArrayOutputStream bao=null;
            bao = new ByteArrayOutputStream();
            arrayImagenes.get(0).imagen.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            imgFin1 = Base64.encodeToString(ba, Base64.NO_WRAP);//Base64.encodeBytes(ba);
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

        SQLController database = new SQLController(getContext());
        Map<String, String> params = new HashMap<String, String>();
        params.put("idAfiliadotitular", database.obtenerIDAfiliado());
        params.put("idAfiliado", _idAfiliado);
        params.put("tipoDocumentacion", _nombreTipoDocumento);
        params.put("imgFin1", imgFin1);
        params.put("imgFin2", imgFin2);
        params.put("imgFin3", imgFin3);
        params.put("IMEI", funciones.obtenerIMEI(getContext()));

        fabrica_WS.APPPresentarDocumentacion(getContext(), params, new SuccessResponseHandler<Document>() {
            @Override
            public void onSuccess(Document valores) {
                dialogo.dismiss();
                String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                String idPresDoc = valores.getElementsByTagName("idPresDoc").item(0).getTextContent();
                String fecSolicitud = valores.getElementsByTagName("fecSolicitud").item(0).getTextContent();
                String idEstado = valores.getElementsByTagName("idEstado").item(0).getTextContent();

                SQLController database= new SQLController(getContext());
                database.agregarPresentacionDoc(idPresDoc, fecSolicitud, _nombreAfiliado, _nombreTipoDocumento);
                database.agregarAviso(idPresDoc);

                dialogo = fabrica_dialogos.dlgAviso(getContext(), msgAviso);
                LinearLayout lnMsgTotal=(LinearLayout)dialogo.findViewById(R.id.lnMsgTotal);
                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogo.dismiss();
                        trigger.fireChange("tramites_incorporar_familiar_btnTermino");
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
    //endregion
}
