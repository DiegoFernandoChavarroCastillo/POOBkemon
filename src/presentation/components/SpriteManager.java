package presentation.components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteManager {
    private static final String BASE_PATH = "src/sprites/";
    private static final int DEFAULT_SPRITE_WIDTH = 150;
    private static final int DEFAULT_SPRITE_HEIGHT = 150;

    public void loadPokemonSprite(JLabel label, String pokemonName, boolean isBackView) {
        loadPokemonSprite(label, pokemonName, isBackView, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT);
    }

    public void loadPokemonSprite(JLabel label, String pokemonName, boolean isBackView, int width, int height) {
        String suffix = isBackView ? "_back" : "_front";

        // Intentar cargar sprite específico primero
        if (tryLoadSprite(label, pokemonName + suffix, width, height)) return;

        // Intentar sprite genérico
        if (tryLoadSprite(label, pokemonName, width, height)) return;

        // Fallback a texto
        setTextFallback(label, pokemonName);
    }

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

    private void setTextFallback(JLabel label, String pokemonName) {
        label.setIcon(null);
        label.setText(pokemonName);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
    }

    public BufferedImage loadBackgroundImage(String fileName) {
        try {
            return ImageIO.read(new File(BASE_PATH + fileName));
        } catch (IOException e) {
            System.err.println("Error loading background: " + e.getMessage());
            return null;
        }
    }
}
