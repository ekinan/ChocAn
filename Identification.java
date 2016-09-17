/**
 *
 * @author Enis
 * This is the Identification class, which serves as the ancestor class in the
 * identification, service, information, member, provider inheritance hierarchy
 * It contains variables to store the ID and name, a feature that all three
 * objects share in common, along with corresponding getters and setters.
 * Note that the id is immutable because once a service, member, or provider
 * has their code set, it cannot be changed as it is the unique feature that
 * differentiates them from one another.
 * 
 * It also overrides the compareTo and equals methods, since members, services
 * and providers are sorted by their corresponding codes.
 */
public class Identification implements Comparable<Identification>
{
    private int id = 0;
    private String name = "";
    
    public Identification()
    {
    }
    
    /**
     * 
     * @param id_ 
     */
    public Identification(final int id_)
    {
        this.id = id_;
    }
    
    /**
     * 
     * @param id_
     * @param name_ 
     */
    public Identification(final int id_, final String name_)
    {
        this.id = id_;
        this.name = name_;
    }
    
    /**
     * 
     * @param oth 
     */
    public Identification(final Identification oth)
    {
        this.id = oth.id;
        this.name = oth.name;
    }
    
    /**
     * 
     * @return ID
     */
    public int GetID()
    {
        return this.id;
    }
    
    /**
     * 
     * @return Name
     */
    public String GetName()
    {
        return this.name;
    }
    
    /**
     * 
     * @param name_ 
     */
    public void SetNameTo(String name_)
    {
        this.name = name_;
    }
    
    /**
     * Comparison is based only on ids. This is used to insert Services, Providers, and Members
     * into their corresponding tree-based data structures.
     * 
     * @param o
     * @return compareTo value
     */
    public int compareTo(Identification o) //Services are sorted by their ID
    {
        if (this.id < o.id)
            return -1;
        else if (this.id == o.id)
            return 0;
        else
            return 1;
    }
    
    public boolean equals(Identification o) //Services are equal by their ID
    {
        return (this.id == o.id);
    }    
}
