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
import android.text.Html;
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

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class soy_afiliado_sin_dni_activacion extends AppCompatActivity {

    final Activity actActual=this;
    private Dialog dialog, dialogCambiaContacto;
    TextView lblDatos, lblInicio, lblTelefono, lblMail;
    static SharedPreferences appPref;
    LinearLayout lnCambiarDatosContacto, lnTengoDNI;
    String numTelContacto, emailContacto, idPedido;
    public static Activity activityEsperandoActivacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actActual.overridePendingTransition(R.anim.entrar_derecha, R.anim.salir_izquierda);
        setContentView(R.layout.soy_afiliado_sin_dni_activacion);

        activityEsperandoActivacion = this;
        mostrarMenuRedesSociales();

        lblDatos = (TextView) findViewById(R.id.lblDatos);
        lblInicio = (TextView) findViewById(R.id.lblInicio);
        lblTelefono = (TextView) findViewById(R.id.lblTelefono);
        lblMail = (TextView) findViewById(R.id.lblMail);

        appPref = actActual.getSharedPreferences("datosAplicacion", MODE_PRIVATE);
        String htmlString="<b><u>Datos de activación:</u></b>";
        lblDatos.setText(Html.fromHtml(htmlString));
        htmlString="<b>Inicio: </b>"+appPref.getString("fechaInicioActivacion", "---");
        lblInicio.setText(Html.fromHtml(htmlString));

        numTelContacto=appPref.getString("numTelActivacion", "---");
        htmlString="<b>Cel.: </b>"+numTelContacto;
        lblTelefono.setText(Html.fromHtml(htmlString));

        emailContacto=appPref.getString("emailActivacion", "---");
        htmlString="<b>Mail: </b>"+emailContacto;
        lblMail.setText(Html.fromHtml(htmlString));

        lnCambiarDatosContacto = (LinearLayout) findViewById(R.id.lnCambiarDatosContacto);
        lnCambiarDatosContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiaContactos();
            }
        });

        lnTengoDNI = (LinearLayout) findViewById(R.id.lnTengoDNI);
        lnTengoDNI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tengoDNI();
            }
        });

        idPedido=appPref.getString("idPedidoSinDocumentacion", "---");
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

    //region cambiaContactos
    private void cambiaContactos(){
        dialog = new Dialog(actActual);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dlg_modal_activacion_contactos);
        EditText edtTelefono = (EditText) dialog.findViewById(R.id.edtTelefono);
        edtTelefono.setText(numTelContacto);
        EditText edtMail = (EditText) dialog.findViewById(R.id.edtMail);
        edtMail.setText(emailContacto);
        ImageView btnSi = (ImageView) dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                almacenaDatosContacto();
            }
        });
        ImageView btnNo = (ImageView) dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return;
    }

    private void almacenaDatosContacto(){
        final String telefono= ((EditText) dialog.findViewById(R.id.edtTelefono)).getText().toString();
        final String mail= ((EditText) dialog.findViewById(R.id.edtMail)).getText().toString();

        try {
            dialogCambiaContacto = new Dialog(this);
            dialogCambiaContacto.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialogCambiaContacto.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogCambiaContacto.setContentView(R.layout.dlg_modal_buscando);
            TextView textMsg = (TextView) dialogCambiaContacto.findViewById(R.id.lblMsg);
            textMsg.setText("Almacenando los datos");
            dialogCambiaContacto.setCancelable(false);
            dialogCambiaContacto.setCanceledOnTouchOutside(false);
            dialogCambiaContacto.show();

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = getString(R.string.srvLocProduccion);

            url=url+"APPVerificaLoginSoyAfiliadoSinDocumento";
            StringRequest req = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialogCambiaContacto.dismiss();
                            try {
                                Document valores = funciones.getDomElement(response);
                                String valor = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                                if (valor.substring(0, 2).equals("00")) {
                                    String idPedidoSinDocumentacion= valores.getElementsByTagName("idPedido").item(0).getTextContent();
                                    String fechaInicioActivacion= valores.getElementsByTagName("fechaInicio").item(0).getTextContent();

                                    SharedPreferences datosLocales = getSharedPreferences("datosAplicacion", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = datosLocales.edit();
                                    editor.putString("numTelActivacion", telefono);
                                    editor.putString("emailActivacion", mail);
                                    editor.commit();

                                    String htmlString="<b>Cel.: </b>"+telefono;
                                    lblTelefono.setText(Html.fromHtml(htmlString));

                                    emailContacto=appPref.getString("emailActivacion", "---");
                                    htmlString="<b>Mail: </b>"+mail;
                                    lblMail.setText(Html.fromHtml(htmlString));

                                    dialog.dismiss();

                                    dialog = new Dialog(actActual);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.setContentView(R.layout.dlg_modal_aviso);
                                    TextView textMsg1 = (TextView) dialog.findViewById(R.id.lblMsg);
                                    textMsg1.setText("Sus datos se actualizaron correctamente.");
                                    LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
                                    lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                    return;
                                } else if (valor.substring(0, 2).equals("01")) {
                                    String msgAviso = valores.getElementsByTagName("mensaje").item(0).getTextContent();
                                    dialogCambiaContacto = new Dialog(actActual);
                                    dialogCambiaContacto.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                    dialogCambiaContacto.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialogCambiaContacto.setContentView(R.layout.dlg_modal_error);
                                    TextView textMsg1 = (TextView) dialogCambiaContacto.findViewById(R.id.lblMsg);
                                    textMsg1.setText(msgAviso);
                                    LinearLayout lnMsgTotal=(LinearLayout)dialogCambiaContacto.findViewById(R.id.lnMsgTotal);
                                    lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogCambiaContacto.dismiss();
                                        }
                                    });
                                    dialogCambiaContacto.show();
                                    return;
                                } else {
                                    dialogCambiaContacto = new Dialog(actActual);
                                    dialogCambiaContacto.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                    dialogCambiaContacto.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialogCambiaContacto.setContentView(R.layout.dlg_modal_error);
                                    TextView textMsg1 = (TextView) dialogCambiaContacto.findViewById(R.id.lblMsg);
                                    textMsg1.setText("Error al solicitar la verificación sin D.N.I.:\n\tCódigo de retorno inválido");
                                    LinearLayout lnMsgTotal=(LinearLayout)dialogCambiaContacto.findViewById(R.id.lnMsgTotal);
                                    lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                }
                            }catch (Exception errEx){
                                dialogCambiaContacto = new Dialog(actActual);
                                dialogCambiaContacto.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialogCambiaContacto.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogCambiaContacto.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialogCambiaContacto.findViewById(R.id.lblMsg);
                                textMsg1.setText("Error desconocido al solicitar la verificación sin D.N.I.:\n\t"+errEx.getMessage());
                                LinearLayout lnMsgTotal=(LinearLayout)dialogCambiaContacto.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogCambiaContacto.dismiss();
                                    }
                                });
                                dialogCambiaContacto.show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialogCambiaContacto.dismiss();
                            if (error.getClass().equals(TimeoutError.class)) {
                                dialogCambiaContacto = new Dialog(actActual);
                                dialogCambiaContacto.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialogCambiaContacto.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogCambiaContacto.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg = (TextView) dialogCambiaContacto.findViewById(R.id.lblMsg);
                                textMsg.setText(R.string.msgErrorConexion);
                                LinearLayout lnMsgTotal=(LinearLayout)dialogCambiaContacto.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogCambiaContacto.dismiss();
                                    }
                                });
                                dialogCambiaContacto.show();
                            }else if (error.getClass().equals(ServerError.class)) {
                                dialogCambiaContacto = new Dialog(actActual);
                                dialogCambiaContacto.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialogCambiaContacto.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogCambiaContacto.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialogCambiaContacto.findViewById(R.id.lblMsg);
                                textMsg1.setText(R.string.msgErrorServidor);
                                LinearLayout lnMsgTotal=(LinearLayout)dialogCambiaContacto.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                dialogCambiaContacto.show();
                            }else if (error.getClass().equals(NoConnectionError.class)) {
                                dialogCambiaContacto = new Dialog(actActual);
                                dialogCambiaContacto.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialogCambiaContacto.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogCambiaContacto.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialogCambiaContacto.findViewById(R.id.lblMsg);
                                textMsg1.setText(R.string.msgErrorSinConexion);
                                LinearLayout lnMsgTotal=(LinearLayout)dialogCambiaContacto.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogCambiaContacto.dismiss();
                                    }
                                });
                                dialogCambiaContacto.show();
                            }
                            else
                            {
                                dialogCambiaContacto = new Dialog(actActual);
                                dialogCambiaContacto.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                                dialogCambiaContacto.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogCambiaContacto.setContentView(R.layout.dlg_modal_error);
                                TextView textMsg1 = (TextView) dialogCambiaContacto.findViewById(R.id.lblMsg);
                                textMsg1.setText("Error desconocido solicitar la verificación sin D.N.I.:\n"+error);
                                LinearLayout lnMsgTotal=(LinearLayout)dialogCambiaContacto.findViewById(R.id.lnMsgTotal);
                                lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogCambiaContacto.dismiss();
                                    }
                                });
                                dialogCambiaContacto.show();
                            }
                            return;
                        }
                    }) {

                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("administradora", "AND");
                    params.put("tipoDocumento", "");
                    params.put("imagen1", "");
                    params.put("numCelular", telefono);
                    params.put("email", mail);
                    params.put("regID", "");
                    params.put("IMEI", "");
                    params.put("idPedido", idPedido);
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
    //endregion

    //region tengo documento
    private void tengoDNI(){
        Intent intent = new Intent(getBaseContext(), soy_afiliado_identidad.class);
        intent.putExtra("idPedido", idPedido);
        startActivity(intent);
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
