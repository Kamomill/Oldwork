import Test.QuickCheck
import Expr
import Data.Maybe

instance Arbitrary Expr where
  arbitrary = sized arbExpr

--Generates a random expr
arbExpr :: Int -> Gen Expr
arbExpr s =
  frequency [ (1, do n <- arbitrary
                     return (Num (abs n)))

            , (s, do a <- arbExpr s'
                     b <- arbExpr s'
                     return (Add a b))

            , (s, do a <- arbExpr s'
                     b <- arbExpr s'
                     return (Mul a b))
         
            , (s, do a <- arbExpr s'
                     b <- arbExpr s'
                     return (Var "x")) -- !!!
     
            , (s, do a <- arbExpr s'    -- !!!
                     b <- arbExpr s'
                     return (Sin a ))


            , (s, do a <- arbExpr s'
                     b <- arbExpr s'
                     return (Cos a ))                     
            ]
 where
  s' = s `div` 2

-- Property that checks if a input is the same after a conversion expr-> string -> expr
prop_ShowReadExpr :: Expr -> Bool
prop_ShowReadExpr oexpr=
    case (readExpr (showExpr oexpr)) of
        Nothing -> False 
        _       -> oexpr == (fromJust( readExpr (showExpr oexpr)))

--Property that checks if expr is associative
prop_ShowReadAssoc a =
  readExpr (show a) == Just (assoc a)

assoc :: Expr -> Expr
assoc (Add (Add a b) c) = assoc (Add a (Add b c))
assoc (Add a b)         = Add (assoc a) (assoc b)
assoc (Mul (Mul a b) c) = assoc (Mul a (Mul b c))
assoc (Mul a b)         = Mul (assoc a) (assoc b)
assoc (Sin a)           = Sin (assoc a) 
assoc (Cos a)           = Cos (assoc a) 
assoc (Var a)           = Var a
assoc (Num a)           = Num a


