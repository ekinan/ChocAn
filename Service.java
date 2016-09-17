
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Enis
 * This is the service class. A service consists of
 * its name, number and fee (up to $999.99).
 *
 */
public class Service extends Identification
{
    private BigDecimal fee = new BigDecimal(0.0);
    
    /**
     * 
     * @param id_ 
     */
    public Service(final int id_)
    {
        super(id_, "");
    }
    
    /**
     * 
     * @param id_
     * @param name_
     * @param fee_ 
     */
    public Service(final int id_, final String name_, final BigDecimal fee_)
    {
        super(id_, name_);
        this.fee = fee_;
    }
    
    /**
     * 
     * @param other 
     */
    public Service(final Service other)
    {
        super(other);
        this.fee = new BigDecimal(other.fee.doubleValue());    
    }
    
    /**
     * 
     * @return The service fee
     */
    public BigDecimal GetFee()
    {
        return this.fee;
    }
    
    /**
     * Sets the fee of the service to newFee iff newFee >= 0.0. Otherwise does
     * nothing.
     * 
     * @param newFee
     * @return true if the new fee was successfully set, false otherwise 
     */
    public boolean SetFee(final BigDecimal newFee)
    {
        if (newFee.doubleValue() < 0.0)
            return false;
        else
        {
            this.fee = newFee;
            return true;
        }
    }
    
    /**
     * Ignore, this method is for debugging.
     * 
     * @return 
     */
    public String toString()
    {
        String info = this.GetID() + "\n"
                + this.GetName() + "\n"
                + (fee.toPlainString());
        
        return info;        
    }
}
