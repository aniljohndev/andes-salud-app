package ar.com.andessalud.andes.fragmentos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.R;

public class fragmento_tramites extends Fragment implements View.OnClickListener{

    FragmentChangeTrigger trigger;
    ImageView imvInteracion, imvFormularios, imvDocumentacion, imvReintegros, imvIncorporar;

    public fragmento_tramites() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imvInteracion = (ImageView)view.findViewById(R.id.imvInteracion);
        imvInteracion.setOnClickListener(this);
        imvFormularios = (ImageView)view.findViewById(R.id.imvFormularios);
        imvFormularios.setOnClickListener(this);
        imvDocumentacion = (ImageView)view.findViewById(R.id.imvDocumentacion);
        imvDocumentacion.setOnClickListener(this);
        imvReintegros = (ImageView)view.findViewById(R.id.imvReintegros);
        imvReintegros.setOnClickListener(this);
        imvIncorporar = (ImageView)view.findViewById(R.id.imvIncorporar);
        imvIncorporar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imvInteracion){
            trigger.fireChange("tramites_btnInternacion");
        }else if(v.getId()==R.id.imvFormularios){
            trigger.fireChange("tramites_btnFormularios");
        }else if(v.getId()==R.id.imvDocumentacion){
            trigger.fireChange("tramites_btnDocumentacion");
        }else if(v.getId()==R.id.imvReintegros){
            trigger.fireChange("tramites_btnReintegros");
        }else if(v.getId()==R.id.imvIncorporar){
            trigger.fireChange("tramites_btnIncorporarFam");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_tramites, container, false);
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
