package ru.netology.tests;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;
import ru.netology.data.DataGenerator.UserInfo;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

class DeliveryCardTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    private void fillField(String selector, String value) {
        SelenideElement field = $(selector + " input");
        field.sendKeys(Keys.CONTROL + "a"); // выделяем всё содержимое
        field.sendKeys(value);              // печатаем поверх — заменяет выделенное
    }

    private void fillForm(UserInfo user, String date) {
        fillField("[data-test-id=city]", user.getCity());
        fillField("[data-test-id=date]", date);
        $("[data-test-id=date] input").pressEscape(); // закрыть выпавший календарь
        fillField("[data-test-id=name]", user.getName());
        fillField("[data-test-id=phone]", user.getPhone());

        // кликаем по чекбоксу только если он ещё не отмечен,
        // иначе повторный клик его снимет
        SelenideElement checkbox = $("[data-test-id=agreement] input");
        if (!checkbox.isSelected()) {
            $("[data-test-id=agreement]").click();
        }

        $$(".button").find(exactText("Запланировать")).click();
    }

    private SelenideElement successContent() {
        return $("[data-test-id=success-notification] .notification__content");
    }

    private SelenideElement replanContent() {
        return $("[data-test-id=replan-notification] .notification__content");
    }

    private void checkSuccess(String date) {
        // "Успешно!" живёт в заголовке, а не в контенте
        $("[data-test-id=success-notification] .notification__title")
                .shouldHave(text("Успешно!"), TIMEOUT);
        successContent()
                .shouldHave(text("Встреча успешно запланирована на " + date), TIMEOUT);
    }

    @Test
    void shouldReplanMeetingWithSameUser() {
        UserInfo user = DataGenerator.generateUser();
        String firstDate = DataGenerator.generateDate(4);
        String secondDate = DataGenerator.generateDate(7);

        fillForm(user, firstDate);
        checkSuccess(firstDate);

        // открываем форму заново — маска поля даты не всегда
        // принимает замену значения, а на чистой форме проблемы нет
        open("http://localhost:9999");

        // заполняем повторно теми же данными, но с другой датой
        fillForm(user, secondDate);
        replanContent().shouldBe(visible, TIMEOUT)
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"), TIMEOUT);

        $("[data-test-id=replan-notification] button").click();
        checkSuccess(secondDate);
    }
}
