package com.example.pokedle.data;

import android.util.Log;

import com.example.pokedle.data.Pokemon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PokeApiClient {
    private static final String BASE_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final String TAG = "PokeApiClient";


    public Pokemon getPokemon(String nameOrId) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(BASE_URL + nameOrId.toLowerCase());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            int status = connection.getResponseCode();
            if (status != 200) {
                Log.e(TAG, "Erreur HTTP: " + status);
                return null;
            }

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder response = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return parsePokemon(response.toString());

        } catch (IOException | JSONException e) {
            Log.e(TAG, "Erreur lors de la requête PokéAPI", e);
            return null;
        } finally {
            if (connection != null) connection.disconnect();
            try {
                if (reader != null) reader.close();
            } catch (IOException ignored) {}
        }
    }

    /**
     * Transforme la réponse JSON en objet Pokemon.
     */
    private Pokemon parsePokemon(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);

        int id = obj.getInt("id");
        String name = obj.getString("name");

        JSONObject sprites = obj.getJSONObject("sprites");
        String imageUrl = sprites.getString("front_default");

        JSONObject typesArray = obj.getJSONArray("types").getJSONObject(0).getJSONObject("type");
        String type = typesArray.getString("name");

        return new Pokemon(id, name, type, imageUrl);
    }
}
