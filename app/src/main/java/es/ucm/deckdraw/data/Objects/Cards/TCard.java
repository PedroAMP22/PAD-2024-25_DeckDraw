package es.ucm.deckdraw.data.Objects.Cards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCard  implements Serializable {
    private String ID;
    private String largeImageUrl;
    private String normalImageUrl;
    private String smallImageUrl;
    private String artCropImageUrl;
    private String language;
    private String layout;
    private Boolean legal;
    private String manaCost;
    private String name;
    private String power;
    private String toughness;
    private String artist;
    private String borderColor;
    private String type;
    private String setName;
    private String rarity;
    private Double cmc;
    private String text;
    private List<String> colors;
    private Integer quantity;


    public TCard() {}


    // Constructor con parámetros
    public TCard(String ID, String largeImageUrl, String normalImageUrl, String smallImageUrl, String artCropImageUrl,
                 String language, String layout, Boolean legal, String manaCost, String name, String power,
                 String toughness, String artist, String borderColor, String type, String setName, String rarity,
                 Double cmc, String text ,List<String> colors) {
        this.ID = ID;
        this.largeImageUrl = largeImageUrl;
        this.normalImageUrl = normalImageUrl;
        this.smallImageUrl = smallImageUrl;
        this.artCropImageUrl = artCropImageUrl;
        this.language = language;
        this.layout = layout;
        this.legal = legal;
        this.manaCost = manaCost;
        this.name = name;
        this.power = power;
        this.toughness = toughness;
        this.artist = artist;
        this.borderColor = borderColor;
        this.type = type;
        this.setName = setName;
        this.rarity = rarity;
        this.cmc = cmc;
        this.text = text;
        this.colors = colors;
        this.quantity = 1;
    }

    // Getter y Setter para ID
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    // Getter y Setter para los colores
    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    // Getter y Setter para largeImageUrl
    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    // Getter y Setter para normalImageUrl
    public String getNormalImageUrl() {
        return normalImageUrl;
    }

    public void setNormalImageUrl(String normalImageUrl) {
        this.normalImageUrl = normalImageUrl;
    }

    // Getter y Setter para smallImageUrl
    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    // Getter y Setter para artCropImageUrl
    public String getArtCropImageUrl() {
        return artCropImageUrl;
    }

    public void setArtCropImageUrl(String artCropImageUrl) {
        this.artCropImageUrl = artCropImageUrl;
    }

    // Getter y Setter para language
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    // Getter y Setter para layout
    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    // Getter y Setter para legal
    public Boolean getLegal() {
        return legal;
    }

    public void setLegal(Boolean legal) {
        this.legal = legal;
    }

    // Getter y Setter para manaCost
    public String getManaCost() {
        return manaCost;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    // Getter y Setter para name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter y Setter para power
    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    // Getter y Setter para toughness
    public String getToughness() {
        return toughness;
    }

    public void setToughness(String toughness) {
        this.toughness = toughness;
    }

    // Getter y Setter para artist
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    // Getter y Setter para borderColor
    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    // Getter y Setter para type
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Getter y Setter para setName
    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    // Getter y Setter para rarity
    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    // Getter y Setter para cmc
    public Double getCmc() {
        return cmc;
    }

    public void setCmc(Double cmc) {
        this.cmc = cmc;
    }

    //Getter de quantity
    public Integer getQuantity(){return quantity;}
    //Añadir una carta
    public void addCardQuantity(){quantity++;}
    public void removeCardQuantity(){quantity--;}

    public void printCardDetails() {
        System.out.println("ID: " + ID);
        System.out.println("Large Image URL: " + largeImageUrl);
        System.out.println("Normal Image URL: " + normalImageUrl);
        System.out.println("Small Image URL: " + smallImageUrl);
        System.out.println("Art Crop Image URL: " + artCropImageUrl);
        System.out.println("Language: " + language);
        System.out.println("Layout: " + layout);
        System.out.println("Legal: " + legal);
        System.out.println("Mana Cost: " + manaCost);
        System.out.println("Name: " + name);
        System.out.println("Power: " + power);
        System.out.println("Toughness: " + toughness);
        System.out.println("Artist: " + artist);
        System.out.println("Border Color: " + borderColor);
        System.out.println("Type: " + type);
        System.out.println("Set Name: " + setName);
        System.out.println("Rarity: " + rarity);
        System.out.println("CMC: " + cmc);
        System.out.println("Text: " + text);

        // Si TColor tiene un método para obtener los colores, por ejemplo, getColors(), puedes imprimirlos así:
        System.out.print("Colors: ");
        if (colors.isEmpty()) {
            System.out.print("None/Colorless");
        } else {
            for (String color : colors) {
                System.out.print(color + " ");
            }
        }
        System.out.println("\n----------------------------------------------------------------------------------------------\n");
    }



    public String getCardDetails() {
        StringBuilder details = new StringBuilder();

        details.append("ID: ").append(ID).append("\n");
        details.append("Large Image URL: ").append(largeImageUrl).append("\n");
        details.append("Normal Image URL: ").append(normalImageUrl).append("\n");
        details.append("Small Image URL: ").append(smallImageUrl).append("\n");
        details.append("Art Crop Image URL: ").append(artCropImageUrl).append("\n");
        details.append("Language: ").append(language).append("\n");
        details.append("Layout: ").append(layout).append("\n");
        details.append("Legal: ").append(legal).append("\n");
        details.append("Mana Cost: ").append(manaCost).append("\n");
        details.append("Name: ").append(name).append("\n");
        details.append("Power: ").append(power).append("\n");
        details.append("Toughness: ").append(toughness).append("\n");
        details.append("Artist: ").append(artist).append("\n");
        details.append("Border Color: ").append(borderColor).append("\n");
        details.append("Type: ").append(type).append("\n");
        details.append("Set Name: ").append(setName).append("\n");
        details.append("Rarity: ").append(rarity).append("\n");
        details.append("CMC: ").append(cmc).append("\n");
        details.append("Text: ").append(text).append("\n");

        // Añadir detalles de los colores de la carta
        details.append("Colors: ");
        if (colors.isEmpty()) {
            details.append("None/Colorless");
        } else {
            for (String color : colors) {
                details.append(color).append(" ");
            }
        }
        details.append("\n----------------------------------------------------\n");

        return details.toString();
    }

}

