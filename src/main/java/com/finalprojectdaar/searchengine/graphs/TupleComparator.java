package com.finalprojectdaar.searchengine.graphs;

import org.javatuples.Pair;

import java.util.Comparator;

public class TupleComparator implements Comparator<Pair<String, Double>> {
    @Override
    public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
        return o1.getValue1().compareTo(o2.getValue1());
    }
}
