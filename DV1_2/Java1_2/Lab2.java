
import java.io.*;
import java.util.*;

public class Lab2 {
    public static String pureMain(String[] commands) {
        StringBuilder sb = new StringBuilder();

        SellBidComparator sell = new SellBidComparator();
        PriorityQueue<Bid> sell_pq = new PriorityQueue<>(sell);

        BuyBidComparator buy= new BuyBidComparator();
        PriorityQueue<Bid> buy_pq = new PriorityQueue<>(buy);

        for(int line_no=0;line_no<commands.length;line_no++){ //MARK [1] For open
            String line = commands[line_no];
            if( line.equals("") )continue;

            String[] parts = line.split("\\s+");
            if( parts.length != 3 && parts.length != 4)
                throw new RuntimeException("line " + line_no + ": " + parts.length + " words");
            String name = parts[0];
            if( name.charAt(0) == '\0' )
                throw new RuntimeException("line " + line_no + ": invalid name");
            String action = parts[1];
            int price;
            try {
                price = Integer.parseInt(parts[2]);
            } catch(NumberFormatException e){
                throw new RuntimeException(
                        "line " + line_no + ": invalid price");
            }


            if( action.equals("K") ) {
                buy_pq.add(new Bid(name,price));

            } else if( action.equals("S") ) {
                sell_pq.add(new Bid(name,price));

            } else if( action.equals("NK") ){
                buy_pq.update(new Bid(name, price),
                              new Bid(name, Integer.parseInt(parts[3])));


            } else if( action.equals("NS") ){

                sell_pq.update(new Bid(name, price),
                               new Bid(name, Integer.parseInt(parts[3])));

            } else {
                throw new RuntimeException(
                        "line " + line_no + ": invalid action");
            }

            if( sell_pq.size() == 0 || buy_pq.size() == 0 )continue;

            if((sell_pq.minimum()).bid <= (buy_pq.minimum()).bid){
                sb.append(( buy_pq.minimum()).name + " köper från "
                        + (sell_pq.minimum()).name + " för "
                        + (buy_pq.minimum()).bid+" kr\n");

                sell_pq.deleteMinimum();
                buy_pq.deleteMinimum();
            }
        }                                                       //MARK [1] For closed

        sb.append("Orderbok:\n");

        sb.append("Säljare:");

        while(sell_pq.size() > 0){
            sb.append((sell_pq.minimum()).name + " "
                    + (sell_pq.minimum()).bid  + " kr");
            sell_pq.deleteMinimum();
            sb.append(", ");
        }

        sb.append("\nKöpare:");

        while (buy_pq.size()>0){
            sb.append((buy_pq.minimum()).name + " "
                    + (buy_pq.minimum()).bid + " kr");
            buy_pq.deleteMinimum();
            sb.append(", ");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        final BufferedReader actions;
        if( args.length != 1 ){
            actions = new BufferedReader(new InputStreamReader(System.in));
        } else {
            actions = new BufferedReader(new FileReader(args[0]));
        }

        List<String> lines = new LinkedList<String>();
        while(true){
            String line = actions.readLine();
            if( line == null)break;
            lines.add(line);
        }
        actions.close();

        System.out.println(pureMain(lines.toArray(new String[lines.size()])));
    }
}