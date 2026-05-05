package ru.protei.qa.test;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.protei.qa.model.QuestionnaireData;
import ru.protei.qa.pages.QuestionnairePage;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class QuestionnaireTest extends BaseUiTest {

    private static final QuestionnaireData FULL_FORM = new QuestionnaireData(
            "person@example.com",
            "Мария",
            "Женский",
            List.of("1.1", "1.2"),
            "2.2"
    );
    private static final QuestionnaireData WITHOUT_CHECKBOXES = new QuestionnaireData(
            "second@example.com",
            "Петр",
            "Мужской",
            List.of(),
            "2.3"
    );

    private record QuestionnaireCase(
            String email,
            String name,
            Consumer<QuestionnairePage> assertion
    ) {}

    @Test(description = "Анкета проверяет формат E-Mail")
    public void shouldValidateEmailFormat() {
        loginAndOpenQuestionnaire()
                .submitEmailAndName("wrong-email", "Иван")
                .shouldShowEmailFormatError()
                .shouldHaveRowsCount(0);
    }

    @Test(description = "Анкета требует заполнить имя")
    public void shouldRequireName() {
        loginAndOpenQuestionnaire()
                .submitEmailAndName("person@example.com", "")
                .shouldShowBlankNameError()
                .shouldHaveRowsCount(0);
    }

    @Test(description = "Анкета добавляет заполненные данные в таблицу")
    public void shouldAddRowAfterSubmit() {
        loginAndOpenQuestionnaire()
                .submit(FULL_FORM)
                .shouldHaveRowsCount(1)
                .shouldHaveRow(0, FULL_FORM.expectedTableValues());
    }

    @Test(description = "Анкета добавляет дубликатные записи в таблицу")
    public void shouldAddDuplicateRowsAfterMultipleSubmits() {
        QuestionnairePage questionnaire = loginAndOpenQuestionnaire();
        questionnaire
                .submit(FULL_FORM)
                .shouldCloseSuccessModal()
                .submit(FULL_FORM)
                .shouldHaveRowsCount(2)
                .shouldHaveRow(0, FULL_FORM.expectedTableValues())
                .shouldHaveRow(1, FULL_FORM.expectedTableValues());
    }

    @Test(description = "Анкета пишет Нет, если чекбоксы не выбраны")
    public void shouldMarkNoWhenNoCheckboxesSelected() {
        loginAndOpenQuestionnaire()
                .submit(WITHOUT_CHECKBOXES)
                .shouldHaveRowsCount(1)
                .shouldHaveRow(0, WITHOUT_CHECKBOXES.expectedTableValues());
    }

    @Test(dataProvider = "blankQuestionnaireFields",
            description = "Анкета не допускает пустые обязательные поля")
    public void shouldValidateRequiredQuestionnaireFields(QuestionnaireCase testCase) {
        QuestionnairePage page = loginAndOpenQuestionnaire()
                .submitEmailAndName(testCase.email(), testCase.name());
        testCase.assertion().accept(page);
        page.shouldHaveRowsCount(0);
    }

    @DataProvider
    public Iterator<QuestionnaireCase> blankQuestionnaireFields() {
        return List.of(
                new QuestionnaireCase("", "Иван",
                        page -> page.shouldShowEmailFormatError()),
                new QuestionnaireCase("", "",
                        page -> page.shouldShowEmailFormatError()))
                .iterator();
    }
}
