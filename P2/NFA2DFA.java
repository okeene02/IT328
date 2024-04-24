public class NFA2DFA{

    public static void main(String[] args) {

        // check number of args
        if(args.length != 1){
            System.out.println("Incorrect usage: java NFA2DFA <NFA file>");
            System.exit(0);
        }

        NFA nfa = new NFA(args[0]);
        nfa.print();
       
        // pass NFA to B

        // receive DFA from B, test input strings
        // receive DFA from C, test input strings
    }


}