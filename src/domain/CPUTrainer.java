package domain;

import java.io.Serializable;
import java.util.List;

/**
 * Representa un entrenador controlado por la CPU, capaz de tomar decisiones
 * automáticas durante una batalla mediante una estrategia configurable.
 */
public class CPUTrainer extends Trainer implements Serializable {
    private BattleStrategy strategy;
    private static final long serialVersionUID = 1L;

    /**
     * Crea un nuevo entrenador CPU con una estrategia de ataque por defecto.
     *
     * @param name  Nombre del entrenador
     * @param color Color que representa al entrenador
     */
    public CPUTrainer(String name, String color) {
        super(name, color);
        this.isCPU = true;
        this.strategy = new AttackingStrategy(); // Estrategia por defecto
    }

    /**
     * Establece una estrategia de batalla para este entrenador CPU.
     *
     * @param strategy Estrategia que define el comportamiento en batalla
     */
    public void setStrategy(BattleStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Decide la acción a ejecutar durante el turno del CPU, usando la estrategia actual.
     *
     * @param battle Instancia actual de la batalla
     * @return Acción decidida según la estrategia
     */
    public Action decideAction(Battle battle) {
        return strategy.decideAction(this, battle);
    }
}
