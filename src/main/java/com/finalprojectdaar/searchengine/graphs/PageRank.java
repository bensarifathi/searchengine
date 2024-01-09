package com.finalprojectdaar.searchengine.graphs;

import java.util.HashMap;
import java.util.Map;

public class PageRank {
    public static Map<String, Double> calculate(Map<String, String[]> graph, double dampingFactor) {
        Map<String, Double> pageRank = new HashMap<>();
        for (String node : graph.keySet()) {
            pageRank.put(node, 1.0);
        }

        // Number of iterations (adjust as needed)
        int iterations = 10;

        for (int i = 0; i < iterations; i++) {
            Map<String, Double> newPageRank = new HashMap<>();

            // Calculate PageRank for each node
            for (String node : graph.keySet()) {
                double sum = 0.0;

                // Iterate over incoming links to the node
                for (String incomingLink : graph.keySet()) {
                    if (containsLink(graph.get(incomingLink), node)) {
                        int outgoingLinksCount = graph.get(incomingLink).length;
                        sum += pageRank.get(incomingLink) / outgoingLinksCount;
                    }
                }

                // Update PageRank for the node
                double updatedPageRank = (1 - dampingFactor) + dampingFactor * sum;
                newPageRank.put(node, updatedPageRank);
            }

            // Update PageRank values for the next iteration
            pageRank = newPageRank;
        }

        return pageRank;
    }

    private static boolean containsLink(String[] links, String target) {
        for (String link : links) {
            if (link.equals(target)) {
                return true;
            }
        }
        return false;
    }
}
