package ar.com.andessalud.andes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class soy_prestador extends AppCompatActivity {

    Activity actActual=this;
    private Dialog dialog;

    LinearLayout lnConfimar;
    EditText edtUsuario, edtContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actActual.overridePendingTransition(R.anim.entrar_derecha, R.anim.salir_izquierda);
        setContentView(R.layout.soy_prestador);

        mostrarMenuRedesSociales();

        lnConfimar=(LinearLayout) findViewById(R.id.lnConfimar);
        lnConfimar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarPrestador();
            }
        });

        edtUsuario = findViewById(R.id.edtUsuario);
        edtContrasena = findViewById(R.id.edtContrasena);

        SharedPreferences datosLocales = getSharedPreferences("datosAplicacion", MODE_PRIVATE);
        String prestUsuario = datosLocales.getString("prestadorUsuario", "");
        String prestPass = datosLocales.getString("prestadorPass", "");

        edtUsuario.setText(prestUsuario);
        edtContrasena.setText(prestPass);
        edtUsuario.setFocusableInTouchMode(true);
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
        //ImageView btnContactCenter= (ImageView) contentFragment.findViewById(R.id.btnContactCenter);
        //btnContactCenter.setVisibility(View.GONE);
    }

    private void verificarPrestador(){
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dlg_modal_buscando);
        TextView textMsg = (TextView) dialog.findViewById(R.id.lblMsg);
        textMsg.setText("Verificando datos");
        ImageView imageView = dialog.findViewById(R.id.imgCargando);
        /*load from raw folder*/
        Glide.with(this).load(R.drawable.loading).into(imageView);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final String usuario=edtUsuario.getText().toString();
        final String pass=edtContrasena.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url =getString(R.string.srvLocProduccion);
        url=url+"APPVerificaLoginPrestador";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            Document valores = funciones.getDomElement(response);
                            String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                            if (valor.substring(0, 2).equals("00")) {
                                SharedPreferences datosLocales = getSharedPreferences("datosAplicacion", MODE_PRIVATE);
                                SharedPreferences.Editor editor = datosLocales.edit();
                                editor.putString("prestadorUsuario", usuario);
                                editor.putString("prestadorPass", pass);
                                editor.commit();

                                datosPrestador.inicializaAreaPrestador();
                                datosPrestador.guardarIdUsuario(valores.getElementsByTagName("idUsuario").item(0).getTextContent());
                                datosPrestador.guardarIdConvenio(valores.getElementsByTagName("idConvenio").item(0).getTextContent());
                                for (int i = 0; i < valores.getElementsByTagName("codModulo").getLength(); i++) {
                                    datosPrestador.agregarAreaPrestador(valores.getElementsByTagName("codModulo").item(i).getTextContent());
                                }
                                Intent intPrestadorInicio = new Intent(getBaseContext(), principal_prestador.class);
                                startActivity(intPrestadorInicio);
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
                            } else {
                                dialog = new Dialog(actActual);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                textMsg1.setText("Error al validar al prestador:\n\tCódigo de retorno inválido");
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
                            textMsg1.setText("Error desconocido al al verificar el usuario y la contraseña del prestador:\n\t"+errEx.getMessage());
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
                            TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                            textMsg1.setText(R.string.msgErrorConexion);
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
                            textMsg1.setText("Error desconocido al validar al prestador:\n"+error);
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
                params.put("usuario", usuario);
                params.put("pass", pass);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        req.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    //region otros
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        actActual.overridePendingTransition(R.anim.entrar_izquierda, R.anim.salir_derecha);
    }
    //endregion
}
