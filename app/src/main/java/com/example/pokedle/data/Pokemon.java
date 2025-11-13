package com.example.pokedle.data;

public class Pokemon {
    private int id;
    private String name;
    private PokemonType[] type;
    private PokemonColor[] color;
    private int evolutionState;
    private int height;
    private int weight;
    private int gen;
    private PokemonHabitat habitat;
    private String imageUrl;

    public Pokemon(int id, String name, PokemonType[] type, PokemonColor[] color,
                   int evolutionState, int height, int weight, int gen,
                   PokemonHabitat habitat, String imageUrl) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.color = color;
        this.evolutionState = evolutionState;
        this.height = height;
        this.weight = weight;
        this.gen = gen;
        this.habitat = habitat;
        this.imageUrl = imageUrl;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PokemonType[] getType() {
        return type;
    }

    public PokemonColor[] getColor() {
        return color;
    }

    public int getEvolutionState() {
        return evolutionState;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public PokemonHabitat getHabitat() {
        return habitat;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
