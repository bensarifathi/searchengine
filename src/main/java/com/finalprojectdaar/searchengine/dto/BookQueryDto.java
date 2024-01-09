package com.finalprojectdaar.searchengine.dto;

import lombok.Getter;
import lombok.Setter;

public class BookQueryDto {

    public void setRegex(boolean regex) {
        isRegex = regex;
    }

    @Getter
    @Setter
    private String pattern;

    public boolean isRegex() {
        return isRegex;
    }

    private boolean isRegex;


}
