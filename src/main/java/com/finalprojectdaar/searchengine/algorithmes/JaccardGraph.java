package com.finalprojectdaar.searchengine.algorithmes;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class JaccardGraph {
    private ArrayList<Integer> vertices;

    private ConcurrentHashMap<Integer, ArrayList<Integer>> graph = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Set<String>> tokenizedVertices = new ConcurrentHashMap<>();
    private final float threshold = 0.05F;

    public JaccardGraph(ArrayList<Integer> vertices) {
        this.vertices = vertices;
        instantiateGraph();
    }

    public Map<Integer, ArrayList<Integer>> getGraph() {
        return graph;
    }

    private Set<String> getTokenizedText(Integer id) {
        Set<String> tokenizedText = tokenizedVertices.get(id);
        if (tokenizedText == null) {
            // cache the vertex that is already tokenize
            try {

                tokenizedText = tokenize(id);
                tokenizedVertices.put(id, tokenizedText);
            } catch (IOException e) {
                return new HashSet<>();
            }

        }
        return tokenizedText;
    }

    private void instantiateGraph() {
        for (int v : vertices) {
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
        for (String word : text.split(" ")) {
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

    public JaccardGraph buildJaccardGraph() {
        ConcurrentMap<Integer, Set<String>> idToTokens = graph.keySet().parallelStream()
                .collect(Collectors.toConcurrentMap(
                        Integer.class::cast,
                        this::getTokenizedText,
                        (Set<String> existingSet, Set<String> newSet) -> {
                            existingSet.addAll(newSet);
                            return existingSet;
                        },
                        ConcurrentHashMap::new
                ));

        int numThreads = Runtime.getRuntime().availableProcessors(); // You can adjust this based on your needs
        ExecutorService executor = Executors.newFixedThreadPool(1);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        idToTokens.forEach((u, uSet) -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                idToTokens.forEach((v, vSet) -> {
                    if(u > v) return ;

                    Set<String> uInterV = new HashSet<>(vSet);
                    Set<String> uUnionV = new HashSet<>(vSet);

                    // keep only the common words
                    uInterV.retainAll(uSet);
                    float intersection = uInterV.size();
                    // now get the union
                    uUnionV.addAll(uSet);
                    float union = uUnionV.size();
                    float jaccardSimilarity = intersection / union;
                    // check if the similarity of u and v is > threshold and add a link between them
                    if (jaccardSimilarity >= threshold) {
                        graph.get(u).add(v);
                        graph.get(v).add(u);
                    }
                });
            }, executor);

            futures.add(future);
        });

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();
        executor.shutdown();

        return this;
    }
}
