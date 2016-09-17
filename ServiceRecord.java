import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Enis
 * This is the class that stores the basic fields required in a ChocAn
 * service record. They match what's listed in the requirement documents.
 * 
 * It also implements compareTo and equals for insertion into the tree data
 * structure. Service records are organized in chronological order based
 * on their current date and time (i.e. earlier services precede later ones).
 */
public class ServiceRecord implements Comparable<ServiceRecord>
{
    private LocalDateTime currentDateTime = null;
    private LocalDate serviceDate = null;
    private int providerNumber = 0;
    private int memberNumber = 0;
    private int serviceCode = 0;
    private String comments = "";
    
    /**
     * 
     * @param currentDateTime_
     * @param serviceDate_
     * @param providerNumber_
     * @param memberNumber_
     * @param serviceCode_
     * @param comments_ 
     */
    public ServiceRecord(final LocalDateTime currentDateTime_, final LocalDate serviceDate_, final int providerNumber_,
                            final int memberNumber_, final int serviceCode_, final String comments_)
    {
        this.currentDateTime = currentDateTime_;
        this.serviceDate = serviceDate_;
        this.providerNumber = providerNumber_;
        this.memberNumber = memberNumber_;
        this.serviceCode = serviceCode_;
        this.comments = comments_;
    }
    
    /**
     * 
     * @param other 
     */
    public ServiceRecord(final ServiceRecord other)
    {
        
        this.currentDateTime = other.currentDateTime.minusDays(0);
        this.serviceDate = other.serviceDate.minusDays(0);
        this.providerNumber = other.providerNumber;
        this.memberNumber = other.memberNumber;
        this.serviceCode = other.serviceCode;
        this.comments = other.comments;
    }
    
    /**
     * 
     * @return The date and time in which this record was made
     */
    public LocalDateTime GetCurrentDateTime()
    {
        return this.currentDateTime;
    }
    
    /**
     * 
     * @return The date in which the service was provided
     */
    public LocalDate GetServiceDate()
    {
        return this.serviceDate;
    }
    
    /**
     * 
     * @return Provider number
     */
    public int GetProviderNumber()
    {
        return this.providerNumber;
    }
    
    /**
     * 
     * @return Member number
     */
    public int GetMemberNumber()
    {
        return this.memberNumber;
    }
    
    /**
     * 
     * @return Service code
     */
    public int GetServiceCode()
    {
        return this.serviceCode;
    }
    
    /**
     * 
     * @return Comments associated with the service record
     */
    public String GetComments()
    {
        return this.comments;
    }
    
    /**
     * Note that two service records could be written concurrently if two providers
     * were in the same time zone. Because TreeSet in Java doesn't allow duplicates, 
     * I modify compareTo slightly so that it places duplicates after one another.
     * 
     * @param o
     * @return Same as normal compareTo, except when the two services are equal
     * in date and time it returns a 1 so that TreeSet can still insert the record.
     */
    public int compareTo(ServiceRecord o)
    {
        int compareToVal = this.currentDateTime.compareTo(o.currentDateTime);
        return (compareToVal == 0 ? 1 : compareToVal); 
    }
    
    public boolean equals(Object o)
    {
        return (this.currentDateTime.equals(((ServiceRecord)o).currentDateTime));
    }
    
    /**
     * Ignore, used for debugging purposes.
     * 
     * @return 
     */
    public String toString()
    {
        String str = this.currentDateTime.format(DateTimeFormatter.ofPattern(Format.DATE + " " + Format.TIME)) + "\n";
        str += this.serviceDate.format(DateTimeFormatter.ofPattern(Format.DATE)) + "\n";
        str += this.providerNumber + "\n";
        str += this.memberNumber + "\n";
        str += this.serviceCode + "\n";
        str += this.comments + "\n";
        
        return str;
    }
}
