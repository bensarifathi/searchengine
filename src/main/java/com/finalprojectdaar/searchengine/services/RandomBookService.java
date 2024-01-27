package com.finalprojectdaar.searchengine.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalprojectdaar.searchengine.models.Book;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class RandomBookService {

    private final Integer limit = 24;

    public ArrayList<Book> fetch() throws IOException {
        return bookMapper();
    }

    private ArrayList<Book> bookMapper() throws IOException {
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
        int count = 0;
        while (elements.hasNext()) {
            Map.Entry<String, JsonNode> entry = elements.next();
            String id = entry.getKey();
            JsonNode bookNode = entry.getValue();

            String author = bookNode.get("author").asText();
            String category = bookNode.get("category").asText();
            String title = bookNode.get("title").asText();

            // Create a new Book object
            Book book = new Book();
            book.setId(Integer.parseInt(id));
            book.setAuthor(author);
            book.setCategory(category);
            book.setTitle(title);

            bookList.add(book);
            count ++;
            if (count == limit)
                break;
        }
        // Custom comparator based on the order of IDs
        return bookList;
    }
}
