import java.time.DayOfWeek;

/**
 *
 * @author Enis
 * These are the main constants used in the program.
 */
public class Parameters
{
    public static final DayOfWeek WEEK_START = DayOfWeek.SATURDAY; //Start of the week for ChocAn, I kept it at Saturday
    public static final DayOfWeek WEEK_END = DayOfWeek.FRIDAY; //Friday is the end day of the week, specifically on 23:59:00
    public static final long WEEK_LENGTH = Math.abs(Utilities.DayDifference(Parameters.WEEK_START,Parameters.WEEK_END)); //The number of days between Saturday and Friday
    
    /**
     * Time parameters
     */
    public static final long SECONDS_PER_DAY = 86400;
    public static final long SECONDS_PER_MINUTE = 60;
    public static final long SECONDS_PER_HOUR = 3600;
}
