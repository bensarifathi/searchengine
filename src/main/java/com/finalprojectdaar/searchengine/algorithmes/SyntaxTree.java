package com.finalprojectdaar.searchengine.algorithmes;



import java.util.HashSet;
import java.util.Set;

public class SyntaxTree {

    private final Node root; //the head of raw syntax tree
    private final HashSet[] followPos;

    public SyntaxTree(String regex) {
        BinaryTree bt = new BinaryTree();

        root = bt.generateTree(regex);
        int numOfLeafs = bt.getNumberOfLeafs();
        followPos = new HashSet[numOfLeafs];
        for (int i = 0; i < numOfLeafs; i++) {
            followPos[i] = new HashSet<>();
        }
        // bt.printInorder(root);
        generateNullable(root);
        generateFirstposLastPos(root);
        generateFollowPos(root);
    }

    private void generateNullable(Node node) {
        if (node == null) {
            return;
        }
        if (!(node instanceof LeafNode)) {
            Node left = node.getLeft();
            Node right = node.getRight();
            generateNullable(left);
            generateNullable(right);
            switch (node.getSymbol()) {
                case "|":
                    node.setNullable(left.isNullable() | right.isNullable());
                    break;
                case "&":
                    node.setNullable(left.isNullable() & right.isNullable());
                    break;
                case "*":
                    node.setNullable(true);
                    break;
            }
        }
    }

    private void generateFirstposLastPos(Node node) {
        if (node == null) {
            return;
        }
        if (node instanceof LeafNode lnode) {
            node.addToFirstPos(lnode.getNum());
            node.addToLastPos(lnode.getNum());
        } else {
            Node left = node.getLeft();
            Node right = node.getRight();
            generateFirstposLastPos(left);
            generateFirstposLastPos(right);
            switch (node.getSymbol()) {
                case "|":
                    node.addAllToFirstPos(left.getFirstPos());
                    node.addAllToFirstPos(right.getFirstPos());
                    //
                    node.addAllToLastPos(left.getLastPos());
                    node.addAllToLastPos(right.getLastPos());
                    break;
                case "&":
                    if (left.isNullable()) {
                        node.addAllToFirstPos(left.getFirstPos());
                        node.addAllToFirstPos(right.getFirstPos());
                    } else {
                        node.addAllToFirstPos(left.getFirstPos());
                    }
                    //
                    if (right.isNullable()) {
                        node.addAllToLastPos(left.getLastPos());
                        node.addAllToLastPos(right.getLastPos());
                    } else {
                        node.addAllToLastPos(right.getLastPos());
                    }
                    break;
                case "*":
                    node.addAllToFirstPos(left.getFirstPos());
                    node.addAllToLastPos(left.getLastPos());
                    break;
            }
        }
    }

    private void generateFollowPos(Node node) {
        if (node == null) {
            return;
        }
        Node left = node.getLeft();
        Node right = node.getRight();
        switch (node.getSymbol()) {
            case "&":
                Object[] lastpos_c1 = left.getLastPos().toArray();
                Set<Integer> firstpos_c2 = right.getFirstPos();
                for (Object o : lastpos_c1) {
                    followPos[(Integer) o - 1].addAll(firstpos_c2);
                }
                break;
            case "*":
                Object[] lastpos_n = node.getLastPos().toArray();
                Set<Integer> firstpos_n = node.getFirstPos();
                for (Object o : lastpos_n) {
                    followPos[(Integer) o - 1].addAll(firstpos_n);
                }
                break;
        }
        generateFollowPos(node.getLeft());
        generateFollowPos(node.getRight());

    }

    public void show(Node node) {
        if (node == null) {
            return;
        }
        show(node.getLeft());
        Object[] s = node.getLastPos().toArray();

        show(node.getRight());
    }

    public void showFollowPos() {
        for (HashSet followPo : followPos) {
            Object[] s = followPo.toArray();
        }
    }

    public Node getRoot() {
        return root;
    }

    public HashSet[] getFollowPos() {
        return followPos;
    }
}