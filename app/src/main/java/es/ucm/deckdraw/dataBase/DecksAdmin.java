package es.ucm.deckdraw.dataBase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import es.ucm.deckdraw.decks.TDecks;

public class DecksAdmin {

    FirebaseDatabase db;

    public DecksAdmin() {
        this.db = FirebaseDatabase.getInstance();
    }

    public void createDeck(String uid, TDecks newDeck) {
        DatabaseReference decksRef = db.getReference("users").child(uid).child("decks");


        DatabaseReference newDeckRef = decksRef.push();
        newDeck.setIdDeck(newDeckRef.getKey());

        newDeckRef.child("cards").setValue(new HashMap<>());



        newDeckRef.setValue(newDeck)
                .addOnSuccessListener(aVoid -> Log.d("Firebase Database", "createDeck: success " + newDeckRef.getKey()))
                .addOnFailureListener(e -> Log.e("Firebase Database", "createDeck, failure", e));
    }
}
