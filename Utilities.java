import static java.lang.Math.abs;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;

/**
 *
 * @author Enis
 * This class houses some useful utilities and generic functions that reduced
 * the project code
 */
public class Utilities
{
    /**
     * Global input scanner, to avoid having to create multiple scanner objects
     */
    public static Scanner sc = new Scanner(System.in); //This is the global scanner, to facilitate the input
    /**
     * Random number generator
     */
    public static Random random = new Random();
    /**
     * Information array. [1] = the identifier (member, provider or service), [2] = the component entered
     * (number, code, etc.), while [3] houses the error message
     */
    public static String[] info = new String[3]; //To prevent from creating multiple arrays on the heap;
    
    /**
     * Takes a string str, and truncates its length to match whatever's in limit,
     * if the string's length exceeds it. Otherwise, it leaves it as is.
     * 
     * @param str The string to be truncated (if necessary)
     * @param limit The maximum allowable length of the string
     * @return The truncated string, if its original length > limit. Otherwise
     * returns the same str untouched.
     */
    public static String AdjustStringToLimit(String str, int limit)
    {
        if (str.length() >= limit)
        {
            return str.substring(0, limit);
        }
        
        return str;
    }
    
    /**
     * This function takes a fee and a maximum allowable fee in limit,
     * It adjusts fee down to limit's value if fee > limit, otherwise
     * it leaves it untouched.
     * 
     * @param fee The fee to be adjusted
     * @param limit The maximum allowable fee
     * @return The modified fee if it's > limit, otherwise returns the fee
     * untouched
     */
    public static BigDecimal AdjustFeeToLimit(BigDecimal fee, BigDecimal limit)
    {
        if (fee.compareTo(limit) > 0)
        {
            return new BigDecimal(limit.toString());
        }
        
        return fee;
    }
    
    /**
     * This function takes an integer, x, and a maximum allowable value, limit.
     * It adjusts x to limit's value if x > limit, otherwise leaves x untouched.
     * 
     * @param x The value to be adjusted
     * @param limit The maximum allowable value
     * @return 
     */
    public static int AdjustIntToLimit(int x, int limit)
    {
        if (x > limit)
        {
            x = limit;
        }
        
        return x;
    }
    
    /**
     * Function sets info[0] to s0, info[1] to s1, info[2] to s2
     * 
     * @param s0 First string
     * @param s1 Second string
     * @param s2  Third string
     */
    public static void SetInfoParameters(String s0, String s1, String s2)
    {
        Utilities.info[0] = s0;
        Utilities.info[1] = s1;
        Utilities.info[2] = s2;
    }
    
    /**
     * Java labels the days as Monday = 1 to Sunday = 7. For this project
     * the week start is Saturday = 1 to Friday = 7. This function
     * just converts the first set of values (mapped down to modulo 7)
     * to the second set (i.e. permutation function). However note that
     * x needs to be between 0 and 6 and the function returns a 0 and 6 value.
     * 
     * In other words, Monday = 0 to Sunday = 6 for the way java does it,
     * while for this project Saturday = 0 to Friday = 6.
     * 
     * @param x
     * @return The permuted value of x. Returns a long to make it easy to input into Java's
     * temporal amount function
     */
    public static long FromDayOfWeekEnum(int x)
    {
        return ((x+2)%7);
    }
    
    /**
     * Same as above, except it returns the inverse of FromDaysOfWeek. So converts
     * x = Saturday (0) to Friday (6) to an x = Monday (0) to Sunday (6)
     * 
     * @param x The value of x that's to be permuted
     * @return If x = FromDayOfWeekEnum(y), then this function returns y.
     */
    public static long ToDayOfWeekEnum(int x)
    {
        return ((8*x-2)%7);
    }
     
    /**
     * Returns the numeric difference between start and end with respect to this project.
     * 
     * @param start The start day
     * @param end The end day
     * @return The difference 
     */
    public static long DayDifference(DayOfWeek start, DayOfWeek end)
    {
        return abs(Utilities.FromDayOfWeekEnum(start.getValue()-1)-Utilities.FromDayOfWeekEnum(end.getValue()-1));
    }
    
    /**
     * Returns an N-digit random number
     * 
     * @param n The number of digits
     * @return N-digit random number
     */
    public static int RandomNDigitNumber(int n)
    {
        int minNum = (int)Math.pow(10, n-1);
        return ((int)(minNum+random.nextFloat()*9*minNum));
    }
    
