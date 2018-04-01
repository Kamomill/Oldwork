package exercises.products.memory;


import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;

import java.util.ArrayList;
import java.util.List;
import java.math.*;
import java.util.Random;


import static java.lang.Math.*;

/**
 * A Board for a Memory Game
 *
 * See TestBoard for functionality
 */
public class Board {

    // All cards (must use a List like this)
    private final List<Card> cards = new ArrayList<>();

    private final Random rand = new Random();
    private final int size;

    // names should be in random order
    // names.length must be a even square >= 4
    public Board(String[] names)  {
        this.size = (int)Math.sqrt(names.length);

        //String name, int row, int col)
        if ((Math.sqrt(names.length) >= 4) && (Math.sqrt(names.length) % 1 == 0)) {
            for (int i = names.length - 1; i > 0; i--) { //Fisher Yates?
                int j = rand.nextInt(i);
                String ii = names[i];
                names[i] = names[j];
                names[j] = ii;
            }


            for (int i = 0; i < Math.sqrt(names.length); i++) {
                for (int j = 0; j < (int)Math.sqrt(names.length); j++) {
                    this.cards.add(new Card(names[(int) (Math.sqrt(names.length) * i) + j], j, i));//!

                }
            }
        }else {
           //FIX ERROR DOUCHEBAG.
        }

    }



    public List<Card> getCards() {
        return cards;
    }

    // Return side length of board
    public int size() {
        return this.size;
    }


    // Select Card at row, col
    public void select(int row, int col) {
      get(row, col).setStatus(Card.Status.VISIBLE);
    }

     // Reset the selected Cards
    public void resetSelected() {
        for(int i = 0; i< this.size()*this.size;i++){
            if(this.getCards().get(i).isVisible()){
                this.getCards().get(i).setStatus(Card.Status.INVISIBLE);
            }
        }

    }

    public boolean isEmpty() {
        boolean b = true;
        for (Card c : cards) {
            if (!c.isRemoved()) {
                b = false;
            }
        }
        return b;
    }

    // Remove the selected Cards
    public void removeSelected() {
        for(int i = 0; i< this.size()*this.size;i++){
            if(this.getCards().get(i).isVisible()){
                this.getCards().get(i).setStatus(Card.Status.REMOVED);
            }
        }

    }

    // Are the selected cards equal?
    public boolean sameSelected() {
        Card tempCard=new Card("Z", 0,0);
        boolean foundOne=false;
        for(int i = 0; i< this.size()*this.size; i++){
            if(this.getCards().get(i).isVisible() && !foundOne ){
                tempCard = this.getCards().get(i);
                foundOne=true;

            }else if(this.getCards().get(i).isVisible() && foundOne ){
                if(tempCard.equals(getCards().get(i))){
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isSelected(int row, int col) {
        return get(row, col).isVisible();
    }

    public boolean isRemoved(int row, int col) {
        return get(row, col).isRemoved();
    }

    // Get card at row, col
    public Card get(int row, int col) {
       /* for (int i = 0; i < this.size(); i++){
            if ( this.getCards().get(i).getRow() == row &&
                this.getCards().get(i).getCol() == col){

                return this.getCards().get(i);
            }
        }*/
        //Old code above,dident work, use this:
        if(size*col+row >=0 ||size*col+row <=size*size) {return getCards().get(size * col + row);}//if the algorithm is >=0 or is <=size*size, return that card


        return null;//otherwise return null
    }
}
