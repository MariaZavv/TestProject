package ru.protei.qa.pages;

import ru.protei.qa.model.QuestionnaireData;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static org.testng.Assert.assertEquals;
import static ru.protei.qa.support.TestStep.call;
import static ru.protei.qa.support.TestStep.run;

public class QuestionnairePage {
    private static final String EMAIL_FORMAT_ERROR = "Неверный формат E-Mail";
    private static final String BLANK_NAME_ERROR = "Поле имя не может быть пустым";

    public QuestionnairePage shouldBeOpened() {
        return call("Проверить, что открыта анкета", () -> {
            $("#inputsPage").shouldBe(visible);
            return this;
        });
    }

    public QuestionnairePage shouldAuthPageBeHidden() {
        return call("Проверить, что форма авторизации скрыта", () -> {
            $("#authPage").shouldBe(hidden);
            return this;
        });
    }

    public QuestionnairePage shouldShowEmailFormatError() {
        return call("Проверить ошибку формата E-Mail в анкете", () -> {
            $("#dataAlertsHolder").shouldHave(exactText(EMAIL_FORMAT_ERROR));
            return this;
        });
    }

    public QuestionnairePage shouldShowBlankNameError() {
        return call("Проверить ошибку пустого имени", () -> {
            $("#dataAlertsHolder").shouldHave(exactText(BLANK_NAME_ERROR));
            return this;
        });
    }

    public QuestionnairePage shouldHaveRowsCount(int expectedCount) {
        return call("Проверить количество строк в таблице: " + expectedCount, () -> {
            $$("#dataTable tbody tr").shouldHave(size(expectedCount));
            return this;
        });
    }

    public QuestionnairePage shouldHaveRow(int rowIndex, List<String> expectedValues) {
        return call("Проверить значения строки таблицы #" + rowIndex, () -> {
            assertEquals(rowValues(rowIndex), expectedValues, "Unexpected table row values");
            return this;
        });
    }

    public QuestionnairePage submitEmailAndName(String email, String name) {
        run("Ввести E-Mail в анкету: " + email, () -> $("#dataEmail").setValue(email));
        run("Ввести имя в анкету: " + displayValue(name), () -> $("#dataName").setValue(name));
        run("Отправить анкету", () -> $("#dataSend").click());
        return this;
    }

    public QuestionnairePage submit(QuestionnaireData data) {
        run("Ввести E-Mail в анкету: " + data.email(), () -> $("#dataEmail").setValue(data.email()));
        run("Ввести имя в анкету: " + displayValue(data.name()), () -> $("#dataName").setValue(data.name()));
        run("Выбрать пол: " + data.gender(), () -> $("#dataGender").selectOption(data.gender()));
        data.checkboxValues().forEach(this::selectCheckbox);
        selectRadio(data.radioValue());
        run("Отправить анкету", () -> $("#dataSend").click());
        return this;
    }

    public QuestionnairePage shouldCloseSuccessModal() {
        return call("Закрыть модальное окно успеха", () -> {

            $x("//button[normalize-space()='Ok']")
                    .shouldBe(visible, Duration.ofSeconds(2))
                    .click();

            return this;
        });
    }

    private void selectCheckbox(String value) {
        run("Выбрать чекбокс: " + value, () -> {
            switch (value) {
                case "1.1" -> $("#dataCheck11").setSelected(true);
                case "1.2" -> $("#dataCheck12").setSelected(true);
                default -> throw new IllegalArgumentException("Unsupported checkbox value: " + value);
            }
        });
    }

    private void selectRadio(String value) {
        run("Выбрать радиокнопку: " + value, () -> {
            switch (value) {
                case "2.1" -> $("#dataSelect21").click();
                case "2.2" -> $("#dataSelect22").click();
                case "2.3" -> $("#dataSelect23").click();
                default -> throw new IllegalArgumentException("Unsupported radio value: " + value);
            }
        });
    }

    private List<String> rowValues(int rowIndex) {
        return $$("#dataTable tbody tr")
                .get(rowIndex)
                .$$("td")
                .texts();
    }

    private static String displayValue(String value) {
        return value == null || value.isBlank() ? "<empty>" : value;
    }
}
