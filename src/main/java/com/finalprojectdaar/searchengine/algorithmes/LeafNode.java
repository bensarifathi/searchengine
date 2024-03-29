package com.finalprojectdaar.searchengine.algorithmes;


import java.util.HashSet;
import java.util.Set;



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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Set<Integer> getFollowPos() {
        return followPos;
    }

    public void setFollowPos(Set<Integer> followPos) {
        this.followPos = followPos;
    }
}