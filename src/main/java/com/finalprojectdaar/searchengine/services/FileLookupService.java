package com.finalprojectdaar.searchengine.services;

import com.finalprojectdaar.searchengine.algorithmes.KMP;
import com.finalprojectdaar.searchengine.algorithmes.RegexToDfa;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Service
public class FileLookupService {
    private final KMP kmpAlgo;
    private final RegexToDfa regexAlgo;

    private ArrayList<Integer> bookIds;

    public FileLookupService(KMP kmpAlgo, RegexToDfa regexAlgo) throws IOException {
        this.kmpAlgo = kmpAlgo;
        this.regexAlgo = regexAlgo;
        this.bookIds = loadBookIds();
    }

    private ArrayList<Integer> loadBookIds() throws IOException {
        ArrayList<Integer> ids = new ArrayList<>();
        String baseDirPath = "books/";
        Resource resource = new ClassPathResource(baseDirPath);
        File baseDir = resource.getFile();
        File[] directoryListing = baseDir.listFiles();
        if(directoryListing == null) {
            return ids;
        }
        for (File file: directoryListing) {
            if(file.isDirectory())
                continue;
            String filename = file.getName();
            if(!filename.endsWith(".txt"))
                continue;
            int bookID = Integer.parseInt(filename.split(".txt")[0]);
            ids.add(bookID);
        }
        return ids;
    }

    public ArrayList<Integer> getCandidate(String pattern, boolean isRegex) throws IOException {
        ArrayList<Integer> matchIds = new ArrayList<>();
        for (Integer id: bookIds) {
            boolean result = findMatch(pattern, isRegex, id);
            if(result)
                matchIds.add(id);
        }
        return matchIds;
    }

    private boolean findMatch(String pattern, boolean isRegex, Integer id) throws IOException {
        return isRegex ? findMatchRegex(pattern, id) : findMatchKMP(pattern, id);
    }

    private boolean findMatchRegex(String pattern, Integer id) throws IOException {
        return regexAlgo.findMatch(pattern + "#", id);
    }

    private boolean findMatchKMP(String pattern, Integer id) throws IOException {
        kmpAlgo.init(pattern);
        return kmpAlgo.findMatch(id);
    }
}
