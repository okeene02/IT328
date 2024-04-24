public class NFA2DFA{

    public static void main(String[] args) {

        // check number of args
        if(args.length != 1){
            System.out.println("Incorrect usage: java NFA2DFA <NFA file>");
            System.exit(0);
        }
        String dfaName = args[0].substring(0, args[0].length() - 3) + "dfa";

        NFA nfa = new NFA(args[0]);
        // nfa.print();
       
        // receive DFA from B, test input strings
        DFA dfa = new DFA(nfa);
        System.out.println("NFA " + args[0] + " to DFA " + dfaName + "\n");
        dfa.print();
        System.out.println("Parsing results of strings attached in " + args[0] + ":");
        dfa.printStringTests();

        System.out.println();

        // receive DFA from C, test input strings
        DFA mdfa = dfa.minimize();
        System.out.println("Minimized DFA from " + dfaName + "\n");
        mdfa.print();
        System.out.println("Parsing results of strings attached in " + args[0] + ":");
        mdfa.printStringTests();
        System.out.println();
        System.out.printf("|Q| %d -> %d", dfa.numberOfStates, mdfa.numberOfStates);
    }


}