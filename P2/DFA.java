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