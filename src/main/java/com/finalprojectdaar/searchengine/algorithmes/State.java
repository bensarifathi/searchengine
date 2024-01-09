package com.finalprojectdaar.searchengine.algorithmes;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class State {

    @Getter
    private Set<Integer> name;
    private final HashMap<String, State> move;
    
    private boolean IsAcceptable;
    @Setter
    private boolean IsMarked;
    
    public State(int ID){
        move = new HashMap<>();
        name = new HashSet<>();
        IsAcceptable = false;
        IsMarked = false;
    }
    
    public void addMove(String symbol, State s){
        move.put(symbol, s);
    }
    
    public void addToName(int number){
        name.add(number);
    }
    public void addAllToName(Set<Integer> number){
        name.addAll(number);
    }

    public boolean getIsMarked(){
        return IsMarked;
    }

    public void setAccept() {
        IsAcceptable = true;
    }
    
    public boolean getIsAcceptable(){
        return  IsAcceptable;
    }
    
    public State getNextStateBySymbol(String str){
        return this.move.get(str);
    }
    
    public HashMap<String, State> getAllMoves(){
        return move;
    }
    
}