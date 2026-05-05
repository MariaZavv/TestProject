package ru.protei.qa.model;

import java.util.List;

public record QuestionnaireData(
        String email,
        String name,
        String gender,
        List<String> checkboxValues,
        String radioValue
) {
    public List<String> expectedTableValues() {
        String checkboxCellValue = checkboxValues.isEmpty()
                ? "Нет"
                : String.join(", ", checkboxValues);

        return List.of(email, name, gender, checkboxCellValue, radioValue);
    }
}
