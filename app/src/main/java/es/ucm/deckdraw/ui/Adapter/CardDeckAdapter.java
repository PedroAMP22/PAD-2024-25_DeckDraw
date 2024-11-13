package es.ucm.deckdraw.ui.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.ui.Fragment.EditDeckFragment;
import es.ucm.deckdraw.ui.Fragment.FragmentViewerInterface;

public class CardDeckAdapter extends RecyclerView.Adapter<CardDeckAdapter.CardDeckViewHolder>{


    private List<TCard> cardList;
    private FragmentViewerInterface sch_frag;
    private List<Integer> cardQuantity;

    public CardDeckAdapter(List<TCard> cardList, EditDeckFragment frg) {
        this.cardList = cardList;
        this.sch_frag = frg;
    }

    @NonNull
    @Override
    public CardDeckAdapter.CardDeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_in_deck, parent, false);
        return new CardDeckAdapter.CardDeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardDeckAdapter.CardDeckViewHolder holder, int position) {
        TCard card = cardList.get(position);
        Picasso.get().load(card.getLargeImageUrl()).fit().placeholder(R.drawable.mtg_placeholder_card).error(R.drawable.not_connected).into(holder.img);
        holder.itemView.setOnClickListener(v -> {
            sch_frag.openDetails(card);
        });
    }

    public static class CardDeckViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public CardDeckViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.deckCardImg);
        }
    }

    public void updateCardList(List<TCard> cardList) {
        this.cardList.clear();
        this.cardList.addAll(cardList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }
}
