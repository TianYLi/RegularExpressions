/**
 * Created by Spencer on 11/2/2015.
 */
public class NFATransitionNode {
    private String NFA_in_state; //indicates state that you are in
    private char NFA_transition; //indicates transition character
    private String NFA_to_state; //indicates state to go to if transition character is read

    public NFATransitionNode(String in_state, char transition, String to_state){

        this.NFA_in_state = in_state;
        this.NFA_transition = transition;
        this.NFA_to_state = to_state;
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