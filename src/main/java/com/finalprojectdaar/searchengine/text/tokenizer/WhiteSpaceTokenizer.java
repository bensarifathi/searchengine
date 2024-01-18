package com.finalprojectdaar.searchengine.text.tokenizer;

import java.util.*;
import java.util.regex.Pattern;

public class WhiteSpaceTokenizer implements Tokenizer{
    private final Pattern pattern = Pattern.compile("\\s+");

    @Override
    public List<String> tokenizeToList(String input) {
        if (input.isEmpty()) {
            return Collections.emptyList();
        }

        String[] tokens = pattern.split(input);

        if (tokens.length > 0 && tokens[0].isEmpty()) {
            return Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length));
        }

        return Arrays.asList(tokens);
    }

    @Override
    public Set<String> tokenizeToSet(String input) {
        return input.isEmpty() ? Collections.emptySet() : new HashSet<>(tokenizeToList(input));
    }
}

