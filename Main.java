import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Main {
    static HashMap<String, Grammar> grammarmap = new HashMap<String, Grammar>();
    static Stack<StringLabel> TerminalStack = new Stack<>();
    static Stack<ArrayList<StringLabel>> HistoryStack = new Stack<>();
    static Stack<Character> inputStack = new Stack<>();
    static StringLabel parentNode;
    static int currStackAdded = 0;
    static String firstoffendingToken;
    static String message;
    public static void main(String[] args) {
        grammarInterpreter();
        try{	
           FileReader fileread = new FileReader("input.txt");
           FileWriter filewrite = new FileWriter("output.txt");
           BufferedReader br = new BufferedReader(fileread);
           BufferedWriter bw = new BufferedWriter(filewrite);		
	   String contentLine = br.readLine();
	   while (contentLine != null) {
          bw.write(contentLine + recursiveDescent(contentLine) + '\n');
          contentLine = br.readLine();
          message = null;
          resetEverything();
        }
        br.close();
        bw.close();
        }
        catch (IOException ioe) 
        {
        ioe.printStackTrace();
        }
        
    }

    public static void grammarInterpreter(){
        try{	
           FileReader fileread = new FileReader("grammar.txt");
           BufferedReader br = new BufferedReader(fileread);		

	   String contentLine = br.readLine();
	   while (contentLine != null) {
          String inputParts[] = contentLine.split(":");        
          StringLabel LHS = new StringLabel(inputParts[0].trim());
          String tempInner[];       
          ArrayList<ArrayList<StringLabel>> RHS = new ArrayList<>();
          if(contentLine.length() > 0){
            String temp[] = inputParts[1].replaceAll("'","").split("\\|");
            for (int i = 0 ; i < temp.length; i++){
                tempInner = temp[i].replaceAll(";","").trim().split(" ");
                ArrayList<StringLabel> tempList = new ArrayList<>();
                for(int j = 0; j < tempInner.length; j++){
                    if (!tempInner[j].equals("")){
                        if(Character.isLowerCase(LHS.name.charAt(0))){
                            if(tempInner[j].charAt(tempInner[j].length()-1) == '+'){
                                tempInner[j] = tempInner[j].replace("+","");
                                tempList.add(new StringLabel(tempInner[j], "One or More"));
                            }
                            else if(tempInner[j].charAt(tempInner[j].length()-1) == '*'){
                                tempInner[j] = tempInner[j].replace("*","");
                                tempList.add(new StringLabel(tempInner[j], "Zero or More"));
                            }
                            else if(tempInner[j].charAt(tempInner[j].length()-1) == '?'){
                                tempInner[j] = tempInner[j].replace("?","");
                                tempList.add(new StringLabel(tempInner[j], "One or Zero"));
                            }
                            else
                                tempList.add(new StringLabel(tempInner[j]));
                        }
                        else
                            tempList.add(new StringLabel(tempInner[j]));
                    }
                    else{
                        LHS.kind = "Epsilon";
                    }
                }
                if(tempList.size() != 0)
                    RHS.add(tempList);
            }           
            grammarmap.put(LHS.name,new Grammar(LHS, RHS));
        }
          contentLine = br.readLine();
        }
        br.close();
        }
        catch (IOException ioe) 
        {
        ioe.printStackTrace();
        }
        
    }

    public static String recursiveDescent(String textline){
        StringLabel currentRule;
        for (int i = textline.length()-1; i >= 0; i--){ //push input char to stack
            if(textline.charAt(i) != ' ')
                inputStack.push(textline.charAt(i));
        }
        //insert recursive descent algorithm
        currentRule = grammarmap.get("start").LHS;
        TerminalStack.push(currentRule);
        expandTerminal(currentRule.name);
        return message;
    }


    public static boolean isPossible(String terminal, String character){
        boolean possible = false;
        ArrayList<ArrayList<StringLabel>> checker = grammarmap.get(terminal).RHS;
        if(checker.get(0).get(0).name.equals(character))
            possible = true;
        return possible;
    }

    public static void expandTerminal(String x){
        boolean symbolPlus = false;
        StringLabel check = TerminalStack.peek();
        String top = check.name;
        boolean isRequired = false;         
        while((!inputStack.empty() && !TerminalStack.isEmpty()) || symbolPlus ){
            if(TerminalStack.peek().kind != null){
                if(TerminalStack.peek().kind.equals("One or More"))
                    symbolPlus = true;
            }
            if(symbolPlus && !Character.isUpperCase(TerminalStack.peek().name.charAt(0))){
                check = TerminalStack.peek();
            }else{
                check = TerminalStack.pop();
            }
            top = check.name;
            if(currStackAdded > 0)
                currStackAdded--;
            if(Character.isUpperCase(top.charAt(0))){
                if(isPossible(top, inputStack.peek().toString())){
                    inputStack.pop();
                    resetforCurrent();
                    if(symbolPlus)
                        isRequired = true;
                }
                else{
                    if(HistoryStack.isEmpty()){
                        if(symbolPlus && isRequired){
                            symbolPlus = false;
                            TerminalStack.pop();
                            isRequired = false;
                        }
                        else if(parentNode.kind == "Epsilon")
                            TerminalStack.pop();
                        else{
                            break;
                        }
                    }
                    else{
                        backTrack();
                    }
                }               
            }
            else{
                if(symbolPlus && inputStack.empty()){
                    TerminalStack.pop();
                    symbolPlus = false;
                }
                else{
                    if (grammarmap.get(top).RHS.size()> 1){ // if more than one possible statement
                        for (int i = grammarmap.get(top).RHS.size()-1; i > 0 ; i--){
                            HistoryStack.push(grammarmap.get(top).RHS.get(i));;
                        }
                        parentNode = grammarmap.get(top).LHS;
                    }
                    currStackAdded = 0;
                    for(int i = grammarmap.get(top).RHS.get(0).size(); i > 0; i--){ // push production rules to stack
                        TerminalStack.push(grammarmap.get(top).RHS.get(0).get(i-1));
                        currStackAdded++;
                    }
                }
            }
        }
            if(!TerminalStack.isEmpty()){
                while (!TerminalStack.isEmpty() && grammarmap.get(TerminalStack.peek().name).LHS.kind == "Epsilon"){
                    TerminalStack.pop();
                }
            }

            if(inputStack.empty() && TerminalStack.empty())
                message = " - ACCEPT";
            else if (!inputStack.empty()){
                firstoffendingToken = inputStack.peek().toString();
                    message = " - REJECT. First Offending Token = '"+ firstoffendingToken+"'";  
            }
            else if (!TerminalStack.empty()){
                firstoffendingToken = grammarmap.get(TerminalStack.peek().name).RHS.get(0).get(0).name;
                    message = " - REJECT. Missing Token = '"+ firstoffendingToken+"'";
            }
    }

    public static void backTrack(){
        ArrayList<StringLabel> hatdog = new ArrayList<StringLabel>();
        for(int i=0 ; i < currStackAdded; i++)
            TerminalStack.pop();
        hatdog = HistoryStack.pop();
        currStackAdded = 0;
        for(int i = hatdog.size(); i > 0; i--){
            TerminalStack.push(hatdog.get(i-1));
            currStackAdded++;
        }
    }

    public static void resetforCurrent(){
        HistoryStack.clear();
        currStackAdded = 0;
        parentNode = new StringLabel();
    }

    public static void resetEverything(){
        TerminalStack = new Stack<>();
        HistoryStack = new Stack<>();
        inputStack = new Stack<>();
        parentNode = null;
        currStackAdded = 0;
        firstoffendingToken = null;
        message = null;
    }

}