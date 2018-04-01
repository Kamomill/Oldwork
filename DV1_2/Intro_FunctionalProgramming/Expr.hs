module Expr where 

import Data.Char
import Data.Maybe

data Expr        -- A recursive datatype that can handle decimals
  = Num Double
  | Add Expr Expr
  | Mul Expr Expr
  | Sin Expr
  | Cos Expr
  | Var Name
 deriving ( Eq )
type Name = String

--takes an expr and turns in into a more plesent to read string
showExpr :: Expr -> String

showExpr (Num n)   = show n 
showExpr (Add a b) = "("++ showExpr a ++ "+" ++ showExpr b ++ ")"  -- !!! 
showExpr (Mul a b) = "("++ showFactor a ++ "*" ++ showFactor b ++")"
showExpr (Var x)   = x

showExpr (Sin (Mul a b))= "sin (" ++ showExpr (Mul a b) ++ ")"
showExpr (Sin (Add a b))= "sin (" ++ showExpr (Add a b) ++ ")" 
showExpr (Sin a)   = "sin (" ++ showExpr a ++")"

showExpr (Cos (Mul a b))= "cos (" ++ showExpr (Mul a b) ++ ")"
showExpr (Cos (Add a b))= "cos (" ++ showExpr (Add a b) ++ ")"
showExpr (Cos a)   = "cos (" ++ showExpr a ++")"

showFactor :: Expr -> String
showFactor (Add a b)         = "("++ showExpr a ++ "+" ++ showExpr b ++")"
showFactor a                 = showExpr a

instance Show Expr where
  show = showExpr
  
--Evaluates the expression 
eval:: Expr -> Double -> Double
eval (Var n)   d = d 
eval (Num n)   d = n
eval (Add a b) d = eval a d + eval b d
eval (Mul a b) d = eval a d * eval b d 
eval (Sin a)   d = sin (eval a d) 
eval (Cos a)   d = cos (eval a d)  

-- * an expression is a '+'-chain of terms
-- * a term is a '*'-chain of factors
type Parser a = String -> Maybe (a,String)

num :: Parser Expr
num s = case number s of
    Just (n,s') -> Just (Num n, s')
    Nothing     -> Nothing
    
number (c:s)
    | isDigit c = Just (fst (head numb), snd (head numb))
    | otherwise = Nothing
  where
    numb = reads (c:s) :: [(Double,String)]


-- * an expression is a '+'-chain of terms
-- * a term is a '*'-chain of factors

expr, term :: Parser Expr
expr = chain term   '+' Add
term = chain factor '*' Mul

-- `chain p op f s1` parsers a "chain" of things.
--
--   * The things are parsed by the parser `p`.
--   * The things are separated by the symbol `op`.
--   * The things are combined by the function `f`.
-- For example "12+23+1+172" is a chain of numbers, separated by the symbol '+'.
chain :: Parser a -> Char -> (a -> a -> a) -> Parser a
chain p op f s1 =
  case p s1 of
    Just (a,s2) -> case s2 of
                     c:s3 | c == op -> case chain p op f s3 of
                                         Just (b,s4) -> Just (f a b, s4)
                                         Nothing     -> Just (a,s2)
                     _              -> Just (a,s2)
    Nothing     -> Nothing

-- `factor` parses a "factor": either a number or an expression surrounded by
-- parentheses
factor :: Parser Expr

factor ('(':s) =
   case expr s of
      Just (a, ')':s1) -> Just (a, s1)
      _                -> Nothing
      
factor ('x':s) = Just ((Var "x"),s) 


factor ('s':'i':'n':'(':s) =
   case expr s of
      Just (x,')':s1) -> Just ((Sin x), s1)
      _               -> Nothing

factor ('s':'i':'n':s) =
   case expr s of
      Just (x, s1) -> Just ((Sin x), s1)
      _            -> Nothing 

factor ('c':'o':'s':'(':s) =
   case expr s of
      Just (x,')': s1) -> Just ((Cos x), s1)
      _                -> Nothing 

factor ('c':'o':'s':s) =
   case expr s of
      Just (x, s1) -> Just ((Cos x), s1)
      _            -> Nothing          
      
factor s = num s


-- `readExpr` reads a string into an expression
readExpr :: String -> Maybe Expr
readExpr s =
  case expr (filter (/= ' ')s) of
    Just (a,"") -> Just a
    _           -> Nothing

-- Defines the type named Point    
type Point = (Double,Double) 

--Takes one expression, one scale and a tuple of (Width,Heigth) and cerates a list of Points    
points :: Expr -> Double -> (Int,Int)-> [Point]
points exp scale (w,h) = [((((realToPix a)+ rw)) , mid-(realToPix(eval exp a)) ) | a <- [(rescale* (-1) ),(rescale* (-1)  + 0.1) .. ( rescale)]]

  where
    rescale = ( scale * (fromIntegral w) / 2 )
    rw = (fromIntegral w) / 2
    rh = (fromIntegral h) 
    mid = (rw + rh ) / 2
    pixToReal :: Double -> Double -- converts a pixel x-coordinate to a real x-coordinate
    pixToReal x = x*scale

    realToPix :: Double -> Double -- converts a real y-coordinate to a pixel y-coordinate
    realToPix y = y/scale



