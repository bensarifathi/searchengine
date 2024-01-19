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

    public List<Book> order(List<Integer> inputIds, OrderAlgorithm algorithm) throws IOException {
        List<Integer> ids;
        if(algorithm == OrderAlgorithm.CLOSENESS_CENTRALITY)
            ids = orderWithClosenessCentrality(inputIds);
        else if (algorithm == OrderAlgorithm.BETWEENNESS_CENTRALITY) {
            ids = orderWithBetweennessCentrality(inputIds);
        } else if (algorithm == OrderAlgorithm.DEGREE_CENTRALITY) {
            ids = orderWithDegreeCentrality(inputIds);
        } else
            ids = new ArrayList<>();
        return bookMapper(ids);
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

    private List<Book> bookMapper(List<Integer> ids) throws IOException {
        // load the resource file
        String fileName = "db/books.json";
        Resource resource = new ClassPathResource(fileName);
        // read json file into jsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        );
        // parse the JsonNode and create a list of Book objects
        ArrayList<Book> bookList = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> elements = rootNode.fields();
        while (elements.hasNext()) {
            Map.Entry<String, JsonNode> entry = elements.next();
            String id = entry.getKey();
            if (!ids.contains(Integer.parseInt(id)))
                continue;
            JsonNode bookNode = entry.getValue();

            String author = bookNode.get("author").asText();
            String category = bookNode.get("category").asText();

            // Create a new Book object
            Book book = new Book();
            book.setId(Integer.parseInt(id));
            book.setAuthor(author);
            book.setCategory(category);

            bookList.add(book);
        }
        // Custom comparator based on the order of IDs
        Comparator<Book> idOrderComparator = Comparator.comparingInt(book -> ids.indexOf(book.getId()));
        bookList.sort(idOrderComparator);
        return bookList;
    }

}
