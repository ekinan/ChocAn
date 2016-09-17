import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

/**
 *
 * @author Enis 
 * This is the main data center class. It stores all of ChocAn's members, providers
 * and the service records that have been written to it, as well as the main data center
 * date. Note that the globalDate is assumed to correspond to time zone "0" (i.e. the "starting"
 * reference for all of the provider dates).
 * 
 * The data center can either be initialized from scratch, or it can read multiple files
 * to restore itself to a previous state before program termination.
 */
public class ChocAnDPS
{
    /**
     * This field houses all of ChocAn's members
     */
    private TreeSet<Member> members = new TreeSet<Member>();
    
    /**
     * This field houses all of ChocAn's providers
     */
    private TreeSet<Provider> providers = new TreeSet<Provider>();
    
    /**
     * This field houses all of the current service records on ChocAn's disks
     */
    private ServiceRecordDisk serviceRecords = new ServiceRecordDisk();
    
    /**
     * This field houses the data center's date.
     */
    private LocalDateTime globalDate = null;
    
    
    /**
     * The constructor first sees if the main directory storing all of the files
     * "ChocAnFiles" exists. If it does, it assumes that the data center
     * was left off at a previous state and reads in the remaining data in
     * the following sequence.
     *      1) Read the parameters (i.e. the data center date)
     *      2) Read the members
     *      3) Read the providers
     *      4) Read the service records
     * 
     * If it doesn't exist, the constructor proceeds to create this directory
     * and the Member and Provider directories as well. Please refer to the report
     * for more details. It also initializes the data center date to whatever
     * the current date and time is. Effectively, it initializes a new data center
     * from scratch.
     * 
     * @throws IOException 
     */
    public ChocAnDPS() throws IOException //Here is where the DPS is initialized
    {
        if (FileUtilities.FileExists(FileUtilities.FILE_LOC)) //We have pre-created data here so we read whatever was written from the previous state
        {
            System.out.println("Initializing data center to its previous state...");
            this.ReadParameters();
            this.ReadMembers();
            this.ReadProviders();
            this.ReadServiceRecords();  
            System.out.println("Initialization successful!\n");
        }
        else //No created data, we're beginning at a blank slate
        {
            System.out.println("Initializing data center to the blank state...");            
            FileUtilities.CreateDirectory(FileUtilities.FILE_LOC); //Create the ChocAnFiles directory
            FileUtilities.CreateDirectory(FileUtilities.FILE_LOC+"/"+FileUtilities.MEMBER_LOC); //Create members directory
            FileUtilities.CreateDirectory(FileUtilities.FILE_LOC+"/"+FileUtilities.PROVIDERS_LOC); //Create providers directory            
            this.globalDate = LocalDateTime.now(); //Get the current date and time of the system           
            System.out.println("Initialization successful!\n");
        }
    }
    
    /**
     * 
     * @return The current date and time of the data center
     */
    public LocalDateTime GetGlobalDate()
    {
        return this.globalDate;
    }
    
    /**
     * 
     * @return The members data structure
     */
    public TreeSet<Member> GetMemberDatabase()
    {
        return this.members;
    }
    
    /**
     * 
     * @return The providers data structure
     */
    public TreeSet<Provider> GetProviderDatabase()
    {
        return this.providers;
    }
    
    /**
     * 
     * @return The service records on disk
     */
    public ServiceRecordDisk GetServiceRecordDisk()
    {
        return this.serviceRecords;
    }
    
    /**
     * This method should be called upon program termination. It writes
     * the current state of the DPS to several files (read the report for more details)
     * using the following sequence:
     *      1) Write the parameters (i.e the global date)
     *      2) Write the members
     *      3) Write the providers
     *      4) Write the service records
     * 
     * @throws IOException 
     */
    public void Exit() throws IOException
    {
        System.out.println("Saving current state of the data center...");
        this.WriteParameters();
        this.WriteMembers();
        this.WriteProviders();
        this.WriteServiceRecords();
        System.out.println("Save successful! Exiting the data center...\n");
    }
    
    /**
     * Adds a new member to the data center
     * 
     * @param newMember 
     */
    public void AddMember(final Member newMember)
    {
        this.members.add(newMember);
    }
    
