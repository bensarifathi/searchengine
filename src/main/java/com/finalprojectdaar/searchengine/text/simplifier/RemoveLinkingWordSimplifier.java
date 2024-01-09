package com.finalprojectdaar.searchengine.text.simplifier;

public class RemoveLinkingWordSimplifier implements Simplifier {
    @Override
    public String simplify(String text) {
        String[] linkingWords = {"and", "or", "but", "nor", "for", "so", "yet", "after", "although",
                "as", "because", "before", "if", "once", "since", "though", "unless",
                "until", "when", "while", "even", "however", "whereas", "moreover",
                "nevertheless", "nonetheless", "therefore", "meanwhile", "furthermore",
                "consequently", "otherwise", "besides", "thus", "likewise", "hence",
                "accordingly", "otherwise", "additionally", "subsequently", "inasmuch",
                "henceforth", "nevertheless", "notwithstanding", "further", "thereafter"
        };
        String[] words = text.split(" ");
        StringBuilder simplified = new StringBuilder();
        for (String word : words) {
            boolean isLinkingWord = false;
            for (String linkingWord : linkingWords) {
                if (word.equals(linkingWord)) {
                    isLinkingWord = true;
                    break;
                }
            }
            if (!isLinkingWord) {
                simplified.append(word).append(" ");
            }
        }
        return simplified.toString().trim();
    }
}
