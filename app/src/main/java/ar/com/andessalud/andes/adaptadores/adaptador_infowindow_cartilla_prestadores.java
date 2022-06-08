package ar.com.andessalud.andes.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.clases.InfoWindowData;

public class adaptador_infowindow_cartilla_prestadores implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public adaptador_infowindow_cartilla_prestadores(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.infowindow_cartilla_prestadores, null);

        TextView info_window_nombre = view.findViewById(R.id.info_window_nombre);
        TextView info_window_domicilio = view.findViewById(R.id.info_window_domicilio);
        TextView info_window_telefonos = view.findViewById(R.id.info_window_telefonos);
        //ImageView img = view.findViewById(R.id.info_window_imagen);

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        //int imageId = context.getResources().getIdentifier(infoWindowData.getImage().toLowerCase(),
        //        "drawable", context.getPackageName());
        //img.setImageResource(imageId);

        info_window_nombre.setText(infoWindowData.getNombre());
        info_window_domicilio.setText(infoWindowData.getDomicilio());
        info_window_telefonos.setText(infoWindowData.getTelefono());
        return view;
    }
}