package ar.com.andessalud.andes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
//import android.support.design.widget.TextInputEditText;
//import android.support.v7.app.AppCompatActivity;
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
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class soy_afiliado_identidad_valida extends AppCompatActivity {

    Activity actActual = this;
    private Dialog dialog;
    String txtMensaje, usuario, pass, idAfiliadoRegistro;
    TextView txtFraseInicio;
    EditText edtUsuario;
    TextInputEditText edtPassword, edtPasswordRepetir;
    LinearLayout lnConfimar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actActual.overridePendingTransition(R.anim.entrar_derecha, R.anim.salir_izquierda);
        setContentView(R.layout.soy_afiliado_identidad_valida);

        mostrarMenuRedesSociales();

        SharedPreferences prefs = getSharedPreferences("datosAplicacion", MODE_PRIVATE);
        idAfiliadoRegistro = prefs.getString("idAfiliadoRegistro", "");
        txtMensaje = prefs.getString("mensajeUsuYPass", "");
        usuario = prefs.getString("usuarioUsuYPass", "");
        pass = prefs.getString("passUsuYPass", "");

        txtFraseInicio = (TextView) findViewById(R.id.txtFraseInicio);
        txtFraseInicio.setText(txtMensaje);

        edtUsuario = (EditText) findViewById(R.id.edtUsuario);
        edtPassword = (TextInputEditText) findViewById(R.id.edtPassword);
        edtPasswordRepetir = (TextInputEditText) findViewById(R.id.edtPasswordRepetir);

        if (!usuario.equals("---")) {
            edtUsuario.setText(usuario);
            edtPassword.setText(pass);
            edtPasswordRepetir.setText(pass);
        }

        lnConfimar = (LinearLayout) findViewById(R.id.lnConfimar);
        lnConfimar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                almacenarUsuarioYPass();
            }
        });
    }

    private void mostrarMenuRedesSociales() {
        Bundle args = new Bundle();
        args.putString("muestraContactCenter", "NO");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment_menu_sociales contentFragment = new fragment_menu_sociales();
        contentFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .add(R.id.frg_sociales, contentFragment)
                .commit();
    }


    //region almacenar usuario y pass
    private void almacenarUsuarioYPass() {
        try {

            final String _edtUsuario = edtUsuario.getText().toString();
            final String _edtPassword = edtPassword.getText().toString();
            String _edtPasswordRepetir = edtPasswordRepetir.getText().toString();

            if (!_edtPassword.equals(_edtPasswordRepetir)) {
                dialog = new Dialog(actActual);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dlg_modal_error);
                TextView textMsg = (TextView) dialog.findViewById(R.id.lblMsg);
                textMsg.setText("La contraseña y su verificación no coinciden");
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

            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dlg_modal_buscando);
            TextView textMsg = (TextView) dialog.findViewById(R.id.lblMsg);
            textMsg.setText("Almacenando sus datos de conexión");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            if (idAfiliadoRegistro.equals("")) {
                dialog.dismiss();
                dialog = new Dialog(actActual);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dlg_modal_error);
                TextView textMsgError = (TextView) dialog.findViewById(R.id.lblMsg);
                textMsgError.setText("Error al buscar la identificación del afiliado durante el registro");
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

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = getString(R.string.srvLocProduccion);

            url = url + "APPVerificaLoginSoyAfiliadoAlmacenarUsuario";
            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            try {
                                Document valores = funciones.getDomElement(response);
                                String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                                if (valor.substring(0, 2).equals("00")) {

                                    SQLController database = new SQLController(actActual);
                                    database.actualizarEstadoRegistro("YAINGRESO");
                                    database.actualizarDatosIDAfiliado(idAfiliadoRegistro);

                                    Intent intent = new Intent(getBaseContext(), principal.class);
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
                                    LinearLayout lnMsgTotal = (LinearLayout) dialog.findViewById(R.id.lnMsgTotal);
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
                                    LinearLayout lnMsgTotal = (LinearLayout) dialog.findViewById(R.id.lnMsgTotal);
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
                                    textMsg1.setText(msgTitulo + "\n" + msgAviso);
                                    LinearLayout lnMsgTotal = (LinearLayout) dialog.findViewById(R.id.lnMsgTotal);
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
                                    LinearLayout lnMsgTotal = (LinearLayout) dialog.findViewById(R.id.lnMsgTotal);
                                    lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                }
                            } catch (Exception errEx) {
                                dialog = new Dialog(actActual);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                textMsg1.setText("Error desconocido al verificar el D.N.I.:\n\t" + errEx.getMessage());
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
                                LinearLayout lnMsgTotal = (LinearLayout) dialog.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            } else if (error.getClass().equals(ServerError.class)) {
                                dialog = new Dialog(actActual);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                textMsg1.setText(R.string.msgErrorServidor);
                                LinearLayout lnMsgTotal = (LinearLayout) dialog.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                dialog = new Dialog(actActual);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                textMsg1.setText(R.string.msgErrorSinConexion);
                                LinearLayout lnMsgTotal = (LinearLayout) dialog.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            } else {
                                dialog = new Dialog(actActual);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                textMsg1.setText("Error desconocido al enviar el registro:\n" + error);
                                LinearLayout lnMsgTotal = (LinearLayout) dialog.findViewById(R.id.lnMsgTotal);
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
                    params.put("idAfiliado", idAfiliadoRegistro);
                    params.put("usuario", _edtUsuario);
                    params.put("pass", _edtPassword);
                    return params;
                }
            };
            // Add the request to the RequestQueue.
            req.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(40), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(req);
        } catch (Exception ex) {
            dialog.dismiss();
            String a = ex.getMessage();
        }
    }
    //endregion

    //region otros
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        actActual.overridePendingTransition(R.anim.entrar_izquierda, R.anim.salir_derecha);
    }
}
//endregion