import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class representing a CNF SAT problem (a boolean
 * satisfiability problem where the formula is in
 * conjunctive normal form).
 * 
 * Provides methods for reading from a string, managing
 * variable assignments, and generating random assignments.
 */
class CNFProblem {
    int[] cnf;
    int varCount;
    int clauseCount;

    /**
     * Converts this CNF into a string such as ( 2|-1|-1)∧(-3|-2|-4)∧( 4|-3|-1)
     */
    @Override
    public String toString() {
        String ret = "";
        int i;
        for (i = 0; i < (cnf.length / 3) - 1; i++) {
            ret += String.format("(%2d|%2d|%2d)∧", cnf[3*i], cnf[3*i+1], cnf[3*i+2]);
        }
        ret += String.format("(%2d|%2d|%2d)", cnf[3*i], cnf[3*i+1], cnf[3*i+2]);
        return ret;
    }

    /**
     * Creates a string showing the assignment of variables to the CNF.
     * Ex. ( T| T| T)∧( X| F| T)∧( F| X| T)
     * @param assignment
     * An array such that assignment[variableNumber] == 1 (true) | 0 (unspecified) | -1 (false)
     * @return
     * A string representing the assignment of variables to the CNF
     */
    String toStringWithAssignment(int[] assignment) {
        String ret = "";
        int i;
        for (i = 0; i < (cnf.length / 3) - 1; i++) {
            ret += String.format("(%2s|%2s|%2s)∧", 
                getAssignmentCharFromVariable(cnf[3*i], assignment),
                getAssignmentCharFromVariable(cnf[3*i+1], assignment),
                getAssignmentCharFromVariable(cnf[3*i+2], assignment));
        }
        ret += String.format("(%2s|%2s|%2s)", 
            getAssignmentCharFromVariable(cnf[3*i], assignment),
            getAssignmentCharFromVariable(cnf[3*i+1], assignment),
            getAssignmentCharFromVariable(cnf[3*i+2], assignment));
        return ret;
    }

    /**
     * Given an assignment number (1, 0, -1 for true, either, false), returns
     * the appropriate character
     * @param assignment
     * The assignment number
     * @return
     * The character representing the assignment
     */
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

    /**
     * Converts a variable from the cnf into the appropriate assignment character
     * @param variable
     * The variable being assigned. Can be negative, represented the negated variable
     * @param assignment
     * The array of variable assignments
     * @return
     * The appropriate character
     */
    static char getAssignmentCharFromVariable(int variable, int[] assignment) {
        int val = 0;
        if (variable < 0) {
            val = -assignment[-variable];
        }
        else {
            val = assignment[variable];
        }
        return getAssignmentCharFromValue(val);
    }

    /**
     * Converts a CNF assignment into a string such as: [1:F, 2:T, 3:X]
     * @param assignment
     * The assignment to be converted to a string
     * @return
     * The string representation of the assignment
     */
    static String assignmentToString(int[] assignment) {
        String ret = "[";
        int i;
        for (i = 1; i < assignment.length - 1; i++) {
            ret += i + ":" + getAssignmentCharFromValue(assignment[i]) + " ";
        }
        ret += i + ":" + getAssignmentCharFromValue(assignment[i]) + "]";
        return ret;
    }

    /**
     * Creates a CNFProblem from a string. The string consist a list of numbers, such
     * that the number of numbers is divisible by three.
     * @param str
     * The string to be converted
     * @return
     * The CNFProblem represented by the string
     */
    public static CNFProblem fromString(String str) {

            Scanner scanner = new Scanner(str);
            // We don't know the number of clauses ahead of time, so use dynamic array.
            ArrayList<Integer> cnfList = new ArrayList<Integer>();
            while (scanner.hasNextInt()) {
                cnfList.add(scanner.nextInt());
            }

            int varCount = 1;
            
            // Convert the dynamic array to a static array (mostly just for indexing with brackets)
            // Also counts the number of variables
            int[] cnfArr = new int[cnfList.size()];
            for (int i = 0; i < cnfArr.length; i++) {
                cnfArr[i] = cnfList.get(i);
                if (Math.abs(cnfArr[i]) > varCount)
                    varCount = Math.abs(cnfArr[i]);
            }

            // Create the CNFProblem instance
            CNFProblem ret = new CNFProblem();
            ret.cnf = cnfArr;
            ret.clauseCount = cnfArr.length / 3;
            ret.varCount = varCount;

            scanner.close();

            return ret;
    }

    /**
     * Given a solution to the corresponding clique problem,
     * create a solution to a CNF problem. Iterates over the 
     * literals selected for the clique and marks the
     * respective variables true (1), false (-1), or either (0)
     * @param clique
     * @return
     */
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

    /**
     * Creates a random CNF assignments. Each variable
     * has a 50% chance of being true.
     * @return
     * The random assignment
     */
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

