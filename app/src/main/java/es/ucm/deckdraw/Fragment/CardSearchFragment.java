package es.ucm.deckdraw.Fragment;

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
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.ucm.deckdraw.Adapter.CardTextAdapter;
import es.ucm.deckdraw.MainActivity;
import es.ucm.deckdraw.Model.TCard;
import es.ucm.deckdraw.R;
import es.ucm.deckdraw.Service.CardLoader;

public class CardSearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<TCard>> {

    private static final int LOADER_ID = 1; // Solo un loader para la búsqueda
    private RecyclerView recyclerView;
    private CardTextAdapter adapter;
    private List<TCard> cardList = new ArrayList<>(); // Lista para almacenar las cartas

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        // Configuración del RecyclerView
        adapter = new CardTextAdapter(cardList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Permitir que el fragmento maneje los menús
        setHasOptionsMenu(true);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setToolbarTitle("");
            mainActivity.setHomeAsUpEnabled(true);
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

        // Configurar el listener para manejar las consultas de búsqueda
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Reiniciar el loader con la nueva consulta
                Bundle args = new Bundle();
                args.putString("name", query);
                args.putString("format", "");
                args.putString("type", "");
                args.putStringArrayList("colors", new ArrayList<>());

                LoaderManager.getInstance(CardSearchFragment.this).restartLoader(LOADER_ID, args, CardSearchFragment.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Puedes implementar lógica para buscar en tiempo real si es necesario
                return false;
            }
        });
    }

    @Override
    public Loader<List<TCard>> onCreateLoader(int id, Bundle args) {
        String name = args.getString("name", "");
        String format = args.getString("format", "");
        ArrayList<String> colors = args.getStringArrayList("colors");
        String type = args.getString("type", "");

        // Crear el loader y forzar su carga
        CardLoader loader = new CardLoader(getContext(), name, format, colors, type);
        loader.forceLoad(); // Forzamos la ejecución del Loader
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<TCard>> loader, List<TCard> data) {
        if (data != null && !data.isEmpty()) {
            cardList.clear();
            cardList.addAll(data);
            adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
        } else {
            cardList.clear();
            adapter.notifyDataSetChanged(); // Limpiar el adaptador si no hay resultados
        }
    }

    @Override
    public void onLoaderReset(Loader<List<TCard>> loader) {
        cardList.clear();
        adapter.notifyDataSetChanged();
    }
}
