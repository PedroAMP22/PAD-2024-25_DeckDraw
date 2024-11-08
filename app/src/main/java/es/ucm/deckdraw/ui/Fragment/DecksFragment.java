package es.ucm.deckdraw.ui.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import es.ucm.deckdraw.data.Service.CommanderLoaderCallbacks;
import es.ucm.deckdraw.data.Service.MTGServiceAPI;
import es.ucm.deckdraw.ui.Activities.MainScreenActivity;
import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;

    public class DecksFragment extends Fragment {


        MultiAutoCompleteTextView commanderAutoComplete;
        TextView commanderText ;
        int counter;
        Context context;
        LoaderManager manager;
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
        // Crear el diálog
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_create_deck);
        dialog.setCancelable(true);
        context = this.getContext();

        // Obtener las referencias a los elementos del layout
        EditText deckNameEditText = dialog.findViewById(R.id.editTextDeckName);
        Spinner formatSpinner = dialog.findViewById(R.id.spinnerFormat);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, MTGServiceAPI.getAvailableFormats());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        formatSpinner.setAdapter(adapter);
        formatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                if (selectedItem.equals("Commander")) {
                    commanderAutoComplete.setVisibility(View.VISIBLE);
                    commanderText.setVisibility(View.VISIBLE);
                }
                else{
                    commanderAutoComplete.setVisibility(View.INVISIBLE);
                    commanderText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                commanderAutoComplete.setVisibility(View.INVISIBLE);
                commanderText.setVisibility(View.INVISIBLE);
            }
        });
        commanderAutoComplete = dialog.findViewById(R.id.multiAutoCompleteTextViewCommander);
        commanderText = dialog.findViewById(R.id.textViewCommander);
        commanderAutoComplete.setVisibility(View.INVISIBLE);
        commanderText.setVisibility(View.INVISIBLE);
        counter = 0;
        manager = LoaderManager.getInstance(this);
        ArrayAdapter<String> commanderAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());

        commanderAutoComplete.addTextChangedListener(new TextWatcher() {
            private Runnable searchCommanderRunnable = null;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchCommanderRunnable != null) {
                    commanderAutoComplete.removeCallbacks(searchCommanderRunnable);
                }

                searchCommanderRunnable = () -> {
                    MTGServiceAPI api = new MTGServiceAPI();
                    Bundle args = new Bundle();
                    args.putString("Commander", charSequence.toString());
                    CommanderLoaderCallbacks callback = new CommanderLoaderCallbacks(context);
                    // Inicia o reinicia el Loader
                    manager.restartLoader(0, args, callback);
                    // Actualiza la lista de datos del adaptador sin recrearlo
                    commanderAdapter.clear();
                    commanderAdapter.addAll(callback.getCommanders());
                    commanderAdapter.notifyDataSetChanged();
                };

                // Ejecuta el Runnable después de 500 ms
                commanderAutoComplete.postDelayed(searchCommanderRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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
