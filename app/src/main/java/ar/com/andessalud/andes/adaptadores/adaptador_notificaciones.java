package ar.com.andessalud.andes.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.fragmentos.fragmento_notificaciones;

public class adaptador_notificaciones extends ArrayAdapter<fragmento_notificaciones.adap_notificaciones> {

    Context mContext;

    public adaptador_notificaciones(Context context, ArrayList<fragmento_notificaciones.adap_notificaciones> avisos) {
        super(context, 0, avisos);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        fragmento_notificaciones.adap_notificaciones avisos = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fila_buzon_notificacion, parent, false);
        }

        //linkeamos el "front"
        TextView idElemento = (TextView) convertView.findViewById(R.id.idElemento);
        TextView codTipo = (TextView) convertView.findViewById(R.id.codTipo);
        TextView txtTipo = (TextView) convertView.findViewById(R.id.txtTipo);
        TextView txtAfiliado = (TextView) convertView.findViewById(R.id.txtAfiliado);
        TextView txtFecEmision = (TextView) convertView.findViewById(R.id.txtFecEmision);
        LinearLayout barraEstado = (LinearLayout) convertView.findViewById(R.id.barraEstado);
        ImageView imgTipo = (ImageView) convertView.findViewById(R.id.imgTipo);
        TextView lblSolicitud = (TextView) convertView.findViewById(R.id.lblSolicitud);

        //Llenamos el "front"
        idElemento.setText(avisos.id);
        codTipo.setText(avisos.codTipo);
        txtTipo.setText(avisos.descTipo);
        txtAfiliado.setText(avisos.afiliado);
        txtFecEmision.setText(avisos.fecEmision);

        //Dependiendo el estado indicamos el icono y el color de la barra
        try{
            switch (avisos.codTipo) {
                case "AMBAUT":
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonVerde));
                    imgTipo.setImageResource(R.drawable.ordenes_left_side);
                    break;
                case "AMBAUD":
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonAmarilla));
                    imgTipo.setImageResource(R.drawable.ordenes_left_side);
                    break;
                case "RECHAZADO":
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonRojo));
                    imgTipo.setImageResource(R.drawable.ordenes_left_side);
                    break;
                case "PRACTAUT":
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonVerde));
                    imgTipo.setImageResource(R.drawable.side_estudiomedicos);
                    break;
                case "PRACTAUD":
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonAmarilla));
                    imgTipo.setImageResource(R.drawable.side_estudiomedicos);
                    break;
                case "RECHAZOPRACTICA":
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonRojo));
                    imgTipo.setImageResource(R.drawable.side_estudiomedicos);
                    break;
                case "TURSINCONF":
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonAzul));
                    imgTipo.setImageResource(R.drawable.side_turnos);
                    break;
                case "TURAUDIT":
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonAmarilla));
                    imgTipo.setImageResource(R.drawable.side_turnos);
                    break;
                case "CONFTURANDES":
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonMarron));
                    imgTipo.setImageResource(R.drawable.side_turnos);
                    break;
                case "TURCANC":
                    txtTipo.setText("Turno cancelado");
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonGris));
                    imgTipo.setImageResource(R.drawable.side_turnos);
                    break;
                case "TURNRECH":
                    txtTipo.setText("Turno no autorizado");
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonRojo));
                    imgTipo.setImageResource(R.drawable.side_turnos);
                    break;
                case "TURNCONF":
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonVerde));
                    imgTipo.setImageResource(R.drawable.side_turnos);
                    break;
                case "CREDPROV":
                    txtTipo.setText("Credencial virtual");
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonGris));
                    imgTipo.setImageResource(R.drawable.side_credencial_nueva);
                    break;
                case "NOTIFICACION":
                    txtTipo.setText("Notificación");
                    barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonAmarilloNotificacion));
                    imgTipo.setImageResource(R.drawable.notificacion_side);
                    lblSolicitud.setText("Recepción:");
                    break;
            }
        }
        catch (Exception ignored)
        {

        }

        return convertView;
    }
}
