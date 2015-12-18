import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Jack Li on 12/3/2015.
 */
public class RegExpressionMain {
    public static void main(String[] args) throws IOException {
        if (args.length <= 1) { //If no filename was entered
            System.out.println("Error: Invalid File Name");
            System.exit(1);
        }
        String filename = args[0];
        String outputname = args[1];
        readFile(filename, outputname);
    }

    public static void readFile(String filename, String outputname) throws IOException {
        //Initiailize file reader
        FileReader file_reader = new FileReader(filename);
        BufferedReader buffered_reader = new BufferedReader(file_reader);

        //reading the alphabet of the language
        String line = buffered_reader.readLine();
        String[] alphabet = line.split("(?!^)");

        ArrayList<Character> alph = new ArrayList<Character>();
        for(int a = 0; a < alphabet.length; a++){
            alph.add(alphabet[a].charAt(0));
        }
        //the regular expression
        String regExp = buffered_reader.readLine();
        String[] regExpArr = regExp.split("");

        //Convert everything into a Node and all non-operation symbols and non-parentheses into NFA's
        //Store in ArrayList
        ArrayList<Node> nodes = new ArrayList<Node>();
        Node obj;
        for(int loop = 0; loop < regExpArr.length; loop++){
            if(!(regExpArr[loop].compareTo("o") == 0 || regExpArr[loop].compareTo("U") == 0 || regExpArr[loop].compareTo("*") == 0 || regExpArr[loop].compareTo("(") == 0 || regExpArr[loop].compareTo(")") == 0)){
                obj = new Node();
                obj.setIsNFA(true);
                obj.setNFA(createSingleSymbolNFA(regExpArr[loop]));
            } else {
                obj = new Node();
                obj.setCharac(regExpArr[loop]);
            }
            nodes.add(obj);
        }

        //Get the final NFA
        Node n = convertNFA(nodes);

        ArrayList<NFATransitionNode> transitions = convertNodes(n);

        //Get total number of states
        int total_size = 0;
        for(int b = 0; b < transitions.size(); b++){
            if(Integer.parseInt(transitions.get(b).getInState()) > total_size){
                total_size = Integer.parseInt(transitions.get(b).getInState());
            }
            if (Integer.parseInt(transitions.get(b).getToState()) > total_size){
                total_size = Integer.parseInt(transitions.get(b).getToState());
            }
        }
        total_size++;

        //Store accept states
        int accept[] = new int[1];
        accept[0] = getAcceptState(n.getNFA());
        ArrayList<String> testcases = new ArrayList<String>();
        while (null != (line = buffered_reader.readLine())) {
            String str = line;
            testcases.add(str);
        }
        createDFA(total_size, alph, transitions, getStartState(n.getNFA()), accept, "dfa.txt", testcases);
        /*printNFATest(n);*/
        //e is epsilon, N is empty set, U is union, o is concatenation, * is star operator
        resolveDFA("dfa.txt", outputname);
        buffered_reader.close();
        file_reader.close();
    }

    //Converts NFANodes into NFATransitionNodes
    public static ArrayList<NFATransitionNode> convertNodes(Node n) {
        ArrayList<NFANode> nfa = n.getNFA();
        ArrayList<NFATransitionNode> transitions = new ArrayList<NFATransitionNode>();
        for(int loop = 0; loop < nfa.size(); loop++){
            NFANode obj = nfa.get(loop);
            ArrayList<String> nfa_transitions = obj.getTransitions();
            ArrayList<String> nfa_idstates = obj.getIdStates();
            for(int loop2 = 0; loop2 < nfa_transitions.size(); loop2++){
                NFATransitionNode tran = new NFATransitionNode("" + obj.getId(), nfa_transitions.get(loop2).charAt(0), nfa_idstates.get(loop2));
                transitions.add(tran);
            }
        }
        return transitions;
    }
    /*public static void printNFATest(Node n){
        ArrayList<NFANode> test = n.getNFA();
        for(int loop = 0; loop < test.size(); loop++){
            if(test.get(loop).getStart()){
                System.out.println("Start id: " + test.get(loop).getId());
            } else if(test.get(loop).getAccept()){
                System.out.println("Accept id: " + test.get(loop).getId());
            }
        }
        for(int loop2 = 0; loop2 < test.size(); loop2++){
            ArrayList<String> id_states = test.get(loop2).getIdStates();
            ArrayList<String> transitions = test.get(loop2).getTransitions();
            for(int loop3 = 0; loop3 < id_states.size(); loop3++){
                System.out.println("" + test.get(loop2).getId() + " '" + transitions.get(loop3) + "' " + id_states.get(loop3));
            }
        }
    }*/

