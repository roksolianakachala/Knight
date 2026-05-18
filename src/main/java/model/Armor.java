package model;

public class Armor extends Ammunition {

    private int defense;

    public Armor(String name, double weight, double price, String material, int defense) {
        super(name, weight, price, material);
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