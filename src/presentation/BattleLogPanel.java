package presentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedList;

/**
 * Panel dedicado a mostrar el log de acciones de batalla.
 * Responsabilidad única: Mostrar y gestionar los mensajes de acciones durante la batalla.
 */
public class BattleLogPanel extends JPanel {
    private JTextArea logArea;
    private LinkedList<String> messages;
    private static final int MAX_MESSAGES = 5;

    /**
     * Inicializa el panel de log de batalla con un área de texto
     * y formato similar al del juego original.
     */
    public BattleLogPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#FDF074"));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        messages = new LinkedList<>();
        logArea = new JTextArea(5, 30);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        logArea.setBackground(Color.decode("#FDF074"));
        logArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(logArea, BorderLayout.CENTER);
    }

    /**
     * Añade un mensaje al log y refresca la visualización.
     * Mantendrá solo un número limitado de mensajes visibles.
     *
     * @param message Mensaje de acción a mostrar
     */
    public void addMessage(String message) {
        // Limite de mensajes
        if (messages.size() >= MAX_MESSAGES) {
            messages.removeFirst();
        }

        messages.addLast(message);
        updateDisplay();
    }

    /**
     * Actualiza el área de texto con todos los mensajes actuales.
     */
    private void updateDisplay() {
        StringBuilder sb = new StringBuilder();
        for (String msg : messages) {
            sb.append(msg).append("\n");
        }
        logArea.setText(sb.toString());
    }

    /**
     * Limpia todos los mensajes del log.
     */
    public void clearMessages() {
        messages.clear();
        logArea.setText("");
    }
}