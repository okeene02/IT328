import java.util.ArrayList;
import java.util.Scanner;

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

