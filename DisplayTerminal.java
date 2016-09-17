import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;

/**
 *
 * @author Enis
 * This is the terminal that displays the current data centers information.
 * It allows the user to see ChocAn's members, providers, as well as the
 * current service records that are on disk.
 * 
 * Because there could be quite a bit of information displayed, the program
 * outputs this information in a folder called "Output" in the "ChocAnFiles"
 * directory.
 * 
 * There are three options for the user:
 *      0) Display members
 *      1) Display providers
 *      2) Display service records on disk
 *      3) Exit
 */
public class DisplayTerminal extends ChocAnTerminal
{

    public DisplayTerminal(final ChocAnDPS dataCenter_)
    {
        super(dataCenter_);
        
        FileUtilities.CreateDirectory(FileUtilities.FILE_LOC+"/"+FileUtilities.OUTPUT_LOC);
        
        this.options = new String[4];
        
        options[0] = "Display members.";
        options[1] = "Display providers.";
        options[2] = "Display service records on disk.";
        options[3] = "Exit.";
        
    }

    /**
     * This one has a UseTerminalWrapper in order to avoid having a throws IOException
     * tag placed in the Terminal class' abstract method, since not all terminals
     * will write to files.
     * 
     * @return 
     */
    public int UseTerminal()
    {
        int returnVal = 0;
        
        try
        {
            returnVal = this.UseTerminalWrapper();
        }catch(IOException ex)
        {
            System.out.println("ERROR! Cannot print provider directory because the required directories don't exist!");
        }
        
        return returnVal;
    }

    protected int GetNumberOfOptions()
    {
        return 4;
    }
    
    protected void PrintTerminalInfo()
    {
        LocalDateTime globalDate = this.dataCenter.GetGlobalDate();
        System.out.println("Datacenter Display Terminal.");
        System.out.println("Date: "+globalDate.format(DateTimeFormatter.ofPattern(Format.DATE_TERMINAL)));
        System.out.println("Time: "+globalDate.format(DateTimeFormatter.ofPattern(Format.TIME))+"\n");        
    }    
    
    /**
     * Here is where the main terminal code is executed, following the psuedocode
     * outlined in Terminal.java
     * 
     * @return
     * @throws IOException 
     */
    private int UseTerminalWrapper() throws IOException
    {
        int userChoice = 0;
        do
        {
            this.PrintTerminalInfo();
            switch ((userChoice = InputHandler.TerminalInput(this.options, Utilities.sc)))
            {
            case 0:
                this.DisplayMembers();
                break;
            case 1:
                this.DisplayProviders();
                break;
            case 2:
                this.DisplayServiceRecords();
                break;
            default:
                break;
            }       
        } while (userChoice != (this.GetNumberOfOptions()-1));
        
        return -1; //This only returns if we've exited from the terminal.           
    }
    
