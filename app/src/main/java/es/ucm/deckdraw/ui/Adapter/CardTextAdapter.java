package es.ucm.deckdraw.ui.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.R;

public class CardTextAdapter extends RecyclerView.Adapter<CardTextAdapter.CardViewHolder> {

    private final List<TCard> cardList;

    public CardTextAdapter(List<TCard> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_text, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        TCard card = cardList.get(position);
        holder.cardTitle.setText(card.getName()); // Asegúrate de que `getName()` existe en `TCard`
        holder.cardDetails.setText(card.getCardDetails()); // Asegúrate de que `getCardDetails()` existe en `TCard`
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardTitle;
        TextView cardDetails;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardDetails = itemView.findViewById(R.id.cardDetails);
        }
    }
}
