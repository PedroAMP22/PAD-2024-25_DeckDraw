package es.ucm.deckdraw.data.Objects.decks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.deckdraw.data.Objects.Cards.TCard;

public class TDecks {

    private String idDeck;
    private String deckName;
    private String deckFormat;
    private int numCards;
    private String urlDeckCover;
    private String deckOwner;
    private List<TCard> Cards;
    private TCard commander;
    private Map<String, Integer> cardSearcher;

    public TDecks() {
        this.deckOwner = "";
        this.urlDeckCover = "";
        this.numCards = 0;
        this.deckFormat = "";
        this.deckName = "";
        this.idDeck = "";
        this.Cards = new ArrayList<>();
        commander = null;
        cardSearcher = new HashMap<>();
    }


    public TDecks(String deckOwner, String urlDeckCover, int numCards, String deckFormat, String deckName, String idDeck) {
        this.deckOwner = deckOwner;
        this.urlDeckCover = urlDeckCover;
        this.numCards = numCards;
        this.deckFormat = deckFormat;
        this.deckName = deckName;
        this.idDeck = idDeck;
        this.Cards = new ArrayList<>();
        this.cardSearcher = new HashMap<>();
        commander = null;
    }

    public String getIdDeck() {
        return idDeck;
    }

    public void setIdDeck(String idDeck) {
        this.idDeck = idDeck;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getDeckFormat() {
        return deckFormat;
    }

    public void setDeckFormat(String deckFormat) {
        this.deckFormat = deckFormat;
    }

    public int getNumCards() {
        return numCards;
    }

    //Para cuando se añade mas de una misma carta
    public void addNumCard(int q) { numCards+=q;}
    public void removeNumCard(int q) { numCards-=q;}

    public void setNumCards(int numCards) {
        this.numCards = numCards;
    }

    public String getUrlDeckCover() {
        return urlDeckCover;
    }

    public void setUrlDeckCover(String urlDeckCover) {
        this.urlDeckCover = urlDeckCover;
    }

    public String getDeckOwner() {
        return deckOwner;
    }

    public void setDeckOwner(String deckOwner) {
        this.deckOwner = deckOwner;
    }

    public void populateCardSearcher(){
        if(!cardSearcher.isEmpty())
            return;
        for(TCard card: Cards){
            cardSearcher.put(card.getID(), cardSearcher.getOrDefault(card.getID(), 0) + 1);
        }
    }


    public void addCard(TCard card, int quantity){
        Cards.add(card);
        cardSearcher.put(card.getID(), cardSearcher.getOrDefault(card.getID(), 0) + quantity);
        addNumCard(quantity);

    }
    public void removeCard(TCard card, int quantity){

        if (cardSearcher.containsKey(card.getID())) {
            int currentCount = cardSearcher.get(card.getID());
            if (currentCount > quantity) {
                cardSearcher.put(card.getID(), currentCount - quantity);
            } else {
                cardSearcher.remove(card.getID());
            }
            Cards.remove(card);
            removeNumCard(quantity);
        }
    }

    public Integer getNumberOfCardInDeck(TCard card){
        if(cardSearcher.containsKey(card.getID()))
            return cardSearcher.get(card.getID());
        return 0;
    }


    public List<TCard> getCards(){return Cards;}
    public void setCards(List<TCard> cards){
        Cards = cards;
    }

    public void setCommander(TCard commander){this.commander = commander;}

    public TCard getCommander(){
        return this.commander;
    }

    public Map<String, Integer> getCardSearcher() {
        return cardSearcher;
    }
}
