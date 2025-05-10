package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


/**
 * A dialog for selecting items to be used by a trainer in the game.
 * Allows the trainer to choose up to a maximum number of items from available options.
 */
public class ItemSelectionGUI extends JDialog {
    private Trainer trainer;
    private JPanel itemsPanel;
    private JButton confirmButton;
    private List<Item> selectedItems = new ArrayList<>();
    private int maxItems = 3;


    /**
     * Constructs an ItemSelectionGUI dialog.
     *
     * @param parent  the parent frame of this dialog
     * @param trainer the trainer who will receive the selected items
     */
    public ItemSelectionGUI(JFrame parent, Trainer trainer) {
        super(parent, "Seleccionar Ítems para " + trainer.getName(), true);
        this.trainer = trainer;
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setupUI();
    }

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

    private void updateConfirmButton() {
        confirmButton.setText("Confirmar (" + selectedItems.size() + "/" + maxItems + ")");
        confirmButton.setEnabled(selectedItems.size() > 0);
    }

    private void confirmSelection() {
        trainer.getItems().clear();
        trainer.getItems().addAll(selectedItems);
        dispose();
    }


    /**
     * Displays the item selection dialog.
     *
     * @param parent   the parent component
     * @param trainer  the trainer who will receive the selected items
     */
    public static void showItemSelection(Component parent, Trainer trainer) {
        ItemSelectionGUI dialog = new ItemSelectionGUI(
                parent instanceof JFrame ? (JFrame) parent : null,
                trainer);
        dialog.setVisible(true);
    }
}