package model;

/**
 * Базовий абстрактний клас для всієї амуніції лицаря.
 * Демонструє принцип інкапсуляції та абстракції.
 */
public abstract class Ammunition {

    private String name;
    private double weight;
    private double price;

    public Ammunition(String name, double weight, double price) {
        this.name = name;
        this.weight = weight;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("Ammunition{name='%s', weight=%.2f, price=%.2f}", 
                name, weight, price);
    }
}