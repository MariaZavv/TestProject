# UI-автотесты HTML-формы

Проект с UI-автотестами для `qa-test.html` на Java, TestNG, Selenide,
Selenium WebDriver и Allure.

## Запуск

Обычный локальный запуск:

```bash
./gradlew test
```

По умолчанию тесты запускаются в видимом Google Chrome. На весь suite
используется одна browser-сессия, а перед каждым тестом заново открывается
локальная HTML-страница.

После прогона отчеты доступны здесь:

```text
build/reports/tests/test/index.html
build/allure-results
build/selenide-reports
```
Если установлен Allure CLI, отчет можно открыть командой:

```bash
allure serve build/allure-results
```

## Структура

```text
src/test/java/ru/protei/qa/test/BaseUiTest.java               базовая инфраструктура UI тестов: настройка Selenide, Allure, браузера и общих шагов
src/test/java/ru/protei/qa/test/AuthTest.java                 тестовые сценарии связанные с авторизацией
src/test/java/ru/protei/qa/test/QuestionnaireTest.java        тестовые сценарии связанные с анкетой
src/test/java/ru/protei/qa/pages/                             page objects
src/test/java/ru/protei/qa/model/                             модели тестовых данных
src/test/java/ru/protei/qa/support/                           конфигурация и вспомогательные классы
src/test/resources/qa-test.html                               тестируемая HTML-страница
```

## Полезные параметры

```bash
./gradlew test -Dbrowser=chrome
./gradlew test -Dbrowser=chrome -Dheadless=false -Dui.pause.ms=300
./gradlew test -Dheadless=true -Dui.pause.ms=0
./gradlew test -Dbrowser=firefox
./gradlew test -Dbrowser=safari -Dheadless=false
./gradlew test -Dapp.html=/Users/mariazavyalova/Downloads/qa-test.html
./gradlew test -Dpage.load.strategy=eager
```

Параметр `ui.pause.ms` задает паузу между UI-шагами. Это удобно для
демонстрационного запуска, когда нужно видеть в браузере, как выполняется тест.

## Логи и Allure

В консоль выводятся понятные события прогона:

```text
SUITE START
TEST START
STEP
TEST PASS
TEST FAIL
SUITE FINISH
```

Действия page object-ов обернуты в Allure steps. В Allure также попадают
Selenide substeps, screenshots и page source при ошибках.

## Тестовые учетные данные

По умолчанию suite использует учетные данные, которые принимает текущий HTML:

```text
E-Mail: test@protei.ru
Password: test
```
