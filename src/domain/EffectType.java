package domain;

import java.io.Serializable;

public enum EffectType implements Serializable {
    BUFF, DEBUFF, STATUS, WEATHER, FORCE_SWITCH, RESET_STATS, RESTRICTION
}
