
import java.util.TreeSet;

/**
 *
 * @author Enis
 * This is a class derived from ChocAnTerminal.
 * Its main purpose is to store common functions that will be shared between
 * ModifyMembersTerminal and ModifyProvidersTerminal when adding new members and providers, respectively,
 * since a member and provider have the same information (e.g. they're classified by a number, name, and address).
 * 
 * It is abstract because it is not intended to be used as a terminal
 */
public abstract class InformationExtractionTerminal extends ChocAnTerminal
{    
    public InformationExtractionTerminal(ChocAnDPS dataCenter_)
    {
        super(dataCenter_);
    }
    
    /**
     * This function gives the user the chance to enter the information
     * of destination. Specifically, they can change the name,
     * address, city, state and zip code all at once.
     * 
     * 
     * After this function executes, destination's fields
     * should have the same parameters as what the user entered,
     * sized to their corresponding limits.
     * 
     * Note that this function is only used when adding a new member or provider
     * to obtain their information all at once.
     * 
     * @param destination the information location to be updated
     * @param identifier the identifier (either "member" or "provider")
     */
    protected void ExtractInformation(Information destination, String identifier)
    {
        destination.SetNameTo(this.ExtractInfoComponent(identifier, "name", Limits.INFO_NAME_LENGTH_LIMIT));
        destination.ChangeStreetAddress(this.ExtractInfoComponent(identifier, "address", Limits.STREET_ADDRESS_LENGTH_LIMIT));
        destination.ChangeCity(this.ExtractInfoComponent(identifier, "city", Limits.CITY_NAME_LENGTH_LIMIT));
        destination.ChangeState(this.ExtractInfoComponent(identifier, "state", Limits.STATE_LENGTH_LIMIT).toUpperCase());
        destination.ChangeZip(this.ExtractInfoComponent(identifier, "zip code", Limits.ZIP_CODE_LENGTH_LIMIT));
    }
    
    /**
     * This method abstracts the ExtractInformation method in the sense that it takes some component
     * of info that the user needs to enter, its limit, and then has the user enter that component.
     * 
     * It returns the component sized to its specified length.
     * 
     * @param identifier the identifier (either "member" or "provider)
     * @param component the portion of the information requested (either name, address, city, state, or zip code)
     * @param componentLimit the maximum length of the component
     * @return 
     */
    protected String ExtractInfoComponent(String identifier, String component, int componentLimit)
    {
        String input = InputHandler.ExtractString("Please enter the new "+identifier+"'s "+component+": ", Utilities.sc);
        return Utilities.AdjustStringToLimit(input, componentLimit);
    }
    
    /**
     * This method randomly generates a 9-digit ID number that is unique in the sense
     * that container does not have it. This function is used to generate
     * member and provider numbers, with container consisting of either
     * the member or provider databases, respectively.
     * 
     * @param <E> Either Member or Provider
     * @param container Either a member database or provider database
     * @return A 9-digit, randomly generated ID that is unique to container.
     */
    protected <E> int ExtractID(TreeSet<E> container)
    {
        int id = Utilities.RandomNDigitNumber(Limits.INFO_NUMBER_LENGTH_LIMIT);
        while (!Utilities.IsUniqueCode(id, container)) //While the service code isn't unique
        {
            id = Utilities.RandomNDigitNumber(Limits.INFO_NUMBER_LENGTH_LIMIT); //Try another one
        }        
        
        return id;
    }
}
