package es.ucm.deckdraw.ui.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import es.ucm.deckdraw.data.Service.ImageLoaderCallbacks;
import es.ucm.deckdraw.ui.Adapter.CardTextAdapter;
import es.ucm.deckdraw.ui.Activities.MainScreenActivity;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.Adapter.ImageAdapter;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;

public class CardSearchFragment extends Fragment implements  FragmentViewerInterface{
    private SharedViewModel sharedViewModel;
    private static final int LOADER_ID_IMAGES = 2; // Solo un loader para la búsqueda
    private RecyclerView recyclerView;
    private CardTextAdapter adapter;
    private List<TCard> cardList = new ArrayList<>(); // Lista para almacenar las cartas

    private ImageAdapter imageAdapter;
    private ArrayList<Bitmap> imageData = new ArrayList<>();
    private ImageLoaderCallbacks imageLoaderCallbacks;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_search, container, false);

        // Inicialización del ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        //adapter para conseguir la informacion de las cartas
        adapter = new CardTextAdapter(cardList, this);

        // Configuración del RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewDeck);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));



        // Permitir que el fragmento maneje los menús
        setHasOptionsMenu(true);

        // Observa los cambios en los resultados de la búsqueda
        sharedViewModel.getCurrentSearchResults().observe(getViewLifecycleOwner(), this::onCardsLoaded);

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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // Inflar el menú de búsqueda
        inflater.inflate(R.menu.search_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) { // Verifica si es el ítem de filtro.
            // Mostrar el dialog de filtros al hacer clic en el icono de búsqueda
            SearchFiltersDialogFragment filtersDialog = new SearchFiltersDialogFragment();
            filtersDialog.show(getParentFragmentManager(), "SearchFiltersDialogFragment");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCardsLoaded(List<TCard> data) {
        adapter.updateCardList(data);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void openDetails(TCard card) {
        // Guardamos la carta seleccionada en el ViewModel
        sharedViewModel.setSelectedCard(card);
        sharedViewModel.setEditableCard(true);

        CardDetailFragment frag = new CardDetailFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).addToBackStack(null).commit();
    }

    @Override
    public void cardWasUpdated(boolean added) {
        //Null xq aqui no se usa
    }

    @Override
    public void removeCardFromDeck(TCard card) {

    }
}
