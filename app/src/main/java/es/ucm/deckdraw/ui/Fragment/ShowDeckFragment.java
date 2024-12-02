package es.ucm.deckdraw.ui.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.data.Objects.decks.TDecks;
import es.ucm.deckdraw.data.dataBase.DecksAdmin;
import es.ucm.deckdraw.ui.Activities.MainScreenActivity;
import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.Adapter.ShowDeckAdapter;
import es.ucm.deckdraw.ui.Adapter.ShowDeckAdapter;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;
import es.ucm.deckdraw.util.Callback;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


public class ShowDeckFragment extends Fragment{
    private SharedViewModel sharedViewModel;
    private EditText toolbarEditText;
    private Context context;
    private String deckName;
    private RecyclerView recyclerView;
    private ShowDeckAdapter adapter;
    private boolean leavingEditDeck;
    private List<TCard> cardList = new ArrayList<>(); // Lista para almacenar las cartas
    private boolean hasChanged;
    private LifecycleOwner lifecycleowner;
    private TDecks deck;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_deck, container, false);
        context = this.getContext();
        // Inicialización del ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
        toolbarEditText = mainScreenActivity.findViewById(R.id.toolbarEditText);

        leavingEditDeck = false;

        ImageView commanderImage = view.findViewById(R.id.commander);
        commanderImage.setOnClickListener(v -> openDetails(deck.getCommander()));


        adapter = new ShowDeckAdapter(cardList, this);
        // Configuración del RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewDeck);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
        lifecycleowner = getViewLifecycleOwner();

        if (sharedViewModel.getCurrentDeck() == null) {
            Log.e("Debug", "getCurrentDeck() returned null.");
        }

        TDecks deck = sharedViewModel.getCurrentDeck().getValue();
        if (deck == null) {
            Log.e("Debug", "No value in LiveData before observing.");
        }


        if (toolbarEditText != null) {
            toolbarEditText.setVisibility(View.VISIBLE);

                if (deck != null) {
                    toolbarEditText.setText(deck.getDeckName());
                    if(deck.getDeckFormat().equals("Commander")){
                        Picasso.get()
                                .load(deck.getCommander().getLargeImageUrl()) // Suponiendo que getDeckImageUrl() devuelve la URL de la imagen
                                .placeholder(R.drawable.logo) // Imagen placeholder mientras carga
                                .error(R.drawable.not_connected) // Imagen de error si falla la carga
                                .into(commanderImage);
                    }else{
                        commanderImage.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        recyclerView.setLayoutParams(params);

                    }
                    cardList = new ArrayList<>(deck.getCards());
                    toolbarEditText.setText(deck.getDeckName());
                    deckName = deck.getDeckName();

                    cardList = new ArrayList<>(deck.getCards());
                    this.deck = deck;

                    deck.populateCardSearcher();

                    refreshCardList(view);
                }

        }


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // showSaveChangesDialog();
    }


    @Override
    public void onResume() {
        super.onResume();
        // Configurar el título de la toolbar y la flecha de retroceso
        if (getActivity() instanceof MainScreenActivity) {
            MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
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
        // Ocultar el EditText cuando el fragmento no esté activo
        if (toolbarEditText != null) {
            toolbarEditText.setVisibility(View.GONE);
        }

        // Mostrar la BottomNavigationView al salir del fragmento
        if (getActivity() != null) {
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.VISIBLE);

        }
    }

    public void refreshCardList(View view) {
        List<TCard> filteredList = new ArrayList<>();
        Set<String> duplicatedCards = new HashSet<String>();
        //Filtramos las cartas para que no se muestren dos veces
        for(TCard card: cardList){
            if(!duplicatedCards.contains(card.getID())){
                filteredList.add(card);
                duplicatedCards.add(card.getID());
            }
        }
        adapter.updateCardList(filteredList);
    }



    public void openDetails(TCard card) {
        leavingEditDeck = true;

        sharedViewModel.setSelectedCard(card);
        sharedViewModel.setEditableCard(false);

        CardDetailFragment frag = new CardDetailFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).addToBackStack(null).commit();
    }



}
