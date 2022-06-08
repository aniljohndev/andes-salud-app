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
public class fragmento_principal_prestador extends Fragment implements View.OnClickListener{
    ImageView imvOrdenes, imvInformes;
    FragmentChangeTrigger trigger;

    public fragmento_principal_prestador() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_principal_prestador, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imvOrdenes = (ImageView)view.findViewById(R.id.imvOrdenes);
        imvOrdenes.setOnClickListener(this);
        imvInformes = (ImageView)view.findViewById(R.id.imvInformes);
        imvInformes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imvOrdenes){
            trigger.fireChange("principal_prestador_btnOrdenes");
        }else if(v.getId()==R.id.imvInformes){
            trigger.fireChange("principal_prestador_btnInformes");
        }/* else if(v.getId()==R.id.imvTramites){
            trigger.fireChange("principal_btnTramites");
        }else if(v.getId()==R.id.imvCatilla){
            trigger.fireChange("principal_btnCartilla");
        }*/
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
