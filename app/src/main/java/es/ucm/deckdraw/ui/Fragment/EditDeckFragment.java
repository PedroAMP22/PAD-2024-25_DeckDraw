package es.ucm.deckdraw.ui.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.ui.Activities.MainScreenActivity;
import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.Adapter.CardDeckAdapter;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class EditDeckFragment extends Fragment{
    private SharedViewModel sharedViewModel;
    private EditText toolbarEditText;
    private Context context;
    private String deckName;
    private RecyclerView recyclerView;
    private CardDeckAdapter adapter;
    private List<TCard> cardList = new ArrayList<>(); // Lista para almacenar las cartas

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_deck, container, false);
        context = this.getContext();
        // Inicialización del ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);


        MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
        toolbarEditText = mainScreenActivity.findViewById(R.id.toolbarEditText);

        adapter = new CardDeckAdapter(cardList, this);
        // Configuración del RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewDeck);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);

        if (toolbarEditText != null) {
            toolbarEditText.setVisibility(View.VISIBLE);

            sharedViewModel.getCurrentDeck().observe(getViewLifecycleOwner(), deck -> {
                if (deck != null) {
                    toolbarEditText.setText(deck.getDeckName());
                    deckName = deck.getDeckName();
                    cardList = new ArrayList<>(deck.getCards());
                    refreshCardList(view);
                }
            });
        }

        FloatingActionButton addCardButton = view.findViewById(R.id.addCardFab);
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
    public void onDestroyView() {
        super.onDestroyView();
        if (!Objects.equals(deckName, toolbarEditText.getText().toString())) {
            showSaveChangesDialog();
        }
    }

    private void showSaveChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Guardar cambios");
        builder.setMessage("¿Deseas guardar los cambios antes de salir?");
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            if (toolbarEditText != null) {
                dialog.dismiss();
                //CUANDO ESTE LA DB
                Toast.makeText(context, "Cambios guardados", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Descartar", (dialog, which) -> {
            Toast.makeText(context, "Cambios descartados", Toast.LENGTH_SHORT).show();
        });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Toast.makeText(context, "Cambios descartados", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
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

        // Ocultar la BottomNavigationView
        if (getActivity() != null) {
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Ocultar el EditText cuando el fragmento no esté activo
        if (toolbarEditText != null) {
            toolbarEditText.setVisibility(View.GONE);
        }

        // Mostrar la BottomNavigationView al salir del fragmento
        if (getActivity() != null) {
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    public void refreshCardList(View view) {
        adapter.updateCardList(cardList);
    }



    public void openDetails(TCard card) {
        CardDetailFragment frag = new CardDetailFragment(card);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).addToBackStack(null).commit();
    }

}
