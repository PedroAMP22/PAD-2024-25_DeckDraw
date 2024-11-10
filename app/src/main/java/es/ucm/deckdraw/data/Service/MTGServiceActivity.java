package es.ucm.deckdraw.data.Service;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.ucm.deckdraw.ui.Adapter.CardTextAdapter;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.R;

public class MTGServiceActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<TCard>> {

    private static final int LOADER_ID_AVACYN = 1;
    private static final int LOADER_ID_COMMANDER = 2;
    private static final int LOADER_ID_THING_IN_THE_ICE = 3;
    private TextView resultTextView;
    private MTGServiceAPI service;
    private RecyclerView recyclerView; // Añadido para el RecyclerView
    private CardTextAdapter cardTextAdapter; // Adaptador para el RecyclerView
    private List<TCard> cardList = new ArrayList<>(); // Lista para almacenar las cartas


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mtgservice);

        resultTextView = findViewById(R.id.resultTextView);
        resultTextView.setMovementMethod(new ScrollingMovementMethod());
        service = new MTGServiceAPI();

        recyclerView = findViewById(R.id.recyclerView); // Inicializar el RecyclerView

        // Configurar el RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardTextAdapter = new CardTextAdapter(cardList); // Inicializar el adaptador
        recyclerView.setAdapter(cardTextAdapter); // Establecer el adaptador en el RecyclerView

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Mostrar detalles generales de formatos, tipos y colores
        displayServiceDetails();

        // Iniciar las búsquedas asíncronas con Loaders
        initiateCardLoaders();
    }

    private void displayServiceDetails() {
        StringBuilder details = new StringBuilder();

        // Obtener y mostrar formatos disponibles
        details.append("FORMATOS DISPONIBLES:\n");
        for (String format : service.getAvailableFormats()) {
            details.append("- ").append(format).append("\n");
        }

        // Obtener y mostrar tipos de carta disponibles
        details.append("\nTIPOS DE CARTA DISPONIBLES:\n");
        for (String type : service.getAvailableCardTypes()) {
            details.append("- ").append(type).append("\n");
        }

        // Obtener y mostrar colores disponibles
        details.append("\nTIPOS DE MANA:\n");
        for (String color : service.getAvailableColors()) {
            details.append("{" + color + "} ");
        }

        details.append("\n\n---------------------------------------------------------------------------------------\n");
        resultTextView.setText(details.toString());
    }

    private void initiateCardLoaders() {
        // Configurar la búsqueda de "Avacyn"
        Bundle avacynParams = new Bundle();
        avacynParams.putString("name", "avacyn");
        avacynParams.putString("format", "");
        avacynParams.putString("type", "");
        avacynParams.putStringArrayList("colors", new ArrayList<>());

        // Configurar la búsqueda de formato "Commander"
        Bundle commanderParams = new Bundle();
        commanderParams.putString("name", "");
        commanderParams.putString("format", "Commander");
        commanderParams.putStringArrayList("types", new ArrayList<>());
        commanderParams.putStringArrayList("colors", new ArrayList<>());
        commanderParams.putStringArrayList("rarity", new ArrayList<>());


        // Configurar la búsqueda de "Thing in the Ice"
        Bundle thingInTheIceParams = new Bundle();
        thingInTheIceParams.putString("name", "thing in the ice");
        thingInTheIceParams.putString("format", "");
        thingInTheIceParams.putStringArrayList("types", new ArrayList<>());
        thingInTheIceParams.putStringArrayList("colors", new ArrayList<>());
        thingInTheIceParams.putStringArrayList("rarity", new ArrayList<>());


        // Iniciar Loaders para cada búsqueda
        LoaderManager.getInstance(this).initLoader(LOADER_ID_AVACYN, avacynParams, this);
        LoaderManager.getInstance(this).initLoader(LOADER_ID_COMMANDER, commanderParams, this);
        LoaderManager.getInstance(this).initLoader(LOADER_ID_THING_IN_THE_ICE, thingInTheIceParams, this);
    }

    @Override
    public Loader<List<TCard>> onCreateLoader(int id, Bundle args) {
        String name = args.getString("name", "");
        String format = args.getString("format", "");
        ArrayList<String> colors = args.getStringArrayList("colors");
        ArrayList<String> types = args.getStringArrayList("types");
        ArrayList<String> rarity = args.getStringArrayList("rarity");

        // Crear el loader y forzar su carga
        CardLoader loader = new CardLoader(this, name, format, colors, types,rarity);
        loader.forceLoad(); // Forzamos la ejecución del Loader
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<TCard>> loader, List<TCard> data) {
        StringBuilder output = new StringBuilder(resultTextView.getText());

        // Mostrar título según el ID del loader
        if (loader.getId() == LOADER_ID_AVACYN) {
            output.append("\nBUSQUEDA DE CARTAS QUE SE LLAMEN AVACYN:\n");
        } else if (loader.getId() == LOADER_ID_COMMANDER) {
            output.append("\nBUSQUEDA DE CARTAS EN FORMATO COMMANDER:\n");
        } else if (loader.getId() == LOADER_ID_THING_IN_THE_ICE) {
            output.append("\nBUSQUEDA DE CARTA - THING IN THE ICE:\n");
        }

        // Agregar los resultados obtenidos de cada búsqueda
        if (data != null && !data.isEmpty()) {
            cardList.clear(); // Limpiar la lista anterior
            cardList.addAll(data); // Añadir los nuevos datos
            cardTextAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado

            for (TCard card : data) {
                output.append(card.getCardDetails()).append("\n");
            }
        } else {
            output.append("No se encontraron cartas para esta consulta.\n");
        }

        output.append("\n---------------------------------------------------------------------------------------\n");

        // Actualizar el TextView y guardar el resultado en el archivo
        resultTextView.setText(output.toString());
        saveOutputToFile(output.toString());
    }

    @Override
    public void onLoaderReset(Loader<List<TCard>> loader) {
        // No se necesita limpieza específica aquí
    }

    private void saveOutputToFile(String output) {
        try (FileOutputStream fos = openFileOutput("cardDetailsOutput.txt", MODE_PRIVATE)) {
            fos.write(output.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
