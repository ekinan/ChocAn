import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Enis
 * This is a ChocAn provider class. A ChocAn provider, in addition to the parameters
 * in the Information class, also has its own provider directory to store its services
 * as well as its own date (since there could be different providers in different
 * time zones).
 * 
 * 
 */
public class Provider extends Information
{
    private TreeSet<Service> provDir = new TreeSet<Service>();
    private LocalDateTime provDate = null;
    
    /**
     * 
     * @param id_ 
     */
    public Provider(final int id_)
    {
        super(id_);
    }
    
    /**
     * 
     * @param id_
     * @param date_ 
     */
    public Provider(final int id_, final LocalDateTime date_)
    {
        super(id_);
        this.provDate = date_;
    }

    /**
     * 
     * @param id_
     * @param name_
     * @param street_
     * @param city_
     * @param state_
     * @param zip_
     * @param provDate_ 
     */
    public Provider(final int id_, final String name_, final String street_, final String city_, final String state_, final String zip_, final LocalDateTime provDate_)
    {
        super(id_,name_,street_,city_,state_,zip_);
        this.provDate = provDate_;
    }
    
    /**
     * 
     * @param original 
     */
    public Provider(final Provider original) //COPY THE DATE STUFF LATER
    {
        super(original); //Copy the parent parameters
        Iterator<Service> current = original.provDir.iterator();
        while (current.hasNext()) //Now copy the provider directory
        {
            this.provDir.add(new Service(current.next()));
        }
    }
    
    /**
     * This method takes in a service code, and looks up the corresponding service
     * inside the provider directory.
     * 
     * @param serviceCode
     * @return The service if it exists, otherwise null
     */
    public Service LookUpService(final int serviceCode)
    {
        return Utilities.LookUpElement(serviceCode, this.provDir);
    }
    
    /**
     * This method inserts a new service into the provider directory
     * 
     * @param newService
     * @return true if the add was successful, false otherwise
     */
    public boolean AddService(final Service newService)
    {
        return this.provDir.add(newService);
    }
    
    /**
     * This method removes a service, if it exists
     * 
     * @param service
     * @return true if the remove was successful, false otherwise 
     */
    public boolean RemoveService(final Service service)
    {
        return this.provDir.remove(service);
    }
    
    /**
     * 
     * @return The current date and time of this provider
     */
    public LocalDateTime GetProviderDateTime()
    {
        return this.provDate;
    }
    
    /**
     * 
     * @param date_ 
     */
    public void SetProviderDateTime(LocalDateTime date_)
    {
        this.provDate = date_;
    }    
    
    /**
     * 
     * @return A reference to the provider directory
     */
    public TreeSet<Service> GetProviderDirectory()
    {
        return this.provDir;
    }
 
    /**
     * Ignore, used for debugging purposes
     * 
     * @param filePath
     * @throws IOException 
     */
    public void WriteServicesTo(final String filePath) throws IOException
    {
        BufferedWriter writer = FileUtilities.OpenWriter(filePath); //First write the info
        Iterator<Service> iterator = this.provDir.iterator();
        if (iterator.hasNext())
        {
            Service currentService = iterator.next();
            writer.write(currentService.toString());
            while (iterator.hasNext())
            {
                currentService = iterator.next();
                writer.write("\n"+currentService.toString());
            }
        }
        writer.close();
    }
    
    /**
     * Extracts this provider's services in alphabetical order and places them in
     * container. Note that container should use ServiceAlphabetical as its Comparator
     * object.
     * 
     * @param container The data structure for which the services will be extracted to
     */
    public void ExtractServicesInAlphabeticalOrder(TreeSet<Service> container)
    {
        Iterator<Service> iterator = this.provDir.iterator();
        while (iterator.hasNext())
        {
            container.add(iterator.next());
        }
    }

    /**
     * Ignore, used for debugging purposes.
     * 
     * @return 
     */
    public String toString()
    {
        String info = super.toString();
        info += "Date: " + (this.provDate.format(DateTimeFormatter.ofPattern(Format.DATE + " " + Format.TIME))) + "\n";
        
        String services = "SERVICES OFFERED: \n";
        Iterator<Service> current = this.provDir.iterator();
        while (current.hasNext())
        {
            services += current.next().toString() + "\n";
        }
        
        info += services+"\n\n";
        
        return info;
    }
}
