import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author Enis
 * This class is used to handle the input requirements of the program.
 * 
 */

public class InputHandler
{
    /*
    This is the function that does the handling for the terminals. It returns an integer value
    between 0 and options.length - 1 to indicate the user's choice. Until the user enters a valid number,
    the function loops indefinitely
    */
    public static int TerminalInput(final String[] options, Scanner sc)
    {
        int userChoice = -1;
        DisplayOptions(options);
        System.out.print("Choice: ");

        if (sc.hasNextInt()) //Don't take input if the stream is in an input fail state
                userChoice = sc.nextInt();
        
        while (!InputHandler.IsValidChoice(userChoice, options.length))
        {
                System.out.println(); //Flush the stream
                sc.nextLine();
                System.out.println("Invalid input! Please enter your choice from the options below.");
                InputHandler.DisplayOptions(options);
                
                System.out.print("Choice: ");
                try
                {
                    userChoice = sc.nextInt();
                }
                catch (InputMismatchException mismatch)
                {
                    userChoice = -1;
                }
        }

        return userChoice;
    }

    /*
    Displays the available options to the user
    */
    public static void DisplayOptions(final String[] options)
    {
        for (int i = 0; i < options.length; ++i)
        {
                for (int j = 0; j < 4; ++j)
                        System.out.print(' '); //Simulate setw(5)
                System.out.println(i + ". " + options[i]);
        }
        System.out.println();
    }

    /*
    Pauses the program until the user enters any key to proceed.
    */
    public static void Pause()
    {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter any key to continue: ");
        sc.next();
        System.out.println();
        sc.nextLine();
    }
    
    /**
     * Extracts a >= 0 integer from the input stream. If the input
     * is incorrectly formatted, it returns a negative value instead.
     * Primarily used to get inputs for the service, member, and provider
     * numbers.
     * 
     * It outputs whatever's in phrase to the user, and waits for their input
     * 
     * @param phrase Text phrase that's to be displayed to the user
     * @param sc //Input stream object
     * @return Integer >= 0 if successful, -1 otherwise
     */
    public static int ExtractInt(String phrase, Scanner sc)
    {
        System.out.print(phrase);
        int input = 0;
        
        if (sc.hasNextInt())
        {
            input = sc.nextInt();
        }
        else
        {
            sc.useDelimiter("\n");
            input = -1;
            sc.next();
            sc.reset();
        }        

        return input;        
    }
    
    /**
     * Extracts a string from the input stream. It outputs phrase to the user
     * to tell them what to input specifically.
     * 
     * @param phrase The phrase displayed to the user
     * @param sc The input stream
     * @return Whatever the user enters
     */
    public static String ExtractString(String phrase, Scanner sc)
    {
        System.out.print(phrase);
        sc.useDelimiter("\n");
        phrase = sc.next();
        sc.reset();
        
        return phrase;
    }
    
    /**
     * Extracts a big decimal number from the input stream. If the input is
     * incorrectly formatted, it returns a null pointer to indicate failure
     * 
     * Outputs phrase to tell the user what to enter
     * 
     * @param phrase Phrase to be displayed
     * @param sc The input stream
     * @return BigDecimal value if successful, null otherwise
     */
    public static BigDecimal ExtractBigDecimal(String phrase, Scanner sc)
    {
        System.out.print(phrase);
        BigDecimal input;
        
        if (sc.hasNextBigDecimal())
        {
            input = sc.nextBigDecimal();
        }
        else
        {
            sc.useDelimiter("\n");
            input = null;
            sc.next();
            sc.reset();
        }        
        
        return (input == null ? input : input.setScale(2, RoundingMode.CEILING)); 
    }    
    
    /**
     * Extracts a date object from the stream. User should enter as MM-DD-YYYY.
     * If the input is invalidly formatted, returns a null pointer to indicate failure.
     * 
     * Also outputs whatever's in phrase as the prompt
     * 
     * @param phrase The phrase to be displayed
     * @param sc Input stream
     * @return Returns a LocalDate object if the input is correctly entered, otherwise returns null
     */
    public static LocalDate ExtractDate(String phrase, Scanner sc)
    {
        System.out.print(phrase);
        sc.useDelimiter("\n");
        LocalDate returnVal = null;
        try
        {
            phrase = sc.next();
            returnVal = LocalDate.parse(phrase,DateTimeFormatter.ofPattern(Format.DATE));
        }
        catch (DateTimeParseException failed)
        {
            returnVal = null;
        }
        
        sc.reset();
        
        //sc.nextLine(); //Flush the input
        
        return returnVal;
    }
    
    /**
     * Extracts a date time object, formatted as MM-DD-YYYY HH:MM:SS
     * If the input is invalidly formatted, returns a null pointer to indicate failure
     * 
     * Also outputs whatever's in phrase as the prompt.
     * 
     * @param phrase The prompt
     * @param sc Input stream object
     * @return Returns a LocalDateTime object if the input is correctly entered,
     * otherwise returns null
     */
    public static LocalDateTime ExtractDateTime(String phrase, Scanner sc)
    {
        System.out.print(phrase);
        sc.useDelimiter("\n");
        LocalDateTime returnVal = null;
        try
        {
            phrase = sc.next();
            returnVal = LocalDateTime.parse(phrase,DateTimeFormatter.ofPattern(Format.DATE+" "+Format.TIME));
        }
        catch (DateTimeParseException failed)
        {
            returnVal = null;
        }
        
        sc.reset();
        
        return returnVal;        
    }
    
    /**
     * Extracts the user comments pertaining to a particular service.
     * User is allowed to keep entering comments until they enter
     * whatever is in the COMMENT_DELIMITER variable on a separate line
     * 
     * Whatever's in phrase is the prompt outputted to the user
     * 
     * @param phrase The prompt
     * @param sc The input stream object
     * @return User comments regarding the particular service
     */
    public static String ExtractComments(String phrase, Scanner sc)
    {
        System.out.print(phrase);
        sc.useDelimiter("\n");
        
        String currentToken = null;
        phrase = "";
        if (!(currentToken = sc.next()).equals(Format.COMMENTS_DELIMITER))
        {
            phrase += currentToken;
            while (!(currentToken = sc.next()).equals(Format.COMMENTS_DELIMITER))
            {
                phrase += "\n"+currentToken;
            }
        }
        sc.reset();
        
        return phrase;
    }
    
    /*
    Checks if userChoice is a valid choice (for handling terminal inputs)
    */
    private static boolean IsValidChoice(int userChoice, int numOptions)
    {
        return ((0 <= userChoice) && (userChoice < numOptions));
    }   
    
    
}
