package domain;


import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Pokemon {
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
    private String restriction;       // ej: "taunt", "encore"
    private int restrictionDuration;  // en turnos
    private String status; // Ej: "toxic", "paralysis", etc.
    private boolean hasSubstitute = false;
    private int maxHp;

    // Boosts temporales de batalla, ej: +1 ataque, -2 defensa, etc.
    private Map<String, Integer> statBoosts = new HashMap<>();

    private static final int LEVEL = 100;

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
        this.maxHp=hp;
    }

    public void attack(int index, Pokemon target) {
        if (hp <= 0 || target == null || target.getHp() <= 0) return;

        if (hasPPAvailable()) {
            if (index >= 0 && index < moves.size()) {
                moves.get(index).use(this, target);
            }
        } else {
            MoveDatabase.getMove("STRUGGLE").use(this,target);
        }
    }

    public void setHp(int nuevoHp){this.hp = nuevoHp;}


    public void takeDamage(int amount) {
        if (hasSubstitute) {
            System.out.println(name + " está protegido por el sustituto.");
            hasSubstitute = false; // el sustituto se rompe al primer golpe
        } else {
            hp = Math.max(0, hp - amount);
        }
    }


    public void heal(int amount) {
        if (hp > 0) {
            hp = Math.min(hp + amount, getMaxHp());
        }
    }

    public void revive(int amount) {
        if (hp == 0) {
            hp = Math.min(amount, getMaxHp());
        }
    }

    public boolean isFainted() {
        return hp <= 0;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void modifyStat(String stat, int amount) {
        int current = statBoosts.getOrDefault(stat, 0);
        statBoosts.put(stat, current + amount);
    }

    public void applyRestriction(String restriction, int duration) {
        this.restriction = restriction.toLowerCase();
        this.restrictionDuration = duration;
    }

    public void updateRestriction() {
        if (restrictionDuration > 0) {
            restrictionDuration--;
            if (restrictionDuration == 0) {
                restriction = null;
            }
        }
    }

    public boolean isRestricted() {
        return restriction != null;
    }

    public String getRestriction() {
        return restriction;
    }

    public void resetBoosts() {
        statBoosts.clear();
    }

    public void createSubstitute() {
        hasSubstitute = true;
    }

    public boolean hasSubstitute() {
        return hasSubstitute;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public boolean hasPPAvailable() {
        if (moves == null) {
            System.out.println("no hay movimientos");// defensivo: lista vacía
            return false;

        }

        for (Move m : moves) {
            if (m == null) {System.out.println("m is null");}
            if (m != null && m.pp() > 0) { // ✅ verificas si m no es null
                return true;
            }
        }


        return false;
    }


    // Getters básicos
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
}
