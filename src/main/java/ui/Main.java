package ui;

import model.*;

import service.DatabaseInitializer;

public class Main {

    public static void main(String[] args) {
        DatabaseInitializer.initialize();
        Knight knight = new Knight("Артур", 180, 80, 50, 50);

        Sword sword = new Sword(
                "Excalibur",
                5.5,
                300,
                "Сталь",
                50
        );

        Armor armor = new Armor(
                "Steel Armor",
                15,
                500,
                "Сталь",
                80
        );

        knight.equip(sword);
        knight.equip(armor);

        System.out.println("Екіпірування:");
        knight.getEquipment().forEach(System.out::println);

        System.out.println("\nЗагальна вартість:");
        System.out.println(knight.calculateTotalPrice());

        System.out.println("\nВідсортовано за вагою:");
        knight.sortByWeight();
        knight.getEquipment().forEach(System.out::println);

        System.out.println("\nДіапазон ціни 200-400:");
        knight.findByCriteria(200.0, 400.0, null, null, null, null, null)
                .forEach(System.out::println);
    }
}