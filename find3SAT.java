import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

class find3SAT {
    public static void main(String[] args) throws FileNotFoundException {
        // Make sure arguments are correct
        if (args.length != 1) {
            System.out.println("Usage: find3SAT [filename]");
            System.exit(0);
        }

        // Get the input file
        String filename = args[0];
        CNFProblem[] cnfArray = readCNFFile(filename);

        // Print the header information
        System.out.println("* Solve 3CNF in " + filename + ": (reduced to k-clique) *\n" + 
                           "X means can be either T or F\n");

        // Loop over each CNF
        for (int cnfNum = 0; cnfNum < cnfArray.length; cnfNum++) {
            CNFProblem cnf = cnfArray[cnfNum];
            // Convert the CNF to a k-clique problem
            CliqueProblem cliqueProblem = CliqueProblem.createFromCNFProblem(cnf);

            // Convert to k-clique problem to a (V-k)-cover problem
            VertexCoverProblem coverProblem = VertexCoverProblem.createFromCliqueProblem(cliqueProblem);

            // Print the CNF information
            String infoString = String.format("3CNF No.%d: [n=%d c=%d] ==> Clique: [V=%d E=%d K=%d]",
                                        cnfNum + 1,
                                        cnf.varCount,
                                        cnf.clauseCount,
                                        cliqueProblem.graph.vertexCount,
                                        cliqueProblem.graph.edgeCount,
                                        cliqueProblem.k);
            System.out.println(infoString);

            // Start timing and begin solving
            long start_time = System.currentTimeMillis();
            int[] cover = coverProblem.findKVertexCover();

            // A null cover means there is no k vertex cover
            if (cover != null) {
                // Convert the cover solution to the clique solution
                int[] clique = CliqueProblem.createCliqueFromCover(cover);

                // Get a CNF assignment from the clique solution and stop timing
                int[] cnfAssignment = cnf.createCNFAssignmentFromClique(clique);
                long end_time = System.currentTimeMillis();
                long difference = end_time-start_time;

                // Print the results
                System.out.println("In " + difference + " ms, find solution " + CNFProblem.assignmentToString(cnfAssignment));
                System.out.println(cnf);
                System.out.println(cnf.toStringWithAssignment(cnfAssignment));
            }
            // If there is no solution
            else {
                // Stop timing
                long end_time = System.currentTimeMillis();
                long difference = end_time-start_time;
                // Get a random assignment
                int[] randomCNFAssignment = cnf.getRandomAssignment();
                // Print the results
                System.out.println("In " + difference + " ms, find no solution! " +
                                   "Random assignment " + CNFProblem.assignmentToString(randomCNFAssignment));
                System.out.println(cnf);
                System.out.println(cnf.toStringWithAssignment(randomCNFAssignment));
            }
            System.out.println();
        }
        System.out.println("***");

    }

    static CNFProblem[] readCNFFile(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new FileReader(filename));

        // List of 3cnf's. Total number of lines is unknown, so use dynamic array.
        ArrayList<CNFProblem> cnfList = new ArrayList<CNFProblem>();

        // Iterate over file lines and add each 3cnf to the list
        while (in.hasNextLine()) {
            String line = in.nextLine();

            // Create a CNFProblem from the line
            CNFProblem cnf = CNFProblem.fromString(line);

            // Add the CNF to the list
            cnfList.add(cnf);
        }

        in.close();

        // Convert the ArrayList of CNFs to a static array.
        return cnfList.toArray(new CNFProblem[0]);
    }
}

class CNFProblem {
    int[] cnf;
    int varCount;
    int clauseCount;

    // Converts a CNF to a string such as:
    // ( 2|-1|-1)∧(-3|-2|-4)∧( 4|-3|-1)
    public String toString() {
        String ret = "";
        int i;
        for (i = 0; i < (cnf.length / 3) - 1; i++) {
            ret += String.format("(%2d|%2d|%2d)∧", cnf[3*i], cnf[3*i+1], cnf[3*i+2]);
        }
        ret += String.format("(%2d|%2d|%2d)", cnf[3*i], cnf[3*i+1], cnf[3*i+2]);
        return ret;
    }

