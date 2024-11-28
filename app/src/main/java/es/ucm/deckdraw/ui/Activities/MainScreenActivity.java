package es.ucm.deckdraw.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.ui.Fragment.DecksFragment;
import es.ucm.deckdraw.ui.Fragment.EditDeckFragment;
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

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_deck);
        }


        //Keep this to start the Firebase database
        FirebaseApp.initializeApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("DeckDraw"); // Título predeterminado
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        //para que la flecha para atras no destruya el fragment de editar mazo
        if (currentFragment instanceof EditDeckFragment) {
            ((EditDeckFragment) currentFragment).handleBackPressFromToolbar();
            return true;
        }
        getSupportFragmentManager().popBackStack();
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

