package es.ucm.deckdraw.ui.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.data.Service.CardLoaderCallbacks;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;
import es.ucm.deckdraw.util.Callback;

public class SearchFiltersDialogFragment extends BottomSheetDialogFragment {

    private static final String FILTER_COLORS_KEY = "colors";
    private static final String FILTER_TYPES_KEY = "types";
    private static final String FILTER_RARITIES_KEY = "rarities";

    private String query;
    private Map<String, ArrayList<String>> filterMap;

    private CardLoaderCallbacks callback;
    private SharedViewModel sharedViewModel;
    private TextInputEditText searchEditText;
    private BottomSheetBehavior<View> bottomSheetBehavior;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_filters_dialog, container, false);

        // Inicialización del ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Configurar el campo de texto para búsqueda
        searchEditText = view.findViewById(R.id.busqueda);

        // Observa el texto de búsqueda en el ViewModel para restaurar el último valor
        sharedViewModel.getCurrentSearchQuery().observe(getViewLifecycleOwner(), q -> {
            if (q != null) {
                searchEditText.setText(q); // Restaura el texto de búsqueda en el campo
            }
        });


        // Inicialización del Map para filtros
        filterMap = new HashMap<>();
        filterMap.put(FILTER_COLORS_KEY, new ArrayList<>());
        filterMap.put(FILTER_RARITIES_KEY, new ArrayList<>());
        filterMap.put(FILTER_TYPES_KEY, new ArrayList<>());

        // Configuración de botones y listeners
        setupButtons(view);

        // Configurar el botón de búsqueda
        MaterialButton searchButton = view.findViewById(R.id.button_search);
        searchButton.setOnClickListener(v -> executeSearch());

        return view;
    }

    private void setupButtons(View view) {
        // Colores
        setupFilterButton(view.findViewById(R.id.button_red_mana), "r", FILTER_COLORS_KEY);
        setupFilterButton(view.findViewById(R.id.button_blue_mana), "u", FILTER_COLORS_KEY);
        setupFilterButton(view.findViewById(R.id.button_green_mana), "g", FILTER_COLORS_KEY);
        setupFilterButton(view.findViewById(R.id.button_white_mana), "w", FILTER_COLORS_KEY);
        setupFilterButton(view.findViewById(R.id.button_black_mana), "b", FILTER_COLORS_KEY);
        setupFilterButton(view.findViewById(R.id.button_colorless_mana), "c", FILTER_COLORS_KEY);

        // Rarezas
        setupFilterButton(view.findViewById(R.id.button_common_rarity), "common", FILTER_RARITIES_KEY);
        setupFilterButton(view.findViewById(R.id.button_uncommon_rarity), "uncommon", FILTER_RARITIES_KEY);
        setupFilterButton(view.findViewById(R.id.button_rare_rarity), "rare", FILTER_RARITIES_KEY);
        setupFilterButton(view.findViewById(R.id.button_special_rarity), "special", FILTER_RARITIES_KEY);
        setupFilterButton(view.findViewById(R.id.button_mythic_rarity), "mythic", FILTER_RARITIES_KEY);
        setupFilterButton(view.findViewById(R.id.button_bonus_rarity), "bonus", FILTER_RARITIES_KEY);


        // Tipos de carta
        setupFilterButton(view.findViewById(R.id.button_artifact_type), "artifact", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_battle_type), "battle", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_conspiracy_type), "conspiracy", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_creature_type), "creature", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_dungeon_type), "dungeon", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_emblem_type), "emblem", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_enchantment_type), "enchantment", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_hero_type), "hero", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_instant_type), "instant", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_kindred_type), "kindred", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_land_type), "land", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_phenomenon_type), "phenomenon", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_plane_type), "plane", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_planeswalker_type), "planeswalker", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_scheme_type), "scheme", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_sorcery_type), "sorcery", FILTER_TYPES_KEY);
        setupFilterButton(view.findViewById(R.id.button_vanguard_type), "vanguard", FILTER_TYPES_KEY);
    }

    private void setupFilterButton(MaterialButton button, String filterValue, String filterType) {
        button.setOnClickListener(v -> toggleButtonSelection(button, filterValue, filterType));
    }

    private void toggleButtonSelection(MaterialButton button, String value, String filterType) {
        boolean isSelected = button.isSelected();
        button.setSelected(!isSelected);

        ArrayList<String> filterList = filterMap.get(filterType);
        if (filterList != null) {
            if (isSelected) {
                filterList.remove(value);
            } else {
                filterList.add(value);
            }
        }

        // Actualiza el ViewModel en función del filtro
        switch (filterType) {
            case FILTER_COLORS_KEY:
                sharedViewModel.setManaColors(filterList);
                break;
            case FILTER_RARITIES_KEY:
                sharedViewModel.setCardRarity(filterList);
                break;
            case FILTER_TYPES_KEY:
                sharedViewModel.setCardTypes(filterList);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Ajustar comportamiento de arrastre si es necesario
        if (getDialog() != null && getDialog().getWindow() != null) {
            View bottomSheet = getDialog().findViewById(R.id.bottom_sheet_search_filters);
            if (bottomSheet != null) {
                // Obtener el BottomSheetBehavior del layout
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                // Configurar el estado inicial del BottomSheet como colapsado
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }


    // Método para expandir el BottomSheet cuando se active la búsqueda
    public void expandBottomSheet() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void executeSearch() {
        // Configuracion de los filtros seleccionados y ejecucion de la búsqueda con el Loader
        query = searchEditText.getText().toString().trim();
        sharedViewModel.setCurrentSearchQuery(query); // Actualiza el query en el ViewModel

        ArrayList<String> colors = filterMap.get(FILTER_COLORS_KEY);  // Filtra los colores
        ArrayList<String> types = filterMap.get(FILTER_TYPES_KEY);  // Filtra los tipos
        ArrayList<String> rarity = filterMap.get(FILTER_RARITIES_KEY);  // Filtra la rareza

        Bundle args = new Bundle();
        args.putString(CardLoaderCallbacks.ARG_NAME, query);
        args.putString(CardLoaderCallbacks.ARG_FORMAT,"");
        args.putStringArrayList(CardLoaderCallbacks.ARG_COLORS, colors);
        args.putStringArrayList(CardLoaderCallbacks.ARG_TYPE, types);
        args.putStringArrayList(CardLoaderCallbacks.ARG_RARITY, rarity);

        // Usa la interfaz Callback para manejar el resultado de la búsqueda
        CardLoaderCallbacks loaderCallbacks = new CardLoaderCallbacks(getContext(), new Callback<List<TCard>>() {
            @Override
            public void onSuccess(List<TCard> data) {
                sharedViewModel.setCurrentCardSearchResults(data);
                dismiss(); // Cierra el diálogo
            }

            @Override
            public void onFailure(Exception e) {
                // Manejo del error, muestra un mensaje si es necesario
                Toast.makeText(getContext(), "Error al cargar las cartas", Toast.LENGTH_SHORT).show();
            }
        });

        // Inicia o reinicia el Loader
        LoaderManager.getInstance(this).restartLoader(1, args, loaderCallbacks);
    }


}
