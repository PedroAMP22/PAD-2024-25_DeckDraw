package es.ucm.deckdraw.ui.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.decks.TDecks;
import es.ucm.deckdraw.data.dataBase.DecksAdmin;
import es.ucm.deckdraw.ui.Fragment.DecksFragment;
import es.ucm.deckdraw.util.Callback;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {


    private final List<TDecks> deckList;
    private DecksFragment fragment;

    public DeckAdapter(List<TDecks> deckList, DecksFragment fragment) {
        this.deckList = deckList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public DeckAdapter.DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_deck, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckAdapter.DeckViewHolder holder, int position) {
        TDecks deck = deckList.get(position);
        holder.deckName.setText(deck.getDeckName());
        holder.deckFormat.setText(deck.getDeckFormat());
        if (!deck.getUrlDeckCover().isBlank() && deck.getUrlDeckCover() != null) {
            Picasso.get()
                    .load(deck.getUrlDeckCover()) // Suponiendo que getDeckImageUrl() devuelve la URL de la imagen
                    .placeholder(R.drawable.logo) // Imagen placeholder mientras carga
                    .error(R.drawable.not_connected) // Imagen de error si falla la carga
                    .into(holder.deckCover);
        }
        else {
            holder.deckCover.setImageResource(R.drawable.logo);
        }
        holder.deleteButton.setOnClickListener(v -> {
           this.deckList.remove(deck);
           notifyDataSetChanged();
           DecksAdmin db = new DecksAdmin();
           db.deleteDeck(deck, new Callback<Boolean>() {
               @Override
               public void onSuccess(Boolean data) {
               }

               @Override
               public void onFailure(Exception e) {

               }
           });
        });
        holder.editButton.setOnClickListener(v -> {
           fragment.onEditDeck(deck);
        });
    }

    @Override
    public int getItemCount() {
        return deckList.size();
    }

    public void setDecks(List<TDecks> decks) {
        this.deckList.clear();
        this.deckList.addAll(decks);
        notifyDataSetChanged();
    }

    public static class DeckViewHolder extends RecyclerView.ViewHolder {
        TextView deckName;
        TextView deckFormat;
        ImageView deckCover;
        Button deleteButton;
        Button editButton;
        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            deckName = itemView.findViewById(R.id.textViewDeckName);
            deckFormat = itemView.findViewById(R.id.textViewDeckFormat);
            deckCover = itemView.findViewById(R.id.imageDeckCover);
            deleteButton = itemView.findViewById(R.id.buttonDeleteDeck);
            editButton = itemView.findViewById(R.id.buttonEditDeck);
        }
    }
}
