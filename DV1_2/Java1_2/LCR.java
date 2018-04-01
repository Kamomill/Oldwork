package exercises.products;

import java.util.Random;
import java.util.Scanner;

import static java.lang.System.*;

/**
 * LCR Game
 * http://en.wikipedia.org/wiki/LCR_%28dice_game%29
 */
public class LCR {

    public static void main(String[] args) {
        new LCR().program();
    }

    void program() {

        final int roll = 1;    // Command for command line
        final int quit = 2;

        final int nPlayers = 3;                           // Number of players
        final char[] result = {' ', ' ', ' '};            // Results for players
        final String[] names = {"Olle", "Pelle", "Fia"};  // Player(names)
        final int[] chips = {3, 3, 3};       // Chips for players
        int actual = 0;                      // Actual player
        boolean done = false;                // Game over
        boolean aborted = false;             // Game aborted
        Random rand = new Random();
        Scanner scan = new Scanner(in);


        Player player1= new Player(3,"Olle");
        Player player2= new Player(3,"Pelle");
        Player player3= new Player(3,"Fia");

        Player[] players={player1,player2,player3}; // Cerates a list of objects named players
        Dice dice=new Dice();

        out.println("LCR Game started");
        out.println("Commands are: 1 = roll, 2 = quit");
        out.println(players[0].name + "=" + players[0].chips + "," + players[1].name+ "=" + players[1].chips
                + "," + players[1].name+ "=" + players[2].chips);


        // ---- Input  ------
        while (!done) {
            out.print("Player is " + players[actual].name + " > ");
            out.println();
            int cmd = scan.nextInt();

            switch (cmd) {
                case roll:
                    // -------- Process ---------------
                    calcChip(players,dice,actual, result); //Calculates how the chips will be distributed after the roll

                    outPutPrint(players, result); //Prints a lot of text

                    done = haveWiner(players, done);//Checks to se if we have a winner

                    actual = (actual + 1) % 3; // Makes it to the next players turn
                    break;

                case quit:
                    aborted = true;
                    break;

                default:
                    out.println("?");
            }

            if (aborted) {
                break;
            }
            char[]sides = dice.getsides();
            result[0] = sides[6];
            result[1] = sides[6]; //Clears result with a "null"-dice state, dice side 6= ' '
            result[2] = sides[6];

        } // End Game loop
        gameEnd(chips, names, aborted, done); //Presents the winner
    }

    //-------------------------------------------------------------------------
    void rolldice(char result[], Dice dice,int diceroll ) {
        //dice rolls a player has done

        char[]sides = dice.getsides();
        result[diceroll] = sides[(dice.roll())];

    }

    void calcChip(Player[] players, Dice dice, int actual, char result[]) {
        if(players[actual].getchips()==0) {
            out.println("No chips,no dice sry brah"); //Player has no chips so he cant roll
        } else {

            int nchip = players[actual].getchips(); //actual players start amount of chips

            int diceNo = 0; // amount of dice player has thrown
            while (diceNo < 3 && nchip >0) {//either player rolls 3 dice or player rolls as manny as he/she had chips to start with

                rolldice(result,dice, diceNo); //Makes randomized dice rolls

                if (result[diceNo] == 'L') {
                    players[actual].chips = players[actual].chips - 1; //-1 on actual player
                    players[((actual - 1) + 3) % 3].chips = players[((actual - 1) + 3) % 3].chips + 1; // +1 on left player
                } else if (result[diceNo] == 'R') {
                    players[actual].chips = players[actual].chips - 1; //-1 on actual player
                    players[(actual + 1) % 3].chips = players[(actual + 1) % 3].chips + 1;            //+1 on right player
                } else if (result[diceNo] == 'C') {
                    players[actual].chips = players[actual].chips - 1; //-1 on actual player
                }
                diceNo += 1;
                nchip  -= 1;
            }
        }

    }

    void outPutPrint(Player[] players, char result[]) {
        out.println("Result: " + result[0] + " " + result[1] + " " + result[2]);
        out.println(players[0].name + "=" + players[0].chips+ "," + players[1].name + "=" + players[1].chips
                + "," + players[2].name + "=" + players[2].chips);
    }

    boolean haveWiner(Player[] players, boolean done) {

        if (players[0].chips + players[1].chips == 0 || players[1].chips+ players[2].chips == 0 || players[2].chips + players[0].chips == 0) { //  If any 2 players has 0 chips then...
            done = true; //We are done
        }
        return done;
    }

    void gameEnd(int chips[], String names[], boolean aborted, boolean done) {

        if (aborted) {
            out.println("Aborted");
        } else {
            //Present the winner
            if (chips[0] > 0)
                out.println("Game over! Winner is " + names[0]);
            else if (chips[1] > 0)
                out.println("Game over! Winner is " + names[1]);
            else
                out.println("Game over! Winner is " + names[2]);

        }
    }
}

class Player {
    int chips;//current amount of chips for a player
    final String name;

    public int getchips() {
        return this.chips;
    }

    public Player(int chips,String name) {
        this.chips=chips;
        this.name = name;
    }
}

class Dice{
    private Random rand = new Random();
    final char[] sides = {'L', 'R', 'C', '*', '*', '*', ' '}; //Left, Right, Center, keep, keep ,keep, (no dice to roll (n/a))

    public int roll() {
        return rand.nextInt(5);
    }
    public char[] getsides() {
        return this.sides;
    }

}