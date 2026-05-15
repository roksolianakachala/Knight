package model;

/**
 * Клас, що представляє зброю як тип амуніції.
 * Демонструє успадкування.
 */
public class Weapon extends Ammunition {

    private int damage;

    public Weapon(String name, double weight, double price, int damage) {
        super(name, weight, price);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public String toString() {
        return "Weapon: " + super.toString() + ", damage=" + damage;
    }
}