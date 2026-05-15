package model;

/**
 * Клас, що представляє обладунки як тип амуніції.
 */
public class Armor extends Ammunition {

    private int defense;

    public Armor(String name, double weight, double price, int defense) {
        super(name, weight, price);
        this.defense = defense;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    @Override
    public String toString() {
        return "Armor: " + super.toString() + ", defense=" + defense;
    }
}