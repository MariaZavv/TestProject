package ru.protei.qa.test;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import ru.protei.qa.pages.AuthPage;
import ru.protei.qa.pages.QuestionnairePage;
import ru.protei.qa.support.LocalHtmlSupport;
import ru.protei.qa.support.TestConfig;
import ru.protei.qa.support.TestRunLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

@Listeners({AllureTestNg.class, TestRunLogger.class})
public abstract class BaseUiTest {
    private static final String ALLURE_SELENIDE_LISTENER = "AllureSelenide";

    protected final AuthPage authPage = new AuthPage();

    static {
        configureSeleniumLogging();
        registerAllureSelenideListener();
    }

    @BeforeSuite(alwaysRun = true)
    public void registerSelenideAllureListener() {
        configureSeleniumLogging();
        registerAllureSelenideListener();
    }

    private static void registerAllureSelenideListener() {
        if (!SelenideLogger.hasListener(ALLURE_SELENIDE_LISTENER)) {
            SelenideLogger.addListener(ALLURE_SELENIDE_LISTENER, new AllureSelenide()
                    .screenshots(true)
                    .savePageSource(true)
                    .includeSelenideSteps(true));
        }
    }

    private static void configureSeleniumLogging() {
        disableJulLogger("org.openqa.selenium.devtools.CdpVersionFinder");
        disableJulLogger("org.openqa.selenium.chromium.ChromiumDriver");
    }

    private static void disableJulLogger(String loggerName) {
        Logger logger = Logger.getLogger(loggerName);
        logger.setLevel(Level.SEVERE);
        logger.setUseParentHandlers(false);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        TestConfig.configureSelenide();
        open("http://localhost:8080/qa-test.html");
        $("#authPage").should(exist);
    }

    @AfterSuite(alwaysRun = true)
    public void tearDown() {
        closeWebDriver();
    }

    protected QuestionnairePage loginAndOpenQuestionnaire() {
        return authPage.loginSuccessfully(TestConfig.authEmail(), TestConfig.authPassword());
    }
}
