package domain;

public class StatusMove extends Move {
    private String name;
    private String type;
    private int pp;
    private Effect effect;

    public StatusMove(String name, String type, int pp, Effect effect) {
        this.name = name;
        this.type = type;
        this.pp = pp;
        this.effect = effect;
    }

    public Effect getEffect() {
        return effect;
    }

    public void apply(Pokemon user, Pokemon target) {
        if (effect != null) {
            effect.apply(user, target);
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public int power() {
        return 0; // Status moves typically have no power
    }

    @Override
    public int precision() {
        return 100; // Default accuracy unless specified
    }

    @Override
    public int maxPP() {
        return pp;
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void use(Pokemon user, Pokemon target) {
        if (pp > 0) pp--;
    }

    @Override
    public int pp() {
        return pp;
    }
}