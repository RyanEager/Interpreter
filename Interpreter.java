package a5;

import a5.Value.Lyst;
import a5.Value.Symbol;
import java.util.HashMap;

/**
    This class provides an Interpreter for the grammer  given below.

       Expr -> Literal | Var | FCall | LetExpr | IfExpr
       Literal -> ListLiteral | SymbolLiteral
       ListLiteral -> [ {Literal} ]
       FCall -> FName ( {Expr} )
       FName -> UserFName | PrimFName
       LetExpr -> let {Def} Expr
       Def -> define Sig Expr
       Sig -> UserFName ( {Var} )
       IfExpr -> if Expr Expr Expr


       
    The start symbol is Expr.
    
    In these productions, the parentheses and brackets are terminal symbols; 
      the braces and vertical bars are metasymbols. 
    Also, the following are preterminals:
      Var:  a string that begins with a capital letter
      SymbolLiteral:  a string that begins with the backquote character `
      PrimFName:  one of the strings car, cdr, or cons
      UserFunctionName: a string that begins with a lower-case letter, 
        is not a PrimFName, and does not appear on the right-hand side of any rule. 
    
    
     A5.java, Parser.java, Token.java, & OrderedTree.java are taken from Jeff Smith's solutions for Assignment 2.
    
  */

public class Interpreter {
    //Hashmap for saving user defined functions
    HashMap<String, Value> environment = new HashMap();
    
    /**
     * Interprets a string for the given grammar
     * first parses the program into an syntax tree and then interprets it.
     * 
     * @param program the input string
     * @return the value of the program represented by the string
     * @throws IllegalArgumentException if a syntactic or semantic error is found
     */
    public Value interpret(String program){
        Parser parser = new Parser();
        OrderedTree parse = parser.parse(program);
        return interpretExpr(parse);
        
        
    }
    
    /**
     * Interprets an Expr from the syntax tree  
     * 
     * @param in in input string
     * @return the value of the program represented by the string
     */
    private Value interpretExpr(OrderedTree in){
        Token current = (Token) in.getRootData(); //the next symbol to be consumed
        switch (current.getType()){
            case "List": 
                return interpretList(in);
            case "LetExpr": 
                return interpretLetExpr(in);
            case "IfExpr": 
                return interpretIfExpr(in);
            case "SymbolLiteral":   
                return interpretSymbolLiteral(in);
            case "UserFName":
                return interpretUserFName(in);
            case "PrimFName":
                return interpretPrimFName(in);
        }   
        return new Symbol("ERROR!!!");
    }
    
    /**
     * Interprets a List from the syntax tree by first
     * creating an array of the members of the Lyst and 
     * then creating a new Lyst from said array
     * 
     * @param in in input string
     * @return the value of the Lyst
     */
    private Value interpretList(OrderedTree in){
        OrderedTree[] childern = new OrderedTree[in.getNumberOfChildren()]; 
        for(int i = 0; i < in.getNumberOfChildren(); i++){
            childern[i] = in.getKthChild(i+1);
        }
        return new Lyst(childern);
    }
    
    /**
     * Interprets a SymbolLiteral from the syntax tree by
     * creating a new symbol from the spelling of the SymbolLiteral
     * 
     * @param in in input string
     * @return the value of the SymbolLiteral
     */
    private Value interpretSymbolLiteral(OrderedTree in){
        Token current = (Token) in.getRootData();
        return new Symbol(current.getSpelling());
    }
    
    /**
     * Interprets a LetExpr from the syntax tree  
     * 
     * @param in in input string
     * @return body of LetExpr
     */
    private Value interpretLetExpr(OrderedTree in){
        Token firstChild = (Token) in.getKthChild(1).getRootData();
        //check to see if defining a fuction or symbol
        if(firstChild.getType() != "Def"){
            return interpretExpr(in.getKthChild(1));
        }
        //check for user-defiend functions with arguments
        if(in.getKthChild(1).getKthChild(1).getNumberOfChildren() != 0){
            throw new IllegalArgumentException("Error: user-defined functions with arguments");
        }
        
        //get name of user-defined function
        Token key = (Token) in.getKthChild(1).getKthChild(1).getRootData();
       
        //store user-defined function
        environment.put(key.getSpelling(), interpretExpr(in.getKthChild(1).getKthChild(2)));
        
        //checking and storing of multiple user-defined functions
        for(int i =1; i < in.getNumberOfChildren(); i++){
            //nthChild of the input 
            Token nthChild = (Token) in.getKthChild(i+1).getRootData();
            
            //check if nthChild defines user-defined function
            if(nthChild.getSpelling() == "Def"){
                
                //get name of user-defined function
                 key = (Token) in.getKthChild(i+1).getKthChild(1).getRootData();
                 //store user-defined function
                 environment.put(key.getSpelling(), interpretExpr(in.getKthChild(i+1).getKthChild(2)));
             }
  
        }
        //Body of LetExpr evluated and then returned
        return interpretExpr(in.getKthChild(in.getNumberOfChildren()));
    }
    
   
    
