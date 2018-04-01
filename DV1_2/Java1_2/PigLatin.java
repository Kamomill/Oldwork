package exercises.basics;

import java.util.Scanner;

import static java.lang.System.*;

/**
 * This program translates from English to Pig latin
 * See,https://en.wikipedia.org/wiki/Pig_Latin
 * <p>
 * See StringProblems for inspiration
 */
public class PigLatin {

    public static void main(String[] args) {
        new PigLatin().program();
    }

    private final Scanner scan = new Scanner(in);

    private void program() {
        // Only for english
        out.println(toPigLatin("My name is Eric").equals("yMay amenay isway Ericway"));
        out.println(toPigLatin("My name is Eric"));

        // Kill with Ctrl + c or let IntelliJ do it
        while (true) {
            String line = scan.nextLine();
            out.println(toPigLatin(line));
        }
    }

    private String toPigLatin(String text) {
        StringBuffer buff = new StringBuffer(text); //modifiable copy of text
        int tic = 0;
        boolean havevowel =false;
        boolean firstvowel =false;
        String vowel = "AEIOUYaeiouy";
        String cons = "BCDFGHJKLMNPQRSTVXZbcdfghjklmnpqrstvxz";
        StringBuffer pig = new StringBuffer("");
        StringBuffer temp = new StringBuffer(""); // require a temp to hold the chars before a vowel
        while (tic < text.length()) {

/**This code exploits the fact that "indexOf" returns -1 if the char is not a index of the list of vowel or cons **/

        if((vowel.indexOf(buff.charAt(tic)) >= 0) && tic == 0){ //EDGE CASE, the first time the if below with "tic-1" will crash the program
            firstvowel = true;
            pig = pig.append(buff.charAt(tic));
            havevowel=true;
        }
        else if(vowel.indexOf(buff.charAt(tic)) >= 0){ // Due to edge case the if must be split
                if (vowel.indexOf(buff.charAt(tic - 1)) < 0 && cons.indexOf(buff.charAt(tic - 1)) < 0) { //If the char before "tic" was not a vowel or a cons (space or beginning of a sentence ect.
                    firstvowel = true;
                    pig = pig.append(buff.charAt(tic));
                    havevowel = true;
                } else if (vowel.indexOf(buff.charAt(tic)) >= 0) {
                    pig = pig.append(buff.charAt(tic));
                    havevowel = true;
                }
            }

            if(cons.indexOf(buff.charAt(tic) ) >= 0 && havevowel){
                pig = pig.append(buff.charAt(tic)); //if we have a vowel then add  the cons after the vowel
            }else if ((cons.indexOf(buff.charAt(tic)) >= 0 && !havevowel)){
                temp= temp.append(buff.charAt(tic)); // if we don't have a vowel then add the char to a temp to be added on later
            }

            if((buff.charAt(tic)==' ' && !firstvowel)){
                pig=pig.append(temp).append("ay ");
                temp=new StringBuffer("");
                havevowel=false;
            }else if(buff.charAt(tic)==' ' && firstvowel){
                pig=pig.append("way ");// we already have all the cons in the word
                havevowel = false;
                firstvowel = false;
            }

            if (tic==text.length()-1 &&!firstvowel) { //EDGE CASE, separated cuz of the space in "ay " and "way " should not be the last char
                pig = pig.append(temp).append("ay");
            } else if (tic==text.length()-1 &&firstvowel) {
                pig = pig.append("way");
            }


            tic++;
        }
        return pig.toString();
    }
}
      /*  while (tic < text.length()) {
            if (buff.charAt(tic) == ' ' || tic  == buff.length()) {// If we have passed a word or if @ end of string
                while (cons.indexOf(buff.charAt(letter)) <= 0){


                    if (vowel.indexOf(buff.charAt(letter)) < 0) {// exploiting the fact that .indexOf() returns -1 if index doesn't exist in the list
                        pig = pig.append(buff, letter+1, tic).append(buff.charAt(letter)).append("ay ");
                    }

                    if(vowel.indexOf(buff.charAt(letter)) >= 0){
                        pig = pig.append(buff, letter, tic).append("way");
                    }
                    letter=tic;
                }
                tic++;
            }*/

// Possible helper methods here


