package com.finalprojectdaar.searchengine.text.jaccard;

import com.finalprojectdaar.searchengine.scraper.WebScraper;
import com.finalprojectdaar.searchengine.text.simplifier.LowerCaseSimpliffier;
import com.finalprojectdaar.searchengine.text.simplifier.RemoveLinkingWordSimplifier;
import com.finalprojectdaar.searchengine.text.simplifier.RemoveNonWordSimplifier;
import com.finalprojectdaar.searchengine.text.simplifier.Simplifier;
import com.finalprojectdaar.searchengine.text.tokenizer.Tokenizer;
import com.finalprojectdaar.searchengine.text.tokenizer.WhiteSpaceTokenizer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JaccardGraphGeneratorPair {
    private static final Logger logger = LogManager.getLogger(WebScraper.class);
    protected final Simplifier removeNonWord = new RemoveNonWordSimplifier();
    protected final Simplifier removeLinkingWords = new RemoveLinkingWordSimplifier();
    protected final Simplifier lowerCase = new LowerCaseSimpliffier();
    protected final Tokenizer whiteSpaceTokenizer = new WhiteSpaceTokenizer();

    private Set<String> getTokenizedText(int bookId) {
        String text_path = "data/scrap-results/texts/" + bookId + ".txt";
        text_path = System.getProperty("user.dir") + File.separator + text_path;
        File file = new File(text_path);
        if (!file.exists()) {
            logger.error("File " + text_path + " does not exist");
            return null;
        }

        String text;
        try {
            text = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(text_path)));
        } catch (Exception e) {
            logger.error("Error while reading file " + text_path);
            return null;
        }

        text = removeNonWord.simplify(removeLinkingWords.simplify(lowerCase.simplify(text)));
        return whiteSpaceTokenizer.tokenizeToSet(text);
    }

    public Map<Pair<String, String>, Double> calculateJaccard(Map<Integer, Set<String>> idToWords) {
        long startTime = System.nanoTime();
        System.out.println("Started Jaccard graph generation");

        Map<Pair<String, String>, Double> jaccardGraph = new ConcurrentHashMap<>();

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        List<Integer> nodeIds = new ArrayList<>(idToWords.keySet());

        for (int i = 0; i < nodeIds.size(); i++) {
            int node1 = nodeIds.get(i);
            Set<String> set1 = idToWords.get(node1);

            for (int j = i + 1; j < nodeIds.size(); j++) {
                int node2 = nodeIds.get(j);
                Set<String> set2 = idToWords.get(node2);

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    double jaccardSimilarity;
                    if (jaccardGraph.containsKey(new Pair<>(String.valueOf(node2), String.valueOf(node1)))) {
                        jaccardSimilarity = jaccardGraph.get(new Pair<>(String.valueOf(node2), String.valueOf(node1)));
                    } else {
                        jaccardSimilarity = calculateJaccardDistance(set1, set2);
                    }
                    Pair<String, String> key = new Pair<>(String.valueOf(node1), String.valueOf(node2));
                    jaccardGraph.put(key, jaccardSimilarity);
                }, executor);

                futures.add(future);
            }
        }

        // Wait for all CompletableFuture to complete
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // Handle completion
        allOf.join();

        executor.shutdown();

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("Jaccard graph generated in " + duration + " ms");

        return jaccardGraph;
    }

    public Map<Pair<String, String>, Double> initForAllNodes() throws FileNotFoundException {
        String jsonPath = "data/scrap-results/json/bookIdToName.json";
        jsonPath = System.getProperty("user.dir") + File.separator + jsonPath;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HashMap<String, String> bookIdToTitle = gson.fromJson(new FileReader(jsonPath), HashMap.class);
        HashMap<Integer, Set<String>> idToText = new HashMap<>();

        for (String bookIdString : bookIdToTitle.keySet()) {
            int bookId = Integer.parseInt(bookIdString);
            Set<String> words = getTokenizedText(bookId);
            idToText.put(bookId, words);
        }

        return calculateJaccard(idToText);
    }

    public Map<Pair<String, String>, Double> initForBookList(List<Integer> bookIds) {
        System.out.println("Started jackard graph generation");
        // timing
        long startTime = System.nanoTime();

        ConcurrentMap<Integer, Set<String>> idToText = bookIds.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        Integer.class::cast,
                        this::getTokenizedText,
                        (Set<String> existingSet, Set<String> newSet) -> {
                            existingSet.addAll(newSet);
                            return existingSet;
                        },
                        ConcurrentHashMap::new
                ));

        // end timinh
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // divide by 1000000 to get milliseconds.
        logger.info("Jaccard graph generated in " + duration + " ms");

        return calculateJaccard(new HashMap<>(idToText));
    }

    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    private double calculateJaccardDistance(Set<String> set1, Set<String> set2) {
        return 1.0 - calculateJaccardSimilarity(set1, set2);
    }


    static void addToJaccardGraph(Map<String, Map<String, Double>> jaccardGraph, String node1, String
            node2, double similarity) {
        jaccardGraph.computeIfAbsent(node1, k -> new HashMap<>()).put(node2, similarity);
        jaccardGraph.computeIfAbsent(node2, k -> new HashMap<>()).put(node1, similarity);
    }
}
