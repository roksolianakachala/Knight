package model;

public class Weapon extends Ammunition {

    private int damage;

    public Weapon(String name, double weight, double price, int damage) {
        super(name, weight, price);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}