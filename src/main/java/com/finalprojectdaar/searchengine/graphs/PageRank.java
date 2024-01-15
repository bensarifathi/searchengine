package com.finalprojectdaar.searchengine.graphs;

import org.javatuples.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class PageRank {
    public static Set<Pair<String, Double>> calculate(Map<String, Map<String, Double>> graph, double dampingFactor) {
        Map<String, Double> pageRankMap = new HashMap<>();
        // Map<String, Map<String, Double>>
        TreeSet<Pair<String, Double>> orderedClosenessCentrality = new TreeSet<>(new TupleComparator());

        for (String node : graph.keySet()) {
            pageRankMap.put(node, 1.0);
        }

        // Number of iterations
        int iterations = 10;

        for (int i = 0; i < iterations; i++) {
            Map<String, Double> newPageRank = new HashMap<>();

            // Calculate PageRank for each node
            for (String node : graph.keySet()) {
                double sum = 0.0;

                // Iterate over incoming links to the node
                for (String incomingLink : graph.keySet()) {
                    if (graph.get(incomingLink).containsKey(node)) {
                        int outgoingLinksCount = graph.get(incomingLink).size();
                        sum += pageRankMap.get(incomingLink) / outgoingLinksCount;
                    }
                }

                // Update PageRank for the node
                double updatedPageRank = (1 - dampingFactor) + dampingFactor * sum;
                newPageRank.put(node, updatedPageRank);
            }

            // Update PageRank values for the next iteration
            pageRankMap = newPageRank;
        }

        TreeSet<Pair<String, Double>> sortedPageRankSet =
                new TreeSet<>(new TupleComparator()); // for descending order

        for (Map.Entry<String, Double> entry : pageRankMap.entrySet()) {
            Pair<String, Double> pair = new Pair<>(entry.getKey(), entry.getValue());
            sortedPageRankSet.add(pair);
        }


        return sortedPageRankSet;
    }
}
