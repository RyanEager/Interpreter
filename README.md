Interpreter
===========

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
    
