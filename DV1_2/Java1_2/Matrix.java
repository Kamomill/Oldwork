package exercises.basics;

import java.util.Random;

import static java.lang.Math.sqrt;
import static java.lang.System.lineSeparator;

/**
 * A Integer Matrix class using 2D arrays
 * <p>
 * Same class as from samples (but here you should add some methods)
 */
public class Matrix {
    private final int nRows;  // Convenience
    private final int nCols;
    private final int[][] elems;

    // Constructor. Assume square matrix
    public Matrix(int[] array) {
        this.nRows = (int) sqrt(array.length);
        this.nCols = nRows;
        elems = new int[nRows][nCols];
        toMatrix(array);
    }

    // Constructor. Non-square matrix
    public Matrix(int[] array, int rows, int cols) {
        this.nRows = rows;
        this.nCols = cols;
        elems = new int[nRows][nCols];
        toMatrix(array);
    }

    // Helper method (private)
    private void toMatrix(int[] array) {
        for (int i = 0; i < array.length; i++) {
            elems[i / nCols][i % nCols] = array[i];
        }
    }


    // ----------- Added methods  ------------------

    public int[] toArray() {
        int[][] temp = elems;
        int[] arr = new int[nRows * nCols];
        for (int r = 0; r < this.nRows; r++) {
            for (int c = 0; c < this.nCols; c++) {
                arr[(r * nCols) + c] = temp[r][c];
            }
        }
        return arr;
    }

    public void randomSwap() {
        Random rand = new Random();
        int[] switched = new int[toArray().length];
        int[] newarr = new int[toArray().length];
        int current = 0;
        for (int turn = toArray().length; turn > 0; turn--) {
            int x = rand.nextInt(turn);

            switchpos(switched, newarr, current, x);
            current += 1;
        }
        toMatrix(newarr);

    }

    public void switchpos(int[] switched, int[] newarr, int current, int x) {
        int temp = 0;
        for (int y = 0; y <newarr.length; y++) {//goes thro the whole array and if it finds a switched value and it's possition
            if (switched[y] == 1 && y <= x + temp) {//is less than the random x+ the other values it has found, temp++.
                temp++;
            }
        }
            newarr[current] = toArray()[x+temp];
            switched[x+temp] = 1;
    }

    public int sumNeighbors(int row, int col) {
        int sum = 0;

        for (int r = -1; r <= 1; r++) {    //the highest amount  of possibe terms is 9
            for (int c = -1; c <= 1; c++) {
                if (isInside(row + r, col + c)) { //we check for all nine locations around [row][col]
                    sum += elems[row + r][col + c]; // and add them to sum if they belong to the matrix
                    //also we never trigger an error due to array size because the "if" filters that away
                }
            }
        }
        sum -= elems[row][col]; // but we then subtract the value in the middle since we only are interested in it's neighbors
        return sum;
    }




       /* if (isInside(row, col))
            sum = elems[row - 1][col - 1] + elems[row - 1][col] + elems[row - 1][col + 1] +
                    elems[row][col - 1] + elems[row][col] + elems[row][col + 1] +
                    elems[row + 1][col - 1] + elems[row + 1][col] + elems[row + 1][col + 1];

            else if ()

                    elems[row - 1][col - 1] + elems[row - 1][col] + elems[row - 1][col + 1] +
                    elems[row][col - 1]     + elems[row][col]     + elems[row][col + 1] +
                    elems[row + 1][col - 1] + elems[row + 1][col] + elems[row + 1][col + 1];*/


    // -------- End added methods -----------------------------

    public int sumCol(int index) {
        int sum = 0;
        for (int row = 0; row < nRows; row++) {
            sum += elems[row][index];
        }
        return sum;
    }

    public int get(int row, int col) {
        return elems[row][col];
    }

    public void set(int row, int col, int value) {
        elems[row][col] = value;
    }

    public int get(int index) {
        return elems[index / nCols][index % nRows];
    }

    public void set(int index, int value) {
        elems[index / nCols][index % nRows] = value;
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && col >= 0 && row < nRows && col < nCols;
    }

    public String toString() {
        String s = "[";
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                s += elems[row][col];
                if (col != nCols - 1) {
                    s += ", ";
                }
            }
            if (row != nRows - 1) {
                s += lineSeparator();  // From System, operating independent newline char
            }
        }
        return s + "]";
    }
}
