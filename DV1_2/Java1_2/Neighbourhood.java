package exercises.products.simulation;

import java.util.Random;

import static java.lang.Math.*;
import static java.lang.System.*;

/**
 * The logic behind the simulation
 */
public class Neighbourhood {

    private final Agent[][] locations;
    private static final Random rand = new Random();
    private final double threshold;
    private final int nRows;
    private final int nCols;
    private int nEmpty;       // Keep track of number of Agents.NONE. Convenience

    // Threshold is quotient  (satisfied / total neighbours)
    public Neighbourhood(int nRows, int nCols, double threshold) {
        this.threshold = threshold;
        locations = new Agent[nRows][nCols];
        this.nRows = locations.length;    // Convenience
        this.nCols = locations[0].length;
    }

    // Randomly placement of agents according to distribution
    public void setData(Agent[] agents, double[] distribution) {

        // Generate all the agents
        Agent[] tmp = new Agent[nRows * nCols];
        int n = 0;
        for (int i = 0; i < agents.length; i++) {
            int m = n + (int) round(distribution[i] * nRows * nCols);
            for (int j = n; j < m; j++) {
                tmp[j] = new Agent(agents[i]); // Must have own copy!!! Have state!
                if (agents[i].getType() == Type.NONE) {
                    nEmpty++;  // Nice to have, see moveDissatisfied
                }
            }
            n = m;
        }

        for (int i = tmp.length - 1; i > 0; i--) {
            int j = rand.nextInt(i);
            Agent ii = tmp[i];
            tmp[i] = tmp[j];
            tmp[j] = ii;
            //out.println("Swap " + i + ":"  + j + "-->" + Arrays.toString(is));
        }
        // Move to matrix
        for (int i = 0; i < tmp.length; i++) {
            locations[i / nCols][i % nCols] = tmp[i];
        }
    }

    // Mostly for testing
    public void setData(Agent[] agents) {
        for (int i = 0; i < agents.length; i++) {
            locations[i / nCols][i % nCols] = agents[i];
            if (agents[i].getType() == Type.NONE) {
                nEmpty++;   // Nice to have
            }
        }
    }


    public void moveDissatisfied() {

        Random rand = new Random();  //Random generator
        int[] switched = new int[nCols*nRows]; // An arrray of 0, used to check if a agent already has switched
        Agent[] newagents =  toArray(locations);
        int current = 0;
        for (int turn = newagents.length; turn > 0; turn--) {
            int x = rand.nextInt(turn);
            if(newagents[current].getType()!=Type.NONE && !newagents[current].isSatisfied()) { // if current agent is not satisfied nor a type.NONE, try to switch
                int temp = 0;
                boolean hasswitched=false;
                for (int y = 0; y <newagents.length; y++) {//goes thro the whole array and if it finds a switched value and it's position
                    if (switched[y] == 1 && y <= x + temp) {//is less than the random x+ the other values it has found, temp++.
                        temp++; //this whole for-loop is to counter a bias in the system
                    }
                }

                    if(!newagents[x+temp].isSatisfied() || newagents[x+temp].getType()==Type.NONE) { // if the
                        switchpos(switched, newagents, current, x, temp);
                    }//else{turn++;} //Possible edgecase

            }//else{turn++;} //Possible edgecase
            current += 1;
        }
        toMatrix(newagents);
    }


    public void switchpos(int[] switched,Agent[] newagents, int current, int x,int temp) {

        Agent holder;
        holder = newagents[x+temp];
        newagents[x+temp]=newagents[current];
        newagents[current]=holder;
        switched[current] = 1;

    }


    public Agent[] toArray(Agent[][] locations) {
        Agent[] arr = new Agent[nRows * nCols];
        for (int r = 0; r < this.nRows; r++) {
            for (int c = 0; c < this.nCols; c++) {
                arr[(r * nCols) + c] = locations[r][c];
            }
        }
        return arr;
    }
    void toMatrix(Agent[] old) {
       // Agent[] temp = new Agent [nRows*nCols];
        for (int i = 0; i < old.length; i++) {
            locations[i / nCols][i % nCols] = old[i];
        }
    }

    public void setSatisfied() {
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                //if(this.getAgent(row,col).getType()!=Type.NONE){
                    if (isSatisfied(row, col)) {
                         this.getAgent(row, col).setSatisfied(true);
                        } else {
                            this.getAgent(row,col).setSatisfied(false);
                        }
            //    }
            }
        }
    }



    public boolean isSatisfied(int row, int col) {;//This must receive a red or blue, NOT NONE
        double teamtot=0;
        double opptot=0;

       if(this.getAgent(row,col).getType()==Type.NONE)//EDGE CASE
            return true;// EDGE CASE, it has no type therfore it cant be satesfied or unsatesfied

        for (int r = -1; r <= 1; r++) {    //we check the 9 relevant positions
            for (int c = -1; c <= 1; c++) {
                if (isValidLocation(row + r, col + c)) { //we check for all nine locations around row col
                  if(this.getAgent(row+r,col+c).getType()==this.getAgent(row,col).getType()){

                      teamtot+=1.0;//also we never trigger an error due to array size because the "if" filters that away

                }else if(this.getAgent(row+r,col+c).getType()!=Type.NONE){
                      opptot+=1.0;
                  }

            }
        }// We now have 2 ints symbolising the amount of the respective colours and we want
      }
        teamtot-=1;
            // we then subtract the value in the middle since we only are interested in it's neighbors but we included it in the for loops

            if (teamtot == 0){
                return false;
            } else if(teamtot/(opptot+teamtot)>=this.threshold) {
                return true;
            }else {
                return false;
            }
    }


    private boolean isValidLocation(int row, int col) {
        return row >= 0 && col >= 0 && row < nRows && col < nCols;
    }


    // ------ Mostly for GUI -----------------

    public int getNRows() {
        return nRows;
    }

    public int getNCols() {
        return nCols;
    }

    public Agent getAgent(int row, int col) {
        return locations[row][col];
    }

    public String toString() {
        String s = "[";
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                s += locations[row][col];
                if (col != nCols - 1) {
                    s += ", ";
                }
            }
            if (row != nRows - 1) {
                s += lineSeparator();  // From System
            }
        }
        return s + "]";
    }


}

