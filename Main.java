import java.io.IOException;
/**
 *
 * @author Enis
 * The main class of the program.
 */
public class Main
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        System.out.println("File loc: "+FileUtilities.FILE_LOC+"\n");
        ChocAnDPS dataCenter = new ChocAnDPS();
        GlobalTerminal terminal = new GlobalTerminal(dataCenter);        
        terminal.UseTerminal();
        dataCenter.Exit();
    }

}
