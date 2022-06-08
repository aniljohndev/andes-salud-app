package ar.com.andessalud.andes.fragmentos;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.R;

public class fragmento_ordenes_estudios extends Fragment implements View.OnClickListener{

    ImageView btnOrdenes, btnEstudiosMedicos;
    FragmentChangeTrigger trigger;

    public fragmento_ordenes_estudios() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_ordenes_estudios, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnOrdenes = (ImageView)view.findViewById(R.id.btnOrdenes);
        btnOrdenes.setOnClickListener(this);
        btnEstudiosMedicos = (ImageView)view.findViewById(R.id.btnEstudiosMedicos);
        btnEstudiosMedicos.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnOrdenes){
            trigger.fireChange("principal_btnOrdenes");
        }else if(v.getId()==R.id.btnEstudiosMedicos){
            trigger.fireChange("principal_btnEstudiosMedicos");
        }
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}