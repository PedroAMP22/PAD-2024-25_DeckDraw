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

public class CreateDeckFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_deck, container, false);

        Button addCardButton = view.findViewById(R.id.addCardButton);

        addCardButton.setOnClickListener(v -> {
            // Aquí puedes navegar al fragmento de búsqueda de cartas
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
        // Cambiar título y mostrar flecha de retroceso
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setToolbarTitle("Crear Mazo");
            mainActivity.setHomeAsUpEnabled(true);
        }
    }
}
