package com.finalprojectdaar.searchengine.graphs;

import java.util.HashMap;
import java.util.Map;

public class ClosenessCentrality {
    public static Map<String, Double> calculateClosenessCentrality(Map<String, Map<String, Double>> graph) {
        Map<String, Double> closenessCentrality = new HashMap<>();

        for (String node : graph.keySet()) {
            double sumDistances = 0.0;
            int reachableNodes = 0;

            for (String otherNode : graph.keySet()) {
                if (!node.equals(otherNode)) {
                    double distance = graph.get(node).getOrDefault(otherNode, Double.MAX_VALUE);
                    if (distance != Double.MAX_VALUE) {
                        sumDistances += distance;
                        reachableNodes++;
                    }
                }
            }

            if (reachableNodes > 0) {
                double closeness = (reachableNodes - 1) / sumDistances;  // Subtract 1 to exclude the node itself
                closenessCentrality.put(node, closeness);
            } else {
                closenessCentrality.put(node, 0.0);  // Set closeness to 0 for isolated nodes
            }
        }

        return closenessCentrality;
    }
}
