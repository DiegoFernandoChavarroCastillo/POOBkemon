package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.File;

/**
 * Diálogo para seleccionar ítems que usará un entrenador en el juego.
 * Permite al entrenador elegir hasta un número máximo de ítems entre las opciones disponibles.
 */
public class ItemSelectionGUI extends JDialog {
    private Trainer trainer;
    private JPanel itemsPanel;
    private JButton confirmButton;
    private List<Item> selectedItems = new ArrayList<>();
    private int maxItems = 3;

    // Colores temáticos de Pokémon Emerald
    private static final Color EMERALD_GREEN = new Color(152, 251, 152);
    private static final Color LIGHT_BLUE = new Color(176, 224, 230);
    private static final Color SELECTED_GREEN = new Color(34, 139, 34);
    private static final Color BORDER_COLOR = new Color(0, 100, 0);

    // Fuente personalizada
    private Font pokemonFont;

    /**
     * Construye el diálogo ItemSelectionGUI.
     *
     * @param parent  la ventana principal (padre) del diálogo
     * @param trainer el entrenador que recibirá los ítems seleccionados
     */
    public ItemSelectionGUI(JFrame parent, Trainer trainer) {
        super(parent, "Seleccionar Ítems para " + trainer.getName(), true);
        this.trainer = trainer;
        loadPokemonFont();
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setupUI();
    }

