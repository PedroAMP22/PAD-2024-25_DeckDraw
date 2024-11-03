package es.ucm.deckdraw.ui.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import es.ucm.deckdraw.ui.Activities.MainScreenActivity;
import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;

public class DecksFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decks, container, false);

        // Botón para navegar a CreateDeckFragment
        Button createDeckButton = view.findViewById(R.id.button_create_deck);
        createDeckButton.setOnClickListener(v -> {
// Limpiar los datos del SharedViewModel
            if (getActivity() != null) {
                SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                sharedViewModel.setCurrentDeckName("");  // Limpiar el nombre del mazo
                sharedViewModel.setCurrentSearchQuery("");  // Limpiar la consulta de búsqueda
            }

            // Reemplazar el fragmento y añadirlo a la pila de retroceso para permitir navegación hacia atrás
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CreateDeckFragment())
                    .addToBackStack("CreateDeckFragment")
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainScreenActivity) {
            MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
            mainScreenActivity.setToolbarTitle("Tus mazos");
            mainScreenActivity.setHomeAsUpEnabled(false);
        }
    }
}