    /**
     * Given an integer key, this function looks up the element having that key
     * in container. Returns the element if it exists, otherwise returns null
     * 
     * @param <E>
     * @param key
     * @param container
     * @return a pointer to the element if it exists, null otherwise
     */
    public static <E> E LookUpElement(int key, TreeSet<E> container)
    {
        Iterator<E> iterator = container.iterator();
        E elem = null;
        
        while (iterator.hasNext() && (((Identification)(elem = iterator.next())).GetID() != key));
        
        if (!iterator.hasNext() && elem != null) //We've reached the end of the iterator, so we have to check to see if the last element matches the key
        {
            if (((Identification)elem).GetID() != key) //If it doesn't then the element does not exist in the list.
                elem = null;
        }
        
        return elem;
    }
    
    /**
     * This function is the same as LookUpElement, except that it takes the key as input from the user.
     * The idea is that info[0] stores which (member, service, provider) number we need, while
     * info[1] stores whether it's a number or code, and info[2] stores the error message for a key
     * that's <= 0.
     * 
     * 
     * @param <E>
     * @param container The data structure to do the look-up in.
     * @return A pointer to the element if it exists, otherwise null
     */
    public static <E> E ExtractElementFromInput(TreeSet<E> container) //0 = identifier, 1 = input entry, 2 = error message
    {
        int num = InputHandler.ExtractInt("Please enter the "+Utilities.info[0]+" "+Utilities.info[1]+": ", Utilities.sc);
        E curElem = null;
        if (num <= 0)
        {
            System.out.println(info[2]);
        }
        else
        {
            curElem = Utilities.LookUpElement(num, container);
            if (curElem == null)
            {
                System.out.println(info[2]); 
            }
            
        }
        
        return curElem;
    }
    
    /**
     * This function checks if num is a unique key in container. Returns true if so,
     * false otherwise.
     * 
     * @param <E>
     * @param num The key that we're checking
     * @param container The container to check in
     * 
     * @return True if num is a unique key, false otherwise
     */
    public static <E> boolean IsUniqueCode(int num, TreeSet<E> container)
    {
        Iterator<E> iterator = container.iterator();
        boolean isUnique = true;
        
        while (iterator.hasNext() && (isUnique = (((Identification)(iterator.next())).GetID() != num)));
        
        return isUnique;
    }
         
    /**
     * This function checks to see if date1 <= date2.
     * 
     * @param date1 The first date
     * @param date2 The second date
     * @return true if date1 <= date2, false otherwise
     */
    public static boolean IsWithinDate(LocalDateTime date1, LocalDateTime date2)
    {
        return (date1.compareTo(date2) < 0);
    }
    
    /**
     * Same as IsWithinDate, except for LocalDate objects.
     * 
     * @param date1 The first date
     * @param date2 The second date
     * @return true if date1 <= date2, false otherwise
     */
    public static boolean IsWithinDate(LocalDate date1, LocalDate date2)
    {
        return (date1.compareTo(date2) <= 0);
    }
    
    /**
     * This function checks if the date and refDate differ by at most 26 hours,
     * where date has to be greater than or equal to refDate.
     * 
     * 
     * @param date The date for comparison
     * @param refDate The reference date to compare to
     * @return True if date is greater than or equal to refDate and is no more than 26 hours
     * further from it; false otherwise.
     */
    public static boolean IsWithinValidTimeZone(LocalDateTime date, LocalDateTime refDate)
    {
        long diff = ChronoUnit.SECONDS.between(refDate, date);
        return (0 <= diff && diff <= Limits.MAX_TIME_ZONE_DIFFERENCE);
    }
    
    /**
     * This function takes a LocalDateTime object and calculates how many seconds are left until it reaches its
     * corresponding Friday, 23:59:00. For example if the date was December 3rd, 2015 at 21:34,
     * the function would calculate the number of seconds left until December 4th, 2015; 23:59:00
     * 
     * @param date The date whose end of the week we're calculating
     * @return the number of seconds left until the end of the week
     */
    public static long SecondsUntilWeekEnd(LocalDateTime date)
    {
        long numDaysLeftInSeconds = Utilities.DayDifference(date.getDayOfWeek(), Parameters.WEEK_END)*(86400);
        long numMinLeftInSeconds = ChronoUnit.SECONDS.between(date.toLocalTime(), LocalTime.parse("23:59:00", DateTimeFormatter.ofPattern(Format.TIME)));
        return (numDaysLeftInSeconds+numMinLeftInSeconds);
    }
}
