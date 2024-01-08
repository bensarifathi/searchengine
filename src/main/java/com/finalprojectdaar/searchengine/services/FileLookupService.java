package com.finalprojectdaar.searchengine.services;

import com.finalprojectdaar.searchengine.algorithmes.KMP;
import com.finalprojectdaar.searchengine.algorithmes.RegexToDfa;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class FileLookupService {
    private final KMP kmpAlgo;
    private final RegexToDfa regexAlgo;

    public FileLookupService(KMP kmpAlgo, RegexToDfa regexAlgo) {
        this.kmpAlgo = kmpAlgo;
        this.regexAlgo = regexAlgo;
    }

    private ArrayList<Integer> getBookIds() {
        return null;
    }

    public ArrayList<Integer> getCandidate(String pattern, boolean isRegex) throws IOException {
        var ids = getBookIds();
        ArrayList<Integer> matchIds = new ArrayList<>();
        for (Integer id: ids) {
            boolean isMatch = findMatch(pattern, isRegex, id);
            if(isMatch)
                matchIds.add(id);
        }
        return matchIds;
    }

    private boolean findMatch(String pattern, boolean isRegex, Integer id) throws IOException {
        return isRegex ? findMatchRegex(pattern, id) : findMatchKMP(pattern, id);
    }

    private boolean findMatchRegex(String pattern, Integer id) throws IOException {
        return regexAlgo.findMatch(pattern, id);
    }

    private boolean findMatchKMP(String pattern, Integer id) throws IOException {
        kmpAlgo.init(pattern);
        return kmpAlgo.findMatch(id);
    }
}
