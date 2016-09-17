import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Enis
 * This is the class that will implement the word limits (i.e. maximum length
 * of the member digits, street address, etc.) as outlined in the requirements document.
 * 
 * The names are straightforward.
 */
public class Limits
{
    //These are where the name, address and comments limits are at
    public static final int SERVICE_NAME_LENGTH_LIMIT = 20;
    public static final int INFO_NAME_LENGTH_LIMIT = 25;
    public static final int CITY_NAME_LENGTH_LIMIT = 14;
    public static final int COMMENT_LENGTH_LIMIT = 100;
    public static final int STREET_ADDRESS_LENGTH_LIMIT = 25;
    public static final int STATE_LENGTH_LIMIT = 2;
    public static final int ZIP_CODE_LENGTH_LIMIT = 5;    

    //These are the number limits. Info is for providers and members
    public static final int INFO_NUMBER_LENGTH_LIMIT = 9;
    public static final int SERVICE_CODE_LENGTH_LIMIT = 6;
    
    //Consultation limits
    public static final int MEMBER_CONSULTATIONS_LIMIT = 999;
    
    //These are the fee limits
    public static final BigDecimal SERVICE_FEE_LIMIT = new BigDecimal("999.99");
    public static final BigDecimal WEEKLY_FEE_LIMIT = new BigDecimal("99999.99");
    
    /**
     * Maximum time zone difference allowed for provider dates
     */
    public static final long MAX_TIME_ZONE_DIFFERENCE = 93600;
}
