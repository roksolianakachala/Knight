package model;

public class Shield extends Armor {

    public Shield(String name, double weight, double price, String material, int defense) {
        super(name, weight, price, material, defense);
    }

    @Override
    public String toString() {
        return "Shield: " + super.toString();
    }
}
