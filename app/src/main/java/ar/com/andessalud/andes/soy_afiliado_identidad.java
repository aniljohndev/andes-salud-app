package ar.com.andessalud.andes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
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

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class soy_afiliado_identidad extends AppCompatActivity {

    Activity actActual=this;
    private Dialog dialog;
    FrameLayout framAbrirScanner;
    static final int BARCODE_SCAN = 1111;
    LinearLayout lnNoTengoDNI;
    String idPedido="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actActual.overridePendingTransition(R.anim.entrar_derecha, R.anim.salir_izquierda);
        setContentView(R.layout.soy_afiliado_identidad);

        mostrarMenuRedesSociales();

        framAbrirScanner=(FrameLayout) findViewById(R.id.framAbrirScanner);
        framAbrirScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirScanner();
            }
        });

        lnNoTengoDNI=(LinearLayout) findViewById(R.id.lnNoTengoDNI);
        lnNoTengoDNI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sinDNI();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lnNoTengoDNI.setVisibility(View.GONE);
            idPedido = extras.getString("idPedido","");
        }
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

    //region DNI
    private void abrirScanner(){
        dialog = new Dialog(actActual);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dlg_modal_scanner_dni_detalle);
        ImageView btnSi=(ImageView)dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                soy_afiliado_identidad.this.startActivityForResult(new Intent(getBaseContext(), soy_afiliado_scanner.class), soy_afiliado_identidad.BARCODE_SCAN);
            }
        });
        ImageView btnNo=(ImageView)dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_SCAN && resultCode == -1) {
            verificarDNI(data.getStringExtra("codeValue"));
        }
    }

    public void verificarDNI(String codeValue) {
        try{
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dlg_modal_buscando);
            TextView textMsg = (TextView) dialog.findViewById(R.id.lblMsg);
            textMsg.setText("Verificando los datos del D.N.I.");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            //codeValue="00560822791@aleman farras@david@m@35925374@b@28/07/1992@15/08/2018@204";
            //final String _codeValue= Uri.encode(codeValue);
            final String _codeValue= codeValue;

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = getString(R.string.srvLocProduccion);

            url=url+"APPVerificaLoginSoyAfiliadoDocumento";
            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            try {
                                Document valores = funciones.getDomElement(response);
                                String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                                if (valor.substring(0, 2).equals("00")) {
                                    String idAfiliado= valores.getElementsByTagName("idAfiliado").item(0).getTextContent();
                                    SharedPreferences datosLocales = getSharedPreferences("datosAplicacion", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = datosLocales.edit();
                                    editor.putString("idAfiliadoRegistro", idAfiliado);
                                    editor.putString("mensajeUsuYPass", valores.getElementsByTagName("mensaje").item(0).getTextContent());
                                    editor.putString("usuarioUsuYPass", valores.getElementsByTagName("usuario").item(0).getTextContent());
                                    editor.putString("passUsuYPass", valores.getElementsByTagName("pass").item(0).getTextContent());
                                    editor.commit();

                                    SQLController database= new SQLController(actActual);
                                    database.actualizarEstadoRegistro("ESPUSUYPASSNUEVOS");

                                    Intent intent = new Intent(getBaseContext(), soy_afiliado_identidad_valida.class);
                                    startActivity(intent);
                                    finishAffinity();
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
                                } else if (valor.substring(0, 2).equals("02")) {
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
                                } else if (valor.substring(0, 2).equals("03")) {
                                    String msgTitulo = valores.getElementsByTagName("titulo").item(0).getTextContent();
                                    String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                    dialog = new Dialog(actActual);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.setContentView(R.layout.dlg_modal_error);
                                    TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                    textMsg1.setText(msgTitulo+"\n"+msgAviso);
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
                                    textMsg1.setText("Error al verificar el D.N.I.:\n\tCódigo de retorno inválido");
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
                                textMsg1.setText("Error desconocido al verificar el D.N.I.:\n\t"+errEx.getMessage());
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
                                textMsg1.setText("Error desconocido al enviar el registro:\n"+error);
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
                    params.put("cadena", _codeValue);
                    params.put("idPedido", idPedido);
                    return params;
                }
            };
            // Add the request to the RequestQueue.
            req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(req);
        }catch (Exception ex) {
            dialog.dismiss();
            String a=ex.getMessage();
        }
    }
    //endregion

    //region sinDNI()
    private void sinDNI(){
        Intent inent = new Intent(getBaseContext(), soy_afiliado_sin_dni.class);
        startActivity(inent);
    }
    //endregion

    //region otros
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        actActual.overridePendingTransition(R.anim.entrar_izquierda, R.anim.salir_derecha);
    }
    //endregion
}
