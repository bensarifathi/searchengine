package com.finalprojectdaar.searchengine.services;

import com.finalprojectdaar.searchengine.models.Book;
import org.springframework.stereotype.Service;


import java.util.ArrayList;

@Service
public class OrderOutputService {
    public ArrayList<Book> order(ArrayList<Integer> inputIds) {
        ArrayList<Book> books = new ArrayList<>();
        for (Integer id: inputIds) {
            Book book = new Book();
            book.setId(id);
            books.add(book);
        }
        return books;
    }
}
