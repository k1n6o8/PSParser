import java.util.ArrayList;

public class Grammar{
    StringLabel LHS;
    ArrayList<ArrayList<StringLabel>> RHS;

    public Grammar(StringLabel x, ArrayList<ArrayList<StringLabel>> y){
        LHS = x;
        RHS = y;
    }

}
