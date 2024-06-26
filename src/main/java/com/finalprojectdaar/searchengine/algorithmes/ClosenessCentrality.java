package com.finalprojectdaar.searchengine.algorithmes;

import java.util.*;

public class ClosenessCentrality {
    private Map<Integer, ArrayList<Integer>> graph;

    public ClosenessCentrality(Map<Integer, ArrayList<Integer>> graph) {
        this.graph = graph;
    }

    // Breadth-first search algo O(m) where m is |E|
    private Map<Integer, Integer> bfs(Integer node) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        Map<Integer, Integer> level = new HashMap<>();
        // push the given node to the queue
        queue.add(node);
        level.put(node, 0);
        while (!queue.isEmpty()) {
            Integer currentNode = queue.poll();
            visited.add(currentNode);
            for (Integer neighbor: graph.get(currentNode)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    level.put(neighbor, level.get(currentNode) + 1);
                }
            }
        }
        return level;
    }

    // O(n*m) where n |V| and m |E|
    private Float getNodeCentrality(Integer node) {
        Map<Integer, Integer> nodeDistances = bfs(node);
        float sum = 0F;
        for (Integer d: nodeDistances.values())
            sum += d;
        return sum > 0 ? 1 / sum : 0;
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

    public ArrayList<Integer> getOrderedNodes() {
        // key: node, value: closenessCentrality
        Map<Integer, Float> nodesCentrality = new HashMap<>();
        for (Integer node: graph.keySet()) {
            float centrality = getNodeCentrality(node);
            nodesCentrality.put(node, centrality);
        }
        return sortNodesDescending(nodesCentrality);
    }
}
