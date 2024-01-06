package com.finalprojectdaar.searchengine.scraper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.annotation.ElementType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class WebScraper {
    private static final Logger logger = LogManager.getLogger(WebScraper.class);
    static final String BASE_URL = "https://www.gutenberg.org";
    static final int FIRST_BOOK_ID = 2701;
    static final int MAX_SIZE = 20;


    public static void main(String[] args) throws IOException {
        HashSet<Integer> scannedBooks = new HashSet<>();
        HashSet<Integer> allBooks = new HashSet<>();
        ArrayDeque<Integer> todoBooks = new ArrayDeque<>();
        HashMap<Integer, String>  bookIdToName = new HashMap<>();
        HashMap<Integer, HashSet<Integer>> bookIdToSimilarIds = new HashMap<>();

        todoBooks.add(FIRST_BOOK_ID);


        // log start
        logger.info("Starting scraping");
        while (!todoBooks.isEmpty() ){
            int currentBookId = todoBooks.pop();
            allBooks.add(currentBookId);
            if(scannedBooks.contains(currentBookId)){
                logger.info("Book with id " + currentBookId + " already scanned, skipping");
                continue;
            }
            String bookUrl = BASE_URL + "/ebooks/" + currentBookId ;
            Document doc = Jsoup.connect(bookUrl).get();
            Element titleElement = doc.selectFirst("#content > h1:nth-child(2)");
            if(titleElement == null){
                logger.info("Title for book with id " + currentBookId + " not found, skipping");
                // print the doc html
                System.out.println(doc.html());
                continue;
            }
            logger.info("===========================================");
            logger.info("Scraping book " + currentBookId);
            String bookTitle = titleElement.text();
            logger.info("Book Title: " + bookTitle);
            bookIdToName.put(currentBookId, bookTitle);
            scannedBooks.add(currentBookId);
            doc = Jsoup.connect(bookUrl + "/also/").get();
            Elements similarBooksLiElement = doc.select("li.booklink");

            for (Element similarBookLiElement : similarBooksLiElement) {
                if(allBooks.size() >= MAX_SIZE){
                    break;
                }
                logger.info("Scanning similar books...");
                logger.info("Found " + similarBooksLiElement.size() + " similar books");

                Element similarBookAElement = similarBookLiElement.selectFirst("a");
                if(similarBookAElement == null){
                    continue;
                }
                String similarBookUrl = similarBookAElement.attr("href");
                // href="/ebooks/1259"
                int similarBookId = Integer.parseInt(similarBookUrl.substring(8));
                if(!scannedBooks.contains(similarBookId)){
                    todoBooks.add(similarBookId);
                }
                if(!bookIdToSimilarIds.containsKey(currentBookId)){
                    bookIdToSimilarIds.put(currentBookId, new HashSet<>());
                }
                bookIdToSimilarIds.get(currentBookId).add(similarBookId);
            }

            logger.info("Getting book text...");

            String textFilPath = "data/scrap-results/texts/" + currentBookId + ".txt";
            textFilPath =  System.getProperty("user.dir") + File.separator + textFilPath;
            File file = new File(textFilPath);
            if(file.exists()){
                logger.info("Book text already exists, skipping");
            }

            String downloadUrl = bookUrl + ".txt.utf-8";
            System.out.println(downloadUrl);
            // download the file and follow redirects with three retries ( cnx 3ayatni yakho )
            Connection.Response response = null;
            int retryCounter = 0;
            while (retryCounter <= 3){
                try {
                    response = Jsoup.connect(downloadUrl).followRedirects(true).execute();
                    break;
                } catch (IOException e) {
                    logger.error("Error while downloading book text for book with id " + currentBookId);
                    retryCounter++;
                    if(retryCounter < 3){
                        logger.error("Retrying (" + retryCounter + "/3)...");
                    }
                }
            }

            if(response == null){
                logger.error("Couldn't download the text for book " + currentBookId + ". Fix your internet and try againt :) ");
                System.exit(1);
            }
            // save reponse body to file
            try (FileOutputStream out = (new FileOutputStream(new File(textFilPath)))) {
                out.write(response.bodyAsBytes());
            } catch (IOException e) {
                logger.error("Error while saving book text to file: " + textFilPath);
                logger.error(e.getMessage());
            }



            logger.info("Scanning book with id " + currentBookId + " finished");
            logger.info("===========================================");
        }
        logger.info("*****************************************************");
        logger.info("Scraping finished");

        logger.info("*****************************************************");
        logger.info("Saving generated graph results to json files...");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json_path = "data/scrap-results/json/similar-graph.json";
        json_path =  System.getProperty("user.dir") + File.separator + json_path;
        File file = new File(json_path);
        if(file.exists()){
            renameFile(json_path);
        }

        try (FileWriter writer = new FileWriter(json_path)) {
            // Convert HashMap to JSON and write to file
            gson.toJson(bookIdToSimilarIds, writer);
            System.out.println("HashMap saved to file: " + json_path);
        } catch (IOException e) {
            logger.error("Error while saving HashMap to file: " + json_path);
            logger.error(e.getMessage());
        }

        logger.info("Generating dot file for graphviz...");
        String dot_path = "data/scrap-results/dot/similar-graph.dot";
        dot_path =  System.getProperty("user.dir") + File.separator + dot_path;
        file = new File(dot_path);
        if(file.exists()){
            renameFile(dot_path);
        }
        StringBuilder dotFileContent = new StringBuilder();
        dotFileContent.append("digraph G {\n");

        for(Integer bookId : allBooks){
            dotFileContent.append("\t").append(bookId).append(" [label=\"").append(bookIdToName.get(bookId)).append("\"];\n");
        }

        // add edges
        for(Integer bookId : bookIdToSimilarIds.keySet()){
            for(Integer similarBookId : bookIdToSimilarIds.get(bookId)){
                dotFileContent.append("\t").append(bookId).append(" -> ").append(similarBookId).append(";\n");
            }
        }

        dotFileContent.append("}");

        logger.info("Saving dot file...");

        try(FileWriter writer = new FileWriter(dot_path)) {
            writer.write(dotFileContent.toString());
            logger.info("Dot file saved to file: " + dot_path);
        } catch (IOException e) {
            logger.error("Error while saving dot file to file: " + dot_path);
            logger.error(e.getMessage());
        }

        logger.info("*****************************************************");
        logger.info("done.");
    }

    static void renameFile(String filePath){

        // Generate a timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());

        // Extract the file extension (if any)
        int lastDotIndex = filePath.lastIndexOf(".");
        String extension = lastDotIndex > 0 ? filePath.substring(lastDotIndex) : "";

        // Generate the new file name with timestamp
        String newFileName = filePath.replaceFirst("[.][^.]+$", "") + "_" + timestamp + extension;

        // Rename the existing file
        File oldFile = new File(filePath);
        File newFile = new File(newFileName);

        if (oldFile.renameTo(newFile)) {
            logger.warn("File renamed to: " + newFileName);
        } else {
            logger.error("Failed to rename the file.");
        }
    }
}
