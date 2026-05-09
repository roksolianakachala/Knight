package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Knight {

    private String name;

    private List<Ammunition> equipment = new ArrayList<>();

    public Knight(String name) {
        this.name = name;
    }

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
                .filter(item ->
                        item.getPrice() >= min &&
                                item.getPrice() <= max)
                .collect(Collectors.toList());
    }

    public void showEquipment() {
        equipment.forEach(System.out::println);
    }
}