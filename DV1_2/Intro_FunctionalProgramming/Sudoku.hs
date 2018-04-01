module Sudoku where

import Test.QuickCheck
import Data.Char
import Data.List
import Data.Maybe

data Sudoku = Sudoku [[Maybe Int]]
 deriving ( Show, Eq )

 
rows :: Sudoku -> [[Maybe Int]]
rows (Sudoku rs) = rs

--Uppgift A1-A3
-------------------------------------------------------------------------

--[A1]-- ~~~~~~~~~~~~~~
-- allBlankSudoku is a sudoku with just blanks, replicates "Nothing" 
-- into a length 9 and then that into a nested list.
allBlankSudoku :: Sudoku
allBlankSudoku =  Sudoku (replicate 9 (replicate 9 Nothing))

--[A2]-- ~~~~~~~~~~~~~~
-- isSudoku sud checks if sud is really a valid representation of a sudoku.
-- Flattens the list to check the total amount of elements,
-- and checks if the amount of rows is 9
-- Finally checks if the length of every row is 9
isSudoku :: Sudoku -> Bool
isSudoku a 
 | length (concat b) == 81 && length b == 9 
   && map length b == replicate 9 9 = True
 | otherwise = False
 where b = rows a
 
--[A3]-- ~~~~~~~~~~~~~~ 
-- isSolved sud checks if sud is already solved, i.e. there are no blanks
-- Flattens the sudoku, checks if there's an element Nothing and then negates.
isSolved :: Sudoku -> Bool
isSolved a = not(Nothing `elem` (concat  (rows a)))


--Uppgift B1-B2
-------------------------------------------------------------------------
--[B1]-- ~~~~~~~~~~~~~~
-- printSudoku sud prints a representation of the sudoku sud on the screen
-- Uses a helper function and a converter function
printSudoku :: Sudoku -> IO ()
printSudoku =  putStrLn . unlines . printSudoku' . rows

-- Helpfunction for printing, remake the Sudoku into a list of strings
printSudoku' :: [[Maybe Int]] -> [String]
printSudoku' = map (map toChar)

