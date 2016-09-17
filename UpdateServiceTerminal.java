
import java.math.BigDecimal;
/**
 *
 * @author Enis
 * This terminal is used to update an individual service.
 * It gives the user the following options:
 *      0) Change service name
 *      1) Change service fee
 *      2) Exit
 * 
 */
public class UpdateServiceTerminal extends Terminal
{
    /**
     * The current service we are updating
     */
    private Service service = null;
    
    public UpdateServiceTerminal()
    {
        this.options = new String[3];
        this.options[0] = "Change service name.";
        this.options[1] = "Change service fee.";
        this.options[2] = "Exit.";        
    }
    
    public void SetServiceTo(Service service_)
    {
        this.service = service_;
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
                this.ChangeName();
                break;
            case 1:
                this.ChangeFee();
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
        System.out.println("Service records update terminal for service number: "+(this.service.GetID()));
        System.out.println("Service name: "+this.service.GetName()+"\n");              
    }     
    
    /**
     * The user is asked to enter the service's new name, and the corresponding field in "Service"
     * is updated to indicate the change (adhering to limits)
     */
    private void ChangeName()
    {
        String serviceName = InputHandler.ExtractString("Please enter the new service name: ", Utilities.sc);
        this.service.SetNameTo(Utilities.AdjustStringToLimit(serviceName, Limits.SERVICE_NAME_LENGTH_LIMIT));
        
        System.out.println("Successfully changed service number "+(this.service.GetID())+"'s name to "+(this.service.GetName())+"\n");
    }
    
    /**
     * The user is asked to enter the new service fee. If the input handler returns a null value or the user
     * enters a negative fee, an error message is invoked. Otherwise, the fee is adjusted to be within
     * the service fee limits, and the corresponding field in service is updated accordingly.
     */
    private void ChangeFee()
    {
        BigDecimal fee = InputHandler.ExtractBigDecimal("Please enter the new service fee (e.g. enter $12.32 as 12.32): ", Utilities.sc);        
        if (fee == null || (fee.doubleValue() <= 0.0)) //Something went wrong
        {
            System.out.println("ERROR! An invalidly formatted/negative service fee was entered! Cannot change!\n");
        }     
        else
        {            
            this.service.SetFee(Utilities.AdjustFeeToLimit(fee, Limits.SERVICE_FEE_LIMIT));
            System.out.println("Successfully changed service number "+(this.service.GetID())+"'s fee to "+(Format.USD_COST_FORMAT.format(this.service.GetFee().doubleValue()))+"\n");
        }
    }
    
}
