package es.ucm.deckdraw.ui.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import es.ucm.deckdraw.data.Objects.decks.TDecks;
import java.util.ArrayList;
import java.util.List;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.data.Objects.users.TUsers;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> currentDeckName = new MutableLiveData<>();
    private final MutableLiveData<String> currentSearchQuery = new MutableLiveData<>();
    private final MutableLiveData<TDecks> currentDeck = new MutableLiveData<>();
    private final MutableLiveData<String> currentSearchFormat = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<String>> currentManaColors = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<String>> currentCardRarity = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<String>> currentCardTypes = new MutableLiveData<>();
    private final MutableLiveData<List<TCard>> currentCardSearchResults = new MutableLiveData<>();
    //For user
    private final MutableLiveData<TUsers> currentUser = new MutableLiveData<>();
    // For selected Card
    private final MutableLiveData<TCard> selectedCard = new MutableLiveData<>();
    private final MutableLiveData<Boolean> editableCard = new MutableLiveData<>();



    public void setCurrentUser(TUsers user) {
        currentUser.setValue(user);
    }

    public LiveData<TUsers> getCurrentUser() {
        return currentUser;
    }

    public void setSelectedCard(TCard card) {
        selectedCard.setValue(card);
    }

    public LiveData<TCard> getSelectedCard() {return selectedCard;}

    public  void setEditableCard (Boolean editable){editableCard.setValue(editable);}

    public LiveData<Boolean> getEditableCard() {return editableCard;}


    // Método para manejar el nombre del mazo
    public void setCurrentDeckName(String name) {currentDeckName.setValue(name);}

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

    // Método para manejar EL FORMATO DE LA BUSQUEDA
    public void setCurrentSearchFormat(String format) {
        currentSearchFormat.setValue(format);
    }

    public LiveData<String> getCurrentSearchFormat() {
        return currentSearchFormat;
    }

    public void setManaColors(ArrayList<String> colorList) {currentManaColors.setValue(colorList);}

    public LiveData<ArrayList<String>> getCurrentManaColors(){return currentManaColors;}

    public void setCardRarity(ArrayList<String> rarityList) {currentCardRarity.setValue(rarityList);}

    public LiveData<ArrayList<String>> getCurrentCardRarity(){return currentCardRarity;}

    public void setCardTypes(ArrayList<String> typesList) {currentCardTypes.setValue(typesList);}

    public LiveData<ArrayList<String>> getCurrentCardTypes(){return currentCardTypes;}

    public void setCurrentCardSearchResults(List<TCard> cardResultList){currentCardSearchResults.setValue(cardResultList);}

    public LiveData<List<TCard>> getCurrentSearchResults() { return currentCardSearchResults;}
}
