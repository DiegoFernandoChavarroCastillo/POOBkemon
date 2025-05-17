    package domain;

    import java.util.*;

    public class MoveDatabase {
        private static final Map<String, Move> moves = new HashMap<>();

        static {
            moves.put("BODY SLAM", new PhysicalMove("BODY SLAM", "NORMAL", 85, 100, 15, 0));
            moves.put("HYPER BEAM", new PhysicalMove("HYPER BEAM", "NORMAL", 150, 90, 5, 0));
            moves.put("CROSS CHOP", new PhysicalMove("CROSS CHOP", "FIGHTING", 100, 80, 5, 0));
            moves.put("BRICK BREAK", new PhysicalMove("BRICK BREAK", "FIGHTING", 75, 100, 15, 0));
            moves.put("WING ATTACK", new PhysicalMove("WING ATTACK", "FLYING", 60, 100, 35, 0));
            moves.put("AERIAL ACE", new PhysicalMove("AERIAL ACE", "FLYING", 60, 100, 20, 0));
            moves.put("SLUDGE BOMB", new PhysicalMove("SLUDGE BOMB", "POISON", 90, 100, 10, 0));
            moves.put("POISON STING", new PhysicalMove("POISON STING", "POISON", 15, 100, 35, 0));
            moves.put("EARTHQUAKE", new PhysicalMove("EARTHQUAKE", "GROUND", 100, 100, 10, 0));
            moves.put("DIG", new PhysicalMove("DIG", "GROUND", 60, 100, 10, 0));
            moves.put("ROCK SLIDE", new PhysicalMove("ROCK SLIDE", "ROCK", 75, 90, 10, 0));
            moves.put("ANCIENTPOWER", new PhysicalMove("ANCIENTPOWER", "ROCK", 60, 100, 5, 0));
            moves.put("MEGAHORN", new PhysicalMove("MEGAHORN", "BUG", 120, 85, 10, 0));
            moves.put("TWINNEEDLE", new PhysicalMove("TWINNEEDLE", "BUG", 25, 100, 20, 0));
            moves.put("LICK", new PhysicalMove("LICK", "GHOST", 20, 100, 30, 0));
            moves.put("SHADOW PUNCH", new PhysicalMove("SHADOW PUNCH", "GHOST", 60, 100, 20, 0));
            moves.put("METAL CLAW", new PhysicalMove("METAL CLAW", "STEEL", 50, 95, 35, 0));
            moves.put("IRON TAIL", new PhysicalMove("IRON TAIL", "STEEL", 100, 75, 15, 0));
            moves.put("DRAGON CLAW", new PhysicalMove("DRAGON CLAW", "DRAGON", 80, 100, 15, 0));
            moves.put("TWISTER", new PhysicalMove("TWISTER", "DRAGON", 40, 100, 20, 0));
            moves.put("BITE", new PhysicalMove("BITE", "DARK", 60, 100, 25, 0));
            moves.put("CRUNCH", new PhysicalMove("CRUNCH", "DARK", 80, 100, 15, 0));

            moves.put("FLAMETHROWER", new SpecialMove("FLAMETHROWER", "FIRE", 90, 100, 15, 0));
            moves.put("FIRE BLAST", new SpecialMove("FIRE BLAST", "FIRE", 110, 85, 5, 0));
            moves.put("SURF", new SpecialMove("SURF", "WATER", 90, 100, 15, 0));
            moves.put("HYDRO PUMP", new SpecialMove("HYDRO PUMP", "WATER", 110, 80, 5, 0));
            moves.put("RAZOR LEAF", new SpecialMove("RAZOR LEAF", "GRASS", 55, 95, 25, 0));
            moves.put("GIGA DRAIN", new SpecialMove("GIGA DRAIN", "GRASS", 60, 100, 5, 0));
            moves.put("THUNDERBOLT", new SpecialMove("THUNDERBOLT", "ELECTRIC", 90, 100, 15, 0));
            moves.put("THUNDER", new SpecialMove("THUNDER", "ELECTRIC", 110, 70, 10, 0));
            moves.put("PSYCHIC", new SpecialMove("PSYCHIC", "PSYCHIC", 90, 100, 10, 0));
            moves.put("CONFUSION", new SpecialMove("CONFUSION", "PSYCHIC", 50, 100, 25, 0));
            moves.put("ICE BEAM", new SpecialMove("ICE BEAM", "ICE", 90, 100, 10, 0));
            moves.put("BLIZZARD", new SpecialMove("BLIZZARD", "ICE", 110, 70, 5, 0));
            // forcejeo
            moves.put("STRUGGLE", new StruggleMove());;

            //pruebas
            moves.put("p1", new SpecialMove("p1", "PSYCHIC", 1, 100, 1, 0));
            moves.put("p2", new SpecialMove("p2", "PSYCHIC", 1, 100, 1, 0));
            moves.put("p3", new SpecialMove("p3", "PSYCHIC", 1, 100, 1, 0));
            moves.put("p4", new SpecialMove("p4", "PSYCHIC", 5, 70, 1, 0));
        }


        public static Move getMove(String name) {
            Move base = moves.get(name.toUpperCase());
            return base != null ? base.clone() : null;
        }
        public static List<Move> getAvailableMoves() {
            return new ArrayList<>(moves.values());
        }

        public static List<Move> getRandomMoves(int n) {
            List<Move> available = new ArrayList<>(moves.values());
            if (available.size() < n) {
                throw new IllegalStateException("No hay suficientes movimientos disponibles.");
            }
            Collections.shuffle(available); // aleatorizar
            List<Move> selected = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                selected.add(available.get(i).clone());
            }
            return selected;
        }

    }