    public static ArrayList<NFANode> createSingleSymbolNFA(String charac){
        NFANode start = new NFANode();
        start.setStart(true);

        start.setId(0);
        ArrayList<String> start_id_states = new ArrayList<String>();
        start_id_states.add("1");
        start.setIdStates(start_id_states);

        NFANode accept = new NFANode();
        accept.setAccept(true);
        accept.setId(1);
        ArrayList<NFANode> to_states = new ArrayList<NFANode>();
        to_states.add(accept);
        ArrayList<String> transition = new ArrayList<String>();
        transition.add(charac);
        start.setToStates(to_states);
        start.setTransitions(transition);
        ArrayList<NFANode> nfa = new ArrayList<NFANode>();
        nfa.add(start);
        nfa.add(accept);
        return nfa;
    }

    public static Node convertNFA(ArrayList<Node> regExpArr) {
        Stack<Node> stack = new Stack<Node>();
        for (int i = 0; i < regExpArr.size(); i++) {
            if(regExpArr.get(i).getCharac().compareTo(")") != 0){ //if char value at index != ")"
                stack.push(regExpArr.get(i));
            } else { //if char value at index == ")"
                ArrayList<Node> nodes_temp = new ArrayList<Node>();
                while(stack.peek().getCharac().compareTo("(") != 0){ //pop off of stack until you see "("
                    nodes_temp.add(stack.pop());
                }
                stack.pop(); //pop off extra "("
                Node temp = convertHelper(nodes_temp); //create node out of contents of parenthesis
                stack.push(temp);
            }
        }
        return stack.pop();
    }

    private static Node convertHelper(ArrayList<Node> arr) {
        Node obj = arr.get(0);
        //convert contents of parenthesis together and return Node that has a nfa
        if(arr.get(0).getIsNFA()){ //If Node object at index 0 contains a NFA
            if(arr.get(1).getCharac().compareTo("o") == 0) {//If object right after is operation "o"
                obj = Concatenate(arr.get(2), arr.get(0));
            } else if(arr.get(1).getCharac().compareTo("U") == 0) {//If object right after is operation "U"
                obj = Union(arr.get(2), arr.get(0));
            } else {
                System.out.println("Format Error");
                System.exit(0);
            }
        } else if(arr.get(0).getCharac().compareTo("*") == 0){
            obj = Star(arr.get(1));
        } else {
            System.out.println("Format Error");
            System.exit(0);
        }

        return obj;
    }

    //helper method that updates id's
    public static ArrayList<NFANode> updateIds(ArrayList<NFANode> nfa, int size){
        for(int loop = 0; loop < nfa.size(); loop++){
            NFANode node = nfa.get(loop);
            node.setId(node.getId() + size);
            ArrayList<String> id_states = node.getIdStates();
            for(int loop2 = 0; loop2 < id_states.size(); loop2++){
                id_states.set(loop2, "" + (Integer.parseInt(id_states.get(loop2)) + size));
            }
            node.setIdStates(id_states);
            nfa.set(loop, node);
        }
        return nfa;
    }

