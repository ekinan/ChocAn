
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Enis
 * This is the manager terminal used to generate the individual reports
 * at a manager's request. Note that for simplicity, all member and provider reports
 * are generated with respect to the global data center's date and time (and not, say,
 * a certain provider's date and time). The user options here are the following:
 *      0) Request member report
 *      1) Request provider report
 *      2) Request EFT report
 *      3) Request summary report
 *      4) Exit
 */
public class ManagerTerminal extends ChocAnTerminal
{
    private ReportParameters parameters = new ReportParameters(); //This is to avoid repedeatly creating new ReportParameter objecs to pass to ReportGenerator
    
    public ManagerTerminal(final ChocAnDPS dataCenter_)
    {
        super(dataCenter_);
        
        this.options = new String[5];
        
        options[0] = "Request member report.";
        options[1] = "Request provider report.";
        options[2] = "Request EFT report.";
        options[3] = "Request summary report.";
        options[4] = "Exit.";        
    }

    /**
     * Wrapper is there to avoid having to insert "throw IOException" to this methods
     * definition in the Terminal class, since not all terminals require file writing.
     * @return 
     */
    public int UseTerminal()
    {
        int returnVal = -1;
        try
        {
            returnVal = this.UseTerminalWrapper(); //To avoid the throws IOException issue
        }catch(IOException ex)
        {
            System.out.println("ERROR! Cannot print reports because required directories cannot be created!"); //DEAL WITH THIS LATER
        }
        
        return returnVal;
    }    
    
    protected int GetNumberOfOptions()
    {
        return 5;
    }
    
    protected void PrintTerminalInfo()
    {
        LocalDateTime globalDate = this.dataCenter.GetGlobalDate();
        System.out.println("Manager Terminal");
        System.out.println("Date: "+globalDate.format(DateTimeFormatter.ofPattern(Format.DATE_TERMINAL)));
        System.out.println("Time: "+globalDate.format(DateTimeFormatter.ofPattern(Format.TIME))+"\n");        
    }    

    private int UseTerminalWrapper() throws IOException
    {
        int userChoice = 0;
        do
        {
            this.PrintTerminalInfo();
            switch ((userChoice = InputHandler.TerminalInput(this.options, Utilities.sc)))
            {
            case 0:
                this.GenerateMemberReport();
                break;
            case 1:
                this.GenerateProviderReport();
                break;
            case 2:
                this.GenerateEFTReport();
                break;
            case 3:
                this.GenerateSummaryReport();
                break;
            default:
                break;
            }       
        } while (userChoice != -1 && (userChoice != (this.GetNumberOfOptions()-1)));
        
        return -1; //This only returns if we've exited from the terminal.        
    }
    
    /**
     * This method takes as input a member number from the user. If the number is valid,
     * it then checks to see if that member has any service records under their name. If they do,
     * the corresponding member report is generated. Otherwise, the method prints a message
     * telling the manager that the specified member doesn't have any services provided to them
     * for this week.
     * 
     * If the number is invalid, the function does nothing as the error message is handled in 
     * another method.
     * 
     * @throws IOException 
     */
    private void GenerateMemberReport() throws IOException
    {        
        Utilities.SetInfoParameters("member", "number", "ERROR: Invalid/nonexistant member number entered! Cannot generate member report!\n");
        Member member = Utilities.ExtractElementFromInput(this.dataCenter.GetMemberDatabase());
        
        if (member != null)
        {
            this.parameters.iterator = this.dataCenter.GetServiceRecordDisk().StartAtMember(member.GetID());
            if (this.parameters.iterator.hasNext() && (Utilities.IsWithinDate(this.parameters.iterator.next().GetCurrentDateTime(), this.dataCenter.GetGlobalDate()))) //We generate a report only for those members who have a service record associated for this particular week
            {
                this.parameters.SetParameters(member, this.dataCenter.GetServiceRecordDisk().StartAtMember(member.GetID()), this.dataCenter.GetGlobalDate(), true);
                ReportGenerator.GenerateMemberReport(this.parameters, this.dataCenter);
                System.out.println("The member report for " + (member.GetID()) + " has successfully been created!\n");
            }
            else
            {
                System.out.println("Member number " + (member.GetID()) + " does not have any services provided to them so far that are within this week!\n");
            }
        }
    }
    
    /**
     * This method takes as input a provider number from the user. If the number is valid,
     * it then checks to see if that provider has any service records under their name. If they do,
     * the corresponding provider report is generated. Otherwise, the method prints a message
     * telling the manager that the specified provider doesn't have any services provided to them
     * for this week.
     * 
     * If the number is invalid, the function does nothing as the error message is handled in 
     * another method.
     * 
     * @throws IOException 
     */
    private void GenerateProviderReport() throws IOException
    {        
        Utilities.SetInfoParameters("provider", "number", "ERROR: Invalid/nonexistant provider number entered! Cannot generate provider report!\n");
        Provider provider = Utilities.ExtractElementFromInput(this.dataCenter.GetProviderDatabase());
        
        if (provider != null)
        {
            this.parameters.iterator = this.dataCenter.GetServiceRecordDisk().StartAtProvider(provider.GetID());
            if (this.parameters.iterator.hasNext() && (Utilities.IsWithinDate(this.parameters.iterator.next().GetCurrentDateTime(), this.dataCenter.GetGlobalDate())))
            {
                this.parameters.SetParameters(provider, this.dataCenter.GetServiceRecordDisk().StartAtProvider(provider.GetID()), this.dataCenter.GetGlobalDate(), true);
                ReportGenerator.GenerateProviderReport(this.parameters, this.dataCenter);
                System.out.println("The provider report for " + (provider.GetID()) + " has successfully been created!\n");
            }
            else
            {
                System.out.println("Provider number " + (provider.GetID()) + " has not had any consultations so far that are within this week!\n");
            }
        }        
    }
    
    /**
     * This method generates the EFT report for the data center
     * 
     * @throws IOException 
     */
    private void GenerateEFTReport() throws IOException
    {
        ReportGenerator.GenerateEFTReport(this.dataCenter);
        System.out.println("The EFT report has successfully been generated!\n");
    }
    
    /**
     * This method generates the Summary Report for the data center.
     * 
     * @throws IOException 
     */
    private void GenerateSummaryReport() throws IOException
    {
        ReportGenerator.GenerateSummaryReport(this.dataCenter);
        System.out.println("The summary report has successfully been generated!\n");
    }    
}
