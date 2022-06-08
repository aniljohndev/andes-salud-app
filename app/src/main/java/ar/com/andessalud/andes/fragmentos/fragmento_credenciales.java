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

public class fragmento_credenciales extends Fragment implements View.OnClickListener{

    ImageView btnCredencialNueva, btnCredencialProvisoria;
    FragmentChangeTrigger trigger;

    public fragmento_credenciales() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_credenciales, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCredencialNueva = (ImageView)view.findViewById(R.id.btnCredencialNueva);
        btnCredencialNueva.setOnClickListener(this);
        btnCredencialProvisoria = (ImageView)view.findViewById(R.id.btnCredencialProvisoria);
        btnCredencialProvisoria.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnCredencialNueva){
            trigger.fireChange("credenciales_btnCredencialNueva");
        }else if(v.getId()==R.id.btnCredencialProvisoria){
            trigger.fireChange("credenciales_btnCredencialProvisoria");
        }
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
