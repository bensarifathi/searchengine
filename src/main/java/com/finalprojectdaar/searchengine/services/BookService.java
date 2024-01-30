package com.finalprojectdaar.searchengine.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalprojectdaar.searchengine.models.Book;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class BookService {
    private HashMap<Integer, Book> allBooks;
    private HashMap<Integer, Integer> clicks;
    private final ResourceLoader resourceLoader;

    public BookService(ResourceLoader resourceLoader) throws IOException {
        this.resourceLoader = resourceLoader;
        allBooks = new HashMap<>();
        clicks = new HashMap<>();
        initBooks();
        initClicks();
    }

    private void initBooks() throws IOException {
        String fileName = "db/books.json";
        Resource resource = new ClassPathResource(fileName);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        );
        Iterator<Map.Entry<String, JsonNode>> elements = rootNode.fields();
        while (elements.hasNext()) {
            Map.Entry<String, JsonNode> entry = elements.next();
            String id = entry.getKey();
            int intId = Integer.parseInt(id);

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

            allBooks.put(intId, book);
        }
    }
    public int getBookClicks(int bookId) {
        return clicks.getOrDefault(bookId, 0);
    }

    private void initClicks() throws IOException {
        String fileName = "db/clicks.json";
        Resource resource = new ClassPathResource(fileName);
        if (!resource.exists()) {
            initEmptyClicks();
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );
            Iterator<Map.Entry<String, JsonNode>> elements = rootNode.fields();
            while (elements.hasNext()) {
                Map.Entry<String, JsonNode> entry = elements.next();
                String id = entry.getKey();
                int intId = Integer.parseInt(id);

                int nbClicks = entry.getValue().asInt();

                clicks.put(intId, nbClicks);

                Book book = allBooks.get(intId);
                book.setClicks(nbClicks);
            }
        }
    }

    private void initEmptyClicks() throws IOException {
        String fileName = "db/clicks.json";
        Resource resource = new ClassPathResource(fileName);
        if (!resource.exists()) {
            HashMap<Integer, Integer> newClicks = new HashMap<>();
            for (Integer key : allBooks.keySet()) {
                // Put each key with the value of zero into the resultMap
                newClicks.put(key, 0);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Resource baseResource = resourceLoader.getResource("classpath:");
            String fullPath = baseResource.getFile().getPath() + "/" + fileName;
            System.out.println(fullPath);
            objectMapper.writeValue(new File(fullPath), newClicks);
        }
    }

    public List<Book> bookMapper(List<Integer> ids) {
        List<Book> books = new ArrayList<>();
        for (Integer id : ids) {
            books.add(allBooks.get(id));
        }

        return books;
    }

    public List<Book> randomBooks() {
        List<Book> books = new ArrayList<>();
        List<Integer> keys = new ArrayList<>(allBooks.keySet());
        Collections.shuffle(keys);
        for (int i = 0; i < 24; i++) {
            books.add(allBooks.get(keys.get(i)));
        }

        return books;
    }

    public List<Book> randomBooks(int limit) {
        List<Book> books = new ArrayList<>();
        List<Integer> keys = new ArrayList<>(allBooks.keySet());
        Collections.shuffle(keys);
        for (int i = 0; i < limit; i++) {
            books.add(allBooks.get(keys.get(i)));
        }

        return books;
    }

    public void clickOnBook(int bookId) throws IOException {
        Book book = allBooks.get(bookId);
        clickOnBook(book);
    }

    public void clickOnBook(Book book) throws IOException {
        int id = book.getId();
        int nbClicks = clicks.get(id);
        nbClicks++;
        clicks.put(id, nbClicks);
        book.setClicks(nbClicks);
        ObjectMapper objectMapper = new ObjectMapper();
        Resource baseResource = resourceLoader.getResource("classpath:");
        String fullPath = baseResource.getFile().getPath() + "/db/clicks.json";
        File file = new File(fullPath);
        file.delete();
        objectMapper.writeValue(file, clicks);
    }

}
