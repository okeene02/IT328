// Basic adjacency matrix graph representation. Vertices are indices into the matrix.
public class Graph {
    public int vertexCount;
    public int edgeCount;
    public int[][] adjacencyMatrix;

    Graph(int vertexCount) {
        this.vertexCount = vertexCount;
        this.edgeCount = 0;
        adjacencyMatrix = new int[vertexCount][vertexCount];

        // Every vertex should be connected to itself
        for (int i = 0; i < vertexCount; i++) {
            adjacencyMatrix[i][i] = 1;
        }
    }

    Graph(int vertexCount, int[][] adjacencyMatrix) {
        this.vertexCount = vertexCount;
        this.adjacencyMatrix = adjacencyMatrix.clone();

        for (int i = 0; i < vertexCount; i++) {
            for (int j = i+1; j < vertexCount; j++) {
                if (this.adjacencyMatrix[i][j] == 1) {
                    this.edgeCount++;
                }
            }
        }
    }

    // return String representing size and adjacency matrix 
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

    // Connects two vertices in the graph
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

    // Creates the complement of the graph.
    // Ammounts to swapping ones and zeroes in the adjacency matrix, 
    // except on the diagonal. 
    public Graph createComplement() {
        Graph complement = new Graph(this.vertexCount);
        for (int i = 0; i < complement.vertexCount; i++) {
            for (int j = 0; j < complement.vertexCount; j++) {
                if (i != j)
                    complement.adjacencyMatrix[i][j] = 1 - this.adjacencyMatrix[i][j];
            }
        }
        return complement;
    }

    // Given a cover, check if it covers this graph
    // Loops over every edge and checks if it's covered
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