    //Performs concatenation and returns NFA
    public static Node Concatenate(Node first, Node second){
        ArrayList<NFANode> first_nfa = first.getNFA();
        int first_accept_index = getAcceptState(first_nfa);
        ArrayList<NFANode> second_nfa = second.getNFA();
        int second_start_index = first_nfa.size() + getStartState(second_nfa);

        //Update id's
        second_nfa = updateIds(second_nfa, first_nfa.size());

        //Add second nfa to first nfa
        for(int loop = 0; loop < second_nfa.size(); loop++){
            first_nfa.add(second_nfa.get(loop));
        }

        //Update first accept state
        NFANode first_accept_obj = first_nfa.get(first_accept_index);
        first_accept_obj.setAccept(false);
        ArrayList<NFANode> first_to_states = first_accept_obj.getToStates();
        ArrayList<String> first_id_states = first_accept_obj.getIdStates();
        ArrayList<String> first_transitions = first_accept_obj.getTransitions();
        first_to_states.add(first_nfa.get(second_start_index));
        first_id_states.add("" + second_start_index);
        first_transitions.add("e");
        first_accept_obj.setToStates(first_to_states);
        first_accept_obj.setIdStates(first_id_states);
        first_accept_obj.setTransitions(first_transitions);
        first_nfa.set(first_accept_index, first_accept_obj);

        //Update second start state
        NFANode second_start_obj = first_nfa.get(second_start_index);
        second_start_obj.setStart(false);
        first_nfa.set(second_start_index, second_start_obj);

        first.setNFA(first_nfa);
        return first;

    }

    //Performs union and returns NFA
    public static Node Union(Node first, Node second){
        ArrayList<NFANode> first_nfa = first.getNFA();
        int first_start = getStartState(first_nfa);
        int first_accept = getAcceptState(first_nfa);
        ArrayList<NFANode> second_nfa = second.getNFA();
        int second_start = first_nfa.size() + getStartState(second_nfa);
        int second_accept = first_nfa.size() + getAcceptState(second_nfa);

        second_nfa = updateIds(second_nfa, first_nfa.size()); //Update Id's

        //Add second_nfa to first_nfa
        for(int loop = 0; loop < second_nfa.size(); loop++){
            first_nfa.add(second_nfa.get(loop));
        }

        //Create new accept state
        NFANode new_accept = new NFANode();
        new_accept.setAccept(true);
        new_accept.setId(first_nfa.size());
        first_nfa.add(new_accept);

        //Update old accept states
        NFANode first_old_accept = first_nfa.get(first_accept);
        first_old_accept.setAccept(false);
        ArrayList<NFANode> first_old_tostates = first_old_accept.getToStates();
        ArrayList<String> first_old_transitions = first_old_accept.getTransitions();
        ArrayList<String> first_old_idstates = first_old_accept.getIdStates();
        first_old_tostates.add(new_accept);
        first_old_transitions.add("e");
        first_old_idstates.add("" + new_accept.getId());
        first_old_accept.setToStates(first_old_tostates);
        first_old_accept.setTransitions(first_old_transitions);
        first_old_accept.setIdStates(first_old_idstates);
        first_nfa.set(first_accept, first_old_accept);

        NFANode second_old_accept = first_nfa.get(second_accept);
        second_old_accept.setAccept(false);
        ArrayList<NFANode> second_old_tostates = second_old_accept.getToStates();
        ArrayList<String> second_old_transitions = second_old_accept.getTransitions();
        ArrayList<String> second_old_idstates = second_old_accept.getIdStates();
        second_old_tostates.add(new_accept);
        second_old_transitions.add("e");
        second_old_idstates.add("" + new_accept.getId());
        second_old_accept.setToStates(second_old_tostates);
        second_old_accept.setTransitions(second_old_transitions);
        second_old_accept.setIdStates(second_old_idstates);
        first_nfa.set(second_accept, second_old_accept);

        //Create new start state
        NFANode new_start = new NFANode();
        new_start.setStart(true);
        new_start.setId(first_nfa.size());
        ArrayList<NFANode> new_start_tostates = new ArrayList<NFANode>();
        new_start_tostates.add(first_nfa.get(first_start));
        new_start_tostates.add(first_nfa.get(second_start));
        new_start.setToStates(new_start_tostates);
        ArrayList<String> new_start_transitions = new ArrayList<String>();
        new_start_transitions.add("e");
        new_start_transitions.add("e");
        new_start.setTransitions(new_start_transitions);
        ArrayList<String> new_start_idstates = new ArrayList<String>();
        new_start_idstates.add("" + first_start);
        new_start_idstates.add("" + second_start);
        new_start.setIdStates(new_start_idstates);
        first_nfa.add(new_start);

        //Update old start states
        NFANode first_old_start = first_nfa.get(first_start);
        first_old_start.setStart(false);
        first_nfa.set(first_start, first_old_start);

        NFANode second_old_start = first_nfa.get(second_start);
        second_old_start.setStart(false);
        first_nfa.set(second_start, second_old_start);

        first.setNFA(first_nfa);
        return first;
    }