--[B2]-- ~~~~~~~~~~~~~~
-- readSudoku file reads from the file, and either delivers it, or stops
-- if the file did not contain a sudoku
-- Uses a helper function to return an IO String
readSudoku :: FilePath ->IO Sudoku
readSudoku file = 
    do 
    newsud <- readFile file
    
    if not(isSudoku ( (readSudoku' newsud)))  
    then error "This is not a Sudoku" 
    else return (readSudoku' newsud)

-- Same way as printSudoku' but reversed
readSudoku':: String -> Sudoku
readSudoku' ourLine = Sudoku [[toInt b | b <- a]|a<- (lines ourLine)]

--[Helpfunctions]--
-- Conversion function Int->Char
toChar :: Maybe Int -> Char
toChar a 
  | a == Nothing = '.'
  | otherwise = maybe ' ' intToDigit (a)
  
-- Conversion Function Char->Int
toInt :: Char -> Maybe Int 
toInt x  
 | x == '.' = Nothing
 | otherwise = Just (digitToInt x)
 

--Uppgift C1-C3
-------------------------------------------------------------------------
--[C1]-- ~~~~~~~~~~~~~~
-- cell generates an arbitrary cell in a Sudoku
cell:: Gen (Maybe Int)
cell = frequency
    [ (8,return Nothing)
     ,(2, do
         x<- choose (1,9)
         return (Just x)
    )
    ]

--[C2]-- ~~~~~~~~~~~~~~
-- an instance for generating Arbitrary Sudokus
instance Arbitrary Sudoku where
  arbitrary =
    do rows <- sequence [ sequence [ cell | j <- [1..9] ] | i <- [1..9] ]
       return (Sudoku rows)

--[C3]-- ~~~~~~~~~~~~~~    
-- quickcheck ready function for testing Sudokus	
prop_Sudoku :: Sudoku -> Bool
prop_Sudoku = isSudoku


--Uppgift D1-D3
-------------------------------------------------------------------------
--[D1]-- ~~~~~~~~~~~~~~
-- 3x3, 9 in Each Sudoku
type Block = [Maybe Int]


-- Row,Column, total of 81 positions in each Sudoku
type Pos   = (Int, Int )


-- Checks if a block has doubles of a digit
-- First makes a list without Nothings, match against list without
-- Nothing & all "doubles" removed, if true then we have the same
-- list on both sides
isOkayBlock :: Block -> Bool
isOkayBlock x = nub oblock == oblock  
 where oblock = [a | a <- x, a /= Nothing]

--[D2]-- ~~~~~~~~~~~~~~
--Takes a Sudoku, reshapes the elements into 9 Blocks
--Uses a helper function to deconstruct the list instead of take/drop combo
--Begins by letting us manipulate the Sudoku with rows.
--Maps the destructor over the whole nested list, grouping everything into
--threes, transposes this so that we get a beneficial setup.
--The deconstructor however generates a bracket too many, concat it away.
--Now that we have the list setup in the proper way we want, deconstruct it
--into blocks again and finally get rid of the extra inner brackets.
blocks :: Sudoku-> [Block]
blocks sud = (transpose $ rows sud) ++ (rows sud) ++ ( blocks' sud)
blocks' = map concat . blocks'' . concat . transpose . map blocks'' . rows


--Takes a list, restructures/deconstructs it into groupings of 3
--This approach was more comfortable in contrast to taking and dropping.
--Since that would require some kind of guard or listcomprehension to
--constantly drop the correct amount
blocks'' :: [a] -> [[a]]
blocks'' (a:b:c:ds) = [a,b,c]: blocks'' ds
blocks'' [] = []

--[D3]-- ~~~~~~~~~~~~~~
-- Function to check if a Sudoku has the correct properties
-- uses two helper functions to check the rows.
isOkay :: Sudoku -> Bool
isOkay x
    |(and(map isOkayBlock ( blocks x)) 
    && (isOkayRow x) 
    && (isOkayColumn x))== True = True
    | otherwise = False


-- checks if a given row has the correct property
-- Helper function for the time being, but a function like this can have
-- many uses, therefor own name.	
isOkayRow :: Sudoku -> Bool
isOkayRow x 
 |map nub olist == olist = True
 |map nub olist == [[]]  = True 
 |otherwise          = False
 where olist = [[a | a <- row, a /= Nothing] | row <- (rows x)]

 
-- Basically the same thing however here we use transpose so we can
-- manipulate the columns into rows		
isOkayColumn :: Sudoku -> Bool
isOkayColumn x 
 | map nub olist == olist = True
 | map nub olist == [[]]  = True
 | otherwise          = False
 where olist = [[a | a <- row, a /= Nothing] | row <- (transpose (rows x))]

 
--Uppgift E1-E3
-------------------------------------------------------------------------
--[E1]-- ~~~~~~~~~~~~~~
--Checks if a given Sudoku has any blank spots, uses 4 helper functions
--and forces the generated list from everything to give its head 
blank :: Sudoku -> Pos
blank sud = 
 let xs = ( ( blank4 (blank3 sud) (blank'' (blank' sud))))
    in if null xs 
       then error "blank"
       else head xs


--Four helper functions:
--Function 1 & 2 Handle connecting a cell with a column index
--And then filters everything that's not a "Nothing"
--Third one, checks if there's a row with Nothing, and generates a list 
-- with Row indexes
-- Fourth one joins the two list into a list of tuples with Y,X values
-- The top function then uses the list generated through Four and takes
-- takes the head of it.
blank' :: (Enum b, Num b) => Sudoku -> [[(Maybe Int, b)]]
blank' sud  = map (`zip` [0..8]) $ rows sud

blank'' :: Eq a => [[(Maybe a, t)]] -> [[t]]
blank''  sud  = [ [snd b|  b <- a, (fst b) == Nothing ]| a  <-  sud]

blank3 :: (Enum t, Num t) => Sudoku -> [t]
blank3 sud  = [ snd a  |a <- ((rows sud) `zip` [0..8]), Nothing `elem` (fst a)]

blank4 :: (Eq t, Eq t1) => [t] -> [[t1]] -> [(t, t1)]
blank4 y x = concat( nub   [ [(i,b) |b <- a ] | a <- x,  i<- y])


--Property for checking if a Sudoku actually has nothing at the given spot
prop_blank:: Sudoku -> Bool
prop_blank sud = (rows sud !! fst (blank sud) !! snd(blank sud ) )== Nothing

--[E2]-- ~~~~~~~~~~~~~~
-- Operator function to updated a  given list at a given index
-- with a given value, generating a new list, uses a lower function  
(!!=) :: [a] -> (Int, a) -> [a]
(!!=) sud (ind, inj) = funcSplit (splitAt ind sud) inj

--An error ocurs when the function is named "prop_(!!=)"
prop_test::(Eq a)=> [a] -> (Int, a) -> Bool
prop_test [] (ind, inj )  = True
--[Probaly trash]-> prop_test [] (ind, _ )  = undefined
prop_test [a]  (ind, inj)  = ((!!=) [a] (ind, inj)) !! ind == inj 
    where
     _ = [a] /= []       --Testing in progress
     _ = inj `elem` [a] -- :3

-- Another general function, this can be used in different places
-- takes a toupled with lists and lets you put something 
-- between the head of the snd element and fst
-- and join these together at the same time
-- the middle part is _ because we replace it with the sent value

funcSplit :: ([a], [a]) -> a -> [a]
funcSplit (x,_:ys) a  = x ++ a : ys

--[E3]-- ~~~~~~~~~~~~~~
-- Relies heavily on funcSplit and SplitAt.
update :: Sudoku -> Pos -> Maybe Int -> Sudoku
update oursud ourpos inj = Sudoku 
 (funcSplit (splitAt y sud)( (sud !! y) !!= (x , inj) ))
 
    where y   = fst ourpos 
          x   = snd ourpos
          sud = rows oursud
          
prop_update:: Sudoku -> Pos -> Maybe Int -> Bool
prop_update oursud ourpos inj = oursud /=  (update oursud ourpos inj)
  -- | oursud ourpos inj = oursud /=  (update oursud (blank oursud) inj)
   -- | otherwise = False
    where
        _ = ourpos > (0,0)  -- Still testing :<
        _ = inj /= Nothing
--Uppgift F1-F4
------------------------------------------------------------------------------
--[F1]-- ~~~~~~~~~~~~~~
solve :: Sudoku -> Maybe Sudoku
solve s | isOkay s == False     = Nothing          -- There's a violation in s
        | isSolved s && isOkay s  == True     = Just s  -- s is already solved
        | otherwise = pickASolution possibleSolutions
  where
    nineUpdatedSuds   = [update s (blank s) (Just a)|a<-[1..9]] :: [Sudoku]
    possibleSolutions = [solve s' | s' <- nineUpdatedSuds]
 
--Nothings not interessting, so we get rid of those, if the list gets empty
--then catch that case and return Nothing
pickASolution :: [Maybe Sudoku] -> Maybe Sudoku
pickASolution suds =  
  let xs = [ correctsud | correctsud<- suds, correctsud /= Nothing ]
     in if null xs 
     then Nothing
     else head xs  

--[F2]-- ~~~~~~~~~~~~~~  
--Basically takes ReadSudoku, Solve and print into the same function.
readAndSolve :: FilePath -> IO ()
readAndSolve file = 
 do
 sud <- readSudoku file
 if  solve sud == Nothing
 then error "No Solutions"
 else printSudoku (fromJust ( (solve sud)))
 
--[F3]-- ~~~~~~~~~~~~~~
-- Takes two Sudokus, turns them into un-nested lists, zips into touples
-- Takes the single list and for each touple, compares their values
-- "Special case" being the Nothing, pattern matches in the end
isSolutionOf :: Sudoku -> Sudoku -> Bool
isSolutionOf   ssud osud 
 | (False `elem` (isSolutionOf' ssud osud) )== True = False 
 | otherwise = True

isSolutionOf'  ssud osud = nub [if snd a == Nothing then True else fst a == snd a  |a <-(isSolutionOf'' ssud osud)]
isSolutionOf'' ssud osud = (concat $ rows ssud) `zip` (concat $ rows osud)

--[F4]-- ~~~~~~~~~~~~~~
--prop_SolveSound :: Sudoku -> Property
--prop_SolveSound sud = (fromJust $ solve sud) `isSolutionOf` sud