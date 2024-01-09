package com.finalprojectdaar.searchengine.text.simplifier;

public class LowerCaseSimpliffier implements Simplifier {
    @Override
    public String simplify(String text) {
        return text.toLowerCase();
    }
}
