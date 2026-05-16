package model;

public class Boots extends Armor {

    public Boots(String name, double weight, double price, String material, int defense) {
        super(name, weight, price, material, defense);
    }

    @Override
    public String toString() {
        return "Boots: " + super.toString();
    }
}
