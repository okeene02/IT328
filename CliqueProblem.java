/**
 * A class representing the clique problem.
 * Contains both the underlying graph and a value
 * for representing a k-clique problem.
 * 
 * Can be created from a {@link CNFProblem} and can create
 * solutions to the clique problem given a cover from a corresponding
 * vertex cover problem.
 */
class CliqueProblem {
    Graph graph;
    int k;

    // Convert a CNF into a graph for a clique problem
    /**
     * Converts a CNFProblem into a CliqueProblem
     * @param cnfProblem
     * The CNFProblem to be converted
     * @return
     * The CliqueProblem created
     */
    static CliqueProblem createFromCNFProblem(CNFProblem cnfProblem) {
        CliqueProblem ret = new CliqueProblem();
        // Create a graph with a vertex per literal
        ret.graph = new Graph(cnfProblem.clauseCount * 3);
        ret.k = cnfProblem.clauseCount;
        // Connect all logically consistent literals in separate clauses
        for (int i = 0; i < ret.graph.vertexCount; i++) {
            for (int j = 0; j < ret.graph.vertexCount; j++) {
                if (cnfProblem.cnf[i] != -cnfProblem.cnf[j] && i / 3 != j / 3) {
                    ret.graph.connect(i, j);
                }
            }
        }
        return ret;
    }


    /**
     * Converts a solution for the corresponding vertex
     * cover problem into a clique. This is just the
     * complement of the cover. Anything that was
     * included is excluded and vice versa.
     * @param cover
     * The cover to be converted
     * @return
     * The corresponding clique
     */
    static int[] createCliqueFromCover(int[] cover) {
        int[] clique = new int[cover.length];
        for (int i = 0; i < cover.length; i++) {
            clique[i] = 1 - cover[i];
        }
        return clique;
    }
}