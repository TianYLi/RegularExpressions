import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
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
        String[] alphabet = line.split("");

        //the regular expression
        String regExp = buffered_reader.readLine();
        String[] regExpArr = regExp.split("");
        Node n = convertNFA(regExpArr);
        //e is epsilon, N is empty set, U is union, o is concatenation, * is star operator


        //convert to NFA

        //convert NFA to DFA (USE PA2)

        //sequence of strings
        while (null != (line = buffered_reader.readLine())) {
            String str = line;

            //Use DFA (PA1) to simulate strings
        }
    }

    public static Node convertNFA(String[] regExpArr) {
        Stack<Node> stack = new Stack<Node>();
        for(int i = 0; i < regExpArr.length; i++) {
            if(!(stack.isEmpty()) && stack.peek().getType().equals("Character") && stack.peek().getChara().equals(")")){
                ArrayList<Node> arr = new ArrayList<Node>(); //the operation to be performed
                while(!(stack.peek().getChara().equals("("))) {
                    Node n = stack.pop();
                    if(!(n.getChara().equals(")")))
                        arr.add(n);
                }
                stack.pop(); //pops the "("

                stack.push(convertHelper(arr));
                //operation to convert to NFA and push the node back on
            }
            else {
                Node n = new Node("Character", regExpArr[i]);
                stack.push(n);
            }
        }
        return stack.pop();
    }

    private static Node convertHelper(ArrayList<Node> arr) {
        //conver the given characters into NFA (i.e. 1* into nodes and transitions)
        return null;
    }
}