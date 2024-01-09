package com.finalprojectdaar.searchengine.services;


public class KMP {
    String pattern;
    int[] lspArray;

    public int[] getLspArray() {
        return lspArray;
    }

    public KMP(String pattern) {
        this.pattern = pattern;
        this.lspArray = new int[pattern.length()];
    }

    public void fillLspArray() {
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

    public void findPattern(String text, int lineNum) {
        int textLen = text.length();
        int patLen = pattern.length();
        int i = 0; // text index
        int j = 0; // pattern index
        fillLspArray();
        while (textLen - i >= patLen - j) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == patLen) {
                System.out.println("[+] Pattern found in line " + lineNum + " at index " + (i-j));
                j = lspArray[j-1];
            } else if (i < textLen && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0)
                    j = lspArray[j-1];
                else
                    i++;
            }
        }

    }


}
