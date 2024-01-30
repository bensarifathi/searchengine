package com.finalprojectdaar.searchengine.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalprojectdaar.searchengine.algorithmes.BetweennessCentrality;
import com.finalprojectdaar.searchengine.algorithmes.ClosenessCentrality;
import com.finalprojectdaar.searchengine.algorithmes.DegreeCentrality;
import com.finalprojectdaar.searchengine.algorithmes.JaccardGraph;
import com.finalprojectdaar.searchengine.enumerations.OrderAlgorithm;
import com.finalprojectdaar.searchengine.models.Book;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class OrderOutputService {
    private final BookService booksService;

    public OrderOutputService(BookService booksService) {
        this.booksService = booksService;
    }

    public List<Integer> order(List<Integer> inputIds, OrderAlgorithm algorithm) throws IOException {
        List<Integer> ids;
        if (algorithm == OrderAlgorithm.NUMBER_OF_CLICKS)
            ids = orderWithNumberOfClicks(inputIds);
        else if (algorithm == OrderAlgorithm.CLOSENESS_CENTRALITY)
            ids = orderWithClosenessCentrality(inputIds);
        else if (algorithm == OrderAlgorithm.BETWEENNESS_CENTRALITY) {
            ids = orderWithBetweennessCentrality(inputIds);
        } else if (algorithm == OrderAlgorithm.DEGREE_CENTRALITY) {
            ids = orderWithDegreeCentrality(inputIds);
        } else
            ids = new ArrayList<>();
        return ids;
    }

    private List<Integer> orderWithNumberOfClicks(List<Integer> nodes) {
        List<Integer> orderedNodes = new ArrayList<>(nodes);
        orderedNodes.sort((o1, o2) -> {
            int clicks1 = booksService.getBookClicks(o1);
            int clicks2 = booksService.getBookClicks(o2);
            return clicks2 - clicks1;
        });
        return orderedNodes;
    }

    private List<Integer> orderWithDegreeCentrality(List<Integer> nodes) throws IOException {
        Map<Integer, List<Integer>> graph = buildJaccardGraph(nodes);
        DegreeCentrality dc = new DegreeCentrality(graph);
        return dc.getOrderedNodes();
    }

    private List<Integer> orderWithClosenessCentrality(List<Integer> nodes) throws IOException {
        Map<Integer, List<Integer>> graph = buildJaccardGraph(nodes);
        ClosenessCentrality cc = new ClosenessCentrality(graph);
        return cc.getOrderedNodes();
    }

    private List<Integer> orderWithBetweennessCentrality(List<Integer> nodes) throws IOException {
        Map<Integer, List<Integer>> graph = buildJaccardGraph(nodes);
        BetweennessCentrality bc = new BetweennessCentrality(graph);
        return bc.getOrderedNodes();
    }

    private static Map<Integer, List<Integer>> buildJaccardGraph(List<Integer> nodes) throws IOException {
        JaccardGraph jaccardGraph = new JaccardGraph(nodes);
        Map<Integer, List<Integer>> graph = jaccardGraph.buildJaccardGraph().getGraph();
        return graph;
    }

}
