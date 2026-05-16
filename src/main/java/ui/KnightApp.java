package ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import model.*;
import service.KnightRepository;
import service.LoggerService;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class KnightApp extends Application {

    private Knight knight = new Knight("");
    private final KnightRepository repository = new KnightRepository();
    private List<Ammunition> allAmmunition = new ArrayList<>(); // Каталог всієї доступної амуніції

    private final TextField nameField = new TextField("");
    private final TextField heightField = new TextField("");
    private final TextField weightField = new TextField("");
    private final TextField strengthField = new TextField("");
    private final TextField enduranceField = new TextField("");

    private final Label statsLabel = new Label();
    private final ListView<Ammunition> catalogListView = new ListView<>(); // Каталог всієї амуніції
    private final ListView<Ammunition> ammunitionListView = new ListView<>(); // Екіпірування лицаря
    private final Label summaryLabel = new Label("Загальна вартість: 0.0 | Вага: 0.0");
    private final Label overweightWarning = new Label("");

    private final TextField minPriceField = new TextField("");
    private final TextField maxPriceField = new TextField("");
    private final TextField minWeightField = new TextField("");
    private final TextField maxWeightField = new TextField("");
    private final TextField materialSearchField = new TextField("");
    private final ComboBox<String> typeSearchCombo = new ComboBox<>(FXCollections.observableArrayList("", "Sword", "Armor", "Helmet", "Shield", "Boots"));
    private final TextField minProtField = new TextField("");
    private final TextField maxProtField = new TextField("");

    private final Pane knightPane = new Pane();

    private final Button calculateButton = new Button("Оновити дані лицаря");
    private final Button editButton = new Button("Редагувати параметри");
    private final Button loadKnightsButton = new Button("Зчитати лицарів");
    private final Button editSelectedKnightButton = new Button("Редагувати вибраного лицаря");
    private final Button addNewKnightButton = new Button("Додати нового лицаря");

    private final ListView<Knight> knightsListView = new ListView<>();

    private final ChoiceBox<String> sortChoice = new ChoiceBox<>(FXCollections.observableArrayList("Вагою", "Ціною", "Назвою", "Захистом"));

    @Override
    public void start(Stage stage) {
        LoggerService.logInfo("Додаток запущено.");
        overweightWarning.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        
        // repository.createTables(); // Тепер ініціалізація бази йде через DatabaseInitializer
        loadKnightFromDB();
        loadAllAmmunitionCatalog();

        VBox knightParamsBox = new VBox(8);
        knightParamsBox.setPadding(new Insets(10));
        knightParamsBox.getChildren().addAll(
                new Label("Параметри лицаря"),
                new Label("Ім'я:"), nameField,
                new Label("Зріст:"), heightField,
                new Label("Вага:"), weightField,
                new Label("Сила:"), strengthField,
                new Label("Витривалість:"), enduranceField,
                calculateButton, editButton
        );

        calculateButton.setOnAction(e -> updateKnight());
        editButton.setOnAction(e -> enableEditing(true));
        editButton.setDisable(true);

        knightsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Knight k, boolean empty) {
                super.updateItem(k, empty);
                if (empty || k == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (Зріст: %.1f, Вага: %.1f, Сила: %d, Витривалість: %d)",
                        k.getName(), k.getHeight(), k.getWeight(), k.getStrength(), k.getEndurance()));
                }
            }
        });

        VBox knightsManagementBox = new VBox(8);
        knightsManagementBox.setPadding(new Insets(10));
        knightsManagementBox.setStyle("-fx-border-color: lightblue; -fx-border-radius: 5;");
        knightsManagementBox.getChildren().addAll(
                new Label("Список лицарів"),
                knightsListView,
                loadKnightsButton,
                editSelectedKnightButton,
                addNewKnightButton
        );

        loadKnightsButton.setOnAction(e -> loadAllKnights());
        editSelectedKnightButton.setOnAction(e -> editSelectedKnight());
        addNewKnightButton.setOnAction(e -> addNewKnight());

        // Налаштування списку каталогу амуніції
        catalogListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Ammunition item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String info = String.format("%s: %s (%.1fкг, %.1fгрн, %s)", 
                        item.getClass().getSimpleName(), item.getName(), item.getWeight(), item.getPrice(), item.getMaterial());
                    if (item instanceof Armor) info += " [Захист: " + ((Armor) item).getDefense() + "]";
                    if (item instanceof Weapon) info += " [Урон: " + ((Weapon) item).getDamage() + "]";
                    setText(info);
                }
            }
        });

        ammunitionListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Ammunition item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String info = String.format("%s: %s (%.1fкг, %.1fгрн, %s)", 
                        item.getClass().getSimpleName(), item.getName(), item.getWeight(), item.getPrice(), item.getMaterial());
                    if (item instanceof Armor) info += " [Захист: " + ((Armor) item).getDefense() + "]";
                    if (item instanceof Weapon) info += " [Урон: " + ((Weapon) item).getDamage() + "]";
                    setText(info);
                }
            }
        });

        VBox searchBox = new VBox(5);
        searchBox.setPadding(new Insets(10));
        searchBox.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5;");
        typeSearchCombo.valueProperty().addListener((obs, oldType, newType) -> updateProtectionFilterAvailability());
        updateProtectionFilterAvailability();
        
        Button equipFromCatalogBtn = new Button("Екіпірувати вибране");
        equipFromCatalogBtn.setOnAction(e -> {
            Ammunition selected = catalogListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                knight.equip(selected);
                repository.saveKnight(knight);
                updateEquipmentList(knight.getEquipment());
                LoggerService.logInfo("Екіпіровано: " + selected.getName());
            }
        });
        
        searchBox.getChildren().addAll(
            new Label("Каталог амуніції:"),
            catalogListView,
            equipFromCatalogBtn,
            new Label("Пошук/Фільтр:"),
            new HBox(5, new Label("Ціна від:"), minPriceField, new Label("до:"), maxPriceField),
            new HBox(5, new Label("Вага від:"), minWeightField, new Label("до:"), maxWeightField),
            new HBox(5, new Label("Матеріал:"), materialSearchField, new Label("Тип:"), typeSearchCombo),
            new HBox(5, new Label("Захист від:"), minProtField, new Label("до:"), maxProtField),
            new Button("Застосувати фільтр") {{
                setOnAction(e -> applyFilter());
            }},
            new Button("Показати весь каталог") {{
                setOnAction(e -> showAllCatalog());
            }}
        );

        VBox ammoActionsBox = new VBox(10);
        ammoActionsBox.setPadding(new Insets(10));
        
        Button addAmmoButton = new Button("Додати амуніцію");
        addAmmoButton.setOnAction(e -> showAmmoDialog(null));
        
        Button editAmmoButton = new Button("Редагувати вибране");
        editAmmoButton.setOnAction(e -> {
            Ammunition selected = ammunitionListView.getSelectionModel().getSelectedItem();
            if (selected != null) showAmmoDialog(selected);
        });
        
        Button deleteAmmoButton = new Button("Видалити вибране");
        deleteAmmoButton.setOnAction(e -> {
            Ammunition selected = ammunitionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                knight.unequip(selected);
                repository.saveKnight(knight);
                updateEquipmentList(knight.getEquipment());
            }
        });

        HBox sortBox = new HBox(5, new Label("Сортувати за:"), sortChoice);
        sortChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            switch (newVal) {
                case "Вагою": knight.sortByWeight(); break;
                case "Ціною": knight.sortByPrice(); break;
                case "Назвою": knight.sortByName(); break;
                case "Захистом": knight.sortByProtection(); break;
            }
            updateEquipmentList(knight.getEquipment());
        });

        Button compareKitsButton = new Button("Порівняти комплекти");
        compareKitsButton.setOnAction(e -> compareKits());

        Button saveToFileButton = new Button("Зберегти у файл");
        saveToFileButton.setOnAction(e -> saveToFile());
        
        Button loadFromFileButton = new Button("Завантажити з файлу");
        loadFromFileButton.setOnAction(e -> loadFromFile());

        ammoActionsBox.getChildren().addAll(
            new Label("Екіпірування"),
            ammunitionListView,
            summaryLabel,
            overweightWarning,
            sortBox,
            new HBox(5, addAmmoButton, editAmmoButton, deleteAmmoButton),
            new HBox(5, saveToFileButton, loadFromFileButton),
            compareKitsButton
        );

        HBox mainContent = new HBox(20);
        mainContent.getChildren().addAll(knightParamsBox, knightPane, statsLabel, ammoActionsBox, searchBox, knightsManagementBox);
        mainContent.setPadding(new Insets(15));

        updateKnight();
        updateEquipmentList(knight.getEquipment());

        Scene scene = new Scene(mainContent, 1400, 700);
        stage.setTitle("Лицарський зброяр");
        stage.setScene(scene);
        stage.show();
    }

    private void loadKnightFromDB() {
        try {
            List<Knight> knights = repository.getAllKnights();
            if (!knights.isEmpty()) {
                this.knight = knights.get(0);
                nameField.setText(knight.getName());
                heightField.setText(String.valueOf(knight.getHeight()));
                weightField.setText(String.valueOf(knight.getWeight()));
                strengthField.setText(String.valueOf(knight.getStrength()));
                enduranceField.setText(String.valueOf(knight.getEndurance()));
                LoggerService.logInfo("Дані лицаря завантажено з бази даних.");
            } else {
                LoggerService.logInfo("База даних порожня. Очікуємо введення даних від користувача.");
            }
        } catch (Exception e) {
            LoggerService.logCriticalError("Помилка завантаження даних. Очікуємо введення даних.", e);
        }
    }

    private void applyFilter() {
        try {
            Double minP = minPriceField.getText().isEmpty() ? null : Double.parseDouble(minPriceField.getText());
            Double maxP = maxPriceField.getText().isEmpty() ? null : Double.parseDouble(maxPriceField.getText());
            Double minW = minWeightField.getText().isEmpty() ? null : Double.parseDouble(minWeightField.getText());
            Double maxW = maxWeightField.getText().isEmpty() ? null : Double.parseDouble(maxWeightField.getText());
            String mat = materialSearchField.getText().trim();
            String type = typeSearchCombo.getValue();
            Integer minProt = minProtField.getText().isEmpty() ? null : Integer.parseInt(minProtField.getText());
            Integer maxProt = maxProtField.getText().isEmpty() ? null : Integer.parseInt(maxProtField.getText());
            if (isWeaponTypeSelected(type)) {
                minProt = null;
                maxProt = null;
            }

            List<Ammunition> filtered = filterAmmunition(allAmmunition, minP, maxP, minW, maxW, type, mat, minProt, maxProt);
            
            if (filtered.isEmpty()) {
                LoggerService.logInfo("Пошук не дав результатів з параметрами: ціна[" + minP + "-" + maxP + "]");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Результат пошуку");
                alert.setHeaderText("Нічого не знайдено");
                alert.setContentText("За вказаними критеріями амуніції не знайдено.");
                alert.showAndWait();
            }
            
            updateCatalogList(filtered);
            LoggerService.logInfo("Знайдено " + filtered.size() + " предметів амуніції");
        } catch (NumberFormatException ex) {
            showInputErrors("Некоректні дані для фільтрації.");
        }
    }
    
    private List<Ammunition> filterAmmunition(List<Ammunition> items, Double minPrice, Double maxPrice, 
                                               Double minWeight, Double maxWeight, String type, 
                                               String material, Integer minProt, Integer maxProt) {
        return items.stream()
                .filter(item -> (minPrice == null || item.getPrice() >= minPrice))
                .filter(item -> (maxPrice == null || item.getPrice() <= maxPrice))
                .filter(item -> (minWeight == null || item.getWeight() >= minWeight))
                .filter(item -> (maxWeight == null || item.getWeight() <= maxWeight))
                .filter(item -> (type == null || type.isEmpty() || item.getClass().getSimpleName().equalsIgnoreCase(type)))
                .filter(item -> (material == null || material.isEmpty() || item.getMaterial().toLowerCase().contains(material.toLowerCase())))
                .filter(item -> {
                    if (minProt == null && maxProt == null) return true;
                    if (item instanceof Armor armor) {
                        int defense = armor.getDefense();
                        return (minProt == null || defense >= minProt)
                                && (maxProt == null || defense <= maxProt);
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
    
    private void showAllCatalog() {
        updateCatalogList(allAmmunition);
        minPriceField.clear();
        maxPriceField.clear();
        minWeightField.clear();
        maxWeightField.clear();
        materialSearchField.clear();
        typeSearchCombo.setValue("");
        minProtField.clear();
        maxProtField.clear();
        LoggerService.logInfo("Показано весь каталог амуніції: " + allAmmunition.size() + " предметів");
    }

    private void updateProtectionFilterAvailability() {
        boolean weaponTypeSelected = isWeaponTypeSelected(typeSearchCombo.getValue());
        minProtField.setDisable(weaponTypeSelected);
        maxProtField.setDisable(weaponTypeSelected);
        if (weaponTypeSelected) {
            minProtField.clear();
            maxProtField.clear();
        }
    }

    private boolean isWeaponTypeSelected(String type) {
        return "Sword".equalsIgnoreCase(type) || "Weapon".equalsIgnoreCase(type);
    }
    
    private void loadAllAmmunitionCatalog() {
        try {
            allAmmunition = repository.getAllAmmunition();
            updateCatalogList(allAmmunition);
            LoggerService.logInfo("Завантажено каталог амуніції: " + allAmmunition.size() + " предметів");
        } catch (Exception e) {
            LoggerService.logCriticalError("Помилка завантаження каталогу амуніції", e);
        }
    }
    
    private void updateCatalogList(List<Ammunition> items) {
        catalogListView.setItems(FXCollections.observableArrayList(items));
    }
    
    private void updateEquipmentList(List<Ammunition> items) {
        ammunitionListView.setItems(FXCollections.observableArrayList(items));
        double totalWeight = knight.calculateTotalWeight();
        summaryLabel.setText(String.format("Загальна вартість: %.1f | Вага: %.1f кг", 
            knight.calculateTotalPrice(), totalWeight));
        
        if (knight.isOverweight()) {
            overweightWarning.setText(String.format("УВАГА: Перевантаження (> %.1f кг)!", knight.getMaxWeight()));
        } else {
            overweightWarning.setText("");
        }
    }
    
    private void showAmmoDialog(Ammunition existing) {
        Stage dialog = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        
        TextField nameF = new TextField(existing != null ? existing.getName() : "");
        TextField weightF = new TextField(existing != null ? String.valueOf(existing.getWeight()) : "");
        TextField priceF = new TextField(existing != null ? String.valueOf(existing.getPrice()) : "");
        TextField materialF = new TextField(existing != null ? existing.getMaterial() : "");
        
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("Sword", "Armor", "Helmet", "Shield", "Boots"));
        if (existing != null) typeCombo.setValue(existing.getClass().getSimpleName());
        else typeCombo.setValue("Armor");
        
        TextField specialF = new TextField(); // damage or defense
        specialF.setPromptText("Урон або Захист");
        if (existing instanceof Weapon) specialF.setText(String.valueOf(((Weapon) existing).getDamage()));
        else if (existing instanceof Armor) specialF.setText(String.valueOf(((Armor) existing).getDefense()));

        Button saveBtn = new Button("Зберегти");
        saveBtn.setOnAction(e -> {
            try {
                String name = nameF.getText();
                double w = Double.parseDouble(weightF.getText());
                double p = Double.parseDouble(priceF.getText());
                String mat = materialF.getText();
                int spec = Integer.parseInt(specialF.getText());
                String type = typeCombo.getValue();
                
                Ammunition newItem = switch (type) {
                    case "Sword" -> new Sword(name, w, p, mat, spec);
                    case "Helmet" -> new Helmet(name, w, p, mat, spec);
                    case "Shield" -> new Shield(name, w, p, mat, spec);
                    case "Boots" -> new Boots(name, w, p, mat, spec);
                    default -> new Armor(name, w, p, mat, spec);
                };
                
                if (existing != null) {
                    newItem.setId(existing.getId());
                    int idx = knight.getEquipment().indexOf(existing);
                    knight.updateAmmunition(idx, newItem);
                } else {
                    knight.equip(newItem);
                }
                repository.saveKnight(knight);
                updateEquipmentList(knight.getEquipment());
                dialog.close();
            } catch (Exception ex) {
                showInputErrors("Перевірте введені дані.");
            }
        });

        root.getChildren().addAll(
            new Label("Тип:"), typeCombo,
            new Label("Назва:"), nameF,
            new Label("Вага:"), weightF,
            new Label("Ціна:"), priceF,
            new Label("Матеріал:"), materialF,
            new Label("Параметр (Урон/Захист):"), specialF,
            saveBtn
        );
        
        dialog.setScene(new Scene(root));
        dialog.setTitle(existing == null ? "Додати" : "Редагувати");
        dialog.show();
    }

    private void compareKits() {
        Knight opponent = new Knight("Драконячий Воїн");
        
        // Екіпірування противника найкращими драконячими предметами з нового каталогу
        allAmmunition.stream()
            .filter(item -> item.getName().equals("Меч із драконячої кістки"))
            .findFirst()
            .ifPresent(opponent::equip);
            
        allAmmunition.stream()
            .filter(item -> item.getName().equals("Драконяча броня імператора"))
            .findFirst()
            .ifPresent(opponent::equip);
            
        allAmmunition.stream()
            .filter(item -> item.getName().equals("Драконячий бойовий шолом"))
            .findFirst()
            .ifPresent(opponent::equip);
            
        allAmmunition.stream()
            .filter(item -> item.getName().equals("Драконячий щит володаря"))
            .findFirst()
            .ifPresent(opponent::equip);
            
        allAmmunition.stream()
            .filter(item -> item.getName().equals("Драконячі чоботи володаря"))
            .findFirst()
            .ifPresent(opponent::equip);
        
        String comparisonResult = Knight.compareKits(knight, opponent);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Порівняння комплектів");
        alert.setHeaderText("Ваш комплект проти Драконячого Воїна");
        alert.setContentText(comparisonResult);
        alert.showAndWait();
    }

    private void saveToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter("equipment.txt"))) {
            for (Ammunition item : knight.getEquipment()) {
                out.println(item.getClass().getSimpleName() + ";" + 
                            item.getName() + ";" + 
                            item.getWeight() + ";" + 
                            item.getPrice() + ";" + 
                            item.getMaterial() + ";" + 
                            (item instanceof Armor ? ((Armor) item).getDefense() : (item instanceof Weapon ? ((Weapon) item).getDamage() : 0)));
            }
            LoggerService.logInfo("Амуніцію збережено у файл equipment.txt");
        } catch (IOException e) {
            showInputErrors("Помилка збереження файлу.");
        }
    }

    private void loadFromFile() {
        File file = new File("equipment.txt");
        if (!file.exists()) {
            showInputErrors("Файл не знайдено.");
            return;
        }
        try (Scanner sc = new Scanner(file)) {
            List<Ammunition> newEq = new ArrayList<>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length < 6) continue;
                String type = parts[0];
                String name = parts[1];
                double w = Double.parseDouble(parts[2]);
                double p = Double.parseDouble(parts[3]);
                String mat = parts[4];
                int spec = Integer.parseInt(parts[5]);
                
                switch (type) {
                    case "Sword": newEq.add(new Sword(name, w, p, mat, spec)); break;
                    case "Helmet": newEq.add(new Helmet(name, w, p, mat, spec)); break;
                    case "Shield": newEq.add(new Shield(name, w, p, mat, spec)); break;
                    case "Boots": newEq.add(new Boots(name, w, p, mat, spec)); break;
                    default: newEq.add(new Armor(name, w, p, mat, spec)); break;
                }
            }
            for (Ammunition a : new ArrayList<>(knight.getEquipment())) {
                knight.unequip(a);
            }
            newEq.forEach(knight::equip);
            updateEquipmentList(knight.getEquipment());
            LoggerService.logInfo("Амуніцію завантажено з файлу.");
        } catch (Exception e) {
            showInputErrors("Помилка завантаження файлу.");
        }
    }

    private void enableEditing(boolean enabled) {
        LoggerService.logInfo("Режим редагування змінено на: " + (enabled ? "увімкнено" : "вимкнено"));
        nameField.setEditable(enabled);
        heightField.setEditable(enabled);
        weightField.setEditable(enabled);
        strengthField.setEditable(enabled);
        enduranceField.setEditable(enabled);
        
        calculateButton.setDisable(!enabled);
        editButton.setDisable(enabled);
        
        if (enabled) {
            nameField.requestFocus();
        }
    }

    private void updateKnight() {
        LoggerService.logInfo("Оновлення параметрів лицаря...");
        StringBuilder errors = new StringBuilder();

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            errors.append("- Ім'я не може бути порожнім.\n");
        }

        Double height = validateDoubleInRange(heightField, "Зріст", 50.0, 250.0, errors);
        Double weight = validateDoubleInRange(weightField, "Вага", 30.0, 300.0, errors);
        Integer strength = validateIntegerInRange(strengthField, "Сила", 1, 100, errors);
        Integer endurance = validateIntegerInRange(enduranceField, "Витривалість", 1, 100, errors);

        if (!errors.isEmpty()) {
            String errorMessage = "Виявлено помилки валідації:\n" + errors;
            LoggerService.logCriticalError(errorMessage, new IllegalArgumentException("Некоректний ввід користувача"));
            showInputErrors(errors.toString());
            return;
        }

        LoggerService.logInfo("Параметри лицаря успішно оновлено: Ім'я=" + name + ", Зріст=" + height + ", Вага=" + weight);
        enableEditing(false);

        knight.setName(name);
        if (height != null) knight.setHeight(height);
        if (weight != null) knight.setWeight(weight);
        if (strength != null) knight.setStrength(strength);
        if (endurance != null) knight.setEndurance(endurance);
        // Зберігаємо в базу даних
        repository.saveKnight(knight);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Успіх");
        successAlert.setHeaderText(null);
        successAlert.setContentText("Дані лицаря успішно збережено у базу даних!");
        successAlert.show();

        drawKnight(knight);

        statsLabel.setText(
                "Ім'я: " + knight.getName() +
                        "\nАтака: " + knight.calculateAttack() +
                        "\nЗахист: " + knight.calculateDefense() +
                        "\nШвидкість: " + knight.calculateSpeed() +
                        "\nТип статури: " + knight.getBodyType()
        );
    }

    private Double validateDoubleInRange(TextField field, String fieldName, final double min, final double max, StringBuilder errors) {
        String text = field.getText().trim();
        try {
            double value = Double.parseDouble(text);
            if (value < min || value > max) {
                errors.append("- ").append(fieldName).append(" має бути від ").append(min).append(" до ").append(max).append(".\n");
                return null;
            }
            return value;
        } catch (NumberFormatException exception) {
            errors.append("- Неправильне введення у полі \"").append(fieldName).append("\". Будь ласка, введіть число.\n");
            return null;
        }
    }

    private Integer validateIntegerInRange(TextField field, String fieldName, final int min, final int max, StringBuilder errors) {
        String text = field.getText().trim();
        try {
            int value = Integer.parseInt(text);
            if (value < min || value > max) {
                errors.append("- ").append(fieldName).append(" має бути від ").append(min).append(" до ").append(max).append(".\n");
                return null;
            }
            return value;
        } catch (NumberFormatException exception) {
            errors.append("- Неправильне введення у полі \"").append(fieldName).append("\". Будь ласка, введіть ціле число.\n");
            return null;
        }
    }

    private void showInputErrors(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка введення");
        alert.setHeaderText("Будь ласка, виправте наступні помилки:");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    private void loadAllKnights() {
        try {
            List<Knight> knights = repository.getAllKnights();
            knightsListView.setItems(FXCollections.observableArrayList(knights));
            LoggerService.logInfo("Завантажено " + knights.size() + " лицарів з бази даних.");
        } catch (Exception e) {
            LoggerService.logCriticalError("Помилка завантаження списку лицарів.", e);
            showInputErrors("Помилка завантаження лицарів з бази даних.");
        }
    }

    private void editSelectedKnight() {
        Knight selected = knightsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInputErrors("Будь ласка, виберіть лицаря зі списку.");
            return;
        }
        this.knight = selected;
        nameField.setText(knight.getName());
        heightField.setText(String.valueOf(knight.getHeight()));
        weightField.setText(String.valueOf(knight.getWeight()));
        strengthField.setText(String.valueOf(knight.getStrength()));
        enduranceField.setText(String.valueOf(knight.getEndurance()));
        updateEquipmentList(knight.getEquipment());

        // Малюємо лицаря
        drawKnight(knight);

        // Оновлюємо статистику
        statsLabel.setText(
                "Ім'я: " + knight.getName() +
                        "\nАтака: " + knight.calculateAttack() +
                        "\nЗахист: " + knight.calculateDefense() +
                        "\nШвидкість: " + knight.calculateSpeed() +
                        "\nТип статури: " + knight.getBodyType()
        );

        // Увімкнути режим редагування
        enableEditing(true);

        LoggerService.logInfo("Обрано лицаря для редагування: " + knight.getName());
    }

    private void addNewKnight() {
        this.knight = new Knight("");
        nameField.setText("");
        heightField.setText("");
        weightField.setText("");
        strengthField.setText("");
        enduranceField.setText("");

        updateEquipmentList(knight.getEquipment());

        knightPane.getChildren().clear();

        statsLabel.setText("");

        enableEditing(true);

        LoggerService.logInfo("Створення нового лицаря.");
    }

    private void drawKnight(Knight knight) {
        knightPane.getChildren().clear();

        double bodyHeight = knight.getHeight() / 2;
        double bodyWidth = knight.getWeight() / 2;

        Circle head = new Circle(150, 60, 25);

        Rectangle body = new Rectangle(
                150 - bodyWidth / 2,
                90,
                bodyWidth,
                bodyHeight
        );
        Line leftLeg = new Line(135, 90 + bodyHeight, 110, 90 + bodyHeight + 70);
        Line rightLeg = new Line(165, 90 + bodyHeight, 190, 90 + bodyHeight + 70);

        Line leftArm = new Line(150 - bodyWidth / 2, 120, 80, 170);
        Line rightArm = new Line(150 + bodyWidth / 2, 120, 220, 170);

        Rectangle sword = new Rectangle(220, 120, 8, 60 + (double) knight.getStrength() / 2);
        Rectangle shield = new Rectangle(65, 145, 35, 55);

        knightPane.getChildren().addAll(
                head, body, leftLeg, rightLeg, leftArm, rightArm, sword, shield
        );
    }
    public static void main(String[] args) {
        launch(args);
    }
}
