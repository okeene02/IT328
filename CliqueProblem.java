class CliqueProblem {
    Graph graph;
    int k;

    // Convert a CNF into a graph for a clique problem
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


    static int[] createCliqueFromCover(int[] cover) {
        int[] clique = new int[cover.length];
        for (int i = 0; i < cover.length; i++) {
            clique[i] = 1 - cover[i];
        }
        return clique;
    }
}