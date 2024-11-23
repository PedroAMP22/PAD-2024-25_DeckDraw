package es.ucm.deckdraw.data.dataBase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.data.Objects.decks.TDecks;


import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<String, Object> updates = new HashMap<>();
        if (deck.getDeckName() != null) {
            updates.put("deckName", deck.getDeckName());
        }

        if(deck.getCards() != null){
            updates.put("cards", deck.getCards());
        }

        updates.put("numCards", deck.getCards().size());

        DatabaseReference deckRef = db.getReference("users").child(uid).child("decks").child(deckId);
        deckRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }


    public void deleteDeck(TDecks deck, Callback<Boolean> callback) {
        db.getReference().child("users").child(deck.getDeckOwner()).child("decks").child(deck.getIdDeck())
                .removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess(true))
                .addOnFailureListener(unused -> callback.onSuccess(false));
    }

    public void getUserDecks(String userId, Callback<List<TDecks>> callback) {
        db.getReference().child("users").child(userId).child("decks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<TDecks> userDecks = new ArrayList<>();
                for (DataSnapshot deckSnapshot : dataSnapshot.getChildren()) {
                    TDecks deck = deckSnapshot.getValue(TDecks.class);
                    db.getReference().child("users").child(deck.getDeckOwner()).child("decks").child(deck.getIdDeck()).child("cards").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<TCard> cards = new ArrayList<>();
                            for (DataSnapshot cardSnapshot : snapshot.getChildren()) {
                                TCard card = cardSnapshot.getValue(TCard.class);
                                cards.add(card);
                            }
                            deck.setCards(cards);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callback.onFailure(error.toException());

                        }
                    });
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
