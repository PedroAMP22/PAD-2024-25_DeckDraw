package es.ucm.deckdraw;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;

import es.ucm.deckdraw.ui.Activities.LogInActivity;
import es.ucm.deckdraw.ui.Activities.error_NetConnection;

public class MainActivity extends AppCompatActivity {


    private static final long SPLASH_SCREEN_DELAY = 3000;

    private static final String TAG = "Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ConnectivityManager conMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = conMgr.getActiveNetwork();
        NetworkCapabilities networkCap =
                conMgr.getNetworkCapabilities(network);
        if (networkCap!= null) {//Connection available
            Log.i(TAG, "Connected to network");
            //Keep this to start the Firebase database
            FirebaseApp.initializeApp(this);


            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();
            }, SPLASH_SCREEN_DELAY);
        } else {//Connection not available
            Log.i(TAG, "Couldn't connect to network");
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, error_NetConnection.class);
                startActivity(intent);
                finish();
            }, SPLASH_SCREEN_DELAY);

        }



    }


}