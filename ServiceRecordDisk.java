import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.TreeSet;

/**
 *
 * @author Enis
 * This is the disk that stores the service records associated with each provider
 * and member. Every time a member is served, that record gets written here.
 * Providers can also manually write their own records in the case
 * of hardware failure.
 * 
 * To avoid having to search the records one by one and figure out which record
 * belongs to what member when generating the reports, the fields recordsByMemberNum
 * and recordsByProviderNum were included that also organizes the records in "records"
 * by the corresponding member and provider that they are associated with.
 */
public class ServiceRecordDisk
{
    /**
     * The main data structure that stores the records. They are organized
     * in chronological order.
     */
    private TreeSet<ServiceRecord> records = new TreeSet<ServiceRecord>();
    
    /**
     * Organizes the records in "records" by member number.
     */
    private TreeSet<ServiceRecordList> recordsByMemberNum = new TreeSet<ServiceRecordList>();
    
    /**
     * Organizes the records in "records" by provider number.
     */
    private TreeSet<ServiceRecordList> recordsByProviderNum = new TreeSet<ServiceRecordList>();
    
    /**
     * 
     */
    public ServiceRecordDisk()
    {
    }
    
    /**
     * Does a shallow copy of each field. Note that this was not used.
     * @param o 
     */
    public ServiceRecordDisk(final ServiceRecordDisk o)
    {     
        this.records = o.records;
        this.recordsByMemberNum = o.recordsByMemberNum;
        this.recordsByProviderNum = o.recordsByProviderNum;
    }
    
    /**
     *
     * @return The contents of the disk
     */
    public TreeSet<ServiceRecord> GetRecords()
    {
        return this.records;
    }
    
    /**
     * This method takes a new service record and adds them to the disk.
     * It also updates the corresponding member and provider associated with
     * the record in the recordsByMemberNum and recordsByProviderNum fields
     * 
     * @param newRecord 
     */
    public void WriteToDisk(final ServiceRecord newRecord)
    {
        this.records.add(newRecord); //Add to the main record
        ServiceRecordList member = this.GetServiceList(this.recordsByMemberNum, newRecord.GetMemberNumber()); //Get the service records associated with the member listed in the record
        ServiceRecordList provider = this.GetServiceList(this.recordsByProviderNum, newRecord.GetProviderNumber()); //Same as above, but for the provider
        
        //Update the corresponding records
        member.AddServiceRecord(newRecord);
        provider.AddServiceRecord(newRecord);
    }
    
    /**
     * Returns an iterator that allows one to traverse through the service records associated
     * with memberNum.
     * 
     * @param memberNum the member number whose records we wish to traverse
     * @return the corresponding iterator belonging to the member
     */
    public ListIterator<ServiceRecord> StartAtMember(int memberNum)
    {
        return this.GetServiceList(this.recordsByMemberNum, memberNum).Start();
    }
    
    /**
     * Returns an iterator that allows one to traverse through the service records associated
     * with providerNum
     * 
     * @param providerNum the provider number whose records we wish to traverse
     * @return the corresponding iterator belonging to the provider
     */
    public ListIterator<ServiceRecord> StartAtProvider(int providerNum)
    {
        return this.GetServiceList(this.recordsByProviderNum, providerNum).Start();
    }

    /**
     * This function removes all records that have a chronological date <= to filterDate.
     * 
     * @param filterDate 
     */
    public void Filter(LocalDateTime filterDate)
    {
        Iterator<ServiceRecord> iterator = this.records.iterator();
        ServiceRecord current = null;
        
        //Service records are organized in chronological order, so we can simply traverse
        //and remove records while they are less than or equal to the filter date.
        while (iterator.hasNext() && (Utilities.IsWithinDate((current = iterator.next()).GetCurrentDateTime(), filterDate)))
        {
            iterator.remove(); //Remove the record from the main disk.
            
            ServiceRecordList member = this.GetServiceList(this.recordsByMemberNum, current.GetMemberNumber());
            ServiceRecordList provider = this.GetServiceList(this.recordsByProviderNum, current.GetProviderNumber());
            
            //Remove the record from the corresponding member's and provider's service records, if it exists
            member.RemoveServiceRecord(current);
            provider.RemoveServiceRecord(current);
        }
    }
    
    /**
     * Writes the contents of the disk to the file contained in filePath, if any exist
     * 
     * @param filePath
     * @throws IOException 
     */
    public void WriteRecordsTo(final String filePath) throws IOException
    {
        BufferedWriter writer = FileUtilities.OpenWriter(filePath);
        Iterator<ServiceRecord> iterator = records.iterator();
        if (iterator.hasNext())
        {
            String record = iterator.next().toString();
            record += Format.SERVICE_RECORD_SEPARATOR; //I repeat code here to prevent having a new line
            writer.write(record);            //inserted into the file after the first entry.
            while (iterator.hasNext())      //This makes it easier to read from the file in case we have only one service record on disk
            {
                record = "\n"+iterator.next().toString();
                record += Format.SERVICE_RECORD_SEPARATOR;
                writer.write(record);
            }            
        }        
        writer.close();        
    }
    
    /**
     * This function returns the service record list in treeToSearch associated with listKey.
     * If no records exist, the function adds a new service record list having key listKey
     * to the tree and returns it.
     * 
     * @param treeToSearch
     * @param listKey
     * @return 
     */
    private ServiceRecordList GetServiceList(TreeSet<ServiceRecordList> treeToSearch, int listKey)
    {
        ServiceRecordList listForSearch = new ServiceRecordList(listKey);
        ServiceRecordList actualList = treeToSearch.floor(listForSearch);
        
        if (actualList == null || actualList.GetKey() != listKey) //The list doesn't exist, so we create it and add it to treeToSearch
        {
            treeToSearch.add(listForSearch);
            actualList = listForSearch;
        }
        
        return actualList;
    }
    
    
    /**
     * Ignore, used for debugging purposes
     * 
     * @return 
     */
    public String toString()
    {
        String info = "";
        Iterator<ServiceRecord> current = records.iterator();
        while (current.hasNext())
        {
            info += current.next().toString() + "\n";
        }
        
        return info;
    }
    
}
