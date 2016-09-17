/**
 *
 * @author Enis
 * This is the terminal that updates an individual provider.
 * It gives the user the following options.
 *      0) Update provider information
 *      1) Update provider directory
 *      2) Exit
 * 
 * It essentially is a wrapper class to two other terminals:
 * UpdateInformationTerminal and ModifyServicesTerminal.
 * 
 */
public class UpdateProviderTerminal extends Terminal
{
    private Provider provider = null;
    private UpdateInformationTerminal infoTerminal = null;
    private ModifyServicesTerminal serviceTerminal = null;
    
    public UpdateProviderTerminal()
    {
        this.options = new String[3];
        
        this.options[0] = "Update provider information.";
        this.options[1] = "Update provider directory.";
        this.options[2] = "Exit.";
        
        this.infoTerminal = new UpdateInformationTerminal("provider");
        this.serviceTerminal = new ModifyServicesTerminal();
    }
    
    public void SetProviderTo(Provider prov)
    {
        this.provider = prov;
        this.infoTerminal.SetInformationTo(prov);        
        this.serviceTerminal.provider = prov;
    }

    public int UseTerminal()
    {
        int userChoice = 0;
        do
        {
            this.PrintTerminalInfo();
            switch ((userChoice = InputHandler.TerminalInput(this.options, Utilities.sc)))
            {
            case 0:
                this.UpdateProviderInformation();
                break;
            case 1:
                this.UpdateProviderServices();
                break;
            default:
                break;
            }   
        } while (userChoice != (this.GetNumberOfOptions()-1));
        
        return -1; //This only returns if we've exited from the terminal.
    }

    protected int GetNumberOfOptions()
    {
        return 3;
    }
    
    protected void PrintTerminalInfo()
    {
        System.out.println("Provider records update terminal for provider number: "+(this.provider.GetID()));
        System.out.println("Provider name: "+this.provider.GetName()+"\n");              
    }     
    
    /**
     * Here the provider information is updated. This is a wrapper to infoTerminal.UseTerminal()
     */
    private void UpdateProviderInformation()
    {
        System.out.println();
        this.infoTerminal.UseTerminal();
        System.out.println("Successfully updated provider number "+this.provider.GetID()+"'s information!\n");        
    }
    
    /**
     * Here the provider directory is updated. This is a wrapper to serviceTerminal.UseTerminal()
     */
    private void UpdateProviderServices()
    {
        System.out.println();
        this.serviceTerminal.UseTerminal();
        System.out.println("Successfully updated provider number "+this.provider.GetID()+"'s provider directory!\n");
    }
}
