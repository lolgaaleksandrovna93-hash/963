package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {

    private DataGenerator() {
    }

    private static final List<String> CITIES = Arrays.asList(
            "Казань", "Москва", "Санкт-Петербург", "Новосибирск",
            "Екатеринбург", "Самара", "Краснодар", "Ростов-на-Дону");

    public static String generateDate(int shift) {
        return LocalDate.now()
                .plusDays(shift)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String generateCity() {
        return CITIES.get(new Random().nextInt(CITIES.size()));
    }

    @Value
    public static class UserInfo {
        String city;
        String name;
        String phone;
    }

    public static UserInfo generateUser() {
        Faker ru = new Faker(new Locale("ru"));
        return new UserInfo(
                generateCity(),
                ru.name().lastName() + " " + ru.name().firstName(),
                "+7" + ru.numerify("##########"));
    }
}
