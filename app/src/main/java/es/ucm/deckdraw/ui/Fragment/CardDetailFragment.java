package es.ucm.deckdraw.ui.Fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.Map;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.ui.Activities.MainScreenActivity;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;


public class CardDetailFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private static final String ARG_IMAGE_URL = "image_url";
    private TCard card;
    private boolean edit;
    private static final Map<String, Integer> COLOR_MAP = Map.of(
        "W", R.color.ManaColorWhite,
        "U", R.color.ManaColorBlue,
        "B", R.color.ManaColorBlack,
        "R", R.color.ManaColorRed,
        "G", R.color.ManaColorGreen,
        "C", R.color.ManaColorColorless
);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_card_detail, container, false);

        //Shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        card = new TCard();
        card = sharedViewModel.getSelectedCard().getValue();

        edit = Boolean.TRUE.equals(sharedViewModel.getEditableCard().getValue());

        ImageView imageView = view.findViewById(R.id.cardImageView);
        TextView nameTextView = view.findViewById(R.id.name_card);
        TextView descriptionTextView = view.findViewById(R.id.description_card);
        TextView typeTextView = view.findViewById(R.id.type_card);
        TextView colorTextView = view.findViewById(R.id.color_card);
        TextView manaCostTextView = view.findViewById(R.id.mana_cost_card);
        TextView powerTextView = view.findViewById(R.id.power_card);
        TextView toughnessTextView = view.findViewById(R.id.toughness_card);
        TextView setNameTextView = view.findViewById(R.id.set_name_card);
        TextView rarityTextView = view.findViewById(R.id.rarity_card);
        TextView artistTextView = view.findViewById(R.id.artist_card);

        // Cargar la imagen usando Picasso
        Picasso.get().load(card.getLargeImageUrl())
                .placeholder(R.drawable.mtg_placeholder_card)
                .error(R.drawable.logo)
                .fit()
                .into(imageView);
        nameTextView.setText(card.getName());
        descriptionTextView.setText(card.getText());
        typeTextView.setText(card.getType());
        colorTextView.setText(card.getAllColors());
        manaCostTextView.setText(card.getManaCost());
        powerTextView.setText(card.getPower());
        toughnessTextView.setText(card.getToughness());
        setNameTextView.setText(card.getSetName());
        rarityTextView.setText(card.getRarity());
        artistTextView.setText(card.getArtist());

        // Cambiar el color del fondo según el color de la carta
        String singleColor = card.getSingleColor(); // esta funcion solo devuelve un color
        int colorResId = colorResId= COLOR_MAP.get(singleColor); // obtenemos el color de colors.xml
        int color = ContextCompat.getColor(requireContext(), colorResId);

        LinearLayout colorContainer = view.findViewById(R.id.container);

        // Modificar el fondo del contenedor
        GradientDrawable background = (GradientDrawable) colorContainer.getBackground();
        background.setColor(color);


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

            if(edit) {
                addCardButton.setOnClickListener(v -> {
                    sharedViewModel.getCurrentDeck().observe(getViewLifecycleOwner(), deck -> {
                        if (deck != null) {
                            int quantity = Integer.parseInt(quantityText.getText().toString());
                            if (deck.getDeckFormat().equals("Commander")) {
                                if (deck.getNumCards() + quantity - deck.getNumberOfCardInDeck(card) < 99) {
                                    if (quantity < 1 || card.getType().contains("Basic Land")) {
                                        quantity++;
                                        quantityText.setText(Integer.toString(quantity));
                                    } else {
                                        Toast.makeText(this.requireContext(), getString(R.string.duplicate_commander_toast), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(this.requireContext(), getString(R.string.more_than_100), Toast.LENGTH_LONG).show();
                                }

                            } else {
                                if (deck.getNumCards() + quantity - deck.getNumberOfCardInDeck(card) < 60) {
                                    if (quantity < 4 || card.getType().contains("Basic Land")) {
                                        ;
                                        quantity++;
                                        quantityText.setText((Integer.toString(quantity)));
                                    } else {
                                        Toast.makeText(this.requireContext(), getString(R.string.more_than_4)+ deck.getDeckFormat(), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(this.requireContext(), getString(R.string.limit_60) + deck.getDeckFormat(), Toast.LENGTH_LONG).show();
                                }

                            }

                        }
                    });

                });
            }else
                addCardButton.setVisibility(View.GONE);

            if(edit) {
                removeCardButton.setOnClickListener(v -> {
                    sharedViewModel.getCurrentDeck().observe(getViewLifecycleOwner(), deck -> {
                        if (deck != null) {
                            int quantity = Integer.parseInt(quantityText.getText().toString());
                            if (quantity > 0) {
                                quantity--;
                                if (quantity < 0)
                                    quantity = 0;
                                quantityText.setText((Integer.toString(quantity)));
                            }
                        }
                    });

                });
            }else
                removeCardButton.setVisibility(View.GONE);

            okayCardButton.setOnClickListener(v -> {
                sharedViewModel.getCurrentDeck().observe(getViewLifecycleOwner(), deck -> {
                    if (deck != null && edit) {
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


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainScreenActivity) {
            MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
            mainScreenActivity.setToolbarTitle("");
            mainScreenActivity.setHomeAsUpEnabled(true);
        }

        // Ocultar la BottomNavigationView
        if (getActivity() != null) {
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Mostrar la BottomNavigationView al salir del fragmento
        if (getActivity() != null) {
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}