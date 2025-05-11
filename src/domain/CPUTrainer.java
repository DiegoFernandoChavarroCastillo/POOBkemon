package domain;

import java.util.List;

/**
 * Entrenador controlado por la CPU con comportamiento configurable
 */
public class CPUTrainer extends Trainer {
    private BattleStrategy strategy;

    public CPUTrainer(String name, String color) {
        super(name, color);
        this.isCPU = true;
        this.strategy = new AttackingStrategy(); // Estrategia por defecto
    }

    public void setStrategy(BattleStrategy strategy) {
        this.strategy = strategy;
    }

    public Action decideAction(Battle battle) {  // Ahora recibe Battle como parámetro
        return strategy.decideAction(this, battle);
    }
}