package ar.com.andessalud.andes.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import ar.com.andessalud.andes.Interfaces.Eventos_Click_Interface;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.fragmentos.fragmento_notificaciones;
import ar.com.andessalud.andes.models.MedicialPrescriptionList;
import ar.com.andessalud.andes.models.Notifications;
import ar.com.andessalud.andes.models.NotificationsModel;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.fragmentos.fragmento_notificaciones;

public class EventsNotificationAdapter extends RecyclerView.Adapter<EventsNotificationAdapter.ViewHolder> {
    Context mContext;
    ArrayList<NotificationsModel> checkoutItems;;
    public Eventos_Click_Interface eventos_click_interface;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.fila_buzon_notificacion, parent, false);
        return new ViewHolder(v);
    }

    public EventsNotificationAdapter(Context context , ArrayList<NotificationsModel> productItem,Eventos_Click_Interface eventos_click_interface){
        this.eventos_click_interface = eventos_click_interface;
        this.checkoutItems = productItem;
        this.mContext = context;
    };



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {


  /*          if (checkoutItems.get(position).getNotificationslist().get(0).getHasMedicalPrescription().equals(true))
            {
                Log.d("statsvalm","ev");
*/
                holder.txtAfiliado.setText(checkoutItems.get(position).getNotificationslist().get(0).getText());
                holder.txtFecEmision.setText(checkoutItems.get(position).getNotificationslist().get(0).getCreatedAt());

    /*        }
*/
       /*     holder.txtAfiliado.setText(checkoutItems.get(position).getNotificationslist().get(0).getText());
            holder.txtFecEmision.setText(checkoutItems.get(position).getNotificationslist().get(0).getUpdatedAt());
       */
            /*if (checkoutItems.get(position).getNotificationslist().get(0).getHasEvent().equals(true))
            {
                Log.d("statsval","ev");
                holder.txtAfiliado.setText(checkoutItems.get(position).getNotificationslist().get(0).getText());
                holder.txtFecEmision.setText(checkoutItems.get(position).getNotificationslist().get(0).getCreatedAt());

            }
*/
            holder.LnMessage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (checkoutItems.get(position).getNotificationslist().get(0).getHasEvent().equals(true))
                    {
                        eventos_click_interface.onItemClick(checkoutItems.get(position).getNotificationslist().get(0).getEventId(),position);

                    }

                            eventos_click_interface.onItemClickUrl(checkoutItems.get(position).getNotificationslist().get(0).getMp_link());

                }
            });
        }
        catch (Exception ignored)
        {

        }
        //    for (int i = 0 ; i<checkoutItems.size();i++ )
//    {
//        Log.d("checkingdata",""+i);
//
//        List<Notifications> notificationsList = checkoutItems.get(i).getNotificationslist();
//        Log.d("checkingdata",notificationsList.get(0).getText());
//    }

    }

    @Override
    public int getItemCount() {
        return checkoutItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final View convertView;
        TextView idElemento ;
        TextView codTipo ;
        TextView txtTipo ;
        TextView txtAfiliado ;
        TextView txtFecEmision;
        LinearLayout barraEstado,LnMessage;
        ImageView imgTipo
                ;
        TextView lblSolicitud ;
        public ViewHolder( View itemView) {
            super(itemView);
            convertView = itemView;
            LnMessage = (LinearLayout) convertView.findViewById(R.id.lnMensaje);
            idElemento = (TextView) convertView.findViewById(R.id.idElemento);
            codTipo = (TextView) convertView.findViewById(R.id.codTipo);
            txtTipo = (TextView) convertView.findViewById(R.id.txtTipo);
            txtAfiliado = (TextView) convertView.findViewById(R.id.txtAfiliado);
            txtFecEmision = (TextView) convertView.findViewById(R.id.txtFecEmision);
            barraEstado = (LinearLayout) convertView.findViewById(R.id.barraEstado);
            imgTipo = (ImageView) convertView.findViewById(R.id.imgTipo);
            lblSolicitud = (TextView) convertView.findViewById(R.id.lblSolicitud);
            barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonAmarilloNotificacion));
            imgTipo.setImageResource(R.drawable.notificacion_side);
            lblSolicitud.setText("Eventos:");
            txtTipo.setText("Eventos");
            lblSolicitud.setText("Date:");

        }

        //Llenamos el "front"


    }

}

//linkeamos el "front"
/*
        TextView idElemento = (TextView) convertView.findViewById(R.id.idElemento);
        TextView codTipo = (TextView) convertView.findViewById(R.id.codTipo);
        TextView txtTipo = (TextView) convertView.findViewById(R.id.txtTipo);
        TextView txtAfiliado = (TextView) convertView.findViewById(R.id.txtAfiliado);
        TextView txtFecEmision = (TextView) convertView.findViewById(R.id.txtFecEmision);
        LinearLayout barraEstado = (LinearLayout) convertView.findViewById(R.id.barraEstado);
        ImageView imgTipo = (ImageView) convertView.findViewById(R.id.imgTipo);
        TextView lblSolicitud = (TextView) convertView.findViewById(R.id.lblSolicitud);

        //Llenamos el "front"
        idElemento.setText(getItem(position).getId());
        codTipo.setText(avisos.getCreatedAt());
        txtTipo.setText(notificaList.get(position).getText());
        txtAfiliado.setText(notificaList.get(position).getEventId());
        txtFecEmision.setText("");
*/

//Dependiendo el estado indicamos el icono y el color de la barra
//        try{

/*
                        barraEstado.setBackgroundColor(ContextCompat.getColor(mContext, R.color.barraBuzonAmarilloNotificacion));
                        imgTipo.setImageResource(R.drawable.notificacion_side);
                        lblSolicitud.setText("Eventos:");
*/

//        }
//        catch (Exception ignored)
//        {
//
//        }
//
//        return convertView;
//    }
//}
