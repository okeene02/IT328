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

