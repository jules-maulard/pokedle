package com.example.pokedle.data;

public enum PokemonHabitat {
    CAVE,
    FOREST,
    GRASSLAND,
    MOUNTAIN,
    RARE,
    ROUGH_TERRAIN,
    SEA,
    URBAN,
    WATERS_EDGE,
    UNKNOWN;

    public static PokemonHabitat fromString(String habitatName) {
        if (habitatName == null) return UNKNOWN;

        try {
            // la PokéAPI renvoie parfois "waters-edge" → on le convertit proprement
            return PokemonHabitat.valueOf(habitatName.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
