package es.ucm.deckdraw.ui.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import es.ucm.deckdraw.ui.Activities.MainScreenActivity;
import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;

public class CreateDeckFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private EditText toolbarEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_deck, container, false);

        // Inicialización del ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Obtener el EditText de la Toolbar
        if (getActivity() instanceof MainScreenActivity) {
            MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
            toolbarEditText = mainScreenActivity.findViewById(R.id.toolbarEditText);

            if (toolbarEditText != null) {
                toolbarEditText.setVisibility(View.VISIBLE);
                sharedViewModel.getCurrentDeckName().observe(getViewLifecycleOwner(), name -> {
                    if (name != null) {
                        toolbarEditText.setText(name);
                    }
                });
            }
        }

        Button addCardButton = view.findViewById(R.id.addCardButton);
        addCardButton.setOnClickListener(v -> {
            if (toolbarEditText != null) {
                sharedViewModel.setCurrentDeckName(toolbarEditText.getText().toString());
            }

            // Navegar al fragmento de búsqueda de cartas
            CardSearchFragment cardSearchFragment = new CardSearchFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, cardSearchFragment)
                    .addToBackStack(null) // Añadir a la pila de retroceso
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Configurar el título de la toolbar y la flecha de retroceso
        if (getActivity() instanceof MainScreenActivity) {
            MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
            mainScreenActivity.setToolbarTitle(""); // Dejar vacío el título porque usamos el EditText
            mainScreenActivity.setHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Ocultar el EditText cuando el fragmento no esté activo
        if (toolbarEditText != null) {
            toolbarEditText.setVisibility(View.GONE);
        }
    }
}
