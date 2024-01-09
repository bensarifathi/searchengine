package com.finalprojectdaar.searchengine.algorithmes;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


@Component
public class RegexToDfa {

    private static HashSet[] followPos;
    private static Node root;
    private static Set<State> DStates;

    private static Set<String> input; //set of characters is used in input regex

    private static HashMap<Integer, String> symbNum;

    public boolean findMatch(String regex, Integer textID) throws IOException {
        DStates = new HashSet<>();
        input = new HashSet<>();
        getSymbols(regex);
        SyntaxTree st = new SyntaxTree(regex);
        root = st.getRoot(); //root of the syntax tree
        followPos = st.getFollowPos(); //the followpos of the syntax tree
        State q0 = createDFA();
        return lookupForMatch(q0, textID);
    }

    private static void getSymbols(String regex) {
        Character[] ch = {'(', ')', '*', '|', '&', '.', '\\', '[', ']', '+'};
        Set<Character> op = new HashSet<>(Arrays.asList(ch));

        input = new HashSet<>();
        symbNum = new HashMap<>();
        int num = 1;
        for (int i = 0; i < regex.length(); i++) {
            char charAt = regex.charAt(i);

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

    private static boolean lookupForMatch(State q0, Integer textID) throws IOException {
        DfaTraversal dfat = new DfaTraversal(q0, input);
        BufferedReader buffer = new BufferedReader(
                new FileReader("/Users/macos/Desktop/Daar-projet-1/src/main/resources/"
                        + textID
                        + ".txt"
                )
        );
        String line;
        int lineNumber = 0;
        boolean output = false;
        while ((line = buffer.readLine()) != null) {
            lineNumber++;
            dfat.resetState();
            boolean acc;
            for (int i = 0; i < line.length(); i++) {
                for (int j = i; j < line.length(); j++) {
                    if (dfat.setCharacter(line.charAt(j))) {
                        acc = dfat.traverse();
                        if (acc) {
                            output = true;
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
        return output;
    }
}