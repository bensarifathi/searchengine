package com.finalprojectdaar.searchengine.algorithmes;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Setter
@Getter
public class LeafNode extends Node {
    
    private int num;
    private Set<Integer> followPos;

    public LeafNode(String symbol, int num) {
        super(symbol);
        this.num = num;
        followPos = new HashSet<>();
    }

    public void addToFollowPos(int number){
        followPos.add(number);
    }

}