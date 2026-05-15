package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Клас, що представляє лицаря.
 * Містить параметри лицаря та його екіпірування.
 */
public class Knight {

    private String name;
    private double height;
    private double weight;
    private int strength;
    private int endurance;
    private List<Ammunition> equipment = new ArrayList<>();

    public Knight(String name) {
        this.name = name;
        this.height = 180;
        this.weight = 80;
        this.strength = 50;
        this.endurance = 50;
    }

    public Knight(String name, double height, double weight, int strength, int endurance) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.strength = strength;
        this.endurance = endurance;
    }

    // Розрахункові методи

    public int calculateAttack() {
        return strength * 2;
    }

    public int calculateDefense() {
        return endurance + (int) (weight / 2);
    }

    public int calculateSpeed() {
        return Math.max(10, 100 - (int) weight);
    }

    public String getBodyType() {
        double bmi = weight / Math.pow(height / 100, 2);
        if (bmi < 18.5) return "Thin knight";
        if (bmi < 25) return "Normal knight";
        return "Strong / heavy knight";
    }

    // Методи екіпірування

    public void equip(Ammunition item) {
        equipment.add(item);
    }

    public double calculateTotalPrice() {
        return equipment.stream()
                .mapToDouble(Ammunition::getPrice)
                .sum();
    }

    public List<Ammunition> sortByWeight() {
        return equipment.stream()
                .sorted(Comparator.comparingDouble(Ammunition::getWeight))
                .collect(Collectors.toList());
    }

    public List<Ammunition> findByPriceRange(double min, double max) {
        return equipment.stream()
                .filter(item -> item.getPrice() >= min && item.getPrice() <= max)
                .collect(Collectors.toList());
    }

    // Геттери та сеттери

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }

    public int getEndurance() { return endurance; }
    public void setEndurance(int endurance) { this.endurance = endurance; }

    public List<Ammunition> getEquipment() { return new ArrayList<>(equipment); }

    @Override
    public String toString() {
        return "Knight{name='" + name + "', stats=[A:" + calculateAttack() + 
               ", D:" + calculateDefense() + ", S:" + calculateSpeed() + "]}";
    }
}