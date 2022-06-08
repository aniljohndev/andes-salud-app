package ar.com.andessalud.andes.fragmentos;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.R;

public class fragmento_prestador_reportes extends Fragment implements View.OnClickListener{

    LinearLayout lnRptPresOdontologicas, imvInformes;
    FragmentChangeTrigger trigger;

    public fragmento_prestador_reportes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_prestador_reportes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lnRptPresOdontologicas= (LinearLayout)view.findViewById(R.id.lnRptPresOdontologicas);
        lnRptPresOdontologicas.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.lnRptPresOdontologicas){
            trigger.fireChange("principal_prestador_rpt_prestaciones_odontologicas");
        }
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}