package es.ucm.deckdraw;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import es.ucm.deckdraw.Fragment.CreateDeckFragment;
import es.ucm.deckdraw.Fragment.DecksFragment;
import es.ucm.deckdraw.Fragment.FriendsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_friends) {
                selectedFragment = new FriendsFragment();
            } else if (itemId == R.id.nav_deck) {
                selectedFragment = new DecksFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Seleccionar el elemento "Decks" por defecto
        bottomNavigationView.setSelectedItemId(R.id.nav_deck);
      
        //Keep this to start the Firebase database
        FirebaseApp.initializeApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Quitar la flecha de retroceso y restablecer el título
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("DeckDraw"); // Título predeterminado
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack(); // Navegar hacia atrás en la pila de fragmentos
        return true;
    }

    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void setHomeAsUpEnabled(boolean enabled) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
        }
    }
}

