package model;

import java.util.Map;

public final class AmmunitionWeightRules {

    public record WeightRange(double min, double max) {
        public boolean contains(double weight) {
            return weight >= min && weight <= max;
        }

        public String format() {
            return String.format("%.1f-%.1f кг", min, max);
        }
    }

    private static final Map<String, WeightRange[]> RANGES = Map.of(
            "Sword", new WeightRange[]{
                    new WeightRange(1.0, 3.0),
                    new WeightRange(2.0, 5.0),
                    new WeightRange(2.0, 8.0)
            },
            "Weapon", new WeightRange[]{
                    new WeightRange(1.0, 3.0),
                    new WeightRange(2.0, 5.0),
                    new WeightRange(2.0, 8.0)
            },
            "Armor", new WeightRange[]{
                    new WeightRange(3.0, 8.0),
                    new WeightRange(7.0, 16.0),
                    new WeightRange(10.0, 25.0)
            },
            "Helmet", new WeightRange[]{
                    new WeightRange(0.5, 2.0),
                    new WeightRange(1.5, 4.0),
                    new WeightRange(1.5, 7.0)
            },
            "Shield", new WeightRange[]{
                    new WeightRange(1.0, 4.0),
                    new WeightRange(3.0, 8.0),
                    new WeightRange(3.0, 12.0)
            },
            "Boots", new WeightRange[]{
                    new WeightRange(0.5, 2.0),
                    new WeightRange(1.0, 4.0),
                    new WeightRange(1.0, 6.0)
            }
    );

    private AmmunitionWeightRules() {
    }

    public static WeightRange getAllowedRange(Knight knight, String ammunitionType) {
        WeightRange[] typeRanges = RANGES.getOrDefault(ammunitionType, RANGES.get("Armor"));
        return typeRanges[knight.getKnightTypeNumber() - 1];
    }

    public static WeightRange getAllowedRange(Knight knight, Ammunition item) {
        return getAllowedRange(knight, item.getClass().getSimpleName());
    }

    public static boolean canUse(Knight knight, String ammunitionType, double weight) {
        return getAllowedRange(knight, ammunitionType).contains(weight);
    }

    public static boolean canUse(Knight knight, Ammunition item) {
        return getAllowedRange(knight, item).contains(item.getWeight());
    }

    public static String buildValidationMessage(Knight knight, String ammunitionType, double weight) {
        WeightRange range = getAllowedRange(knight, ammunitionType);
        return String.format(
                "Для типу лицаря \"%s\" предмет типу %s повинен мати вагу %s. Введена вага: %.1f кг.",
                knight.getKnightTypeName(),
                ammunitionType,
                range.format(),
                weight
        );
    }

    public static String buildValidationMessage(Knight knight, Ammunition item) {
        return buildValidationMessage(knight, item.getClass().getSimpleName(), item.getWeight());
    }
}
