package ssp;

public class SSPMain {
    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();
        Ssp ssp = new Ssp();
        ssp.processCommandLineArguments(args);  /* Get input file name, source vertex index and destination vertex index from the argument list */
        final long midTime1 = System.currentTimeMillis();
        System.out.println("Time to Process Command Line Args = " + (midTime1 - startTime) + "ms");
        ssp.processFile();  /* Read the input file and store all the vertex and edge information */
        final long midTime2 = System.currentTimeMillis();
        System.out.println("Time to Process File = " + (midTime2 - midTime1) + "ms");
        
        if(ssp.destNode >= ssp.numVertices || ssp.destNode < 0 || ssp.sourceNode >= ssp.numVertices || ssp.sourceNode < 0) {
            System.out.println("Specify source and destination properly. Exiting, try again");
            System.exit(1);
        }
        /* Get the source and destination vertex object */
        Vertex sourceVertex = ssp.vertices[ssp.sourceNode];
        Vertex destVertex = ssp.vertices[ssp.destNode];
        if(sourceVertex == null || destVertex == null) {
            System.out.println("Inside main ... Source or Dest Vertex is NULL ... Exiting");
            System.exit(1);
        }
        /* Add nodes to the fibonacci heap. Node corrosponding to source vertex has 'dist' = 0, others have 'dist' = Integer.MAX_VALUE */
        final long midTime3 = System.currentTimeMillis();
        System.out.println("Time before start = " + (midTime3 - midTime2) + "ms");
        
        ssp.runDijkstra();
        final long midTime4 = System.currentTimeMillis();
        System.out.println("Time to Run Dijkstra = " + (midTime4 - midTime3) + "ms");
        if(ssp.vertices[ssp.destNode].visited == true) {
            int net_weight = ssp.vertices[ssp.destNode].node.dist;
            System.out.println(net_weight);
            ssp.printPath(ssp.vertices[ssp.destNode]);
            System.out.print("\n");
        }
        else {
            System.out.println("No Path Found");
        }
        final long endTime = System.currentTimeMillis();
        System.out.println("Time to end: " + (endTime - midTime4) + "ms"); /* Execution time of the algorithm */
    }
}
