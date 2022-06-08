package ar.com.andessalud.andes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.MenuCloser;

public class fragmento_drawer_prestador extends Fragment {
    MenuCloser closer;
    FragmentChangeTrigger trigger;

    public fragmento_drawer_prestador() {
        // Required empty public constructor
    }

    public void sideMenuClose(){
        this.closer.onCloseMenu();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnCerrarSesion = (ImageView)view.findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sideMenuClose();
                trigger.fireChange("menu_lateral_btnCerrarSesion");
            }
        });

        TextView txtVersion = (TextView)view.findViewById(R.id.txtVersion);
        try {
            txtVersion.setText("ANDES Salud Ver.: "+getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);
        }catch (Exception ex){

        }
    }

    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_sidemenu_prestador, container, false);
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }

    public void setCloser(MenuCloser closer){
        this.closer = closer;
    }
}
