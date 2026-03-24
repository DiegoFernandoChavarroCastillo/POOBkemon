package presentation.components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Clase que gestiona la carga y manipulación de sprites e imágenes del juego.
 * Proporciona métodos para cargar sprites de Pokémon, imágenes de fondo,
 * y maneja casos de fallo con alternativas de texto.
 */
public class SpriteManager {
    private static final String BASE_PATH = "src/sprites/";
    private static final int DEFAULT_SPRITE_WIDTH = 150;
    private static final int DEFAULT_SPRITE_HEIGHT = 150;

    /**
     * Carga un sprite de Pokémon en un JLabel con dimensiones por defecto.
     *
     * @param label El JLabel donde se mostrará el sprite
     * @param pokemonName Nombre del Pokémon cuyo sprite se cargará
     * @param isBackView true para cargar la vista trasera, false para la frontal
     */
    public void loadPokemonSprite(JLabel label, String pokemonName, boolean isBackView) {
        loadPokemonSprite(label, pokemonName, isBackView, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT);
    }

    /**
     * Carga un sprite de Pokémon en un JLabel con dimensiones personalizadas.
     *
     * @param label El JLabel donde se mostrará el sprite
     * @param pokemonName Nombre del Pokémon cuyo sprite se cargará
     * @param isBackView true para cargar la vista trasera, false para la frontal
     * @param width Ancho deseado para el sprite
     * @param height Alto deseado para el sprite
     */
    public void loadPokemonSprite(JLabel label, String pokemonName, boolean isBackView, int width, int height) {
        String suffix = isBackView ? "_back" : "_front";

        // Intentar cargar sprite específico primero
        if (tryLoadSprite(label, pokemonName + suffix, width, height)) return;

        // Intentar sprite genérico
        if (tryLoadSprite(label, pokemonName, width, height)) return;

        // Fallback a texto
        setTextFallback(label, pokemonName);
    }

    /**
     * Intenta cargar un sprite desde el sistema de archivos.
     *
     * @param label El JLabel donde se mostrará la imagen
     * @param fileName Nombre del archivo (sin extensión)
     * @param width Ancho deseado para la imagen
     * @param height Alto deseado para la imagen
     * @return true si la carga fue exitosa, false en caso contrario
     */
    protected boolean tryLoadSprite(JLabel label, String fileName, int width, int height) {
        for (String ext : new String[]{".png", ".jpg", ".gif"}) {
            File file = new File(BASE_PATH + fileName + ext);
            if (file.exists()) {
                try {
                    BufferedImage originalImage = ImageIO.read(file);
                    Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaledImage));
                    label.setText("");
                    return true;
                } catch (IOException e) {
                    System.err.println("Error loading sprite: " + e.getMessage());
                }
            }
        }
        return false;
    }

    /**
     * Establece un texto de respaldo cuando no se puede cargar un sprite.
     *
     * @param label El JLabel donde se mostrará el texto
     * @param pokemonName Nombre del Pokémon que se mostrará como texto
     */
    private void setTextFallback(JLabel label, String pokemonName) {
        label.setIcon(null);
        label.setText(pokemonName);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
    }

    /**
     * Carga una imagen de fondo desde el sistema de archivos.
     *
     * @param fileName Nombre del archivo de imagen (con extensión)
     * @return BufferedImage cargada, o null si hubo un error
     */
    public BufferedImage loadBackgroundImage(String fileName) {
        try {
            return ImageIO.read(new File(BASE_PATH + fileName));
        } catch (IOException e) {
            System.err.println("Error loading background: " + e.getMessage());
            return null;
        }
    }
}