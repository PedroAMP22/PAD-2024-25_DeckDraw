package es.ucm.deckdraw.ui.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
import es.ucm.deckdraw.ui.Adapter.CardDeckAdapter;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;
import es.ucm.deckdraw.util.Callback;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


public class EditDeckFragment extends Fragment{
    private SharedViewModel sharedViewModel;
    private EditText toolbarEditText;
    private Context context;
    private String deckName;
    private RecyclerView recyclerView;
    private CardDeckAdapter adapter;
    private boolean leavingEditDeck;
    private List<TCard> cardList = new ArrayList<>(); // Lista para almacenar las cartas
    private boolean hasChanged;
    private LifecycleOwner lifecycleowner;
    private TDecks deck;
    private TextView deckFormat;
    private TextView cardsNumber;
    private TextView deckLimit;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_deck, container, false);
        context = this.getContext();
        // Inicialización del ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
        toolbarEditText = mainScreenActivity.findViewById(R.id.toolbarEditText);

        leavingEditDeck = false;

        ImageView commanderImage = view.findViewById(R.id.commander);
        commanderImage.setOnClickListener(v -> openDetailsCommander(deck.getCommander()));


        adapter = new CardDeckAdapter(cardList, this);
        // Configuración del RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewDeck);

        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_PORTRAIT){ //movil en vertical
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }
        else if(orientation == Configuration.ORIENTATION_LANDSCAPE){ //movil en horizontal
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        }

        recyclerView.setAdapter(adapter);
        lifecycleowner = getViewLifecycleOwner();
        if (toolbarEditText != null) {
            toolbarEditText.setVisibility(View.VISIBLE);

            sharedViewModel.getCurrentDeck().observe(lifecycleowner, deck -> {
                if (deck != null) {
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
            });
        }

        FloatingActionButton addCardButton = view.findViewById(R.id.addCardFab);
        addCardButton.setOnClickListener(v -> {
            if (toolbarEditText != null) {
                sharedViewModel.setCurrentDeckName(toolbarEditText.getText().toString());
            }

            leavingEditDeck = true;

            // Navegar al fragmento de búsqueda de cartas
            CardSearchFragment cardSearchFragment = new CardSearchFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, cardSearchFragment)
                    .addToBackStack(null) // Añadir a la pila de retroceso
                    .commit();
        });

        deck = sharedViewModel.getCurrentDeck().getValue();

        deckFormat = mainScreenActivity.findViewById(R.id.bottom_format);
        cardsNumber = mainScreenActivity.findViewById(R.id.bottom_number_card);
        deckLimit = mainScreenActivity.findViewById(R.id.bottom_deck_limit);

        deckFormat.setText(deck.getDeckFormat());
        cardsNumber.setText(String.valueOf(deck.getNumCards()));

        if(deck.getDeckFormat().equalsIgnoreCase("commander")){
            deckLimit.setText("100");
        }else{
            deckLimit.setText("60");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showSaveChangesDialog();
            }
        };

        //onBackPressed Callback
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // showSaveChangesDialog();
    }


    private void showSaveChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.save_changes));
        builder.setMessage(getString(R.string.ask_save_changes));
        builder.setPositiveButton(getString(R.string.save), (dialog, which) -> {
            if (toolbarEditText != null) {

                DecksAdmin db = new DecksAdmin();
                db.updateDeck(deck, new Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        Toast.makeText(context, getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, getString(R.string.error_saving), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        builder.setNegativeButton(getString(R.string.discard), (dialog, which) -> {
            Toast.makeText(context, getString(R.string.changes_discarded), Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        builder.setNeutralButton(getString(R.string.cancel), (dialog, which) -> {
            // Cancelar la acción y permanecer en el fragmento
            dialog.dismiss();
        });
        builder.create().show();


        /*
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Toast.makeText(context, getString(R.string.changes_discarded), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
        */
    }

    @Override
    public void onResume() {
        super.onResume();
        // Configurar el título de la toolbar y la flecha de retroceso
        if (getActivity() instanceof MainScreenActivity) {
            MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
            mainScreenActivity.setToolbarTitle(""); // Dejar vacío el título porque usamos el EditText
            mainScreenActivity.setHomeAsUpEnabled(true);
            // Cambiar la BottomNavigationView
            mainScreenActivity.setBottomDeck(true);

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof MainScreenActivity) {
            MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
            // Cambiar la BottomNavigationView
            mainScreenActivity.setBottomDeck(false);

        }

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

    public void handleBackPressFromToolbar() {
        // show dialog with setHomeAsUpEnabled
        showSaveChangesDialog();
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
        sharedViewModel.setEditableCard(true);

        CardDetailFragment frag = new CardDetailFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).addToBackStack(null).commit();
    }

    public void openDetailsCommander(TCard card) {
        leavingEditDeck = true;

        sharedViewModel.setSelectedCard(card);
        sharedViewModel.setEditableCard(false);

        CardDetailFragment frag = new CardDetailFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).addToBackStack(null).commit();
    }

}
