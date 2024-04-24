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
    ArrayList<Integer> acceptingStates;
    ArrayList<String> inputStrings;
    final int NUM_INPUT_STRINGS = 30;

    public DFA(int numberOfStates, ArrayList<Character> alphabet, ArrayList<ArrayList<Integer>> transitionTable, 
               int initialState, ArrayList<Integer> acceptingStates, ArrayList<String> inputStrings) {
            
            this.numberOfStates = numberOfStates;
            this.alphabet = alphabet;
            this.transitionTable = transitionTable;
            this.initialState = initialState;
            this.acceptingStates = acceptingStates;
            this.inputStrings = inputStrings;
    }

    public DFA(NFA dfa) {

        this.alphabet = new ArrayList<>(dfa.alphabet);
        this.inputStrings = new ArrayList<>(dfa.inputStrings);

        ArrayList<Set<Integer>> states = new ArrayList<>();

        Set<Integer> initialState = new HashSet<>(1);
        initialState.add(dfa.initialState);
        initialState = dfa.lambdaClosure(initialState);

        states.add(initialState);
        this.initialState = 0;
        this.transitionTable = new ArrayList<>();
        this.acceptingStates = new ArrayList<>();

        for (int stateIndex = 0; stateIndex < states.size(); stateIndex++) {
            this.transitionTable.add(new ArrayList<>());

            Set<Integer> state = states.get(stateIndex);
            for (int input = 0; input < this.alphabet.size(); input++) {
                Set<Integer> nextState = dfa.step(state, input);

                if (!states.contains(nextState))
                    states.add(nextState);

                int nextStateIndex = states.indexOf(nextState);
                this.transitionTable.get(stateIndex).add(nextStateIndex);

                for (int acceptingState : dfa.acceptingStates)
                    if (nextState.contains(acceptingState) && !this.acceptingStates.contains(nextStateIndex))
                        this.acceptingStates.add(nextStateIndex);
            }
        }

        this.numberOfStates = states.size();
        Collections.sort(this.acceptingStates);
    }

    public DFA minimize() {
        // Distinguishability table
        int[][] dTable = new int[this.numberOfStates][this.numberOfStates];

        for (int acceptingState : this.acceptingStates) {
            for (int state = 0; state < this.numberOfStates; state++) {
                if (!this.acceptingStates.contains(state)) {
                    dTable[state][acceptingState] = 1;
                    dTable[acceptingState][state] = 1;
                }
            }
        }

        boolean changed = true;
        while(changed) {
            changed = false;
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

        ArrayList<Integer> oldToNewStateMap = new ArrayList<>();
        ArrayList<Integer> newToOldStateMap = new ArrayList<>();

        ArrayList<Set<Integer>> newStates = new ArrayList<>();
        ArrayList<Integer> newAcceptingStates = new ArrayList<>();

        for (int state = 0; state < this.numberOfStates; state++) {
            Set<Integer> newState = new HashSet<>(1);
            newState.add(state);
            for (int otherState = 0; otherState < this.numberOfStates; otherState++) {
                if (dTable[state][otherState] == 0) {
                    newState.add(otherState);
                }
            }
            if (!newStates.contains(newState)) {
                newStates.add(newState);
                if (this.acceptingStates.contains(state)) {
                    newAcceptingStates.add(newStates.size() - 1);
                }
                newToOldStateMap.add(state);
            }
            int newStateIndex = newStates.indexOf(newState);
            oldToNewStateMap.add(newStateIndex);
        }

        ArrayList<ArrayList<Integer>> newTransitionTable = new ArrayList<>();

        for (int newState = 0; newState < newStates.size(); newState++) {
            newTransitionTable.add(new ArrayList<>());
            for (int input = 0; input < this.alphabet.size(); input++) {
                int oldStateIndex = newToOldStateMap.get(newState);
                int oldNextState = this.transitionTable.get(oldStateIndex).get(input);
                int newNextState = oldToNewStateMap.get(oldNextState);
                newTransitionTable.get(newState).add(newNextState);
            }
        }

        Collections.sort(newAcceptingStates);
        return new DFA(newStates.size(), this.alphabet, newTransitionTable, this.initialState,
        newAcceptingStates, this.inputStrings);
    }

    public int step(int state, int input) {
        return this.transitionTable.get(state).get(input);
    }

    public int run(String inputString) {
        int state = this.initialState;
        for (char c : inputString.toCharArray()) {
            int input = this.alphabet.indexOf(c);
            state = step(state, input);
        }
        return state;
    }

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
        System.out.printf("%s:   Accepting State(s)\n", String.join(",", acceptingStates.stream().map(Object::toString).collect(Collectors.toList())));
        System.out.println();
    }
}