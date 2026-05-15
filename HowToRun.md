# Як запустити проект (How to run the project)

Ви бачите помилку `mvn : The term 'mvn' is not recognized`, тому що Maven не додано до змінних середовища (PATH) вашої системи. 

Оскільки ви використовуєте IntelliJ IDEA, найпростіший спосіб запустити проект — використовувати вбудовані інструменти IDE, які не потребують налаштування терміналу.

### Варіант 1: Через інтерфейс IntelliJ IDEA (найпростіший)
1. Відкрийте файл `src/main/java/ui/Launcher.java`.
2. Знайдіть рядок `public static void main(String[] args)`.
3. Натисніть на **зелений трикутник** (Run) зліва від назви класу або методу `main`.
4. Виберіть **Run 'Launcher.main()'**.

### Варіант 2: Через панель Maven в IntelliJ IDEA
1. З правого боку вікна IntelliJ IDEA знайдіть вкладку **Maven**.
2. Розгорніть список: `Knight` -> `Plugins` -> `javafx` -> `javafx:run`.
3. Двічі клацніть на **javafx:run**.

### Варіант 3: Виправлення помилки в терміналі (якщо ви хочете використовувати `mvn`)

Щоб команда `mvn` працювала в терміналі Windows, виконайте ці кроки:

1.  **Перевірте чи встановлено Maven:** Якщо ні, завантажте "Binary zip archive" з [сайту Maven](https://maven.apache.org/download.cgi) та розпакуйте його (наприклад, у `C:\apache-maven`).
2.  **Налаштуйте змінні середовища:**
    *   Натисніть `Win + R`, введіть `sysdm.cpl` і натисніть Enter.
    *   Перейдіть на вкладку **Додатково (Advanced)** -> **Змінні середовища (Environment Variables)**.
    *   У розділі "Системні змінні" знайдіть **Path**, виберіть його та натисніть **Змінити (Edit)**.
    *   Натисніть **Створити (New)** і додайте повний шлях до папки `bin` вашого Maven (наприклад, `C:\apache-maven\bin`).
    *   Натисніть OK у всіх вікнах.
3.  **Перезапустіть термінал:** Закрийте термінал в IntelliJ IDEA і відкрийте його знову. Тепер команда `mvn -version` має працювати.

### Варіант 4: Використання Maven Wrapper (без встановлення)
Я додав у проект файли `mvnw` та `mvnw.cmd`. Вони дозволяють запускати Maven без його глобального встановлення в систему.
*   У терміналі Windows (PowerShell/CMD) замість `mvn` пишіть `.\mvnw`:
    ```powershell
    .\mvnw javafx:run
    ```

---

# How to run the project (English)

You are seeing the `mvn : The term 'mvn' is not recognized` error because Maven is not added to your system's PATH.

Since you are using IntelliJ IDEA, the easiest way to run the project is to use the IDE's built-in tools.

### Option 1: Via IntelliJ IDEA interface (Recommended)
1. Open the file `src/main/java/ui/Launcher.java`.
2. Find the line `public static void main(String[] args)`.
3. Click the **green triangle** (Run icon) to the left of the class name or the `main` method.
4. Select **Run 'Launcher.main()'**.

### Option 2: Via the Maven panel in IntelliJ IDEA
1. On the right side of the IntelliJ IDEA window, find the **Maven** tab.
2. Expand the list: `Knight` -> `Plugins` -> `javafx` -> `javafx:run`.
3. Double-click **javafx:run**.

### Option 3: Fixing the terminal error
To make the `mvn` command work in the terminal, you need to install Maven and add its `bin` folder to your system's **PATH** environment variable.

1.  **Download Maven:** If you haven't, download the "Binary zip archive" from the [official Maven website](https://maven.apache.org/download.cgi).
2.  **Extract:** Unzip it to a folder (e.g., `C:\apache-maven`).
3.  **Environment Variables:**
    *   Press `Win + R`, type `sysdm.cpl`, and press Enter.
    *   Go to **Advanced** -> **Environment Variables**.
    *   Under "System variables", find **Path**, select it, and click **Edit**.
    *   Click **New** and add the path to the `bin` folder (e.g., `C:\apache-maven\bin`).
    *   Click OK.
4.  **Restart Terminal:** Restart your IntelliJ terminal or the IDE itself. Now `mvn -version` should work.

### Option 4: Use Maven Wrapper (No installation required)
I have added `mvnw` and `mvnw.cmd` to the project. You can run Maven commands without installing Maven globally.
*   In the terminal, use `.\mvnw` instead of `mvn`:
    ```powershell
    .\mvnw javafx:run
    ```
