package es.ucm.deckdraw.ui.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.data.Objects.decks.TDecks;
import es.ucm.deckdraw.ui.Fragment.ShowDeckFragment;
import es.ucm.deckdraw.ui.Fragment.FragmentViewerInterface;

public class ShowDeckAdapter extends RecyclerView.Adapter<ShowDeckAdapter.ShowDeckViewHolder>{


    private List<TCard> cardList;
    private ShowDeckFragment sch_frag;
    private List<Integer> cardQuantity;
    Set<String> duplicated = new HashSet<String>();

    public ShowDeckAdapter(List<TCard> cardList, ShowDeckFragment frg) {
        this.cardList = cardList;
        this.sch_frag = frg;
    }

    @NonNull
    @Override
    public ShowDeckAdapter.ShowDeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_image, parent, false);

        return new ShowDeckAdapter.ShowDeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowDeckAdapter.ShowDeckViewHolder holder, int position) {
        TCard card = cardList.get(position);

        if(!duplicated.contains(card.getName())) {
            Picasso.get().load(card.getLargeImageUrl()).fit().placeholder(R.drawable.mtg_placeholder_card).error(R.drawable.not_connected).into(holder.img);
            holder.itemView.setOnClickListener(v -> {
                sch_frag.openDetails(card);
            });
            duplicated.add(card.getName());

            holder.number.setText(String.valueOf(card.getQuantity()));

            if(holder.number.getText().equals("0")){
                holder.number.setVisibility(View.GONE);
            }
        }

        //holder.quantityView.setText(deckCards.get(card));

    }

    public static class ShowDeckViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView number;
        public ShowDeckViewHolder(@NonNull View itemView) {
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

    @Override
    public int getItemCount() {
        return cardList.size();
    }


}
