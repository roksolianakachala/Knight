package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmmunitionTest {

    private static class TestAmmunition extends Ammunition {
        TestAmmunition(int id, String name, double weight, double price, String material) {
            super(id, name, weight, price, material);
        }
    }

    @Test
    void ammunitionAccessorsUpdateCommonFields() {
        Sword sword = new Sword("Iron sword", 2.5, 300.0, "Iron", 35);

        sword.setId(7);
        sword.setName("Steel sword");
        sword.setWeight(3.0);
        sword.setPrice(500.0);
        sword.setMaterial("Steel");

        assertEquals(7, sword.getId());
        assertEquals("Steel sword", sword.getName());
        assertEquals(3.0, sword.getWeight());
        assertEquals(500.0, sword.getPrice());
        assertEquals("Steel", sword.getMaterial());
        assertTrue(sword.toString().contains("Steel sword"));
    }

    @Test
    void ammunitionIdConstructorInitializesCommonFields() {
        Ammunition item = new TestAmmunition(12, "Training item", 1.5, 75.0, "Wood");

        assertEquals(12, item.getId());
        assertEquals("Training item", item.getName());
        assertEquals(1.5, item.getWeight());
        assertEquals(75.0, item.getPrice());
        assertEquals("Wood", item.getMaterial());
    }

    @Test
    void weaponAccessorsAndSwordStringExposeDamage() {
        Sword sword = new Sword("Blade", 2.0, 250.0, "Iron", 20);

        sword.setDamage(45);

        assertEquals(45, sword.getDamage());
        assertTrue(sword.toString().startsWith("Sword: Weapon:"));
        assertTrue(sword.toString().contains("damage=45"));
    }

    @Test
    void armorAccessorsAndDerivedArmorTypesExposeDefense() {
        Armor armor = new Armor("Armor", 8.0, 800.0, "Steel", 30);
        Helmet helmet = new Helmet("Helmet", 2.0, 200.0, "Bronze", 15);
        Shield shield = new Shield("Shield", 4.0, 150.0, "Wood", 10);
        Boots boots = new Boots("Boots", 1.0, 90.0, "Leather", 5);

        armor.setDefense(40);

        assertEquals(40, armor.getDefense());
        assertTrue(armor.toString().startsWith("Armor:"));
        assertTrue(helmet.toString().startsWith("Helmet:"));
        assertTrue(shield.toString().startsWith("Shield:"));
        assertTrue(boots.toString().startsWith("Boots:"));
    }
}
