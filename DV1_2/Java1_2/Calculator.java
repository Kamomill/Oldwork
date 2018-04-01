package exercises.products.calc;

import java.util.*;

import static java.lang.Math.*;
import static java.lang.System.*;


/**
 * Calculator (Flow is: tokenize -> infix -> postfix -> eval) for
 * NON-negative numbers
 * <p>
 * NOTE : A pure static class
 */
public class Calculator {

    // Error messages
    public final static String NO_INPUT = "No input";
    public final static String MISSING_OPERATOR = "Missing operator or parenthesis";
    public final static String MISSING_OPERAND = "Missing or bad operand";
    public final static String DIV_BY_ZERO = "Division with 0";
    public final static String OP_NOT_FOUND = "Operator not found";

    // Definition of operators and parentheses
    public static final String OPERATORS = "+-*/^";
    public static final String PARENTHESES = "()";

    // Stack to hold operators and parentheses
    private final static Deque<String> opStack = new ArrayDeque<>();
    // The output from infix2postfix
    private final static List<String> output = new ArrayList<>();

    private Calculator() {  // Pure static class no instances possible
    }

    // Only public method
    public static double eval(String expr) {
        if (expr.length() == 0) {
            throw new IllegalArgumentException(NO_INPUT);
        }
        // Initialize
        opStack.clear();
        output.clear();

        List<String> tokens = tokenize(expr);
        List<String> postfix = infix2Postfix(tokens);
        out.print(postfix);
        return evalPostfix(postfix);
    }

    // Evaluate RPN expression
    private static double evalPostfix(List<String> postfix) {
        // A stack for evaluation. NOTE: No parentheses in data
        Deque<Double> evalStack = new ArrayDeque<>();

        for (String token : postfix) {
            if (!OPERATORS.contains(token)) {  // It's a value
                evalStack.push(Double.valueOf(token));
            } else {                          // It's an operator
                try {
                    double d1 = evalStack.pop();
                    double d2 = evalStack.pop();
                    double result = applyOperator(token, d1, d2);// Apply op, push result
                    evalStack.push(result);
                } catch (EmptyStackException e) {
                    throw new IllegalArgumentException(MISSING_OPERAND);
                }
            }
        }
        double result = evalStack.pop();  // Pop final result
        if (!evalStack.isEmpty()) {
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }
        return result;
    }

    // This method converts a list of strings (in infix order)
    // to a list of strings in postfix order (parentheses dropped)
    private static List<String> infix2Postfix(List<String> infix) {
        Deque<String> stack = new ArrayDeque<>(); //stack
        List<String> postfix = new ArrayList<>(); // the final string
      //  String operand = "0123456789";
        String opslow = "+-";
        String opshigh = "*/^";
        String assocleft = "-/";

        for (int i = 0; i < infix.size(); i++) {


            if (infix.get(i).equals("(")) { // Checks to se if "i" is a operand (num 0-9)
                stack.push(infix.get(i));//push it in to the stack
            }else if (infix.get(i).equals(")")) { // Checks to se if "i" is a operand (num 0-9)
                boolean done = false;
                while (!done) {
                    if (stack.peek().equals("(")) {
                        stack.pop();
                        done = true;
                    } else {
                        postfix.add(stack.pop());
                    }
                }
            }else if (opslow.contains(infix.get(i))|| opshigh.contains(infix.get(i))) { // Checks to se if "i" is a op low=(+-) high=(*/^) assocleft (-/^)
                if (stack.size() != 0) { //EDGECASE peaking on empty
                    if (opslow.contains(infix.get(i)) && opshigh.contains(stack.peek())) {
                        postfix.add(infix.get(i));//adds it last to the list
                    } else if (((opslow.contains(infix.get(i)) && opslow.contains(stack.peek())) || (opshigh.contains(infix.get(i)) && opshigh.contains(stack.peek()))) && (assocleft.contains(infix.get(i)))) {
                        postfix.add(stack.pop());
                        stack.push(infix.get(i));
                    } else
                        stack.push(infix.get(i));
                }else {stack.push(infix.get(i));}

            } else { // Checks to se if "i" is a operand (num 0-9)
                postfix.add(infix.get(i)); // adds it last in the list
            }
            // TODO Check it later, it follows the algorithm in pp, cant check du to checkOP is not done...  DERP! checkOP is not used, uh oh..
        }
        while(stack.size() != 0) {
            postfix.add(stack.pop());
        }
        return postfix;
    }
    // This method checks the stack to see if operators with
    // higher precedence or if same precedence associates to left

 //   private static void checkOp(String op) {

        // (TODO) built in in infix2postfix

  //  }


    // If found ")" pop stack until "(" found
    private static void checkParentheses() {
        boolean found = false;
        while (!opStack.isEmpty()) {
            String op = opStack.pop();
            if (op.equals("(")) {
                found = true;
                break;
            } else {
                output.add(op);
            }
        }
        if (!found) {
            throw new IllegalArgumentException(MISSING_OPERATOR);
        }
    }

    private static List<String> tokenize(String str) {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            // Is this an operator or a parenthese?
            if (OPERATORS.indexOf(ch) >= 0 || PARENTHESES.indexOf(ch) >= 0) {
                tokens.add(String.valueOf(ch));
            } else if (Character.isDigit(ch)) {
                // If digit collect all digits to a number
                StringBuilder b = new StringBuilder();
                b.append(ch);
                while (true) {
                    i++;
                    if (i < str.length() && Character.isDigit(str.charAt(i))) {
                        b.append(str.charAt(i));
                    } else {
                        i--;  // Reset if not a digit or beyond length
                        break;
                    }
                }
                tokens.add(b.toString());
            } else {
                // Here we skip white space and others
            }
        }
        return tokens;
    }

    private static double applyOperator(String op, double d1, double d2) {
        switch (op) {
            case "+":
                return d1 + d2;
            case "-":
                return d2 - d1;
            case "*":
                return d1 * d2;
            case "/":
                if (d1 == 0) {
                    throw new IllegalArgumentException(DIV_BY_ZERO);
                }
                return d2 / d1;
            case "^":
                return pow(d2, d1);
        }
        throw new RuntimeException(OP_NOT_FOUND); // Used for programming errors, caused by us
    }

    private static int getPrecedence(String op) {
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    private static Assoc getAssociativity(String op) {
        if ("+-*/".contains(op)) {
            return Assoc.LEFT;
        } else if ("^".contains(op)) {
            return Assoc.RIGHT;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    private enum Assoc {
        LEFT,
        RIGHT
    }

}
