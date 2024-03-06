class VertexCoverProblem {
    Graph graph;
    int k;

    // Converts a clique problem graph into a vertex cover problem graph.
    // Which amounts to just taking the complement graph
    static VertexCoverProblem createFromCliqueProblem(CliqueProblem cliqueProblem) {
        VertexCoverProblem vertexCoverProblem = new VertexCoverProblem();
        vertexCoverProblem.graph = cliqueProblem.graph.createComplement();
        vertexCoverProblem.k = cliqueProblem.graph.vertexCount - cliqueProblem.k;
        return vertexCoverProblem;
    }


    // Finds a k-vertex cover of the graph using backtracking
    // Size of cover will always be exactly k, even if less is possible
    public int[] findKVertexCover() {
        int[] cover = new int[this.graph.vertexCount];
        if (findKVertexCoverHelper(cover, 0, 0))
            return cover;
        return null;
    }

    // Recursive helper function. For each vertex, tries to find a vertex
    // cover with and without that vertex.
    private boolean findKVertexCoverHelper(int[] wipCover, int index, int count) {
        // Check if the current cover is valid
        if (count == k && this.graph.isCoveredBy(wipCover))
            return true;

        // Check if the current cover is invalid
        if (index == wipCover.length || count >= k)
            return false;

        // Keep looking recursively:
        
        // Try including the vertex
        wipCover[index] = 1;
        if (findKVertexCoverHelper(wipCover, index+1, count+1))
            return true;

        // Try excluding the vertex
        wipCover[index] = 0;
        if (findKVertexCoverHelper(wipCover, index+1, count))
            return true;

        return false;
    }

    int[] currentMinCover;
    int currentMinCoverSize;

    // Finds a minimum vertex cover of the graph using backtracking
    public int[] findMinVertexCover() {
        currentMinCoverSize = this.graph.vertexCount;
        findMinVertexCoverHelper(new int[this.graph.vertexCount], 0, 0);
        return currentMinCover;
    }

    private void findMinVertexCoverHelper(int[] wipCover, int wipCoverSize, int index) {
        if (wipCoverSize > currentMinCoverSize)
            return;
        if (graph.isCoveredBy(wipCover)) {
            if (wipCoverSize < currentMinCoverSize) {
                currentMinCover = wipCover.clone();
                currentMinCoverSize = wipCoverSize;
            }
            return;
        }

        if (index == wipCover.length) {
            return;
        }

        wipCover[index] = 1;
        findMinVertexCoverHelper(wipCover, wipCoverSize + 1, index + 1);
        wipCover[index] = 0;
        findMinVertexCoverHelper(wipCover, wipCoverSize, index+1);
    }
}