    //performs star and returns NFA
    public static Node Star(Node first){
        ArrayList<NFANode> nfa = first.getNFA();
        int start_index = getStartState(nfa);
        int accept_index = getAcceptState(nfa);

        //Create new accept state
        NFANode new_accept = new NFANode();
        new_accept.setAccept(true);
        new_accept.setId(nfa.size());
        nfa.add(new_accept);

        //Create new start state
        NFANode new_start = new NFANode();
        new_start.setStart(true);
        ArrayList<NFANode> new_start_tostates = new ArrayList<NFANode>();
        ArrayList<String> new_start_transitions = new ArrayList<String>();
        ArrayList<String> new_start_id_states = new ArrayList<String>();
        new_start_tostates.add(nfa.get(start_index)); //Add last start state
        new_start_tostates.add(new_accept); //Add new accept state
        new_start_transitions.add("e");
        new_start_transitions.add("e");
        new_start_id_states.add("" + nfa.get(start_index).getId());
        new_start_id_states.add("" + (nfa.size() - 1));
        new_start.setToStates(new_start_tostates);
        new_start.setTransitions(new_start_transitions);
        new_start.setIdStates(new_start_id_states);
        new_start.setId(nfa.size());
        nfa.add(new_start);

        //Update old accept state
        NFANode old_accept = nfa.get(accept_index);
        ArrayList<NFANode> old_accept_tostates = old_accept.getToStates();
        ArrayList<String> old_accept_transitions = old_accept.getTransitions();
        ArrayList<String> old_accept_idstates = old_accept.getIdStates();
        old_accept_tostates.add(nfa.get(start_index));
        old_accept_tostates.add(new_accept);
        old_accept_transitions.add("e");
        old_accept_transitions.add("e");
        old_accept_idstates.add("" + start_index);
        old_accept_idstates.add("" + new_accept.getId());
        old_accept.setToStates(old_accept_tostates);
        old_accept.setTransitions(old_accept_transitions);
        old_accept.setIdStates(old_accept_idstates);
        old_accept.setAccept(false);
        nfa.set(accept_index, old_accept);

        //Update old start state
        NFANode old_start = nfa.get(start_index);
        old_start.setStart(false);
        nfa.set(start_index, old_start);

        first.setNFA(nfa);
        return first;
    }

    public static int getAcceptState(ArrayList<NFANode> nfa){
        for(int loop = 0; loop < nfa.size(); loop++){
            if(nfa.get(loop).getAccept()){
                return loop;
            }
        }
        return -1;
    }

    public static int getStartState(ArrayList<NFANode> nfa){
        for(int loop = 0; loop < nfa.size(); loop++){
            if(nfa.get(loop).getStart()){
                return loop;
            }
        }
        return -1;
    }

