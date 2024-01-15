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
import java.util.*;

public class JaccardGraphGenerator {
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

        String text = "";
        try {
            text = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(text_path)));
        } catch (Exception e) {
            logger.error("Error while reading file " + text_path);
            return null;
        }

        text = removeNonWord.simplify(removeLinkingWords.simplify(lowerCase.simplify(text)));
        return whiteSpaceTokenizer.tokenizeToSet(text);
    }

    public Map<String, Map<String, Double>> calculateJackard(HashMap<Integer, Set<String>> idToWords) {
        Map<String, Map<String, Double>> jaccardGraph = new HashMap<>();

        for (Map.Entry<Integer, Set<String>> entry1 : idToWords.entrySet()) {
            int node1 = entry1.getKey();
            Set<String> set1 = entry1.getValue();

            for (Map.Entry<Integer, Set<String>> entry2 : idToWords.entrySet()) {
                int node2 = entry2.getKey();
                if (node1 != node2) { // Avoid comparing the same node
                    Set<String> set2 = entry2.getValue();

                    // Calculate Jaccard similarity
                    double jaccardSimilarity = calculateJaccardDistance(set1, set2);

                    // Add to the Jaccard graph
                    addToJaccardGraph(jaccardGraph, String.valueOf(node1), String.valueOf(node2), jaccardSimilarity);
                }
            }
        }
        return jaccardGraph;
    }

    public Map<String, Map<String, Double>> initForAllNodes() throws FileNotFoundException {
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

        return calculateJackard(idToText);
    }

    public Map<String, Map<String, Double>> initForBookList(List<Integer> bookIds) {
        HashMap<Integer, Set<String>> idToText = new HashMap<>();

        for (int bookId : bookIds) {
            Set<String> words = getTokenizedText(bookId);
            idToText.put(bookId, words);
        }

        return calculateJackard(idToText);
    }

    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    private double calculateJaccardDistance(Set<String> set1, Set<String> set2){
        return 1.0 - calculateJaccardSimilarity(set1, set2);
    }



    static void addToJaccardGraph(Map<String, Map<String, Double>> jaccardGraph, String node1, String
            node2, double similarity) {
        jaccardGraph.computeIfAbsent(node1, k -> new HashMap<>()).put(node2, similarity);
        jaccardGraph.computeIfAbsent(node2, k -> new HashMap<>()).put(node1, similarity);
    }
}
