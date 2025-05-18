package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

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

    /**
     * Construye el diálogo ItemSelectionGUI.
     *
     * @param parent  la ventana principal (padre) del diálogo
     * @param trainer el entrenador que recibirá los ítems seleccionados
     */
    public ItemSelectionGUI(JFrame parent, Trainer trainer) {
        super(parent, "Seleccionar Ítems para " + trainer.getName(), true);
        this.trainer = trainer;
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setupUI();
    }

    /**
     * Configura la interfaz gráfica del diálogo.
     * Establece el diseño, los botones y las áreas de selección.
     */
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));

        itemsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        confirmButton = new JButton("Confirmar (" + selectedItems.size() + "/" + maxItems + ")");
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> confirmSelection());

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> dispose());

        bottomPanel.add(cancelButton);
        bottomPanel.add(confirmButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadAvailableItems();
    }

    /**
     * Carga los ítems disponibles que el jugador puede seleccionar.
     * Los ítems se representan como botones.
     */
    private void loadAvailableItems() {
        Item[] availableItems = {
                new Potion(),
                new SuperPotion(),
                new HyperPotion(),
                new Revive()
        };

        for (Item item : availableItems) {
            JButton itemButton = new JButton(item.getName());
            itemButton.addActionListener(e -> toggleItemSelection(item, itemButton));
            itemsPanel.add(itemButton);
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
            selectedItems.remove(item);
            button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        } else if (selectedItems.size() < maxItems) {
            selectedItems.add(item);
            button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        }
        updateConfirmButton();
    }

    /**
     * Actualiza el texto y el estado del botón de confirmación según la selección actual.
     */
    private void updateConfirmButton() {
        confirmButton.setText("Confirmar (" + selectedItems.size() + "/" + maxItems + ")");
        confirmButton.setEnabled(selectedItems.size() > 0);
    }

    /**
     * Confirma la selección de ítems, los asigna al entrenador y cierra el diálogo.
     */
    private void confirmSelection() {
        trainer.getItems().clear();
        trainer.getItems().addAll(selectedItems);
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
