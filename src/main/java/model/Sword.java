package model;

/**
 * Клас, що представляє меч.
 */
public class Sword extends Weapon {

    public Sword(String name, double weight, double price, int damage) {
        super(name, weight, price, damage);
    }

    @Override
    public String toString() {
        return "Sword: " + super.toString();
    }
}