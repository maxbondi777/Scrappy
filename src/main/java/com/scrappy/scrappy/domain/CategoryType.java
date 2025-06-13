package com.scrappy.scrappy.domain;

public enum CategoryType {
    ALL("Все"),
    WORK("Работа"),
    PERSONAL("Личное"),
    GENERAL("Общее"),
    IDEAS("Идеи"),
    TRAVEL("Путешествия"),
    HEALTH("Здоровье"),
    FINANCE("Финансы"),
    EDUCATION("Обучение"),
    SHOPPING("Покупки"),
    FAMILY("Семья"),
    HOBBY("Хобби"),
    EVENTS("Мероприятия"),
    TECHNOLOGY("Технологии"),
    RECIPES("Рецепты");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}