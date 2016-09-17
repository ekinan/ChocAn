
import java.util.HashSet;

/**
 *
 * @author Enis
 * This is the terminal that updates an individual member.
 * It gives the user the following options:
 * 
 *      0) Update the member information
 *      1) Update the member status
 *      2) Exit
 */
public class UpdateMemberTerminal extends Terminal
{
    /**
     * This is the member we are currently updating
     */
    private Member member = null;
    
    /**
     * The information terminal. This will be invoked when the user selects option 0
     */
    private UpdateInformationTerminal infoTerminal = null;
    
    public UpdateMemberTerminal()
    {        
        this.options = new String[3];
        
        options[0] = "Update member information.";
        options[1] = "Update member status.";
        options[2] = "Exit.";
        
        this.infoTerminal = new UpdateInformationTerminal("member");
    }
    
    public void SetMemberTo(Member member_)
    {
        this.member = member_;
        this.infoTerminal.SetInformationTo(this.member);
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
                this.UpdateMemberInformation();
                break;
            case 1:
                this.UpdateMemberStatus();
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
        System.out.println("Member records update terminal for member number: "+(this.member.GetID()));
        System.out.println("Member name: "+this.member.GetName()+"\n");              
    }    

    /**
     * Wrapper to infoTerminal.UseTerminal, since all of the information updating functions are there.
     */
    private void UpdateMemberInformation()
    {
        System.out.println();
        this.infoTerminal.UseTerminal();
        System.out.println("Successfully updated the information of member number "+this.member.GetID()+"!\n");
    }
    
    /**
     * This function updates the membership status. Essentially,
     * the user is asked to enter the new status by either typing in "valid"
     * or "suspended". If they type in "valid," the membership status is set to valid
     * (i.e. status = true); for "suspended," the membership status is set to suspended
     * (i.e. status = false).
     * 
     * If the user does not enter one of these words, then an ERROR message pops
     * up indicating the situation.
     */
    private void UpdateMemberStatus()
    {
        String phrase = "Change membership status to \"valid\" or \"suspended\"?\n";
        phrase += "Enter one of the words in quotations to proceed: ";
        
        String status = InputHandler.ExtractString(phrase, Utilities.sc).toUpperCase();
        if (status.equals("VALID"))
        {
            this.member.SetStatus(true);
            System.out.println("Member number "+this.member.GetID()+"'s status was successfully validated!\n");            
        }
        else if (status.equals("SUSPENDED"))
        {
            this.member.SetStatus(false);
            System.out.println("Member number "+this.member.GetID()+" is now suspended from accessing ChocAn's services!\n");
        }
        else //Invalid entry
        {
            System.out.println("ERROR! Invalid status was entered. Could not update member number "+this.member.GetID()+"'s status!\n");
        }
    }
}
