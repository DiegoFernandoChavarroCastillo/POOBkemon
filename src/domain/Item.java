package domain;

import java.io.Serializable;

public abstract class Item implements Serializable {
    protected String name;
    protected boolean REVIVES;
    protected int HEAL;
    private static final long serialVersionUID = 1L;

    public Item(String name, boolean revives, int heal) {
        this.name = name;
        this.REVIVES = revives;
        this.HEAL = heal;
    }

    public abstract void use(Pokemon pokemon);

    public String getName() {
        return name;
    }
}
