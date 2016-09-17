import java.util.Comparator;

/**
 *
 * @author Enis
 * This is a comparator class which compares two services based on the
 * lexicographic ordering of their names. It is used when extracting
 * the alphabetically ordered provider directory.
 */
public class ServiceAlphabetical implements Comparator<Service>
{
    /**
     * 
     * @param o1
     * @param o2
     * @return 
     */
    public int compare(Service o1,Service o2)
    {
        return (o1.GetName().compareTo(o2.GetName()));
    }
    
}
