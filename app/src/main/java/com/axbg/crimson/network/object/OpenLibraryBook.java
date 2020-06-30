package com.axbg.crimson.network.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OpenLibraryBook implements Serializable {
    String title;
    String author;
    String editionKey;

    public static List<OpenLibraryBook> fromJson(String json) {
        List<OpenLibraryBook> books = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonBooks = jsonObject.getJSONArray("docs");

            for (int i = 0; i < jsonBooks.length(); i++) {
                books.addAll(fromJsonObject(jsonBooks.getJSONObject(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return books;
    }

    public static List<OpenLibraryBook> fromJsonObject(JSONObject jsonObject) {
        String title, author = "";
        JSONArray authorArray;
        List<OpenLibraryBook> bookEditions = new ArrayList<>();

        try {
            title = jsonObject.getString("title_suggest");
            authorArray = jsonObject.getJSONArray("author_name");

            if (authorArray.length() > 0) {
                author = authorArray.getString(0);
            }

            JSONArray editions = jsonObject.getJSONArray("edition_key");
            for (int i = 0; i < editions.length(); i++) {
                bookEditions.add(new OpenLibraryBook(title, author, editions.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bookEditions;
    }
}