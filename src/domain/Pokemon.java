package domain;

import java.io.Serializable;
import java.util.*;

/**
 * Representa un Pokémon con estadísticas, movimientos y estado de batalla.
 */
public class Pokemon implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String type;
    private int hp;
    private int level;
    private int attack;
    private int defense;
    private int specialAttack;
    private int specialDefense;
    private int speed;
    private int accuracy;
    private int evasion;
    private List<Move> moves;
    private String restriction;
    private int restrictionDuration;
    private String status;
    private boolean hasSubstitute = false;
    private int maxHp;
    private Map<String, Integer> statBoosts = new HashMap<>();
    private int evasionStage = 0;
    private static final int LEVEL = 100;
    private List<ActiveEffect> activeEffects = new ArrayList<>();
    private boolean forcedToSwitch = false;
    private String restriction = null; // para Taunt, Encore, etc.
    private int restrictionDuration = 0;

    /**
     * Crea un nuevo Pokémon con sus atributos básicos y lista de movimientos.
     */
    public Pokemon(String name, String type, int hp, int attack, int defense,
                   int specialAttack, int specialDefense, int speed, int accuracy,
                   int evasion, List<Move> moves) {
        this.name = name;
        this.type = type;
        this.hp = hp;
        this.level = LEVEL;
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
        this.accuracy = accuracy;
        this.evasion = evasion;
        this.moves = moves;
        this.maxHp = hp;
    }

    /**
     * Ejecuta un ataque usando el movimiento en el índice indicado.
     * Si no hay PP disponibles, se usa 'Struggle'.
     */
    public void attack(int index, Pokemon target) {
        if (hp <= 0 || target == null || target.getHp() <= 0) return;

        if (hasPPAvailable()) {
            if (index >= 0 && index < moves.size()) {
                moves.get(index).use(this, target);
            }
        } else {
            MoveDatabase.getMove("STRUGGLE").use(this, target);
        }
    }

    /**
     * Crea una copia profunda del Pokémon, incluyendo sus movimientos.
     */
    @Override
    public Pokemon clone() {
        try {
            Pokemon cloned = (Pokemon) super.clone();
            cloned.moves = new ArrayList<>();
            for (Move move : this.moves) {
                cloned.moves.add(move.clone());
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Establece directamente los puntos de vida del Pokémon.
     */
    public void setHp(int nuevoHp) {
        this.hp = nuevoHp;
    }

    /**
     * Aplica daño al Pokémon, considerando si tiene sustituto.
     */
    public void takeDamage(int amount) {
        if (hasSubstitute) {
            System.out.println(name + " está protegido por el sustituto.");
            hasSubstitute = false;
        } else {
            hp = Math.max(0, hp - amount);
        }
    }

    /**
     * Restaura puntos de vida al Pokémon, sin exceder su máximo.
     *
     * @param amount cantidad de HP a recuperar
     */
    public void heal(int amount) {
        if (hp > 0) {
            hp = Math.min(hp + amount, getMaxHp());
        }
    }

    /**
     * Revive al Pokémon si está debilitado, restaurando una cantidad de HP.
     *
     * @param amount cantidad de HP a restaurar
     */
    public void revive(int amount) {
        if (hp == 0) {
            hp = Math.min(amount, getMaxHp());
        }
    }

    /**
     * Verifica si el Pokémon está debilitado.
     *
     * @return true si el HP es 0 o menor
     */
    public boolean isFainted() {
        return hp <= 0;
    }

    /**
     * @return HP máximo del Pokémon
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * Establece el estado del Pokémon (como paralizado, envenenado, etc.).
     *
     * @param status estado a aplicar
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return el estado actual del Pokémon
     */
    public String getStatus() {
        return status;
    }

    /**
     * Modifica un estadístico temporalmente durante la batalla.
     *
     * @param stat   nombre del estadístico
     * @param amount cantidad a sumar o restar
     */
    public void modifyStat(String stat, int amount) {
        int current = statBoosts.getOrDefault(stat, 0);
        statBoosts.put(stat, current + amount);
    }


    /**
     * Actualiza la duración de la restricción al final del turno.
     * Elimina la restricción si la duración llega a cero.
     */
    public void updateRestriction() {
        if (restrictionDuration > 0) {
            restrictionDuration--;
            if (restrictionDuration == 0) {
                restriction = null;
            }
        }
    }

    /**
     * Reinicia todos los aumentos y reducciones de estadísticas del Pokémon.
     */
    public void resetBoosts() {
        statBoosts.clear();
    }

    /**
     * Crea un sustituto que protege al Pokémon del próximo golpe.
     */
    public void createSubstitute() {
        hasSubstitute = true;
    }

    /**
     * @return true si el Pokémon tiene un sustituto activo
     */
    public boolean hasSubstitute() {
        return hasSubstitute;
    }

    /**
     * Establece la lista de movimientos del Pokémon.
     *
     * @param moves lista de movimientos a asignar
     */
    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    /**
     * Verifica si el Pokémon tiene al menos un movimiento con PP disponibles.
     *
     * @return true si hay al menos un movimiento utilizable
     */
    public boolean hasPPAvailable() {
        if (moves == null) {
            System.out.println("no hay movimientos");
            return false;
        }

        for (Move m : moves) {
            if (m == null) System.out.println("m is null");
            if (m != null && m.pp() > 0) {
                return true;
            }
        }

        return false;
    }

    public void addEffect(Effect effect) {
        activeEffects.add(new ActiveEffect(effect));
    }

    public void processStartOfTurnEffects() {
        Iterator<ActiveEffect> it = activeEffects.iterator();
        while (it.hasNext()) {
            ActiveEffect ae = it.next();
            Effect e = ae.getEffect();

            if (e.getType() == Effect.Type.STATUS && "toxic".equals(status)) {
                // Daño escalonado: 1/16 * turno actual
                int toxicTurn = e.getDuration() - ae.getRemainingTurns() + 1;
                int damage = getMaxHp() / 16 * toxicTurn;
                takeDamage(damage);
            } else if ("burned".equals(status)) {
                takeDamage(getMaxHp() / 8);
            } else if ("cursed".equals(status)) {
                takeDamage(getMaxHp() / 4);
            }

            ae.tick();
            if (ae.isExpired()) it.remove();
        }

        if (restrictionDuration > 0) {
            restrictionDuration--;
            if (restrictionDuration == 0) restriction = null;
        }

        forcedToSwitch = false; // reseteamos después de procesar
    }

    public void applyRestriction(String restriction, int duration) {
        this.restriction = restriction;
        this.restrictionDuration = duration;
    }

    public boolean isRestricted() {
        return restriction != null;
    }

    public String getRestriction() {
        return restriction;
    }

    public void setForcedToSwitch(boolean forced) {
        this.forcedToSwitch = forced;
    }

    public boolean mustSwitch() {
        return forcedToSwitch;
    }

    // Getters

    public String getName() { return name; }
    public String getType() { return type; }
    public int getLevel() { return level; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getSpecialAttack() { return specialAttack; }
    public int getSpecialDefense() { return specialDefense; }
    public int getHp() { return hp; }
    public int getSpeed() { return speed; }
    public int getAccuracy() { return accuracy; }
    public int getEvasion() { return evasion; }
    public List<Move> getMoves() { return moves; }

    public void increaseEvasionStage() {
        if (evasionStage < 6) {
            evasionStage++;
            System.out.println(name + " aumentó su evasión a nivel " + evasionStage);
        }
    }

    public int getEvasionStage() {
        return evasionStage;
    }

    public double getEvasionMultiplier() {
        int stage = evasionStage;
        if (stage >= 0) {
            return (3.0 + stage) / 3.0;
        } else {
            return 3.0 / (3.0 - stage);
        }
    }


    public class ActiveEffect {
        private final Effect effect;
        private int remainingTurns;

        public ActiveEffect(Effect effect) {
            this.effect = effect;
            this.remainingTurns = effect.getDuration();
        }

        public Effect getEffect() {
            return effect;
        }

        public int getRemainingTurns() {
            return remainingTurns;
        }

        public void tick() {
            remainingTurns--;
        }

        public boolean isExpired() {
            return remainingTurns <= 0;
        }
    }

}