    /**
     * Removes a member from the data center, if they exist.
     * 
     * @param member
     * @return 
     */
    public boolean RemoveMember(final Member member)
    {
        return this.members.remove(member);
    }
    
    /**
     * Looks up a member given their member number
     * 
     * @param memberNum
     * @return The member if they exist, otherwise null.
     */
    public Member LookUpMember(final int memberNum)
    {
        return Utilities.LookUpElement(memberNum, this.members);
    }
    
    /**
     * Adds a new provider to the data center
     * 
     * @param newProvider 
     */
    public void AddProvider(final Provider newProvider)
    {
        this.providers.add(newProvider);
    }
    
    /**
     * Removes a provider from the data center, if they exist.
     * 
     * @param provider
     * @return 
     */
    public boolean RemoveProvider(final Provider provider)
    {
        return this.providers.remove(provider);
    }
    
    /**
     * Looks up a provider given their provider number
     * 
     * @param providerNum
     * @return 
     */
    public Provider LookUpProvider(final int providerNum)
    {
        return Utilities.LookUpElement(providerNum, this.providers);     
    }
    
    /**
     * Advances the current date and time by numDays. User should enter
     * a positive value when calling this function.
     * 
     * @param numDays The number of days to be advanced
     * @throws IOException 
     */
    public void AdvanceTimeByDays(int numDays) throws IOException
    {
        this.AddByTemporalAmount(numDays*Parameters.SECONDS_PER_DAY);
    }
    
    /**
     * Advances the current date and time by numHours. User should enter
     * a positive value when calling this function
     * 
     * @param numHours
     * @throws IOException 
     */
    public void AdvanceTimeByHours(int numHours) throws IOException
    {
        this.AddByTemporalAmount(numHours*Parameters.SECONDS_PER_HOUR);
    }
    
    /**
     * Advances the global date and time by the temporal amount specified in timeToAdd.
     * This is done in seconds, since the time for the project is written as HH:MM:SS.
     * 
     * Note that if the timeToAdd exceeds the amount required to reach the end of the week
     * when the DPS does its weekly report generation, then the system first advances the date
     * to this period, generates the weekly reports, and then uses the remaining time to advance
     * it to its actual date. This is more apparent in the code.
     * 
     * Otherwise, the system proceeds to add timeToAdd seconds to the current date, and all
     * of the provider dates as well.
     * 
     * NOTE: End of the week is the corresponding Friday, at 23:59:00
     * 
     * @param timeToAdd The number of seconds we're advancing the time by.
     * 
     * @throws IOException 
     */
    private void AddByTemporalAmount (long timeToAdd) throws IOException //First check to see if adding the amount would exceed Friday 11:59 PM date of the data processor
    {
        long timeTillEnd = Utilities.SecondsUntilWeekEnd(globalDate); //Get the number of seconds left until the end of the week.
        
        if (timeToAdd > timeTillEnd) //If the time we're adding exceeds what we need to reach the end of the week
        {
            timeToAdd -= timeTillEnd; //Decrement it
            this.AddByTemporalAmount(timeTillEnd); //Get the system to the end of the week
            
            System.out.println("Generating the weekly reports...");
            ReportGenerator.GenerateWeeklyReports(this); //Generate the weekly reports
            System.out.println("The reports have successfully been generated!\n");
        }    
        
        this.globalDate = this.globalDate.plus(timeToAdd, ChronoUnit.SECONDS);
        Iterator<Provider> iterator = this.providers.iterator();
        while (iterator.hasNext())
        {
            Provider provider = iterator.next();
            provider.SetProviderDateTime(provider.GetProviderDateTime().plus(timeToAdd, ChronoUnit.SECONDS));
        }                
    }
    
    /**
     * Writes the global date to its corresponding file
     * 
     * @throws IOException 
     */
    private void WriteParameters() throws IOException
    {
        BufferedWriter writer = FileUtilities.OpenWriter(FileUtilities.FILE_LOC+"/"+FileUtilities.CHOC_AN_PARAMETERS_FILE_LOC);
        writer.write(this.globalDate.format(DateTimeFormatter.ofPattern(Format.DATE + " " + Format.TIME))+"\n");
        writer.close();
    }
    
