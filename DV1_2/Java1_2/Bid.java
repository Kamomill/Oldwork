public class Bid {
    final public String name;
    final public int bid;

    public Bid(String name, int bid) {
        this.name = name;
        this.bid = bid;
    }

    public int hashCode() {
        return 1 + 23*bid + 31*name.hashCode();
    }

    public boolean equals(Object obj) { //Send in a Bid object instead
        if (obj == null || !(obj instanceof Bid)) return false;

        Bid bid = (Bid) obj;

       /* if (this.bid == bid.bid) //MARK this or the other one?
        {return true;
        } else return false;*/

        //Alternative
        if(this.hashCode() == bid.hashCode()){//MARK This one!
            return true;

        } else {
            return false;
        }
    }

    public String toString(){
        return (this.name + " " + this.bid);
    }
}

