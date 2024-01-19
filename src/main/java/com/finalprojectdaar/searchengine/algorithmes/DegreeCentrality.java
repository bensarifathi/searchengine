package com.finalprojectdaar.searchengine.algorithmes;

import java.util.*;

public class DegreeCentrality {
    private Map<Integer, ArrayList<Integer>> graph;

    public DegreeCentrality(Map<Integer, ArrayList<Integer>> graph) {
        this.graph = graph;
    }


    public ArrayList<Integer> getOrderedNodes() {
        // O(n) where n = |V|
        // key: node, value: node degree
        Map<Integer, Integer> nodesDegree = new HashMap<>();
        for (Integer node: graph.keySet()) {
            int degree = getNodeDegree(node);
            nodesDegree.put(node, degree);
        }
        return sortNodesDescending(nodesDegree);
    }

    // O(1)
    private Integer getNodeDegree(Integer node) {
        return graph.get(node).size();
    }

    // O(n log n) where n is |v|
    private static ArrayList<Integer> sortNodesDescending(Map<Integer, Integer> map) {
        List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(map.entrySet());

        // Custom comparator to sort in descending order
        Comparator<Map.Entry<Integer, Integer>> valueComparator = (entry1, entry2) ->
                Float.compare(entry2.getValue(), entry1.getValue());

        // Sort the list using the custom comparator
        entryList.sort(valueComparator);

        // Create an arrayList to maintain the order
        ArrayList<Integer> sortedIds = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : entryList) {
            sortedIds.add(entry.getKey());
        }
        System.out.println(sortedIds);
        // return the ordered array
        return sortedIds;
    }

}
