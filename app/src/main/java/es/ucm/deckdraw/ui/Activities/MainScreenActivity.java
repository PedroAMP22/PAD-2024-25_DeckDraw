package es.ucm.deckdraw.ui.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.Fragment.DecksFragment;
import es.ucm.deckdraw.ui.Fragment.FriendsFragment;
import com.google.firebase.FirebaseApp;

public class MainScreenActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

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

        // Cargar el fragmento por defecto solo si el estado es nulo (es decir, no se está recreando la actividad)
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_deck);
        }
      
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

