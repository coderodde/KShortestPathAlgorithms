package net.coderodde.graph.kshortest.impl;

import java.util.ArrayList;
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
 * </a>.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jan 9, 2016)
 */
public class DefaultKShortestPathFinder extends AbstractKShortestPathFinder {

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

        List<Path> paths = new ArrayList<>(k);
        Map<DirectedGraphNode, Integer> countMap = new HashMap<>();
        Queue<Path> HEAP = new PriorityQueue<>();

        HEAP.add(new Path(weightFunction, source));

        while (!HEAP.isEmpty() && countMap.getOrDefault(target, 0) < k) {
            Path currentPath = HEAP.remove();
            DirectedGraphNode endNode = currentPath.getEndNode();

            countMap.put(endNode, countMap.getOrDefault(endNode, 0) + 1);

            if (endNode.equals(target)) {
                paths.add(currentPath);
            }

            if (countMap.get(endNode) <= k) {
                for (DirectedGraphNode child : endNode.children()) {
                    Path path = currentPath.append(child);
                    HEAP.add(path);
                }
            }
        }

        return paths;
    }
}