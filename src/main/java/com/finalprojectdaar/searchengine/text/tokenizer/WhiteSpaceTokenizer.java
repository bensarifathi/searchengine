package com.finalprojectdaar.searchengine.text.tokenizer;

import java.util.*;
import java.util.regex.Pattern;

public class WhiteSpaceTokenizer implements Tokenizer{
    private final Pattern pattern = Pattern.compile("\\s+");

    @Override
    public List<String> tokenizeToList(String input) {
        if(input.isEmpty()){
            return new ArrayList<>();
        }

        String[] tokens = pattern.split(input);

        if (tokens.length > 0 && tokens[0].isEmpty()) {
            tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        }

        return Arrays.asList(tokens);
    }

    @Override
    public Set<String> tokenizeToSet(String input) {
        return new HashSet<>(this.tokenizeToList(input));
    }

}

