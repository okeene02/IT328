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

    // Finds a minimum vertex cover of the graph using backtracking
    public int[] findMinVertexCover() {
        return findMinVertexCoverHelper(new int[this.graph.vertexCount]);
    }

    int[] findMinVertexCoverHelper(int[] cover) {
        if (graph.isCoveredBy(cover))
            return cover;

        int nextVertex = 0;
        for (int i = 0; i < cover.length; i++) {
            if (cover[i] == 0) {
                nextVertex = i;
                break;
            }
        }

        int[] coverWith = cover.clone();
        coverWith[nextVertex] = 1;
        coverWith = findMinVertexCoverHelper(coverWith);

        int[] coverWithout = cover.clone();
        for (int i = 0; i < graph.vertexCount; i++) {
            if (graph.adjacencyMatrix[nextVertex][i] == 1)
                coverWithout[i] = 1;
        }
        coverWithout[nextVertex] = -1;
        coverWithout = findMinVertexCoverHelper(coverWithout);

        int withCount = 0;
        int withoutCount = 0;

        for (int i = 0; i < graph.vertexCount; i++) {
            if (coverWith[i] == 1)
                withCount++;
            if (coverWithout[i] == 1)
                withoutCount++;
        }

        if (withCount > withoutCount)
            return coverWithout;
        else
            return coverWith;
            
    }

}
