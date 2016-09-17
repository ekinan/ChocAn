
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Enis
 * This is the terminal that deals with modifying the service records of a given provider.
 * It allows for the operator add new or remove services,
 * or to update the records (i.e. name and fee)
 * of existing ones.
 * 
 * The terminal options are below:
 *      0) Add new service
 *      1) Remove existing service
 *      2) Update existing service records
 *      3) Exit
 *
 */
public class ModifyServicesTerminal extends Terminal
{
    /**
     * The updateTerminal field is here, because it will be what is used
     * to update an existing service's records. See comments for more info
     */
    private UpdateServiceTerminal updateTerminal = new UpdateServiceTerminal();
    
    /**
     * This is the provider whose services we are updating.
     */
    protected Provider provider = null;
    
    public ModifyServicesTerminal()
    {
        this.options = new String[4];
        
        options[0] = "Add new service.";
        options[1] = "Remove existing service.";
        options[2] = "Update existing service.";
        options[3] = "Exit.";
    }
    
    public ModifyServicesTerminal(boolean dummy) //To not create the options array when the terminal is not being used.
    {
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
                this.AddNewService();
                break;
            case 1:
                this.RemoveExistingService();
                break;
            case 2:
                this.UpdateExistingService();
                break;
            default:
                break;
            }    
        } while (userChoice != (this.GetNumberOfOptions()-1));
        
        return -1; //This only returns if we've exited from the terminal.        
    }

    protected int GetNumberOfOptions()
    {
        return 4;
    }
    
    protected void PrintTerminalInfo()
    {
        LocalDateTime globalDate = this.provider.GetProviderDateTime();
        System.out.println("Provider directory modification terminal for provider number: "+(this.provider.GetID()));
        System.out.println("Provider name: "+this.provider.GetName()+"\n");          
    }    
    
    /**
     * This function adds a new service to the current provider.
     * First, it asks the user to enter the service's name.
     * After, the user is asked to enter its fee formatted as, for example, 12.32.
     * If the fee is incorrectly formatted or a negative value, the program outputs
     * an error message and the service is not added. Otherwise, the fee and name are adjusted
     * to fit within their specified limits, and the service is added to the provider's directory.
     * 
     */    
    protected Service AddNewService()
    {
        Service newService = null;
        String serviceName = InputHandler.ExtractString("Please enter the service name: ", Utilities.sc);
        BigDecimal fee = InputHandler.ExtractBigDecimal("Please enter the service fee (e.g. enter $12.32 as 12.32): ", Utilities.sc);
        
        if (fee == null || (fee.doubleValue() <= 0.0)) //Something went wrong
        {
            System.out.println("ERROR! An invalidly formatted/negative service fee was entered!\n");
        }
        else
        {
            int serviceCode = Utilities.RandomNDigitNumber(Limits.SERVICE_CODE_LENGTH_LIMIT);
            while (!Utilities.IsUniqueCode(serviceCode, this.provider.GetProviderDirectory())) //While the service code isn't unique
            {
                serviceCode = Utilities.RandomNDigitNumber(Limits.SERVICE_CODE_LENGTH_LIMIT); //Try another one
            }

            newService = new Service(serviceCode, Utilities.AdjustStringToLimit(serviceName, Limits.SERVICE_NAME_LENGTH_LIMIT), Utilities.AdjustFeeToLimit(fee, Limits.SERVICE_FEE_LIMIT));
            this.provider.AddService(newService);
            
            System.out.println("The new service was successfully to "+this.provider.GetName()+"'s provider directory!\n");
        }

        return newService;
    }
    
    /**
     * This function removes a service from the provider. It first proceeds to extract the service that the operator wishes
     * to remove and, if it exists, removes it. Otherwise, it does nothing (error handling is done in ExtractElementFromInput).
     */    
    protected Service RemoveExistingService()
    {
        Utilities.SetInfoParameters("service", "code", "ERROR! An invalid/nonexistant service code was entered! Cannot remove!\n");
        Service service = Utilities.ExtractElementFromInput(provider.GetProviderDirectory());
        
        if (service != null) //We can remove the service
        {
            this.provider.RemoveService(service);            
            System.out.println("Successfully removed service number "+(service.GetID())+" from "+(this.provider.GetName())+"'s provider directory!\n");
        }   
        
        return service;
    }
    
    /**
     * This function asks the user which service they'd like to update and then, if that service exists, passes
     * control over to updateTerminal.
     */
    protected void UpdateExistingService()
    {
        Utilities.SetInfoParameters("service", "code", "ERROR! An invalid/nonexistant service code was entered! Cannot remove!\n");
        Service service = Utilities.ExtractElementFromInput(provider.GetProviderDirectory());
        
        if (service != null) //We can remove the service
        {
            this.updateTerminal.SetServiceTo(service);
            this.updateTerminal.UseTerminal();
            System.out.println();
        }           
    }
}
