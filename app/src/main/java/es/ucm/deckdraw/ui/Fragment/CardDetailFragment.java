package es.ucm.deckdraw.ui.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;


public class CardDetailFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private static final String ARG_IMAGE_URL = "image_url";
    private TCard card;
    public CardDetailFragment(TCard card){
        this.card = card;
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
        FloatingActionButton removeCardButton = view.findViewById(R.id.removeCardFab);
        Button okayCardButton = view.findViewById(R.id.okayButton);
        TextView quantityText = view.findViewById(R.id.quantityView);

            sharedViewModel.getCurrentDeck().observe(getViewLifecycleOwner(), deck -> {
                if (deck != null) {
                    quantityText.setText((deck.getNumberOfCardInDeck(card)).toString());
                }
            });

            addCardButton.setOnClickListener(v -> {
                sharedViewModel.getCurrentDeck().observe(getViewLifecycleOwner(), deck -> {
                    if (deck != null) {
                        int quantity = Integer.parseInt(quantityText.getText().toString());
                        if(deck.getDeckFormat().equals("Commander")){
                            if(quantity < 1){
                                quantityText.setText("1");
                            }
                        }else{
                            if(quantity < 4){;
                                quantityText.setText((Integer.toString(quantity)));
                            }
                        }

                    }
                });

            });


            removeCardButton.setOnClickListener(v -> {
                sharedViewModel.getCurrentDeck().observe(getViewLifecycleOwner(), deck -> {
                    if (deck != null) {
                        int quantity = Integer.parseInt(quantityText.getText().toString());
                        if(quantity > 0){
                            int quantityInDeck = (deck.getNumberOfCardInDeck(card)-1);
                            if(quantityInDeck < 0)
                                quantityInDeck = 0;
                            quantityText.setText((Integer.toString(quantityInDeck)));
                        }
                    }
                });

            });

            okayCardButton.setOnClickListener(v -> {
                sharedViewModel.getCurrentDeck().observe(getViewLifecycleOwner(), deck -> {
                    if (deck != null) {
                        int quantity = Integer.parseInt(quantityText.getText().toString());
                        int quantityOnDeck = deck.getNumberOfCardInDeck(card);

                        if(quantity > quantityOnDeck){
                            //Actualizamos el numero
                            deck.addCard(card,quantity-quantityOnDeck);
                        }else if(quantity < quantityOnDeck) {
                            deck.removeCard(card,quantityOnDeck-quantity);
                        }


                    }

                    //Navegar de vuelta al fragment de editDeck
                    EditDeckFragment editDeckFragment = new EditDeckFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, editDeckFragment)
                            .addToBackStack(null) // Añadir a la pila de retroceso
                            .commit();
                });

            });



        return view;
    }
}