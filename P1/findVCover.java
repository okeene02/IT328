import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A program to solve a number of vertex cover
 * problems from a file of graphs. Solves each
 * problem directly, without reductions
 */
public class findVCover {
    public static void main(String[] args) throws FileNotFoundException {
        // Check args
        if (args.length != 1) {
            System.out.println("Usage: findClique [filename]");
            System.exit(0);
        }

        // Input file
        String filename = args[0];

        // Create array of graphs (represented by adjacency matrix)
        VertexCoverProblem[] graphArray = readGraphFile(filename);

        // Print header
        System.out.printf("* A Minimum Vertex Cover of every graph in %s *\n", filename);
        System.out.println("(|V|,|E|) (size, ms used) Vertex Cover");

        for(int cliqueNum = 0; cliqueNum < graphArray.length; cliqueNum++){
            // Results variables
            VertexCoverProblem vertexCoverProblem = graphArray[cliqueNum];
            
            long start_time = System.currentTimeMillis();
            
            int[] cover = vertexCoverProblem.findMinVertexCover();

            int coverSize = 0;
            for (int i = 0; i < cover.length; i++) {
                if (cover[i] == 1)
                    coverSize++;
            }

            // Calculate time spent
            long total_time = System.currentTimeMillis() - start_time;

            // Print information
            System.out.printf("G%d (%d, %d) (size = %d ms=%d) %s\n", 
                cliqueNum + 1, 
                vertexCoverProblem.graph.vertexCount, 
                vertexCoverProblem.graph.edgeCount, 
                coverSize,
                total_time, 
                coverToString(cover));
        }
    }

    /**
     * Converts a clique into a string representation. 
     * Ex. [0,1,0,1] is converted to "{1,3}"
     * @param clique
     * The clique to convert
     * @return
     * The string representation of the clique
     */
    public static String coverToString(int[] clique) {
        // A dynamic array to collect all of the included vertices
        ArrayList<Integer> vertices = new ArrayList<Integer>();

        // Place every included vertex into the array
        for (int i = 0; i < clique.length; i++) {
            if (clique[i] == 1)
                vertices.add(i);
        }

        // Format the array
        String ret = "{";
        for (int i = 0; i < vertices.size() - 1; i++) {
            ret += vertices.get(i) + ",";
        }

        ret += vertices.get(vertices.size() - 1);

        return ret + "}";
    }
    /**
     * 
     * Creates an array of VertexCoverProblem's from the 
     * contents of a file of graphs
     * @param filename
     * The file to read from
     * @return
     * An array of CliqueProblem's read from the file
     * @throws FileNotFoundException
     */
    public static VertexCoverProblem[] readGraphFile(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new FileReader(filename));

        // List of graphs. Total number of lines is unknown, so use dynamic array.
        ArrayList<VertexCoverProblem> graphList = new ArrayList<VertexCoverProblem>();

        // Iterate over file lines and add each graph to the list
        while (in.hasNextLine()) {
            int size = in.nextInt();

            if(size == 0) break;
            VertexCoverProblem vertexCoverProblem = new VertexCoverProblem();
            vertexCoverProblem.graph = new Graph(size, in);
            graphList.add(vertexCoverProblem);
        }

        in.close();

        // Convert to static array
        return graphList.toArray(new VertexCoverProblem[0]);
    }
}
