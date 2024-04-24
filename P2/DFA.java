import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DFA {
    int numberOfStates;
    ArrayList<Character> alphabet;
    ArrayList<ArrayList<Integer>> transitionTable;
    int initialState;
    ArrayList<Integer> acceptingStates;
    ArrayList<String> inputStrings;
    final int NUM_INPUT_STRINGS = 30;

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
    }

    public void minimize() {
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

        this.acceptingStates = newAcceptingStates;
        this.numberOfStates = newStates.size();
        this.transitionTable = newTransitionTable;

    }

    public void print(){
        System.out.println("|Q|: " + numberOfStates);
        System.out.println("Sigma: " + alphabet.toString());
        System.out.println("----------------------------------------");
        for(int i = 0; i < numberOfStates; i++){
            System.out.print(i + ": ");
            for(int j = 0; j < alphabet.size(); j++){
                System.out.print(transitionTable.get(i).get(j).toString() + ' ');
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
}