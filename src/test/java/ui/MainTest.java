package ui;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void consoleMainRunsDemoScenario() throws Exception {
        Files.deleteIfExists(Path.of("knight_database.db"));

        assertDoesNotThrow(() -> Main.main(new String[0]));

        assertTrue(Files.exists(Path.of("knight_database.db")));
    }
}
