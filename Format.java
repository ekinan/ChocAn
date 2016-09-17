import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 *
 * @author Enis
 * This is where the patterns for formatting the date and time are stored,
 * and various other components.
 */

public class Format
{
    /**
     * How the date is outputted in files. M = month, d = days, u = year
     */
    public static final String DATE = "MM-dd-uuuu";
    /**
     * How the date is outputted in the date terminal.
     */
    public static final String DATE_TERMINAL = "MMMM dd, uuuu";
    /**
     * How the time is outputted in both files and terminal. k = hour, m = minute
     * s = seconds
     */
    public static final String TIME = "kk:mm:ss";
    /**
     * Used to output the fees in USD format (e.g. 12.32 = $12.32)
     */
    public static final NumberFormat USD_COST_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);  
    /**
     * Used as the delimiter to separate service records from one another in the file
     */
    public static final String SERVICE_RECORD_SEPARATOR = "!@#!$!"; //String of characters used to distinguish records from one another
    /**
     * Used to end the user's comments.
     */
    public static final String COMMENTS_DELIMITER = "#@$";
    
    /**
     * Returns a text string of the form "MM-DD-YYYY to MM-DD-YYYY"
     * where the left side is the start date, and the right side is the end date.
     * 
     * @param start The start date
     * @param end The end date
     * @return 
     */
    public static String GetDateRange(LocalDateTime start, LocalDateTime end)
    {
        return (start.format(DateTimeFormatter.ofPattern(Format.DATE)) + " to " + end.format(DateTimeFormatter.ofPattern(Format.DATE)));
    }
    
    
}