package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Knight {

    private int id;
    private String name;
    private double height;
    private double weight;
    private int strength;
    private int endurance;
    private double maxWeight = 50.0;
    private List<Ammunition> equipment = new ArrayList<>();

    public Knight(String name) {
        this.name = name;
        this.height = 180;
        this.weight = 80;
        this.strength = 50;
        this.endurance = 50;
    }

    public Knight(int id, String name, double height, double weight, int strength, int endurance, double maxWeight) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.strength = strength;
        this.endurance = endurance;
        this.maxWeight = maxWeight;
    }

    public Knight(String name, double height, double weight, int strength, int endurance) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.strength = strength;
        this.endurance = endurance;
    }

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
        return getKnightTypeDescription();
    }

    public int getKnightTypeNumber() {
        if (weight < 70) return 1;
        if (weight < 90) return 2;
        return 3;
    }

    public String getKnightTypeName() {
        return switch (getKnightTypeNumber()) {
            case 1 -> "легкий лицар";
            case 2 -> "середній лицар";
            default -> "важкий лицар";
        };
    }

    public String getKnightTypeDescription() {
        return getKnightTypeNumber() + " — " + getKnightTypeName();
    }


    public void equip(Ammunition item) {
        if (!AmmunitionWeightRules.canUse(this, item)) {
            throw new IllegalArgumentException(AmmunitionWeightRules.buildValidationMessage(this, item));
        }
        equipment.add(item);
    }

    public void unequip(Ammunition item) {
        equipment.remove(item);
    }

    public void updateAmmunition(int index, Ammunition newItem) {
        if (index >= 0 && index < equipment.size()) {
            if (!AmmunitionWeightRules.canUse(this, newItem)) {
                throw new IllegalArgumentException(AmmunitionWeightRules.buildValidationMessage(this, newItem));
            }
            equipment.set(index, newItem);
        }
    }

    public double calculateTotalPrice() {
        return equipment.stream()
                .mapToDouble(Ammunition::getPrice)
                .sum();
    }

    public double calculateTotalWeight() {
        return equipment.stream()
                .mapToDouble(Ammunition::getWeight)
                .sum();
    }

    public boolean isOverweight() {
        return calculateTotalWeight() > maxWeight;
    }

    public void sortByWeight() {
        equipment.sort(Comparator.comparingDouble(Ammunition::getWeight));
    }

    public void sortByPrice() {
        equipment.sort(Comparator.comparingDouble(Ammunition::getPrice));
    }

    public void sortByName() {
        equipment.sort(Comparator.comparing(Ammunition::getName));
    }

    public void sortByProtection() {
        equipment.sort((a, b) -> {
            int protA = (a instanceof Armor) ? ((Armor) a).getDefense() : 0;
            int protB = (b instanceof Armor) ? ((Armor) b).getDefense() : 0;
            return Integer.compare(protB, protA); // Descending
        });
    }

    public List<Ammunition> findByCriteria(Double minPrice, Double maxPrice, Double minWeight, Double maxWeight, String type, String material, Integer minProt) {
        return equipment.stream()
                .filter(item -> (minPrice == null || item.getPrice() >= minPrice))
                .filter(item -> (maxPrice == null || item.getPrice() <= maxPrice))
                .filter(item -> (minWeight == null || item.getWeight() >= minWeight))
                .filter(item -> (maxWeight == null || item.getWeight() <= maxWeight))
                .filter(item -> (type == null || type.isEmpty() || item.getClass().getSimpleName().equalsIgnoreCase(type)))
                .filter(item -> (material == null || material.isEmpty() || item.getMaterial().equalsIgnoreCase(material)))
                .filter(item -> {
                    if (minProt == null) return true;
                    if (item instanceof Armor) return ((Armor) item).getDefense() >= minProt;
                    return false;
                })
                .collect(Collectors.toList());
    }

    public static String compareKits(Knight k1, Knight k2) {
        StringBuilder result = new StringBuilder();
        result.append("Порівняння: ").append(k1.getName()).append(" vs ").append(k2.getName()).append("\n");
        
        double p1 = k1.calculateTotalPrice();
        double p2 = k2.calculateTotalPrice();
        result.append("Ціна: ").append(p1).append(" / ").append(p2)
              .append(p1 < p2 ? " (" + k1.getName() + " дешевше)" : (p2 < p1 ? " (" + k2.getName() + " дешевше)" : " (Однакова)"))
              .append("\n");

        double w1 = k1.calculateTotalWeight();
        double w2 = k2.calculateTotalWeight();
        result.append("Вага: ").append(w1).append(" / ").append(w2)
              .append(w1 < w2 ? " (" + k1.getName() + " легше)" : (w2 < w1 ? " (" + k2.getName() + " легше)" : " (Однакова)"))
              .append("\n");

        int d1 = k1.calculateDefense();
        int d2 = k2.calculateDefense();
        result.append("Захист: ").append(d1).append(" / ").append(d2)
              .append(d1 > d2 ? " (" + k1.getName() + " кращий)" : (d2 > d1 ? " (" + k2.getName() + " кращий)" : " (Однаковий)"))
              .append("\n");

        return result.toString();
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getMaxWeight() { return maxWeight; }
    public void setMaxWeight(double maxWeight) { this.maxWeight = maxWeight; }

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
