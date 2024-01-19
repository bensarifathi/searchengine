package com.finalprojectdaar.searchengine.services;

import com.finalprojectdaar.searchengine.algorithmes.KMP;
import com.finalprojectdaar.searchengine.algorithmes.RegexToDfa;
import com.finalprojectdaar.searchengine.algorithmes.State;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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

    private List<Integer> getCandidateRegex(String pattern) throws IOException, InterruptedException {
        List<Integer> matchIds = new CopyOnWriteArrayList<Integer>();
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Callable<Void>> tasks = new ArrayList<>();
        State q0 = regexAlgo.buildDfa(pattern + "#");

        for (Integer id : bookIds) {
            tasks.add(() -> {
                if (regexAlgo.findMatch(q0, id)) matchIds.add(id);
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        return matchIds;
    }

    private List<Integer> getCandidateKMP(String pattern) throws IOException, InterruptedException {
        List<Integer> matchIds = new CopyOnWriteArrayList<Integer>();
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Callable<Void>> tasks = new ArrayList<>();
        int[] lspArray = kmpAlgo.getLSPArray(pattern);

        for (Integer id : bookIds) {
            tasks.add(() -> {
                if (kmpAlgo.findMatch(lspArray, pattern, id)) matchIds.add(id);
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        return matchIds;
    }

    public List<Integer> getCandidate(String pattern, boolean isRegex) throws IOException, ExecutionException, InterruptedException {
        if (isRegex) {
            return getCandidateRegex(pattern);
        } else {
            return getCandidateKMP(pattern);
        }
    }
}
