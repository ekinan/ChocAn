import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ListIterator;

/**
 *
 * @author Enis
 * This class serves the purpose of a C-struct.
 * It lumps the necessary information needed to generate an individual
 * provider or member report in order to reduce the number
 * of method arguments in ReportGenerator.
 * 
 * Note that all we need to generate any one of these reports is:
 *      1) The information, which is the number, name, and address info of the provider or member.
 *      2) The start point of the service records associated with that individual
 *      3) The reference date to generate the reports with. Originally this was meant so that
 *      one could generate the provider reports based on their provider date (which can at most be 26 hours
 *      more than the data center's date for this project), but I discarded this. However one could modify
 *      the project to do so easily.
 *      4) This variable indicates if this is a manager-requested report or one obtained from the DPS'
 *      weekly processing. It is necessary for naming conventions, outlined in more detail in the report.
 */
public class ReportParameters
{
    public Information info = null;
    public ListIterator<ServiceRecord> iterator = null;
    public LocalDateTime refDate = null;
    public boolean isManagerReport = false;
    
    ReportParameters()
    {
    }
    
    void SetParameters(Information info_, ListIterator<ServiceRecord> iterator_, LocalDateTime refDate_, boolean isManagerReport_)
    {
        this.info = info_;
        this.iterator = iterator_;
        this.refDate = refDate_;
        this.isManagerReport = isManagerReport_;
    }
}
