package ssp;

import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

/*  Edge objects stored in each Vertex object    */
class Edges {
    int destVert;   /* Destination Vertex index of the edge */
    int weight;     /* Weight of the edge */
    Edges next;     /* Next Edge of the vertex this object belongs to. Used to make a linked list of Edges */
    
    Edges(int dv, int w) {
        this.destVert = dv;
        this.weight = w;
        this.next = null;
    }
}

/*  Vertex object representing a vertex of the graph    */
class Vertex {
    Node node;  /* Fibonacci Heap node corrosponding to this vertex */
    Edges edges;    /* List of undirected Edges associated with this vertex */
    Vertex prev;    /* Previous vertex in the path from source vertex to this vertex */
    Boolean visited;    /* A boolean flag to indicate if this vertex has been visited or not. If true, this vertex will not be a part of any removeMin or reduceKey operation.  */
    
    Vertex() {
        this.node = null;
        this.edges = null;
        this.prev = null;
        this.visited = false;
    }
    
    /*  func addEdge: used to add edges to a vertex */
    public void addEdge(int dv, int w) {
        Edges e = new Edges(dv, w);
        if(this.edges == null)
            this.edges = e;
        else {
            e.next = this.edges;
            this.edges = e;
        }
    }
}

/*  SSP public class, contains the function main()  */
class Ssp {
    FHeap mainHeap; /* The Fibonacci Heap main object */
    String inputFile;   /* Input file name, mentioned in argument #1   */
    int sourceNode = -1;    /* Source vertex index, mentioned in argument #2 */
    int destNode = -1;  /* Destination vertex index, mentioned in argument #3 */
    int numVertices = 0;    /* Number of vertices in the graph  */
    int numEdges = 0;   /* Number of edges in the graph */
    Vertex[] vertices;  /* Array of all the vertices in the graph */
    Map<Integer, Map<Integer, String>> shortestpath;
    
    protected Ssp() {
        this.mainHeap = new FHeap();
    }
    
    protected void runDijkstra() {
        
        for(int i=0;i<this.numVertices;i++) {
            int data = Integer.MAX_VALUE;
            if(i == this.sourceNode)
                data = 0;
            this.vertices[i].node = this.mainHeap.addNode(data);
            this.vertices[i].node.vertex = i;
            this.vertices[i].prev = null;
            this.vertices[i].visited = false;
        }
        
        while(true) {
            Node currentNode = this.mainHeap.removeMin(); /* Remove the node which has minimum dist value */
            
            /* Check if there is no nodes left in the heap or there is no path from the current vertex. If true, 
                the algorithm terminates with no path from source vertex to destination vertex. */
            if(currentNode == null || currentNode.dist == Integer.MAX_VALUE) {
                System.out.println("Algorithm Ended ... ");
                break;
            }
            else {
                this.vertices[currentNode.vertex].visited = true; /* set the current vertex as visited */
                if(currentNode.vertex == this.destNode) {
                    /* current vextex = destination vertex, shortest path has been found */
                    //int net_weight = ssp.vertices[currentNode.vertex].node.dist; /* dist value of current node will give the net weight of the path */
                    //System.out.println(net_weight);
                    //Vertex temp = ssp.vertices[currentNode.vertex]; /* Current vertex */
                    //ssp.printPath(temp); /* This is a recursive function, which ends at the source vertex, and starts printing vertex indices */
                    //System.out.print("\n");
                    break;
                }
                //else {
                    Edges e = this.vertices[currentNode.vertex].edges; /* Linked list of edges of the vertex */
                    while(e != null) {
                        /* Update dist value of all neighbor vertices, provided the vertices have not been visited already and don't 
                            already have a less dist value */
                        if((this.vertices[e.destVert].visited == false) && ((currentNode.dist + e.weight) < this.vertices[e.destVert].node.dist)) {
                            this.mainHeap.reduceKey(this.vertices[e.destVert].node, (currentNode.dist + e.weight));
                            this.vertices[e.destVert].prev = this.vertices[currentNode.vertex];
                        }
                        e = e.next;
                    }
                //}
            }
        }
    }
    /* func printPath: Recursively go to the source vertex of the path, and start printing indices of the vertices 
        in the path from source to destination */
    protected void printPath(Vertex temp) {
        if(temp != null) {
            this.printPath(temp.prev);
            System.out.print(temp.node.vertex + " ");
        }
    }
    
    /* func processFile: Store vertices and edges information from the input file */
    protected void processFile() {
        StringTokenizer st;
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.inputFile));
            //StreamTokenizer s = new StreamTokenizer(br);
            //s.resetSyntax();
            //s.parseNumbers();

            //int tok;


            String lineOne = br.readLine(); /* First line of the file, contains vertex count and edges count */
            st = new StringTokenizer(lineOne, " ");
            //s.nextToken();
            this.numVertices = Integer.parseInt(st.nextToken().trim());
            //s.nextToken();
            //s.nextToken();
            this.numEdges = Integer.parseInt(st.nextToken().trim());
            
            /* Initialize vertex array */
            this.vertices = new Vertex[this.numVertices]; 
            for(int i=0;i<this.numVertices;i++) {
                this.vertices[i] = new Vertex();
            }
            
            String inputLines;
            while((inputLines = br.readLine()) != null) {
                inputLines = inputLines.trim();
                if(!inputLines.isEmpty()) { /* Avoid empty lines in the input file */
                    st = new StringTokenizer(inputLines, " ");
                    //s.nextToken();
                    int sv = Integer.parseInt(st.nextToken().trim()); /* Source vertex index of the edge */
                    //s.nextToken();
                    //s.nextToken();
                    int dv = Integer.parseInt(st.nextToken().trim()); /* Destination vertex index of the edge */
                    //s.nextToken();
                    //s.nextToken();
                    int w = Integer.parseInt(st.nextToken().trim()); /* weight of the edge */
                    this.vertices[sv].addEdge(dv, w); /* add edge information to source vertex */
                    this.vertices[dv].addEdge(sv, w); /* add edge information to destination vertex too since it is a undirected graph */
                }

            }
            
        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found ... Exiting");
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("Error in File Format ... Exiting");
            System.exit(1);
        }
    }
    
    /* func processCommandLineArguments: Get input file name, source vertex index and destination vertex index from argument list */
    protected void processCommandLineArguments(String[] args) {
        if(args.length != 3) {
            System.out.println("Arguments not specified properly ....");
            System.out.println("Format: java -jar ssp.jar <input file name> <source vertex index> <destination vertex index>");
            System.exit(1);
        }
        this.inputFile = args[0];
        this.sourceNode = Integer.parseInt(args[1]);
        this.destNode = Integer.parseInt(args[2]);
        //System.out.println("ARGS ... Length: " + args.length + " FileName: " + this.inputFile + " SourceNode: " + this.sourceNode + " Dest Node: " + this.destNode);
    }
    
}
