package com.finalprojectdaar.searchengine.algorithmes;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
public class Node {

    @Setter
    private String symbol;

    @Setter
    private Node parent;

    @Setter
    private Node left;

    @Setter
    private Node right;
    private final Set<Integer> firstPos;
    private final Set<Integer> lastPos;

    @Setter
    private boolean nullable;

    public Node(String symbol) {
        this.symbol = symbol;
        parent = null;
        left = null;
        right = null;

        firstPos = new HashSet<>();
        lastPos = new HashSet<>();
        nullable = false;
    }


    public void addToFirstPos(int number) {
        firstPos.add(number);
    }
    public void addAllToFirstPos(Set set) {
        firstPos.addAll(set);
    }

    public void addToLastPos(int number) {
        lastPos.add(number);
    }
    public void addAllToLastPos(Set set) {
        lastPos.addAll(set);
    }

}