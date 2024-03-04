import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class findClique {
    public static void main(String[] args) throws FileNotFoundException {
        // Check args
        if (args.length != 1) {
            System.out.println("Usage: findClique [filename]");
            System.exit(0);
        }

        // Input file
        String filename = args[0];
        // Create array of graphs (represented by adjacency matrix)
        adjacencyMatrix[] graphArray = readGraphFile(filename);

        System.out.printf("* Max Cliques in %s (reduced to K-Vertex Cover) *\n", filename);
        System.out.println("(|V|,|E|) (size, ms used) Cliques");
        for(int graphNum = 0; graphNum < graphArray.length; graphNum++){
            adjacencyMatrix thisMatrix = graphArray[graphNum];
            int graphSize = thisMatrix.size, edges = thisMatrix.edges;
            long start_time = System.currentTimeMillis();
            int cliqueSize = 0;
            String cliques = "";
            
            adjacencyMatrix complement = adjacencyMatrix.createComplement(graphArray[graphNum]);

            // TODO hand off new vertex cover problem (complement graph) to part C

            System.out.printf("G%d ( %d, %d) (size = %d ms=%d) {%s}\n", 
                graphNum + 1, 
                graphSize, 
                edges, 
                cliqueSize,
                System.currentTimeMillis() - start_time, 
                cliques);
        }
    }

    static adjacencyMatrix[] readGraphFile(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new FileReader(filename));

        // List of graphs. Total number of lines is unknown, so use dynamic array.
        ArrayList<adjacencyMatrix> graphList = new ArrayList<adjacencyMatrix>();

        // Iterate over file lines and add each graph to the list
        while (in.hasNextLine()) {
            int size = in.nextInt();
            int edges = 0;
            if(size == 0) break;
            // Use 2d arrays to make adjacency matrix
            int[][] this2Darray = new int[size][size];
            for(int i = 0; i < size; i++){
                for(int j = 0; j < size; j++){
                    int val = in.nextInt();
                    if(val == 1 && i > j) edges++;
                    this2Darray[i][j] = val;
                }
            }
            graphList.add(new adjacencyMatrix(this2Darray, edges));
        }

        in.close();

        // Convert to static array
        return graphList.toArray(new adjacencyMatrix[0]);
    }

    static class adjacencyMatrix{
        int size, edges;
        int[][] this2Darray;

        // constructor for adjacency matrix
        public adjacencyMatrix(int[][] some2Darray, int someEdges){
            this2Darray = some2Darray;
            size = some2Darray.length;
            edges = someEdges;
        }

        // return String representing size and adjacency matrix 
        public String toString(){
            String ret = size + "\n";
            for(int i = 0; i < size; i++){
                for(int j = 0; j < size; j++){
                    ret += this2Darray[i][j] + " ";
                }
                ret += "\n";
            }
            return ret;
        }

        // create the complement of a graph
        public static adjacencyMatrix createComplement(adjacencyMatrix thisMatrix){
            int size = thisMatrix.size;
            int edges = 0;
            int[][] return2Darray = new int[size][size];

            for(int i = 0; i < size; i++){
                for(int j = 0; j < size; j++){
                    if(i == j) return2Darray[i][j] = 1;
                    else{
                        int val = thisMatrix.this2Darray[i][j];
                        if(val == 0){
                            edges++;
                            return2Darray[i][j] = 1;
                        } else {
                            return2Darray[i][j] = 0;
                        }
                    } 
                }
            }

            return new adjacencyMatrix(return2Darray, edges);
        }
    }
}
