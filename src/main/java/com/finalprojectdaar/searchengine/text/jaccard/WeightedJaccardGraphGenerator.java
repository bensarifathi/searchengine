package com.finalprojectdaar.searchengine.text.jaccard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WeightedJaccardGraphGenerator extends JaccardGraphGenerator {
    public Map<String, Map<String, Double>> calculateWieghtedJaccard(HashMap<Integer, Set<String>> idToWords, String searchedText) {
        searchedText = removeNonWord.simplify(removeLinkingWords.simplify(lowerCase.simplify(searchedText)));

        Set<String> powerWords = whiteSpaceTokenizer.tokenizeToSet(searchedText);
        Map<String, Map<String, Double>> jaccardGraph = new HashMap<>();

        for (Map.Entry<Integer, Set<String>> entry1 : idToWords.entrySet()) {
            int node1 = entry1.getKey();
            Set<String> set1 = entry1.getValue();

            for (Map.Entry<Integer, Set<String>> entry2 : idToWords.entrySet()) {
                int node2 = entry2.getKey();
                if (node1 != node2) { // Avoid comparing the same node
                    Set<String> set2 = entry2.getValue();

                    // Calculate Jaccard similarity
                    double jaccardSimilarity = calculateWeightedJaccardDistance(set1, set2, powerWords);

                    // Add to the Jaccard graph
                    addToJaccardGraph(jaccardGraph, String.valueOf(node1), String.valueOf(node2), jaccardSimilarity);
                }
            }
        }
        return jaccardGraph;
    }

    static double calculateWeightedJaccardDistance(Set<String> set1, Set<String> set2, Set<String> searchedText) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        double weightedIntersectionSum = intersection.stream().mapToDouble((w) -> searchedText.contains(w) ? 2 : 1).sum();
        double weightedUnionSum = union.stream().mapToDouble((w) -> searchedText.contains(w) ? 2 : 1).sum();


        // Avoid division by zero
        if (weightedUnionSum == 0.0) {
            return 0.0;
        }

        return 1.0 - (weightedIntersectionSum / weightedUnionSum);
    }

}