    /**
     * Writes all of the member numbers to their corresponding file.
     * For each member number, it also writes the member information to its
     * corresponding file in the corresponding directory.
     * 
     * See the report for more details on file organization.
     * 
     * @throws IOException 
     */
    private void WriteMembers() throws IOException
    {
        BufferedWriter memberNumbers = FileUtilities.OpenWriter(FileUtilities.FILE_LOC+"/"+FileUtilities.MEMBER_NUMBERS_FILE_LOC);
        String memberDirectory = FileUtilities.FILE_LOC+"/"+FileUtilities.MEMBER_LOC;
        Iterator<Member> iterator = members.iterator();
        
        if (iterator.hasNext())
        {
            Member currentMember = iterator.next(); //This is executed this way to avoid having a new line after the
            memberNumbers.write(""+currentMember.GetID()); //first sentence of the file, in case there's only one member. Makes it
            this.WriteMember(currentMember, memberDirectory+"/"+currentMember.GetID()); //easier to read from later.
            while (iterator.hasNext())
            {
                currentMember = iterator.next();
                memberNumbers.write("\n" + currentMember.GetID());
                this.WriteMember(currentMember, memberDirectory+"/"+currentMember.GetID());                
            }
        }
        
        memberNumbers.close();
    }
    
    /**
     * Writes all of the provider numbers to their corresponding file.
     * For each provider number, it also writes the provider information and directory
     * to their corresponding file in the corresponding directory.
     * 
     * See the report for more details on file organization.
     * 
     * @throws IOException 
     */
    private void WriteProviders() throws IOException
    {
        BufferedWriter providerNumbers = FileUtilities.OpenWriter(FileUtilities.FILE_LOC+"/"+FileUtilities.PROVIDER_NUMBERS_FILE_LOC);
        String provDirectory = FileUtilities.FILE_LOC+"/"+FileUtilities.PROVIDERS_LOC;
        Iterator<Provider> iterator = providers.iterator();
        
        if (iterator.hasNext())
        {
            Provider currentProvider = iterator.next(); //See the comments in WriteMembers above regarding why this code is executed like it is here.
            providerNumbers.write(""+currentProvider.GetID());
            this.WriteProvider(currentProvider, provDirectory+"/"+currentProvider.GetID());
            while (iterator.hasNext())
            {
                currentProvider = iterator.next();
                providerNumbers.write("\n" + currentProvider.GetID());
                this.WriteProvider(currentProvider, provDirectory+"/"+currentProvider.GetID());                
            }
        }
        providerNumbers.close();        
    }
    
    /**
     * Writes the current records on disk to their corresponding file.
     * 
     * See the report for more details on file organization.
     * 
     * @throws IOException 
     */
    private void WriteServiceRecords() throws IOException
    {
        serviceRecords.WriteRecordsTo(FileUtilities.FILE_LOC+"/"+FileUtilities.SERVICE_RECORD_LOC);
    }
    
    /**
     * Reads the parameters (i.e. the global date) of the data center
     * 
     * @return True if the read was successful, false otherwise
     * @throws IOException 
     */
    private boolean ReadParameters() throws IOException //Returns true if the read was successful, false otherwise
    {
        BufferedReader reader = FileUtilities.OpenReader(FileUtilities.FILE_LOC+"/"+FileUtilities.CHOC_AN_PARAMETERS_FILE_LOC);
        if (reader == null) //File doesn't exist, or an unsuccessful open
        {
            return false;
        }
        else
        {
            this.globalDate = LocalDateTime.parse(reader.readLine(), DateTimeFormatter.ofPattern(Format.DATE + " " + Format.TIME));
            
            reader.close(); //Close the file
            
            return true;
        }
    }
    
    /**
     * Reads the members of the data center. First it opens the member numbers file
     * to read the numbers and then, for each number, it goes to its corresponding directory
     * and reads the member information from there. Function assumes that the file and member directories
     * exist. See report for more details.
     * 
     * @throws IOException 
     */
    private void ReadMembers() throws IOException
    {
        BufferedReader reader = FileUtilities.OpenReader(FileUtilities.FILE_LOC+"/"+FileUtilities.MEMBER_NUMBERS_FILE_LOC);
        if (reader != null) //File exists
        {
            String line = null;
            while ((line = reader.readLine()) != null) //Members exist
            {
                this.members.add(this.ReadMember(Integer.parseInt(line), FileUtilities.FILE_LOC+"/"+FileUtilities.MEMBER_LOC+"/"+line+"/"+FileUtilities.INFO_LOC)); //Read individual member
            }
            
            reader.close();
        }
    }
    
