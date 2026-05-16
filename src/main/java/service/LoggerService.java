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
            logger.info("Конфігурація завантажена з config.properties");
        } catch (IOException e) {
            logger.warn("Не вдалося завантажити config.properties, використовуються значення за замовчуванням");
        }
    }

    private static String getProperty(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }

    private static final String SMTP_HOST = getProperty("mail.smtp.host", "smtp.gmail.com");
    private static final String SMTP_PORT = getProperty("mail.smtp.port", "587");
    private static final String EMAIL_FROM = getProperty("mail.from", "your-email@gmail.com");
    private static final String EMAIL_TO = getProperty("mail.to", "admin@example.com");
    private static final String EMAIL_PASSWORD = getProperty("mail.password", "");
    private static final boolean EMAIL_ENABLED = Boolean.parseBoolean(getProperty("mail.enabled", "false"));

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
        if (!EMAIL_ENABLED) {
            logger.info("Email сповіщення вимкнено в конфігурації. Встановіть mail.enabled=true для активації.");
            return;
        }

        if (EMAIL_PASSWORD.isEmpty()) {
            logger.warn("Email пароль не налаштовано. Неможливо відправити сповіщення.");
            return;
        }

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", SMTP_HOST);
        prop.put("mail.smtp.port", SMTP_PORT);
        prop.put("mail.smtp.ssl.trust", SMTP_HOST);

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
            message.setSubject("[Knight App] " + subject);

            StringBuilder content = new StringBuilder();
            content.append("КРИТИЧНА ПОМИЛКА в додатку Knight\n\n");
            content.append("Час: ").append(java.time.LocalDateTime.now()).append("\n\n");
            content.append("Помилка: ").append(throwable.toString()).append("\n\n");
            content.append("Stack trace:\n");
            for (StackTraceElement element : throwable.getStackTrace()) {
                content.append("  ").append(element.toString()).append("\n");
            }
            
            message.setText(content.toString());
            
            logger.info("Спроба відправити email сповіщення про критичну помилку...");
            Transport.send(message);
            logger.info("Email сповіщення успішно відправлено на " + EMAIL_TO);

        } catch (MessagingException e) {
            logger.error("Не вдалося відправити email сповіщення", e);
        }
    }
}
