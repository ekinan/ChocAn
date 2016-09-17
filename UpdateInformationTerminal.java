/**
 *
 * @author Enis
 * This is the terminal that's used to update the information
 * of either an existing member or a provider. It gives the user the
 * following options:
 * 
 *      0) Change name
 *      1) Change street address
 *      2) Change city
 *      3) Change state
 *      4) Change zip code
 *      5) Exit
 * 
 * The methods are self-explanatory from their titles. Essentially,
 * they ask the user to enter either a name, address, city, state, or zip,
 * and then they update the corresponding field of the existing variable
 * info accordingly (being sure to adjust the input down if it exceeds
 * the allowable length limits of the parameter).
 */

public class UpdateInformationTerminal extends Terminal
{
    /**
     * The information that's being changed.
     */
    private Information info = null;
    
    /**
     * The identifier is either "member" or "provider," used to
     * let the user know what type of "Information" object they
     * are working with.
     */
    private String identifier = "";
    
    public UpdateInformationTerminal(String identifier_)
    {
        this.options = new String[6];
        
        this.options[0] = "Change name.";
        this.options[1] = "Change street address.";
        this.options[2] = "Change city.";
        this.options[3] = "Change state.";
        this.options[4] = "Change zip code.";
        this.options[5] = "Exit.";
        
        this.identifier = identifier_;
    }
    
    public void SetInformationTo(Information info_)
    {
        this.info = info_;
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
                this.ChangeStreetAddress();
                break;
            case 2:
                this.ChangeCity();
                break;
            case 3:
                this.ChangeState();
                break;
            case 4:
                this.ChangeZipCode();
                break;
            default:
                break;
            }   
        } while (userChoice != (this.GetNumberOfOptions()-1));
        
        return -1; //This only returns if we've exited from the terminal.        
    }

    protected void PrintTerminalInfo()
    {
        System.out.println("Information Update Terminal\n");       
    }    

    protected int GetNumberOfOptions()
    {
        return 6;
    }
    
    private void ChangeName()
    {
        String name = InputHandler.ExtractString("Please enter the new name: ", Utilities.sc);
        this.info.SetNameTo(Utilities.AdjustStringToLimit(name, Limits.INFO_NAME_LENGTH_LIMIT));
        
        System.out.println("Successfully changed "+this.identifier+" number "+(this.info.GetID())+"'s name to "+(this.info.GetName())+"\n");        
    }
    
    private void ChangeStreetAddress()
    {
        String address = InputHandler.ExtractString("Please enter the new street address: ", Utilities.sc);
        this.info.ChangeStreetAddress(Utilities.AdjustStringToLimit(address, Limits.STREET_ADDRESS_LENGTH_LIMIT));
        
        System.out.println("Successfully changed "+this.identifier+" number "+(this.info.GetID())+"'s street address to "+(this.info.GetStreetAddress())+"\n");        
    }
    
    private void ChangeCity()
    {
        String city = InputHandler.ExtractString("Please enter the new city: ", Utilities.sc);
        this.info.ChangeCity(Utilities.AdjustStringToLimit(city, Limits.CITY_NAME_LENGTH_LIMIT));
        
        System.out.println("Successfully changed "+this.identifier+" number "+(this.info.GetID())+"'s city to "+(this.info.GetCity())+"\n");        
    }
    
    private void ChangeState()
    {
        String state = InputHandler.ExtractString("Please enter the new state: ", Utilities.sc);
        this.info.ChangeState(Utilities.AdjustStringToLimit(state, Limits.STATE_LENGTH_LIMIT).toUpperCase());
        
        System.out.println("Successfully changed "+this.identifier+" number "+(this.info.GetID())+"'s state to "+(this.info.GetState())+"\n");        
    }
    
    private void ChangeZipCode()
    {
        String zipCode = InputHandler.ExtractString("Please enter the new zip code: ", Utilities.sc);
        this.info.ChangeZip(Utilities.AdjustStringToLimit(zipCode, Limits.ZIP_CODE_LENGTH_LIMIT));
        
        System.out.println("Successfully changed "+this.identifier+" number "+(this.info.GetID())+"'s zip code to "+(this.info.GetZipCode())+"\n");        
    }
    
}
