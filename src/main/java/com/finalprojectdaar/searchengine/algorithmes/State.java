package com.finalprojectdaar.searchengine.algorithmes;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class State {

    private Set<Integer> name;
    private final HashMap<String, State> move;
    
    private boolean IsAcceptable;
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

    public Set<Integer> getName() {
        return name;
    }

    public void setName(Set<Integer> name) {
        this.name = name;
    }

    public HashMap<String, State> getMove() {
        return move;
    }

    public boolean isAcceptable() {
        return IsAcceptable;
    }

    public void setAcceptable(boolean acceptable) {
        IsAcceptable = acceptable;
    }

    public boolean isMarked() {
        return IsMarked;
    }

    public void setMarked(boolean marked) {
        IsMarked = marked;
    }
}