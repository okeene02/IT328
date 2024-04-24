public class Tester {
    public static void main(String[] args) {
        NFA nfa = new NFA("nfa/X.nfa");
        nfa.print();
        DFA dfa = new DFA(nfa);
        dfa.print();
        dfa.minimize();
        dfa.print();
    }
}
