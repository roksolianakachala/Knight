package ui;

import model.*;
import service.DatabaseInitializer;
import service.KnightRepository;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Ініціалізація бази даних (створення таблиць та наповнення каталогу)
        DatabaseInitializer.initialize();
        
        KnightRepository repository = new KnightRepository();
        
        // Завантаження каталогу амуніції з БД
        List<Ammunition> catalog = repository.getAllAmmunition();
        System.out.println("=== Каталог амуніції з БД ===");
        System.out.println("Всього предметів: " + catalog.size());
        
        // Створення лицаря
        Knight knight = new Knight("Артур", 180, 80, 50, 50);
        
        // Екіпірування лицаря предметами з каталогу БД
        catalog.stream()
            .filter(item -> item.getName().equals("Міфрільний меч"))
            .findFirst()
            .ifPresent(knight::equip);
            
        catalog.stream()
            .filter(item -> item.getName().equals("Повний латний обладунок"))
            .findFirst()
            .ifPresent(knight::equip);

        System.out.println("\n=== Екіпірування лицаря " + knight.getName() + " ===");
        knight.getEquipment().forEach(System.out::println);

        System.out.println("\n=== Загальна вартість ===");
        System.out.println(knight.calculateTotalPrice() + " грн");

        System.out.println("\n=== Пошук в каталозі: діапазон ціни 200-400 грн ===");
        catalog.stream()
            .filter(item -> item.getPrice() >= 200 && item.getPrice() <= 400)
            .forEach(System.out::println);
            
        System.out.println("\n=== Відсортовано екіпірування за вагою ===");
        knight.sortByWeight();
        knight.getEquipment().forEach(System.out::println);
        
        // Збереження лицаря в БД
        repository.saveKnight(knight);
        System.out.println("\n✓ Лицар збережений в базу даних");
    }
}