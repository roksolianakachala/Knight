package service;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Authenticator;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerServiceTest {

    @Test
    void loggingMethodsDoNotThrowWithDefaultConfiguration() {
        assertDoesNotThrow(() -> LoggerService.logInfo("info"));
        assertDoesNotThrow(() -> LoggerService.logWarning("warning"));
        assertDoesNotThrow(() -> LoggerService.logError("error", new RuntimeException("boom")));
        assertDoesNotThrow(() -> LoggerService.logCriticalError("critical", new RuntimeException("boom")));
    }

    @Test
    void createsMailPropertiesFromConfigurationDefaults() {
        Properties properties = LoggerService.createMailProperties();

        assertEquals("true", properties.getProperty("mail.smtp.auth"));
        assertEquals("true", properties.getProperty("mail.smtp.starttls.enable"));
        assertEquals("smtp.gmail.com", properties.getProperty("mail.smtp.host"));
        assertEquals("587", properties.getProperty("mail.smtp.port"));
        assertEquals("smtp.gmail.com", properties.getProperty("mail.smtp.ssl.trust"));
    }

    @Test
    void buildsCriticalEmailContentWithThrowableAndStackTrace() {
        RuntimeException exception = new RuntimeException("boom");

        String content = LoggerService.buildEmailContent(exception);

        assertTrue(content.contains("КРИТИЧНА ПОМИЛКА"));
        assertTrue(content.contains("java.lang.RuntimeException: boom"));
        assertTrue(content.contains("Stack trace:"));
        assertTrue(content.contains("buildsCriticalEmailContentWithThrowableAndStackTrace"));
    }

    @Test
    void describesWhyEmailNotificationShouldBeSkipped() {
        assertTrue(LoggerService.getEmailSkipReason(false, "secret").contains("вимкнено"));
        assertTrue(LoggerService.getEmailSkipReason(true, "").contains("пароль"));
        assertNull(LoggerService.getEmailSkipReason(true, "secret"));
    }

    @Test
    void createsEmailMessageWithoutSendingIt() throws Exception {
        Session session = Session.getInstance(LoggerService.createMailProperties());

        Message message = LoggerService.createEmailMessage(session, "Subject", new RuntimeException("boom"));

        assertEquals("[Knight App] Subject", message.getSubject());
        assertTrue(message.getFrom()[0].toString().contains("@"));
        assertTrue(message.getRecipients(Message.RecipientType.TO)[0].toString().contains("@"));
        assertTrue(message.getContent().toString().contains("boom"));
    }

    @Test
    void createsMailAuthenticatorForSmtpSession() {
        Authenticator authenticator = LoggerService.createMailAuthenticator();
        Session session = Session.getInstance(LoggerService.createMailProperties(), authenticator);

        assertTrue(authenticator.getClass().getName().contains("LoggerService"));
        assertEquals("smtp.gmail.com", session.getProperty("mail.smtp.host"));
    }
}
