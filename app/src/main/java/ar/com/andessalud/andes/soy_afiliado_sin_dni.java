package ar.com.andessalud.andes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
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
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class soy_afiliado_sin_dni extends AppCompatActivity {

    Activity actActual=this;
    private Dialog dialog;

    static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 112;
    static final int RESULT_LOAD_IMAGE = 111;
    LinearLayout lnTotalPaso1, lnConfimarPaso1, lnTotalPaso2, lnConfimarPaso2, lnVolverPaso2;
    ImageView imvPreview, imvFromGallery, imvTakePhoto;
    int mode = 0, estaPaso2=0;
    String nombreImagen;
    int stepCount = 0, yaSubioImagen=0;
    String tipoDocumentoActual="";
    Bitmap imgSubir1;
    MaterialSpinner spinner;
    EditText edtTelefono, edtMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actActual.overridePendingTransition(R.anim.entrar_derecha, R.anim.salir_izquierda);
        setContentView(R.layout.soy_afiliado_sin_dni);

        mostrarMenuRedesSociales();

        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems("Trámite D.N.I.", "Pasaporte", "Carnet de conducir (nacional)");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                tipoDocumentoActual=item;
            }
        });

        this.imvPreview = (ImageView) findViewById(R.id.imvPreview);
        this.lnTotalPaso1 = (LinearLayout) findViewById(R.id.lnTotalPaso1);
        this.lnConfimarPaso1 = (LinearLayout) findViewById(R.id.lnConfimarPaso1);
        lnConfimarPaso1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muestraPaso2();
            }
        });

        this.imvFromGallery = (ImageView) findViewById(R.id.imvFromGallery);
        imvFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarImagenDeGaleria();
            }
        });

        this.imvTakePhoto = (ImageView) findViewById(R.id.imvTakePhoto);
        imvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarImagenDeCamara();
            }
        });

        this.lnTotalPaso2 = (LinearLayout) findViewById(R.id.lnTotalPaso2);
        lnTotalPaso2.setVisibility(View.GONE);
        this.lnConfimarPaso2 = (LinearLayout) findViewById(R.id.lnConfimarPaso2);
        lnConfimarPaso2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarDatos();
            }
        });

        this.lnVolverPaso2 = (LinearLayout) findViewById(R.id.lnVolverPaso2);
        lnVolverPaso2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverPaso1();
            }
        });

        edtTelefono = (EditText) findViewById(R.id.edtTelefono);
        edtMail = (EditText) findViewById(R.id.edtMail);
    }

    private void mostrarMenuRedesSociales(){
        Bundle args = new Bundle();
        args.putString("muestraContactCenter", "NO");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment_menu_sociales contentFragment = new fragment_menu_sociales();
        contentFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .add(R.id.frg_sociales, contentFragment)
                .commit();
    }

    //region foto desde galeria o cámara
    private void buscarImagenDeGaleria() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
        //startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 111);
    }

    private void buscarImagenDeCamara() {
        this.nombreImagen = UUID.randomUUID().toString();
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
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                imgSubir1 = BitmapFactory.decodeStream(imageStream);
                Glide.with(this).load(selectedImage).into(this.imvPreview);
                this.imvPreview.setVisibility(View.VISIBLE);
                yaSubioImagen=1;
            } catch (Exception errorEx) {
                dialog = new Dialog(actActual);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dlg_modal_error);
                TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                textMsg1.setText("Error al agregar la imágen la cámara:\n\t" + errorEx.getMessage());
                LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        } else if (requestCode == 112 && resultCode == -1) {
            try {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + this.nombreImagen + ".jpg");
                imgSubir1 = BitmapFactory.decodeFile(file.getAbsolutePath());
                Glide.with(this).load(file).into(this.imvPreview);
                this.imvPreview.setVisibility(View.VISIBLE);
                yaSubioImagen=1;
            }catch (Exception errorEx) {
                dialog = new Dialog(actActual);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dlg_modal_error);
                TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                textMsg1.setText("Error al agregar la imágen la cámara:\n\t" + errorEx.getMessage());
                LinearLayout lnMsgTotal = (LinearLayout) dialog.findViewById(R.id.lnMsgTotal);
                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }
    }
    //endregion

    private void muestraPaso2(){
        if (yaSubioImagen==0){
            dialog = new Dialog(actActual);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dlg_modal_error);
            TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
            textMsg1.setText("Falta agregar la imágen");
            LinearLayout lnMsgTotal = (LinearLayout) dialog.findViewById(R.id.lnMsgTotal);
            lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return;
        }
        Animation saleIzquierda = AnimationUtils.loadAnimation(this, R.anim.salir_izquierda);
        lnTotalPaso1.startAnimation(saleIzquierda);

        Animation ingresaDerecha = AnimationUtils.loadAnimation(this, R.anim.entrar_derecha);
        lnTotalPaso2.setVisibility(View.VISIBLE);
        lnTotalPaso2.startAnimation(ingresaDerecha);

        lnTotalPaso1.setVisibility(View.GONE);
        estaPaso2=1;
    }

    private void volverPaso1(){
        Animation entraIzquierda = AnimationUtils.loadAnimation(this, R.anim.entrar_izquierda);
        lnTotalPaso1.setVisibility(View.VISIBLE);
        lnTotalPaso1.startAnimation(entraIzquierda);

        Animation saleDerecha = AnimationUtils.loadAnimation(this, R.anim.salir_derecha);
        lnTotalPaso2.startAnimation(saleDerecha);
        lnTotalPaso2.setVisibility(View.GONE);

        estaPaso2=0;
    }

    //region confirmar datos
    private void confirmarDatos(){
        SharedPreferences datosLocales =  actActual.getSharedPreferences("datosAplicacion", actActual.MODE_PRIVATE);
        final String regID = datosLocales.getString("senderid", "");
        if (regID.equals("")){
            dialog = new Dialog(actActual);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dlg_modal_error);
            TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
            textMsg1.setText("Todavía no se puede enviar su solicitud de validación de identidad sin D.N.I. porque falta registrar su dispositivo.\nPor favor reintente en unos minutos.");
            LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
            lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return;
        }
        final String IMEI=funciones.obtenerIMEI(actActual);
        final String imagenBase64 = saveBitmapToBase64(imgSubir1);

        int tipoIndex = this.spinner.getSelectedIndex();
        final String tipoDocumento=(String) this.spinner.getItems().get(tipoIndex);
        final String telefono=edtTelefono.getText().toString();
        final String mail=edtMail.getText().toString();

        try {
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dlg_modal_buscando);
            TextView textMsg = (TextView) dialog.findViewById(R.id.lblMsg);
            textMsg.setText("Almacenando los datos");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = getString(R.string.srvLocProduccion);

            url=url+"APPVerificaLoginSoyAfiliadoSinDocumento";
            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            try {
                                Document valores = funciones.getDomElement(response);
                                String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                                if (valor.substring(0, 2).equals("00")) {
                                    String idPedidoSinDocumentacion= valores.getElementsByTagName("idPedido").item(0).getTextContent();
                                    String fechaInicioActivacion= valores.getElementsByTagName("fechaInicio").item(0).getTextContent();

                                    SharedPreferences datosLocales = getSharedPreferences("datosAplicacion", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = datosLocales.edit();
                                    editor.putString("idPedidoSinDocumentacion", idPedidoSinDocumentacion);
                                    editor.putString("fechaInicioActivacion", fechaInicioActivacion);
                                    editor.putString("numTelActivacion", telefono);
                                    editor.putString("emailActivacion", mail);
                                    editor.commit();

                                    SQLController database= new SQLController(actActual);
                                    database.actualizarEstadoRegistro("ESPACTIVACION");

                                    String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                    dialog = new Dialog(actActual);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.setContentView(R.layout.dlg_modal_aviso);
                                    TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                    textMsg1.setText(msgAviso);
                                    LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
                                    lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                            Intent inent = new Intent(getBaseContext(), soy_afiliado_sin_dni_activacion.class);
                                            startActivity(inent);
                                            finishAffinity();
                                        }
                                    });
                                    dialog.show();
                                    return;
                                } else if (valor.substring(0, 2).equals("01")) {
                                    String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                    dialog = new Dialog(actActual);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.setContentView(R.layout.dlg_modal_error);
                                    TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                    textMsg1.setText(msgAviso);
                                    LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
                                    lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                    return;
                                } else {
                                    dialog = new Dialog(actActual);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.setContentView(R.layout.dlg_modal_error);
                                    TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                    textMsg1.setText("Error al solicitar la verificación sin D.N.I.:\n\tCódigo de retorno inválido");
                                    LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
                                    lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                }
                            }catch (Exception errEx){
                                dialog = new Dialog(actActual);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                textMsg1.setText("Error desconocido al solicitar la verificación sin D.N.I.:\n\t"+errEx.getMessage());
                                LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialog.dismiss();
                            if (error.getClass().equals(TimeoutError.class)) {
                                dialog = new Dialog(actActual);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg = (TextView) dialog.findViewById(R.id.lblMsg);
                                textMsg.setText(R.string.msgErrorConexion);
                                LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }else if (error.getClass().equals(ServerError.class)) {
                                dialog = new Dialog(actActual);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                textMsg1.setText(R.string.msgErrorServidor);
                                LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }else if (error.getClass().equals(NoConnectionError.class)) {
                                dialog = new Dialog(actActual);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                textMsg1.setText(R.string.msgErrorSinConexion);
                                LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                            else
                            {
                                dialog = new Dialog(actActual);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                textMsg1.setText("Error desconocido solicitar la verificación sin D.N.I.:\n"+error);
                                LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                            return;
                        }
                    }) {

                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("administradora", "AND");
                    params.put("tipoDocumento", tipoDocumento);
                    params.put("imagen1", imagenBase64);
                    params.put("numCelular", telefono);
                    params.put("email", mail);
                    params.put("regID", regID);
                    params.put("IMEI", IMEI);
                    params.put("idPedido", "00000000-0000-0000-0000-000000000000");
                    return params;
                }
            };
            // Add the request to the RequestQueue.
            req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(req);
        }catch (Exception ex) {
            String a="";
        }
    }

    public String saveBitmapToBase64(Bitmap bm) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
        return Base64.encodeToString(bao.toByteArray(), 2);
    }
    //endregion

    //region otros
    @Override
    public void onBackPressed() {
        if (this.estaPaso2==1){
            volverPaso1();
            estaPaso2=0;
            return;
        }
        super.onBackPressed();
        actActual.overridePendingTransition(R.anim.entrar_izquierda, R.anim.salir_derecha);
    }
    //endregion
}
