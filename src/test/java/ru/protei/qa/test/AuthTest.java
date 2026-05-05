package ru.protei.qa.test;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.protei.qa.pages.AuthPage;
import ru.protei.qa.support.TestConfig;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class AuthTest extends BaseUiTest {

    private record AuthCase(String email,
                    String password,
                    Consumer<AuthPage> assertion) {}

    @Test(description = "На старте показана форма авторизации")
    public void shouldShowAuthFormOnStart() {
        authPage.shouldBeOpened();
        authPage.shouldQuestionnaireBeHidden();
    }

    @Test(description = "Авторизация отклоняет неверный формат E-Mail")
    public void shouldShowErrorForInvalidEmailFormat() {
        authPage
                .login("wrong-email", TestConfig.authPassword())
                .shouldShowEmailFormatError()
                .shouldBeOpened()
                .shouldQuestionnaireBeHidden();
    }

    @Test(dataProvider = "blankAuthFields",
            description = "Авторизация не допускает пустые поля")
    public void shouldValidateEmptyAuthFields(AuthCase authCase) {
        AuthPage page = authPage.login(authCase.email, authCase.password);
        authCase.assertion.accept(page);
        page.shouldBeOpened()
                .shouldQuestionnaireBeHidden();
    }

    @Test(description = "Авторизация отклоняет неверные учетные данные")
    public void shouldShowErrorForInvalidCredentials() {
        authPage
                .login("wrong@example.com", "wrong-password")
                .shouldShowInvalidCredentialsError()
                .shouldBeOpened()
                .shouldQuestionnaireBeHidden();
    }

    @Test(description = "Авторизация с валидными учетными данными открывает анкету")
    public void shouldOpenQuestionnaireWithValidCredentials() {
        loginAndOpenQuestionnaire()
                .shouldBeOpened()
                .shouldAuthPageBeHidden();
    }

    @DataProvider
    public Iterator<AuthCase> blankAuthFields() {
        return List.of(
                        new AuthTest.AuthCase("", "password",
                                page -> page.shouldShowEmailFormatError()),
                        new AuthTest.AuthCase("user@example.com", "",
                                page -> page.shouldShowInvalidCredentialsError()),
                        new AuthTest.AuthCase("", "",
                                page -> page.shouldShowEmailFormatError()))
                .iterator();
    }
}
