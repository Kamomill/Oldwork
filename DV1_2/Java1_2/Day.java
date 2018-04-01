package exercises.basics;

import static java.lang.System.*;

import java.util.Scanner;

/*
   Some day calculations
   See also https://en.wikipedia.org/wiki/Determination_of_the_day_of_the_week#A_tabular_method_to_calculate_the_day_of_the_week
 */
public class Day {

    public static void main(String[] arg) {
        new Day().program();
    }

    void program() {
        final int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        final String[] days = {"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"}; //Changed the order du to easier to calc given the value of day from link above
        final int beginningOfTime = 1754;    // Year
        int monthtable[] = {0, 3, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5}; // A month table for georgian calenders
        int monthtableleap[] = {6, 2, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5};// same but adapted to  a leap year
        int numberOfDay = 0;

        int year;
        int month;
        int day;
        int nmonth = 0;
        int date;
        int leapyear = 0;

        // -- In ----------------
        Scanner scan = new Scanner(in);

        out.print("Enter a year: ");
        year = scan.nextInt();

        out.print("Enter a month number: ");
        month = scan.nextInt();
        out.println(daysInMonth[month - 1]);

        out.print("Enter a day: ");
        day = scan.nextInt();


        // ---- Process -----------
        if ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 != 0))) { //Because leap years...
            leapyear = 1;
        }

        while (nmonth < (month - 1)) {   //Calculates the number of days in every month before the month they entered
            numberOfDay = numberOfDay + daysInMonth[nmonth];
            nmonth++;
        }
        numberOfDay = numberOfDay + day;

        if (leapyear == 1 && month > 2) { // When it's a leap year the year i 1 day longer and becomes one longer on the 1st of mars compared to a normal year
            numberOfDay++;
        }

        if (leapyear == 0) {
            //(d  +    m       +       y    +   (y/4)  c==0,5,3,1 => (0,(7-2),(7-4),(7-6))%7) %7
            date = ((day + monthtable[month - 1] + (year % 100) + ((year % 100) / 4) + (7 - (((year / 100) % 4) * 2) % 7)) % 7);

        } else {      //Takes month number from an other table
            date = ((day + monthtableleap[month - 1] + (year % 100) + ((year % 100) / 4) + (7 - (((year / 100) % 4) * 2) % 7)) % 7);

        }
        // ---- Out ----------
        if (leapyear == 1) {
            out.println(year + " is a leap year");
            out.println("Day number:" + (numberOfDay));
        } else if (leapyear == 0) {
            out.println(year + " is NOT a leap year");
            out.println("Day number: " + numberOfDay);
        }

        //((day + monthtable[month-1] + (year % 100) + ((year % 100) / 4) + (7 - (((year / 100) % 4) * 2) % 7)) % 7) is correct!
        out.println("Day is " + days[date]);
       /* Test outputs
        out.println(monthtable[month-1]);
        out.println(year % 100);
        out.println(((year % 100) % 4)*2);
        out.println(7 - ((year % 100) % 4)*2) ;
        out.println((day + monthtable[month-1] + (year % 100) + ((year % 100) / 4) + (7 - (((year / 100) % 4) * 2) % 7)) % 7);
       */
    }
}
