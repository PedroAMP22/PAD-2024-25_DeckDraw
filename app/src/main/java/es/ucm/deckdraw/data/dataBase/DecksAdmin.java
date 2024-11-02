package es.ucm.deckdraw.data.dataBase;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import es.ucm.deckdraw.data.Objects.decks.TDecks;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import es.ucm.deckdraw.data.Objects.decks.TDecks;

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


    public TDecks searchDeck(String uidD, String uidU){
        final TDecks[] deck = {null};

        DatabaseReference ref = db.getReference();
        ref.child(uidU).child("decks").child(uidD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    deck[0] = dataSnapshot.getValue(TDecks.class);
                    Log.d("Username", "El nombre de usuario es: " + deck[0].getIdDeck());
                } else {
                    Log.d("Username", "El usuario no existe en la base de datos.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error al leer el nombre de usuario: " + databaseError.getMessage());
            }


        });

        return deck[0];
    }

}
