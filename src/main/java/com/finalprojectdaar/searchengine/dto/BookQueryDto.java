package com.finalprojectdaar.searchengine.dto;

public class BookQueryDto {
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setRegex(boolean regex) {
        isRegex = regex;
    }

    private String pattern;

    public String getPattern() {
        return pattern;
    }

    public boolean isRegex() {
        return isRegex;
    }

    private boolean isRegex;


}
