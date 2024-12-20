package es.ucm.deckdraw.ui.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.Fragment.CardSearchFragment;
import es.ucm.deckdraw.ui.Fragment.EditDeckFragment;
import es.ucm.deckdraw.ui.Fragment.FragmentViewerInterface;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;

public class CardTextAdapter extends RecyclerView.Adapter<CardTextAdapter.CardViewHolder> {

    private List<TCard> cardList;
    private CardSearchFragment sch_frag;

    public CardTextAdapter(List<TCard> cardList, CardSearchFragment frg) {
        this.cardList = cardList;
        this.sch_frag = frg;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_image, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        TCard card = cardList.get(position);
        Picasso.get().load(card.getLargeImageUrl()).fit().placeholder(R.drawable.mtg_placeholder_card).error(R.drawable.not_connected).into(holder.img);
        holder.itemView.setOnClickListener(v -> {
            sch_frag.openDetails(card);
        });

        if(holder.number.getText().equals("0")){
            holder.number.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView number;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView);
            number = itemView.findViewById(R.id.cardsNumber);
        }
    }

    public void updateCardList(List<TCard> cardList) {
        this.cardList.clear();
        this.cardList.addAll(cardList);
        notifyDataSetChanged();
    }
}
