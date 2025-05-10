package domain;

public abstract class Item {
    protected String name;
    protected boolean REVIVES;
    protected int HEAL;

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
