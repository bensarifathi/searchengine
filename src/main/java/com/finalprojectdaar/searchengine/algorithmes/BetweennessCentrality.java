package com.finalprojectdaar.searchengine.algorithmes;

import java.util.*;

public class BetweennessCentrality {
    private Map<Integer, List<Integer>> graph = new HashMap<>();

    public BetweennessCentrality(Map<Integer, List<Integer>> graph) {
        this.graph = graph;
    }

    public ArrayList<Integer> getOrderedNodes() {
        Map<Integer, Float> betweenness = calculateBetweennessCentrality();
        return sortNodesDescending(betweenness);
    }


    private Map<Integer, Float> calculateBetweennessCentrality() {
            Queue<Integer> queue = new LinkedList<>();
            Stack<Integer> stack = new Stack<>();
            Map<Integer, Integer> sigma = new HashMap<>();
            Map<Integer, Integer> distance = new HashMap<>();
            Map<Integer, List<Integer>> pred = new HashMap<>();
            Map<Integer, Integer> delta = new HashMap<>();
            Map<Integer, Float> betweenness = new HashMap<>();

        for (int u: graph.keySet()) {
            distance.put(u, -1);
            sigma.put(u, 0);
            delta.put(u, 0);
            betweenness.put(u, 0F);
            pred.put(u, new ArrayList<>());
        }
        for (int source: graph.keySet()) {
            // BFS
            queue.add(source);
            sigma.put(source, 1);
            distance.put(source, 0);
            while (!queue.isEmpty()) {
                int current = queue.poll();
                stack.push(current);
                for (int neighbor : graph.get(current)) {
                    if (distance.get(neighbor) < 0) {
                        queue.add(neighbor);
                        distance.put(neighbor, distance.get(current) + 1);
                    }
                    if (distance.get(neighbor) == distance.get(current) + 1) {
                        sigma.put(neighbor, sigma.get(neighbor) + sigma.get(current));
                        pred.get(neighbor).add(current);
                    }
                }
            }
            // DFS
            while (!stack.isEmpty()) {
                int current = stack.pop();
                for (int predecessor: pred.get(current)) {
                    delta.put(
                            predecessor,
                            (sigma.get(predecessor) / sigma.get(current) * (1 + delta.get(current)))
                    );
                    if (predecessor != source)
                        betweenness.put(
                                current,
                                betweenness.get(current) + delta.get(current)
                        );
                }
            }
        }
        return betweenness;
    }


    private static ArrayList<Integer> sortNodesDescending(Map<Integer, Float> map) {
        List<Map.Entry<Integer, Float>> entryList = new ArrayList<>(map.entrySet());

        // Custom comparator to sort in descending order
        Comparator<Map.Entry<Integer, Float>> valueComparator = (entry1, entry2) ->
                Float.compare(entry2.getValue(), entry1.getValue());

        // Sort the list using the custom comparator
        entryList.sort(valueComparator);

        // Create an arrayList to maintain the order
        ArrayList<Integer> sortedIds = new ArrayList<>();
        for (Map.Entry<Integer, Float> entry : entryList) {
            sortedIds.add(entry.getKey());
        }
        // return the ordered array
        return sortedIds;
    }

}