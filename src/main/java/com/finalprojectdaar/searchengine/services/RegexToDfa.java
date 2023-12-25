package com.finalprojectdaar.searchengine.services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


public class RegexToDfa {

    private static Set<Integer>[] followPos;
    private static Node root;
    private static Set<State> DStates;

    private static Set<String> input; //set of characters is used in input regex

    /**
     * a number is assigned to each characters in the regex (even duplicate ones)
     *
     * @param symbNum is a hash map has a key which mentions the number and has
     * a value which mentions the corresponding character or sometimes a string
     * for characters is followed up by backslash like "\*"
     */
    private static HashMap<Integer, String> symbNum;

    public static void main(String[] args) throws IOException {
        initialize();
    }

    public static void initialize() throws IOException {
        Scanner in = new Scanner(System.in);
        //allocating
        DStates = new HashSet<>();
        input = new HashSet<String>();

        String regex = getRegex(in);
        long startTime = System.currentTimeMillis();
        getSymbols(regex);

        /**
         * giving the regex to SyntaxTree class constructor and creating the
         * syntax tree of the regular expression in it
         */
        SyntaxTree st = new SyntaxTree(regex);
        root = st.getRoot(); //root of the syntax tree
        followPos = st.getFollowPos(); //the followpos of the syntax tree

        /**
         * creating the DFA using the syntax tree were created upside and
         * returning the start state of the resulted DFA
         */
        State q0 = createDFA();
        lookupForMatch(q0);
        in.close();
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Program execution time: " + executionTime + " milliseconds");
    }

    private static String getRegex(Scanner in) {
        System.out.print("Enter a regex: ");
        String regex = in.nextLine();
        return regex+"#";
    }

    private static void getSymbols(String regex) {
        /**
         * op is a set of characters have operational meaning for example '*'
         * could be a closure operator
         */
        Set<Character> op = new HashSet<>();
        Character[] ch = {'(', ')', '*', '|', '&', '.', '\\', '[', ']', '+'};
        op.addAll(Arrays.asList(ch));

        input = new HashSet<>();
        symbNum = new HashMap<>();
        int num = 1;
        for (int i = 0; i < regex.length(); i++) {
            char charAt = regex.charAt(i);

            /**
             * if a character which is also an operator, is followed up by
             * backslash ('\'), then we should consider it as a normal character
             * and not an operator
             */
            if (op.contains(charAt)) {
                if (i - 1 >= 0 && regex.charAt(i - 1) == '\\') {
                    input.add("\\" + charAt);
                    symbNum.put(num++, "\\" + charAt);
                }
            } else {
                input.add("" + charAt);
                symbNum.put(num++, "" + charAt);
            }
        }
    }

    private static State createDFA() {
        int id = 0;
        Set<Integer> firstpos_n0 = root.getFirstPos();

        State q0 = new State(id++);
        q0.addAllToName(firstpos_n0);
        if (q0.getName().contains(followPos.length)) {
            q0.setAccept();
        }
        DStates.clear();
        DStates.add(q0);

        while (true) {
            boolean exit = true;
            State s = null;
            for (State state : DStates) {
                if (!state.getIsMarked()) {
                    exit = false;
                    s = state;
                }
            }
            if (exit) {
                break;
            }

            if (s.getIsMarked()) {
                continue;
            }
            s.setIsMarked(true); //mark the state
            Set<Integer> name = s.getName();
            for (String a : input) {
                Set<Integer> U = new HashSet<>();
                for (int p : name) {
                    if (symbNum.get(p).equals(a)) {
                        U.addAll(followPos[p - 1]);
                    }
                }
                boolean flag = false;
                State tmp = null;
                for (State state : DStates) {
                    if (state.getName().equals(U)) {
                        tmp = state;
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    State q = new State(id++);
                    q.addAllToName(U);
                    if (U.contains(followPos.length)) {
                        q.setAccept();
                    }
                    DStates.add(q);
                    tmp = q;
                }
                s.addMove(a, tmp);
            }
        }

        return q0;
    }

    private static String getStr(Scanner in) {
        System.out.print("Enter a string: ");
        String str;
        str = in.nextLine();
        return str;
    }

    private static void lookupForMatch(State q0) throws IOException {
        DfaTraversal dfat = new DfaTraversal(q0, input);
        BufferedReader buffer = new BufferedReader(
                new FileReader("/Users/macos/Desktop/Daar-projet-1/src/main/resources/text.txt")
        );
        String line;
        int lineNumber = 0;
        System.out.println();
        while ((line = buffer.readLine()) != null) {
            lineNumber++;
            dfat.resetState();
            boolean acc;
            for (int i = 0; i < line.length(); i++) {
                for (int j = i; j < line.length(); j++) {
                    if (dfat.setCharacter(line.charAt(j))) {
                        acc = dfat.traverse();
                        if (acc) {
                            System.out.println("[+] Match found in line " + lineNumber + " at index: <<" + i + ">>");
                            dfat.resetState();
                            break;
                        }
                    } else
                    {
                        dfat.resetState();
                        break;
                    }
                }
            }
        }
        buffer.close();
    }

}
