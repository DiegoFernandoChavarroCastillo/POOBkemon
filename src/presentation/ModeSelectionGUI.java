package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Pantalla inicial que permite seleccionar el modo de juego: Normal o Supervivencia.
 * Esta pantalla aparece al iniciar la aplicación, antes del menú principal.
 */
public class ModeSelectionGUI extends JFrame {

    /**
     * Constructor de la pantalla de selección de modo.
     *
     * @param gui Referencia al BattleGUI principal para continuar el flujo de la aplicación
     */
    public ModeSelectionGUI(BattleGUI gui) {
        setTitle("POOBkemon Battle - Seleccionar modo de juego");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 450);  // Tamaño aumentado para mejor visualización
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 400));  // Tamaño mínimo para evitar que se vea mal

        // Panel principal con BoxLayout para mejor distribución vertical
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(194, 255, 82));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Panel para el título (centrado)
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(194, 255, 82));
        JLabel titleLabel = new JLabel("Selecciona un modo de juego", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));  // Fuente más grande
        titlePanel.add(titleLabel);
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Espacio entre título y botones
        mainPanel.add(Box.createVerticalStrut(20));

        // Panel para los botones con GridBagLayout para mejor control
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(194, 255, 82));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);  // Márgenes aumentados
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Botón de modo normal con descripción
        JButton normalMode = createStyledButton("Modo Normal",
                new Color(100, 150, 255),
                "Configura tu equipo y el de tu oponente");
        normalMode.setPreferredSize(new Dimension(450, 100));  // Botones más grandes
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(normalMode, gbc);

        // Botón de modo supervivencia con descripción
        JButton survivalMode = createStyledButton("Modo Supervivencia",
                new Color(255, 150, 100),
                "¡Equipos aleatorios y batallas intensas!");
        survivalMode.setPreferredSize(new Dimension(450, 100));
        gbc.gridy = 1;
        buttonPanel.add(survivalMode, gbc);

        // Añadir acción a los botones
        normalMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setGameMode(GameController.MODO_NORMAL);
                dispose();
                gui.showGameModeSelection();
            }
        });

        survivalMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setGameMode(GameController.MODO_SUPERVIVENCIA);
                dispose();
                gui.startSurvivalGame();
            }
        });

        // Panel de información en la parte inferior
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(240, 240, 240));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoLabel = new JLabel("<html><div style='text-align:center;'>" +
                "<b>¡Bienvenido a POOBkemon Battle!</b><br>" +
                "Selecciona cómo quieres jugar" +
                "</div></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 16));  // Fuente más grande
        infoPanel.add(infoLabel);

        // Añadir componentes al panel principal con espaciado
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(infoPanel);

        add(mainPanel);
    }

    /**
     * Crea un botón estilizado con descripción y efectos visuales mejorados.
     */
    private JButton createStyledButton(String text, Color bgColor, String description) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(text, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));  // Texto más grande
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel(description, JLabel.CENTER);
        descLabel.setFont(new Font("Arial", Font.ITALIC, 14));  // Texto más grande
        descLabel.setForeground(Color.WHITE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(titleLabel, BorderLayout.CENTER);
        contentPanel.add(descLabel, BorderLayout.SOUTH);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));  // Más padding

        button.add(contentPanel);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        // Efectos al pasar el ratón mejorados
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return button;
    }
}