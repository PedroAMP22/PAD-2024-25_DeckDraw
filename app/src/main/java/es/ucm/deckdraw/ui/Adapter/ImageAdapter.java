package es.ucm.deckdraw.ui.Adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.Fragment.CardDetailFragment;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imageUrls  = new ArrayList<>();

    public ImageAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // Usa Picasso para cargar la imagen desde la URL
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.mtg_placeholder_card) // imagen de carga
                .error(R.drawable.logo) // Imagen de error si falla la carga
                .fit() // se ajusta la imagen automaticamente
                .centerCrop()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
            CardDetailFragment cardDetailFragment = CardDetailFragment.newInstance(imageUrl);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, cardDetailFragment) // R.id.fragment_container es el contenedor en el layout principal
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public void setImageUrls(List<String> imageUrls){
        this.imageUrls = imageUrls;
        notifyDataSetChanged(); // Actualiza el adaptador
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
