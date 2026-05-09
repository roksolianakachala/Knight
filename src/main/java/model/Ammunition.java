package model;

public abstract class Ammunition {

    protected String name;
    protected double weight;
    protected double price;

    public Ammunition(String name, double weight, double price) {
        this.name = name;
        this.weight = weight;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name +
                ", weight=" + weight +
                ", price=" + price;
    }
}