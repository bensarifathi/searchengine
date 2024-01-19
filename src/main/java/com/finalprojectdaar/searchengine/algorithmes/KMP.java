package com.finalprojectdaar.searchengine.algorithmes;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Component
public class KMP {

    public int[] getLSPArray(String pattern) {
        int patLen = pattern.length();
        int[] lspArray = new int[patLen];
        int i = 1;
        int j = 0;
        while (i < patLen) {
            if (pattern.charAt(i) == pattern.charAt(j)) {
                j++;
                lspArray[i] = j;
                i++;
            } else {
                if (j == 0) {
                    lspArray[i] = j;
                    i++;
                } else {
                    j = lspArray[j - 1];
                }
            }
        }
        return lspArray;
    }

    public boolean findMatch(int[] lspArray, String pattern, int textID) throws IOException {
        String fileName = "books/" + textID + ".txt";
        Resource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            // Read each line and process it
            String text;
            while ((text = reader.readLine()) != null) {
                int textLen = text.length();
                int patLen = lspArray.length;
                int i = 0; // text index
                int j = 0; // pattern index

                while (textLen - i >= patLen - j) {
                    if (pattern.charAt(j) == text.charAt(i)) {
                        i++;
                        j++;
                    }
                    if (j == patLen) {
                        return true;
                    } else if (i < textLen && pattern.charAt(j) != text.charAt(i)) {
                        if (j != 0)
                            j = lspArray[j - 1];
                        else
                            i++;
                    }
                }
            }
        }
        return false;
    }
}
