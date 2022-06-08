package ar.com.andessalud.andes.fragmentos;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.clases.CapturePhotoUtils;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.funciones;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_credencial_provisoria extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo;
    private LinearLayout lnAfiliado, lnAfiliadoResultados, listaAfiliado, lnBotonesConfirmar;
    private TextView lblAfiliadoSeleccionado;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    private String _idAfiliado="", _nombreAfiliado="", nombreAfiliadoCred="", numAfiliadoCred=""
            , fecVencimientoCred="", tipoTarjetaCred="";
    Bitmap credentialBitmap;

    public fragmento_credencial_provisoria() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_credencial_provisoria, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //#region seleccionar afiliado
        lnAfiliado = (LinearLayout) view.findViewById(R.id.lnAfiliado);
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultados = (LinearLayout) view.findViewById(R.id.lnAfiliadoResultados);
        lnAfiliadoResultados.setVisibility(View.GONE);
        lblAfiliadoSeleccionado = (TextView) view.findViewById(R.id.lblAfiliadoSeleccionado);
        listaAfiliado = (LinearLayout) view.findViewById(R.id.listaAfiliado);
        listaAfiliado.setVisibility(View.GONE);

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

        lnBotonesConfirmar= (LinearLayout) view.findViewById(R.id.lnBotonesConfirmar);
        lnBotonesConfirmar.setVisibility(View.GONE);
        lnBotonesConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarDatosCredencial();
            }
        });
    }

    private void clickAfiliado(){
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick){
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultados.setVisibility(View.GONE);
        lblAfiliadoSeleccionado.setText("Seleccionar Afiliado");
        listaAfiliado.setVisibility(View.GONE);
        _idAfiliado="";
        _nombreAfiliado="";
        lnBotonesConfirmar.setVisibility(View.GONE);
        buscarAfiliado();
    }

    //#region afiliado
    private void buscarAfiliado(){
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliadoTitular = database.obtenerIDAfiliado();
            if (idAfiliadoTitular.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando el grupo familiar");

            fabrica_WS.APPBuscarGrupoFamiliar(getActivity(), idAfiliadoTitular, new SuccessResponseHandler<Document>() {
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
            lnAfiliadoResultados.setVisibility(View.VISIBLE);
            listaAfiliado.removeAllViews();
            for (int i = 0; i < idAfiliados.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnPrestador = inflater.inflate(R.layout.lista_grupo_fam, null);

                final String idAfiliadoLocal=idAfiliados[i];
                final String grupoFamLocal=grupoFam[i];
                final String numAfilLocal=numAfil[i];
                final String mensajeLocal=mensaje[i];

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
                        if (mensajeLocal.equals("--")){
                            seleccionarAfiliado(idAfiliadoLocal, grupoFamLocal);
                        }else{
                            dialogo = fabrica_dialogos.dlgError(getContext(), mensajeLocal);
                        }
                    }
                });
                listaAfiliado.addView(lnPrestador);
            }
            listaAfiliado.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar los prestadores:\n" + e.getMessage());
        }
    }

    private void seleccionarAfiliado(String idAfiliado, String apellNomb){
        _idAfiliado=idAfiliado;
        _nombreAfiliado=apellNomb;
        lblAfiliadoSeleccionado.setText(_nombreAfiliado);
        listaAfiliado.setVisibility(View.GONE);
        lnBotonesConfirmar.setVisibility(View.VISIBLE);
    }
    //#endregion

    //region buscar datos credencial
    private void buscarDatosCredencial(){
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando los datos de la credencial provisoria");

            Map<String, String> params = new HashMap<String, String>();
            params.put("IMEI", idAfiliado);
            params.put("idAfiliado", _idAfiliado);

            fabrica_WS.APPBuscaDatoCredencial(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    nombreAfiliadoCred = valores.getElementsByTagName("nombreAfiliado").item(0).getTextContent();
                    numAfiliadoCred = valores.getElementsByTagName("numAfiliado").item(0).getTextContent();
                    fecVencimientoCred = valores.getElementsByTagName("fecVencimiento").item(0).getTextContent();
                    tipoTarjetaCred = valores.getElementsByTagName("tipoTarjeta").item(0).getTextContent();
                    armarCredencial(nombreAfiliadoCred, numAfiliadoCred, fecVencimientoCred, tipoTarjetaCred);
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
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error desconocido al buscar los datos de la credencial provisoria:\n"+ex.getMessage());
        }
    }
    //endregion

    //region buscar la imagen de la credencial
    public void armarCredencial(String afiliado, String numAfiliado, String validez, String tipoTarjeta) {
        try{
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels-50;
            Double d = new Double((width*0.63));
            int height = d.intValue();
            int cardFrontImgRegID, cardBackImgRegID;
            if (tipoTarjeta.equals("TARJETA GOLD")){
                cardFrontImgRegID = R.drawable.cred_gold_front;
                cardBackImgRegID = R.drawable.cred_gold_back;
            }else if (tipoTarjeta.equals("TARJETA BLACK")){
                cardFrontImgRegID = R.drawable.cred_black_front;
                cardBackImgRegID = R.drawable.cred_black_back;
            }else if (tipoTarjeta.equals("TARJETA PLATINUM")){
                cardFrontImgRegID = R.drawable.cred_platinum_front;
                cardBackImgRegID = R.drawable.cred_platinum_back;
            }else if (tipoTarjeta.equals("TARJETA GREEN")){
                cardFrontImgRegID = R.drawable.cred_green_front;
                cardBackImgRegID = R.drawable.cred_green_back;
            }else if (tipoTarjeta.equals("TARJETA TITANIUM")){
                cardFrontImgRegID = R.drawable.cred_green_front;
                cardBackImgRegID = R.drawable.cred_green_back;
            }else {
                dialogo = fabrica_dialogos.dlgError(getContext(), "No se pudo determinar su tipo de tarjeta:\n" + tipoTarjeta+".\nPor favor avise a la delegación de ANDES sobre este mensaje.");
                return;
            }
            dialogo = new Dialog(getActivity());
            dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogo.setContentView(R.layout.dlg_modal_credencial_provisoria);


            ImageView imvCardFront = (ImageView) dialogo.findViewById(R.id.imvCardFront);imvCardFront.setImageResource(cardFrontImgRegID);
            ImageView imvCardBack = (ImageView) dialogo.findViewById(R.id.imvCardBack);imvCardBack.setImageResource(cardBackImgRegID);
            TextView tvNombreAfiliado = (TextView)dialogo.findViewById(R.id.tvNombreAfiliado);tvNombreAfiliado.setText(afiliado);
            TextView tvNumAfiliado = (TextView)dialogo.findViewById(R.id.tvNumAfiliado);tvNumAfiliado.setText(numAfiliado);
            TextView tvFecVenimiento = (TextView)dialogo.findViewById(R.id.tvFecVenimiento);tvFecVenimiento.setText(validez);

            LinearLayout btnConfirm = (LinearLayout)dialogo.findViewById(R.id.btnConfimar);
            LinearLayout btnCancel = (LinearLayout)dialogo.findViewById(R.id.btnCancel );

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout bmpArea = (LinearLayout)dialogo.findViewById(R.id.bmpArea);
                    credentialBitmap = loadBitmapFromView(bmpArea);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    credentialBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    dialogo.dismiss();
                    confirmarCredencial();


                    /*Bundle b = new Bundle();
                    b.putString("_idAfiliado",_idAfiliado);
                    b.putString("tipoTarjetaCred",tipoTarjetaCred);
                    b.putString("nombreAfiliadoCred",nombreAfiliadoCred);
                    b.putString("numAfiliadoCred",numAfiliadoCred);
                    b.putString("fecVencimientoCred",fecVencimientoCred);


                    byte[] byteArray = stream.toByteArray();
                    b.putByteArray("image",byteArray);
                    trigger.fireChange(11,b);*/
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogo.dismiss();
                }
            });
            dialogo.show();
        }catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error desconocido al armar la credencial provisoria:\n"+ex.getMessage());
        }
    }
    //endregion

    //region confirmar credencial
    private void confirmarCredencial(){
        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("IMEI", idAfiliado);
            params.put("idAfiliado", _idAfiliado);

            fabrica_WS.APPConfirmarCredencialProvisoria(getActivity(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    SQLController database= new SQLController(getContext());
                    String fecEmision = valores.getElementsByTagName("fecEmision").item(0).getTextContent();
                    String idCredencial=valores.getElementsByTagName("idCredencial").item(0).getTextContent();
                    database.agregarCredencialProvisoria(idAfiliado, fecEmision, tipoTarjetaCred
                            , nombreAfiliadoCred,numAfiliadoCred,fecVencimientoCred,idCredencial);
                    database.agregarAviso(idCredencial);
                    dialogo = fabrica_dialogos.dlgDescargarCompartir(getContext(),"CREDENCIAL PROVISORIA",R.drawable.side_credencia);
                    LinearLayout btnDownload=(LinearLayout) dialogo.findViewById(R.id.btnDownload);
                    btnDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            descargarImagen();
                        }
                    });
                    LinearLayout btnShare=(LinearLayout) dialogo.findViewById(R.id.btnShare);
                    btnShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            compartirImagen();
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
        }catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error desconocido al confirmar la credencial provisoria:\n"+ex.getMessage());
        }
    }
    //endregion

    private void descargarImagen() {
        CapturePhotoUtils capturePhotoUtils = new CapturePhotoUtils();
        String localUrl = capturePhotoUtils.insertImage(getActivity().getContentResolver(), credentialBitmap, "Credencial de Andes Salud", "Credencial para los afiliados de Andes Salud");
        if (localUrl != null) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgAviso(getContext(), "La credencial se almacenó correctamente");
            LinearLayout lnMsgTotal=(LinearLayout)dialogo.findViewById(R.id.lnMsgTotal);
            lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogo.dismiss();
                    trigger.fireChange("credencial_credencial_provisoria_btnTermino");
                    return;
                }
            });
        } else {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error al almacenar la imagen");
        }
    }

    private void compartirImagen(){
        File mFile = savebitmap(credentialBitmap);

        Uri u = null;
        u = Uri.fromFile(mFile);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("image/*");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Credencial prov. ANDES Salud");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Credencial provisoria del afiliado");
        emailIntent.putExtra(Intent.EXTRA_STREAM, u);
        startActivity(Intent.createChooser(emailIntent, "Enviar credencial"));
        dialogo.dismiss();
        dialogo = fabrica_dialogos.dlgAviso(getContext(), "La credencial se envió correctamente");
        LinearLayout lnMsgTotal=(LinearLayout)dialogo.findViewById(R.id.lnMsgTotal);
        lnMsgTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogo.dismiss();
                trigger.fireChange("credencial_credencial_provisoria_btnTermino");
                return;
            }
        });
    }

    private File savebitmap(Bitmap bmp) {
        String temp="credencial";
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
        Bitmap b = Bitmap.createBitmap( dpToPx(245), dpToPx(328), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void onAttach (Context context){
        super.onAttach(context);
        tiempoMulticlick=Float.parseFloat(getString(R.string.tiempoMulticlick));
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
