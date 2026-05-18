package model;

public class Sword extends Weapon {

    public Sword(String name, double weight, double price, String material, int damage) {
        super(name, weight, price, material, damage);
    }

    @Override
    public String toString() {
        return "Sword: " + super.toString();
    }
}