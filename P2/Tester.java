public class Tester {
    public static void main(String[] args) {
        NFA nfa = new NFA("nfa/A.nfa");
        nfa.print();
        DFA dfa = new DFA(nfa);
        dfa.print();
    }
}
