package ar.com.andessalud.andes.fabricas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.fragmento_drawer;
import ar.com.andessalud.andes.principal;
import ar.com.andessalud.andes.splash;

public class fabrica_menu extends Fragment{
    FragmentChangeTrigger trigger;

    public fabrica_menu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*imvInteracion = (ImageView)view.findViewById(R.id.imvInteracion);
        imvInteracion.setOnClickListener(this);
        imvFormularios = (ImageView)view.findViewById(R.id.imvFormularios);
        imvFormularios.setOnClickListener(this);
        imvDocumentacion = (ImageView)view.findViewById(R.id.imvDocumentacion);
        imvDocumentacion.setOnClickListener(this);
        imvReintegros = (ImageView)view.findViewById(R.id.imvReintegros);
        imvReintegros.setOnClickListener(this);
        imvIncorporar = (ImageView)view.findViewById(R.id.imvIncorporar);
        imvIncorporar.setOnClickListener(this);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_sidemenu, container, false);
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }

    /*int nextScreenInfo;
    Activity activity;
    fragmento_drawer fragment;
    FragmentChangeTrigger trigger;
    principal pantPrincipal;

    public fabrica_menu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public fabrica_menu (Activity activity, fragmento_drawer fragment){
        nextScreenInfo = 0;
        this.activity = activity;
        this.fragment = fragment;
        pantPrincipal=new principal();
    }

    public void setNextScreenInfo(int iNextScreenInfo) {
        this.nextScreenInfo = iNextScreenInfo;
    }

    @Override
    public View makeView() {
        if(nextScreenInfo == 0){
            return makeFragmentEntryView();
        }else if(nextScreenInfo == 1){
            return makeFragmentEntryView();
        }
        return null;
    }

    View makeFragmentEntryView(){
        View v = LayoutInflater.from(activity).inflate(R.layout.layout_sidemenu, null, false);
        LinearLayout btnMiCatilla = (LinearLayout)v.findViewById(R.id.btnMiCatilla);
        btnMiCatilla.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intentMiCartilla = new Intent(activity, principal.class);
                fragment.startActivity(intentMiCartilla);
            }
        });
        ImageView btnCloseMenu = (ImageView)v.findViewById(R.id.btnCloseMenu);
        btnCloseMenu.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                fragment.sideMenuClose();
                SQLController database= new SQLController(activity);
                database.actualizarEstadoRegistro("INICIO");
                Intent i = new Intent(fragment.getActivity(), splash.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                fragment.getActivity().startActivity(i);
            }
        });
        LinearLayout btnMiPerfil = (LinearLayout)v.findViewById(R.id.btnMiPerfil);
        btnMiPerfil.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                fragment.switchNextView(1);
            }
        });

        ImageView btnSedes = (ImageView)v.findViewById(R.id.btnSedes);
        btnSedes.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                fragment.sideMenuClose();
                //pantPrincipal.fireChange("menu_lateral_btnCartillaMedica");
                trigger.fireChange("menu_lateral_btnCartillaMedica");
                //fragment.switchNextView(2);
            }
        });
        return v;
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }*/

    /*View makeFragmentMiPerfilView(){
        View v = LayoutInflater.from(activity).inflate(R.layout.layout_sidemenu_mi_perfil, null, false);
        ImageView btnUpdateProfile = (ImageView)v.findViewById(R.id.btnUpdateProfile);
        btnUpdateProfile.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent updateProfileIntent = new Intent(activity, UpdateContactActivity.class);
                fragment.startActivity(updateProfileIntent);
            }
        });
        ImageView btnShowProfile = (ImageView)v.findViewById(R.id.btnShowProfile);
        btnShowProfile.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                *//*Intent showProfileIntent = new Intent(activity, estado_afiliacion.class);
                fragment.startActivity(showProfileIntent);*//*
            }
        });
        return v;
    }*/
}