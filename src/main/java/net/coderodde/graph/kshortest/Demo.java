package net.coderodde.graph.kshortest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.coderodde.graph.DirectedGraphNode;
import net.coderodde.graph.DirectedGraphWeightFunction;
import net.coderodde.graph.kshortest.impl.DefaultKShortestPathFinder;
import net.coderodde.graph.kshortest.impl.FasterDefaultKShortestPathFinder;

public class Demo {

    private static final int WARMUP_ITERATIONS = 5;
    private static final int BENCHMARK_ITERATIONS = 20;
    private static final int GRAPH_NODES = 5_000;
    private static final int GRAPH_ARCS = 50_000;
    private static final double MAX_WEIGHT = 1000.0;
    private static final int K = 25;
    
    public static void main(String[] args) {
        demo1();
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        List<DirectedGraphNode> graph = constructGraph(GRAPH_NODES,
                                                       GRAPH_ARCS,
                                                       random);
        DirectedGraphWeightFunction weightFunction = 
                new DirectedGraphWeightFunction();
        
        addArcWeights(graph, weightFunction, MAX_WEIGHT, random);
        System.out.println("=== Graph constructed! Warming up...     ===");
        warmup(random, graph, weightFunction, K);
        System.out.println("=== Warming up complete! Benchmarking... ===");
        benchmark(random, graph, weightFunction, K);
    }
    
    private static final List<DirectedGraphNode>
         constructGraph(int graphNodes,
                        int graphArcs,
                        Random random){
        List<DirectedGraphNode> graph = new ArrayList<>(graphNodes);
        
        for (int i = 0; i < graphNodes; i++) {
            graph.add(new DirectedGraphNode(i));
        }
        
        for (int i = 0; i < graphArcs; i++) {
            DirectedGraphNode tail = choose(graph, random);
            DirectedGraphNode head = choose(graph, random);
            tail.addChild(head);
        }
        
        return graph;
    }
         
    private static final void 
        addArcWeights(List<DirectedGraphNode> graph, 
                      DirectedGraphWeightFunction weightFunction,
                      double maxWeight,
                      Random random) {
        for (DirectedGraphNode tail : graph) {
            for (DirectedGraphNode head : tail.children()) {
                weightFunction.put(tail, head, random.nextDouble() * maxWeight);
            }
        }
    }
    
    private static void 
        warmup(Random random,
               List<DirectedGraphNode> graph,
               DirectedGraphWeightFunction weightFunction,
               int k) {
        run(false,
            WARMUP_ITERATIONS,
            random,
            graph,
            weightFunction,
            k);
    }
        
    private static void benchmark(Random random, 
                                  List<DirectedGraphNode> graph,
                                  DirectedGraphWeightFunction weightFunction,
                                  int k) {
        run(true,
            BENCHMARK_ITERATIONS,
            random,
            graph,
            weightFunction,
            k);
    }
    
    private static List<DirectedGraphNode> 
        randomNodes(List<DirectedGraphNode> graph, 
                    Random random, 
                    int size) {
        List<DirectedGraphNode> nodes = new ArrayList<>(size);
        
        for (int i = 0; i < size; i++) {
            nodes.add(choose(graph, random));
        }
        
        return nodes;
    }
    
    private static void run(boolean print,
                            int iterations,
                            Random random,
                            List<DirectedGraphNode> graph,
                            DirectedGraphWeightFunction weightFunction,
                            int k) {
        AbstractKShortestPathFinder finder1 = new DefaultKShortestPathFinder();
        AbstractKShortestPathFinder finder2 = 
                new FasterDefaultKShortestPathFinder();
        
        List<DirectedGraphNode> sourceNodes = randomNodes(graph,
                                                          random, 
                                                          iterations);
        
        List<DirectedGraphNode> targetNodes = randomNodes(graph,
                                                          random, 
                                                          iterations);
        
        List<List<Path>> results1 = new ArrayList<>(iterations);
        List<List<Path>> results2 = new ArrayList<>(iterations);
        
        long nanoTotalDuration1 = 0L;
        long nanoTotalDuration2 = 0L;
        
        for (int iter = 0; iter < iterations; iter++) {
            DirectedGraphNode sourceNode = sourceNodes.get(iter);
            DirectedGraphNode targetNode = targetNodes.get(iter);
            
            long startTime = System.nanoTime();
            List<Path> result1 = 
                    finder1.findShortestPaths(
                            sourceNode, 
                            targetNode, 
                            weightFunction, k);
            long endTime = System.nanoTime();
            long nanoDuration = endTime - startTime;
            nanoTotalDuration1 += nanoDuration;
            
            if (print) {
                System.out.println(finder1.getClass().getSimpleName() + " in " +
                                   (long)(1e-6 * nanoDuration) + 
                                   " milliseconds.");
            }
            
            nanoTotalDuration1 += nanoDuration;
            results1.add(result1);
        }
        
        for (int iter = 0; iter < iterations; iter++) {
            DirectedGraphNode sourceNode = sourceNodes.get(iter);
            DirectedGraphNode targetNode = targetNodes.get(iter);
            
            long startTime = System.nanoTime();
            List<Path> result2 = 
                    finder2.findShortestPaths(
                            sourceNode, 
                            targetNode, 
                            weightFunction, k);
            long endTime = System.nanoTime();
            long nanoDuration = endTime - startTime;
            nanoTotalDuration2 += nanoDuration;
            
            if (print) {
                System.out.println(finder2.getClass().getSimpleName() + " in " +
                                   (long)(1e-6 * nanoDuration) + 
                                   " milliseconds.");
            }
            
            results2.add(result2);
        }
        
        if (print) {
            System.out.println(
                    "Algorithms agree: " + results1.equals(results2));
            
            System.out.println(
                    finder1.getClass().getSimpleName() + " in\n" + 
                    (long)(1e-6 * nanoTotalDuration1) + " milliseconds.");
            
            System.out.println(finder2.getClass().getSimpleName() + " in\n" + 
                    (long)(1e-6 * nanoTotalDuration2) + " millieconds.");
        }
    }
    
    private static <T> T choose(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
    
    private static void demo1() {
        //   1   4
        //  / \ / \
        // 0   3   6
        //  \ / \ /
        //   2   5

        DirectedGraphNode a = new DirectedGraphNode(0); 
        DirectedGraphNode b = new DirectedGraphNode(1); 
        DirectedGraphNode c = new DirectedGraphNode(2); 
        DirectedGraphNode d = new DirectedGraphNode(3); 
        DirectedGraphNode e = new DirectedGraphNode(4); 
        DirectedGraphNode f = new DirectedGraphNode(5); 
        DirectedGraphNode g = new DirectedGraphNode(6); 

        // The edges above the line 0 - 6 have weight of 1.0.
        // The edges below the line 0 - 6 have weight of 2.0
        DirectedGraphWeightFunction weightFunction = 
                new DirectedGraphWeightFunction();

        a.addChild(b); weightFunction.put(a, b, 1);
        a.addChild(c); weightFunction.put(a, c, 2);
        b.addChild(d); weightFunction.put(b, d, 1);
        c.addChild(d); weightFunction.put(c, d, 2);

        d.addChild(e); weightFunction.put(d, e, 1);
        d.addChild(f); weightFunction.put(d, f, 2);
        e.addChild(g); weightFunction.put(e, g, 1);
        f.addChild(g); weightFunction.put(f, g, 2);

        List<Path> paths = new FasterDefaultKShortestPathFinder()
                .findShortestPaths(a, g, weightFunction, 5);

        for (Path path : paths) {
            System.out.println(Arrays.toString(path.getNodeList().toArray()));
        }
    }
}