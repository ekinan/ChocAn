import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Enis
 * This is the provider terminal. It is derived from ModifyServicesTerminal because
 * some of its options are identical to it and so it is desirable that these same functions
 * be re-used in implementing them.
 * 
 * Here, the provider is given options:
 *      0) Serve member
 *      1) Request provider directory
 *      2) Write service record to disk
 *      3) Add a new service
 *      4) Remove an existing service
 *      5) Update an existing service
 *      6) Exit
 */
public class ProviderTerminal extends ModifyServicesTerminal
{
    /**
     * This is the ChocAn data center variable. It is necessary because the service terminal
     * is derived from Terminal and not ChocAnTerminal
     */
    private ChocAnDPS dataCenter = null;
    
    /**
     * This is the comparator that allows services to be compared via
     * alphabetical order instead of by their IDs. It is used to make option 2)
     * "Request provider directory" more efficient.
     */
    private ServiceAlphabetical servComparator = new ServiceAlphabetical();
    
    /**
     * This serves the same purpose as the provider directory in the Provider
     * class, except it stores the services in alphabetical order.
     * This makes it so the provider directories aren't iterated through every time
     * the user enters option 1) to extract the services in alphabetical order.
     */
    private TreeSet<Service> servAlphabetical = new TreeSet<Service>(servComparator);
    
    public ProviderTerminal(ChocAnDPS dataCenter_)
    {
        super(true);
        this.dataCenter = dataCenter_;
        
        this.options = new String[7];
        
        options[0] = "Serve member.";
        options[1] = "Request provider directory.";
        options[2] = "Write service record to disk.";
        options[3] = "Add a new service.";
        options[4] = "Remove an existing service.";
        options[5] = "Update an existing service.";
        options[6] = "Exit.";        
    }
    
    /**
     * This function sets the current provider to provider_
     * and also extracts its provider directory in alphabetical order
     * 
     * @param provider_ The provider to be set to
     */
    public void SetProviderTo(Provider provider_)
    {
        this.provider = provider_;
        this.servAlphabetical.clear(); //Erase the old services
        this.provider.ExtractServicesInAlphabeticalOrder(this.servAlphabetical); //Extract the services in alphabetical order
    }

    /**
     * Wrapper is there to avoid having to insert throw IOException into the abstract Terminal class, since
     * not all terminals will have file writes.
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
        return 7;
    }
    
    protected void PrintTerminalInfo()
    {
        LocalDateTime globalDate = this.dataCenter.GetGlobalDate();
        System.out.println("Provider terminal for provider number: "+(this.provider.GetID()));
        System.out.println("Provider name: "+this.provider.GetName());        
        System.out.println("Provider date: "+globalDate.format(DateTimeFormatter.ofPattern(Format.DATE_TERMINAL)));
        System.out.println("Provider time: "+globalDate.format(DateTimeFormatter.ofPattern(Format.TIME))+"\n");        
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
                this.ServeMember();
                break;
            case 1:
                this.RequestProviderDirectory();
                break;
            case 2:
                this.WriteNewServiceRecord();
                break;
            case 3:
                Service newService = this.AddNewService();
                if (newService != null)
                {
                    this.servAlphabetical.add(newService);
                }
                
                System.out.println("DONE ADDING THE NEW SERVICE!!!\n");
                break;
            case 4:
                Service toRemove = this.RemoveExistingService();
                if (toRemove != null)
                {
                    this.servAlphabetical.remove(toRemove);
                }
                
                break;
            case 5:
                this.UpdateExistingService();
                break;
            default:
                break;
            }       
        } while (userChoice != (this.GetNumberOfOptions()-1));
        
        return -1; //This only returns if we've exited from the terminal.         
    }
    
    /**
     * This function essentially carries out the member-service use case outlined in the
     * Requirements document. The scenario is as follows:
     * 
     * 1) Member walks in, provider enters their member number into the terminal
     * 2) If the number is valid, the provider enters the service that is to be provided for them
     *      Otherwise, the program goes back to the terminal menu. Note that if the member is suspended,
     *      the program accordingly tells the terminal that this is the case.
     * 
     * 3) If the entered service is invalid, the program goes back to the terminal menu. Otherwise,
     *      the terminal asks the provider to verify if the entered service is correct.
     * 
     * 4) If the service is correct, the provider proceeds to enter the comments associated with
     *      the service, and the service is written to record.
     *      If the service is not correct, an error message is outputted indicating that
     *      the entered service is incorrect.
     * 
     * 5) Finally, an output message indicating a successful interaction is displayed,
     *      and the fee to be billed is displayed on the screen.
     */
    private void ServeMember()
    {
        Utilities.SetInfoParameters("member", "number", "ERROR: Invalid/nonexistant member number entered!\n");        
        Member member = Utilities.ExtractElementFromInput(this.dataCenter.GetMemberDatabase());
        
        if (member != null)
        {
            if (member.GetStatus()) //Check if member is suspended before proceeding
            {
                System.out.println("Member has been validated. Welcome to the ChocAn provider "+(this.provider.GetName())+", "+(member.GetName())+"!");                
                Utilities.SetInfoParameters("service", "code", "ERROR: Invalid/nonexistant service code entered!\n");
                Service service = Utilities.ExtractElementFromInput(this.provider.GetProviderDirectory());
                
                if (service != null) //We have a valid service, now we need to ask provider to verify that
                {
                    if (this.IsCorrectService(service))
                    {
                        String comments = this.GetServiceComments();
                        this.dataCenter.GetServiceRecordDisk().WriteToDisk(new ServiceRecord(this.provider.GetProviderDateTime(), this.dataCenter.GetGlobalDate().toLocalDate(), 
                                                                            this.provider.GetID(), member.GetID(), service.GetID(), comments)); //Write the new record
                                                
                        System.out.println(member.GetName()+" has successfully been provided the service of \""+service.GetName()+"\"!");
                        System.out.println("This interaction was also successfully written to disk!");
                        System.out.println("Fee to be billed to ChocAn: "+(Format.USD_COST_FORMAT.format(service.GetFee().doubleValue())));
                        System.out.println("Thank you!\n");

                    }
                    else
                    {
                        System.out.println("ERROR: Could not provide service because the entered service was incorrect!\n");
                    }
                }
            }
            else //Member is suspended
            {
                System.out.println("ERROR: Member "+(member.GetID())+"'s membership is suspended!\n");
            }
        }
    }
    
