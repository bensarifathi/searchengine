package com.finalprojectdaar.searchengine.models;



public class Book {
    private String author;
    private String category;
    private Integer id;
    private int clicks; // number of clicks on the book
    public int getClicks() {
        return clicks;
    }
    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}