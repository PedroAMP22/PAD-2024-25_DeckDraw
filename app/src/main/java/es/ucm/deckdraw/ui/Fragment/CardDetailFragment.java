package es.ucm.deckdraw.ui.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicBoolean;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;


public class CardDetailFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private static final String ARG_IMAGE_URL = "image_url";
    private TCard card;
    private boolean addingCard;
    public CardDetailFragment(TCard card, boolean addingCard){
        this.card = card;
        this.addingCard = addingCard;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_card_detail, container, false);

        ImageView imageView = view.findViewById(R.id.cardImageView);
        TextView nameTextView = view.findViewById(R.id.name_card);
        TextView descriptionTextView = view.findViewById(R.id.description_card);
        // Cargar la imagen usando Picasso
        Picasso.get().load(card.getLargeImageUrl())
                .placeholder(R.drawable.mtg_placeholder_card)
                .error(R.drawable.logo)
                .fit()
                .into(imageView);
        nameTextView.setText(card.getName());
        descriptionTextView.setText(card.getText());

        //Shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        FloatingActionButton addCardButton = view.findViewById(R.id.addCardFab);
        if(addingCard) {
            addCardButton.setOnClickListener(v -> {
                sharedViewModel.getCurrentDeck().observe(getViewLifecycleOwner(), deck -> {
                    if (deck != null) {
                        if(deck.getNumberOfCardInDeck(card) == 0)
                            deck.addCard(card);
                        else if(deck.getNumberOfCardInDeck(card) < 4)
                            deck.addCardToCardSearcher(card);

                    }
                });
                //Navegar de vuelta al fragment de editDeck
                EditDeckFragment editDeckFragment = new EditDeckFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, editDeckFragment)
                        .addToBackStack(null) // Añadir a la pila de retroceso
                        .commit();
            });
        }else
            addCardButton.setVisibility(View.GONE);

        return view;
    }
}