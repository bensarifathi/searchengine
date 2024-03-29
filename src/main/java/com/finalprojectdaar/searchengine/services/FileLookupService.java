package com.finalprojectdaar.searchengine.services;

import com.finalprojectdaar.searchengine.algorithmes.KMP;
import com.finalprojectdaar.searchengine.algorithmes.RegexToDfa;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class FileLookupService {
    private final KMP kmpAlgo;
    private final RegexToDfa regexAlgo;

    private final ArrayList<Integer> bookIds;

    public FileLookupService(KMP kmpAlgo, RegexToDfa regexAlgo) {
        this.kmpAlgo = kmpAlgo;
        this.regexAlgo = regexAlgo;
        this.bookIds = loadBookIds();
    }

    private ArrayList<Integer> loadBookIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        String baseDirPath = System.getProperty("user.dir") + File.separator + "data/scrap-results/texts/";
        File baseDir = new File(baseDirPath);
        File[] directoryListing = baseDir.listFiles();
        if (directoryListing == null) {
            return ids;
        }
        for (File file : directoryListing) {
            if (file.isDirectory())
                continue;
            String filename = file.getName();
            if (!filename.endsWith(".txt"))
                continue;
            int bookID = Integer.parseInt(filename.split(".txt")[0]);
            ids.add(bookID);
        }
        return ids;
    }

    public ArrayList<Integer> getCandidate(String pattern, boolean isRegex) throws InterruptedException, ExecutionException {
        // start timer
        ArrayList<Integer> matchIds = new ArrayList<>();

        int numThreads = Runtime.getRuntime().availableProcessors(); // You can adjust this based on your needs
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Callable<Integer>> tasks = new ArrayList<>();

        for (Integer id : bookIds) {
            tasks.add(() -> findMatch(pattern, isRegex, id));
        }

        List<Future<Integer>> futures = executor.invokeAll(tasks);

        for (int i = 0; i < futures.size(); i++) {
            Future<Integer> future = futures.get(i);
            Integer hitRate = future.get();
            if (hitRate > 0) {
                matchIds.add(bookIds.get(i));
            }
        }

        executor.shutdown();

        // end timer
        long endTime = System.nanoTime();
        return matchIds;
    }

    private Integer findMatch(String pattern, boolean isRegex, Integer id) throws IOException {
        return isRegex ? findMatchRegex(pattern, id) : findMatchKMP(pattern, id);
    }

    private Integer findMatchRegex(String pattern, Integer id) throws IOException {
        return regexAlgo.findMatch(pattern + "#", id);
    }

    private Integer findMatchKMP(String pattern, Integer id) throws IOException {
        kmpAlgo.init(pattern);
        return kmpAlgo.findMatch(id);
    }
}
