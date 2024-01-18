package com.finalprojectdaar.searchengine.graphs;

import org.javatuples.Pair;
import org.javatuples.Tuple;

import java.util.*;

public class ClosenessCentrality {
    public static Set<Pair<String, Double>> calculateClosenessCentrality( Map<Pair<String, String>, Double> graph) {
        TreeSet<Pair<String, Double>> orderedClosenessCentrality = new TreeSet<>(new TupleComparator());

        Set<String> allNodes = getAllNodes(graph);

        for (String node : allNodes) {
            double sumDistances = 0.0;
            int reachableNodes = 0;

            for (String otherNode : allNodes) {
                if (!node.equals(otherNode)) {
                    Pair<String, String> edge = new Pair<>(node, otherNode);
                    double distance = graph.getOrDefault(edge, Double.MAX_VALUE);

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

    private static Set<String> getAllNodes(Map<Pair<String, String>, Double> graph) {
        Set<String> nodes = new HashSet<>();

        for (Pair<String, String> edge : graph.keySet()) {
            nodes.add(edge.getValue0());
            nodes.add(edge.getValue1());
        }

        return nodes;
    }

}
