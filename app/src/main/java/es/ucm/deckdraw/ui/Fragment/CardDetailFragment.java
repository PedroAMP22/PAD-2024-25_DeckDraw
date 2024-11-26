package es.ucm.deckdraw.ui.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
                    if(card == (deck.getCommander())){
                        addCardButton.setVisibility(View.GONE);
                        removeCardButton.setVisibility(View.GONE);
                    }
                }
            });


            addCardButton.setOnClickListener(v -> {
                sharedViewModel.getCurrentDeck().observe(getViewLifecycleOwner(), deck -> {
                    if (deck != null) {
                        int quantity = Integer.parseInt(quantityText.getText().toString());
                        if(deck.getDeckFormat().equals("Commander")){
                            if (deck.getNumCards()+quantity-deck.getNumberOfCardInDeck(card) < 99){
                                if(quantity < 1 || card.getType().contains("Basic Land")){
                                    quantity++;
                                    quantityText.setText(Integer.toString(quantity));
                                }
                                else{
                                    Toast.makeText(this.requireContext(),"No puedes tener más de una carta de cada en commander", Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(this.requireContext(),"No puedes tener más de 100 cartas en commander", Toast.LENGTH_LONG).show();
                            }

                        }
                        else{
                            if (deck.getNumCards()+quantity-deck.getNumberOfCardInDeck(card) < 60){
                                if(quantity < 4|| card.getType().contains("Basic Land")){;
                                    quantity++;
                                    quantityText.setText((Integer.toString(quantity)));
                                }
                                else{
                                    Toast.makeText(this.requireContext(),"No puedes tener más de 4 cartas de cada en " + deck.getDeckFormat(), Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(this.requireContext(),"No puedes tener más de 60 cartas en" + deck.getDeckFormat(), Toast.LENGTH_LONG).show();
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
                            quantity--;
                            if(quantity < 0)
                                quantity = 0;
                            quantityText.setText((Integer.toString(quantity)));
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
                    getActivity().getSupportFragmentManager().popBackStack();
                });

            });



        return view;
    }
}