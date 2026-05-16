package model;

public class Weapon extends Ammunition {

    private int damage;

    public Weapon(String name, double weight, double price, String material, int damage) {
        super(name, weight, price, material);
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