import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Enis
 * This is the ancestor terminal class that governs the various terminals in the program.
 * 
 * 
 */
public abstract class Terminal
{
    /**
     * Every terminal should have an array listing the options they give to the user.
     * Note that the last option should always be "Exit." to make it easier to facilitate
     * the terminal input (see the InputHandler class for more details)
     */
    protected String[] options = null;
    
    /**
     * Every terminal should have some function that allows the user to use and interact with it.
     * Here, the terminal effectively cycles through its options until the user decides to exit it/
     * 
     * The general code is
     * 
     * do
     * {
     *      PrintTerminalInfo();
     *      switch (userChoice)
     *      {
     *          List methods to handle each option in the terminal, except
     *          for the exit option.
     *      }
     * } while (user does not want to exit)
     * 
     * @return Was intended to be used as a signal for a switch and exit, but I realized I did not need it. 
     */
    public abstract int UseTerminal();
    
    /**
     * This function returns the number of options in the terminal. 
     * 
     * @return Number of options the terminal has to offer
     */
    protected abstract int GetNumberOfOptions();
    
    /**
     * This function is used to print the terminal information to the user,
     * so that they can differentiate between the many terminals used
     * in this project.
     */
    protected abstract void PrintTerminalInfo();
}
