import java.util.ArrayList;

/**
 * Created by Spencer on 12/13/2015.
 */
public class NFANode {
    private int id; //indicates the NFANodes id number
    private boolean start; //determines whether this NFANode is a start state
    private boolean accept; //determines whether this NFANode is a accept state
    private ArrayList<NFANode> to_states; //stores states that current NFANode can transition to
    private ArrayList<String> transitions; //stores transition to state corresponding to same index in to_states ArrayList
    private ArrayList<String> id_states; //Indicates the id of the states corresponding to same indec in to_states ArrayList

    public NFANode(){
        this.id = 0;
        this.start = false;
        this.accept = false;
        this.to_states = new ArrayList<NFANode>();
        this.transitions = new ArrayList<String>();
        this.id_states = new ArrayList<String>();
    }

    public int getId(){ return this.id;}

    public void setId(int id){ this.id = id;}

    public boolean getStart(){ return this.start;}

    public void setStart(boolean start){ this.start = start;}

    public boolean getAccept(){ return this.accept;}

    public void setAccept(boolean accept){ this.accept = accept;}

    public ArrayList<NFANode> getToStates(){ return this.to_states;}

    public void setToStates(ArrayList<NFANode> to_states){ this.to_states = to_states;}

    public ArrayList<String> getTransitions(){ return this.transitions;}

    public void setTransitions(ArrayList<String> transitions){ this.transitions = transitions;}

    public ArrayList<String> getIdStates(){ return this.id_states;}

    public void setIdStates(ArrayList<String> id_states){ this.id_states = id_states;}
}