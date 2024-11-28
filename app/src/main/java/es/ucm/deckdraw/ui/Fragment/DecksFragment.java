package es.ucm.deckdraw.ui.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.data.Objects.decks.TDecks;
import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.data.Service.CommanderLoaderCallbacks;
import es.ucm.deckdraw.data.Service.MTGServiceAPI;
import es.ucm.deckdraw.data.dataBase.CurrentUserManager;
import es.ucm.deckdraw.data.dataBase.DecksAdmin;
import es.ucm.deckdraw.data.dataBase.UsersAdmin;
import es.ucm.deckdraw.ui.Activities.LogInActivity;
import es.ucm.deckdraw.ui.Activities.MainScreenActivity;
import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.Adapter.DeckAdapter;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;
import es.ucm.deckdraw.util.Callback;

public class DecksFragment extends Fragment {


        private AutoCompleteTextView commanderAutoComplete;
        private TextView commanderText ;
        private Context context;
        private LoaderManager manager;
        private CommanderLoaderCallbacks callback;
        private ArrayAdapter<String> commanderAdapter;
        private String deckName;
        private String commanderName;
        private int formatPosition;
        private Dialog dialog;

        private List<TCard> commanders;
        private TUsers currentUser;

        private DeckAdapter deckAdapter;
        private List<TDecks> deckList =  new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decks, container, false);
        if (savedInstanceState != null && savedInstanceState.getBoolean("dialog_visible")) {
            // Recupera los datos guardados
            deckName = savedInstanceState.getString("deck_name");
            commanderName = savedInstanceState.getString("commander_name");
            formatPosition = savedInstanceState.getInt("format_position");
            // Vuelve a mostrar el diálogo y restaura los datos
            showCreateDeckDialog();
        }
        else{
            deckName = "";
            commanderName ="";
            formatPosition = 0;
        }

        FloatingActionButton createDeckButton = view.findViewById(R.id.button_create_deck);
        createDeckButton.setOnClickListener(v -> {

            showCreateDeckDialog();
        });
        CurrentUserManager sessionManager = new CurrentUserManager(requireContext());
        currentUser = sessionManager.getCurrentUser();


        deckAdapter = new DeckAdapter(deckList,this);
        RecyclerView recyclerView = view.findViewById(R.id.deckRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DecksAdmin db = new DecksAdmin();
        db.getUserDecks(currentUser.getIdusers(), new Callback<List<TDecks>>(){
            @Override
            public void onSuccess(List<TDecks> data) {

                deckList = data;

                deckAdapter.setDecks(deckList);
                recyclerView.setAdapter(deckAdapter);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        return view;
    }

    private void showCreateDeckDialog() {
        // Crear el diálog
        context = requireContext();
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_create_deck);
        dialog.setCancelable(true);


        // Obtener las referencias a los elementos del layout
        EditText deckNameEditText = dialog.findViewById(R.id.editTextDeckName);
        Spinner formatSpinner = dialog.findViewById(R.id.spinnerFormat);
        deckNameEditText.setText(deckName);
        formatSpinner.setSelection(formatPosition);

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
        manager = LoaderManager.getInstance(this);
        commanderAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        commanderAutoComplete.setAdapter(commanderAdapter);
        commanderAutoComplete.setThreshold(1);
        callback = new CommanderLoaderCallbacks(context,this);
        commanderAutoComplete.setText(commanderName);
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
                    Bundle args = new Bundle();
                    args.putString("Commander", charSequence.toString());
                    manager.restartLoader(0, args, callback);
                };

                // Ejecuta el Runnable después de 500 ms
                commanderAutoComplete.postDelayed(searchCommanderRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.endsWith(",")) {
                    editable.delete(text.length() - 1, text.length());
                }
            }
        });


        Button createDeckButton = dialog.findViewById(R.id.buttonCreateDeck);

        createDeckButton.setOnClickListener(v -> {
            String deckName = deckNameEditText.getText().toString();
            if (!deckName.isEmpty() && formatSpinner.getSelectedItem() != null) {

                TDecks deck = new TDecks(currentUser.getIdusers(), "",0, formatSpinner.getSelectedItem().toString(),deckName,"id" );
                if(formatSpinner.getSelectedItem().toString().equals("Commander")){
                    if(commanderAutoComplete.getText().toString().isEmpty()){
                        deckNameEditText.setError("Por favor ingrese un commandante para el mazo.");
                        return;
                    }
                    String selectedCommander = commanderAutoComplete.getText().toString();
                    for (TCard card : commanders) {
                        if (card.getName().equals(selectedCommander)) {
                            deck.setCommander(card);
                            deck.setUrlDeckCover(card.getArtCropImageUrl());
                            break;
                        }
                    }

                }


                DecksAdmin db = new DecksAdmin();
                db.createDeck(currentUser.getIdusers(), deck);
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

    public void setCommander(List<TCard> commander){
        this.commanders = commander;
        List<String> commanderNames = new ArrayList<>();
        for (TCard card : commander) {
            commanderNames.add(card.getName());
        }
        commanderAdapter.clear();
        commanderAdapter.addAll(commanderNames);
        commanderAdapter.notifyDataSetChanged();
        commanderAutoComplete.setAdapter(commanderAdapter);
    }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

            // Guarda el estado del diálogo y los datos de los campos de texto
            if (dialog != null && dialog.isShowing()) {
                outState.putBoolean("dialog_visible", true);

                // Guarda el texto de los campos
                EditText editTextDeckName = dialog.findViewById(R.id.editTextDeckName);
                MultiAutoCompleteTextView autoCompleteTextViewCommander = dialog.findViewById(R.id.multiAutoCompleteTextViewCommander);
                Spinner spinnerFormat = dialog.findViewById(R.id.spinnerFormat);
                if (editTextDeckName != null) {
                    deckName = editTextDeckName.getText().toString();
                    outState.putString("deck_name", deckName);
                }
                if (autoCompleteTextViewCommander != null) {
                    commanderName = autoCompleteTextViewCommander.getText().toString();
                    outState.putString("commander_name", commanderName);
                }
                if (spinnerFormat != null) {
                    formatPosition = spinnerFormat.getSelectedItemPosition();
                    outState.putInt("format_position", formatPosition);
                }
            }
        }

    public void onEditDeck(TDecks deck) {
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.setCurrentDeck(deck);
        // Navegar al fragmento de edición de mazo
        EditDeckFragment editDeckFragment = new EditDeckFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editDeckFragment)
                .addToBackStack(null)
                .commit();
    }
}
