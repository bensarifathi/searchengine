package com.finalprojectdaar.searchengine.text.tokenizer;

import java.util.List;
import java.util.Set;

public interface Tokenizer {
    public List<String> tokenizeToList(String input);
    public Set<String> tokenizeToSet(String input);

}