    public static void createDFA(int nfa_num_states, ArrayList<Character> alphabet, ArrayList<NFATransitionNode> nfa_transitions, int nfa_start_state, int[] nfa_accept_states, String outputname, ArrayList<String> testcases){
        //Get the start state for the DFA
        DFASetNode dfa_start_state = getStartState(alphabet, nfa_transitions, nfa_start_state, nfa_accept_states);
        /*for(int test = 0; test < dfa_start_state.getNFAStates().size(); test++){
            System.out.print(dfa_start_state.getNFAStates().get(test) + " ");
        }*/

        //This is a ArrayList that contains all of the DFASetNodes
        ArrayList<DFASetNode> dfa = new ArrayList<DFASetNode>();
        dfa.add(dfa_start_state);
        for(int loop = 0; loop < dfa.size(); loop++){
            DFASetNode obj = dfa.get(loop);
            ArrayList<DFASetNode> next = new ArrayList<DFASetNode>();
            //If it is a dead state then skip
            if(obj.getDeadState() == false) {
                for (int loop2 = 0; loop2 < alphabet.size(); loop2++) {
                    DFASetNode new_dfa_node = createDFASetNode(alphabet, nfa_transitions, dfa.get(loop), nfa_accept_states, alphabet.get(loop2));
                    int check = checkInDFA(dfa, new_dfa_node);
                    if(check == -1){
                        new_dfa_node.setID(dfa.size() + 1);
                        dfa.add(new_dfa_node);
                        next.add(new_dfa_node);
                    } else if(check == -2) {
                        int deadcheck = checkDeadNodeExists(dfa);
                        if(deadcheck == -1){
                            new_dfa_node.setID(dfa.size() + 1);
                            dfa.add(new_dfa_node);
                            next.add(new_dfa_node);
                        } else {
                            new_dfa_node.setID(deadcheck + 1);
                            next.add(new_dfa_node);
                        }
                    } else {
                        new_dfa_node.setID(check + 1);
                        next.add(new_dfa_node);
                    }
                }
                obj.setToStates(next);
                dfa.set(loop, obj);
            }
        }
        writeDFA(alphabet, dfa, outputname, testcases);
    }

