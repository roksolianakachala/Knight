package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnightTest {

    @Test
    void calculatesStatsFromKnightParameters() {
        Knight knight = new Knight("Arthur", 180, 80, 50, 50);

        assertEquals(100, knight.calculateAttack());
        assertEquals(90, knight.calculateDefense());
        assertEquals(20, knight.calculateSpeed());

        knight.setWeight(130);
        assertEquals(10, knight.calculateSpeed());
        assertTrue(knight.toString().contains("Knight{name='Arthur'"));
    }

    @Test
    void constructorsAndSettersKeepKnightState() {
        Knight knight = new Knight(3, "Lancelot", 185, 82, 60, 55, 45);

        knight.setId(4);
        knight.setName("Galahad");
        knight.setHeight(190);
        knight.setWeight(90);
        knight.setStrength(70);
        knight.setEndurance(65);
        knight.setMaxWeight(35);

        assertEquals(4, knight.getId());
        assertEquals("Galahad", knight.getName());
        assertEquals(190, knight.getHeight());
        assertEquals(90, knight.getWeight());
        assertEquals(70, knight.getStrength());
        assertEquals(65, knight.getEndurance());
        assertEquals(35, knight.getMaxWeight());
    }

    @Test
    void bodyTypeCoversAllBmiBranches() {
        Knight slim = new Knight("Slim", 190, 60, 40, 40);
        Knight normal = new Knight("Normal", 180, 75, 40, 40);
        Knight heavy = new Knight("Heavy", 165, 90, 40, 40);

        assertNotEquals(slim.getBodyType(), normal.getBodyType());
        assertNotEquals(normal.getBodyType(), heavy.getBodyType());
        assertNotEquals(slim.getBodyType(), heavy.getBodyType());
    }

    @Test
    void equipmentCanBeEquippedUpdatedRemovedAndCopied() {
        Knight knight = new Knight("Arthur");
        Sword sword = new Sword("Sword", 3.0, 500.0, "Steel", 40);
        Armor armor = new Armor("Armor", 12.0, 1200.0, "Steel", 70);
        Helmet helmet = new Helmet("Helmet", 2.0, 300.0, "Steel", 20);

        knight.equip(sword);
        knight.equip(armor);

        assertEquals(1700.0, knight.calculateTotalPrice());
        assertEquals(15.0, knight.calculateTotalWeight());
        assertFalse(knight.isOverweight());

        knight.setMaxWeight(10);
        assertTrue(knight.isOverweight());

        knight.updateAmmunition(1, helmet);
        knight.updateAmmunition(-1, armor);
        knight.updateAmmunition(10, armor);

        List<Ammunition> copy = knight.getEquipment();
        copy.clear();
        assertEquals(2, knight.getEquipment().size());

        knight.unequip(sword);
        assertEquals(List.of(helmet), knight.getEquipment());
    }

    @Test
    void sortsEquipmentByWeightPriceNameAndProtection() {
        Knight knight = new Knight("Arthur");
        Sword sword = new Sword("Bronze sword", 4.0, 200.0, "Bronze", 30);
        Armor armor = new Armor("Plate armor", 14.0, 1500.0, "Steel", 80);
        Helmet helmet = new Helmet("Arming cap", 1.0, 100.0, "Leather", 10);

        knight.equip(sword);
        knight.equip(armor);
        knight.equip(helmet);

        knight.sortByWeight();
        assertEquals(List.of(helmet, sword, armor), knight.getEquipment());

        knight.sortByPrice();
        assertEquals(List.of(helmet, sword, armor), knight.getEquipment());

        knight.sortByName();
        assertEquals(List.of(helmet, sword, armor), knight.getEquipment());

        knight.sortByProtection();
        assertEquals(List.of(armor, helmet, sword), knight.getEquipment());
    }

    @Test
    void findsEquipmentByCriteria() {
        Knight knight = new Knight("Arthur");
        Sword sword = new Sword("Short sword", 2.5, 350.0, "Iron", 35);
        Armor armor = new Armor("Scout armor", 5.0, 300.0, "Leather", 20);
        Shield shield = new Shield("Steel shield", 6.0, 700.0, "Steel", 60);

        knight.equip(sword);
        knight.equip(armor);
        knight.equip(shield);

        assertEquals(List.of(sword), knight.findByCriteria(300.0, 400.0, 2.0, 3.0, "Sword", "iron", null));
        assertEquals(List.of(armor), knight.findByCriteria(null, 350.0, 4.0, null, null, "leather", null));
        assertTrue(knight.findByCriteria(1000.0, null, null, null, null, null, null).isEmpty());
        assertTrue(knight.findByCriteria(null, null, 10.0, null, null, null, null).isEmpty());
        assertTrue(knight.findByCriteria(null, null, null, null, "Bow", null, null).isEmpty());
        assertTrue(knight.findByCriteria(null, null, null, null, null, "Gold", null).isEmpty());
        assertEquals(List.of(shield), knight.findByCriteria(null, null, null, null, "Shield", "steel", 50));
        assertTrue(knight.findByCriteria(null, null, null, null, "Sword", null, 10).isEmpty());
        assertEquals(3, knight.findByCriteria(null, null, null, null, "", "", null).size());
    }

    @Test
    void comparesKitsForCheaperLighterAndBetterDefenseCases() {
        Knight first = new Knight("First", 180, 70, 50, 70);
        Knight second = new Knight("Second", 180, 90, 50, 30);
        first.equip(new Sword("Cheap", 2.0, 100.0, "Iron", 20));
        second.equip(new Armor("Expensive", 10.0, 500.0, "Steel", 50));

        String firstWins = Knight.compareKits(first, second);

        assertTrue(firstWins.contains("First"));
        assertTrue(firstWins.contains("Second"));
        assertTrue(firstWins.contains("100.0 / 500.0"));
        assertTrue(firstWins.contains("2.0 / 10.0"));

        Knight equalA = new Knight("Equal A", 180, 80, 40, 40);
        Knight equalB = new Knight("Equal B", 180, 80, 40, 40);
        String equal = Knight.compareKits(equalA, equalB);

        assertTrue(equal.contains("0.0 / 0.0"));
        assertTrue(equal.contains("80 / 80"));

        Knight expensiveHeavyWeak = new Knight("Expensive", 180, 100, 40, 20);
        Knight cheapLightStrong = new Knight("Cheap", 180, 60, 40, 80);
        expensiveHeavyWeak.equip(new Armor("Gold armor", 20.0, 1000.0, "Gold", 10));
        cheapLightStrong.equip(new Boots("Light boots", 1.0, 50.0, "Leather", 5));

        String secondWins = Knight.compareKits(expensiveHeavyWeak, cheapLightStrong);

        assertTrue(secondWins.contains("1000.0 / 50.0"));
        assertTrue(secondWins.contains("20.0 / 1.0"));
        assertTrue(secondWins.contains("70 / 110"));
    }
}
