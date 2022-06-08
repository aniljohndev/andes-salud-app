package ar.com.andessalud.andes.adaptadores;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ar.com.andessalud.andes.R;

public class HorizontalRecyclerviewAdapter extends RecyclerView.Adapter<HorizontalRecyclerviewAdapter.HorizontalViewHolder> {

    private  ArrayList<Uri> uri = new ArrayList<>();

    public HorizontalRecyclerviewAdapter(ArrayList<Uri> uri) {
        this.uri = uri;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_horizontalrecylerview, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder horizontalViewHolder, @SuppressLint("RecyclerView") int position) {
        horizontalViewHolder.mImageRecyclerView.setImageURI(uri.get(position));
        //           int pos = horizontalViewHolder.getAdapterPosition();
        horizontalViewHolder.removeImagefromrec.setOnClickListener(v -> {
                        try {
                            uri.remove(horizontalViewHolder.getAdapterPosition());
                            notifyItemRemoved(horizontalViewHolder.getAdapterPosition());

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                     });
    }
    @Override
    public int getItemCount() {
        return uri.size();
    }

    public static class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageRecyclerView;
        CardView  removeImagefromrec;
        public HorizontalViewHolder(View itemView) {
            super(itemView);
            mImageRecyclerView = itemView.findViewById(R.id.selectedImage);
            removeImagefromrec = itemView.findViewById(R.id.removeImagefromrec);
        }
    }
}
