import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Enis
 * The purpose of this class is to organize service records
 * by the corresponding member or provider to which they belong to.
 * 
 * Essentially, each member and provider will have a subset of the service records
 * on disk associated with them, and this class serves to keep track of that.
 * It is a wrapper to storing these records in a LinkedList that differentiates itself
 * from other ServiceRecordLists by a having a unique key, the member or provider number
 * 
 * Note the records themselves are stored in chronological order in the LinkedList.
 */
public class ServiceRecordList implements Comparable<ServiceRecordList>
{
    /**
     * The unique key identifying which member or provider these records belong to
     */
    private int key = 0;
    /**
     * The records associated with this provider or member.
     */
    private LinkedList<ServiceRecord> records = new LinkedList<ServiceRecord>();
    
    /**
     * 
     * @param key_ 
     */
    public ServiceRecordList(final int key_)
    {
        this.key = key_;
    }
    
    /**
     * Does a shallow copy of the records.
     * Note that I never had to use the copy constructor, so this is OK.
     * Normally it's better to do a deep copy of data structures.
     * 
     * @param o 
     */
    public ServiceRecordList(final ServiceRecordList o)
    {
        this.key = o.key;
        
        this.records = o.records;        
    }
    
    /**
     * 
     * @return The unique key associated with this set of records
     */
    public int GetKey()
    {
        return this.key;
    }

    /**
     * Adds s to the back of the linked list, since service records
     * are in chronological order.
     * 
     * @param s 
     */
    public void AddServiceRecord(final ServiceRecord s)
    {
        this.records.addLast(s);
    }
    
    /**
     * Removes the record s, if it exists.
     * 
     * @param s
     * @return True if the removal was successful, false otherwise
     */
    public boolean RemoveServiceRecord(final ServiceRecord s)
    {
        return this.records.remove(s);
    }

    /**
     * 
     * @return An iterator to the beginning of this list.
     */
    public ListIterator<ServiceRecord> Start()
    {
        return this.records.listIterator(0);
    }
    
    /**
     * Service record lists are organized by their key.
     * 
     * @param o
     * @return 
     */
    public int compareTo(ServiceRecordList o)
    {
        if (key < o.key)
            return -1;
        else if (key > o.key)
            return 1;
        else
            return 0;
    }
}
