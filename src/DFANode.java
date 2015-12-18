/**
 * DFANode.java
 *
 * This is the Node class that is used to create DFANode objects.
 * It is used in the transition process when reading in symbols from a String to help determine whether it is in the language or not
 *
 * Authors: Spencer McDonald and Jack Li
 */
public class DFANode {
    private boolean accept; //True if it is a accepting state and false if not
    private boolean start; //True if it is a start state and false if not
    private String alphabet; //String of transition symbol
    private int next_nodes; //ID of next node
    private int id; //ID of this node

    public DFANode(){
        this.accept = false;
        this.start = false;
        this.next_nodes = 0;
        this.alphabet = "";
        this.id = -1;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setStart(boolean b) {
        this.start = b;
    }

    public String getAlphabet() {
        return this.alphabet;
    }

    public void setAlphabet(String a) {
        this.alphabet = a;
    }

    public int getDest() {
        return this.next_nodes;
    }

    public void setDest(int dest) {
        this.next_nodes = dest;
    }

    public boolean getAccept() {
        return this.accept;
    }

    public void setAccept(boolean b) {
        this.accept = b;
    }

    public int getID() {return this.id;}
}