    /**
     * This function outputs a file that lists the services of the provider in alphabetical order.
     * It is found in each provider's own personalized directory.
     * 
     * @throws IOException 
     */
    private void RequestProviderDirectory() throws IOException
    {
        BufferedWriter writer = FileUtilities.OpenWriter(FileUtilities.FILE_LOC+"/"+FileUtilities.PROVIDERS_LOC+"/"+this.provider.GetID()+"/"+FileUtilities.ALPHABETICAL_PROV_DIR);
        
        writer.write("Below you will find the provider directory for " + (this.provider.GetName()) + " in alphabetical order.\n\n");        
        this.WriteServicesInAlphabeticalOrder(writer);        
        writer.close();
        
        System.out.println("The provider directory for "+(this.provider.GetName())+" was successfully been written!\n");
    }
    
    /**
     * This function writes a new service record assuming that a hardware failure occurred
     * when serving the member. It proceeds to ask the provider to enter the member number,
     * service code, service date, and then to verify if it is the correct service. After,
     * it asks them to enter the comments before writing the service to disk and outputting
     * an appropriate message indicating that this has been done.
     * 
     * If the member number, service number, or service date are invalid, an appropriate error message is outputted.
     * 
     */
    private void WriteNewServiceRecord()
    {
        Utilities.SetInfoParameters("member", "number", "ERROR: Invalid/nonexistant member number entered!\n");
        Member member = Utilities.ExtractElementFromInput(this.dataCenter.GetMemberDatabase());
        
        if (member != null) //The member code is valid
        {
            Utilities.SetInfoParameters("service", "code", "ERROR: Invalid/nonexistant service code entered!\n");
            Service service = Utilities.ExtractElementFromInput(this.provider.GetProviderDirectory());
            
            if (service != null) //Service code is valid, so now we enter the date the service was provided
            {
                if (this.IsCorrectService(service))
                {
                    LocalDate serviceDate = InputHandler.ExtractDate("Please enter the service date (in MM-DD-YYYY): ", Utilities.sc);
                    if (serviceDate == null || !Utilities.IsWithinDate(serviceDate, this.provider.GetProviderDateTime().toLocalDate())) //Date couldn't be parsed, or a date greater than the provider date was entered
                    {
                        System.out.println("ERROR: Invalid/incorrect service date was entered!\n");
                    }
                    else //Now we have to enter the comments for the record
                    {                    
                        String comments = this.GetServiceComments();                       
                        this.dataCenter.GetServiceRecordDisk().WriteToDisk(new ServiceRecord(this.provider.GetProviderDateTime(), serviceDate, this.provider.GetID(), member.GetID(), service.GetID(), comments)); //Write the new record to disk

                        System.out.println("The service record was successfully added to the disk!\n");
                    }
                }
                else
                {
                    System.out.println("ERROR: Could not write to disk because the entered service was incorrect!\n");
                }
            }
        }
    }
        
    /**
     * This function extracts the comments made by the provider pertaining to any service records that are
     * going to be written.
     * 
     * @return service record comments
     */
    private String GetServiceComments()
    {
        String phrase = "Please enter the comments (up to "+Limits.COMMENT_LENGTH_LIMIT+" characters)\n";
        phrase += "Type in "+Format.COMMENTS_DELIMITER+" on a separate line to indicate that you've reached the end of the comment.\n";
        phrase += "Comments (enter below):\n";
        
        return Utilities.AdjustStringToLimit(InputHandler.ExtractComments(phrase, Utilities.sc), Limits.COMMENT_LENGTH_LIMIT);
    }
    
    /**
     * This function asks the user to verify that the service is
     * the correct service that was provided to the member.
     * 
     * Acceptable answers are any form of "yes" or "y", while
     * all other answers are interpreted as "no."
     * 
     * @param service
     * @return 
     */
    private boolean IsCorrectService(Service service)
    {
        String phrase = "The service named \""+(service.GetName())+"\" was provided.\n";
        phrase += "Is this correct? Please enter yes (y) if so. All other inputs are interpreted as no.\n";
        phrase += "Answer: ";
        String result = InputHandler.ExtractString(phrase, Utilities.sc).toUpperCase();
        return (result.equals("YES") || result.equals("Y"));
    }
    
    /**
     * This function writes the service records of the current provider in alphabetical order
     * to an open file.
     * 
     * @param writer The open file
     * @throws IOException 
     */
    private void WriteServicesInAlphabeticalOrder(BufferedWriter writer) throws IOException
    {
        Iterator<Service> iterator = this.servAlphabetical.iterator();
        Service currentService = null;
        while (iterator.hasNext())
        {
            currentService = iterator.next();
            writer.write("Service name: "+currentService.GetName()+"\n");
            writer.write("Service code: "+currentService.GetID()+"\n");
            writer.write("Service fee: "+Format.USD_COST_FORMAT.format(currentService.GetFee().doubleValue())+"\n\n");            
        }        
    }
}
