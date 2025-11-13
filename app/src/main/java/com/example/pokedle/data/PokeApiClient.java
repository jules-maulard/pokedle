package com.example.pokedle.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PokeApiClient {
    private static final String TAG = "PokeApiClient";
    private static final String BASE_POKEMON_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final String BASE_SPECIES_URL = "https://pokeapi.co/api/v2/pokemon-species/";

    public Pokemon getPokemon(String nameOrId) {
        try {
            JSONObject pokeData = getJsonFromUrl(BASE_POKEMON_URL + nameOrId.toLowerCase());
            if (pokeData == null) return null;

            int id = pokeData.getInt("id");
            int height = pokeData.getInt("height");
            int weight = pokeData.getInt("weight");

            JSONArray typesArray = pokeData.getJSONArray("types");
            PokemonType[] types = new PokemonType[typesArray.length()];
            for (int i = 0; i < typesArray.length(); i++) {
                String typeName = typesArray.getJSONObject(i).getJSONObject("type").getString("name");
                types[i] = PokemonType.fromString(typeName);
            }

            String imageUrl = pokeData.getJSONObject("sprites").getString("front_default");

            // --- 2️⃣ Données de species (nom FR, couleur, habitat, gen, chaîne d’évolution)
            JSONObject speciesData = getJsonFromUrl(BASE_SPECIES_URL + id);
            if (speciesData == null) return null;

            // Nom FR
            String nameFr = getFrenchName(speciesData.getJSONArray("names"));

            // Couleur
            PokemonColor[] colors = new PokemonColor[1];
            colors[0] = PokemonColor.fromString(speciesData.getJSONObject("color").getString("name"));

            // Habitat
            PokemonHabitat habitat = null;
            if (!speciesData.isNull("habitat")) {
                habitat = PokemonHabitat.fromString(speciesData.getJSONObject("habitat").getString("name"));
            }

            // Génération (ex: "generation-iii" → 3)
            int generation = parseGeneration(speciesData.getJSONObject("generation"));

            // --- 3️⃣ Stade d’évolution
            int evolutionState = getEvolutionStage(speciesData.getJSONObject("evolution_chain").getString("url"), nameOrId.toLowerCase());

            // --- 4️⃣ Construction de l’objet final
            return new Pokemon(
                    id,
                    nameFr,
                    types,
                    colors,
                    evolutionState,
                    height,
                    weight,
                    generation,
                    habitat,
                    imageUrl
            );

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la récupération du Pokémon", e);
            return null;
        }
    }

    // ------------------- 🔧 FONCTIONS UTILITAIRES -------------------

    private JSONObject getJsonFromUrl(String urlString) throws IOException, JSONException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            if (connection.getResponseCode() != 200) {
                Log.e(TAG, "Erreur HTTP: " + connection.getResponseCode());
                return null;
            }

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return new JSONObject(response.toString());
        } finally {
            if (connection != null) connection.disconnect();
            if (reader != null) reader.close();
        }
    }

    private String getFrenchName(JSONArray namesArray) throws JSONException {
        for (int i = 0; i < namesArray.length(); i++) {
            JSONObject nameObj = namesArray.getJSONObject(i);
            if (nameObj.getJSONObject("language").getString("name").equals("fr")) {
                return nameObj.getString("name");
            }
        }
        return "Inconnu";
    }

    private int parseGeneration(JSONObject generationObj) {
        try {
            String url = generationObj.getString("url");
            // Exemple : https://pokeapi.co/api/v2/generation/4/
            String[] parts = url.split("/");
            String lastPart = parts[parts.length - 1].isEmpty()
                    ? parts[parts.length - 2]
                    : parts[parts.length - 1];
            return Integer.parseInt(lastPart);
        } catch (Exception e) {
            return 0;
        }
    }


    private int getEvolutionStage(String evoChainUrl, String targetName) {
        try {
            JSONObject evoChain = getJsonFromUrl(evoChainUrl);
            JSONObject chain = evoChain.getJSONObject("chain");
            return findEvolutionStage(chain, targetName.toLowerCase(), 1);
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la récupération du stade d’évolution", e);
            return 1;
        }
    }

    /**
     * Fonction récursive : parcourt l'arbre "evolves_to"
     * et renvoie le niveau (1, 2, 3...) du Pokémon cherché.
     */
    private int findEvolutionStage(JSONObject node, String targetName, int stage) throws JSONException {
        // Récupère le nom du Pokémon de ce nœud
        String speciesName = node.getJSONObject("species").getString("name");

        // Si c’est celui qu’on cherche → on a trouvé son stade
        if (speciesName.equalsIgnoreCase(targetName)) {
            return stage;
        }

        // Sinon, on regarde dans les évolutions suivantes
        JSONArray evolvesTo = node.getJSONArray("evolves_to");
        for (int i = 0; i < evolvesTo.length(); i++) {
            JSONObject nextNode = evolvesTo.getJSONObject(i);
            int result = findEvolutionStage(nextNode, targetName, stage + 1);
            if (result != -1) {
                return result;
            }
        }

        // Pas trouvé dans cette branche
        return -1;
    }
}
