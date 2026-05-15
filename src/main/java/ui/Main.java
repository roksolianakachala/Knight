package ui;

import model.*;

public class Main {

    public static void main(String[] args) {

        Knight knight = new Knight("Arthur");

        Sword sword = new Sword(
                "Excalibur",
                5.5,
                300,
                50
        );

        Armor armor = new Armor(
                "Steel Armor",
                15,
                500,
                80
        );

        knight.equip(sword);
        knight.equip(armor);

        System.out.println("Equipment:");
        knight.getEquipment().forEach(System.out::println);

        System.out.println("\nTotal price:");
        System.out.println(knight.calculateTotalPrice());

        System.out.println("\nSorted by weight:");
        knight.sortByWeight()
                .forEach(System.out::println);

        System.out.println("\nPrice range 200-400:");
        knight.findByPriceRange(200, 400)
                .forEach(System.out::println);
    }
}