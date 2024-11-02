package es.ucm.deckdraw.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import es.ucm.deckdraw.MainActivity;
import es.ucm.deckdraw.R;

public class DecksFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decks, container, false);

        // Botón para navegar a CreateDeckFragment
        Button createDeckButton = view.findViewById(R.id.button_create_deck);
        createDeckButton.setOnClickListener(v -> {
            // Reemplazar el fragmento actual por CreateDeckFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CreateDeckFragment())
                    .addToBackStack(null) // Añade a la pila de retroceso
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setToolbarTitle("Tus mazos");
            mainActivity.setHomeAsUpEnabled(false);
        }
    }
}
