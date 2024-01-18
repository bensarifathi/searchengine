package com.finalprojectdaar.searchengine.text.simplifier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RemoveLinkingWordSimplifier implements Simplifier {
    private static final Set<String> linkingWords = new HashSet<>(List.of(
            "and", "or", "but", "nor", "for", "so", "yet", "after", "although",
            "as", "because", "before", "if", "once", "since", "though", "unless",
            "until", "when", "while", "even", "however", "whereas", "moreover",
            "nevertheless", "nonetheless", "therefore", "meanwhile", "furthermore",
            "consequently", "otherwise", "besides", "thus", "likewise", "hence",
            "accordingly", "additionally", "subsequently", "inasmuch",
            "henceforth", "notwithstanding", "further", "thereafter"
    ));

    @Override
    public String simplify(String text) {
        return String.join(" ", Arrays.stream(text.trim().split("\\s+"))
                .filter(word -> !linkingWords.contains(word))
                .toArray(String[]::new));
    }
}
