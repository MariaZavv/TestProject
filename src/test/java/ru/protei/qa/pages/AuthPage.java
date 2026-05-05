package ru.protei.qa.pages;

import static ru.protei.qa.support.TestStep.call;
import static ru.protei.qa.support.TestStep.run;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class AuthPage {
    private static final String EMAIL_FORMAT_ERROR = "Неверный формат E-MAIL";
    private static final String INVALID_CREDENTIALS_ERROR = "Неверный E-MAIL или пароль";

    public AuthPage shouldBeOpened() {
        return call("Проверить, что открыта форма авторизации", () -> {
            $("#authPage").shouldBe(visible);
            return this;
        });
    }

    public AuthPage shouldQuestionnaireBeHidden() {
        return call("Проверить, что анкета скрыта", () -> {
            $("#inputsPage").shouldBe(hidden);
            return this;
        });
    }

    public AuthPage shouldShowEmailFormatError() {
        return call("Проверить ошибку формата E-MAIL", () -> {
            $("#authAlertsHolder").shouldHave(exactText(EMAIL_FORMAT_ERROR));
            return this;
        });
    }

    public AuthPage shouldShowInvalidCredentialsError() {
        return call("Проверить ошибку неверных учетных данных", () -> {
            $("#authAlertsHolder").shouldHave(exactText(INVALID_CREDENTIALS_ERROR));
            return this;
        });
    }

    public AuthPage login(String email, String password) {
        run("Ввести E-Mail для авторизации: " + email, () -> $("#loginEmail").setValue(email));
        run("Ввести пароль для авторизации", () -> $("#loginPassword").setValue(password));
        run("Нажать кнопку входа", () -> $("#authButton").click());
        return this;
    }

    public QuestionnairePage loginSuccessfully(String email, String password) {
        login(email, password);
        return new QuestionnairePage().shouldBeOpened();
    }
}
