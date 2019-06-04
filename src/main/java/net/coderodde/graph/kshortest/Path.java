package net.coderodde.graph.kshortest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.coderodde.graph.DirectedGraphNode;
import net.coderodde.graph.DirectedGraphWeightFunction;

/**
 * This class implements a graph path.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jan 9, 2016)
 */
public class Path implements Comparable<Path> {

    private final DirectedGraphWeightFunction weightFunction;
    private final List<DirectedGraphNode> nodeList = new ArrayList<>();
    private double totalCost;

    public Path(DirectedGraphWeightFunction weightFunction,
                DirectedGraphNode source) {
        Objects.requireNonNull(weightFunction, 
                               "The input weight function is null.");
        Objects.requireNonNull(source, "The input source node is null.");

        this.weightFunction = weightFunction;
        nodeList.add(source);
    }

    private Path(Path path, DirectedGraphNode node) {
        this.weightFunction = path.weightFunction;
        this.nodeList.addAll(path.nodeList);
        this.nodeList.add(node);

        int listLength = nodeList.size();

        this.totalCost += weightFunction.get(nodeList.get(listLength - 2),
                                             nodeList.get(listLength - 1));
    }
    
    public Path(List<DirectedGraphNode> nodeList, 
                DirectedGraphWeightFunction weightFunction) {
        this.weightFunction = weightFunction;
        this.nodeList.addAll(new ArrayList<>(nodeList));
    }

    public Path append(DirectedGraphNode node) {
        return new Path(this, node);
    }

    public DirectedGraphNode getEndNode() {
        return nodeList.get(nodeList.size() - 1);
    }

    /**
     * Returns the number of nodes in this path.
     * 
     * @return the path length in nodes.
     */
    public int size() {
        return nodeList.size();
    }

    public List<DirectedGraphNode> getNodeList() {
        return Collections.<DirectedGraphNode>unmodifiableList(nodeList);
    }

    public double pathCost() {
        return totalCost;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (o == this) {
            return true;
        }
        
        if (!getClass().equals(o.getClass())) {
            return false;
        }
        
        Path p = (Path) o;
        
        return p.getNodeList().equals(getNodeList());
    }
    
    @Override
    public int compareTo(Path o) {
        return Double.compare(totalCost, o.totalCost);
    }
}