package com.example.calculator.models;

public enum ConversionType {
    LENGTH("Length", new String[]{"Meter", "Kilometer", "Mile", "Foot", "Inch", "Centimeter"}),
    WEIGHT("Weight", new String[]{"Kilogram", "Gram", "Pound", "Ounce", "Ton"}),
    TEMPERATURE("Temperature", new String[]{"Celsius", "Fahrenheit", "Kelvin"}),
    AREA("Area", new String[]{"Square Meter", "Square Kilometer", "Square Mile", "Square Foot", "Acre", "Hectare"}),
    CURRENCY("Currency", new String[]{"USD", "EUR", "GBP", "JPY", "VND", "CNY", "KRW", "AUD"});

    private final String name;
    private final String[] units;

    ConversionType(String name, String[] units) {
        this.name = name;
        this.units = units;
    }

    public String getName() { return name; }
    public String[] getUnits() { return units; }
}