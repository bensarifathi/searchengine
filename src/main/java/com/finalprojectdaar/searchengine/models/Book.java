package com.finalprojectdaar.searchengine.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

public record Book(String id, String title, String author) {
    private static final String BASE_LOCATION = "data/scrap-results/json/books";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Book fromId(int id) {
        // read book json
        HashMap bookFromJson = gson.fromJson(BASE_LOCATION + id + ".json", HashMap.class);
        return new Book(String.valueOf(id), (String) bookFromJson.get("title"), (String) bookFromJson.get("author"));
    }

    public static Book fromId(String id) {
        // read book json
        HashMap bookFromJson = gson.fromJson(BASE_LOCATION + id + ".json", HashMap.class);
        return new Book(id, (String) bookFromJson.get("title"), (String) bookFromJson.get("author"));
    }
}
