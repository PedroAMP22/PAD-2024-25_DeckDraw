package es.ucm.deckdraw.ui.Fragment;

import es.ucm.deckdraw.data.Objects.Cards.TCard;

public interface FragmentViewerInterface {

    void openDetails(TCard card);

    void cardWasUpdated(boolean added);

    public void removeCardFromDeck(TCard card);
}
