package domain;

import java.io.Serializable;
import java.util.Random;

/**
 * Movimiento que cambia el clima en la batalla.
 */
public class WeatherMove extends StatusMove implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String climate;
    private final int duration;
    private final int precision;
    private int currentPP;

    public WeatherMove(String name, String type, int precision, int maxPP, int priority, String climate, int duration) {
        super(name, type, precision, maxPP, priority, null); // No usa un Effect directamente
        this.climate = climate;
        this.duration = duration;
        this.precision = precision;
        this.currentPP = maxPP;
    }

    @Override
    public void use(Pokemon user, Pokemon target) {
        if (currentPP <= 0) return;

        Random rand = new Random();
        if (rand.nextInt(100) < precision) {
            Battle.setClimate(climate, duration);
        }

        currentPP--;
    }

    @Override
    public int pp() {
        return currentPP;
    }

    @Override
    public int maxPP() {
        return super.maxPP(); // Usa el valor original
    }

    @Override
    public int precision() {
        return precision;
    }

    public void setPP(int newPP) {
        this.currentPP = Math.max(0, Math.min(maxPP(), newPP));
    }
}
