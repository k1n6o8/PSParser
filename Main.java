import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        grammarInterpreter();
        try{	
           FileReader fileread = new FileReader("input.txt");
           FileWriter filewrite = new FileWriter("output.txt");
           BufferedReader br = new BufferedReader(fileread);
           BufferedWriter bw = new BufferedWriter(filewrite);		
	   String contentLine = br.readLine();
	   while (contentLine != null) {
          bw.write(recursiveDescent(contentLine));
          contentLine = br.readLine();
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

        HashMap<String, String[]> grammarmap = new HashMap<String, String[]>();
        try{	
           FileReader fileread = new FileReader("grammar.txt");
           BufferedReader br = new BufferedReader(fileread);		

	   String contentLine = br.readLine();
	   while (contentLine != null) {
          String inputParts[] = contentLine.split(":");        
          String LHS = inputParts[0];
          String RHS[] = inputParts[1].replace(";", "").split("\\|");
          grammarmap.put(LHS, RHS);
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
        boolean accept;
        String output = "";
        //insert recursive descent algorithm
        //separate string by word???
        //check if included in grammar
        //if not possible for current backtrack(stacks)
        return output;
    }
}