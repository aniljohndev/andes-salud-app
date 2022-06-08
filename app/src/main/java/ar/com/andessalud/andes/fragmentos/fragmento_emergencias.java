package ar.com.andessalud.andes.fragmentos;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_emergencias extends Fragment implements View.OnClickListener{

    ImageView btnServicioGuardia, btnAmbulancia;
    FragmentChangeTrigger trigger;

    public fragmento_emergencias() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_emergencias, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnServicioGuardia = (ImageView)view.findViewById(R.id.btnServicioGuardia);
        btnServicioGuardia.setOnClickListener(this);
        btnAmbulancia = (ImageView)view.findViewById(R.id.btnAmbulancia);
        btnAmbulancia.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnServicioGuardia){
            trigger.fireChange("emergencias_btnServicioGuardia");
        }else if(v.getId()==R.id.btnAmbulancia){
            trigger.fireChange("emergencias_btnAmbulancia");
        }
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }

}
