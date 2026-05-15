package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import model.*;

public class KnightApp extends Application {

    private Knight knight = new Knight("Arthur");

    private TextField nameField = new TextField("Arthur");
    private TextField heightField = new TextField("180");
    private TextField weightField = new TextField("80");
    private TextField strengthField = new TextField("50");
    private TextField enduranceField = new TextField("50");

    private Label statsLabel = new Label();

    private Pane knightPane = new Pane();

    @Override
    public void start(Stage stage) {
        VBox inputBox = new VBox(8);

        inputBox.getChildren().addAll(
                new Label("Knight parameters"),
                new Label("Name:"), nameField,
                new Label("Height:"), heightField,
                new Label("Weight:"), weightField,
                new Label("Strength:"), strengthField,
                new Label("Endurance:"), enduranceField
        );

        Button calculateButton = new Button("Calculate and update knight");

        calculateButton.setOnAction(e -> updateKnight());

        knightPane.setPrefSize(300, 400);

        HBox root = new HBox(30);
        root.setStyle("-fx-padding: 20; -fx-font-size: 14;");
        root.getChildren().addAll(inputBox, knightPane, statsLabel, calculateButton);

        updateKnight();

        Scene scene = new Scene(root, 900, 500);
        stage.setTitle("Interactive Knight");
        stage.setScene(scene);
        stage.show();
    }

    private void updateKnight() {
        StringBuilder errors = new StringBuilder();

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            errors.append("- Name cannot be empty.\n");
        }

        Double height = validateDoubleInRange(heightField, "Height", 50.0, 250.0, errors);
        Double weight = validateDoubleInRange(weightField, "Weight", 30.0, 300.0, errors);
        Integer strength = validateIntegerInRange(strengthField, "Strength", 1, 100, errors);
        Integer endurance = validateIntegerInRange(enduranceField, "Endurance", 1, 100, errors);

        if (errors.length() > 0) {
            showInputErrors(errors.toString());
            return;
        }

        int attack = strength * 2;
        int defense = endurance + (int) (weight / 2);
        int speed = Math.max(10, 100 - (int) (double) weight);

        drawKnight(height, weight, strength);

        statsLabel.setText(
                "Name: " + name +
                        "\nAttack: " + attack +
                        "\nDefense: " + defense +
                        "\nSpeed: " + speed +
                        "\nBody type: " + getBodyType(height, weight)
        );
    }

    private Double validateDoubleInRange(TextField field, String fieldName, double min, double max, StringBuilder errors) {
        String text = field.getText().trim();
        try {
            double value = Double.parseDouble(text);
            if (value < min || value > max) {
                errors.append("- ").append(fieldName).append(" must be from ").append(min).append(" to ").append(max).append(".\n");
                return null;
            }
            return value;
        } catch (NumberFormatException exception) {
            errors.append("- Incorrect input in field \"").append(fieldName).append("\". Please enter a number.\n");
            return null;
        }
    }

    private Integer validateIntegerInRange(TextField field, String fieldName, int min, int max, StringBuilder errors) {
        String text = field.getText().trim();
        try {
            int value = Integer.parseInt(text);
            if (value < min || value > max) {
                errors.append("- ").append(fieldName).append(" must be from ").append(min).append(" to ").append(max).append(".\n");
                return null;
            }
            return value;
        } catch (NumberFormatException exception) {
            errors.append("- Incorrect input in field \"").append(fieldName).append("\". Please enter an integer number.\n");
            return null;
        }
    }

    private void showInputErrors(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Incorrect input");
        alert.setHeaderText("Please correct the following errors:");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    private void drawKnight(double height, double weight, int strength) {
        knightPane.getChildren().clear();

        double bodyHeight = height / 2;
        double bodyWidth = weight / 2;

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

        Rectangle sword = new Rectangle(220, 120, 8, 60 + strength / 2);
        Rectangle shield = new Rectangle(65, 145, 35, 55);

        knightPane.getChildren().addAll(
                head, body, leftLeg, rightLeg, leftArm, rightArm, sword, shield
        );
    }

    private String getBodyType(double height, double weight) {
        double bmi = weight / Math.pow(height / 100, 2);

        if (bmi < 18.5) {
            return "Thin knight";
        } else if (bmi < 25) {
            return "Normal knight";
        } else {
            return "Strong / heavy knight";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}