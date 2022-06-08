package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
import ar.com.andessalud.andes.mostrar_archivos;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_tramites_formularios_especiales extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    LinearLayout loadingData, listaFormulariosEspeciales;
    TextView lblEstadoLoading;
    String _directorioArchivosBajar;

    public fragmento_tramites_formularios_especiales() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_tramites_formularios_especiales, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lblEstadoLoading= (TextView) view.findViewById(R.id.lblEstadoLoading);
        lblEstadoLoading.setText("Buscando los formularios especiales");
        loadingData= (LinearLayout) view.findViewById(R.id.loadingData);
        listaFormulariosEspeciales= (LinearLayout) view.findViewById(R.id.listaFormulariosEspeciales);
        ImageView imageView = view.findViewById(R.id.imgCargando);
        Glide.with(this).load(R.drawable.loading).into(imageView);
        buscarFormulariosEspeciales();
    }

    //region buscar formularios especiales
    private void buscarFormulariosEspeciales() {
        try{
            SQLController database = new SQLController(getContext());
            Map<String, String> params = new HashMap<String, String>();
            params.put("idAfiliadoTitular", database.obtenerIDAfiliado());

            fabrica_WS.APPObtenerFormulariosEspeciales(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    _directorioArchivosBajar=valores.getElementsByTagName("dirBajarDoc").item(0).getTextContent();
                    String[] nombre = new String[valores.getElementsByTagName("nombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombre").getLength(); i++) {
                        nombre[i] = valores.getElementsByTagName("nombre").item(i).getTextContent();
                    }
                    String[] descripcion = new String[valores.getElementsByTagName("descripcion").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("descripcion").getLength(); i++) {
                        descripcion[i] = valores.getElementsByTagName("descripcion").item(i).getTextContent();
                    }
                    String[] nombreArchivo = new String[valores.getElementsByTagName("nombreArchivo").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nombreArchivo").getLength(); i++) {
                        nombreArchivo[i] = valores.getElementsByTagName("nombreArchivo").item(i).getTextContent();
                    }
                    mostrarFormularios(nombre, descripcion, nombreArchivo);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    loadingData.setVisibility(View.GONE);
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception e) {
            loadingData.setVisibility(View.GONE);
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los formularios especiales:\n" + e.getMessage());
        }
    }

    private void mostrarFormularios(String[] nombre, String[] descripcion, String[] nombreArchivo) {
        try {
            listaFormulariosEspeciales.removeAllViews();
            for (int i = 0; i < nombre.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnGrupoFam = inflater.inflate(R.layout.fila_formularios_especiales, null);

                LinearLayout lnFormularioEspecial = (LinearLayout) lnGrupoFam.findViewById(R.id.lnFormularioEspecial);
                TextView nombreArchivoFila = (TextView) lnGrupoFam.findViewById(R.id.nombreArchivo);
                TextView nombreFila = (TextView) lnGrupoFam.findViewById(R.id.nombre);
                TextView descripcionFila = (TextView) lnGrupoFam.findViewById(R.id.descripcion);

                final String nombreArchivoBajar=nombreArchivo[i];

                nombreArchivoFila.setText(nombreArchivoBajar);
                nombreFila.setText(nombre[i]);
                descripcionFila.setText(descripcion[i]);

                lnFormularioEspecial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarPDF(nombreArchivoBajar, "ver");
                    }
                });
                listaFormulariosEspeciales.addView(lnGrupoFam);
            }
            listaFormulariosEspeciales.setVisibility(View.VISIBLE);
            loadingData.setVisibility(View.GONE);
        } catch (Exception e) {
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar los formularios especiales:\n" + e.getMessage());
            return;
        }
    }

    private void mostrarPDF(final String nombreArchivo, String accion) {
        Intent intent = new Intent(getContext(), mostrar_archivos.class);
        intent.putExtra("docAbrir",  nombreArchivo);
        intent.putExtra("rutaServidor",  _directorioArchivosBajar);
        intent.putExtra("mensajeError",  "No se pudo abrir la factura");
        intent.putExtra("accion",  accion);
        getContext().startActivity(intent);
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

}
