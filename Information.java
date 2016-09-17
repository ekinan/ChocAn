import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Enis
 * This is the information class, which houses all of the elements that providers and members
 * share in common. Effectively, it is different from the Identification class in that it
 * also stores address info of the object, as can be seen from its fields below.
 * The class consists of setters and getters for these fields.
 * 
 */
public class Information extends Identification
{
    private String street = "";
    private String city = "";
    private String state = "";
    private String zip = "";
    
    /**
     * 
     * @param id_ 
     */
    public Information(final int id_)
    {
        super(id_);
    }
    
    /**
     * 
     * @param id_
     * @param name_ 
     */
    public Information(final int id_, final String name_)
    {
        super(id_, name_);
    }
    
    /**
     * 
     * @param id_
     * @param name_
     * @param street_
     * @param city_
     * @param state_
     * @param zip_ 
     */
    public Information(final int id_, final String name_, final String street_, final String city_, final String state_, final String zip_)
    {
        super(id_, name_);
        this.street = street_;
        this.city = city_;
        this.state = state_;
        this.zip = zip_;
    }
    
    /**
     * 
     * @param original 
     */
    public Information(final Information original)
    {
        super(original); //Copy the base class parameters
        
        this.street = original.street;
        this.city = original.city;
        this.state = original.state;
        this.zip = original.zip;
    }
    
    /**
     * 
     * @return Street address
     */
    public String GetStreetAddress()
    {
        return this.street;
    }
    
    /**
     * 
     * @return City
     */
    public String GetCity()
    {
        return this.city;
    }
    
    /**
     * 
     * @return State
     */
    public String GetState()
    {
        return this.state;
    }
    
    /**
     * 
     * @return Zip code
     */
    public String GetZipCode()
    {
        return this.zip;
    }
    
    /**
     * 
     * @param newAddress 
     */
    public void ChangeStreetAddress(final String newAddress)
    {
        this.street = newAddress;
    }
    
    /**
     * 
     * @param newCity 
     */
    public void ChangeCity(final String newCity)
    {
        this.city = newCity;
    }
    
    /**
     * 
     * @param newState 
     */
    public void ChangeState(final String newState)
    {
        this.state = newState;
    }
 
    /**
     * 
     * @param newZip 
     */
    public void ChangeZip(final String newZip)
    {
        this.zip = newZip;
    }    
    
    /**
     * This method was overridden for debugging purposes.
     * 
     * @return String
     */
    public String toString()
    {
        String info = "ID: " + (this.GetID()) + "\n"
                + "Name: " + (this.GetName()) + "\n"
                + "Street: " + (this.street) + "\n"
                + "City: " + (this.city) + "\n"
                + "State: " + (this.state) + "\n"
                + "Zip: " + (this.zip) + "\n";
      
        return info;
    }
}
