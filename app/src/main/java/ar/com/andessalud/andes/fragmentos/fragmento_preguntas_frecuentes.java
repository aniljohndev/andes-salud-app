package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.funciones;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_preguntas_frecuentes extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo;
    LinearLayout listaPreguntas, lnRespuesta1, ultLnRespuesta;

    public fragmento_preguntas_frecuentes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_preguntas_frecuentes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listaPreguntas = (LinearLayout)view.findViewById(R.id.listaPreguntas);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        buscarPreguntas();
                    }
                },
                1000);
    }

    private void buscarPreguntas() {
        try {
            SQLController database = new SQLController(getContext());
            String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando las preguntas frecuentes");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", database.obtenerIDAfiliado());

            fabrica_WS.APPObtenerPreguntasFrecuentes(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] idPregunta = new String[valores.getElementsByTagName("idPregunta").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idPregunta").getLength(); i++) {
                        idPregunta[i] = valores.getElementsByTagName("idPregunta").item(i).getTextContent();
                    }
                    String[] pregunta = new String[valores.getElementsByTagName("pregunta").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("pregunta").getLength(); i++) {
                        pregunta[i] = valores.getElementsByTagName("pregunta").item(i).getTextContent();
                    }
                    mostrarPreguntas(idPregunta, pregunta);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        } catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar las preguntas frecuentes\n"+ex.getMessage());
        }
    }

    private void mostrarPreguntas(String[] idPregunta, String[] pregunta) {
        try {
            listaPreguntas.removeAllViews();
            for (int i = 0; i < idPregunta.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnPregunta = inflater.inflate(R.layout.lista_pregunta_frecuente, null);

                final String _idPregunta=idPregunta[i];
                final String _pregunta=pregunta[i];
                final LinearLayout lnRespuestaActual=lnPregunta.findViewById(R.id.lnRespuesta1);

                TextView idFila = (TextView) lnPregunta.findViewById(R.id.idFila);
                TextView linea1 = (TextView) lnPregunta.findViewById(R.id.linea1);

                idFila.setText(_idPregunta);
                linea1.setText(_pregunta);

                lnPregunta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ultLnRespuesta!=null){
                            TextView txtRespuesta= (TextView) ultLnRespuesta.findViewById(R.id.txtRespuesta);
                            txtRespuesta.setVisibility(View.GONE);
                            if (ultLnRespuesta == lnRespuestaActual){
                                ultLnRespuesta=null;
                                return;
                            }
                        }
                        buscarRespuesta(_idPregunta, v);
                    }
                });
                listaPreguntas.addView(lnPregunta);
            }
            listaPreguntas.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar las preguntas frecuentes:\n" + e.getMessage());
        }
    }

    private void buscarRespuesta(final String idActual, View itemAct) {
        try {
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando la respuesta");
            SQLController database = new SQLController(getContext());

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", database.obtenerIDAfiliado());
            params.put("idPregunta", idActual);

            fabrica_WS.APPObtenerRespuestaFrecuente(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    TextView txtRespuesta= (TextView) itemAct.findViewById(R.id.txtRespuesta);
                    txtRespuesta.setText(valores.getElementsByTagName("respuesta").item(0).getTextContent());
                    txtRespuesta.setVisibility(View.VISIBLE);
                    ultLnRespuesta= (LinearLayout) itemAct.findViewById(R.id.lnRespuesta1);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        } catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar la respuesta\n"+ex.getMessage());
        }
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
