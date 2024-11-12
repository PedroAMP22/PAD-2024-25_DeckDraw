package es.ucm.deckdraw.ui.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.Fragment.CardSearchFragment;
import es.ucm.deckdraw.ui.Fragment.EditDeckFragment;
import es.ucm.deckdraw.ui.Fragment.FragmentViewerInterface;

public class CardTextAdapter extends RecyclerView.Adapter<CardTextAdapter.CardViewHolder> {

    private List<TCard> cardList;
    private FragmentViewerInterface sch_frag;

    private ImageButton plusButton;
    private ImageButton minusButton;
    private TextView quantityText;
    private boolean showButtons;

    public CardTextAdapter(List<TCard> cardList, CardSearchFragment frg) {
        this.cardList = cardList;
        this.sch_frag = frg;
        showButtons = false;
    }

    public CardTextAdapter(List<TCard> cardList, EditDeckFragment frg) {
        this.cardList = new ArrayList<>(cardList);
        this.sch_frag = frg;
        showButtons = true;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_image, parent, false);

        plusButton = view.findViewById(R.id.plusButton);
        minusButton = view.findViewById(R.id.minusButton);
        quantityText= view.findViewById(R.id.quantityText);
        // Ocultamos los botones en la busqueda
        if(!showButtons){
            plusButton.setVisibility(View.GONE);
            minusButton.setVisibility(View.GONE);
            quantityText.setVisibility(View.GONE);
        }


        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        TCard card = cardList.get(position);
        Picasso.get().load(card.getLargeImageUrl()).fit().placeholder(R.drawable.mtg_placeholder_card).error(R.drawable.not_connected).into(holder.img);
        holder.itemView.setOnClickListener(v -> {
            sch_frag.openDetails(card);
        });

        quantityText.setText(card.getQuantity().toString());

        plusButton.setOnClickListener( v -> {
            if(card.getQuantity() < 4) {
                card.addCardQuantity();
                quantityText.setText(card.getQuantity().toString());
                sch_frag.cardWasUpdated(true);
            }
        });

        minusButton.setOnClickListener( v -> {
            card.removeCardQuantity();
            if(card.getQuantity() >= 1) {
                quantityText.setText(card.getQuantity().toString());
                sch_frag.cardWasUpdated(false);
            }else{
                cardList.remove(card);
                sch_frag.removeCardFromDeck(card);
                updateCardList(cardList);
            }

        });



    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView);
        }
    }

    public void updateCardList(List<TCard> cardList) {
        this.cardList.clear();
        this.cardList.addAll(cardList);
        notifyDataSetChanged();
    }



}
