package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LoggerService {
    private static final Logger logger = LoggerFactory.getLogger(LoggerService.class);
    private static final Properties config = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            config.load(fis);
        } catch (IOException e) {
        }
    }

    private static String getProperty(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    private static final String SMTP_HOST = getProperty("mail.smtp.host", "smtp.gmail.com");
    private static final String SMTP_PORT = getProperty("mail.smtp.port", "587");
    private static final String EMAIL_FROM = getProperty("mail.from", "roksolianakachala@gmail.com");
    private static final String EMAIL_TO = getProperty("mail.to", "roksolianakachala@gmail.com");
    private static final String EMAIL_PASSWORD = getProperty("mail.password", "iuta pqsg csik pzbt");

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logWarning(String message) {
        logger.warn(message);
    }

    public static void logError(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    public static void logCriticalError(String message, Throwable throwable) {
        logger.error("CRITICAL ERROR: " + message, throwable);
        sendEmailNotification("CRITICAL ERROR: " + message, throwable);
    }

    private static void sendEmailNotification(String subject, Throwable throwable) {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", SMTP_HOST);
        prop.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_TO));
            message.setSubject(subject);

            String content = "An error occurred:\n" + throwable.toString() + "\n\nStack trace:\n";
            for (StackTraceElement element : throwable.getStackTrace()) {
                content += element.toString() + "\n";
            }
            message.setText(content);
            logger.info("Attempting to send email notification about critical error...");
            logger.info("Email notification simulated successfully.");

        } catch (MessagingException e) {
            logger.error("Failed to send email notification", e);
        }
    }
}
