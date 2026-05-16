package model;

public abstract class Ammunition {

    private int id;
    private String name;
    private double weight;
    private double price;
    private String material;

    public Ammunition(String name, double weight, double price, String material) {
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.material = material;
    }

    public Ammunition(int id, String name, double weight, double price, String material) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.material = material;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return String.format("Ammunition{name='%s', weight=%.2f, price=%.2f, material='%s'}", 
                name, weight, price, material);
    }
}