    /**
     * This function outputs all of ChocAn's current members to the corresponding file in the output directory
     * 
     * @throws IOException 
     */
    private void DisplayMembers() throws IOException
    {
        BufferedWriter writer = FileUtilities.OpenWriter(FileUtilities.FILE_LOC+"/"+FileUtilities.OUTPUT_LOC+"/"+FileUtilities.MEMBER_OUTPUT_LOC);
        writer.write("Below you will find the most updated list of ChocAn's members.\n");
        writer.write("There are currently "+(this.dataCenter.GetMemberDatabase().size())+".\n\n");
        Iterator<Member> iterator = this.dataCenter.GetMemberDatabase().iterator();

        while (iterator.hasNext())
        {
            this.WriteMemberTo(writer, iterator.next());
        }
        
        writer.close();
        
        System.out.println("All of ChocAn's members have successfully been written to the output directory!\n");
    }
    
    
    /**
     * This function outputs all of ChocAn's current providers to the corresponding file in the output directory
     * 
     * @throws IOException 
     */
    private void DisplayProviders() throws IOException
    {
        BufferedWriter writer = FileUtilities.OpenWriter(FileUtilities.FILE_LOC+"/"+FileUtilities.OUTPUT_LOC+"/"+FileUtilities.PROVIDER_OUTPUT_LOC);
        writer.write("Below you will find the most updated list of ChocAn's providers.\n");
        writer.write("There are currently "+(this.dataCenter.GetProviderDatabase().size())+".\n\n");
        
        Iterator<Provider> iterator = this.dataCenter.GetProviderDatabase().iterator();

        while (iterator.hasNext())
        {
            this.WriteProviderTo(writer, iterator.next());
        }
        
        writer.close();     
        
        System.out.println("All of ChocAn's providers have successfully been written to the output directory!\n");        
    }
    
    
    /**
     * This function outputs all of ChocAn's current service records on disk to the corresponding
     * file in the output directory.
     * 
     * @throws IOException 
     */
    private void DisplayServiceRecords() throws IOException
    {
        BufferedWriter writer = FileUtilities.OpenWriter(FileUtilities.FILE_LOC+"/"+FileUtilities.OUTPUT_LOC+"/"+FileUtilities.SERVICE_RECORDS_OUTPUT_LOC);
        writer.write("Below you will find the current service records in ChocAn's disk.\n");
        writer.write("There are currently "+(this.dataCenter.GetServiceRecordDisk().GetRecords().size())+" records on file.\n\n");
        Iterator<ServiceRecord> iterator = this.dataCenter.GetServiceRecordDisk().GetRecords().iterator();

        while (iterator.hasNext())
        {
            this.WriteServiceRecordTo(writer, iterator.next());
        }
        
        writer.close();

        System.out.println("All of ChocAn's service records have successfully been written to the output directory!\n");        
    }
    
    /**
     * This function takes a member, and writes his or her information and status to the open file.
     * 
     * @param writer open file
     * @param member member to be written
     * @throws IOException 
     */
    private void WriteMemberTo(BufferedWriter writer, Member member) throws IOException
    {
        ReportGenerator.WriteInfo(writer, member, "Member");
        writer.write("Member status: "+(member.GetStatus() ? "Valid" : "Suspended")+"\n\n");        
    }
    
    /**
     * This function takes a provider, writes its information to the file, and also
     * its services. Note the "*******" components are used to separate providers from
     * one another and to delineate when services, instead of providers, are being written.
     * 
     * @param writer open file
     * @param provider provider to be written
     * @throws IOException 
     */
    private void WriteProviderTo(BufferedWriter writer, Provider provider) throws IOException
    {
        writer.write("**********Provider Information**********\n\n");
        ReportGenerator.WriteInfo(writer, provider, "provider");

        writer.write("\n**********Services offered by this provider**********\n\n:");            
        Iterator<Service> iterator = provider.GetProviderDirectory().iterator();
        
        while (iterator.hasNext())
        {
            this.WriteServiceTo(writer, iterator.next());
        }
        writer.write("**********End Provider Information**********\n\n\n\n");        
    }
    
    /**
     * This function writes a service to the file opened by writer
     * 
     * @param writer open file
     * @param service provider to be written
     * @throws IOException 
     */
    private void WriteServiceTo(BufferedWriter writer, Service service) throws IOException
    {
        writer.write("Service name: "+(service.GetName())+"\n");
        writer.write("Service code: "+(service.GetID())+"\n");
        writer.write("Service fee: "+(Format.USD_COST_FORMAT.format(service.GetFee().doubleValue()))+"\n\n");        
    }
      
    /**
     * This function writes a service record to an open file.
     * 
     * @param writer open file
     * @param record service record to be written
     * @throws IOException 
     */
    private void WriteServiceRecordTo(BufferedWriter writer, ServiceRecord record) throws IOException
    {
        writer.write("Date and time record was written: "+(record.GetCurrentDateTime().format(DateTimeFormatter.ofPattern(Format.DATE+" "+Format.TIME)))+"\n");
        writer.write("Date of service: "+(record.GetServiceDate().format(DateTimeFormatter.ofPattern(Format.DATE)))+"\n");
        writer.write("Provider number: "+(record.GetProviderNumber())+"\n");
        writer.write("Member number: "+(record.GetMemberNumber())+"\n");
        writer.write("Service code: "+(record.GetServiceCode())+"\n");
        writer.write("Comments:\n");
        writer.write(record.GetComments()+"\n\n\n");        
    }
}
