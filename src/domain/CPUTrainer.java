package domain;

import java.util.List;

/**
 * Entrenador controlado por la CPU con comportamiento básico.
 */
public class CPUTrainer extends Trainer {

    public CPUTrainer(String name, String color) {
        super(name, color);
        this.isCPU = true;
    }

    /**
     * Decide una acción automática para el turno de la CPU.
     */
    public Action decideAction() {
        Pokemon current = getActivePokemon();


        if (current.getHp() < current.getMaxHp() * 0.3) {
            int switchIndex = getTeam().findHealthyPokemon();
            if (switchIndex != -1) {
                return Action.createSwitchPokemon(switchIndex);
            }
        }


        return getRandomAttack();
    }

    private Action getRandomAttack() {
        List<Move> moves = getActivePokemon().getMoves();
        for (int i = 0; i < moves.size(); i++) {
            int index = (int)(Math.random() * moves.size());
            if (moves.get(index).pp() > 0) {
                return Action.createAttack(index);
            }
        }
        return Action.createAttack(-1); // Struggle
    }
}