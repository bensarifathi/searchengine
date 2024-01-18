package com.finalprojectdaar.searchengine;

import com.finalprojectdaar.searchengine.scraper.WebScraper;
import com.finalprojectdaar.searchengine.text.jaccard.JaccardGraphGenerator;

import java.io.IOException;
import java.util.Objects;

public class AppInitialiser {
    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length < 1){
            System.out.println("Usage: Must have --scrape or --serve argument");
            return;
        }
        if(Objects.equals(args[0], "--scrape")){
            if(args.length < 2){
                System.out.println("Usage: Must have --scrape <size> or --serve argument");
                return;
            }
            try{
                Integer.parseInt(args[1]);
                WebScraper.scrape(Integer.parseInt(args[1]));
            }catch (NumberFormatException e){
                System.out.println("Usage: Must have --scrape <size> or --serve argument");
            }
        }else if(Objects.equals(args[1], "--serve")){
            SearchengineApplication.main(args);
        }else{
            System.out.println("Usage: Must have --scrape or --serve argument");
        }
    }
}
