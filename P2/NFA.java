import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class NFA {
    int numberOfStates;
    ArrayList<Character> alphabet;
    ArrayList<ArrayList<Set<Integer>>> transitionTable;
    int initialState;
    ArrayList<Integer> acceptingStates;
    ArrayList<String> inputStrings;
    final int NUM_INPUT_STRINGS = 30;

    // construct NFA from file
    public NFA(String fileName) {
        try {
            // open file and scanner
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            ////////////////////////////////////////////////////////
            /* numberOfStates */
            // retrieve number of states from first line
            String data = scanner.nextLine();
            // store only the digits as an integer
            numberOfStates = Integer.parseInt(data.replaceAll("[^0-9]", ""));
            ////////////////////////////////////////////////////////



            ////////////////////////////////////////////////////////
            /* alphabet */
            // retrieve alphabet from second line
            data = scanner.nextLine();
            // remove "Sigma:"
            data = data.substring(7);
            // remove spaces
            data = data.replaceAll("\\s+", "");
            // alphabet size will be equal to length of remaining string
            alphabet = new ArrayList<>(data.length());
            // copy characters from input into alphabet
            for(int i = 0; i < data.length(); i++){
                alphabet.add(data.charAt(i));
            }
            ////////////////////////////////////////////////////////



            ////////////////////////////////////////////////////////
            /* transition table */

            // discard dashed line
            scanner.nextLine();

            // transition table size will be equal to the # of states
            transitionTable = new ArrayList<>(numberOfStates);
            
            // loop through rows (the # of rows is equal to the # of states)
            for(int row = 0; row < numberOfStates; row++){

                // transition function exists for each state
                ArrayList<Set<Integer>> transitionFunctions = new ArrayList<>(numberOfStates);

                // read transition functions
                data = scanner.nextLine();
            
                // loop through columns (the # of cols is equal to size of alphabet + (lambda))
                for(int col = 0 ; col < alphabet.size() + 1; col++){
                    // get the indices of the next set of brackets
                    int openIndex = data.indexOf("{");
                    int closeIndex = data.indexOf("}");
                    // check for empty set
                    if(openIndex + 1 != closeIndex){
                        // read first set of states (between { and })
                        String states = data.substring(openIndex + 1, closeIndex);
                        String[] arrayOfStates = states.split(",");
                        
                        // make a collection of the states
                        Set<Integer> setOfStates = new HashSet<Integer>(states.length());
                        for(String state : arrayOfStates){
                            // convert from String to int
                            setOfStates.add(Integer.parseInt(state));  
                        }
                        // add this set of states into the transition function
                        transitionFunctions.add(setOfStates);
                    } else {
                        // otherwise add empty set
                        transitionFunctions.add(new HashSet<Integer>());
                    }
                    // skip to the next set of states
                    data = data.substring(closeIndex + 1);
                }
                // add this row of transition functions into the table 
                transitionTable.add(transitionFunctions);
            }

            // discard dashed line
            scanner.nextLine();
            ////////////////////////////////////////////////////////



            ////////////////////////////////////////////////////////
            /* initial state */
            // retrieve initial state from next line
            data = scanner.nextLine();
            // store only the digits as an integer
            initialState = Integer.parseInt(data.replaceAll("[^0-9]", ""));
            ////////////////////////////////////////////////////////



            ////////////////////////////////////////////////////////
            /* acceptings states */
            // retrieve accepting states from next line
            data = scanner.nextLine();
            // remove "Accepting State(s):"
            data = data.substring(20);
            String[] arrayOfStates = data.split(",");
            acceptingStates = new ArrayList<>(arrayOfStates.length);
            for(String state : arrayOfStates){
                acceptingStates.add(Integer.parseInt(state));
            }
            ////////////////////////////////////////////////////////


            
            ////////////////////////////////////////////////////////
            /* input strings */
            // ignore the next two lines
            scanner.nextLine();
            scanner.nextLine();
            // read input strings
            inputStrings = new ArrayList<>(NUM_INPUT_STRINGS);
            for(int i = 0; i < NUM_INPUT_STRINGS; i++){
                inputStrings.add(scanner.nextLine());
            }
            ////////////////////////////////////////////////////////

            // close scanner
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void print(){
        System.out.println("|Q|: " + numberOfStates);
        System.out.println("Sigma: " + alphabet.toString());
        System.out.println("----------------------------------------");
        for(int i = 0; i < numberOfStates; i++){
            System.out.print(i + ": ");
            for(int j = 0; j < alphabet.size() + 1; j++){
                System.out.print(transitionTable.get(i).get(j).toString());
            }
            System.out.println();
        }
        System.out.println("----------------------------------------");
        System.out.println("Initial State: " + initialState);
        System.out.println("Accepting State(s): " + acceptingStates.toString());
        System.out.println();
        System.out.println("-- Input strings for testing -----------");
        for(int i = 0; i < NUM_INPUT_STRINGS; i++){
            System.out.println(inputStrings.get(i));
        }
    }

    public Set<Integer> lambdaClosure(Set<Integer> states) {
        Set<Integer> ret = new HashSet<>(states);
        while (true) {
            Set<Integer> newStates = new HashSet<>(numberOfStates);
            for (Integer state : ret) {
                newStates.addAll(transitionTable.get(state).get(alphabet.size()));
            }
            if (ret.containsAll(newStates))
                break;
            ret.addAll(newStates);
        }
        return ret;
    }

    public Set<Integer> step(Set<Integer> states, int input) {
        Set<Integer> ret = new HashSet<Integer>(this.numberOfStates);
        for (Integer state : states) {
            ret.addAll(this.transitionTable.get(state).get(input));
        }
        return lambdaClosure(ret);
    }
}
