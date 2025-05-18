package domain;

import java.io.Serializable;
import java.util.Map;

public class Effect {
    private EffectType type;
    private String description;

    public Effect(EffectType type, String description) {
        this.type = type;
        this.description = description;
    }

    public void apply(Pokemon user, Pokemon target) {
        switch (this.type) {
            case EVASION_UP:
                user.increaseEvasionStage();
                break;
            default:
                System.out.println("Efecto no definido para este tipo: " + this.type);
        }
    }

    public String getDescription() {
        return description;
    }
}
