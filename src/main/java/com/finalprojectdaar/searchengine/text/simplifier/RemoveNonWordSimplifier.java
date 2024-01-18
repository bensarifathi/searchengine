package com.finalprojectdaar.searchengine.text.simplifier;

import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveNonWordSimplifier implements Simplifier{
    @Override
    public String simplify(String text) {
        Pattern pattern = Pattern.compile("\\b\\w+\\b");
        Matcher matcher = pattern.matcher(text);

        StringJoiner simplified = new StringJoiner(" ");
        while (matcher.find()) {
            simplified.add(matcher.group());
        }

        return simplified.toString();
    }
}
