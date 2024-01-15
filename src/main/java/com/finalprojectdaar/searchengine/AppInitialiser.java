package com.finalprojectdaar.searchengine;

import com.finalprojectdaar.searchengine.scraper.WebScraper;
import com.finalprojectdaar.searchengine.text.jaccard.JaccardGraphGenerator;

import java.io.IOException;

public class AppInitialiser {
    public static void main(String[] args) throws IOException, InterruptedException {
        WebScraper.scrape();
        JaccardGraphGenerator.init();
    }
}
