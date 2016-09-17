
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Enis
 * This is the terminal that deals with modifying provider records.
 * It allows for the operator add new or remove old ChocAn provider,
 * or to update the records (i.e. information and services)
 * of existing ones.
 * 
 * The terminal options are below:
 *      0) Add new provider
 *      1) Remove existing provider
 *      2) Update existing provider records
 *      3) Exit
 * 
 * Note it is derived from InformationExtractionTerminal so
 * as to have access to that class' info extracting functions.
 */
public class ModifyProvidersTerminal extends InformationExtractionTerminal
{
    /**
     * The updateTerminal field is here, because it will be what is used
     * to update an existing member's records. See its comments for more info.
     */
    UpdateProviderTerminal updateTerminal = new UpdateProviderTerminal();

    public ModifyProvidersTerminal(ChocAnDPS dataCenter_)
    {
        super(dataCenter_);
        
        this.options = new String[4];
        
        this.options[0] = "Add new provider.";
        this.options[1] = "Remove existing provider.";
        this.options[2] = "Update existing provider records.";
        this.options[3] = "Exit.";        
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
                this.AddNewProvider();
                break;
            case 1:
                this.RemoveExistingProvider();
                break;
            case 2:
                this.UpdateExistingProviderRecords();
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
        LocalDateTime globalDate = this.dataCenter.GetGlobalDate();
        System.out.println("Provider records terminal.");
        System.out.println("Date: "+globalDate.format(DateTimeFormatter.ofPattern(Format.DATE_TERMINAL)));
        System.out.println("Time: "+globalDate.format(DateTimeFormatter.ofPattern(Format.TIME))+"\n");        
    }    
    
    /**
     * This function adds a new provider to ChocAn.
     * First, it asks the user to enter the provider's personal date and time (MM-DD-YYYY HH:MM:SS), 
     * and then checks to see if the entered input is not null, where a null input occurs if the date is not entered
     * in the correct format. If the input is null, an error message is outputted. Otherwise the program
     * checks to see if the date is within the maximum allowable time zone difference of 26 hours from
     * the data center's date. If this condition is not met, an error message is outputted and the
     * provider is not added to the system.
     * 
     * 
     * If however the date is good, then the program generates a provider ID for the provider, and then 
     * calls the ExtractInformation function to get the rest of its info Finally, 
     * it proceeds to create the member's own private directory (needed in order to store their reports, information, etc.)
     * before adding it to the database and then outputting a message indicating success.
     * 
     */    
    private void AddNewProvider()
    {
        LocalDateTime providerDate = InputHandler.ExtractDateTime("Please enter the provider date and time (MM-DD-YYYY HH:MM:SS): ", Utilities.sc);
        if (providerDate != null) //Failed to extract the date, so we can't add this provider
        {
            if (Utilities.IsWithinValidTimeZone(providerDate, this.dataCenter.GetGlobalDate()))
            {
                int providerNum = this.ExtractID(this.dataCenter.GetProviderDatabase());
                Provider provider = new Provider(providerNum, providerDate);
                this.ExtractInformation(provider, "provider");        

                FileUtilities.CreateDirectory(FileUtilities.FILE_LOC+"/"+FileUtilities.PROVIDERS_LOC+"/"+provider.GetID()); //Create the corresponding member directory                
                
                this.dataCenter.AddProvider(provider);        
                System.out.println("The new provider has successfully been added to the ChocAn database!\n");
            }
            else
            {
                System.out.println("ERROR: Entered date and time is beyond the maximum allowable time zone difference! Cannot add provider!\n");
            }
        }
        else
        {
            System.out.println("ERROR: Invalid date and time entered! Cannot add provider!\n");
        }
    }
    
    /**
     * This function removes a provider from ChocAn. It first proceeds to extract the provider that the operator wishes
     * to remove and, if they exist, removes them. Otherwise, it does nothing (error handling is done in ExtractElementFromInput)
     * 
     * Note that when a provider is removed, their directory is still active. This is because we would still like for them
     * to get their provider report, and their EFT and summary entry, indicating all of the services they provided and their fee totals.
     * ServiceRecordsDisk still stores this information. After the reports are generated, the user can manually remove the directories
     * (I didn't have time to automate this myself).
     */    
    private void RemoveExistingProvider()
    {
        Utilities.SetInfoParameters("provider", "number", "ERROR! An invalid/nonexistant provider number was entered! Cannot remove!\n");
        Provider provider = Utilities.ExtractElementFromInput(this.dataCenter.GetProviderDatabase());
        
        if (provider != null) //We can remove the provider
        {
            this.dataCenter.RemoveProvider(provider);                
            System.out.println("Successfully removed provider number "+(provider.GetID())+" from ChocAn's system!\n");
        }        
    }
    
    /**
     * This function asks the user which provider they'd like to update and then, if that provider exists, passes
     * control over to updateTerminal.
     */
    private void UpdateExistingProviderRecords()
    {
        Utilities.SetInfoParameters("provider", "number", "ERROR! An invalid/nonexistant provider number was entered! Cannot update!\n");
        Provider provider = Utilities.ExtractElementFromInput(this.dataCenter.GetProviderDatabase());
        
        if (provider != null) //We can modify this member
        {
            updateTerminal.SetProviderTo(provider);
            System.out.println();
            updateTerminal.UseTerminal();
            System.out.println("Successfully updated provider number "+(provider.GetID())+"'s records!\n");
        }        
    }
    
}