    /**
     * Reads the providers of the data center. First it opens the provider numbers file
     * to read the numbers and then, for each number, it goes to its corresponding directory
     * and reads the member information from there. Function assumes that the file and member directories
     * exist. See report for more details.
     * 
     * @throws IOException 
     */
    private void ReadProviders() throws IOException
    {
        BufferedReader reader = FileUtilities.OpenReader(FileUtilities.FILE_LOC+"/"+FileUtilities.PROVIDER_NUMBERS_FILE_LOC);
        if (reader != null) //File exists
        {
            String line = null;
            while ((line = reader.readLine()) != null) //Providers exist
            {
                this.providers.add(this.ReadProvider(Integer.parseInt(line), FileUtilities.FILE_LOC+"/"+FileUtilities.PROVIDERS_LOC+"/"+line)); //Read individual provider
            }
            
            reader.close();
        }
    }
    
    
    /**
     * Function reads the individual service records from disk. Assumes that
     * the service records file exists.
     * 
     * Note that every record is stored by:
     *      currentDateTime
     *      serviceDate
     *      providerNum
     *      memberNum
     *      serviceCode
     *      SERVICE_RECORD_SEPARATOR
     *      comments
     * 
     * which is shown in the code.
     * 
     * Note that the separator is used to tell the system that it is about to read the comments.
     * Otherwise the loop would assume  that its still reading one of the other fields.
     * 
     * @throws IOException 
     */
    private void ReadServiceRecords() throws IOException
    {
        BufferedReader reader = FileUtilities.OpenReader(FileUtilities.FILE_LOC+"/"+FileUtilities.SERVICE_RECORD_LOC);
        if (reader != null)
        {
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                LocalDateTime currentDateTime = LocalDateTime.parse(line, DateTimeFormatter.ofPattern(Format.DATE + " " + Format.TIME));
                LocalDate serviceDate = LocalDate.parse(reader.readLine(), DateTimeFormatter.ofPattern(Format.DATE));
                int providerNum = Integer.parseInt(reader.readLine());
                int memberNum = Integer.parseInt(reader.readLine());
                int serviceCode = Integer.parseInt(reader.readLine());
                
                String comments = "";                
                if (!((line = reader.readLine()).equals(Format.SERVICE_RECORD_SEPARATOR))) //To write comments correctly per line
                {
                    comments = line; //This is executed this way since we shouldn't have a new line after the first commment.
                    while (!((line = reader.readLine()).equals(Format.SERVICE_RECORD_SEPARATOR))) //While we are not at the separator, we want to get the comments
                    {
                        comments += "\n"+line;
                    }                    
                }
                
                this.serviceRecords.WriteToDisk(new ServiceRecord(currentDateTime, serviceDate, providerNum, memberNum, serviceCode, comments));
            }
            reader.close();
        }
    }
    
    /**
     * Reads the "Information" object components contained in reader, and places them in the info array.
     * Note that the indices are mapped as:
     * 
     *      0 = Name
     *      1 = Street address
     *      2 = City
     *      3 = State
     *      4 = ZIP
     * 
     * @param reader
     * @param info
     * @throws IOException 
     */
    private void ReadInfo(BufferedReader reader, String[] info) throws IOException
    {
        info[0] = reader.readLine();
        info[1] = reader.readLine();
        info[2] = reader.readLine();
        info[3] = reader.readLine();
        info[4] = reader.readLine();
    }
    
    /**
     * Reads the member located in filePath. Note that a member is written as
     *      1. Name
     *      2. Street address
     *      3. City
     *      4. State
     *      5. ZIP
     *      6. Member status
     * 
     * And this is the order that the reader reads the information.
     * 
     * @param id_ Member number
     * @param filePath Location of the file containing the member information
     * @return the member corresponding to the information just read.
     * 
     * @throws IOException 
     */
    private Member ReadMember(int id_, String filePath) throws IOException
    {
        BufferedReader reader = FileUtilities.OpenReader(filePath);
        String[] info = new String[5]; //0 = name, 1 = Street, 2 = city, 3 = state, 4 = ZIP
        this.ReadInfo(reader, info);
        boolean status = Boolean.parseBoolean(reader.readLine());
        
        reader.close();
        return (new Member(id_, info[0], info[1], info[2], info[3], info[4], status)); //CHANGE THIS LATER TO AN ACTUAL MEMBER
    }
    
    /**
     * Reads the provider located in filePath. Note that a provider is written as
     *      1. Name
     *      2. Street address
     *      3. City
     *      4. State
     *      5. ZIP
     *      6. Provider date and time
     * 
     * After reading this information, it proceeds to read the provider directory
     * associated with the provider. Services are stored as
     *      1) Service code
     *      2) Service name
     *      3) Service fee
     * 
     * @param id_ Provider number
     * @param dirLoc Location of the individual provider directory to do the read
     * @return the provider corresponding to the info just read.
     * @throws IOException 
     */
    private Provider ReadProvider(int id_, String dirLoc) throws IOException
    {
        BufferedReader reader = FileUtilities.OpenReader(dirLoc+"/"+FileUtilities.INFO_LOC);
        String[] info = new String[5]; //0 = name, 1 = Street, 2 = city, 3 = state, 4 = ZIP
        this.ReadInfo(reader, info);
        LocalDateTime provDate = LocalDateTime.parse(reader.readLine(), DateTimeFormatter.ofPattern(Format.DATE + " " + Format.TIME));
        reader.close();
        
        Provider provider = new Provider(id_, info[0], info[1], info[2], info[3], info[4], provDate); //Create the object
        
        reader = FileUtilities.OpenReader(dirLoc+"/"+FileUtilities.PROVIDER_DIR_LOC); //Read the services from the provider directory
        String curInfo = null;
        while ((curInfo = reader.readLine()) != null)
        {
            int serviceCode = Integer.parseInt(curInfo);
            String name = reader.readLine();
            BigDecimal fee = new BigDecimal(reader.readLine());
            provider.AddService(new Service(serviceCode, name, fee));
        }        
        reader.close();
        
        return provider; //CHANGE THIS LATER TO AN ACTUAL PROVIDER
    }    
    
    /**
     * Writes a member to its corresponding file. The file is formatted in the same way as outlined in the ReadMember method
     * java doc.
     * 
     * @param member
     * @param dirLoc
     * @throws IOException 
     */
    private void WriteMember(Member member, String dirLoc) throws IOException
    {
        BufferedWriter writer = FileUtilities.OpenWriter(dirLoc+"/"+FileUtilities.INFO_LOC); //First write the info
        writer.write(member.GetName()+"\n"+member.GetStreetAddress()+"\n"+member.GetCity()+"\n"+member.GetState()+"\n"+member.GetZipCode()+"\n"+member.GetStatus());
        writer.close();
    }
    
    /**
     * Writes a provider to its corresponding file. The file is formatted in the same way as outlined in the ReadProvider method.
     * 
     * @param provider
     * @param dirLoc
     * @throws IOException 
     */
    private void WriteProvider(Provider provider, String dirLoc) throws IOException
    {     
        BufferedWriter writer = FileUtilities.OpenWriter(dirLoc+"/"+FileUtilities.INFO_LOC); //First write the info
        writer.write(provider.GetName()+"\n"+provider.GetStreetAddress()+"\n"+provider.GetCity()+"\n"+provider.GetState()+"\n"+provider.GetZipCode()+"\n"
                        + provider.GetProviderDateTime().format(DateTimeFormatter.ofPattern(Format.DATE + " " + Format.TIME)));
        writer.close();

        provider.WriteServicesTo(dirLoc+"/"+FileUtilities.PROVIDER_DIR_LOC); //Now write the services
    }
    
    /**
     * Used for debugging purposes. Please ignore.
     * 
     * @return 
     */
    public String toString()
    {
        String info = "Date: " + (globalDate.format(DateTimeFormatter.ofPattern(Format.DATE + " " + Format.TIME))) + "\n";
        
        info += "MEMBERS " + "\n";
        info += members.toString() + "\n";
        
        info += "PROVIDERS " + "\n";
        info += providers.toString() + "\n";
        
        info += "SERVICE RECORDS ON FILE: " + "\n";
        info += serviceRecords.toString();
        
        return info;
    }
}


