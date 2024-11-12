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

    private Map<TCard, Integer> cardSearcher;


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
    public void addNumCard() { numCards++;}
    public void removeNumCard() { numCards--;}

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

    public void addCard(TCard card){
        Cards.add(card);
        //Si la carta ya está +1, sino la añadimos
        addCardToCardSearcher(card);
    }
    public void addCardToCardSearcher(TCard card){
        cardSearcher.put(card, cardSearcher.getOrDefault(card, 1) + 1);
    }
    public void removeCard(TCard card){
        Cards.remove(card);

        if (cardSearcher.containsKey(card)) {
            int currentCount = cardSearcher.get(card);
            if (currentCount > 1) {
                cardSearcher.put(card, currentCount - 1);
            } else {
                cardSearcher.remove(card);
            }
        }
    }

    public Integer getNumberOfCardInDeck(TCard card){
        if(cardSearcher.containsKey(card))
            return cardSearcher.get(card);
        return 0;
    }

    public List<TCard> getCards(){return Cards;}



    public void setCommander(TCard commander){this.commander = commander;}


}
