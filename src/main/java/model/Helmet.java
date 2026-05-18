package model;

public class Helmet extends Armor {

    public Helmet(String name, double weight, double price, String material, int defense) {
        super(name, weight, price, material, defense);
    }

    @Override
    public String toString() {
        return "Helmet: " + super.toString() + ", defense=" + getDefense();
    }
}
