package net.coderodde.graph.kshortest.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.coderodde.graph.DirectedGraphWeightFunction;
import java.util.Objects;
import net.coderodde.graph.DirectedGraphNode;
import net.coderodde.graph.kshortest.Path;

/**
 * This class represents a path in a graph. The only difference between 
 * {@link net.coderodde.graph.kshortest.Path} is that this class implements the
 * path as a linked list of graph nodes. This arrangement allows us to extend 
 * any given path by one graph node in constant time.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jun 4, 2019)
 */
final class LinkedPathNode implements Comparable<LinkedPathNode> {
    
    private final DirectedGraphWeightFunction weightFunction;
    private final DirectedGraphNode node;
    private final LinkedPathNode previousLinkedPathNode;
    private double totalCost;
    
    public LinkedPathNode(DirectedGraphWeightFunction weightFunction,
                   DirectedGraphNode node) {
        this.weightFunction =
                Objects.requireNonNull(
                        weightFunction,
                        "The input weight function is null.");
        
        this.node = Objects.requireNonNull(node, "The input node is null.");
        this.totalCost = 0.0;
        this.previousLinkedPathNode = null;
    }
    
    LinkedPathNode(LinkedPathNode path, DirectedGraphNode node) {
        this.weightFunction = path.weightFunction;
        this.node = node;
        this.previousLinkedPathNode = path;
        this.totalCost += weightFunction.get(previousLinkedPathNode.node, node);
    }
    
    LinkedPathNode append(DirectedGraphNode node) {
        return new LinkedPathNode(this, node);
    }
    
    LinkedPathNode getPreviousLinkedPathNode() {
        return this.previousLinkedPathNode;
    }
    
    DirectedGraphNode getTailNode() {
        return this.node;
    }
    
    Path toPath() {
        List<DirectedGraphNode> path = new ArrayList<>();
        LinkedPathNode node = this;
        
        while (node != null) {
            path.add(node.node);
            node = node.previousLinkedPathNode;
        }
        
        Collections.<DirectedGraphNode>reverse(path);
        return new Path(path, this.weightFunction);
    }
    
    @Override
    public int compareTo(LinkedPathNode o) {
        return Double.compare(totalCost, o.totalCost);
    }
}
