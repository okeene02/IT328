import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DFA {
    int numberOfStates;
    ArrayList<Character> alphabet;
    ArrayList<ArrayList<Integer>> transitionTable;
    int initialState;
    Set<Integer> acceptingStates;
    ArrayList<String> inputStrings;
    final int NUM_INPUT_STRINGS = 30;

    // Construct a DFA from components
    public DFA(int numberOfStates, ArrayList<Character> alphabet, ArrayList<ArrayList<Integer>> transitionTable, 
               int initialState, Set<Integer> acceptingStates, ArrayList<String> inputStrings) {
            
            this.numberOfStates = numberOfStates;
            this.alphabet = alphabet;
            this.transitionTable = transitionTable;
            this.initialState = initialState;
            this.acceptingStates = acceptingStates;
            this.inputStrings = inputStrings;
    }

    // Construct a DFA from an NFA
    public DFA(NFA dfa) {

        // Alphabet and input strings remain the same
        this.alphabet = new ArrayList<>(dfa.alphabet);
        this.inputStrings = new ArrayList<>(dfa.inputStrings);

        // Each state of the created DFA will correspond to a set of states of the DFA
        ArrayList<Set<Integer>> states = new ArrayList<>();

        // The initial state of the DFA will correspond to the lambda closure of the initial state of the NFA
        Set<Integer> initialState = new HashSet<>(1);
        initialState.add(dfa.initialState);
        initialState = dfa.lambdaClosure(initialState);

        // Begin with the initial state (which is given index 0)
        states.add(initialState);
        this.initialState = 0;

        // Prepare the new transition table and list of accepting states
        this.transitionTable = new ArrayList<>();
        this.acceptingStates = new HashSet<>();

        // Main loop. Iterates over the states array, whose size may increase within the loop
        for (int stateIndex = 0; stateIndex < states.size(); stateIndex++) {
            // Prepare a new row of the transition table
            this.transitionTable.add(new ArrayList<>());

            // Get the set of states currently being examined
            Set<Integer> state = states.get(stateIndex);

            // Check if this set of states is an accepting state. If so add its index to the DFA's accepting states
            for (int acceptingState : dfa.acceptingStates)
                if (state.contains(acceptingState))
                    this.acceptingStates.add(stateIndex);

            // Loop over each character in the alphabet to generate the current row of the transition table
            for (int input = 0; input < this.alphabet.size(); input++) {
                // Create the next set of states
                Set<Integer> nextState = dfa.step(state, input);

                // Check if this set has already been seen, append it to the working list if it hasn't
                if (!states.contains(nextState))
                    states.add(nextState);

                // Get the index of the next set within the working collection (this will be the id of the corresponding DFA state)
                int nextStateIndex = states.indexOf(nextState);
                // Fill in the current entry in the transition table
                this.transitionTable.get(stateIndex).add(nextStateIndex);
            }
        }

        // Calculate the size of the new DFA
        this.numberOfStates = states.size();
    }

    // Create a minimized version of this DFA using the method taught in class
    public DFA minimize() {
        // Distinguishability table
        // dTable[a][b] = 1 means that state a is distinguishable from state b
        int[][] dTable = new int[this.numberOfStates][this.numberOfStates];

        // Initialize the table with the knowledge that accepting states are distinguishable from non-accepting states
        for (int acceptingState : this.acceptingStates) {
            for (int state = 0; state < this.numberOfStates; state++) {
                if (!this.acceptingStates.contains(state)) {
                    dTable[state][acceptingState] = 1;
                    dTable[acceptingState][state] = 1;
                }
            }
        }

        // Continually iterate over the table until it remains unchanged
        boolean changed = true;
        while(changed) {
            // Initially the table is unchanged
            changed = false;
            // Iterate over the table
            for (int state1 = 0; state1 < this.numberOfStates; state1++) {
                for (int state2 = 0; state2 < this.numberOfStates; state2++) {
                    if (dTable[state1][state2] == 1)
                        continue;

                    for (int input = 0; input < this.alphabet.size(); input++) {
                        int nextState1 = this.transitionTable.get(state1).get(input);
                        int nextState2 = this.transitionTable.get(state2).get(input);
                        if (dTable[nextState1][nextState2] == 1) {
                            dTable[state1][state2] = 1;
                            dTable[state2][state1] = 1;
                            changed = true;
                            break;
                        }
                    }
                }
            }
        }

        // Create mappings between unminimized states and minimized states
        ArrayList<Integer> oldToNewStateMap = new ArrayList<>();
        ArrayList<Integer> newToOldStateMap = new ArrayList<>();

        // Prepare the structures for storing the combined states and accepting states
        ArrayList<Set<Integer>> newStates = new ArrayList<>();
        Set<Integer> newAcceptingStates = new HashSet<>();

        // Iterate over the states of the unminimized DFA
        for (int state = 0; state < this.numberOfStates; state++) {
            // Prepare the set of states indistinguishable from this state
            Set<Integer> newState = new HashSet<>(1);
            // The current state is obviously a member
            newState.add(state);
            for (int otherState = 0; otherState < this.numberOfStates; otherState++) {
                if (dTable[state][otherState] == 0) {
                    newState.add(otherState);
                }
            }

            // Add this set of states to the collection if we haven't already
            // Also update the newToOld mapping
            if (!newStates.contains(newState)) {
                newStates.add(newState);
                if (this.acceptingStates.contains(state)) {
                    newAcceptingStates.add(newStates.size() - 1);
                }
                newToOldStateMap.add(state);
            }

            // Regardless of whether this state is new, update to oldToNew mapping
            int newStateIndex = newStates.indexOf(newState);
            oldToNewStateMap.add(newStateIndex);
        }

        // Prepare the new transition table
        ArrayList<ArrayList<Integer>> newTransitionTable = new ArrayList<>();

        // Iterate over the new states and inputs and determine the transition using the previously created DFA maps
        for (int newState = 0; newState < newStates.size(); newState++) {
            newTransitionTable.add(new ArrayList<>());
            for (int input = 0; input < this.alphabet.size(); input++) {
                int oldStateIndex = newToOldStateMap.get(newState);
                int oldNextState = this.transitionTable.get(oldStateIndex).get(input);
                int newNextState = oldToNewStateMap.get(oldNextState);
                newTransitionTable.get(newState).add(newNextState);
            }
        }

        return new DFA(newStates.size(), this.alphabet, newTransitionTable, this.initialState,
        newAcceptingStates, this.inputStrings);
    }

    // Run one step of the DFA. Returns the new state given the current state and input character
    public int step(int state, int input) {
        return this.transitionTable.get(state).get(input);
    }

    // Runs this DFA for a string of characters and returns the final state
    // Assumes that the string is made up only of characters within the alphabet
    public int run(String inputString) {
        int state = this.initialState;
        for (char c : inputString.toCharArray()) {
            int input = this.alphabet.indexOf(c);
            state = step(state, input);
        }
        return state;
    }

    // Tests all of the associated input strings and returns a boolean array indicating acceptance
    public boolean[] testStrings() {

        boolean[] ret = new boolean[this.inputStrings.size()];

        for (int i = 0; i < this.inputStrings.size(); i++) {
            String s = this.inputStrings.get(i);
            int state = this.run(s);
            if (this.acceptingStates.contains(state)) {
                ret[i] = true;
            }
        }
        return ret;
    }

    // Prints the results of testing the associated input strings
    public void printStringTests() {
        boolean[] results = this.testStrings();
        int yesCount = 0;
        int noCount = 0;
        for (int i = 0; i < results.length; i++) {
            boolean result = results[i];
            if (result) {
                System.out.print("Yes  ");
                yesCount++;
            }
            else {
                System.out.print("No   ");
                noCount++;
            }
            if (i == results.length / 2 - 1) {
                System.out.println();
            }
        }
        System.out.println("\n");
        System.out.printf("Yes:%d No:%d\n", yesCount, noCount);
    }

    // Prints information about the DFA in the required format
    public void print(){
        System.out.print("Sigma:");
        for (char c : this.alphabet)
            System.out.printf("%6c", c);
        System.out.println();

        System.out.println("--------------------------------");
        for(int i = 0; i < numberOfStates; i++){
            System.out.printf("%5d:", i);
            for(int j = 0; j < alphabet.size(); j++){
                System.out.printf("%6d", transitionTable.get(i).get(j));
            }
            System.out.println();
        }
        System.out.println("--------------------------------");
        System.out.printf("%d:   Initial State\n", initialState);
        System.out.printf("%s:   Accepting State(s)\n", String.join(",", 
            // This converts the list of numbers to a list of strings
            acceptingStates.stream().map(Object::toString).collect(Collectors.toList())));
        System.out.println();
    }
}