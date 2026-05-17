package ui;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void consoleMainRunsDemoScenario() throws Exception {
        Files.deleteIfExists(Path.of("knight_database.db"));

        assertDoesNotThrow(Main::new);
        assertDoesNotThrow(Launcher::new);
        assertDoesNotThrow(() -> Main.main(new String[0]));

        assertTrue(Files.exists(Path.of("knight_database.db")));
    }

    @Test
    void launcherVerifiesDatabaseAndDelegatesToApplicationLauncher() throws Exception {
        Files.deleteIfExists(Path.of("knight_database.db"));
        AtomicBoolean launched = new AtomicBoolean(false);

        assertDoesNotThrow(() -> Launcher.run(new String[]{"arg"}, args -> {
            assertTrue(args.length == 1);
            launched.set(true);
        }));

        assertTrue(launched.get());
        assertTrue(Files.exists(Path.of("knight_database.db")));
    }

    @Test
    void launcherHandlesApplicationLauncherFailure() {
        assertDoesNotThrow(() -> Launcher.run(new String[0], args -> {
            throw new RuntimeException("launch failed");
        }));
    }
}
