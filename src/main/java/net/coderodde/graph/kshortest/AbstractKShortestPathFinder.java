package net.coderodde.graph.kshortest;

import java.util.List;
import net.coderodde.graph.DirectedGraphNode;
import net.coderodde.graph.DirectedGraphWeightFunction;

/**
 * This abstract class defines the API for {@code k}-shortest path algorithms.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jan 9, 2016)
 */
public abstract class AbstractKShortestPathFinder {

    public abstract List<Path>
         findShortestPaths(DirectedGraphNode source,
                           DirectedGraphNode target,
                           DirectedGraphWeightFunction weightFunction,
                           int k);

    protected void checkK(int k) {
        if (k < 1) {
            throw new IllegalArgumentException(
                    "The value of k is too small: " + k + ", " +
                    "should be at least 1.");
        }
    }
}