    /**
     * Carga la fuente personalizada de Pokémon.
     */
    private void loadPokemonFont() {
        try {
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/sprites/pokemon_font.ttf"));
            pokemonFont = baseFont.deriveFont(Font.BOLD, 14f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(baseFont);
        } catch (Exception e) {
            // Si no se puede cargar la fuente, usar una por defecto
            pokemonFont = new Font("Arial", Font.BOLD, 14);
            System.out.println("No se pudo cargar la fuente de Pokémon, usando Arial por defecto");
        }
    }

    /**
     * Configura la interfaz gráfica del diálogo.
     * Establece el diseño, los botones y las áreas de selección.
     */
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(EMERALD_GREEN);

        // Panel del título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(EMERALD_GREEN);
        JLabel titleLabel = new JLabel("Selecciona hasta " + maxItems + " ítems", SwingConstants.CENTER);
        titleLabel.setFont(pokemonFont.deriveFont(18f));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Panel de ítems con scroll
        itemsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        itemsPanel.setBackground(EMERALD_GREEN);
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBackground(EMERALD_GREEN);
        scrollPane.getViewport().setBackground(EMERALD_GREEN);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(EMERALD_GREEN);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        confirmButton = new JButton("Confirmar (" + selectedItems.size() + "/" + maxItems + ")");
        confirmButton.setFont(pokemonFont);
        confirmButton.setEnabled(false);
        confirmButton.setBackground(LIGHT_BLUE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        confirmButton.addActionListener(e -> confirmSelection());

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(pokemonFont);
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        cancelButton.addActionListener(e -> dispose());

        bottomPanel.add(cancelButton);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(confirmButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadAvailableItems();
    }

    /**
     * Carga los ítems disponibles que el jugador puede seleccionar.
     * Los ítems se representan como botones con sprites.
     */
    private void loadAvailableItems() {
        Item[] availableItems = {
                new Potion(),
                new SuperPotion(),
                new HyperPotion(),
                new Revive()
        };

        for (Item item : availableItems) {
            JButton itemButton = createItemButton(item);
            itemsPanel.add(itemButton);
        }
    }

    /**
     * Crea un botón personalizado para un ítem con sprite y estilo.
     *
     * @param item el ítem para el cual crear el botón
     * @return el botón configurado
     */
    private JButton createItemButton(Item item) {
        JButton itemButton = new JButton();
        itemButton.setLayout(new BorderLayout());
        itemButton.setBackground(LIGHT_BLUE);
        itemButton.setFocusPainted(false);
        itemButton.setHorizontalAlignment(SwingConstants.LEFT);
        itemButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Panel para el contenido del botón
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.setOpaque(false);

        // Cargar sprite del ítem
        String imagePath = "src/sprites/" + item.getName().toLowerCase() + ".png";
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            ImageIcon icon = new ImageIcon(imagePath);
            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            contentPanel.add(iconLabel, BorderLayout.WEST);
        }

        // Panel de texto
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(pokemonFont);
        nameLabel.setForeground(Color.BLACK);
        textPanel.add(nameLabel, BorderLayout.NORTH);

        // Descripción del ítem (si está disponible)
        String description = getItemDescription(item);
        if (!description.isEmpty()) {
            JLabel descLabel = new JLabel("<html><div style='width: 200px;'>" + description + "</div></html>");
            descLabel.setFont(pokemonFont.deriveFont(11f));
            descLabel.setForeground(Color.DARK_GRAY);
            textPanel.add(descLabel, BorderLayout.CENTER);
        }

        contentPanel.add(textPanel, BorderLayout.CENTER);
        itemButton.add(contentPanel);

        itemButton.addActionListener(e -> toggleItemSelection(item, itemButton));

        return itemButton;
    }

    /**
     * Obtiene la descripción de un ítem.
     *
     * @param item el ítem del cual obtener la descripción
     * @return la descripción del ítem
     */
    private String getItemDescription(Item item) {
        String itemName = item.getName().toLowerCase();
        switch (itemName) {
            case "potion":
                return "Restaura 20 HP";
            case "superpotion":
            case "super potion":
                return "Restaura 50 HP";
            case "hyperpotion":
            case "hyper potion":
                return "Restaura 200 HP";
            case "revive":
                return "Revive un Pokémon debilitado";
            default:
                return "";
        }
    }

    /**
     * Añade o elimina un ítem de la selección actual según su estado.
     *
     * @param item   el ítem que se desea seleccionar o deseleccionar
     * @param button el botón que representa el ítem
     */
    private void toggleItemSelection(Item item, JButton button) {
        if (selectedItems.contains(item)) {
            // Deseleccionar ítem
            selectedItems.remove(item);
            button.setBackground(LIGHT_BLUE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        } else if (selectedItems.size() < maxItems) {
            // Seleccionar ítem
            selectedItems.add(item);
            button.setBackground(SELECTED_GREEN);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 3),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        } else {
            // Mostrar mensaje si se alcanzó el límite
            JOptionPane.showMessageDialog(this,
                    "Ya has seleccionado el máximo de " + maxItems + " ítems.",
                    "Límite alcanzado",
                    JOptionPane.WARNING_MESSAGE);
        }
        updateConfirmButton();
    }

    /**
     * Actualiza el texto y el estado del botón de confirmación según la selección actual.
     */
    private void updateConfirmButton() {
        confirmButton.setText("Confirmar (" + selectedItems.size() + "/" + maxItems + ")");
        confirmButton.setEnabled(selectedItems.size() > 0);

        // Cambiar color del botón según el estado
        if (selectedItems.size() > 0) {
            confirmButton.setBackground(SELECTED_GREEN);
            confirmButton.setForeground(Color.WHITE);
        } else {
            confirmButton.setBackground(LIGHT_BLUE);
            confirmButton.setForeground(Color.BLACK);
        }
    }

    /**
     * Confirma la selección de ítems, los asigna al entrenador y cierra el diálogo.
     */
    private void confirmSelection() {
        trainer.getItems().clear();
        trainer.getItems().addAll(selectedItems);

        // Mostrar mensaje de confirmación
        StringBuilder message = new StringBuilder("Ítems seleccionados:\n");
        for (Item item : selectedItems) {
            message.append("• ").append(item.getName()).append("\n");
        }

        JOptionPane.showMessageDialog(this,
                message.toString(),
                "Selección confirmada",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    /**
     * Muestra el diálogo para seleccionar ítems.
     *
     * @param parent  el componente padre desde el cual se muestra el diálogo
     * @param trainer el entrenador que recibirá los ítems seleccionados
     */
    public static void showItemSelection(Component parent, Trainer trainer) {
        ItemSelectionGUI dialog = new ItemSelectionGUI(
                parent instanceof JFrame ? (JFrame) parent : null,
                trainer);
        dialog.setVisible(true);
    }
}