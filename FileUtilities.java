import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Enis
 * This class is used to store any file-related information.
 * Its fields consist of a bunch of file names, where various parts
 * of the data center data is saved. It also has some wrappers to the Java
 * file functions, to avoid inserting try-catch blocks and cluttering the code
 * (I avoided exception handling here; was more focused on the functionality
 * instead)
 */
public class FileUtilities
{
    /**
     * FILE_LOC is the main directory where all of the data (e.g. members, providers, service records)
     * is located.
     */
    public static final String FILE_LOC = System.getProperty("user.dir") + "/ChocAnFiles";
    /**
     * This text file stores the provider numbers of the data center
     */
    public static final String PROVIDER_NUMBERS_FILE_LOC = "ProviderNumbers.txt";
    /**
     * This text file stores the member numbers
     */
    public static final String MEMBER_NUMBERS_FILE_LOC = "MemberNumbers.txt";
    /**
     * This file stores the data center's global date
     */
    public static final String CHOC_AN_PARAMETERS_FILE_LOC = "ChocAnParameters.txt";
    /**
     * This is the directory where all the provider information is stored.
     * It contains sub-directories that are named by the Provider number
     */
    public static final String PROVIDERS_LOC = "Providers";
    /**
     * Directory where all the member information is stored. It contains
     * sub-directories that are named by the Member number.
     */
    public static final String MEMBER_LOC = "Members";    
    /**
     * Text file where member or provider information is stored. These are contained
     * within the private directories belonging to each member or provider
     */
    public static final String INFO_LOC = "Info.txt";    
    /**
     * This is where the provider directory of each provider is located,
     * listing all of its services.
     */
    public static final String PROVIDER_DIR_LOC = "ProviderDirectory.txt";
    /**
     * This is the text file storing all of the service records currently
     * on ChocAn's disk
     */
    public static final String SERVICE_RECORD_LOC = "ServiceRecords.txt";
    /**
     * This is the directory housing the generated EFT reports
     */
    public static final String EFT_LOC = "EFT_Reports";
    /**
     * Directory that houses the generated summary reports.
     */
    public static final String SUMMARY_LOC = "Summary_Reports";
    /**
     * Text file that stores the provider directory in alphabetical order
     */
    public static final String ALPHABETICAL_PROV_DIR = "AlphabeticalProviderDirectory.txt";
    /**
     * Directory where the data center information is put.
     */
    public static final String OUTPUT_LOC = "Output";
    /**
     * Lists the current members of ChocAn in a more user-friendly format
     */
    public static final String MEMBER_OUTPUT_LOC = "CurrentMembersList.txt";
    /**
     * Lists the current providers of ChocAn in a more user-friendly format
     */
    public static final String PROVIDER_OUTPUT_LOC = "CurrentProvidersList.txt";
    /**
     * Lists the current service records on ChocAn's disk in a more user-friendly
     * format.
     */
    public static final String SERVICE_RECORDS_OUTPUT_LOC = "CurrentServiceRecordsOnDisk.txt";
    
    /**
     * File-decoding, use ASCII
     */
    public static Charset FILE_DECODING = Charset.forName("US-ASCII");
    
    /**
     * This function takes a file name and tries to open a writer to it.
     * If it fails, the function returns null. Otherwise, it returns a writer
     * that can be used to write information to.
     * 
     * @param fileName The file name that's to be opened. Should end with .txt
     * @return a writer that points to the file specified by fileName
     */
    public static BufferedWriter OpenWriter(final String fileName)
    {
        Path p = Paths.get(fileName);
        try
        {
            BufferedWriter writer = Files.newBufferedWriter(p, FileUtilities.FILE_DECODING);
            return writer;
        } catch(IOException ex)
        {
            return null;
        }      
    }
    
    /**
     * This function takes a file name and tries to open a reader to it.
     * If it fails, the function returns null. Otherwise, it returns a reader
     * that can be used to read information to.
     * 
     * @param fileName The file name that's to be opened. Should end with .txt
     * @return 
     */
    public static BufferedReader OpenReader(final String fileName)
    {
        Path p = Paths.get(fileName);
        try
        {
            BufferedReader reader = Files.newBufferedReader(p, FileUtilities.FILE_DECODING);
            return reader;
        } catch(IOException ex)
        {
            return null;
        }        
    }
    
    /**
     * Creates a directory having the name dirName, if it doesn't exist
     * already.
     * 
     * @param dirName the directory path where the directory is created
     * @return Returns true if successful, false otherwise
     */
    public static boolean CreateDirectory(final String dirName)
    {
        File file = new File(dirName);
        if (!file.exists()) 
        {
            return file.mkdir();
        }        
        return true;
    }
    
    /**
     * Checks if the file by fileName exists
     * 
     * @param fileName the file name
     * @return true if the file exists, false otherwise
     */
    public static boolean FileExists(final String fileName)
    {
        return ((new File(fileName)).exists());
    }
    
    
}
