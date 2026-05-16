package service;

import model.Ammunition;
import model.Armor;

import java.util.List;
import java.util.stream.Collectors;

public final class AmmunitionFilter {

    private AmmunitionFilter() {
    }

    public static List<Ammunition> filter(List<Ammunition> items,
                                          Double minPrice,
                                          Double maxPrice,
                                          Double minWeight,
                                          Double maxWeight,
                                          String type,
                                          String material,
                                          Integer minProtection,
                                          Integer maxProtection) {
        return items.stream()
                .filter(item -> minPrice == null || item.getPrice() >= minPrice)
                .filter(item -> maxPrice == null || item.getPrice() <= maxPrice)
                .filter(item -> minWeight == null || item.getWeight() >= minWeight)
                .filter(item -> maxWeight == null || item.getWeight() <= maxWeight)
                .filter(item -> type == null || type.isEmpty() || item.getClass().getSimpleName().equalsIgnoreCase(type))
                .filter(item -> material == null || material.isEmpty() || item.getMaterial().toLowerCase().contains(material.toLowerCase()))
                .filter(item -> matchesProtectionRange(item, minProtection, maxProtection))
                .collect(Collectors.toList());
    }

    private static boolean matchesProtectionRange(Ammunition item, Integer minProtection, Integer maxProtection) {
        if (minProtection == null && maxProtection == null) {
            return true;
        }
        if (item instanceof Armor armor) {
            int defense = armor.getDefense();
            return (minProtection == null || defense >= minProtection)
                    && (maxProtection == null || defense <= maxProtection);
        }
        return false;
    }
}
