import java.util.ArrayList;

/**
 * Created by Spencer on 11/2/2015.
 */
public class Node {
    private boolean is_nfa; //determines whether this node contains a NFA
    private String charac; //If this node doesn't contain a NFA then this stores the operation to perform
    private ArrayList<NFANode> nfa; //Stores nfa

    public Node(){
        is_nfa = false;
        charac = "";
        nfa = new ArrayList<NFANode>();
    }

    public boolean getIsNFA(){ return is_nfa;}

    public void setIsNFA(boolean is_nfa){ this.is_nfa = is_nfa;}

    public String getCharac(){ return this.charac;}

    public void setCharac(String charac){ this.charac = charac;}

    public ArrayList<NFANode> getNFA(){ return this.nfa;}

    public void setNFA(ArrayList<NFANode> nfa){ this.nfa = nfa;}



}