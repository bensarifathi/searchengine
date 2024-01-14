package com.finalprojectdaar.searchengine.dto;


public class BookQueryDto {

    private String pattern;
    private boolean isRegex;


    public void setRegex(boolean regex) {
        isRegex = regex;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }


    public boolean isRegex() {
        return isRegex;
    }



}
