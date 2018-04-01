package exercises.basics;

import com.sun.deploy.security.MozillaJSSDSASignature;

import static java.lang.System.*;

/*
        Transform string MI to MU using rules below. Possible?

        Rules
        1. Double the string after the M (that is, change Mx, to Mxx). For example: MIU to MIUIU.
        2. Add a U to the end of any string ending in I. For example: MI to MIU.
        3. Replace any III with a U. For example: MUIIIU to MUUU.
        4. Remove any UU. For example: MUUU to MU.
        Using these four rules is it possible to change MI into MU in a finite number of steps?


 */
public class MI2MU {

    public static void main(String[] args) {
        new MI2MU().program();
    }

    public void program() {
        String[] strings = generate(256);
        for (int i = 0; i < strings.length; i++) {
            out.println(i + " : " + strings[i]);
            if (strings[i].equals("MU")) {
                out.println("*** FOUND *** at" + i);
                break;
            }

        }
    }

    // Generate, in an orderly fashion, all combinations
    // of the rules (max 256)
    public String[] generate(int max) {
        String[] strs = new String[max + 1];
        strs[0] = "MI";
        int i=1;//string position where the result of a rule is stored
        for(int f=0; f<max && i<max;f++) { //f=current string position we apply rules to
            if(rule1(strs,i)){
                strs[i] = strs[f]+strs[f].substring(1);//Takes every thing after index 0 (MUI -> MUIUI)
                i++;
            }
            if(rule2(strs[f])){
               strs[i] = strs[f] + "U";
                i++;
            }

            if(rule3(strs[f])){
                strs[i] = strs[f].replaceFirst("III","");
                i++;
            }
            if(rule4(strs[f])){
                strs[i] =strs[f].replaceFirst("UU","");
                i++;
            }


        }


            // TODO

        return strs;
    }
   boolean rule1 (String[] str, int curr){  //Always true, method is here for clarification and ascetic purposes
     return true;
    }

   boolean rule2(String str) {
    if (str.endsWith("I")) { //True if ends with I
           return true;
       }else {
           return false;
       }

    }
    boolean rule3 (String str){  //True if string contains III
        return (str.contains("III"));
    }

    boolean rule4 (String str){  //
        return (str.contains("UU"));
    }

}
// Create methods for the rules here
// All methods should be short (2-5 lines)
// Search for methods in String to use
