/**
 * A class representing the vertex cover problem.
 * Contains both the underlying graph and a value k
 * for solving a k-vertex cover problem.
 * 
 * Can be created from a {@link CliqueProblem} and
 * has methods for finding either a minimum cover or a k-cover
 */
class VertexCoverProblem {
    Graph graph;
    int k;

    /**
     * Creates a vertex cover problem from a clique problem.
     * The conversion consists of finding the complement graph
     * as well as the "complement" k, meaning vertexCount - k.
     * @param cliqueProblem
     * The clique problem to be converted
     * @return
     * The created vertex cover problem.
     */
    static VertexCoverProblem createFromCliqueProblem(CliqueProblem cliqueProblem) {
        VertexCoverProblem vertexCoverProblem = new VertexCoverProblem();
        vertexCoverProblem.graph = cliqueProblem.graph.createComplement();
        vertexCoverProblem.k = cliqueProblem.graph.vertexCount - cliqueProblem.k;
        return vertexCoverProblem;
    }


    /**
     * Finds a k-vertex cover using backtracking.
     * Will always return a k-vertex cover, even if smaller
     * covers are possible. Not optimized, but reasonably fast 
     * @return
     * A cover as described in {@link Graph#isCoveredBy(int[])}
     */
    public int[] findKVertexCover() {
        int[] cover = new int[this.graph.vertexCount];
        if (findKVertexCoverHelper(cover, 0, 0))
            return cover;
        return null;
    }

    // Recursive helper function. For each vertex, tries to find a vertex
    // cover with and without that vertex.
    /**
     * Recursive helper function for {@link #findKVertexCover()}
     * @param wipCover
     * A work in progress cover shared by all recursive calls
     * @param index
     * The vertex currently being processed
     * @param count
     * The number of vertices included in the cover so far
     * @return
     * True or false, depending on whether a cover was found
     */
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
    /**
     * Finds a minimum vertex cover using backtracking. More optimized than
     * {@link #findKVertexCover()}, but likely slower due to the larger number
     * of candidates. 
     * @return
     * A cover as described in {@link Graph#isCoveredBy(int[])}
     */
    public int[] findMinVertexCover() {
        int[] cover = findMinVertexCoverHelper(new int[this.graph.vertexCount]);
        // Convert to 0-excluded 1-included format
        for (int i = 0; i < cover.length; i++) {
            if (cover[i] < 0)
                cover[i] = 0;
        }
        return cover;
    }

    /**
     * Recursive helper function for {@link #findMinVertexCover()}.
     * Vertices intentionally excluded are marked -1
     * Vertices included are marked 1
     * Vertices not yet considered are marked 0
     * @param cover
     * An incomplete cover
     * @return
     * The input cover after being completed
     */
    int[] findMinVertexCoverHelper(int[] cover) {
        // If the cover works, return it
        if (graph.isCoveredBy(cover))
            return cover;

        // Find the next unexamined vertex
        int nextVertex = 0;
        for (int i = 0; i < cover.length; i++) {
            if (cover[i] == 0) {
                nextVertex = i;
                break;
            }
        }

        // Create a copy of the current cover and complete
        // it with the current vertex included
        int[] coverWith = cover.clone();
        coverWith[nextVertex] = 1;
        coverWith = findMinVertexCoverHelper(coverWith);

        // Create a copy of the current cover, excluding the 
        // current vertex and including its neighbors
        int[] coverWithout = cover.clone();
        for (int i = 0; i < graph.vertexCount; i++) {
            if (graph.adjacencyMatrix[nextVertex][i] == 1)
                coverWithout[i] = 1;
        }
        coverWithout[nextVertex] = -1;
        coverWithout = findMinVertexCoverHelper(coverWithout);

        // Count the number of vertices in each cover

        int withCount = 0;
        int withoutCount = 0;

        for (int i = 0; i < graph.vertexCount; i++) {
            if (coverWith[i] == 1)
                withCount++;
            if (coverWithout[i] == 1)
                withoutCount++;
        }

        // Return the smaller cover

        if (withCount > withoutCount)
            return coverWithout;
        else
            return coverWith;
            
    }

}
