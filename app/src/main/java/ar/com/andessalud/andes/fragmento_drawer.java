package ar.com.andessalud.andes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.MenuCloser;
import ar.com.andessalud.andes.fabricas.fabrica_menu;
import ar.com.andessalud.andes.twilio.MySingleton;

import static android.content.Context.MODE_PRIVATE;


public class fragmento_drawer extends Fragment {
    MenuCloser closer;
    FragmentChangeTrigger trigger;
    JSONObject updateJsonObjects;
    SharedPreferences appPref;
    public fragmento_drawer() {
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
        ImageView btnSedes = (ImageView)view.findViewById(R.id.btnSedes);
        appPref = getContext().getSharedPreferences("datosAplicacion", MODE_PRIVATE);

        btnSedes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sideMenuClose();
                trigger.fireChange("menu_lateral_btnSedes");
            }
        });

        ImageView btnCerrarSesion = (ImageView)view.findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sideMenuClose();
                try {
                    updateJsonObjects.put("idAfiliado", "");
                    updateJsonObjects.put("token", "");

                    updateCustomero(updateJsonObjects);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                trigger.fireChange("menu_lateral_btnCerrarSesion");
            }
        });

        ImageView btnActDatos = view.findViewById(R.id.btnActDatos);
        btnActDatos.setOnClickListener(v -> {
            sideMenuClose();
            trigger.fireChange("menu_lateral_btnActDatos");
        });

        ImageView btnEstadoAfiliacion = view.findViewById(R.id.btnEstadoAfiliacion);
        btnEstadoAfiliacion.setOnClickListener(v -> {
            sideMenuClose();


            trigger.fireChange("menu_lateral_btnEstadoAfiliacion");
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
        return inflater.inflate(R.layout.layout_sidemenu, container, false);
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }

    public void setCloser(MenuCloser closer){
        this.closer = closer;
    }


    private void updateCustomero(JSONObject params) throws JSONException {
        Log.d("createadata", "" + params);
//        dialogo.dismiss();
        String URLs = this.getResources().getString(R.string.srv_localdiscoveritech).concat("update-device-token");
//                "http://discoveritech.org/staff-dashboard/api/";
        Log.d("urldata", "" + URLs);

//        JSONObject jsonObject = new JSONObject(params);
//        Log.d("taffa",""+jsonObject);
//        Log.d("createCUstomer", "" + params);
//        jsonObject.put("CUIL",12343);

//        Log.d("createJson",)
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, params, response -> {

            try {
                Log.d("responseError", "" + response);
                boolean stats = response.getBoolean("status");
                Log.d("createCustomer", "" + stats);

            } catch (JSONException e) {

                e.printStackTrace();
            }


        }, error -> {
            Log.d("eDASDADS", "" + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                return pars;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        updateJsonObjects = null;
    }
}