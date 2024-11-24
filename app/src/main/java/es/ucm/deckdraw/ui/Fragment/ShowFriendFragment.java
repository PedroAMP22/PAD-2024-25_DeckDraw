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
import es.ucm.deckdraw.ui.Adapter.FriendDeckAdapter;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;
import es.ucm.deckdraw.util.Callback;

public class ShowFriendFragment extends Fragment {


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
    private TUsers friend;
    private TUsers currentUser;

    private FriendDeckAdapter deckAdapter;
    private List<TDecks> deckList =  new ArrayList<>();
    private List<TDecks> friendDeckList =  new ArrayList<>();

    public  ShowFriendFragment(TUsers friend){
        this.friend = friend;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_friend, container, false);
        if (savedInstanceState != null && savedInstanceState.getBoolean("dialog_visible")) {
            // Recupera los datos guardados
            deckName = savedInstanceState.getString("deck_name");
            commanderName = savedInstanceState.getString("commander_name");
            formatPosition = savedInstanceState.getInt("format_position");
        }
        else{
            deckName = "";
            commanderName ="";
            formatPosition = 0;
        }


        CurrentUserManager sessionManager = new CurrentUserManager(requireContext());
        friend = sessionManager.getCurrentUser();
        currentUser = sessionManager.getCurrentUser();

        deckAdapter = new FriendDeckAdapter(deckList,this);
        RecyclerView recyclerView = view.findViewById(R.id.deckRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DecksAdmin db = new DecksAdmin();
        db.getUserDecks(friend.getIdusers(), new Callback<List<TDecks>>(){
            @Override
            public void onSuccess(List<TDecks> data) {

                friendDeckList = data;

                deckAdapter.setDecks(deckList);
                recyclerView.setAdapter(deckAdapter);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

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


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainScreenActivity) {
            MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
            mainScreenActivity.setToolbarTitle("Tus mazos");
            mainScreenActivity.setHomeAsUpEnabled(false);
        }
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

    public void copyDeck(TDecks deck){
        deckList.add(deck);
        DecksAdmin db = new DecksAdmin();
        db.createDeck(currentUser.getIdusers(), deck);
    }

    public void showDeck(){
        ShowDeckFragment sf = new ShowDeckFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, sf).addToBackStack(null).commit();
    }
}
