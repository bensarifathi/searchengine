package com.finalprojectdaar.searchengine.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public record Book(String id, String title, String author) {
    private static final String BASE_LOCATION = System.getProperty("user.dir") + File.separator + "data/scrap-results/json/books/";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Book fromId(int id) throws IOException {
        String path = BASE_LOCATION + id + ".json";
        FileInputStream fis = new FileInputStream(path);
        byte[] buffer = new byte[10];
        StringBuilder sb = new StringBuilder();
        while (fis.read(buffer) != -1) {
            sb.append(new String(buffer));
            buffer = new byte[10];
        }
        fis.close();

        String content = sb.toString();        // read book json
        HashMap<String,String> bookFromJson = gson.fromJson(content, HashMap.class);
        return new Book(String.valueOf(id), (String) bookFromJson.get("title"), (String) bookFromJson.get("author"));
    }

    public static Book fromId(String id) {
        // read book json
        HashMap bookFromJson = gson.fromJson(BASE_LOCATION + id + ".json", HashMap.class);
        return new Book(id, (String) bookFromJson.get("title"), (String) bookFromJson.get("author"));
    }
}
