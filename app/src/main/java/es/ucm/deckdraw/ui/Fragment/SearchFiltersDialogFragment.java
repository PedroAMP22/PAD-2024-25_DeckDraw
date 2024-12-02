package es.ucm.deckdraw.ui.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
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
import es.ucm.deckdraw.data.Objects.decks.TDecks;
import es.ucm.deckdraw.data.Service.CardLoaderCallbacks;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;
import es.ucm.deckdraw.util.Callback;

public class SearchFiltersDialogFragment extends BottomSheetDialogFragment {

    private  static final Integer LOADER_ID_CARDS = 1;
    private static final String FILTER_COLORS_KEY = "colors";
    private static final String FILTER_TYPES_KEY = "types";
    private static final String FILTER_RARITIES_KEY = "rarities";

    private String query;
    private Map<String, ArrayList<String>> filterMap;

    private CardLoaderCallbacks callback;
    private SharedViewModel sharedViewModel;
    private TextInputEditText searchEditText;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TDecks deck;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_filters_dialog, container, false);

        // Inicialización del ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);


        // Configurar el campo de texto para búsqueda y restaurar el valor de la query
        searchEditText = view.findViewById(R.id.busqueda);

        sharedViewModel.getCurrentSearchQuery().observe(getViewLifecycleOwner(), q -> {
            if (q != null) {
                searchEditText.setText(q); // Restaura la búsqueda
            }
        });

        deck = new TDecks();
        deck = sharedViewModel.getCurrentDeck().getValue();

        // Inicialización del Map para filtros

        filterMap = new HashMap<>();
        filterMap.put(FILTER_COLORS_KEY, new ArrayList<>());
        filterMap.put(FILTER_RARITIES_KEY, new ArrayList<>());
        filterMap.put(FILTER_TYPES_KEY,new ArrayList<>());

        // Restaurar los filtros de colores
        sharedViewModel.getCurrentManaColors().observe(getViewLifecycleOwner(), colors -> {
            if (colors != null) {
                filterMap.put(FILTER_COLORS_KEY, colors);
                updateFilterButtons(view, colors, FILTER_COLORS_KEY);
            }

        });

        // Restaurar los filtros de rarezas
        sharedViewModel.getCurrentCardRarity().observe(getViewLifecycleOwner(), rarities -> {
            if (rarities != null) {
                filterMap.put(FILTER_RARITIES_KEY, rarities);
                updateFilterButtons(view, rarities, FILTER_RARITIES_KEY);
            }
        });

        // Restaurar los filtros de tipos
        sharedViewModel.getCurrentCardTypes().observe(getViewLifecycleOwner(), types -> {
            if (types != null) {
                filterMap.put(FILTER_TYPES_KEY, types);
                updateFilterButtons(view, types, FILTER_TYPES_KEY);
            }

        });


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

    private void updateFilterButtons(View view, List<String> selectedValues, String filterType) {
        List<Integer> buttonIds = getButtonIdsForFilterType(filterType);
        for (Integer id : buttonIds) {
            MaterialButton button = view.findViewById(id);
            String value = getFilterValueForButton(button, filterType);
            button.setSelected(selectedValues.contains(value)); // Marca el botón si el valor está en la lista
        }
    }

    private List<Integer> getButtonIdsForFilterType(String filterType) {
        List<Integer> buttonIds = new ArrayList<>();
        switch (filterType) {
            case FILTER_COLORS_KEY:
                buttonIds.add(R.id.button_red_mana);
                buttonIds.add(R.id.button_blue_mana);
                buttonIds.add(R.id.button_green_mana);
                buttonIds.add(R.id.button_white_mana);
                buttonIds.add(R.id.button_black_mana);
                buttonIds.add(R.id.button_colorless_mana);
                break;
            case FILTER_RARITIES_KEY:
                buttonIds.add(R.id.button_common_rarity);
                buttonIds.add(R.id.button_uncommon_rarity);
                buttonIds.add(R.id.button_rare_rarity);
                buttonIds.add(R.id.button_special_rarity);
                buttonIds.add(R.id.button_mythic_rarity);
                buttonIds.add(R.id.button_bonus_rarity);
                break;
            case FILTER_TYPES_KEY:
                buttonIds.add(R.id.button_artifact_type);
                buttonIds.add(R.id.button_battle_type);
                buttonIds.add(R.id.button_conspiracy_type);
                buttonIds.add(R.id.button_creature_type);
                buttonIds.add(R.id.button_dungeon_type);
                buttonIds.add(R.id.button_emblem_type);
                buttonIds.add(R.id.button_enchantment_type);
                buttonIds.add(R.id.button_hero_type);
                buttonIds.add(R.id.button_instant_type);
                buttonIds.add(R.id.button_kindred_type);
                buttonIds.add(R.id.button_land_type);
                buttonIds.add(R.id.button_phenomenon_type);
                buttonIds.add(R.id.button_plane_type);
                buttonIds.add(R.id.button_planeswalker_type);
                buttonIds.add(R.id.button_scheme_type);
                buttonIds.add(R.id.button_sorcery_type);
                buttonIds.add(R.id.button_vanguard_type);
                break;
        }
        return buttonIds;
    }

    private String getFilterValueForButton(MaterialButton button, String filterType) {
        String value = "";
        switch (filterType) {
            case FILTER_COLORS_KEY:
                if (button.getId() == R.id.button_red_mana) {
                    value = "r";
                } else if (button.getId() == R.id.button_blue_mana) {
                    value = "u";
                } else if (button.getId() == R.id.button_green_mana) {
                    value = "g";
                } else if (button.getId() == R.id.button_white_mana) {
                    value = "w";
                } else if (button.getId() == R.id.button_black_mana) {
                    value = "b";
                } else if (button.getId() == R.id.button_colorless_mana) {
                    value = "c";
                }
                break;
            case FILTER_RARITIES_KEY:
                if (button.getId() == R.id.button_common_rarity) {
                    value = "common";
                } else if (button.getId() == R.id.button_uncommon_rarity) {
                    value = "uncommon";
                } else if (button.getId() == R.id.button_rare_rarity) {
                    value = "rare";
                } else if (button.getId() == R.id.button_special_rarity) {
                    value = "special";
                } else if (button.getId() == R.id.button_mythic_rarity) {
                    value = "mythic";
                } else if (button.getId() == R.id.button_bonus_rarity) {
                    value = "bonus";
                }
                break;
            case FILTER_TYPES_KEY:
                if (button.getId() == R.id.button_artifact_type) {
                    value = "artifact";
                } else if (button.getId() == R.id.button_battle_type) {
                    value = "battle";
                } else if (button.getId() == R.id.button_conspiracy_type) {
                    value = "conspiracy";
                } else if (button.getId() == R.id.button_creature_type) {
                    value = "creature";
                } else if (button.getId() == R.id.button_dungeon_type) {
                    value = "dungeon";
                } else if (button.getId() == R.id.button_emblem_type) {
                    value = "emblem";
                } else if (button.getId() == R.id.button_enchantment_type) {
                    value = "enchantment";
                } else if (button.getId() == R.id.button_hero_type) {
                    value = "hero";
                } else if (button.getId() == R.id.button_instant_type) {
                    value = "instant";
                } else if (button.getId() == R.id.button_kindred_type) {
                    value = "kindred";
                } else if (button.getId() == R.id.button_land_type) {
                    value = "land";
                } else if (button.getId() == R.id.button_phenomenon_type) {
                    value = "phenomenon";
                } else if (button.getId() == R.id.button_plane_type) {
                    value = "plane";
                } else if (button.getId() == R.id.button_planeswalker_type) {
                    value = "planeswalker";
                } else if (button.getId() == R.id.button_scheme_type) {
                    value = "scheme";
                } else if (button.getId() == R.id.button_sorcery_type) {
                    value = "sorcery";
                } else if (button.getId() == R.id.button_vanguard_type) {
                    value = "vanguard";
                }
                break;
        }
        return value;
    }

    @Override
    public void onStart() {
        super.onStart();

        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        View dragHandle = getDialog().findViewById(R.id.drag_handle);

        if (bottomSheet != null && dragHandle != null) {
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


            // Desactiva el arrastre general al inicio
            bottomSheetBehavior.setDraggable(false);

            // Configura el OnTouchListener en dragHandle
            dragHandle.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Activa el arrastre al comenzar el toque
                        bottomSheetBehavior.setDraggable(true);
                        Log.d("sheet", "ARRASTRANDO EL DRAG ");

                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // Desactiva el arrastre cuando se suelta el dedo
                        bottomSheetBehavior.setDraggable(false);
                        Log.d("sheet", "CANCELANDO EL DRAG ");

                        break;
                }
                v.performClick(); // Accesibilidad
                return true;
            });
        }
    }





    private void executeSearch() {
        // Configuracion de los filtros seleccionados y ejecucion de la búsqueda con el Loader
        query = searchEditText.getText().toString().trim();
        sharedViewModel.setCurrentSearchQuery(query); // Actualiza el query en el ViewModel

        ArrayList<String> colors = filterMap.get(FILTER_COLORS_KEY);  // Filtra los colores
        ArrayList<String> types = filterMap.get(FILTER_TYPES_KEY);  // Filtra los tipos
        ArrayList<String> rarity = filterMap.get(FILTER_RARITIES_KEY);  // Filtra la rareza

        String format = deck.getDeckFormat();

        Bundle args = new Bundle();
        args.putString(CardLoaderCallbacks.ARG_NAME, query);
        args.putString(CardLoaderCallbacks.ARG_FORMAT, format);
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
                // Manejo del error
                Toast.makeText(getContext(), "No hay ninguna carta con nombre " + query + " en : " + format, Toast.LENGTH_SHORT).show();
            }
        });

        // Inicia o reinicia el Loader
        LoaderManager.getInstance(this).restartLoader(LOADER_ID_CARDS, args, loaderCallbacks);
    }


}
