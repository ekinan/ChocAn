
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 *
 * @author Enis
 * This is the main terminal of the program. It essentially executes
 * each of the actions in the corresponding subterminals (which are its fields).
 * 
 * Its options are as follows:
 *      0) Display information (accomplished through displayTerminal)
 *      1) Update members (accomplished through memberUpdateTerminal)
 *      2) Update providers (accomplished through providerUpdateTerminal)
 *      3) Go to manager terminal (takes the user to managerTerminal)
 *      4) Go to provider terminal (takes the user to provider terminal after prompting them for the provider number)
 *      5) Advance time (takes the user to the timeTerminal)
 *      6) Exit. (Ends the terminal interaction)
 */
public class GlobalTerminal extends ChocAnTerminal
{
    //Each of these fields are explained in more detail in their corresponding classes
    private DisplayTerminal displayTerminal = null;
    private ModifyMembersTerminal memberUpdateTerminal = null;
    private ModifyProvidersTerminal providerUpdateTerminal = null;
    private ManagerTerminal managerTerminal = null;
    private ProviderTerminal providerTerminal = null;
    private AdvanceTimeTerminal timeTerminal = null;

    /**
     * Initialize each of the terminals and this terminals options.
     * @param dataCenter_ 
     */
    public GlobalTerminal(ChocAnDPS dataCenter_)
    {
        super(dataCenter_);
        this.displayTerminal = new DisplayTerminal(dataCenter_);
        this.memberUpdateTerminal = new ModifyMembersTerminal(dataCenter_);
        this.providerUpdateTerminal = new ModifyProvidersTerminal(dataCenter_);
        this.managerTerminal = new ManagerTerminal(dataCenter_);
        this.providerTerminal = new ProviderTerminal(dataCenter_);
        timeTerminal = new AdvanceTimeTerminal(dataCenter_);
        
        this.options = new String[7];
        
        this.options[0] = "Display information.";
        this.options[1] = "Update members.";
        this.options[2] = "Update providers.";
        this.options[3] = "Go to manager terminal";
        this.options[4] = "Go to provider terminal";
        this.options[5] = "Advance time.";
        this.options[6] = "Exit.";
    }

    /**
     * Same pseudocode as outlined in the Terminal class
     * 
     * @return 
     */
    public int UseTerminal()
    {
        int userChoice = 0;
        do
        {          
            this.PrintTerminalInfo();
            switch ((userChoice = InputHandler.TerminalInput(this.options, Utilities.sc)))
            {
            case 0:
                this.DisplayInformation();
                break;
            case 1:
                this.UpdateMembers();
                break;
            case 2:
                this.UpdateProviders();
                break;
            case 3:
                this.GoToManagerTerminal();
                break;
            case 4:
                this.GoToProviderTerminal();
                break;
            case 5:
                this.AdvanceTime();
                break;
            default:
                break;
            }           
        } while (userChoice != (this.GetNumberOfOptions()-1));
        
        return -1; //This only returns if we've exited from the terminal.      
    }    

    protected int GetNumberOfOptions()
    {
        return 7;
    }
 
    /**
     * Function prints the main data center terminal and then the current data center date and time.
     */
    protected void PrintTerminalInfo()
    {
        LocalDateTime globalDate = this.dataCenter.GetGlobalDate();
        System.out.println("Main datacenter terminal.");
        System.out.println("Date: "+globalDate.format(DateTimeFormatter.ofPattern(Format.DATE_TERMINAL)));
        System.out.println("Time: "+globalDate.format(DateTimeFormatter.ofPattern(Format.TIME))+"\n");
    }
    
    /**
     * Wrapper to displayTerminal.UseTerminal();
     */
    private void DisplayInformation()
    {
        System.out.println();
        this.displayTerminal.UseTerminal();
        System.out.println();
    }
    
    /**
     * Wrapper to memberUpdateTerminal.UseTerminal();
     */
    private void UpdateMembers()
    {
        System.out.println();
        this.memberUpdateTerminal.UseTerminal();
        System.out.println("The global data center's members have been updated successfully!\n");
    }
    
    /**
     * Wrapper to providerUpdateTerminal.UseTerminal();
     */
    private void UpdateProviders()
    {
        System.out.println();
        this.providerUpdateTerminal.UseTerminal();
        System.out.println("The global data center's providers have been updated successfully!\n");
    }
    
    /**
     * Wrapper to managerTerminal.UseTerminal();
     */
    private void GoToManagerTerminal()
    {
        System.out.println();
        this.managerTerminal.UseTerminal();
        System.out.println();
    }
    
    /**
     * Function first prompts the user to enter the provider number. If the entered number is ok,
     * it takes the user to providerTerminal.UseTerminal(). Otherwise it outputs an error message shown below
     */
    private void GoToProviderTerminal()
    {
        Utilities.SetInfoParameters("provider", "number", "ERROR: Invalid/nonexistant provider number entered! Cannot switch to provider terminal!\n");
        Provider prov = Utilities.ExtractElementFromInput(this.dataCenter.GetProviderDatabase()); //Get the provider from the provider number
        if (prov != null)
        {
            System.out.println();
            this.providerTerminal.SetProviderTo(prov);
            this.providerTerminal.UseTerminal();
            System.out.println();
        }
    }
    
    /**
     * Wrapper to timeTerminal.UseTerminal()
     */
    private void AdvanceTime()
    {
        System.out.println();
        this.timeTerminal.UseTerminal();
        System.out.println();
    }
}
