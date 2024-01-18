package com.finalprojectdaar.searchengine.controllers;

import com.finalprojectdaar.searchengine.models.Book;
import com.finalprojectdaar.searchengine.services.FileLookupService;
import com.finalprojectdaar.searchengine.services.OrderOutputService;
import com.finalprojectdaar.searchengine.text.jaccard.JaccardGraphGeneratorPair;
import lombok.extern.java.Log;
import org.javatuples.Pair;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController()
@RequestMapping("/api/books")
@Log
public class BookStoreController {

    private final FileLookupService fileLookupService;
    private final OrderOutputService orderOutputService;
    private static final String IMAGE_DIRECTORY = "data/scrap-results/img/";

    public BookStoreController(FileLookupService fileLookupService, OrderOutputService orderOutputService) {
        this.fileLookupService = fileLookupService;
        this.orderOutputService = orderOutputService;
    }

    @GetMapping("")
    public ResponseEntity<ArrayList<Book>> fetchBooks(@RequestParam String pattern, @RequestParam boolean isRegex) throws IOException, InterruptedException, ExecutionException {
        long startTime = (System.currentTimeMillis());
        ArrayList<Integer> results = fileLookupService.getCandidate(pattern, isRegex);
        long executionTime = (System.currentTimeMillis() - startTime) / 1000;
        log.info("Time taken for getCandidate is : " + executionTime + " seconds, found " + results.size());

        startTime = (System.currentTimeMillis());
        JaccardGraphGeneratorPair jaccardGraphGenerator = new JaccardGraphGeneratorPair();
        Map<Pair<String, String>, Double> graph = jaccardGraphGenerator.initForBookList(results);
        executionTime = (System.currentTimeMillis() - startTime) / 1000;
        log.info("Time taken for jaccardGraphGenerator is : " + executionTime + " seconds");

        startTime = (System.currentTimeMillis());
        ArrayList<Book> books = orderOutputService.order(graph, OrderOutputService.OrderAlgorithm.CENTRALITY);
        executionTime = (System.currentTimeMillis() - startTime) / 1000;
        log.info("Time taken for jaccardGraphGenerator is : " + executionTime + " seconds");

        return ResponseEntity
                .status(200)
                .body(books);
    }

    @GetMapping("/img/{id}")
    public ResponseEntity<Resource> getImageById(@PathVariable String id) {
        try {
            // Construct the file path based on the id
            Path imagePath = Paths.get(IMAGE_DIRECTORY).resolve(id + ".png"); // Change the extension based on your image format

            // Load the image as a resource
            Resource resource = new UrlResource(imagePath.toUri());


            System.out.println(resource);
            // Check if the resource exists
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + resource.getFilename())
                        .contentType(MediaType.IMAGE_JPEG) // Change the content type based on your image format
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException | RuntimeException e) {
            // Handle exceptions (e.g., file not found, etc.)
            return ResponseEntity.status(500).build();
        }
    }
}
