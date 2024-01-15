package com.finalprojectdaar.searchengine.algorithmes;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Component
public class KMP {
    String pattern;
    @Getter
    int[] lspArray;

    public void init(String pattern) {
        this.pattern = pattern;
        this.lspArray = new int[pattern.length()];
        fillLspArray();
    }

    private void fillLspArray() {
        int patLen = lspArray.length;
        int i = 1;
        int j = 0;
        while (i < patLen) {
            if (pattern.charAt(i) == pattern.charAt(j)) {
                j++;
                lspArray[i] = j;
                i++;
            }
            else {
                if (j == 0){
                    lspArray[i] = j;
                    i++;
                }else {
                    j = lspArray[j-1];
                }
            }
        }
    }

    public Integer findMatch(Integer textID) throws IOException {
        String filePath = System.getProperty("user.dir") + File.separator + "data/scrap-results/texts/" + textID + ".txt";
        BufferedReader buffer = new BufferedReader(new FileReader(filePath));
        String text;
        int hitRate = 0;
        while ((text = buffer.readLine()) != null) {
            int textLen = text.length();
            int patLen = pattern.length();
            int i = 0; // text index
            int j = 0; // pattern index

            while (textLen - i >= patLen - j) {
                if (pattern.charAt(j) == text.charAt(i)) {
                    i++;
                    j++;
                }
                if (j == patLen) {
                    hitRate ++;
                    j = lspArray[j-1];
                } else if (i < textLen && pattern.charAt(j) != text.charAt(i)) {
                    if (j != 0)
                        j = lspArray[j-1];
                    else
                        i++;
                }
            }
        }
    return hitRate;
    }
}
