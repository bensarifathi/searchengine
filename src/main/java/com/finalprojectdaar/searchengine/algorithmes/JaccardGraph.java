package com.finalprojectdaar.searchengine.algorithmes;

import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class JaccardGraph {
    private ArrayList<Integer> vertices;

    private Map<Integer, ArrayList<Integer>> graph = new HashMap<>();
    private Map<Integer, Set<String>> tokenizedVertices = new HashMap<>();
    private final float threshold = 0.05F;

    public JaccardGraph(ArrayList<Integer> vertices) {
        this.vertices = vertices;
        instantiateGraph();
    }

    public Map<Integer, ArrayList<Integer>> getGraph() {
        return graph;
    }

    private Set<String> getTokenizedText(Integer id) throws IOException {
        Set<String> tokenizedText = tokenizedVertices.get(id);
        if (tokenizedText == null) {
            // cache the vertex that is already tokenize
            tokenizedText = tokenize(id);
            tokenizedVertices.put(id, tokenizedText);
        }
        return tokenizedText;
    }

    private void instantiateGraph() {
        for(int v: vertices) {
            graph.put(v, new ArrayList<>());
        }
    }

    private String readFileFromResources(Integer id) throws IOException {
        String fileName = "books/" + id + ".txt";
        return new ClassPathResource(fileName).getContentAsString(StandardCharsets.UTF_8);
    }

    private Set<String> tokenize(Integer id) throws IOException {
        String text = readFileFromResources(id);
        String[] linkingWords = {"and", "or", "but", "nor", "for", "so", "yet", "after", "although",
                "as", "because", "before", "if", "once", "since", "though", "unless",
                "until", "when", "while", "even", "however", "whereas", "moreover",
                "nevertheless", "nonetheless", "therefore", "meanwhile", "furthermore",
                "consequently", "otherwise", "besides", "thus", "likewise", "hence",
                "accordingly", "otherwise", "additionally", "subsequently", "inasmuch",
                "henceforth", "nevertheless", "notwithstanding", "further", "thereafter"
        };
        Set<String> textSet = new HashSet<>();
        Set<String> keywordSet = new HashSet<>(Arrays.asList(linkingWords));
        for (String word: text.split(" ")) {
            // convert to lower case
            word = word.toLowerCase();
            // remove non alphanumeric value
            word = word.replaceAll("[^a-zA-Z0-9]", "");
            // check if the word in not in linkingWords
            if (!keywordSet.contains(word))
                textSet.add(word);
        }
        return textSet;
    }

    public JaccardGraph buildJaccardGraph() throws IOException {
        for (int u: graph.keySet()) {
            for (int v: graph.keySet()) {
                if (u < v) {
                    Set<String> uSet = getTokenizedText(u);
                    Set<String> vSet = getTokenizedText(v);
                    Set<String> uInterV = new HashSet<>(uSet);
                    // keep only the common words
                    uInterV.retainAll(vSet);
                    float intersection = uInterV.size();
                    // now get the union
                    vSet.addAll(uSet);
                    float union = vSet.size();
                    float jaccardSimilarity = intersection / union;
                    // check if the similarity of u and v is > threshold and add a link between them
                    if (jaccardSimilarity >= threshold) {
                        graph.get(u).add(v);
                        graph.get(v).add(u);
                    }
                }
            }
        }
        return this;
    }

}
