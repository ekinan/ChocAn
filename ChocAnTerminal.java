
/**
 *
 * @author Enis
 * This is an extension of terminal. Many of the terminals in the program will have the data center
 * itself as a field to get access to the member, provider, and service record information, which is why
 * I included this as a class to derive them off of.
 */
public abstract class ChocAnTerminal extends Terminal
{
    /**
     * The data center that's being used.
     */
    protected ChocAnDPS dataCenter = null;
    
    public ChocAnTerminal(ChocAnDPS dataCenter_)
    {
        this.dataCenter = dataCenter_;
    }    
}
