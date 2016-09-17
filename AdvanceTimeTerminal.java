
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Enis
 */
public class AdvanceTimeTerminal extends ChocAnTerminal
{

    public AdvanceTimeTerminal(ChocAnDPS dataCenter_)
    {
        super(dataCenter_);
        
        this.options = new String[3];
        
        this.options[0] = "Advance by days.";
        this.options[1] = "Advance by hours.";
        this.options[2] = "Exit";        
    }

    public int UseTerminal()
    {
        int returnVal = 0;
        
        try
        {
            returnVal = this.UseTerminalWrapper();
        }catch(IOException ex)
        {
            System.out.println("ERROR! Cannot advance time because the weekly data processing doesn't work correctly!\n");
        }
        
        return returnVal;
    }

    protected int GetNumberOfOptions()
    {
        return 3;
    }

    protected void PrintTerminalInfo()
    {
        System.out.println("Please indicate whether you would like to advance the time by days or hours.");
        System.out.println("Note that the system can only advance a maximum of 7 days at a time.\n");
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
                this.AdvanceByDays();
                break;
            case 1:
                this.AdvanceByHours();
                break;
            default:
                break;
            }    
        } while (userChoice != (this.GetNumberOfOptions()-1));
        
        return -1; //This only returns if we've exited from the terminal.        
    }
    
    private void AdvanceByDays() throws IOException
    {
        int days = this.ExtractTimeWithinLimit("days", 7);
        if (days >= 0)
        {
            this.dataCenter.AdvanceTimeByDays(days);
            System.out.println("The time has successfully advanced by "+days+" days!\n");
        }
    }
    
    private void AdvanceByHours() throws IOException
    {
        int hours = this.ExtractTimeWithinLimit("hours", 24*7);
        if (hours >= 0)
        {
            this.dataCenter.AdvanceTimeByHours(hours);
            System.out.println("The time has successfully advanced by "+hours+" hours!\n");
        }        
    }
    
    private int ExtractTimeWithinLimit(String component, int limit)
    {
        int t = InputHandler.ExtractInt("Please enter the number of "+component+" to advance by: ", Utilities.sc);
        if (t < 0)
        {
            System.out.println("ERROR! Negative time amount was entered. Cannot advance "+component+"s.\n");
            return -1;
        }
        
        return Utilities.AdjustIntToLimit(t,limit);
    }
}
