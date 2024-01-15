package com.finalprojectdaar.searchengine.services;

import com.finalprojectdaar.searchengine.graphs.ClosenessCentrality;
import com.finalprojectdaar.searchengine.graphs.PageRank;
import com.finalprojectdaar.searchengine.models.Book;
import com.finalprojectdaar.searchengine.text.jaccard.JaccardGraphGenerator;
import org.javatuples.Pair;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Service
public class OrderOutputService {
    private final Double DAMPING_FACTOR = 0.85;

    public enum OrderAlgorithm {
        PAGE_RANK,
        CENTRALITY
    }

    public ArrayList<Book> order(ArrayList<Integer> inputIds, OrderAlgorithm algorithm) {
        // todo singlton this
        JaccardGraphGenerator jaccardGraphGenerator = new JaccardGraphGenerator();
        Map<String, Map<String, Double>> graph = jaccardGraphGenerator.initForBookList(inputIds);

        Set<Pair<String, Double>> ordered = switch (algorithm) {
            case PAGE_RANK -> orderWithPageRank(graph);
            case CENTRALITY -> orderWithCentrality(graph);
        };

        ArrayList<Book> books = new ArrayList<>();
        for (Integer id : ordered.stream().map(Pair::getValue0).map(Integer::parseInt).toList()) {
            try {
                Book book = Book.fromId(id);
                books.add(book);

            } catch (IOException e) {
                continue;
            }
        }
        return books;
    }

    private Set<Pair<String, Double>> orderWithCentrality(Map<String, Map<String, Double>> graph) {
        return ClosenessCentrality.calculateClosenessCentrality(graph);
    }

    private Set<Pair<String, Double>> orderWithPageRank(Map<String, Map<String, Double>> graph) {
        return PageRank.calculate(graph, DAMPING_FACTOR);
    }

}
