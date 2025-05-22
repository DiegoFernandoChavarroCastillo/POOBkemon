package domain;

import java.io.Serializable;
import java.util.Random;

/**
 * Movimiento que cambia el clima en la batalla.
 * No aplica efectos de estado directos sobre los Pokémon, pero altera el entorno de combate.
 */
public class WeatherMove extends StatusMove implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String climate;
    private final int duration;
    private final int precision;
    private int currentPP;

    /**
     * Construye un nuevo movimiento de tipo climático.
     *
     * @param name       nombre del movimiento
     * @param type       tipo del movimiento (por ejemplo, "AGUA", "FUEGO")
     * @param precision  precisión del movimiento (probabilidad de éxito)
     * @param maxPP      cantidad máxima de puntos de poder (PP)
     * @param priority   prioridad del movimiento en el turno
     * @param climate    clima que se establecerá al usar el movimiento
     * @param duration   duración del clima aplicado, en turnos
     */
    public WeatherMove(String name, String type, int precision, int maxPP, int priority, String climate, int duration) {
        super(name, type, precision, maxPP, priority, null); // No usa un Effect directamente
        this.climate = climate;
        this.duration = duration;
        this.precision = precision;
        this.currentPP = maxPP;
    }

    /**
     * Aplica el movimiento climático, estableciendo el nuevo clima si se cumple la precisión.
     *
     * @param user   el Pokémon que usa el movimiento
     * @param target el Pokémon objetivo (no se usa directamente)
     */
    @Override
    public void use(Pokemon user, Pokemon target) {
        if (currentPP <= 0) return;

        Random rand = new Random();
        if (rand.nextInt(100) < precision) {
            Battle.setClimate(climate, duration);
        }

        currentPP--;
    }

    /**
     * Retorna los PP actuales del movimiento.
     *
     * @return cantidad de PP restantes
     */
    @Override
    public int pp() {
        return currentPP;
    }

    /**
     * Retorna los PP máximos del movimiento.
     *
     * @return cantidad máxima de PP
     */
    @Override
    public int maxPP() {
        return super.maxPP();
    }

    /**
     * Retorna la precisión del movimiento.
     *
     * @return precisión como un valor de 0 a 100
     */
    @Override
    public int precision() {
        return precision;
    }

    /**
     * Establece manualmente la cantidad de PP restantes.
     *
     * @param newPP nuevo valor de PP
     */
    public void setPP(int newPP) {
        this.currentPP = Math.max(0, Math.min(maxPP(), newPP));
    }
}
