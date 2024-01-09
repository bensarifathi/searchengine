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

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JaccardGraphGenerator {
    private static final Logger logger = LogManager.getLogger(WebScraper.class);

    public static void init() throws FileNotFoundException {
        Simplifier removeNonWord = new RemoveNonWordSimplifier();
        Simplifier removeLinkingWords = new RemoveLinkingWordSimplifier();
        Simplifier lowerCase = new LowerCaseSimpliffier();

        Tokenizer whiteSpaceTokenizer = new WhiteSpaceTokenizer();

        String json_path = "data/scrap-results/json/bookIdToName.json";
        json_path = System.getProperty("user.dir") + File.separator + json_path;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HashMap<String, String> bookIdToTitle = gson.fromJson(new FileReader(json_path), HashMap.class);
        HashMap<Integer, Set<String>> idToText = new HashMap<>();

        for (String bookIdString : bookIdToTitle.keySet()) {
            int bookId = Integer.parseInt(bookIdString);
            String text_path = "data/scrap-results/texts/" + bookId + ".txt";
            text_path = System.getProperty("user.dir") + File.separator + text_path;
            File file = new File(text_path);
            if (!file.exists()) {
                logger.error("File " + text_path + " does not exist");
                continue;
            }
            String text = "";
            try {
                text = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(text_path)));
            } catch (Exception e) {
                logger.error("Error while reading file " + text_path);
                continue;
            }

            text = removeNonWord.simplify(removeLinkingWords.simplify(lowerCase.simplify(text)));
            Set<String> words = whiteSpaceTokenizer.tokenizeToSet(text);
            idToText.put(bookId, words);
        }

        Map<String, Map<String, Double>> jaccardGraph = new HashMap<>();

        for (Map.Entry<Integer, Set<String>> entry1 : idToText.entrySet()) {
            int node1 = entry1.getKey();
            Set<String> set1 = entry1.getValue();

            for (Map.Entry<Integer, Set<String>> entry2 : idToText.entrySet()) {
                int node2 = entry2.getKey();
                if (node1 != node2) { // Avoid comparing the same node
                    Set<String> set2 = entry2.getValue();

                    // Calculate Jaccard similarity
                    double jaccardSimilarity = calculateJaccardSimilarity(set1, set2);

                    // Add to the Jaccard graph
                    addToJaccardGraph(jaccardGraph, String.valueOf(node1), String.valueOf(node2), jaccardSimilarity);
                }
            }
        }

        json_path = "data/scrap-results/json/jaccard-graph.json";
        json_path = System.getProperty("user.dir") + File.separator + json_path;
        File file = new File(json_path);
        if (file.exists()) {
            file.delete();
        }
        try (FileWriter writer = new FileWriter(json_path)) {
            // Convert HashMap to JSON and write to file
            gson.toJson(jaccardGraph, writer);
            System.out.println("HashMap saved to file: " + json_path);
        } catch (IOException e) {
            logger.error("Error while saving HashMap to file: " + json_path);
            logger.error(e.getMessage());
        }
    }

    public static Map<String, Map<String, Double>> calculateJaccardGraph(HashMap<Integer, Set<String>> data) {
        Map<String, Map<String, Double>> jaccardGraph = new HashMap<>();

        for (Map.Entry<Integer, Set<String>> entry1 : data.entrySet()) {
            int node1 = entry1.getKey();
            Set<String> set1 = entry1.getValue();

            for (Map.Entry<Integer, Set<String>> entry2 : data.entrySet()) {
                int node2 = entry2.getKey();
                if (node1 != node2) { // Avoid comparing the same node
                    Set<String> set2 = entry2.getValue();

                    // Calculate Jaccard similarity
                    double jaccardSimilarity = calculateJaccardSimilarity(set1, set2);

                    // Add to the Jaccard graph
                    addToJaccardGraph(jaccardGraph, String.valueOf(node1), String.valueOf(node2), jaccardSimilarity);
                }
            }
        }

        return jaccardGraph;
    }

    public static double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    public static void addToJaccardGraph(Map<String, Map<String, Double>> jaccardGraph, String node1, String
            node2, double similarity) {
        jaccardGraph.computeIfAbsent(node1, k -> new HashMap<>()).put(node2, similarity);
        jaccardGraph.computeIfAbsent(node2, k -> new HashMap<>()).put(node1, similarity);
    }

    public static HashMap<String, Map<String, Double>> getCachedGraph() throws FileNotFoundException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json_path = "data/scrap-results/json/jaccard-graph.json";
        json_path = System.getProperty("user.dir") + File.separator + json_path;
        return gson.fromJson(new FileReader(json_path), HashMap.class);
    }
}
