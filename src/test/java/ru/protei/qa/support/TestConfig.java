package ru.protei.qa.support;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;

public final class TestConfig {
    private static final String DEFAULT_AUTH_EMAIL = "test@protei.ru";
    private static final String DEFAULT_AUTH_PASSWORD = "test";
    private static final String DEFAULT_HTML_PATH = "src/test/resources/qa-test.html";
    private static final String DEFAULT_BROWSER = "chrome";
    private static final String DEFAULT_BROWSER_SIZE = "1280x900";
    private static final long DEFAULT_UI_PAUSE_MS = 150;

    private TestConfig() {
    }

    public static void configureSelenide() {
        Configuration.browser = System.getProperty("browser", DEFAULT_BROWSER);
        Configuration.headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        Configuration.browserSize = System.getProperty("browser.size", DEFAULT_BROWSER_SIZE);
        Configuration.timeout = 8000;
        Configuration.pageLoadStrategy = System.getProperty("page.load.strategy", "none");
        Configuration.screenshots = true;
        Configuration.savePageSource = true;
        Configuration.reportsFolder = System.getProperty("selenide.reportsFolder", "build/selenide-reports");

        configureBrowserBinary();
        configureChromeOptions();
    }

    public static String authEmail() {
        return System.getProperty("auth.email", DEFAULT_AUTH_EMAIL);
    }

    public static String authPassword() {
        return System.getProperty("auth.password", DEFAULT_AUTH_PASSWORD);
    }

    public static long uiPauseMs() {
        String configuredPause = System.getProperty("ui.pause.ms");
        if (configuredPause != null && !configuredPause.isBlank()) {
            return Long.parseLong(configuredPause);
        }

        return Configuration.headless ? 0 : DEFAULT_UI_PAUSE_MS;
    }

    public static void pauseForUiDemo() {
        long pauseMs = uiPauseMs();
        if (pauseMs <= 0) {
            return;
        }

        try {
            Thread.sleep(pauseMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting between UI steps", exception);
        }
    }

    private static void configureBrowserBinary() {
        String browserBinary = System.getProperty("browser.binary", System.getenv("CHROME_BIN"));
        if (browserBinary != null && !browserBinary.isBlank()) {
            Configuration.browserBinary = browserBinary;
        }
    }

    private static void configureChromeOptions() {
        String chromeArgs = System.getProperty("chrome.args");
        if (!isChromeBrowser() || chromeArgs == null || chromeArgs.isBlank()) {
            return;
        }

        ChromeOptions options = new ChromeOptions();
        Arrays.stream(chromeArgs.split(","))
                .map(String::trim)
                .filter(argument -> !argument.isBlank())
                .forEach(options::addArguments);
        Configuration.browserCapabilities = options;
    }

    private static boolean isChromeBrowser() {
        return Configuration.browser.toLowerCase(Locale.ROOT).contains("chrome");
    }
}
