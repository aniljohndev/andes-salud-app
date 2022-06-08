package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_reclamos_sugerencia extends Fragment {

    FragmentChangeTrigger trigger;
    EditText txtReclamoSugerencia;
    RadioButton rdoReclamo, rdoSugerencia;
    private Dialog dialogo;

    public fragmento_reclamos_sugerencia() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_reclamos_sugerencia, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView btnEnviar = (ImageView)view.findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarReclamo();
            }
        });

        txtReclamoSugerencia=(EditText) view.findViewById(R.id.txtReclamoSugerencia);
        rdoReclamo = (RadioButton) view.findViewById(R.id.rdoReclamo);
        rdoSugerencia = (RadioButton) view.findViewById(R.id.rdoSugerencia);
    }

    private void enviarReclamo(){
        if (!rdoReclamo.isChecked()
            && !rdoSugerencia.isChecked()) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Falta indicar si es un reclamo o una sugerencia");
            return;
        }
        if (txtReclamoSugerencia.getText().toString().matches("")) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "El mensaje no pude estar en blanco");
            return;
        }
        String tipo="";
        if (rdoReclamo.isChecked()){
            tipo="RECLAMO";
        }else{
            tipo = "SUGERENCIA";
        }

        try{
            SQLController database = new SQLController(getContext());
            final String idAfiliadoTitular = database.obtenerIDAfiliado();
            if (idAfiliadoTitular.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Enviando el reclamo o sugerencia");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliado", idAfiliadoTitular);
            params.put("tipo", tipo);
            params.put("comentario", txtReclamoSugerencia.getText().toString());
            params.put("origenReclamo", "APP");

            fabrica_WS.APPAgregarReclamo(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgAviso(getContext(), valores.getElementsByTagName("mensaje").item(0).getTextContent());
                    LinearLayout lnMsgTotal=(LinearLayout)dialogo.findViewById(R.id.lnMsgTotal);
                    lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogo.dismiss();
                            trigger.fireChange("contactCenter_btnTerminoReclamoSugerencia");
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
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al enviar el reclamo/sugerencia:\n" + e.getMessage());
        }
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
