package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Pantalla inicial que permite seleccionar el modo de juego: Normal o Supervivencia.
 */
public class ModeSelectionGUI extends JFrame {
    public ModeSelectionGUI(BattleGUI gui) {
        setTitle("Seleccionar modo de juego");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1));

        JButton normalMode = new JButton("Modo Normal");
        JButton survivalMode = new JButton("Modo Supervivencia");

        normalMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setGameMode(GameController.MODO_NORMAL);
                dispose(); // cerrar ventana
                gui.showGameModeSelection(); // sigue con selección PvP/PvM/MvM
            }
        });

        survivalMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setGameMode(GameController.MODO_SUPERVIVENCIA);
                dispose(); // cerrar ventana
                gui.startSurvivalGame(); // inicia flujo aleatorio
            }
        });

        add(normalMode);
        add(survivalMode);
    }
}
