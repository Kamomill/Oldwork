import java.util.Comparator;

/**
 * Created by nitsche on 5/10/16.
 */
public class SellBidComparator implements Comparator<Bid>{

    @Override
    public int compare (Bid A, Bid B){

        if(A.bid > B.bid){
            return -1;

        }else if(A.bid < B.bid){
            return 1;

        }else{
            return 0;
        }

    }
}

