package es.ucm.deckdraw.Model;

import java.util.ArrayList;
import java.util.List;

public class TColor {

    // Lista para almacenar los colores de la carta usando sus abreviaturas.
    private List<String> colors;

    // Constructor que inicializa una carta sin colores (colorless).
    public TColor() {
        this.colors = new ArrayList<>();
    }

    // Constructor que acepta una lista de colores.
    public TColor(List<String> colors) {
        this.colors = colors != null ? colors : new ArrayList<>();
    }

    // Método para agregar un color (ejemplo: "W" para blanco).
    public void addColor(String color) {
        if (color != null && !color.isEmpty() && !colors.contains(color)) {
            colors.add(color);
        }
    }

    // Método para remover un color.
    public void removeColor(String color) {
        colors.remove(color);
    }

    // Método para verificar si tiene un color en particular.
    public boolean hasColor(String color) {
        return colors.contains(color);
    }

    // Método para obtener todos los colores.
    public List<String> getColors() {
        return new ArrayList<>(colors); // Retorna una copia para evitar modificaciones externas.
    }

    // Método para establecer colores (reemplaza los colores actuales).
    public void setColors(List<String> colors) {
        this.colors = colors != null ? colors : new ArrayList<>();
    }

    // Método para saber si es incolora.
    public boolean isColorless() {
        return colors.isEmpty();
    }

    // Método para mostrar los colores en formato de string.
    @Override
    public String toString() {
        return colors.toString();
    }
}
