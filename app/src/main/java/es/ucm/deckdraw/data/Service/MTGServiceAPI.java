package es.ucm.deckdraw.data.Service;
import es.ucm.deckdraw.data.Objects.Cards.TCard;
import es.ucm.deckdraw.data.Objects.Cards.TDobleCard;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MTGServiceAPI {

    private static final String API_URL = "https://api.scryfall.com";
    private static final String USER_AGENT = "Demo/0.0"; // Es necesario poner esto para usar la api

    // Colores disponibles para el filtrado
    private static final List<String> AVAILABLE_COLORS = List.of(
            "w", "u", "b", "r", "g", "c"
    );

    // Formatos de juego disponibles (no están en la API)
    private static final List<String> AVAILABLE_FORMATS = List.of(
            "Standard", "Pioneer",
             "Modern", "Legacy", "Pauper", "Commander"
    );

    // Tipos de cartas disponibles (sin API)
    private static final List<String> AVAILABLE_CARD_TYPES = List.of(
            "Artifact", "Battle", "Conspiracy", "Creature", "Dungeon", "Emblem",
            "Enchantment", "Hero", "Instant", "Kindred", "Land", "Phenomenon",
            "Plane", "Planeswalker", "Scheme", "Sorcery", "Vanguard"
    );

    public MTGServiceAPI() {
    }

    public static List<String> getAvailableCardTypes() {
        return AVAILABLE_CARD_TYPES;
    }

    public static List<String> getAvailableColors() {
        return AVAILABLE_COLORS;
    }

    public static List<String> getAvailableFormats() {
        return AVAILABLE_FORMATS;
    }

    // Método que maneja la solicitud GET
    private String makeRequest(String endpoint) {
        try {
            URL url = new URL(API_URL + endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept", "application/json;q=0.9,*/*;q=0.8");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                return content.toString();
            } else {
                System.out.println("Error en la solicitud: Código " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public TCard parseCardObjectCarta(JSONObject card) throws JSONException {
        TCard cardMTG = new TCard();

        // imagenes de las cartas en varios sizes
        if (card.has("image_uris")) {
            JSONObject imageUris = card.getJSONObject("image_uris");
            cardMTG.setArtCropImageUrl(imageUris.has("art_crop") ? imageUris.getString("art_crop") : "");
            cardMTG.setSmallImageUrl(imageUris.has("small") ? imageUris.getString("small") : "");
            cardMTG.setNormalImageUrl(imageUris.has("normal") ? imageUris.getString("normal") : "");
            cardMTG.setLargeImageUrl(imageUris.has("large") ? imageUris.getString("large") : "");
        }

        // referencia: https://scryfall.com/docs/api/cards
        cardMTG.setID(card.has("id") ? card.getString("id") : "");
        cardMTG.setLanguage(card.has("lang") ? card.getString("lang") : "");
        cardMTG.setLayout(card.has("layout") ? card.getString("layout") : "");
        cardMTG.setCmc(card.has("cmc") ? card.getDouble("cmc") : 0.0);
        cardMTG.setName(card.has("name") ? card.getString("name") : "");
        cardMTG.setSetName(card.has("set_name") ? card.getString("set_name") : "");
        cardMTG.setBorderColor(card.has("border_color") ? card.getString("border_color") : "");
        cardMTG.setType(card.has("type_line") ? card.getString("type_line") : "");
        cardMTG.setRarity(card.has("rarity") ? card.getString("rarity") : "");

        // se supone que el color es un campo que siempre se devuelve
        if (card.has("color_identity")) {
            JSONArray JSONColors = card.getJSONArray("color_identity");
            List<String> colors = new ArrayList<>();
            for (int i = 0; i < JSONColors.length(); i++) {
                String color = JSONColors.getString(i);
                colors.add(color);
            }
            cardMTG.setColors(colors);
        }

        // parametros vacios dependiendo de cada carta
        cardMTG.setManaCost(card.has("mana_cost") ? card.getString("mana_cost") : "");
        cardMTG.setPower(card.has("power") ? card.getString("power") : "");
        cardMTG.setToughness(card.has("toughness") ? card.getString("toughness") : "");
        cardMTG.setText(card.has("oracle_text") ? card.getString("oracle_text") : "");
        cardMTG.setArtist(card.has("artist") ? card.getString("artist") : "");

        cardMTG.setLegal(true);

        return cardMTG;
    }

    public void searchCardById(String id) throws JSONException {
        String response = makeRequest("/cards/" + id);
        JSONObject card = new JSONObject(response);
        if (card.has("card_faces")) {
            TCard base = parseCardObjectCarta(card);
            // creamos la carta doble (por default tendra los campos obligatorios y luego
            // ambas caras individuales)
            TDobleCard dobleCard = new TDobleCard(base);

            JSONArray faces = card.getJSONArray("card_faces");

            TCard front = parseCardObjectCarta(faces.getJSONObject(0));
            TCard back = parseCardObjectCarta(faces.getJSONObject(1));

            dobleCard.setFront(front);
            dobleCard.setBack(back);

            dobleCard.printCardDetails();
        } else {
            TCard cardMTG = parseCardObjectCarta(card);

            cardMTG.printCardDetails();
        }

    }

    // busqueda de comanders
    public List<String> searchCommander(String query) {
        List<String> commanderList = new ArrayList<>();

        try {
            String responseBody = makeRequest("/cards/search?q=is:commander+" + query);

            JSONObject jsonResponse = new JSONObject(responseBody);
            int totalCards = jsonResponse.getInt("total_cards");

            boolean hasMore = jsonResponse.getBoolean("has_more");

            JSONArray cards = jsonResponse.getJSONArray("data");

            for (int i = 0; i < cards.length(); i++) {
                JSONObject card = cards.getJSONObject(i);
                String commander = card.getString("name");
                commanderList.add(commander);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No hay ninguna carta que coincida con los parametros de busqueda");

        }
        return commanderList;
    }

    // busqueda de cartas general con query.
    // Ej:https://api.scryfall.com/cards/search?order=cmc&q=c%3Ared+pow%3D3
    public List<TCard> searchCardsByQuery(String query) {

        List<TCard> cardList = new ArrayList<>();
        // poner try catch
        try {
            String responseBody = makeRequest("/cards/search?" + query);

            JSONObject jsonResponse = new JSONObject(responseBody);
            int totalCards = jsonResponse.getInt("total_cards");
            System.out.println("Total de cartas encontradas: " + totalCards);

            boolean hasMore = jsonResponse.getBoolean("has_more");
            System.out.println("¿Hay más páginas? " + hasMore);

            JSONArray cards = jsonResponse.getJSONArray("data");

            for (int i = 0; i < cards.length(); i++) {
                JSONObject card = cards.getJSONObject(i);

                if (card.has("card_faces")) {
                    TCard base = parseCardObjectCarta(card);
                    // creamos la carta doble (por default tendra los campos obligatorios y luego
                    // ambas caras individuales)
                    TDobleCard dobleCard = new TDobleCard(base);

                    JSONArray faces = card.getJSONArray("card_faces");

                    TCard front = parseCardObjectCarta(faces.getJSONObject(0));
                    TCard back = parseCardObjectCarta(faces.getJSONObject(1));

                    dobleCard.setFront(front);
                    dobleCard.setBack(back);

                    cardList.add(dobleCard);
                } else {
                    TCard cardMTG = parseCardObjectCarta(card);
                    cardList.add(cardMTG);

                }
            }

            System.out.println("Numero de Cartas en la lista: " + cardList.size());
            System.out.println("-----------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No hay ninguna carta que coincida con los parametros de busqueda");

        }

        return cardList;
    }

    public List<TCard> searchCardsFilters(String name, String format, List<String> colors, String type) {

        String allCollors = ""; // el formato de los colores es junto : wg por ejemplo
        for (String color : colors) {
            allCollors += color;
        }

        String finalQuery = "q="; // comienzo de la query

        // codificamos la consulta opor si el nombre tiene espacios
        try {
            finalQuery = finalQuery + URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // el formato de las cartas es obligatorio
        if (!format.equalsIgnoreCase("")) {
            finalQuery += "+f%3A" + format;
        }

        // si tiene colores los usamos de filtro
        if (!colors.isEmpty()) {
            finalQuery += "+c%3A" + allCollors;
        }

        if (!type.equalsIgnoreCase("")) {
            finalQuery += "+t%3A" + type;
        }

        System.out.println(finalQuery);

        return searchCardsByQuery(finalQuery);
    }

    public static void main(String[] args){
        MTGServiceAPI service = new MTGServiceAPI();

        // service.searchCardById("56ebc372-aabd-4174-a943-c7bf59e5028d");


        try {
            // Cambiar la salida estándar a un archivo
            PrintStream fileOut = new PrintStream(new File("cardDetailsOutput.txt"));
            System.setOut(fileOut);

            System.out.println("FORMATOS DISPONIBLES:");
            for (String format : service.getAvailableFormats()) {
                System.out.println("- " + format);
            }

            System.out.println("\nTIPOS DE CARTA DISPONIBLES:");
            for (String type : service.getAvailableCardTypes()) {
                System.out.println("- " + type);
            }

            System.out.println("\nTIPOS DE MANA: ");
            for (String color : service.getAvailableColors()) {
                System.out.printf("{" + color + "} ");
            }

            System.out.println(
                    "\n ---------------------------------------------------------------------------------------\n");

            System.out.println("BUSQUEDA DE CARTAS EN CUALQUIER FORMATO, COLOR Y TIPO QUE SE LLAMEN AVACYN");
            // Realizar la búsqueda y obtener la lista de cartas
            List<TCard> lista = service.searchCardsFilters("avacyn", "", new ArrayList<>(), "");

            // Imprimir los detalles de cada carta en el archivo
            for (TCard card : lista) {
                card.printCardDetails();
            }

            System.out.println("BUSQUEDA DE CARTAS DE COMMANDER SIN COLOR O TIPO (DEVUELVE 175 CARTAS DE 27987)");

            lista = service.searchCardsFilters("", "Commander", new ArrayList<>(), "");

            // Imprimir los detalles de cada carta en el archivo
            for (TCard card : lista) {
                card.printCardDetails();
            }

            System.out.println("CARTA DOBLE");

            lista = service.searchCardsFilters("thing in the ice", "", new ArrayList<>(), "");

            // Imprimir los detalles de cada carta en el archivo
            for (TCard card : lista) {
                card.printCardDetails();
            }

            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
