package com.finalprojectdaar.searchengine.text.simplifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveNonWordSimplifier implements Simplifier{
    @Override
    public String simplify(String text) {
        Pattern pattern = Pattern.compile("\\b\\w+\\b");
        Matcher matcher = pattern.matcher(text);

        StringBuilder simplified = new StringBuilder();
        // Use a StringBuilder to build the simplified string
        while (matcher.find()) {
            simplified.append(matcher.group()).append(" ");
        }

        // Trim the trailing space and return the simplified string
        return simplified.toString().trim();
    }
}
