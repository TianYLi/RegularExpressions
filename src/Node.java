/**
 * Created by Spencer on 11/2/2015.
 */
public class Node {
    private String type; //indicates whether it is a node or a character ("node" is node, "character" is character)
    private String NFA_in_state; //indicates state that you are in
    private char NFA_transition; //indicates transition character
    private String NFA_to_state; //indicates state to go to if transition character is read
    private String character; //character that is stored

    public Node(String type, String in_state, char transition, String to_state){
        this.type = type;
        this.NFA_in_state = in_state;
        this.NFA_transition = transition;
        this.NFA_to_state = to_state;
        this.character = "";
    }

    public Node(String type, String character) {
        this.type = type;
        this.character = character;
    }

    public String getType() {
        return this.type;
    }

    public String getChara() {
        return this.character;
    }
    public String getInState(){
        return this.NFA_in_state;
    }

    public char getTransition(){
        return this.NFA_transition;
    }

    public String getToState(){
        return this.NFA_to_state;
    }
}