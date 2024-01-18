package com.finalprojectdaar.searchengine.graphs;

import org.javatuples.Pair;

import java.util.*;

public class PageRank {
    public static Set<Pair<String, Double>> calculate(Map<Pair<String, String>, Double> graph, double dampingFactor) {
        Map<String, Double> pageRankMap = new HashMap<>();
        TreeSet<Pair<String, Double>> sortedPageRankSet = new TreeSet<>(new TupleComparator());

        Set<String> allNodes = getAllNodes(graph);

        for (String node : allNodes) {
            pageRankMap.put(node, 1.0);
        }

        // Number of iterations
        int iterations = 10;

        for (int i = 0; i < iterations; i++) {
            Map<String, Double> newPageRank = new HashMap<>();

            // Calculate PageRank for each node
            for (String node : allNodes) {
                double sum = 0.0;

                // Iterate over incoming links to the node
                for (String incomingLink : allNodes) {
                    Pair<String, String> edge = new Pair<>(incomingLink, node);
                    if (graph.containsKey(edge)) {
                        int outgoingLinksCount = countOutgoingLinks(graph, incomingLink);
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

        for (Map.Entry<String, Double> entry : pageRankMap.entrySet()) {
            Pair<String, Double> pair = new Pair<>(entry.getKey(), entry.getValue());
            sortedPageRankSet.add(pair);
        }

        return sortedPageRankSet;
    }

    private static Set<String> getAllNodes(Map<Pair<String, String>, Double> graph) {
        Set<String> nodes = new HashSet<>();

        for (Pair<String, String> edge : graph.keySet()) {
            nodes.add(edge.getValue0());
            nodes.add(edge.getValue1());
        }

        return nodes;
    }

    private static int countOutgoingLinks(Map<Pair<String, String>, Double> graph, String node) {
        return (int) graph.keySet().stream().filter(edge -> edge.getValue0().equals(node)).count();
    }
}
