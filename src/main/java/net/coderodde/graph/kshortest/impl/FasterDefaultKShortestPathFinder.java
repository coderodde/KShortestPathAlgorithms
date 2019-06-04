package net.coderodde.graph.kshortest.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import net.coderodde.graph.DirectedGraphNode;
import net.coderodde.graph.DirectedGraphWeightFunction;
import net.coderodde.graph.kshortest.AbstractKShortestPathFinder;
import net.coderodde.graph.kshortest.Path;

/**
 * This class implements a rather simple k-shortest path algorithm from
 * <a href="https://en.wikipedia.org/wiki/K_shortest_path_routing#Algorithm">
 * Wikipedia
 * </a>. This version improves 
 * {@link net.coderodde.graph.kshortest.impl.DefaultKShortestPathFinder} via 
 * using linked lists of nodes as the paths.
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jun 4, 2019)
 */
public class FasterDefaultKShortestPathFinder 
        extends AbstractKShortestPathFinder {

    @Override
    public List<Path>
         findShortestPaths(DirectedGraphNode source, 
                           DirectedGraphNode target,
                           DirectedGraphWeightFunction weightFunction, 
                           int k) {
        Objects.requireNonNull(source, "The source node is null.");
        Objects.requireNonNull(target, "The target node is null.");
        Objects.requireNonNull(weightFunction,
                               "The weight function is null.");
        checkK(k);

        List<LinkedPathNode> linkedPathTails = new ArrayList<>(k);
        Map<DirectedGraphNode, Integer> countMap = new HashMap<>();
        Queue<LinkedPathNode> HEAP = new PriorityQueue<>();
        
        HEAP.add(new LinkedPathNode(weightFunction, source));

        while (!HEAP.isEmpty() && countMap.getOrDefault(target, 0) < k) {
            LinkedPathNode currentPath = HEAP.remove();
            DirectedGraphNode endNode = currentPath.getTailNode();
            countMap.put(endNode, countMap.getOrDefault(endNode, 0) + 1);

            if (endNode.equals(target)) {
                linkedPathTails.add(currentPath);
            }

            if (countMap.get(endNode) <= k) {
                for (DirectedGraphNode child : endNode.children()) {
                    HEAP.add(currentPath.append(child));
                }
            }
        }

        return convertListOfLinkedPathsToPaths(linkedPathTails);
    }
         
    private static final List<Path> 
        convertListOfLinkedPathsToPaths(
                List<LinkedPathNode> linkedPathTails) {
        List<Path> paths = new ArrayList<>(linkedPathTails.size());
        
        for (LinkedPathNode linkedPathNode : linkedPathTails) {
            paths.add(linkedPathNode.toPath());
        }
        
        return paths;
    }
}
