package es.ucm.deckdraw.ui.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.ucm.deckdraw.data.Service.CardLoaderCallbacks;
import es.ucm.deckdraw.ui.Adapter.CardTextAdapter;
import es.ucm.deckdraw.ui.Activities.MainScreenActivity;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Service.CardLoader;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;

public class CardSearchFragment extends Fragment implements CardLoaderCallbacks.Callback {
    private SharedViewModel sharedViewModel;
    private static final int LOADER_ID = 1; // Solo un loader para la búsqueda
    private RecyclerView recyclerView;
    private CardTextAdapter adapter;
    private List<TCard> cardList = new ArrayList<>(); // Lista para almacenar las cartas

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_search, container, false);

        // Inicialización del ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        recyclerView = view.findViewById(R.id.recyclerView);

        // Configuración del RecyclerView
        adapter = new CardTextAdapter(cardList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Permitir que el fragmento maneje los menús
        setHasOptionsMenu(true);

        // Observa los cambios en la consulta de búsqueda
        sharedViewModel.getCurrentSearchQuery().observe(getViewLifecycleOwner(), query -> {
            if (query != null) {
                search(query);
            }
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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // Puedes decidir si quieres ocultar la barra de herramientas o no
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflar el menú de búsqueda
        inflater.inflate(R.menu.search_menu, menu);

        // Obtener la SearchView
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        // Configura la SearchView con el estado de búsqueda
        sharedViewModel.getCurrentSearchQuery().observe(getViewLifecycleOwner(), query -> {
            if (query != null) {
                searchView.setQuery(query, false);
            }
        });

        // Configurar el listener para manejar las consultas de búsqueda
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sharedViewModel.setCurrentSearchQuery(query); // Guarda la consulta en el ViewModel
                search(query); // Llama al método de búsqueda centralizado
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Puedes implementar lógica para buscar en tiempo real si es necesario
                return false;
            }
        });
    }

    public void onCardsLoaded(List<TCard> data) {
        cardList.clear();
        if (data != null && !data.isEmpty()) {
            cardList.addAll(data);
        }
        adapter.notifyDataSetChanged();
    }

    private void search(String query) {
        // Lógica para reiniciar el Loader
        Bundle args = new Bundle();
        args.putString(CardLoaderCallbacks.ARG_NAME, query);
        args.putString(CardLoaderCallbacks.ARG_FORMAT, "");
        args.putString(CardLoaderCallbacks.ARG_TYPE, "");
        args.putStringArrayList(CardLoaderCallbacks.ARG_COLORS, new ArrayList<>());

        CardLoaderCallbacks loaderCallbacks = new CardLoaderCallbacks(getContext(), this);
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, args, loaderCallbacks);
    }
}