    // Given both a CNF and an assignment, fills in each literal for its value.
    // Ex. ( T| T| T)∧( X| F| T)∧( F| X| T)
    String toStringWithAssignment(int[] assignment) {
        String ret = "";
        int i;
        for (i = 0; i < (cnf.length / 3) - 1; i++) {
            ret += String.format("(%2s|%2s|%2s)∧", 
                getAssignmentCharFromLiteral(cnf[3*i], assignment),
                getAssignmentCharFromLiteral(cnf[3*i+1], assignment),
                getAssignmentCharFromLiteral(cnf[3*i+2], assignment));
        }
        ret += String.format("(%2s|%2s|%2s)", 
            getAssignmentCharFromLiteral(cnf[3*i], assignment),
            getAssignmentCharFromLiteral(cnf[3*i+1], assignment),
            getAssignmentCharFromLiteral(cnf[3*i+2], assignment));
        return ret;
    }

    // Converts an assignment number into the respective character
    static char getAssignmentCharFromValue(int assignment) {
        switch (assignment) {
            case -1:
                return 'F';
            case 0:
                return 'X';
            case 1:
                return 'T';
        
            default:
                return '?';
        }
    }

    // Given an index into a CNF, finds the character for that variable's assignment
    static char getAssignmentCharFromLiteral(int literal, int[] assignment) {
        int val = 0;
        if (literal < 0) {
            val = -assignment[-literal];
        }
        else {
            val = assignment[literal];
        }
        return getAssignmentCharFromValue(val);
    }

    // Converts a CNF assignment into a string such as below:
    // [1:F, 2:T, 3:X]
    static String assignmentToString(int[] assignment) {
        String ret = "[";
        int i;
        for (i = 1; i < assignment.length - 1; i++) {
            ret += i + ":" + getAssignmentCharFromValue(assignment[i]) + " ";
        }
        ret += i + ":" + getAssignmentCharFromValue(assignment[i]) + "]";
        return ret;
    }

    public static CNFProblem fromString(String str) {

            Scanner scanner = new Scanner(str);
            // We don't know the number of clauses ahead of time, so use dynamic array.
            ArrayList<Integer> cnfList = new ArrayList<Integer>();
            while (scanner.hasNextInt()) {
                cnfList.add(scanner.nextInt());
            }

            int varCount = 1;
            
            // Convert the dynamic array to a static array (mostly just for indexing with brackets)
            int[] cnfArr = new int[cnfList.size()];
            for (int i = 0; i < cnfArr.length; i++) {
                cnfArr[i] = cnfList.get(i);
                if (Math.abs(cnfArr[i]) > varCount)
                    varCount = Math.abs(cnfArr[i]);
            }

            CNFProblem ret = new CNFProblem();
            ret.cnf = cnfArr;
            ret.clauseCount = cnfArr.length / 3;
            ret.varCount = varCount;

            scanner.close();

            return ret;
    }

    // Creates a CNF assignment from a clique. Iterates
    // over the literals selected for the clique and marks
    // the respective variables true (1), false (-1), or either (0) 
    int[] createCNFAssignmentFromClique(int[] clique) {
        int[] assignment = new int[varCount + 1];

        for (int i = 0; i < clique.length; i++) {
            if (clique[i] == 1) {
                if (cnf[i] < 0)
                    assignment[-cnf[i]] = -1;
                else
                    assignment[cnf[i]] = 1;
            }
        }

        return assignment;
    }

    // Randomly generates a CNF assignment with each
    // variable having a 50% chance of being true.
    int[] getRandomAssignment() {
        int[] assignment = new int[varCount + 1];

        for (int i = 1; i < assignment.length; i++) {
            if (Math.random() > 0.5)
                assignment[i] = 1;
            else
                assignment[i] = -1;
        }

        return assignment;
    }
}

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
        
        // Try excluding the next vertex
        if (findKVertexCoverHelper(wipCover, index+1, count))
            return true;
        // If that fails, try including the vertex instead
        wipCover[index] = 1;
        if (findKVertexCoverHelper(wipCover, index+1, count+1))
            return true;

        // This array is shared, so clean it up
        wipCover[index] = 0;

        return false;
    }

    int[] currentMinCover;
    int currentMinCoverSize;

    // Finds a minimum vertex cover of the graph using backtracking
    public int[] findMinVertexCover() {
        //TODO
        return new int[0];
    }

    private void findMinVertexCoverHelper(int[] wipCover, int wipCoverSize, int index) {
        //TODO
        return;
    }
}

// Basic adjacency matrix graph representation. Vertices are indices into the matrix.
class Graph {
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