    /**
     * Interprets a IfExpr from the syntax tree  
     * 
     * @param in in input string
     * @return if first argument is empty return 3rd argument, if not return 2nd argument
     */
    private Value interpretIfExpr(OrderedTree in){
        //get first argument
        Value firstArg = interpretExpr(in.getKthChild(1)); 
        
        // if first argument is empty return 3rd argument, if not return 2nd argument
        if(firstArg.empty()){
            return interpretExpr(in.getKthChild(3));
        }
        else{
            return interpretExpr(in.getKthChild(2));
        }
    }
    
    /**
     * Interprets a UserFName from the syntax tree  
     * 
     * @param in in input string
     * @return the value of user-defined function
     */
    private Value interpretUserFName(OrderedTree in){
        //check for arugmenets in user-defined function
        if(in.getNumberOfChildren() != 0){
            throw new IllegalArgumentException("Error: user-defined functions with arguments");
        }
        //get name of user-defined function
        Token key = (Token) in.getRootData();
        //get value of user-defined function and return
        return environment.get(key.getSpelling());

    }
    
    /**
     * Interprets a PrimFName from the syntax tree  
     * 
     * @param in in input string
     * @return the value of Car || cdr || cons
     */
    private Value interpretPrimFName(OrderedTree in){
        //get root to check function
        Token root = (Token) in.getRootData();
        switch(root.getSpelling()){
            case "car": 
                //check for illegal number of args
                if(in.getNumberOfChildren() != 1){
                    throw new IllegalArgumentException("Error: Illegal number of arguments");
                }
                //check for empty list
                if(in.getKthChild(1).getNumberOfChildren() == 0){
                    throw new IllegalArgumentException("Error: Empty List");
                }
                //get the arg
                Token currentList = (Token) in.getKthChild(1).getRootData();
                
                //if it is a single symobol return that symbol
                if( currentList.getType() == "SymbolLiteral"){
                    return new Symbol(currentList.getSpelling());
                }
                //if it is a List get the first element of that list and return
                else if(currentList.getType() == "List"){
                    currentList = (Token) in.getKthChild(1).getKthChild(1).getRootData();
                    return new Symbol(currentList.getSpelling());
                }
                //interpret the expression and return the first element of the lyst
                else{
                    return interpretExpr (in.getKthChild(1)).members[0];
                }
                
                
            case "cdr":
                //check for illegal numbe of args
                if(in.getNumberOfChildren() != 1){
                    throw new IllegalArgumentException("Error: Illegal number of arguments");
                }
                //check for empty list
                if(in.getKthChild(1).getNumberOfChildren() == 0){
                    throw new IllegalArgumentException("Error: Empty List");
                }
                //get first arg
                Token firstChild = (Token) in.getKthChild(1).getRootData();
                
                //if it is a list create a new list without the first element
                if(firstChild.getType() == "List"){
                    OrderedTree[] shortList = new OrderedTree[in.getKthChild(1).getNumberOfChildren() -1];
                
                    for(int i = 0; i < in.getKthChild(1).getNumberOfChildren() - 1; i++){
                        shortList[i] = in.getKthChild(1).getKthChild(i+2);
                    }
                    return new Lyst(shortList);
                }
                //interpret the expression and return without the first element
                else{
                    Value interpList = interpretExpr(in.getKthChild(1));
                    Symbol[] shortList = new Symbol[interpList.members.length - 1];
                    for(int i = 0; i < interpList.members.length -1; i++){
                        shortList[i] = interpList.members[i+1];
                    }
                    return new Lyst(shortList);
                }
            
                
            case "cons":
                //check for illegal number of arguments
                if(in.getNumberOfChildren() != 2){
                    throw new IllegalArgumentException("Error: Illegal number of arguments");
                }
                //get first arg
                firstChild = (Token) in.getKthChild(1).getRootData();
                //get second arg
                Token secondChild = (Token) in.getKthChild(2).getRootData();
                //type check for args
                if(firstChild.getType() == "List" || secondChild.getType() == "SymbolLiteral"){
                    throw new IllegalArgumentException("Error: Illegal type of arguments, must be Symbol then List");
                }
                
                //if second arg is a List create a new list with 1st arg apendign to the front of 2nd arg
                if(secondChild.getType() == "List"){
                
                    OrderedTree[] combinedList = new OrderedTree[in.getKthChild(2).getNumberOfChildren() + 1 ];
                
                    combinedList[0] = in.getKthChild(1);
                    for(int i = 1; i <= in.getKthChild(2).getNumberOfChildren(); i++){
                        combinedList[i] = in.getKthChild(2).getKthChild(i);
                    }
                return new Lyst(combinedList);
                }
                //interpret second arg and append 1st arg to the front
                else{
                    Value interpList = interpretExpr(in.getKthChild(2));
                     Value[] combinedList = new Symbol[interpList.members.length + 1 ];
                     
                     
                     firstChild = (Token) in.getKthChild(1).getRootData();
                     if(firstChild.getType() == "SymbolLiteral"){
                        combinedList[0] =  new Symbol(firstChild.getSpelling());
                     }
                     else{
                         combinedList[0] = interpretExpr(in.getKthChild(1));
                     }
                     for(int i = 0; i < interpList.members.length; i++){
                        combinedList[i+1] = interpList.members[i];
                    }
                return new Lyst(combinedList);
                     
                }
                
        }
        return new Symbol("Problem PFN");
    }
    
}