    public static void writeDFA(ArrayList<Character> alphabet, ArrayList<DFASetNode> dfa, String outputname, ArrayList<String> testcases){
        try {
            FileWriter writer = new FileWriter(outputname);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            //write number of states
            int num_states = dfa.size();
            bufferedWriter.write("" + num_states);
            bufferedWriter.newLine();

            //write alphabet
            String alph = "";
            for(int alphloop = 0; alphloop < alphabet.size(); alphloop++){
                alph += "" + alphabet.get(alphloop);
            }
            bufferedWriter.write(alph);
            bufferedWriter.newLine();

            //keep track of accept states
            ArrayList<String> accept_states = new ArrayList<String>();
            //write transitions
            for(int dfaloop = 0; dfaloop < dfa.size(); dfaloop++){
                DFASetNode tran = dfa.get(dfaloop);
                if(tran.getAccept() == true){
                    accept_states.add("" + tran.getID());
                }
                for(int loop = 0; loop < alphabet.size(); loop++){
                    DFASetNode next = tran.getToStates().get(loop);
                    bufferedWriter.write("" + tran.getID() + " '" + alphabet.get(loop) + "' " + next.getID());
                    bufferedWriter.newLine();
                }
            }
            //write start state
            bufferedWriter.write("1");
            bufferedWriter.newLine();

            //write accept states
            String accept = "";
            for(int accloop = 0; accloop < accept_states.size(); accloop++){
                accept += accept_states.get(accloop) + " ";
            }
            bufferedWriter.write(accept);
            bufferedWriter.newLine();
            Iterator<String> testIter = testcases.iterator();
            while(testIter.hasNext()) {
                bufferedWriter.write(testIter.next());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();

        } catch (IOException e){
            System.out.println(e);
            System.exit(1);
        }
    }
    public static int checkDeadNodeExists(ArrayList<DFASetNode> dfa){
        for(int loop = 0; loop < dfa.size(); loop++){
            DFASetNode temp = dfa.get(loop);
            if(temp.getDeadState() == true){
                return loop;
            }
        }
        return -1;
    }

    //returns index number if it already exists
    public static int checkInDFA(ArrayList<DFASetNode> dfa, DFASetNode new_dfa_node){
        ArrayList<String> new_node_states = new_dfa_node.getNFAStates();
        if(new_node_states.size() == 0){
            return -2;
        }
        for(int loop = 0; loop < dfa.size(); loop++){
            ArrayList<String> dfa_states = dfa.get(loop).getNFAStates();
            if(dfa_states.size() == new_node_states.size()){
                int count = 0;
                for(int loop2 = 0; loop2 < dfa_states.size(); loop2++){
                    for(int loop3 = 0; loop3 < new_node_states.size(); loop3++){
                        if(dfa_states.get(loop2).compareTo(new_node_states.get(loop3)) == 0){
                            count++;
                        }
                    }
                }
                if(count == new_node_states.size()){
                    return loop;
                }
            }
        }
        return -1;
    }
    public static DFASetNode createDFASetNode(ArrayList<Character> alphabet, ArrayList<NFATransitionNode> nfa_transitions, DFASetNode dfa_node, int[] nfa_accept_nodes, char charac){
        DFASetNode ret = new DFASetNode(alphabet);
        ArrayList<String> new_nfa_states = new ArrayList<String>();
        ArrayList<String> nfa_states = dfa_node.getNFAStates();

        for(int loop = 0; loop < nfa_states.size(); loop++){
            for(int loop2 = 0; loop2 < nfa_transitions.size(); loop2++){
                NFATransitionNode temp = nfa_transitions.get(loop2);
                if(temp.getInState().compareTo(nfa_states.get(loop)) == 0 &&  (temp.getTransition() == charac)){
                    if(checkIt(new_nfa_states , temp.getToState()) == true){
                        new_nfa_states.add(temp.getToState());
                    }
                }
            }
        }

        //Follow epsilon transitions
        for(int loop3 = 0; loop3 < new_nfa_states.size(); loop3++){
            for(int loop4 = 0; loop4 < nfa_transitions.size(); loop4++){
                NFATransitionNode temp2 = nfa_transitions.get(loop4);
                if(temp2.getInState().compareTo(new_nfa_states.get(loop3)) == 0 && (temp2.getTransition() == 'e')){
                    if(checkIt(new_nfa_states, temp2.getToState()) == true){
                        new_nfa_states.add(temp2.getToState());
                    }
                }
            }
        }
        /*for(int test = 0; test < new_nfa_states.size(); test++){
            System.out.print(new_nfa_states.get(test) + " ");
        }*/

        ret.setNFAStates(new_nfa_states);
        //checks to see if there are any NFA transitions
        if(new_nfa_states.size() == 0){
            ret.setDeadState(true);
            ArrayList<DFASetNode> dfa_next = new ArrayList<DFASetNode>();
            //Sets next set nodes to itself
            for(int deadloop = 0; deadloop < alphabet.size(); deadloop++){
                dfa_next.add(ret);
            }
            ret.setToStates(dfa_next);
        } else {
            //Determines if it is a accepting state
            for (int alphloop = 0; alphloop < nfa_accept_nodes.length; alphloop++) {
                for (int alphloop2 = 0; alphloop2 < new_nfa_states.size(); alphloop2++) {
                    if (nfa_accept_nodes[alphloop] == Integer.parseInt(new_nfa_states.get(alphloop2))) {
                        ret.setAccept(true);
                        break;
                    }
                }
                if (ret.getAccept() == true) {
                    break;
                }
            }
        }
        return ret;
    }

    //This gets the start state for the dfa and returns it located in the object DFASetNode
    public static DFASetNode getStartState(ArrayList<Character> alphabet, ArrayList<NFATransitionNode> nfa_transitions, int nfa_start_state, int[] nfa_accept_states){
        DFASetNode ret = new DFASetNode(alphabet);
        ret.setID(1);
        ret.setStart(true);
        //Create ArrayList to store nfa state numbers
        ArrayList<String> nfa_states = new ArrayList<String>();
        nfa_states.add("" + nfa_start_state);
        //loop through the nfa state numbers
        for(int loop = 0; loop < nfa_states.size(); loop++){
            //Loop through all the NFA transition nodes
            for(int temp = 0; temp < nfa_transitions.size(); temp++) {
                NFATransitionNode obj = nfa_transitions.get(temp);
                if (obj.getInState().compareTo("" + nfa_states.get(loop)) == 0) {
                    //If the transition is a epsilon transition
                    if (obj.getTransition() == 'e') {
                        //check if the nfa state number is already in the ArrayList
                        boolean check = checkIt(nfa_states, obj.getToState());
                        if (check == true) {
                            nfa_states.add(obj.getToState());
                        }
                    }
                }
            }
        }
        ret.setNFAStates(nfa_states);
        for(int outloop = 0; outloop < nfa_accept_states.length; outloop++) {
            for (int accloop = 0; accloop < nfa_states.size(); accloop++) {
                if(nfa_accept_states[outloop] == Integer.parseInt(nfa_states.get(accloop))){
                    ret.setAccept(true);
                    break;
                }
            }
            if(ret.getAccept() == true){
                break;
            }
        }
        return ret;
    }

    //This method checks to see if a state is already in the ArrayList so there won't be duplicates
    public static boolean checkIt(ArrayList<String> nfa_states, String state){
        for(int temp = 0; temp < nfa_states.size(); temp++){
            if(nfa_states.get(temp).compareTo(state) == 0){
                return false;
            }
        }
        return true;
    }
    /**
     * Creates a DFA from a text file and then takes strings indicated in the text file and tells you whether they are accepted or rejected in the language
     *
     * @param filename Text file that contains instructions on how to create DFA
     * @throws IOException If there contains an error with reading the file
     */
    public static void resolveDFA(String filename, String outputname) throws IOException{
        int num_states; //Stores number of states in DFA
        char[] alphabet; //Stores characters in alphabet
        int start_state; //Indicates start state

        HashMap map = new HashMap(); //Stores ArrayLists that contain all possible transitions when reading a symbol from a string

        FileWriter writer = new FileWriter(outputname);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        try {
            //Initiailize file reader
            FileReader file_reader = new FileReader(filename);
            BufferedReader buffered_reader = new BufferedReader(file_reader);

            //Get number of states
            String line = buffered_reader.readLine();
            num_states = Integer.parseInt(line);

            //Get alphabet
            line = buffered_reader.readLine();
            alphabet = line.toCharArray();
            int key = -1;

            //Read in transitions and create DFANode for each state and possible transitions to be made when a symbol is read
            for (int i = 0; i < num_states; i++) {
                ArrayList<DFANode> nodeArr = new ArrayList<DFANode>(); //Arraylist to store DFANodes for each possible transition for a state
                for (int j = 0; j < alphabet.length; j++) {
                    line = buffered_reader.readLine();
                    String[] temp = line.split(" ");
                    DFANode tempNode = new DFANode();
                    key = Integer.parseInt(temp[0]);
                    tempNode.setID(key);
                    temp[1] = temp[1].replace("’", "").replace(" ", "");
                    tempNode.setAlphabet(temp[1]);
                    tempNode.setDest(Integer.parseInt(temp[2]));
                    nodeArr.add(j, tempNode); //Store DFANode in state arrayList
                }
                map.put(key, nodeArr); //Store the arraylist for the current state to hashmap map
            }

            //get start state
            line = buffered_reader.readLine();
            start_state = Integer.parseInt(line);
            ArrayList<DFANode> temp = (ArrayList<DFANode>) map.get(start_state);
            Iterator<DFANode> it = temp.iterator();
            while (it.hasNext()) {
                DFANode t = it.next();
                t.setStart(true);
            }

            //get accept states
            line = buffered_reader.readLine();
            String[] accept = line.split(" ");
            for(int i = 0; i<accept.length; i++) {
                int acc = Integer.parseInt(accept[i]);
                ArrayList<DFANode> arr = (ArrayList<DFANode>) map.get(acc);
                it = arr.iterator();
                while (it.hasNext()) {
                    it.next().setAccept(true);
                }
            }
            //Read in the strings in text file and prints whether it is accepted or rejected from the DFA
            while (null != (line = buffered_reader.readLine())) { //loop through all strings indicated in text file
                ArrayList<DFANode> state = (ArrayList<DFANode>) map.get(start_state); //Creates ArrayList that indicates current state

                for (int i = 0; i < line.length(); i++) { //Iterates through string
                    it = state.iterator();
                    boolean found = false;
                    while (!found && it.hasNext()) {
                        DFANode tempNode = it.next();
                        String chk = tempNode.getAlphabet().substring(1,2);
                        if (chk.equals(line.substring(i, i + 1))) { //If current symbol in string matches transition symbol
                            state = (ArrayList<DFANode>) map.get(tempNode.getDest()); //Change to next state indicated
                            found = true;
                        }
                    }
                }
                if(state.get(0).getAccept())
                    bufferedWriter.write("true\n");
                else
                    bufferedWriter.write("false\n");
            }
            bufferedWriter.close();
            buffered_reader.close(); //close buffered reader
        } catch (FileNotFoundException e) {
            System.out.println(e);
            System.exit(1);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

}