package com.example.pokedle.data;

public enum PokemonColor {
    BLACK,
    BLUE,
    BROWN,
    GRAY,
    GREEN,
    PINK,
    PURPLE,
    RED,
    WHITE,
    YELLOW,
    UNKNOWN;

    public static PokemonColor fromString(String colorName) {
        try {
            return PokemonColor.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
