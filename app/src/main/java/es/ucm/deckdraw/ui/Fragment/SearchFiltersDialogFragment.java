package es.ucm.deckdraw.ui.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;


import java.util.ArrayList;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;

public class SearchFiltersDialogFragment extends BottomSheetDialogFragment {

    MaterialButton redManaButton;
    MaterialButton blueManaButton;
    MaterialButton greenManaButton;
    MaterialButton whiteManaButton;
    MaterialButton blackManaButton;
    MaterialButton colorlessManaButton;

    private ArrayList<String> colorList;

    private SharedViewModel sharedViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflar el layout simple que solo muestra filtros
        colorList = new ArrayList<>();

        // Inicialización del ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        /*
           sharedViewModel.getCurrentSearchQuery().observe(getViewLifecycleOwner(), query -> {
            if (query != null) {
                search(query);
            }
        });
        * */

        return inflater.inflate(R.layout.fragment_search_filters_dialog, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Ajustar comportamiento de arrastre si es necesario
        if (getDialog() != null && getDialog().getWindow() != null) {
            View bottomSheet = getDialog().findViewById(R.id.search_filter_bottom_sheet_dialog);
            if (bottomSheet != null) {
                // Obtener la altura de la pantalla
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                bottomSheet.setMinimumHeight(screenHeight); // Altura mínima del bottom sheet
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener las referencias de los botones desde el layout inflado
        redManaButton = view.findViewById(R.id.button_red_mana);
        blueManaButton = view.findViewById(R.id.button_blue_mana);
        greenManaButton = view.findViewById(R.id.button_green_mana);
        whiteManaButton = view.findViewById(R.id.button_white_mana);
        blackManaButton = view.findViewById(R.id.button_black_mana);
        colorlessManaButton = view.findViewById(R.id.button_colorless_mana);

        // Establecer listeners para cada botón
        redManaButton.setOnClickListener(v -> toggleButtonSelection(redManaButton,"r"));
        blueManaButton.setOnClickListener(v -> toggleButtonSelection(blueManaButton,"u"));
        greenManaButton.setOnClickListener(v -> toggleButtonSelection(greenManaButton,"g"));
        whiteManaButton.setOnClickListener(v -> toggleButtonSelection(whiteManaButton,"w"));
        blackManaButton.setOnClickListener(v -> toggleButtonSelection(blackManaButton,"b"));
        colorlessManaButton.setOnClickListener(v -> toggleButtonSelection(colorlessManaButton,"c"));
    }

    // Método para alternar el estado de selección y actualizar el ViewModel
    private void toggleButtonSelection(MaterialButton button, String color) {
        boolean isSelected = button.isSelected();

        if(isSelected){
            colorList.remove(color);
        }
        else{
            colorList.add(color);
        }

        button.setSelected(!isSelected);

        // Actualizar el ViewModel con la selección del color
        sharedViewModel.setManaColors(colorList);  // Asegúrate de tener un método en el ViewModel
    }
}
