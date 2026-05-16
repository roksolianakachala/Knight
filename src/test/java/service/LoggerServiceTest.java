package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LoggerServiceTest {

    @Test
    void loggingMethodsDoNotThrowWithDefaultConfiguration() {
        assertDoesNotThrow(() -> LoggerService.logInfo("info"));
        assertDoesNotThrow(() -> LoggerService.logWarning("warning"));
        assertDoesNotThrow(() -> LoggerService.logError("error", new RuntimeException("boom")));
        assertDoesNotThrow(() -> LoggerService.logCriticalError("critical", new RuntimeException("boom")));
    }
}
