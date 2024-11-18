package es.ucm.deckdraw.data.dataBase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.data.Objects.decks.TDecks;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import es.ucm.deckdraw.data.Objects.decks.TDecks;
import es.ucm.deckdraw.util.Callback;

public class DecksAdmin {

    FirebaseDatabase db;

    public DecksAdmin() {
        this.db = FirebaseDatabase.getInstance();
    }

    public void createDeck(String uid, TDecks newDeck) {
        DatabaseReference decksRef = db.getReference("users").child(uid).child("decks");


        DatabaseReference newDeckRef = decksRef.push();
        newDeck.setIdDeck(newDeckRef.getKey());

        newDeckRef.setValue(newDeck)
                .addOnSuccessListener(aVoid -> Log.d("Firebase Database", "createDeck: success " + newDeckRef.getKey()))
                .addOnFailureListener(e -> Log.e("Firebase Database", "createDeck, failure", e));
    }


    public void updateDeck(TDecks deck, Callback<Boolean> callback) {
        String uid = deck.getDeckOwner();
        String deckId = deck.getIdDeck();
        String newName = deck.getDeckName();
        DatabaseReference deckRef = db.getReference("users").child(uid).child("decks").child(deckId).child("name");
        deckRef.setValue(newName)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }


    public void addCardToDeck(TDecks deck, TCard card, Callback<Void> callback) {
        List<TCard> updatedCards = deck.getCards();
        updatedCards.add(card);

        db.getReference().child(deck.getDeckOwner()).child(deck.getIdDeck()).child("Cards")
                .setValue(updatedCards)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public void removeCardFromDeck(TDecks deck, TCard card, Callback<Boolean> callback) {
        List<TCard> updatedCards = deck.getCards();
        updatedCards.remove(card);

        db.getReference().child(deck.getDeckOwner()).child(deck.getIdDeck()).child("Cards")
                .setValue(updatedCards)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public void deleteDeck(TDecks deck, Callback<Void> callback) {
        db.getReference().child(deck.getDeckOwner()).child(deck.getIdDeck())
                .removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public void getUserDecks(String userId, Callback<List<TDecks>> callback) {
        db.getReference().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<TDecks> userDecks = new ArrayList<>();
                for (DataSnapshot deckSnapshot : dataSnapshot.getChildren()) {
                    TDecks deck = deckSnapshot.getValue(TDecks.class);
                    if (deck != null) userDecks.add(deck);
                }
                callback.onSuccess(userDecks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure(databaseError.toException());
            }
        });
    }

}
