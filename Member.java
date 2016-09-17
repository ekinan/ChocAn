/**
 *
 * @author Enis
 * This is the membership class. A member is an Information object except that
 * it also has a status variable to indicate if they've been suspended (false)
 * or are still a valid member of ChocAn (true)
 */
public class Member extends Information
{ 
    private boolean status = true; //T = Valid member, F = Suspended
    
    /**
     * 
     * @param id_ 
     */
    public Member(final int id_)
    {
        super(id_);
        this.status = true;
    }
    
    /**
     * 
     * @param id_
     * @param name_
     * @param street_
     * @param city_
     * @param state_
     * @param zip_
     * @param status_ 
     */
    public Member(final int id_, final String name_, final String street_, final String city_, final String state_, final String zip_, final boolean status_)
    {
        super(id_,name_,street_,city_,state_,zip_);
        this.status = status_;
    }
     
    /**
     * 
     * @param other 
     */
    public Member(final Member other)
    {
        super(other); //Copy the information parameters
        this.status = other.status;
    }
    
    /**
     * 
     * @return The member status
     */
    public boolean GetStatus()
    {
        return this.status;
    }
    
    /**
     * 
     * @param newStatus 
     */
    public void SetStatus(final boolean newStatus)
    {
        this.status = newStatus;
    }
    
    /**
     * Used for debugging purpose only.
     * 
     * @return 
     */
    public String toString()
    {
        String info = super.toString();
        info += "Status: " + (this.status) + "\n\n";
        
        return info;
    }
}
