
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Enis
 * This is the terminal that deals with modifying member records.
 * It allows for the operator add new or remove old ChocAn members,
 * or to update the records (i.e. information and membership status)
 * of existing ones.
 * 
 * The terminal options are below:
 *      0) Add new member
 *      1) Remove existing member
 *      2) Update existing member records
 *      3) Exit
 * 
 * Note it is derived from InformationExtractionTerminal so
 * as to have access to that class' info extracting functions.
 */
public class ModifyMembersTerminal extends InformationExtractionTerminal
{
    /**
     * The updateTerminal field is here, because it will be what is used
     * to update an existing member's records. See its comments for more info.
     */
    UpdateMemberTerminal updateTerminal = new UpdateMemberTerminal();

    public ModifyMembersTerminal(ChocAnDPS dataCenter_)
    {
        super(dataCenter_);
        
        this.options = new String[4];
        
        this.options[0] = "Add new member.";
        this.options[1] = "Remove existing member.";
        this.options[2] = "Update existing member records.";
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
                this.AddNewMember();
                break;
            case 1:
                this.RemoveExistingMember();
                break;
            case 2:
                this.UpdateExistingMemberRecords();
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
        System.out.println("Member records terminal.");
        System.out.println("Date: "+globalDate.format(DateTimeFormatter.ofPattern(Format.DATE_TERMINAL)));
        System.out.println("Time: "+globalDate.format(DateTimeFormatter.ofPattern(Format.TIME))+"\n");        
    }    
    
    /**
     * This function adds a new member to ChocAn.
     * 
     * It first generates a member ID for the member, and then calls the ExtractInformation function
     * to get the rest of its info. Afterwards, it proceeds to create the member's own private directory
     * (needed in order to store their reports, information, etc.), before adding it
     * to the database and then outputting a message indicating success.
     * 
     */
    private void AddNewMember()
    {
        int memberNum = this.ExtractID(this.dataCenter.GetMemberDatabase());
        Member member = new Member(memberNum);
        this.ExtractInformation(member, "member");
        
        FileUtilities.CreateDirectory(FileUtilities.FILE_LOC+"/"+FileUtilities.MEMBER_LOC+"/"+member.GetID()); //Create the corresponding member directory
        
        this.dataCenter.AddMember(member);
        
        System.out.println("The new member has successfully been added to the ChocAn database!\n");
    }
    
    /**
     * This function removes a member from ChocAn. It first proceeds to extract the member that the operator wishes
     * to remove and, if they exist, removes them. Otherwise, it does nothing (error handling is done in ExtractElementFromInput)
     * 
     * Note that when a member is removed, their directory is still active. This is because we would still like for them
     * to get their membership report indicating all of the services they were provided while their membership was still active.
     * ServiceRecordsDisk still stores this information. After the reports are generated, the user can manually remove the directories
     * (I didn't have time to myself).
     */
    private void RemoveExistingMember()
    {
        Utilities.SetInfoParameters("member", "number", "ERROR! An invalid/nonexistant member number was entered! Cannot remove!\n");
        Member member = Utilities.ExtractElementFromInput(this.dataCenter.GetMemberDatabase());
        
        if (member != null) //We can remove the service
        {
            this.dataCenter.RemoveMember(member);
            System.out.println("Successfully removed member number "+(member.GetID())+" from ChocAn's system!\n");
        }           
    }
    
    /**
     * This function asks the user which member they'd like to update and then, if that member exists, passes
     * control over to updateTerminal.
     */
    private void UpdateExistingMemberRecords()
    {
        Utilities.SetInfoParameters("member", "number", "ERROR! An invalid/nonexistant member number was entered! Cannot update!\n");
        Member member = Utilities.ExtractElementFromInput(this.dataCenter.GetMemberDatabase());
        
        if (member != null) //We can modify this member
        {
            updateTerminal.SetMemberTo(member);
            System.out.println();
            updateTerminal.UseTerminal();
            System.out.println("Successfully updated member number "+(member.GetID())+"'s records!\n");
        }
    }
    
}
