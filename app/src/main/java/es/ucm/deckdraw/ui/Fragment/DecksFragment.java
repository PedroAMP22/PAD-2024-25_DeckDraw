package es.ucm.deckdraw.ui.Fragment;

import android.app.Dialog;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        FloatingActionButton createDeckButton = view.findViewById(R.id.button_create_deck);
        createDeckButton.setOnClickListener(v -> {
            // Llamar al método que abre el diálogo
            showCreateDeckDialog();
        });

        return view;
    }

    private void showCreateDeckDialog() {
        // Crear el diálogo
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_create_deck);
        dialog.setCancelable(true);

        // Obtener las referencias a los elementos del layout
        EditText deckNameEditText = dialog.findViewById(R.id.editTextDeckName);
        Button createDeckButton = dialog.findViewById(R.id.buttonCreateDeck);

        createDeckButton.setOnClickListener(v -> {
            String deckName = deckNameEditText.getText().toString();
            if (!deckName.isEmpty()) {

                dialog.dismiss();
            } else {
                deckNameEditText.setError("Por favor ingrese un nombre para el mazo.");
            }
        });

        dialog.show();
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
