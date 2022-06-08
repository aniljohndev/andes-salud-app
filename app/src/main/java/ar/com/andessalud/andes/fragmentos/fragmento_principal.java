package ar.com.andessalud.andes.fragmentos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Dimension;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ogaclejapan.arclayout.ArcLayout;

import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.funciones;

public class fragmento_principal extends Fragment implements View.OnClickListener{
    public fragmento_principal() {
        // Required empty public constructor
    }

    ImageView imvOrdenes, imvEmergencias, imvEstudiosMedicos, imvCredenCiales
            ,imvTramites, imvTurnos, imvCatilla, imvDrOnline, smileface;

    //Adding by discover
    ImageView imPharmacy;

    ArcLayout arc_layout;

    FragmentChangeTrigger trigger;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_principal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imPharmacy = (ImageView)view.findViewById(R.id.imPharmacy);
        imPharmacy.setOnClickListener(this);

        smileface = (ImageView)view.findViewById(R.id.smileface);
        imvOrdenes = (ImageView)view.findViewById(R.id.imvOrdenes);
        imvOrdenes.setOnClickListener(this);
        imvEmergencias = (ImageView)view.findViewById(R.id.imvEmergencias);
        imvEmergencias.setOnClickListener(this);
        imvEstudiosMedicos = (ImageView)view.findViewById(R.id.imvEstudiosMedicos);
        imvEstudiosMedicos.setOnClickListener(this);
        imvCredenCiales = (ImageView)view.findViewById(R.id.imvCredenCiales);
        imvCredenCiales.setOnClickListener(this);
        imvTurnos = (ImageView)view.findViewById(R.id.imvTurnos);
        imvTurnos.setOnClickListener(this);
        imvTramites = (ImageView)view.findViewById(R.id.imvTramites);
        imvTramites.setOnClickListener(this);
        imvCatilla = (ImageView)view.findViewById(R.id.imvCatilla);
        imvCatilla.setOnClickListener(this);
        imvDrOnline = (ImageView)view.findViewById(R.id.imvDrOnline);
        imvDrOnline.setOnClickListener(this);
        String yaRegistroID= funciones.verificaSenderID(getActivity(),3);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int anchoImagen=(int)(width/4.6);
        int altoImagen=(int)(height/4.6);
        int anchoImagenAndes=(int)(width/3.5);
        int altoImagenAndes=(int)(height/3.5);

        smileface.getLayoutParams().width =anchoImagenAndes;
        smileface.getLayoutParams().height = altoImagenAndes;
        imvOrdenes.getLayoutParams().width =anchoImagen;
        imvOrdenes.getLayoutParams().height = altoImagen;
        imvEmergencias.getLayoutParams().width =anchoImagen;
        imvEmergencias.getLayoutParams().height = altoImagen;
        imvEmergencias.requestLayout();
        imvEstudiosMedicos.getLayoutParams().width =anchoImagen;
        imvEstudiosMedicos.getLayoutParams().height = altoImagen;
        imvCredenCiales.getLayoutParams().width =anchoImagen;
        imvCredenCiales.getLayoutParams().height = altoImagen;
        imvTurnos.getLayoutParams().width =anchoImagen;
        imvTurnos.getLayoutParams().height = altoImagen;
        imvTramites.getLayoutParams().width =anchoImagen;
        imvTramites.getLayoutParams().height = altoImagen;
        imvCatilla.getLayoutParams().width =anchoImagen;
        imvCatilla.getLayoutParams().height = altoImagen;
        imvDrOnline.requestLayout();
        imvDrOnline.getLayoutParams().width =anchoImagen;
        imvDrOnline.getLayoutParams().height = altoImagen;
        imvDrOnline.setScaleType(ImageView.ScaleType.FIT_CENTER);

        int anchoArco=(int)(width/2.8);
        arc_layout=(ArcLayout)view.findViewById(R.id.arc_layout);
        arc_layout.setAxisRadius(anchoArco);
    }

    @Override
    public void onClick(View v) {
        //Adding by discover

        if (v.getId() == R.id.imPharmacy)
        {
//            Toast.makeText(getContext(),"Próximamente, en breve, pronto",Toast.LENGTH_LONG).show();
            trigger.fireChange("principle_btnpharmacyChange");
        }
        else if(v.getId()==R.id.imvOrdenes){
            trigger.fireChange("principal_btnOrdenesEstudios");
        }else if(v.getId()==R.id.imvEmergencias){
            trigger.fireChange("principal_btnEmergencias");
        }else if(v.getId()==R.id.imvEstudiosMedicos){
            trigger.fireChange("principal_btnEstudiosMedicos");
        }else if(v.getId()==R.id.imvCredenCiales){
            trigger.fireChange("principal_btnCredencial");
        }else if(v.getId()==R.id.imvTurnos){
            trigger.fireChange("principal_btnTurnos");
        } else if(v.getId()==R.id.imvTramites){
            trigger.fireChange("principal_btnTramites");
        }else if(v.getId()==R.id.imvCatilla){
            trigger.fireChange("principal_btnCartilla");
        }else if(v.getId()==R.id.imvDrOnline){
            trigger.fireChange("principal_btnDrOnline");
        }

    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}
