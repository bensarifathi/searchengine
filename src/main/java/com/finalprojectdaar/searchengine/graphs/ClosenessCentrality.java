package com.finalprojectdaar.searchengine.graphs;

import org.javatuples.Pair;
import org.javatuples.Tuple;

import java.util.*;

public class ClosenessCentrality {
    public static Set<Pair<String, Double>> calculateClosenessCentrality(Map<String, Map<String, Double>> graph) {
        // use Set for default order
        // data is tuple where (bookId, Rank)

        TreeSet<Pair<String, Double>> orderedClosenessCentrality = new TreeSet<>(new TupleComparator());

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
                Pair<String, Double> tuple = new Pair<>(node, closeness);
                orderedClosenessCentrality.add(tuple);
            } else {
                Pair<String, Double> tuple = new Pair<>(node, 0.0);
                orderedClosenessCentrality.add(tuple);  // Set closeness to 0 for isolated nodes
            }
        }

        return orderedClosenessCentrality;
    }
}
