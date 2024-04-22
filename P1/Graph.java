import java.util.Scanner;

/**
 * Basic undirected graph class.
 * Represents a graph using an adjacency matrix.
 * Vertices do not have names and are represented by their index in the matrix.
 * Provides some helper functions like createComplement and isCoveredBy.
 */
public class Graph {
    public int vertexCount;
    public int edgeCount;
    public int[][] adjacencyMatrix;

    /**
     * Create a graph with the provided vertex count.
     * Initializes the adjacency matrix with each vertex
     * connected to itself.
     * 
     * @param vertexCount
     * The number of vertices
     */
    Graph(int vertexCount) {
        this.vertexCount = vertexCount;
        this.edgeCount = 0;
        adjacencyMatrix = new int[vertexCount][vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            adjacencyMatrix[i][i] = 1;
        }
    }

    /**
     * Creates a graph with the given vertex count and adjacency matrix.
     * @param vertexCount
     * The number of vertices
     * @param adjacencyMatrix
     * Adjacency matrix to be copied
     */
    Graph(int vertexCount, int[][] adjacencyMatrix) {
        this.vertexCount = vertexCount;
        this.adjacencyMatrix = adjacencyMatrix.clone();

        // Both copy matrix and count edges
        for (int i = 0; i < vertexCount; i++) {
            for (int j = i+1; j < vertexCount; j++) {
                if (this.adjacencyMatrix[i][j] == 1) {
                    this.edgeCount++;
                }
            }
        }
    }

    /**
     * Creates a graph by reading an adjacency matrix from the
     * provided scanner.
     * @param vertexCount
     * The number of vertices
     * @param scanner
     * The scanner to read from
     */
    Graph(int vertexCount, Scanner scanner) {
        this.vertexCount = vertexCount;
        // Use 2d arrays to make adjacency matrix
        this.adjacencyMatrix = new int[this.vertexCount][this.vertexCount];
        for(int i = 0; i < this.vertexCount; i++){
            for(int j = 0; j < this.vertexCount; j++){
                adjacencyMatrix[i][j] = scanner.nextInt();
                this.edgeCount += adjacencyMatrix[i][j];
            }
        }
        this.edgeCount = (this.edgeCount - vertexCount) / 2;
    }

    /**
     * Creates a printable representation of a graph.
     */
    @Override
    public String toString(){
        String ret = this.vertexCount + "\n";
        for(int i = 0; i < this.vertexCount; i++){
            for(int j = 0; j < this.vertexCount; j++){
                ret += this.adjacencyMatrix[i][j] + " ";
            }
            ret += "\n";
        }
        return ret;
    }

    /**
     * Connects two vertices in the graph.
     * @param vertexA
     * The index of the first vertex
     * @param vertexB
     * The index of the second vertex
     */
    public void connect(int vertexA, int vertexB) {
        if (this.adjacencyMatrix[vertexA][vertexB] == 1) {
            return;
        }
        else {
            // Undirected graph, so connections are symmetric
            this.adjacencyMatrix[vertexA][vertexB] = 1;
            this.adjacencyMatrix[vertexB][vertexA] = 1;
            // Self-connected vertices don't add to edge count
            if (vertexA != vertexB)
                this.edgeCount++;
        }
    }

    /**
     * Create the complement of this graph.
     * Connects any disconnected vertices and disconnects
     * any connected vertices.
     * @return
     * The complement graph
     */
    public Graph createComplement() {
        Graph complement = new Graph(this.vertexCount);
        for (int i = 0; i < complement.vertexCount; i++) {
            for (int j = 0; j < complement.vertexCount; j++) {
                // Changes zeros to ones, but keeps vertices connected to themselves
                if (i != j)
                    complement.adjacencyMatrix[i][j] = 1 - this.adjacencyMatrix[i][j];
            }
        }
        return complement;
    }

    /**
     * Given a cover, check if it is a valid vertex cover.
     * @param cover
     * An array the size of this.vertexCount where cover[i] == 0
     * means that vertex i is excluded and cover[i] == 1 means that
     * vertex i is included.
     * @return
     * Whether the graph is covered
     */
    public boolean isCoveredBy(int[] cover) {
        for (int i = 0; i < this.vertexCount; i++) {
            for (int j = i+1; j < this.vertexCount; j++) {
                if (this.adjacencyMatrix[i][j] == 1 && cover[i] == 0 && cover[j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }


}
