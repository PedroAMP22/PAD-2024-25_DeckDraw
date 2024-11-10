package es.ucm.deckdraw.ui.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import es.ucm.deckdraw.data.Objects.decks.TDecks;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> currentDeckName = new MutableLiveData<>();
    private final MutableLiveData<String> currentSearchQuery = new MutableLiveData<>();
    private final MutableLiveData<TDecks> currentDeck = new MutableLiveData<>();

    // Método para manejar el nombre del mazo
    public void setCurrentDeckName(String name) {
        currentDeckName.setValue(name);
    }

    public LiveData<String> getCurrentDeckName() {
        return currentDeckName;
    }

    // Método para manejar la consulta de búsqueda
    public void setCurrentSearchQuery(String query) {
        currentSearchQuery.setValue(query);
    }

    public LiveData<String> getCurrentSearchQuery() {
        return currentSearchQuery;
    }

    public void setCurrentDeck(TDecks deck) {
        currentDeck.setValue(deck);
    }

    public LiveData<TDecks> getCurrentDeck() {
        return currentDeck;
    }


    // Agrega más LiveData y métodos según lo necesites
}
