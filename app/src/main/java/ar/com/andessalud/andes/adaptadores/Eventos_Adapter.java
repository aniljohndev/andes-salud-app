package ar.com.andessalud.andes.adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ar.com.andessalud.andes.Interfaces.Eventos_Click_Interface;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.models.Eventos;

public class Eventos_Adapter extends RecyclerView.Adapter<Eventos_Adapter.ViewHolder> {
    Context mContext;
     List<Eventos> checkoutItems = new ArrayList<>();;
    public Eventos_Click_Interface eventos_click_interface;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();

       LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
          View v =       layoutInflater.inflate(R.layout.eventos_rowlist, parent, false);
        return new ViewHolder(v);
    }

    public Eventos_Adapter(Context context , List<Eventos> productItem,Eventos_Click_Interface eventos_click_interface){
        this.checkoutItems = productItem;
        this.mContext = context;
        this.eventos_click_interface = eventos_click_interface ;
    };



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.event_id.setText(checkoutItems.get(position).getDesc());
        holder.event_date.setText(checkoutItems.get(position).getDate());
        if (checkoutItems.get(position).getType().equals("event"))

        {
            holder.cardView.setOnClickListener(v -> eventos_click_interface.onItemClick(Integer.parseInt(checkoutItems.get(position).getId()),position));

        }

    }

    @Override
    public int getItemCount() {
        return checkoutItems.size();
    }

       class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView event_id,
                event_date;
        public final CardView cardView;

        public ViewHolder( View itemView) {
            super(itemView);
            mView = itemView;
            event_id = itemView.findViewById(R.id.eventIds);
            event_date = itemView.findViewById(R.id.event_dates);
//            itemView.setOnClickListener(v -> eventos_click_interface.onItemClick(getAdapterPosition()));
            cardView = itemView.findViewById(R.id.event_cardview);



        }
    }
}