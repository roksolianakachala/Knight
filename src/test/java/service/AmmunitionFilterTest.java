package service;

import model.Ammunition;
import model.Armor;
import model.Helmet;
import model.Shield;
import model.Sword;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmmunitionFilterTest {

    private final Sword sword = new Sword("Iron short sword", 2.8, 350.0, "Iron", 35);
    private final Armor armor = new Armor("Leather armor", 5.0, 300.0, "Leather", 20);
    private final Helmet helmet = new Helmet("Steel helmet", 4.0, 900.0, "Steel", 45);
    private final Shield shield = new Shield("Steel shield", 7.0, 1200.0, "Steel", 65);
    private final List<Ammunition> catalog = List.of(sword, armor, helmet, shield);

    @Test
    void filtersByPriceWeightTypeAndMaterial() {
        List<Ammunition> result = AmmunitionFilter.filter(
                catalog,
                200.0,
                500.0,
                2.0,
                4.0,
                "Sword",
                "iron",
                null,
                null
        );

        assertEquals(List.of(sword), result);
    }

    @Test
    void returnsNoItemsWhenAnyRangeOrTextCriterionDoesNotMatch() {
        assertTrue(AmmunitionFilter.filter(catalog, 2000.0, null, null, null, null, null, null, null).isEmpty());
        assertTrue(AmmunitionFilter.filter(catalog, null, 100.0, null, null, null, null, null, null).isEmpty());
        assertTrue(AmmunitionFilter.filter(catalog, null, null, 20.0, null, null, null, null, null).isEmpty());
        assertTrue(AmmunitionFilter.filter(catalog, null, null, null, 1.0, null, null, null, null).isEmpty());
        assertTrue(AmmunitionFilter.filter(catalog, null, null, null, null, "Bow", null, null, null).isEmpty());
        assertTrue(AmmunitionFilter.filter(catalog, null, null, null, null, null, "Gold", null, null).isEmpty());
    }

    @Test
    void filtersArmorItemsByProtectionRange() {
        List<Ammunition> result = AmmunitionFilter.filter(
                catalog,
                null,
                null,
                null,
                null,
                "",
                "",
                40,
                70
        );

        assertEquals(List.of(helmet, shield), result);
    }

    @Test
    void allowsOpenEndedProtectionRanges() {
        assertEquals(List.of(helmet, shield), AmmunitionFilter.filter(catalog, null, null, null, null, null, null, 40, null));
        assertEquals(List.of(armor, helmet), AmmunitionFilter.filter(catalog, null, null, null, null, null, null, null, 50));
    }

    @Test
    void excludesWeaponsWhenProtectionRangeIsRequested() {
        List<Ammunition> result = AmmunitionFilter.filter(
                catalog,
                null,
                null,
                null,
                null,
                "Sword",
                null,
                10,
                100
        );

        assertTrue(result.isEmpty());
    }
}
