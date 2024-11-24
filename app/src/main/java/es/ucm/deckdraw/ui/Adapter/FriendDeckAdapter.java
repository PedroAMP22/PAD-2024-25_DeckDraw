package es.ucm.deckdraw.ui.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.decks.TDecks;
import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.data.dataBase.CurrentUserManager;
import es.ucm.deckdraw.ui.Fragment.DecksFragment;
import es.ucm.deckdraw.ui.Fragment.ShowFriendFragment;

public class FriendDeckAdapter extends RecyclerView.Adapter<FriendDeckAdapter.FriendDeckViewHolder> {


    private final List<TDecks> friendDeckList;
    private ShowFriendFragment fragment;

    public FriendDeckAdapter(List<TDecks> friendDeckList, ShowFriendFragment fragment) {
        this.friendDeckList = friendDeckList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public FriendDeckAdapter.FriendDeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deck_friend, parent, false);
        return new FriendDeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendDeckAdapter.FriendDeckViewHolder holder, int position) {
        TDecks deck = friendDeckList.get(position);
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

        holder.copyButton.setOnClickListener(v ->{
            fragment.copyDeck(deck);
        });

        holder.showButton.setOnClickListener(v -> {
            fragment.showDeck();
        });
    }

    @Override
    public int getItemCount() {
        return friendDeckList.size();
    }

    public void setDecks(List<TDecks> decks) {
        this.friendDeckList.clear();
        this.friendDeckList.addAll(decks);
        notifyDataSetChanged();
    }

    public static class FriendDeckViewHolder extends RecyclerView.ViewHolder {
        TextView deckName;
        TextView deckFormat;
        ImageView deckCover;
        Button showButton;
        Button copyButton;
        public FriendDeckViewHolder(@NonNull View itemView) {
            super(itemView);
            deckName = itemView.findViewById(R.id.textViewDeckName);
            deckFormat = itemView.findViewById(R.id.textViewDeckFormat);
            deckCover = itemView.findViewById(R.id.imageDeckCover);
            copyButton = itemView.findViewById(R.id.buttonCopyDeck);
            showButton = itemView.findViewById(R.id.buttonShowDeck);
        }
    }
}
