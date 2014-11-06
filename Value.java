package a5;


/**
 * Class for output of interpred program
 * @author ryan_eager
 */
public class Value {
    
    Symbol[] members = new Symbol[1];
    String spelling = "";

    /**
     * check if a Symbol or Lyst is empty
     * @return true if empty, false if not
     */
    public boolean empty(){
        if(spelling != "" || members.length != 0){
            return false;
        }
        return true;
    }
    
    /**
     * Class for representing symbols
     */
    public static class Symbol extends Value{
        
        /**
         * construstor for a new symbol
         * @param input spelling for the symbol
         */
        public Symbol(String input){
            spelling = input;
        }
        
        @Override
        public String toString(){
            return spelling;
        }
        
    }
    
    /**
     * Class for represting Lists of Symbols
     */
    public static class Lyst extends Value{
        
        /**
         * constortor for a new Lyst from an array of ordered tree
         * @param input members of the list
         */
        public Lyst(OrderedTree[] input){
            members = new Symbol[input.length];
            for(int i = 0; i < input.length; i++){
                Token current = (Token) input[i].getRootData();
                members[i] = new Symbol(current.getSpelling());
            }
        }
        
        /**
         * constortor for a new Lyst from an array of Values
         * @param input members of the list
         */
        public Lyst(Value[] input){
            members = new Symbol[input.length];
            for(int i = 0; i < input.length; i++){
                members[i] = (Symbol) input[i];
            }
            
        }
        
        
        @Override
        public String toString(){
            String out = "[ ";
            for(int i = 0; i < members.length; i++){
                out+= members[i].toString() + " ";
            }
            out += "]";
            return out;
        }
        
        
    }
    
}
