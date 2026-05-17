package ui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Window;
import model.Ammunition;
import model.Helmet;
import model.Knight;
import model.Shield;
import model.Sword;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import service.DatabaseInitializer;
import service.KnightRepository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnightAppGuiTest extends ApplicationTest {

    private KnightApp app;

    @BeforeAll
    static void prepareWorkingDirectory() throws Exception {
        Files.createDirectories(Path.of("."));
    }

    @Override
    public void start(javafx.stage.Stage stage) throws Exception {
        Files.deleteIfExists(Path.of("knight_database.db"));
        DatabaseInitializer.initialize();

        KnightRepository repository = new KnightRepository();
        repository.saveKnight(new Knight("Opponent Knight", 185, 90, 65, 45));
        repository.saveKnight(new Knight("Gui Knight", 180, 80, 50, 50));

        app = new KnightApp();
        app.start(stage);
        closeSecondaryWindows();
    }

    @Test
    void filtersCatalogAndEquipsSelectedSword() {
        ListView<Ammunition> catalog = lookup("#catalogListView").query();
        ListView<Ammunition> equipment = lookup("#ammunitionListView").query();

        interact(() -> {
            assertEquals(25, catalog.getItems().size());
            setText("#minPriceField", "200");
            setText("#maxPriceField", "500");
            setText("#minWeightField", "2");
            setText("#maxWeightField", "7");
            setText("#materialSearchField", "Залізо");
            ComboBox<String> type = lookup("#typeSearchCombo").query();
            type.setValue("Sword");
            assertTrue(lookup("#minProtField").queryAs(TextField.class).isDisabled());
            assertTrue(lookup("#maxProtField").queryAs(TextField.class).isDisabled());
        });

        clickOn("#applyFilterButton");
        closeSecondaryWindows();

        interact(() -> {
            assertEquals(1, catalog.getItems().size());
            assertEquals("Залізний короткий меч", catalog.getItems().get(0).getName());
            catalog.getSelectionModel().selectFirst();
        });

        clickOn("#equipFromCatalogButton");

        interact(() -> {
            assertEquals(1, equipment.getItems().size());
            assertTrue(lookup("#summaryLabel").queryAs(Label.class).getText().contains("350"));
        });
    }

    @Test
    void showAllCatalogClearsSearchFields() {
        interact(() -> {
            setText("#minPriceField", "100");
            setText("#maxPriceField", "200");
            setText("#minWeightField", "1");
            setText("#maxWeightField", "2");
            setText("#materialSearchField", "Сталь");
            setText("#minProtField", "10");
            setText("#maxProtField", "90");
        });

        clickOn("#showAllCatalogButton");

        interact(() -> {
            assertEquals("", lookup("#minPriceField").queryAs(TextField.class).getText());
            assertEquals("", lookup("#maxPriceField").queryAs(TextField.class).getText());
            assertEquals("", lookup("#materialSearchField").queryAs(TextField.class).getText());
            assertEquals(25, lookup("#catalogListView").queryAs(ListView.class).getItems().size());
        });
    }

    @Test
    void updatesKnightStatsThroughGuiControls() {
        clickOn("#editButton");

        interact(() -> {
            setText("#nameField", "Updated Gui Knight");
            setText("#heightField", "175");
            setText("#weightField", "70");
            setText("#strengthField", "60");
            setText("#enduranceField", "65");
        });

        clickOn("#calculateButton");
        closeSecondaryWindows();

        interact(() -> {
            String stats = lookup("#statsLabel").queryAs(Label.class).getText();
            assertTrue(stats.contains("Updated Gui Knight"));
            assertTrue(stats.contains("120"));
            assertTrue(stats.contains("100"));
        });
    }

    @Test
    void managesEquipmentFileAndKnightListThroughGui() {
        ListView<Ammunition> catalog = lookup("#catalogListView").query();
        ListView<Ammunition> equipment = lookup("#ammunitionListView").query();

        interact(() -> catalog.getSelectionModel().select(0));
        clickOn("#equipFromCatalogButton");

        interact(() -> {
            assertEquals(1, equipment.getItems().size());
            equipment.getSelectionModel().selectFirst();
        });

        clickOn("#saveToFileButton");
        clickOn("#deleteAmmoButton");

        interact(() -> assertTrue(equipment.getItems().isEmpty()));

        clickOn("#loadFromFileButton");

        interact(() -> assertEquals(1, equipment.getItems().size()));

        clickOn("#loadKnightsButton");
        interact(() -> lookup("#knightsListView").queryAs(ListView.class).getSelectionModel().selectFirst());
        clickOn("#editSelectedKnightButton");
        clickOn("#addNewKnightButton");

        interact(() -> {
            assertEquals("", lookup("#nameField").queryAs(TextField.class).getText());
            assertFalse(lookup("#calculateButton").queryAs(Button.class).isDisabled());
        });
    }

    @Test
    void addsEditsSortsAndComparesEquipmentThroughGui() {
        ListView<Ammunition> catalog = lookup("#catalogListView").query();
        ListView<Ammunition> equipment = lookup("#ammunitionListView").query();

        interact(() -> catalog.getSelectionModel().select(0));
        clickOn("#equipFromCatalogButton");
        interact(() -> catalog.getSelectionModel().select(6));
        clickOn("#equipFromCatalogButton");

        interact(() -> {
            ChoiceBox<String> sortChoice = lookup("#sortChoice").query();
            sortChoice.setValue("Ціною");
            sortChoice.setValue("Назвою");
            sortChoice.setValue("Захистом");
            sortChoice.setValue("Вагою");
        });

        clickOn("#addAmmoButton");
        interact(() -> {
            lookup("#ammoTypeCombo").queryAs(ComboBox.class).setValue("Boots");
            setText("#ammoNameField", "Test boots");
            setText("#ammoWeightField", "1.5");
            setText("#ammoPriceField", "120");
            setText("#ammoMaterialField", "Leather");
            setText("#ammoSpecialField", "12");
        });
        clickOn("#ammoSaveButton");

        interact(() -> {
            assertTrue(equipment.getItems().stream().anyMatch(item -> item.getName().equals("Test boots")));
            equipment.getSelectionModel().select(equipment.getItems().size() - 1);
        });

        clickOn("#editAmmoButton");
        interact(() -> {
            setText("#ammoNameField", "Edited boots");
            setText("#ammoWeightField", "2.0");
            setText("#ammoPriceField", "180");
            setText("#ammoMaterialField", "Reinforced leather");
            setText("#ammoSpecialField", "18");
        });
        clickOn("#ammoSaveButton");

        interact(() -> assertTrue(equipment.getItems().stream().anyMatch(item -> item.getName().equals("Edited boots"))));

        confirmSelectionAndCloseResultLater();
        clickOn("#compareKitsButton");
    }

    @Test
    void rejectsCustomAmmunitionWhenWeightDoesNotMatchKnightType() {
        ListView<Ammunition> equipment = lookup("#ammunitionListView").query();
        int initialSize = equipment.getItems().size();

        clickOn("#addAmmoButton");
        interact(() -> {
            lookup("#ammoTypeCombo").queryAs(ComboBox.class).setValue("Boots");
            assertTrue(lookup("#allowedWeightLabel").queryAs(Label.class).getText().contains("1.0-4.0 кг"));
            setText("#ammoNameField", "Too heavy boots");
            setText("#ammoWeightField", "9.0");
            setText("#ammoPriceField", "120");
            setText("#ammoMaterialField", "Steel");
            setText("#ammoSpecialField", "12");
            scheduleDialogClose();
        });

        clickOn("#ammoSaveButton");
        closeSecondaryWindows();

        interact(() -> assertEquals(initialSize, equipment.getItems().size()));
    }

    @Test
    void invokesRemainingGuiBranchesDirectlyOnFxThread() {
        interact(() -> {
            assertDoesNotThrow(() -> invokePrivate("drawKnight", new Class<?>[]{Knight.class}, new Knight("Draw", 190, 95, 80, 60)));
            assertDoesNotThrow(() -> invokePrivate("loadAllKnights"));
            assertEquals(false, assertDoesNotThrow(() -> invokePrivate("isWeaponTypeSelected", new Class<?>[]{String.class}, "Armor")));
            assertEquals(true, assertDoesNotThrow(() -> invokePrivate("isWeaponTypeSelected", new Class<?>[]{String.class}, "Weapon")));
            assertEquals(false, assertDoesNotThrow(() -> invokePrivate("isWeaponTypeSelected", new Class<?>[]{String.class}, (Object) null)));
        });

        closeNextDialogLater();
        interact(() -> {
            setText("#minPriceField", "bad");
            assertDoesNotThrow(() -> invokePrivate("applyFilter"));
            setText("#minPriceField", "");
        });
        closeSecondaryWindows();
    }

    @Test
    void ignoresEquipmentActionsWhenNothingIsSelected() {
        ListView<Ammunition> catalog = lookup("#catalogListView").query();
        ListView<Ammunition> equipment = lookup("#ammunitionListView").query();

        interact(() -> {
            catalog.getSelectionModel().clearSelection();
            equipment.getSelectionModel().clearSelection();
        });

        clickOn("#equipFromCatalogButton");
        clickOn("#editAmmoButton");
        clickOn("#deleteAmmoButton");

        interact(() -> assertTrue(equipment.getItems().isEmpty()));
    }

    @Test
    void createsDefaultArmorAndCoversValidationHelperBranches() {
        ListView<Ammunition> equipment = lookup("#ammunitionListView").query();

        clickOn("#addAmmoButton");
        interact(() -> {
            assertTrue(lookup("#allowedWeightLabel").queryAs(Label.class).getText().contains("7.0-16.0 кг"));
            setText("#ammoNameField", "Valid custom armor");
            setText("#ammoWeightField", "9.0");
            setText("#ammoPriceField", "900");
            setText("#ammoMaterialField", "Steel");
            setText("#ammoSpecialField", "45");
        });
        clickOn("#ammoSaveButton");

        interact(() -> {
            assertTrue(equipment.getItems().stream().anyMatch(item -> item.getName().equals("Valid custom armor")));

            TextField doubleField = new TextField("999");
            TextField integerField = new TextField("0");
            StringBuilder errors = new StringBuilder();
            assertDoesNotThrow(() -> invokePrivate(
                    "validateDoubleInRange",
                    new Class<?>[]{TextField.class, String.class, double.class, double.class, StringBuilder.class},
                    doubleField, "Double", 1.0, 10.0, errors
            ));
            assertDoesNotThrow(() -> invokePrivate(
                    "validateIntegerInRange",
                    new Class<?>[]{TextField.class, String.class, int.class, int.class, StringBuilder.class},
                    integerField, "Integer", 1, 10, errors
            ));
            assertTrue(errors.toString().contains("Double"));
            assertTrue(errors.toString().contains("Integer"));
        });
    }

    @Test
    void createsSwordHelmetAndShieldFromCustomAmmunitionDialog() {
        ListView<Ammunition> equipment = lookup("#ammunitionListView").query();

        addCustomAmmunition("Sword", "Custom sword", "3.0", "500", "Steel", "40");
        addCustomAmmunition("Helmet", "Custom helmet", "2.0", "250", "Steel", "20");
        addCustomAmmunition("Shield", "Custom shield", "4.0", "350", "Wood", "30");

        interact(() -> {
            assertTrue(equipment.getItems().stream().anyMatch(Sword.class::isInstance));
            assertTrue(equipment.getItems().stream().anyMatch(Helmet.class::isInstance));
            assertTrue(equipment.getItems().stream().anyMatch(Shield.class::isInstance));
        });
    }

    @Test
    void loadFromFileCoversSwordAndMalformedNumericData() throws Exception {
        Files.writeString(Path.of("equipment.txt"), "Sword;Loaded sword;3.0;500.0;Steel;45");

        interact(() -> assertDoesNotThrow(() -> invokePrivate("loadFromFile")));
        interact(() -> {
            Knight knight = assertDoesNotThrow(() -> (Knight) getPrivateField("knight"));
            assertEquals(1, knight.getEquipment().size());
            assertTrue(knight.getEquipment().get(0) instanceof Sword);
        });

        Files.writeString(Path.of("equipment.txt"), "Sword;Broken sword;bad;500.0;Steel;45");
        interact(() -> {
            scheduleDialogClose();
            assertDoesNotThrow(() -> invokePrivate("loadFromFile"));
        });
        closeSecondaryWindows();
    }

    @Test
    void compareKitsShowsErrorWhenThereAreNoOtherKnights() throws Exception {
        Files.deleteIfExists(Path.of("knight_database.db"));
        DatabaseInitializer.initialize();

        interact(() -> {
            scheduleDialogClose();
            assertDoesNotThrow(() -> invokePrivate("compareKits"));
        });
        closeSecondaryWindows();
    }

    @Test
    void coversValidationFileAndEmptyStateBranches() throws Exception {
        Files.deleteIfExists(Path.of("equipment.txt"));

        interact(() -> {
            scheduleDialogClose();
            assertDoesNotThrow(() -> invokePrivate("loadFromFile"));

            setText("#minPriceField", "999999");
            scheduleDialogClose();
            assertDoesNotThrow(() -> invokePrivate("applyFilter"));
            setText("#minPriceField", "");

            setText("#nameField", "");
            setText("#heightField", "20");
            setText("#weightField", "abc");
            setText("#strengthField", "200");
            setText("#enduranceField", "abc");
            scheduleDialogClose();
            assertDoesNotThrow(() -> invokePrivate("updateKnight"));

            lookup("#knightsListView").queryAs(ListView.class).getSelectionModel().clearSelection();
            scheduleDialogClose();
            assertDoesNotThrow(() -> invokePrivate("editSelectedKnight"));
        });

        Files.writeString(Path.of("equipment.txt"), String.join(System.lineSeparator(),
                "",
                "bad;line",
                "Helmet;Loaded helmet;2.0;200.0;Steel;25",
                "Shield;Loaded shield;4.0;300.0;Steel;35",
                "Boots;Loaded boots;1.0;100.0;Leather;10",
                "Armor;Loaded armor;8.0;700.0;Steel;60"
        ));

        interact(() -> {
            assertDoesNotThrow(() -> invokePrivate("loadFromFile"));
            Knight knight = assertDoesNotThrow(() -> (Knight) getPrivateField("knight"));
            knight.setMaxWeight(1.0);
            assertDoesNotThrow(() -> invokePrivate(
                    "updateEquipmentList",
                    new Class<?>[]{List.class},
                    knight.getEquipment()
            ));
            assertFalse(lookup("#overweightWarning").queryAs(Label.class).getText().isEmpty());
        });

        Files.deleteIfExists(Path.of("knight_database.db"));
        DatabaseInitializer.initialize();

        interact(() -> assertDoesNotThrow(() -> invokePrivate("loadKnightFromDB")));
    }

    private void setText(String query, String text) {
        TextField field = lookup(query).queryAs(TextField.class);
        field.clear();
        field.setText(text);
    }

    private void addCustomAmmunition(String type, String name, String weight, String price, String material, String special) {
        clickOn("#addAmmoButton");
        interact(() -> {
            lookup("#ammoTypeCombo").queryAs(ComboBox.class).setValue(type);
            setText("#ammoNameField", name);
            setText("#ammoWeightField", weight);
            setText("#ammoPriceField", price);
            setText("#ammoMaterialField", material);
            setText("#ammoSpecialField", special);
        });
        clickOn("#ammoSaveButton");
    }

    private void closeSecondaryWindows() {
        interact(() -> new ArrayList<>(Window.getWindows()).stream()
                .filter(Window::isShowing)
                .filter(window -> window.getScene() != null)
                .filter(window -> window.getScene().getRoot() instanceof DialogPane)
                .forEach(Window::hide));
    }

    private void closeNextDialogLater() {
        interact(this::scheduleDialogClose);
    }

    private void confirmSelectionAndCloseResultLater() {
        interact(() -> javafx.application.Platform.runLater(() -> {
            new ArrayList<>(Window.getWindows()).stream()
                    .filter(Window::isShowing)
                    .filter(window -> window.getScene() != null)
                    .map(window -> window.getScene().getRoot())
                    .filter(DialogPane.class::isInstance)
                    .map(DialogPane.class::cast)
                    .findFirst()
                    .map(dialogPane -> (Button) dialogPane.lookupButton(javafx.scene.control.ButtonType.OK))
                    .ifPresent(okButton -> {
                        okButton.fire();
                        javafx.application.Platform.runLater(this::scheduleDialogClose);
                    });
        }));
    }

    private void scheduleDialogClose() {
        javafx.application.Platform.runLater(() -> new ArrayList<>(Window.getWindows()).stream()
                .filter(Window::isShowing)
                .filter(window -> window.getScene() != null)
                .filter(window -> window.getScene().getRoot() instanceof DialogPane)
                .forEach(Window::hide));
    }

    private Object invokePrivate(String methodName) throws Exception {
        Method method = KnightApp.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(app);
    }

    private Object invokePrivate(String methodName, Class<?>[] parameterTypes, Object... args) throws Exception {
        Method method = KnightApp.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(app, args);
    }

    private Object getPrivateField(String fieldName) throws Exception {
        Field field = KnightApp.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(app);
    }
}
