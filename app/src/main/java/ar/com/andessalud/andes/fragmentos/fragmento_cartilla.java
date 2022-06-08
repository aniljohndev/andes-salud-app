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
public class fragmento_cartilla extends Fragment implements View.OnClickListener{

    ImageView btnCartillaMedica, btnCartillaFarmacia;
    FragmentChangeTrigger trigger;

    public fragmento_cartilla() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_cartilla, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCartillaMedica = (ImageView)view.findViewById(R.id.btnCartillaMedica);
        btnCartillaMedica.setOnClickListener(this);
        btnCartillaFarmacia = (ImageView)view.findViewById(R.id.btnCartillaFarmacia);
        btnCartillaFarmacia.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnCartillaMedica){
            trigger.fireChange("cartilla_btnCartillaMedica");
        }else if(v.getId()==R.id.btnCartillaFarmacia){
            trigger.fireChange("cartilla_btnCartillaFarmacia");
        }
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
